/**
 * Copyright (C) 2014 Jason Scott
 */
package com.nyit.pocketslate.activities;

import com.nyit.pocketslate.data.PocketDbHelper;
import com.nyit.pocketslate.fragments.ArticleListFragment;
import com.nyit.pocketslate.R;
import com.nyit.pocketslate.data.PocketReaderContract.SlateEntry;
import com.nyit.pocketslate.fragments.LocalArticleListFragment;
import com.nyit.pocketslate.normalized.Entry;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private PocketSectionPagerAdapter mSectionPagerAdapter;

    private static final int PAGE_COUNT = SlateEntry.PAGE_NAMES.length;

    private static int currentPosition = 0;

    private SharedPreferences.Editor mEditor;

    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionPagerAdapter = new PocketSectionPagerAdapter(getSupportFragmentManager());

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

        handleIntent(getIntent());

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

        // Find the SearchView and set Searchable.
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            new QueryDatabaseTask(this).execute(query);
        }
    }

    public static class PocketSectionPagerAdapter
            extends FragmentStatePagerAdapter {

        public PocketSectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment;

            if (position == (PAGE_COUNT - 2)) {
                fragment = new LocalArticleListFragment();
                ((LocalArticleListFragment) fragment).setLocalListType(LocalArticleListFragment.LocalListType.valueOf("SAVED"));
            } else if (position == (PAGE_COUNT - 1)) {
                fragment = new LocalArticleListFragment();
                ((LocalArticleListFragment) fragment).setLocalListType(LocalArticleListFragment.LocalListType.valueOf("SEARCHED"));
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

    /**
     * Asynchronous task for running search query of database off of the application UI thread.
     */
    private class QueryDatabaseTask extends AsyncTask<String, Void, Boolean> {

        private Context context;

        private String query;

        private int queryResultsCount = 0;

        public QueryDatabaseTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            query = params[0];

            Map<String, Entry> entryMap = new HashMap<>();

            PocketDbHelper pocketDbHelper = PocketDbHelper.getInstance(context);

            // Clear existing search results table
            pocketDbHelper.deleteTable("search");

            // Two Loops
            // Outer loop is each table (skip saved)
            for (String table : SlateEntry.TABLE_NAMES) {
                // Last table name in the list is saved...
                if (table.equalsIgnoreCase("saved")
                        || table.equalsIgnoreCase("search")) {
                    continue;
                }

                // Inner loop is each possible column.
                for (String column : SlateEntry.COLUMN_NAMES) {
                    ArrayList<Entry> entries = pocketDbHelper.getEntriesMatching(table, column, query);

                    if (entries == null
                            || entries.isEmpty()) {
                        continue;
                    }

                    // Add each to the map with key ID_TABLE
                    for (Entry entry : entries) {
                        // Collisions will overwrite too handle any duplicate queries.
                        // ID_TABLE is unique.
                        entryMap.put(String.format("%s_%s", entry.getId(), table), entry);
                    }
                }
            }

            // Finally after deciding what we are doing with the results we will insert the entries from the map.
            // If map is empty return false.
            return !entryMap.isEmpty() && (queryResultsCount = pocketDbHelper.insertEntries(new ArrayList(entryMap.values()), "search")) == entryMap.size();

        }

        @Override
        protected void onPostExecute(Boolean result) {

            // Set search tab as current item.
            mSectionViewPager.setCurrentItem(PAGE_COUNT - 1);


            // Store previous search.
            mEditor.putString("lastQuery", query);
            mEditor.putInt("lastQueryResultsCount", queryResultsCount);
            mEditor.commit();

            // Refresh the view.
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof LocalArticleListFragment
                        && ((LocalArticleListFragment) fragment).getSectionTitle().equalsIgnoreCase("Search")) {
                    ((LocalArticleListFragment) fragment).updatedSearch();
                }
            }

        }
    }
}
