/**
 * Copyright (C) 2014 Jason Scott
 */
package com.nyit.campusslate.activities;

import com.nyit.campusslate.fragments.ArticleFragment;
import com.nyit.campusslate.R;
import com.nyit.campusslate.data.PocketDbHelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;
/**
 * <p>Title: ArticleActivity.</p>
 * <p>Description:</p>
 * @author jasonscott
 */
public class ArticleActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);

		String mSectionTitle = getIntent().getBundleExtra("article").getString("section_title");
		String mArticleId = getIntent().getBundleExtra("article").getString("article_id");
		PocketArticlePagerAdapter mArticlePagerAdapter = new PocketArticlePagerAdapter(getSupportFragmentManager(),
				this, mSectionTitle.toLowerCase(Locale.US));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);

		ViewPager mArticleViewPager = (ViewPager) findViewById(R.id.article_pager);
		mArticleViewPager.setAdapter(mArticlePagerAdapter);

		mArticleViewPager.setCurrentItem(Integer.valueOf(mArticleId));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.article, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_save:
			break;
		case R.id.action_find_in_page:
			break;
		default:
		}
		return super.onOptionsItemSelected(item);
	}

    /**
     * <p>Title: PocketArticlePagerAdapter.</p>
     * <p>Description:</p>
     */
	public static class PocketArticlePagerAdapter extends FragmentStatePagerAdapter {

		private FragmentActivity mActivity;
		private String mTable;
		public PocketArticlePagerAdapter(FragmentManager fm, FragmentActivity a, String t) {
			super(fm);
			mActivity = a;
			mTable = t;
		}

		@Override
		public Fragment getItem(int position) {
			ArticleFragment fragment = new ArticleFragment();
			Bundle args = new Bundle();
			args.putString("section_title", mTable);
			args.putInt("article_id", position);
			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public int getCount() {
			return PocketDbHelper.getInstance(mActivity).getNumEntries(mTable);
		}

	}

}
