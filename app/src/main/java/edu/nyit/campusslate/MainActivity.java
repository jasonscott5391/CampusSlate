/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import edu.nyit.campusslate.utils.PocketReaderContract.SlateEntry;
/**
 * <p>Title: MainActivity.java</p>
 * @author jasonscott
 *
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	
	private PocketSectionPagerAdapter mSectionPagerAdapter;
	private ViewPager mSectionViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mSectionPagerAdapter = new PocketSectionPagerAdapter(getSupportFragmentManager());
		
		final ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		
		mSectionViewPager = (ViewPager)findViewById(R.id.section_pager);
		mSectionViewPager.setAdapter(mSectionPagerAdapter);
		mSectionViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});
		
		
		
		if (savedInstanceState == null) { 
		}
		
		for(int i=0; i<mSectionPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab().setText(mSectionPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// Save current tab
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
		if (id == R.id.action_search) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mSectionViewPager.setCurrentItem(tab.getPosition(), true);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}
	
	public static class PocketSectionPagerAdapter extends FragmentStatePagerAdapter {

		public PocketSectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			
			ArticleListFragment fragment = new ArticleListFragment();
			Bundle args = new Bundle();
			args.putString("title", SlateEntry.PAGE_NAMES[position]);
			fragment.setArguments(args);
			
			return fragment;
		}

		@Override
		public int getCount() {
			return 5;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return SlateEntry.PAGE_NAMES[position];
		}
		
	}
}
