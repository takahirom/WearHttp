package com.kogitune.wearhttp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by takam on 2014/09/21.
 */
public class WearGetImageContents extends WearGetContents {

    private WearGetContentsCallBack mCallBack;

    public WearGetImageContents(Context context) {
        super(context);
    }


    public interface WearGetContentsCallBack {
        public void onGetContents(Bitmap contents);

        public void onFail(Exception e);
    }


    public void getContents(final String url, final WearGetContentsCallBack callBack, final int timeOutSeconds) {
        mCallBack = callBack;
        super.getContents(url, timeOutSeconds);
    }

    void callSuccessOnUIThread() {
        if (mCallBack == null) {
            return;
        }
        if (Looper.getMainLooper().equals(Looper.myLooper())) {
            byte[] byteArray = mDataMap.getByteArray("reqId:" + mReqId);
            mCallBack.onGetContents(BitmapFactory.decodeByteArray(byteArray,0,byteArray.length));
            mCallBack = null;
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    if (mCallBack == null) {
                        return;
                    }
                    byte[] byteArray = mDataMap.getByteArray("reqId:" + mReqId);
                    mCallBack.onGetContents(BitmapFactory.decodeByteArray(byteArray,0,byteArray.length));
                    mCallBack = null;
                }
            });
        }
    }

    void callFailOnUIThread(final Exception e) {
        if (mCallBack == null) {
            return;
        }
        if (Looper.getMainLooper().equals(Looper.myLooper())) {
            mCallBack.onFail(e);
            mCallBack = null;
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    if (mCallBack == null) {
                        return;
                    }
                    mCallBack.onFail(e);
                    mCallBack = null;
                }
            });
        }
    }

}
