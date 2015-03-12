package com.kogitune.wearhttp;

import com.kogitune.wearablelistenerservicebroadcaster.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by takam on 2014/09/15.
 */
class HttpByteArrayUtil {
    private URL url;

    public HttpByteArrayUtil(URL url) {
        this.url = url;
    }

    public byte[] getByteArray() {
        byte[] data = null;
        // Create request for remote resource.
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            connection = (HttpURLConnection) url.openConnection();

            is = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            data = new byte[4];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            data = buffer.toByteArray();
            data = CompressionUtils.compress(data);
            buffer.flush();
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return data;
    }


}
