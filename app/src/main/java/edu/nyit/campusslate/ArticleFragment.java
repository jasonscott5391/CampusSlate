/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate;

import edu.nyit.campusslate.utils.PocketBitmapTask;
import edu.nyit.campusslate.utils.PocketBitmapTask.PocketBitmapDrawable;
import edu.nyit.campusslate.utils.PocketDbHelper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;

/**
 * <p>Title: ArticleFragment.</p>
 *
 * @author jasonscott
 */
public class ArticleFragment extends Fragment {
    private TextView mArticleNumber;
    private ImageView mArticleImage;
    private TextView mArticleDate;
    private TextView mArticleTitle;
    private TextView mArticleAuthor;
    private TextView mArticleContent;
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
        mArticleNumber = (TextView) view.findViewById(R.id.article_number);
        mArticleImage = (ImageView) view.findViewById(R.id.article_image);
        mArticleDate = (TextView) view.findViewById(R.id.article_date);
        mArticleTitle = (TextView) view.findViewById(R.id.article_title);
        mArticleAuthor = (TextView) view.findViewById(R.id.article_author);
        mArticleContent = (TextView) view.findViewById(R.id.article_content);

        Entry entry = PocketDbHelper.getInstance(getActivity())
                .retrieveEntry(mArticleSection.toLowerCase(Locale.US),
                        String.valueOf(mArticleId + 1));

        loadBitmap(mArticleImage, entry.imageUrl, 500, 350);

        String position;
        if (mArticleId == 0) {
            position = entry.id + " of " + mNumEntries + RIGHT_SWIPE;
        } else if (mArticleId == mNumEntries - 1) {
            position = LEFT_SWIPE + entry.id + " of " + mNumEntries;
        } else {
            position = LEFT_SWIPE + entry.id + " of " + mNumEntries + RIGHT_SWIPE;
        }

        mArticleNumber.setText(position);
        mArticleDate.setText(new Date(Long.valueOf(entry.publicationDate)).toString());
        mArticleTitle.setText(entry.title);
        mArticleAuthor.setText(entry.creator);
        mArticleContent.setText(entry.content);

        return view;
    }

    private void loadBitmap(ImageView imageView, String url, int width, int height) {

        final Bitmap bitmap = PocketBitmapTask.getBitmapFromMemCache(url);
        if (bitmap != null) {
            mArticleImage.setImageBitmap(bitmap);
        } else {
            if (PocketBitmapTask.cancelPotentialWork(url, imageView)) {
                final PocketBitmapTask task = new PocketBitmapTask(imageView, url, width, height);
                Resources resources = getResources();
                final PocketBitmapDrawable bitmapDrawable = new PocketBitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.test_article_image), task);
                imageView.setImageDrawable(bitmapDrawable);
                task.execute(url);
            }
        }
    }

    //TODO(jasonscott) OptionsMenu for add to saved stories, open in browser, and find in page

}
