package com.kogitune.wearhttpsample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kogitune.wearhttp.WearGetImage;
import com.kogitune.wearhttp.WearGetText;


public class MainActivity extends Activity {

    private TextView mTextView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                onInflate(stub);
            }
        });
    }

    private void onInflate(WatchViewStub stub) {
        mTextView = (TextView) stub.findViewById(R.id.text);
        mImageView = (ImageView) stub.findViewById(R.id.image);

        stub.findViewById(R.id.button_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText("start");
                new WearGetText(MainActivity.this).get("http://headers.jsontest.com/", new WearGetText.WearGetCallBack() {
                    @Override
                    public void onGet(String contents) {
                        mTextView.setText(contents);
                    }

                    @Override
                    public void onFail(final Exception e) {
                        mTextView.setText(e.getMessage());
                    }
                });
                new WearGetImage(MainActivity.this).get("https://cloud.githubusercontent.com/assets/1386930/4347967/65f420c4-4176-11e4-8cb6-d70f1867f8cb.png",
                        new WearGetImage.WearGetCallBack() {
                            @Override
                            public void onGet(Bitmap image) {
                                mImageView.setImageBitmap(image);
                            }

                            @Override
                            public void onFail(final Exception e) {
                                mTextView.setText(e.getMessage());
                            }
                        }
                );
            }
        });

    }
}
