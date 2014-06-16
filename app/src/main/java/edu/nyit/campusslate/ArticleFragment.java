/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate;

import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import edu.nyit.campusslate.utils.PocketBitmapTask;
import edu.nyit.campusslate.utils.PocketBitmapTask.PocketBitmapDrawable;
import edu.nyit.campusslate.utils.PocketDbHelper;
/**
 * <p>Title: ArticleFragment.java</p>
 * @author jasonscott
 *
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
		mNumEntries = PocketDbHelper.getInstance(getActivity()).getNumEntries(mArticleSection.toLowerCase(Locale.US));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;

		view = inflater.inflate(R.layout.fragment_article, container, false);
		mArticleNumber = (TextView)view.findViewById(R.id.article_number);
		mArticleImage = (ImageView)view.findViewById(R.id.article_image);
		mArticleDate = (TextView)view.findViewById(R.id.article_date);
		mArticleTitle = (TextView)view.findViewById(R.id.article_title);
		mArticleAuthor = (TextView)view.findViewById(R.id.article_author);
		mArticleContent = (TextView)view.findViewById(R.id.article_content);

		Entry entry = PocketDbHelper.getInstance(getActivity()).retrieveEntry(mArticleSection.toLowerCase(Locale.US), String.valueOf(mArticleId+1));

		String position = new String();
		if(mArticleId == 0) {
			position = entry.id + " of " + mNumEntries + RIGHT_SWIPE;
		} else if(mArticleId == mNumEntries - 1) {
			position = LEFT_SWIPE + entry.id + " of " + mNumEntries;
		} else {
			position = LEFT_SWIPE + entry.id + " of " + mNumEntries + RIGHT_SWIPE;
		}

		mArticleNumber.setText(position);
		mArticleDate.setText(entry.pubDate);
		mArticleTitle.setText(entry.title);
		mArticleAuthor.setText(entry.creator);
		mArticleContent.setText(Html.fromHtml(entry.content, new Html.ImageGetter() {
			
			@Override
			public Drawable getDrawable(String source) {
				final Bitmap bitmap = PocketBitmapTask.getBitmapFromMemCache(source);
				if(bitmap != null) {
					mArticleImage.setImageBitmap(bitmap);
				} else {
					if(PocketBitmapTask.cancelPotentialWork(source, mArticleImage)) {
						final PocketBitmapTask task = new PocketBitmapTask(mArticleImage, source, 250, 250);
						final PocketBitmapDrawable bitmapDrawable =
								new PocketBitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), task);
						mArticleImage.setImageDrawable(bitmapDrawable);
						task.execute(source);
					}
				}
				return null;
			}
		}, null));

		return view;
	}
	
	//TODO OptionsMenu for add to saved stories, open in browser, and find in page

}
