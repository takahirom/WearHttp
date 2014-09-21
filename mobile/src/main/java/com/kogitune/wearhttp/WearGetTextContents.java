package com.kogitune.wearhttp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by takam on 2014/09/21.
 */
public class WearGetTextContents extends WearGetContents {

    private WearGetContentsCallBack mCallBack;

    public WearGetTextContents(Context context) {
        super(context);
    }

    public interface WearGetContentsCallBack {
        public void onGetContents(String contents);

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
            mCallBack.onGetContents(new String(mDataMap.getByteArray("reqId:" + mReqId)));
            mCallBack = null;
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    if (mCallBack == null) {
                        return;
                    }
                    mCallBack.onGetContents(new String(mDataMap.getByteArray("reqId:" + mReqId)));
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
