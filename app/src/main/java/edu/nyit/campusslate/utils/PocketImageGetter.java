/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate.utils;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.widget.TextView;

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
    private TextView mView;

    public PocketImageGetter(TextView v) {
        mView = v;

    }

    @Override
    public Drawable getDrawable(String source) {
        PocketDrawable drawable = new PocketDrawable();
        ImageGetterAsyncTask imageGetter = new ImageGetterAsyncTask(drawable);

        imageGetter.execute(source);

        return drawable;
    }

    private class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        private PocketDrawable pocketDrawable;

        public ImageGetterAsyncTask(PocketDrawable d) {
            pocketDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            return downloadDrawable(params[0]);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            pocketDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0 + result.getIntrinsicHeight());
            pocketDrawable.drawable = result;
            mView.invalidate();

            mView.setHeight((mView.getHeight() + result.getIntrinsicHeight()));
        }

        private Drawable downloadDrawable(String url) {
            try {
                InputStream is = downloadUrl(url);
                Drawable drawable = Drawable.createFromStream(is, "src");
                drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0 + drawable.getIntrinsicHeight());
                return drawable;
            } catch(IOException e) {
                return null;
            }
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

    public class PocketDrawable extends BitmapDrawable {
        protected Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            if(drawable != null) {
                drawable.draw(canvas);
            }
        }
    }
}
