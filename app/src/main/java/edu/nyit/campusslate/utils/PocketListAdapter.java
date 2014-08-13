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
    private PocketImageGetter mImageGetter;
    private TextView mTitleView;
    private TextView mAuthorView;
    private TextView mDateView;
    private TextView mContentView;
    private ArrayList<Entry> mEntries;
    private String mTableName;

    public PocketListAdapter(Activity activity, String table) {
        mActivity = activity;
        mTableName = table;
        sInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPocketDbHelper = PocketDbHelper.getInstance(mActivity);
        mEntries = mPocketDbHelper.retrieveTable(table);

    }

    @Override
    public int getCount() {
        return mEntries.size();
    }

    @Override
    public Entry getItem(int position) {
        return mEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        Entry entry = mEntries.get(position);
        return Long.valueOf(entry.id);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Entry entry = mEntries.get(position);
        if (convertView == null) {
            convertView = sInflater.inflate(R.layout.article_list_row, null);
        }

        mTitleView = (TextView) convertView.findViewById(R.id.content_title);
        mAuthorView = (TextView) convertView.findViewById(R.id.content_author);
        mDateView = (TextView) convertView.findViewById(R.id.content_date);
        mContentView = (TextView) convertView.findViewById(R.id.content_view);

        mImageGetter = new PocketImageGetter(mContentView);

        mTitleView.setText(entry.title);
        mAuthorView.setText(entry.creator);
        mDateView.setText(new Date(Long.valueOf(entry.publicationDate)).toString());
        mContentView.setText(Html.fromHtml(entry.content, mImageGetter, null));

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mEntries = mPocketDbHelper.retrieveTable(mTableName);
    }

}
