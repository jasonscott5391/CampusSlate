/**
 * Copyright (C) 2014 Jason Scott
 */
package com.nyit.pocketslate.fragments;

import com.nyit.pocketslate.R;
import com.nyit.pocketslate.normalized.Entry;
import com.nyit.pocketslate.data.PocketDbHelper;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.Locale;

/**
 * <p>ArticleFragment.java</p>
 * <p><t>Fragment for displaying an Article.</t></p>
 *
 * @author jasonscott
 */
public class ArticleFragment extends Fragment {
    private Entry entry;
    private ImageView mArticleImage;
    private String mArticleSection;
    private int mArticleId;
    private int mNumEntries;
    private static final String LEFT_SWIPE = "<--- ";
    private static final String RIGHT_SWIPE = " --->";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mArticleSection = getArguments().getString("article_section");
        mArticleId = getArguments().getInt("article_id");
        mNumEntries = PocketDbHelper.getInstance(getActivity())
                .getNumEntries(mArticleSection.toLowerCase(Locale.US));
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        view = inflater.inflate(R.layout.fragment_article, container, false);
        TextView mArticleNumber = (TextView) view.findViewById(R.id.article_number);
        mArticleImage = (ImageView) view.findViewById(R.id.article_image);
        TextView mArticleDate = (TextView) view.findViewById(R.id.article_date);
        TextView mArticleTitle = (TextView) view.findViewById(R.id.article_title);
        TextView mArticleAuthor = (TextView) view.findViewById(R.id.article_author);
        TextView mArticleContent = (TextView) view.findViewById(R.id.article_content);

        entry = PocketDbHelper.getInstance(getActivity())
                .retrieveEntry(mArticleSection.toLowerCase(Locale.US),
                        String.valueOf(mArticleId + 1));

        Uri uri;
        if (entry.getImageUrl().equals("")) {
            uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.drawable.ic_launcher);
        } else {
            uri = Uri.parse(entry.getImageUrl());
        }

        Glide.with(getActivity()).load(uri).into(mArticleImage);

        String position;
        if (mArticleId == 0) {
            position = entry.getId() + " of " + mNumEntries + RIGHT_SWIPE;
        } else if (mArticleId == mNumEntries - 1) {
            position = LEFT_SWIPE + entry.getId() + " of " + mNumEntries;
        } else {
            position = LEFT_SWIPE + entry.getId() + " of " + mNumEntries + RIGHT_SWIPE;
        }

        mArticleNumber.setText(position);
        mArticleDate.setText(new Date(entry.getPublicationDate()).toString());
        mArticleTitle.setText(entry.getTitle());
        mArticleAuthor.setText(entry.getCreator());
        Spanned spanned = Html.fromHtml(entry.getContent());
        mArticleContent.setText(spanned);

        return view;
    }
}
