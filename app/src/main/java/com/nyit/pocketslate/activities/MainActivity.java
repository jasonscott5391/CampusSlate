/**
 * Copyright (C) 2014 Jason Scott
 */
package com.nyit.pocketslate.activities;

import com.nyit.pocketslate.fragments.ArticleListFragment;
import com.nyit.pocketslate.R;
import com.nyit.pocketslate.data.PocketReaderContract.SlateEntry;
import com.nyit.pocketslate.fragments.SavedArticleListFragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

/**
 * <p>MainActivity.java</p>
 * <p><t>Central activity for the application.  Defines the ActionBar and
 * ViewPager for swiping through the various sections.</t></p>
 *
 * @author jasonscott
 */
public class MainActivity
        extends FragmentActivity
        implements ActionBar.TabListener {

    private ViewPager mSectionViewPager;

    private static final int PAGE_COUNT = 6;

    private static int currentPosition = 0;

    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PocketSectionPagerAdapter mSectionPagerAdapter = new PocketSectionPagerAdapter(getSupportFragmentManager());

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mSectionViewPager = (ViewPager) findViewById(R.id.section_pager);
        mSectionViewPager.setAdapter(mSectionPagerAdapter);
        mSectionViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        assert actionBar != null;
                        actionBar.setSelectedNavigationItem(position);
                    }
                }
        );


        for (int i = 0; i < mSectionPagerAdapter.getCount(); i++) {
            assert actionBar != null;
            actionBar.addTab(actionBar.newTab().setText(
                    mSectionPagerAdapter.getPageTitle(i)).setTabListener(this));
        }

        mEditor = getPreferences(Context.MODE_PRIVATE).edit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences userPrefs = getPreferences(Context.MODE_PRIVATE);
        currentPosition = userPrefs.getInt("current_position", 0);
        mSectionViewPager.setCurrentItem(currentPosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEditor.putInt("current_position", currentPosition);
        mEditor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_search || super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        currentPosition = tab.getPosition();
        mSectionViewPager.setCurrentItem(currentPosition, true);
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    public static class PocketSectionPagerAdapter
            extends FragmentStatePagerAdapter {

        public PocketSectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment;

            if (position == (PAGE_COUNT - 1)) {
                fragment = new SavedArticleListFragment();
            } else {
                fragment = new ArticleListFragment();
            }

            Bundle args = new Bundle();
            args.putString("title", SlateEntry.PAGE_NAMES[position]);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return SlateEntry.PAGE_NAMES[position];
        }

    }
}
