package com.nyit.pocketslate.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nyit.pocketslate.R;
import com.nyit.pocketslate.activities.ArticleActivity;
import com.nyit.pocketslate.utils.PocketListAdapter;

import java.util.Locale;

/**
 * <p>LocalArticleListFragment.java</p>
 * <p><t>Fragment containing a list of SAVED or SEARCHED articles.  Needed
 * a different class to disable swipe refresh.</t></p>
 *
 * @author jasonscott
 */
public class LocalArticleListFragment extends Fragment {

    private ListView mArticleList;
    private PocketListAdapter mListAdapter;
    private View mArticleHeader;
    private TextView mArticleHeaderText;
    private String mSectionTitle;
    private SharedPreferences.Editor mEditor;
    private String localListType;

    public static final String SAVED = "SAVED";

    public static final String SEARCHED = "SEARCHED";

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
        SwipeRefreshLayout swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setEnabled(false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences userPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        String headerText = "";

        if (this.localListType.equalsIgnoreCase("SAVED")) {
            headerText = String.format("%s SAVED ARTICLES...", (mArticleList.getCount() - 1));
        } else if (this.localListType.equalsIgnoreCase("SEARCHED")) {
            String lastQuery = userPrefs.getString("lastQuery", null);
            String resultsCount = String.valueOf(userPrefs.getInt("lastQueryResultsCount", (mArticleList.getCount() - 1)));
            headerText = String.format("%s ARTICLES RETURNED FOR SEARCH %s...", resultsCount, lastQuery != null ? lastQuery : "");
        }

        setHeaderText(headerText);

        int position = userPrefs.getInt(mSectionTitle + "_last_index", 0);
        int offset = userPrefs.getInt(mSectionTitle + "_last_index_offset", 0);

        if (position != 0
                && offset != 0) {
            mArticleList.setSelectionFromTop(position, offset);
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

    public String getLocalListType() {
        return localListType;
    }

    public void setLocalListType(String localListType) {
        this.localListType = localListType;
    }

    /**
     * Sets the text of the header TextView with the specified headerText.
     *
     * @param headerText Specified text.
     */
    private void setHeaderText(String headerText) {
        mArticleHeaderText.setText(headerText);
    }

    /**
     * Returns the title of this fragments section.
     *
     * @return String of the section.
     */
    public String getSectionTitle() {
        return mSectionTitle;
    }

    /**
     *
     */
    public void updatedSearch() {
        SharedPreferences userPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        String headerText = String.format("%s ARTICLES RETURNED FOR SEARCH %s...",
                userPrefs.getInt("lastQueryResultsCount", (mArticleList.getCount() - 1)),
                userPrefs.getString("lastQuery", ""));

        setHeaderText(headerText);

        mListAdapter.notifyDataSetChanged();
    }
}
