/**
 * Copyright (C) 2014 Jason Scott
 */
package com.nyit.pocketslate.fragments;

import com.nyit.pocketslate.R;
import com.nyit.pocketslate.activities.ArticleActivity;
import com.nyit.pocketslate.exceptions.PocketSlateException;
import com.nyit.pocketslate.utils.PocketUtils;
import com.nyit.pocketslate.utils.PocketListAdapter;
import com.nyit.pocketslate.data.PocketReaderContract.SlateEntry;
import com.nyit.pocketslate.utils.PocketXmlParser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Locale;


/**
 * <p>ArticleListFragment.java</p>
 * <p><t>Fragment containing a list of articles.  The group of
 * articles are refreshed by using swipe refresh.</t></p>
 *
 * @author jasonscott
 */
public class ArticleListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final long FIFTEEN_MINUTES = 1000 * 60 * 15;
    private static final String UPDATE = "UPDATING...";

    private ListView mArticleList;
    private SwipeRefreshLayout mSwipeRefresh;
    private PocketListAdapter mListAdapter;
    private View mArticleHeader;
    private TextView mArticleHeaderText;
    private String mSectionTitle;
    private AggregatorTask mAsyncTask = new AggregatorTask();
    private Long mLastRefresh;
    private SharedPreferences.Editor mEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSectionTitle = getArguments().getString("title");

        mArticleHeader = getActivity().getLayoutInflater()
                .inflate(R.layout.article_list_header, null, true);

        mArticleHeaderText = (TextView) mArticleHeader.findViewById(R.id.article_count);
        mEditor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        view = inflater.inflate(R.layout.fragment_article_list, container, false);

        mArticleList = (ListView) view.findViewById(R.id.article_list_view);
        mArticleList.addHeaderView(mArticleHeader);
        mListAdapter = new PocketListAdapter(getActivity(),
                mSectionTitle.toLowerCase(Locale.US));
        mArticleList.setAdapter(mListAdapter);
        mArticleList.setOnItemClickListener(new ArticleListClickListener());

        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(R.color.nyit_yellow,
                R.color.nyit_blue,
                R.color.nyit_blue,
                R.color.nyit_yellow);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences userPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        mLastRefresh = userPrefs.getLong("last_refresh_" + mSectionTitle, 0L);

        if ((System.currentTimeMillis() - mLastRefresh) > FIFTEEN_MINUTES) {
            onRefresh();
        } else {
            mArticleHeaderText.setText("Last Update on " + new Date(mLastRefresh).toString());

            int position = userPrefs.getInt(mSectionTitle + "_last_index", 0);
            int offset = userPrefs.getInt(mSectionTitle + "_last_index_offset", 0);

            if (position != 0
                    && offset != 0) {
                mArticleList.setSelectionFromTop(position, offset);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        int position = mArticleList.getFirstVisiblePosition();
        View v = mArticleList.getChildAt(0);
        int offset = (v == null) ? 0 : (v.getTop() - mArticleList.getPaddingTop());
        mEditor.putInt(mSectionTitle + "_last_index", position);
        mEditor.putInt(mSectionTitle + "_last_index_offset", offset);
        mEditor.commit();
    }

    @Override
    public void onRefresh() {
        if (mAsyncTask != null) {
            mSwipeRefresh.setRefreshing(true);
            mArticleHeaderText.setText(UPDATE);
            mAsyncTask.execute(mSectionTitle.toLowerCase(Locale.US));
        } else {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    /**
     * Updates and modifies fields after refresh finishes.
     *
     * @param headerText Specified ListView header text.
     */
    private void updateAfter(String headerText) {
        mLastRefresh = System.currentTimeMillis();
        mEditor.putLong("last_refresh_" + mSectionTitle, mLastRefresh);
        mEditor.commit();
        mArticleHeaderText.setText(headerText);
        mSwipeRefresh.setRefreshing(false);
    }

    // Listener for selecting an article.
    private class ArticleListClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent,
                                View view,
                                int position,
                                long id) {
            Bundle bundle = new Bundle();
            bundle.putString("article_section", mSectionTitle);
            bundle.putInt("article_id", position);
            Intent intent = new Intent(getActivity(), ArticleActivity.class);
            intent.putExtra("article", bundle);
            startActivity(intent);

        }

    }

    // Asynchronous task to make network connection and parse RSS feed...
    public class AggregatorTask extends AsyncTask<String, Integer, Integer> {

        private String cancelMessage = null;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(String... strs) {
            Integer count = -1;
            try {
                String url = SlateEntry.URL + strs[0] + "/feed";
                count = downloadXml(new URL(url), strs[0]);
                if (count == -1) {
                    this.cancel(true);
                }
            } catch (PocketSlateException e) {
                // Articles are up to date.
                cancelMessage = "Articles Up To Date";
                this.cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return count;
        }

        @Override
        protected void onProgressUpdate(Integer... p) {

        }

        @Override
        protected void onPostExecute(Integer result) {
            updateAfter(result + " ARTICLES...");
            mAsyncTask = null;
            mListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled(Integer result) {
            if (cancelMessage != null) {
                updateAfter(cancelMessage);
            } else {
                updateAfter(result + " ARTICLES...");
            }
            mAsyncTask = null;
        }

        /**
         * Returns the number of records downloaded and parsed from the RSS feed.
         *
         * @param url     Campus Slate section URL.
         * @param section Campus Slate section.
         * @return Number of records downloaded and parsed.
         * @throws IOException
         */
        private Integer downloadXml(URL url, String section) throws PocketSlateException, IOException {
            return PocketXmlParser.parse(PocketUtils.downloadUrl(url), getActivity(), section, mLastRefresh);
        }
    }
}
