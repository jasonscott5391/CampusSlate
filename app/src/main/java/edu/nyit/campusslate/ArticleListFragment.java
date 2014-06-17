/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate;

import edu.nyit.campusslate.utils.PocketDbHelper;
import edu.nyit.campusslate.utils.PocketListAdapter;
import edu.nyit.campusslate.utils.PocketReaderContract.SlateEntry;
import edu.nyit.campusslate.utils.PocketXmlParser;

import android.content.Intent;
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
import java.util.Locale;


/**
 * <p>Title: ArticleListFragment.</p>
 * <p>Description:  wwwww</p>
 * @author jasonscott
 */
public class ArticleListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefresh;
    private ListView mArticleList;
    private PocketListAdapter mListAdapter;
    private View mArticleHeader;
    private TextView mArticleHeaderText;
    private String mSectionTitle;
    private Integer mCount = 0;
    private AggregatorTask mAsyncTask = new AggregatorTask();

    //	private Long mLastRefresh;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSectionTitle = getArguments().getString("title");

        mArticleHeader = (View) getActivity().getLayoutInflater()
                .inflate(R.layout.article_list_header, null, true);

        mArticleHeaderText = (TextView) mArticleHeader.findViewById(R.id.article_count);
        mCount = PocketDbHelper.getInstance(getActivity())
                .getNumEntries(mSectionTitle.toLowerCase(Locale.US));
        if (mCount == 0) {
            mArticleHeaderText.setText("...PULL DOWN TO REFRESH...");
        } else {
            mArticleHeaderText.setText("..." + mCount + " ARTICLES...");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;

        view = inflater.inflate(R.layout.fragment_article_list, container, false);
        mArticleList = (ListView) view.findViewById(R.id.article_list_view);
        mArticleList.addHeaderView(mArticleHeader);

        mListAdapter = new PocketListAdapter(getActivity(),
                mSectionTitle.toLowerCase(Locale.US));
        mArticleList.setAdapter(mListAdapter);
        mArticleList.setOnItemClickListener(new ArticleListClickListener());

        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorScheme(R.color.nyit_yellow,
                R.color.nyit_blue,
                R.color.nyit_blue,
                R.color.nyit_yellow);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onRefresh() {
        if (mAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
            mAsyncTask.execute(mSectionTitle.toLowerCase(Locale.US));
        }
    }

    private class ArticleListClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
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
            mSwipeRefresh.setRefreshing(true);
            mArticleHeaderText.setText("...UPDATING...");
        }

        @Override
        protected Integer doInBackground(String... strs) {

            try {
                return downloadXml(new URL(SlateEntry.URL + strs[0] + "/feed"),
                        strs[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return -1;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPostExecute(Integer result) {
            mCount += result;
            mArticleHeaderText.setText("..." + mCount + " ARTICLES...");
            mSwipeRefresh.setRefreshing(false);
            mListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled(Integer result) {

        }

        /**
         * @param url
         * @param table
         * @return
         * @throws IOException
         */
        private Integer downloadXml(URL url, String table) throws IOException {
            return PocketXmlParser.parse(downloadUrl(url), getActivity(), table);
        }

        /**
         * @param url
         * @return
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
