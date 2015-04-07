/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate.fragments;

import edu.nyit.campusslate.R;
import edu.nyit.campusslate.normalized.Entry;
import edu.nyit.campusslate.data.PocketDbHelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.Locale;

/**
 * <p>Title: ArticleFragment.</p>
 * <p>Description:</p>
 *
 * @author jasonscott
 */
public class ArticleFragment extends Fragment {
    private ImageView mArticleImage;
    private String mArticleSection;
    private int mArticleId;
    private int mNumEntries;
    private static final String LEFT_SWIPE = "<--- ";
    private static final String RIGHT_SWIPE = " --->";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mArticleSection = getArguments().getString("section_title");
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

        Entry entry = PocketDbHelper.getInstance(getActivity())
                .retrieveEntry(mArticleSection.toLowerCase(Locale.US),
                        String.valueOf(mArticleId + 1));

        Glide.with(getActivity()).load(entry.getImageUrl()).into(mArticleImage);

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
        mArticleContent.setText(entry.getContent());

        return view;
    }

    //TODO(jasonscott) OptionsMenu for add to saved stories, open in browser, and find in page

}
