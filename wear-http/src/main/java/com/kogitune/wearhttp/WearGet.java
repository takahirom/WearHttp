package com.kogitune.wearhttp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;

/**
 * Created by takam on 2014/09/07.
 */
abstract class WearGet implements GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener {

    int mReqId;
    private PendingResult<MessageApi.SendMessageResult> mPendingResult;
    private CountDownLatch mInTimeCountDownLatch;
    DataMap mDataMap;


    private final GoogleApiClient mGoogleApiClient;
    public static final String TAG = WearGet.class.getPackage().getName();

    public WearGet(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }

    public void get(final String url, final int timeOutSeconds) {
        mInTimeCountDownLatch = new CountDownLatch(1);

        mGoogleApiClient.connect();

        Wearable.DataApi.addListener(mGoogleApiClient, this);
        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (waitForConnect(timeOutSeconds)) {
                    callFailOnUIThread(new RuntimeException("GoogleApiClient was not able to connect in time."));
                    return;
                }

                final Collection<String> nodes = getNodes();
                if (nodes.size() == 0) {
                    callFailOnUIThread(new RuntimeException("There is no node that is connected."));
                    return;
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sendMessage(nodes, timeOutSeconds, url);
                    }
                });
            }
        }).start();
    }


    private void sendMessage(Collection<String> nodes, final int timeOutSeconds, String url) {


        mReqId = 0;


        sendUrlToEachNode(url, nodes);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mInTimeCountDownLatch.await(timeOutSeconds, TimeUnit.SECONDS) == false) {
                        callFailOnUIThread(new RuntimeException("There was no response within the time."));
                    } else {
                        callSuccessOnUIThread();
                    }
                    Wearable.DataApi.removeListener(mGoogleApiClient, WearGet.this);
                    mGoogleApiClient.disconnect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }).start();
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        DataEvent event = dataEvents.get(0);
        mDataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
        if (mDataMap.containsKey("reqId:" + mReqId)) {
            mInTimeCountDownLatch.countDown();
        }
    }

    private void sendUrlToEachNode(String url, Collection<String> nodes) {
        mPendingResult = null;

        String node = nodes.iterator().next();
        mPendingResult = Wearable.MessageApi
                .sendMessage(mGoogleApiClient, node, WearHttpListenerService.MESSAGE_EVENT_PATH, url.getBytes());
        mPendingResult.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                mReqId = sendMessageResult.getRequestId();
                if (mDataMap == null) {
                    return;
                }

                if (mDataMap.containsKey("reqId:" + mReqId)) {
                    mInTimeCountDownLatch.countDown();
                }
            }
        });

    }

    /**
     * @param timeOutSeconds
     * @return true is timeout
     */
    private boolean waitForConnect(int timeOutSeconds) {
        if (!mGoogleApiClient.isConnected()) {
            ConnectionResult connectionResult = mGoogleApiClient
                    .blockingConnect(timeOutSeconds, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        callFailOnUIThread(new RuntimeException("GoogleApiClient connection failed"));
    }

    private HashSet<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    void callSuccessOnUIThread() {
        final Asset asset = mDataMap.getAsset("reqId:" + mReqId);
        PendingResult<DataApi.GetFdForAssetResult> pendingResult = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset);
        DataApi.GetFdForAssetResult assetResult = pendingResult.await();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        try {
            while ((nRead = assetResult.getInputStream().read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            callFailOnUIThread(e);
            return;
        }

        final byte[] byteArray = buffer.toByteArray();

        if (Looper.getMainLooper().equals(Looper.myLooper())) {
            try {
                callSuccess(CompressionUtils.decompress(byteArray));
            } catch (IOException e) {
                callFailOnUIThread(e);
            } catch (DataFormatException e) {
                callFailOnUIThread(e);
            }
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    try {
                        callSuccess(CompressionUtils.decompress(byteArray));
                    } catch (IOException e) {
                        callFailOnUIThread(e);
                    } catch (DataFormatException e) {
                        callFailOnUIThread(e);
                    }
                }
            });
        }
    }

    void callFailOnUIThread(final Exception e) {
        if (Looper.getMainLooper().equals(Looper.myLooper())) {
            callFail(e);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    callFail(e);
                }
            });
        }
    }

    abstract void callSuccess(byte[] byteArray);

    abstract void callFail(final Exception e);
}
