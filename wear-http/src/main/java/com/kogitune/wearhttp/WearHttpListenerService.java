package com.kogitune.wearhttp;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.kogitune.wearablelistenerservicebroadcaster.WearListenerService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by takam on 2014/09/07.
 */
public class WearHttpListenerService extends IntentService {

    private static final String TAG = "WearHttpListenerService";

    public static final String MESSAGE_EVENT_PATH = "/http/get";

    public static final String MESSAGE_EVENT_PATH_KEY = "MESSAGE_EVENT_PATH_KEY";
    public static final String MESSAGE_EVENT_DATA_KEY = "MESSAGE_EVENT_DATA_KEY";
    public static final String MESSAGE_EVENT_REQUEST_ID_KEY = "MESSAGE_EVENT_REQUEST_ID_KEY";
    public static final String MESSAGE_EVENT_SOURCE_NODE_ID_KEY = "MESSAGE_EVENT_SOURCE_NODE_ID_KEY";

    private GoogleApiClient mGoogleApiClient;

    public WearHttpListenerService(){
        super("WearHttpListenerService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (JudgeAndroidWear.isWear(this)) {
            stopSelf();
            //form stackoverflow http://stackoverflow.com/questions/6408086/android-can-i-enable-disable-an-activitys-intent-filter-programmatically
            getPackageManager().setComponentEnabledSetting(new ComponentName(this, WearHttpListenerService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        handleEvent(intent.getStringExtra(MESSAGE_EVENT_PATH_KEY), intent.getByteArrayExtra(MESSAGE_EVENT_DATA_KEY), intent.getIntExtra(MESSAGE_EVENT_REQUEST_ID_KEY, 0), intent.getStringExtra(MESSAGE_EVENT_SOURCE_NODE_ID_KEY));
    }

    private void handleEvent(final String path, final byte[] data, final int requestId, final String sourceNodeId) {
        if (!MESSAGE_EVENT_PATH.equals(path)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendResponse(data, requestId);
            }
        }).start();

    }

    private void sendResponse(byte[] data, int requestId) {

        ConnectionResult connectionResult =
                mGoogleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }
        URL url = null;
        try {
            url = new URL(new String(data));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        byte[] byteArray = new HttpByteArrayUtil(url).getByteArray();


        // Create DataMap instance
        PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/datapath");
        DataMap dataMap = dataMapRequest.getDataMap();

        // set data
        dataMap.putLong("time", new Date().getTime());

        dataMap.putAsset("reqId:" + requestId, Asset.createFromBytes(byteArray));


        // refresh data
        final PutDataRequest request = dataMapRequest.asPutDataRequest();

        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        if(BuildConfig.DEBUG) {
                            Log.d(TAG, "putDataItem status: "
                                    + dataItemResult.getStatus().toString());
                        }
                    }
                });
    }


}
