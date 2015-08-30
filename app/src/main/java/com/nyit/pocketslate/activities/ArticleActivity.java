/**
 * Copyright (C) 2014 Jason Scott
 */
package com.nyit.pocketslate.activities;

import com.nyit.pocketslate.fragments.ArticleFragment;
import com.nyit.pocketslate.R;
import com.nyit.pocketslate.data.PocketDbHelper;
import com.nyit.pocketslate.normalized.Entry;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * <p>ArticleActivity.java</p>
 * <p><t>Activity for containing a pager adapter of article fragments.</t></p>
 *
 * @author jasonscott
 */
public class ArticleActivity extends FragmentActivity {

    private Entry mCurrentEntry;
    private boolean mIsSaved = false;
    private String mSectionTitle;
    private PocketArticlePagerAdapter mArticlePagerAdapter;
    private ViewPager mArticleViewPager;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Bundle articleBundle = getIntent().getBundleExtra("article");
        mSectionTitle = articleBundle.getString("article_section");
        int articleId = articleBundle.getInt("article_id");

        mArticlePagerAdapter = new PocketArticlePagerAdapter(getSupportFragmentManager(),
                this, mSectionTitle.toLowerCase(Locale.US));

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mArticleViewPager = (ViewPager) findViewById(R.id.article_pager);
        mArticleViewPager.setAdapter(mArticlePagerAdapter);
        mArticleViewPager.setCurrentItem(articleId - 1);

        PocketArticlePageChangeListener mArticlePageChangeListener = new PocketArticlePageChangeListener();
        mArticleViewPager.setOnPageChangeListener(mArticlePageChangeListener);
        setCurrentEntry(articleId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.article, menu);
        setSavedMenuItem();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save:

                if (mCurrentEntry != null) {
                    if (!mIsSaved) {
                        saveEntry();
                    } else {
                        removeEntry();
                    }

                    setSavedMenuItem();
                }

                break;

            case R.id.action_find_in_page:

                break;

            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves an Entry to the saved stories table.
     */
    private void saveEntry() {
        String message;
        PocketDbHelper pocketDbHelper = PocketDbHelper.getInstance(this);

        mCurrentEntry.setId(pocketDbHelper.getNumEntries("saved") + 1);
        if (PocketDbHelper.getInstance(this).insertEntry(mCurrentEntry, "saved")) {
            mIsSaved = true;
            message = "Entry has been bookmarked for later!";
        } else {
            message = "FAILED to bookmark entry!";
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Removes an Entry from the saved stories table.
     */
    private void removeEntry() {
        PocketDbHelper pocketDbHelper = PocketDbHelper.getInstance(this);
        String message = "";
        List<Entry> savedEntries = pocketDbHelper.retrieveEntries("saved");

        try {

            // Find the index of the current entry.
            int index = -1;
            for (Entry entry : savedEntries) {
                if (entry.getPublicationDate() == mCurrentEntry.getPublicationDate()) {
                    index = savedEntries.indexOf(entry);
                }
            }

            // Remove from list and delete the table.
            savedEntries.remove(index);
            pocketDbHelper.deleteTable("saved");

            // Update the ID for each remaining saved entry and re-insert into saved table.
            int ids = 1;
            for (Entry entry : savedEntries) {
                // Reset the ID of the entry.
                entry.setId(ids++);
                pocketDbHelper.insertEntry(entry, "saved");
            }

            mIsSaved = false;

            message = "Entry has been removed from bookmarks!";

            // If this is saved stories table, update the ViewPager.
            if (mSectionTitle.equalsIgnoreCase("saved")) {
                mArticleViewPager.setAdapter(null);
                mArticlePagerAdapter.notifyDataSetChanged();
                mArticleViewPager.setAdapter(mArticlePagerAdapter);
                setCurrentEntry(index);
                mArticleViewPager.setCurrentItem(index - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "FAILED to remove entry from bookmarks!";
            Log.e(String.format("%s.removeEntry", getClass().getSimpleName()), String.format("%s:%s:%s", e.getMessage(), message, mCurrentEntry));
        } finally {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            Log.i(String.format("%s.removeEntry", getClass().getSimpleName()), message);
        }
    }

    /**
     * Sets the current entry based on the specified position.
     *
     * @param articleId Specified position.
     */
    public void setCurrentEntry(int articleId) {
        mCurrentEntry = PocketDbHelper.getInstance(this).retrieveEntry(mSectionTitle.toLowerCase(), String.valueOf(articleId));
        if (mCurrentEntry != null) {
            mIsSaved = PocketDbHelper.getInstance(this).isBookmarked(String.valueOf(mCurrentEntry.getPublicationDate()));
        }
    }

    /**
     * Assigns the correct icon and text for the
     * save option.
     */
    private void setSavedMenuItem() {
        MenuItem menuItem = mMenu.findItem(R.id.action_save);
        int resId = mIsSaved ? R.drawable.ic_action_saved : R.drawable.ic_action_not_saved;
        String saveText = mIsSaved ? "Remove" : "Save";
        menuItem.setIcon(resId).setTitle(saveText);
    }


    /**
     * <p>Title: PocketArticlePagerAdapter.</p>
     * <p>Description:</p>
     */
    public class PocketArticlePagerAdapter extends FragmentStatePagerAdapter {

        private FragmentActivity mActivity;
        private String mTable;
        private ArticleFragment mArticleFragment;

        public PocketArticlePagerAdapter(FragmentManager fm, FragmentActivity a, String t) {
            super(fm);
            mActivity = a;
            mTable = t;
        }

        @Override
        public Fragment getItem(int position) {
            mArticleFragment = new ArticleFragment();

            Bundle args = new Bundle();
            args.putString("article_section", mTable);
            args.putInt("article_id", position);

            mArticleFragment.setArguments(args);

            return mArticleFragment;
        }

        @Override
        public int getCount() {
            return PocketDbHelper.getInstance(mActivity).getNumEntries(mTable);
        }
    }

    public class PocketArticlePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //DO NOTHING
        }

        @Override
        public void onPageSelected(int position) {
            //Update current entry
            setCurrentEntry(position + 1);
            if (mCurrentEntry != null) {

                if (mMenu != null) {
                    setSavedMenuItem();
                }
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // DO NOTHING.
        }
    }

}
