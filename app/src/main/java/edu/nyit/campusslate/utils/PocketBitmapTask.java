/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p>Title: PocketBitmapTask.</p>
 * <p>Description:</p>
 * @author jasonscott
 */
public class PocketBitmapTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> mImageViewWeakRef;
    private int mWidth = 0;
    private int mHeight = 0;
    private String mUrl;
    private static final int mMaxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private static final int mCacheSize = mMaxMemory / 4;
    private static LruCache<String, Bitmap> mMemoryCache =
            new LruCache<String, Bitmap>(mCacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap.getByteCount() / 1024;
        }
    };

    /**
     * @param imageView
     * @param url
     * @param reqWidth
     * @param reqHeight
     */
    public PocketBitmapTask(ImageView imageView, String url, int reqWidth, int reqHeight) {
        mImageViewWeakRef = new WeakReference<ImageView>(imageView);
        mUrl = url;
        mWidth = reqWidth;
        mHeight = reqHeight;
    }

    // Decode image in background
    @Override
    protected Bitmap doInBackground(String... params) {
        final Bitmap bitmap = decodeBitmapFromNetwork(params[0], mWidth, mHeight);
        addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        //When complete, check if ImageView is still around
        if (isCancelled()) {
            bitmap = null;
        }

        if (mImageViewWeakRef != null && bitmap != null) {
            final ImageView imageView = mImageViewWeakRef.get();
            final PocketBitmapTask bitmapTask = getBitmapTask(imageView);

            if (this == bitmapTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * Adds bitmap to memory cache.
     * @param key - String of added.
     * @param bitmap - Bitmap to be added.
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {

    }

    /**
     * Returns Bitmap from memory cache.
     * @param key - String of Bitmap to return.
     * @return Bitmap
     */
    public static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * Returns trues if work has been cancelled or not.
     * @param url - String of image url.
     * @param imageView - ImageView to be set.
     * @return boolean
     */
    public static boolean cancelPotentialWork(String url, ImageView imageView) {
        final PocketBitmapTask bitmapTask = getBitmapTask(imageView);
        if (bitmapTask != null) {
            final String bitmapUrl = bitmapTask.mUrl;
            if (!bitmapUrl.equals(url)) {
                bitmapTask.cancel(true);        // Cancel previously existing task
            } else {
                return false;                    // Same work currently in progress
            }
        }
        return true;                //No task associated with the ImageView,
                                    // or an existing task was cancelled.
    }

    /**
     * Returns task associated with an ImageView.
     * @param imageView - ImageView task is being worked on.
     * @return PocketBitmapTask
     */
    private static PocketBitmapTask getBitmapTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof PocketBitmapDrawable) {
                final PocketBitmapDrawable bitmapDrawable = (PocketBitmapDrawable) drawable;
                return bitmapDrawable.getBitmapTask();
            }
        }
        return null;
    }

    /**
     * Returns calculated dimensions.
     * @param options - Options for Bitmap.
     * @param reqWidth - required with of Bitmap.
     * @param reqHeight - required height of Bitmap.
     * @return int
     */
    public static int calcInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfWidth = width / 2;
            final int halfHeight = height / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            while ((halfWidth / inSampleSize) > reqWidth
                    && (halfHeight / inSampleSize) > reqHeight) {
                // height and width larger than the requested height and width.
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Returns Bitmap decoded and downloaded through Network connection.
     * @param url - String of image url
     * @param reqWidth - Bitmap required width.
     * @param reqHeight - Bitmap required height.
     * @return Bitmap
     */
    public static Bitmap decodeBitmapFromNetwork(String url, int reqWidth, int reqHeight) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;  // to check dimensions
            options.inScaled = false;
            BitmapFactory.decodeStream(downloadUrl(url), null, options);

            // Calculate inSampleSize
            options.inSampleSize = calcInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(downloadUrl(url), null, options);

        } catch (IOException e) {
            // Handle IOException
            return null;
        }
    }

    /**
     * Returns InputStream from given URL.
     * @param urlString
     * @return InputStream
     * @throws IOException
     */
    private static InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(5000 /* milliseconds */);
        conn.setConnectTimeout(5000/*milliseconds*/);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;

    }

    /**
     * <p>Title: PocketBitmapDrawable.</p>
     * <p>Description: Custom BitmapDrawable.</p>
     */
    public static class PocketBitmapDrawable extends BitmapDrawable {
        private final WeakReference<PocketBitmapTask> mBitmapTaskRef;

        /**
         * Constructs the custom BitmapDrawable.
         * @param res - Resources needed for drawable.
         * @param bitmap - Bitmap to be drawn.
         * @param bitmapTask - Task to download Bitmap.
         */
        public PocketBitmapDrawable(Resources res, Bitmap bitmap, PocketBitmapTask bitmapTask) {
            super(res, bitmap);
            mBitmapTaskRef = new WeakReference<PocketBitmapTask>(bitmapTask);
        }

        /**
         * Returns task reference.
         * @return PocketBitmapTask
         */
        public PocketBitmapTask getBitmapTask() {
            return mBitmapTaskRef.get();
        }

    }
}
