/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p>Title: PocketImageGetter.</p>
 * <p>Description:</p>
 *
 * @author jasonscott
 */
public class PocketImageGetter extends AsyncTask<String, Void, Bitmap> implements ImageGetter {
    private Context mContext;
    private View mView;
    private Drawable mDawable;

    private PocketImageGetter(Context c, View v) {
        mContext = c;
        mView = v;
    }

    @Override
    public Drawable getDrawable(String source) {
        Drawable drawable = null;
        //TODO(jasonscott) set drawable to placeholder for loading.
        try {
            Drawable.createFromStream(downloadUrl(source), "src");
        } catch (IOException e) {
            // TODO(jasonscott) Auto-generated catch block
            e.printStackTrace();
        }
        return drawable;
    }

    @Override
    protected void onPreExecute() {

    }


    @Override
    protected Bitmap doInBackground(String... params) {
        // TODO(jasonscott) Auto-generated method stub
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bm) {

    }

    private InputStream downloadUrl(String stringUrl) throws IOException {
        URL url = new URL(stringUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(5000);		/* milliseconds*/
        conn.setConnectTimeout(5000);   /* milliseconds*/
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();

    }
}
