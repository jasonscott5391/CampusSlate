/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate.utils;

import edu.nyit.campusslate.Entry;
import edu.nyit.campusslate.R;
import edu.nyit.campusslate.utils.PocketBitmapTask.PocketBitmapDrawable;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * <p>Title: ArticleListAdapter.</p>
 * <p>Description:</p>
 *
 * @author jasonscott
 */
public class PocketListAdapter extends BaseAdapter {
    private Activity mActivity;
    private static LayoutInflater sInflater;
    private static PocketDbHelper mPocketDbHelper;
    private String mTableName;
    private ImageView mImageView;
    private TextView mTitleView;
    private TextView mDateView;

    public PocketListAdapter(Activity activity, String table) {
        mActivity = activity;
        mTableName = table;
        sInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPocketDbHelper = PocketDbHelper.getInstance(mActivity);
    }

    @Override
    public int getCount() {
        return mPocketDbHelper.getNumEntries(mTableName);
    }

    @Override
    public Entry getItem(int position) {
        //return mPocketDbHelper.retrieveEntry(mTableName, String.valueOf(position + 1));
        return mPocketDbHelper.retrieveTable(mTableName).get(position);
    }

    @Override
    public long getItemId(int position) {
//        return Long.valueOf(mPocketDbHelper.retrieveEntry(mTableName, String.valueOf(position + 1)).id);
        return Long.valueOf(mPocketDbHelper.retrieveTable(mTableName).get(position).id);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Entry entry = mPocketDbHelper.retrieveTable(mTableName).get(position);
        if (convertView == null) {
            convertView = sInflater.inflate(R.layout.article_list_row, null);
        }

        mImageView = (ImageView) convertView.findViewById(R.id.list_article_image);
        mTitleView = (TextView) convertView.findViewById(R.id.list_article_title);
        mDateView = (TextView) convertView.findViewById(R.id.list_article_date);

        mTitleView.setText(entry.title);
        mDateView.setText(new Date(Long.valueOf(entry.publicationDate)).toString());
        Html.fromHtml(entry.content, new Html.ImageGetter() {

            @Override
            public Drawable getDrawable(String source) {
                final Bitmap bitmap = PocketBitmapTask.getBitmapFromMemCache(source);
                if (bitmap != null) {
                    mImageView.setImageBitmap(bitmap);
                } else {
                    if (PocketBitmapTask.cancelPotentialWork(source, mImageView)) {
                        final PocketBitmapTask task =
                                new PocketBitmapTask(mImageView, source, 250, 250);
                        final PocketBitmapDrawable bitmapDrawable = new PocketBitmapDrawable(
                                mActivity.getResources(),
                                BitmapFactory.decodeResource(mActivity.getResources(),
                                        R.drawable.ic_launcher),
                                task
                        );
                        mImageView.setImageDrawable(bitmapDrawable);
                        task.execute(source);
                    }
                }
                return null;
            }
        }, null);

        return convertView;
    }

}
