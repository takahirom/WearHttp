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


    @Override
    void callSuccess(byte[] byteArray) {
        if (mCallBack == null) {
            return;
        }
        mCallBack.onGetContents(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
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
