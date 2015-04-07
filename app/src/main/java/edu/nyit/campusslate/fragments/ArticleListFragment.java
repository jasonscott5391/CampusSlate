/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate.fragments;

import edu.nyit.campusslate.R;
import edu.nyit.campusslate.activities.ArticleActivity;
import edu.nyit.campusslate.exceptions.PocketBuildException;
import edu.nyit.campusslate.utils.PocketUtils;
import edu.nyit.campusslate.utils.PocketListAdapter;
import edu.nyit.campusslate.data.PocketReaderContract.SlateEntry;
import edu.nyit.campusslate.utils.PocketXmlParser;

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
 * <p>Title: ArticleListFragment.</p>
 * <p>Description: </p>
 *
 * @author jasonscott
 */
public class ArticleListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final long FIFTEEN_MINUTES = 1000 * 60 * 15;
    private static final String UPDATE = "UPDATING...";

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
        ListView mArticleList = (ListView) view.findViewById(R.id.article_list_view);
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
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //TODO(jasonscott) Save page state.
    }

    @Override
    public void onRefresh() {
        if (mAsyncTask != null) {
            mArticleHeaderText.setText(UPDATE);
            mAsyncTask.execute(mSectionTitle.toLowerCase(Locale.US));
            mSwipeRefresh.setRefreshing(true);
        } else {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void updateAfter(String headerText) {
        mLastRefresh = System.currentTimeMillis();
        mEditor.putLong("last_refresh_" + mSectionTitle, mLastRefresh);
        mEditor.commit();
        mArticleHeaderText.setText(headerText);
        mSwipeRefresh.setRefreshing(false);
    }

    private class ArticleListClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent,
                                View view,
                                int position,
                                long id) {
            Bundle bundle = new Bundle();
            bundle.putString("section_title", mSectionTitle);
            bundle.putString("article_id", String.valueOf(position - 1));
            Intent intent = new Intent(getActivity(), ArticleActivity.class);
            intent.putExtra("article", bundle);
            startActivity(intent);

        }

    }

    // Asyncronous task to make network connection and parse RSS feed    implements PocketCallBack
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
            } catch (PocketBuildException e) {
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
         * @param url     Campus Slate section URL.
         * @param section Campus Slate section.
         * @return Returns number of articles downloaded.
         * @throws IOException
         */
        private Integer downloadXml(URL url, String section) throws PocketBuildException, IOException {
            return PocketXmlParser.parse(PocketUtils.downloadUrl(url), getActivity(), section, mLastRefresh);
        }
    }
}
