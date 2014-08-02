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
public class PocketImageGetter implements ImageGetter {
    private Context mContext;
    private View mView;
    private Drawable mDrawable;
    private int mResId;

    public PocketImageGetter(Context c, View v, int r) {
        mContext = c;
        mView = v;
        mDrawable = null;
        mResId = r;
    }

    @Override
    public Drawable getDrawable(String source) {
        ImageGetterAsyncTask imageGetter = new ImageGetterAsyncTask();

        imageGetter.execute(source);

        return mDrawable;
    }

    private class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        private Drawable drawable;

        public ImageGetterAsyncTask() {

        }

        @Override
        protected void onPreExecute() {
            // TODO (jasonscott) Something before background work.
            mDrawable = mContext.getResources().getDrawable(mResId);
        }


        @Override
        protected Drawable doInBackground(String... params) {
            try {
                drawable = Drawable.createFromStream(downloadUrl(params[0]), "src");
            } catch(IOException e) {
                //TODO (jasonscott) Handle IOExceptions.
                drawable = null;
            }

            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            mDrawable = result;
            mView.invalidate();
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
}
