package com.kogitune.wearhttp;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by takam on 2014/09/07.
 */
public class WearHttpListenerService extends WearableListenerService {

    private static final String TAG = "WearHttpListenerService";
    private GoogleApiClient mGoogleApiClient;

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
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        ConnectionResult connectionResult =
                mGoogleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }
        URL url = null;
        try {
            url = new URL(new String(messageEvent.getData()));
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
        dataMap.putByteArray("reqId:" + messageEvent.getRequestId(), byteArray);

        // refresh data
        final PutDataRequest request = dataMapRequest.asPutDataRequest();

        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        Log.d(TAG, "putDataItem status: "
                                + dataItemResult.getStatus().toString());
                    }
                });
    }


}
