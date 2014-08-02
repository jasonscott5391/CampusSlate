/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate;

import edu.nyit.campusslate.utils.PocketListAdapter;
import edu.nyit.campusslate.utils.PocketReaderContract.SlateEntry;
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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Locale;


/**
 * <p>Title: ArticleListFragment.</p>
 *
 * @author jasonscott
 */
public class ArticleListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final long FIFTEEN_MINUTES = 1000 * 60 * 15;
    private static final String UPDATE = "...UPDATING...";

    private SwipeRefreshLayout mSwipeRefresh;
    private ListView mArticleList;
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
            mArticleHeaderText.setText(UPDATE);
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
        if (mAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
            mAsyncTask.execute(mSectionTitle.toLowerCase(Locale.US));
            mSwipeRefresh.setRefreshing(true);
        }
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

    // Asyncronous task to make network connection and parse RSS feed
    private class AggregatorTask extends AsyncTask<String, Integer, Integer> {


        @Override
        protected void onPreExecute() {
            mArticleHeaderText.setText(UPDATE);
        }

        @Override
        protected Integer doInBackground(String... strs) {
            Integer count = -1;
            try {
                count = downloadXml(new URL(SlateEntry.URL + strs[0] + "/feed"), strs[0]);
                if (count == -1) {
                    this.cancel(true);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return count;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPostExecute(Integer result) {
            mArticleHeaderText.setText("..." + result + " ARTICLES...");
            mSwipeRefresh.setRefreshing(false);
            mLastRefresh = System.currentTimeMillis();
            mEditor.putLong("last_refresh_" + mSectionTitle, mLastRefresh);
            mEditor.commit();
            mListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled(Integer result) {
            onRefresh();
        }

        /**
         * @param url Campus Slate section URL.
         * @param section Campus Slate section.
         * @return Returns number of articles downloaded.
         * @throws IOException
         */
        private Integer downloadXml(URL url, String section) throws IOException {
            return PocketXmlParser.parse(downloadUrl(url), getActivity(), section);
        }

        /**
         * @param url Url to establish Http connection.
         * @return Returns and InputStream from Http connection.
         * @throws IOException
         */
        private InputStream downloadUrl(URL url) throws IOException {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000); // milliseconds
            conn.setConnectTimeout(10000); // milliseconds
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            return conn.getInputStream();
        }

    }
}
