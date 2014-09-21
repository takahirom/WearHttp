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

    @Override
    void callSuccess(byte[] byteArray) {
        if (mCallBack == null) {
            return;
        }

        mCallBack.onGetContents(new String(byteArray));
        mCallBack = null;
    }

    @Override
    void callFail(Exception e) {
        if (mCallBack == null) {
            return;
        }
        mCallBack.onFail(e);
        mCallBack = null;
    }

}
