/**
 * Copyright (C) 2014 Jason Scott
 */
package com.nyit.pocketslate.utils;

import com.nyit.pocketslate.data.PocketDbHelper;
import com.nyit.pocketslate.normalized.Entry;
import com.nyit.pocketslate.R;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * <p>ArticleListAdapter.java</p>
 * <p><t>Adapter for ListView of articles.</t></p>
 *
 * @author jasonscott
 */
public class PocketListAdapter extends BaseAdapter {
    private Activity mActivity;
    private static LayoutInflater sInflater;
    private static PocketDbHelper mPocketDbHelper;
    private ArrayList<Entry> mEntries;
    private String mTableName;

    public PocketListAdapter(Activity activity, String table) {
        mActivity = activity;
        mTableName = table;
        sInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPocketDbHelper = PocketDbHelper.getInstance(mActivity);
        mEntries = mPocketDbHelper.retrieveEntries(table);

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
        return (long) entry.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Entry entry = mEntries.get(position);

        if (convertView == null) {
            convertView = sInflater.inflate(R.layout.article_list_row, null);
        }

        ImageView mArticleImage = (ImageView) convertView.findViewById(R.id.content_image);
        TextView mTitleView = (TextView) convertView.findViewById(R.id.content_title);
        TextView mDescriptionView = (TextView) convertView.findViewById(R.id.content_description);


        Uri uri;
        if (entry.getImageUrl().equals("")) {
            uri = Uri.parse("android.resource://"
                    + mActivity.getPackageName()
                    + "/" + R.drawable.ic_launcher);
        } else {
            uri = Uri.parse(entry.getImageUrl());
        }

        Glide.with(mActivity)
                .load(uri)
                .placeholder(R.drawable.ic_action_refresh)
                .crossFade()
                .into(mArticleImage);

        mTitleView.setText(entry.getTitle());
        mDescriptionView.setText(Html.fromHtml(entry.getDescription()));

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mEntries = mPocketDbHelper.retrieveEntries(mTableName);
    }
}
