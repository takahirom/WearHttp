package com.kogitune.wearhttp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by takam on 2014/09/21.
 */
public class WearGetImage extends WearGet {

    private WearGetCallBack mCallBack;

    public WearGetImage(Context context) {
        super(context);
    }


    public interface WearGetCallBack {
        public void onGet(Bitmap contents);

        public void onFail(Exception e);
    }

    /**
     * Get image contents.
     *
     * @param url      contents url
     * @param callBack On get image called get.
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
        mCallBack.onGet(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
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
