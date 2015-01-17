package com.kogitune.wearhttp;

import android.content.Context;

/**
 * Created by takam on 2014/09/21.
 */
public class WearGetText extends WearGet {

    private WearGetCallBack mCallBack;

    public WearGetText(Context context) {
        super(context);
    }

    public interface WearGetCallBack {
        public void onGet(String contents);

        public void onFail(Exception e);
    }

    /**
     * Get text contents.
     *
     * @param url      contents url
     * @param callBack On get text called get.
     */
    public void get(final String url, final WearGetCallBack callBack) {
        get(url, callBack, 60);
    }

    public void get(final String url, final WearGetCallBack callBack, final int timeOutSeconds) {
        mCallBack = callBack;
        super.get(url, timeOutSeconds);
    }

    @Override
    void callSuccess(byte[] byteArray) {
        if (mCallBack == null) {
            return;
        }

        mCallBack.onGet(new String(byteArray));
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
