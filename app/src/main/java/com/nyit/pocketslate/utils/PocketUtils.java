package com.nyit.pocketslate.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p>PocketUtils.java</p>
 * <p><t>Utility class for commonly used methods.</t></p>
 *
 * @author jasonscott
 */
public class PocketUtils {

    /**
     * Returns InputStream for specified URL.
     *
     * @param url Specified URL.
     * @return InputStream
     * @throws IOException
     */
    public static InputStream downloadUrl(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(5000 /* milliseconds */);
        conn.setConnectTimeout(5000/*milliseconds*/);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();

    }
}
