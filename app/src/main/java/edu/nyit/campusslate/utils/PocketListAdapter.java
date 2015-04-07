/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate.utils;

import edu.nyit.campusslate.data.PocketDbHelper;
import edu.nyit.campusslate.normalized.Entry;
import edu.nyit.campusslate.R;

import android.app.Activity;
import android.content.Context;
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
 * <p>Title: ArticleListAdapter.</p>
 * <p>Description:</p>
 *
 * @author jasonscott
 */
public class PocketListAdapter extends BaseAdapter {
    private Activity mActivity;
    private static LayoutInflater sInflater;
    private static PocketDbHelper mPocketDbHelper;
    private ImageView mArticleImage;
    private TextView mTitleView;
    private TextView mDescriptionView;
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
        return (long) entry.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Entry entry = mEntries.get(position);
        if (convertView == null) {
            convertView = sInflater.inflate(R.layout.article_list_row, null);
        }

        mArticleImage = (ImageView) convertView.findViewById(R.id.content_image);
        mTitleView = (TextView) convertView.findViewById(R.id.content_title);
        mDescriptionView = (TextView) convertView.findViewById(R.id.content_description);

        Glide.with(mActivity)
                .load(entry.getImageUrl())
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
        mEntries = mPocketDbHelper.retrieveTable(mTableName);
    }
}
