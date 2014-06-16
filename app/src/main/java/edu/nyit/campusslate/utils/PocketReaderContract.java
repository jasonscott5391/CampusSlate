/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate.utils;

import android.provider.BaseColumns;
/**
 * <p>Title: PocketReaderContract.java</p>
 * @author jasonscott
 *
 */
public class PocketReaderContract {
	//COMPLETE
	public PocketReaderContract() {} // Empty Constructor

	// Inner class that defines the tables contents
	public static abstract class SlateEntry implements BaseColumns {

		// Url for the campus slate
		public static final String URL_NEWS = "http://www.campusslate.com/category/news/feed/";
		public static final String URL_FEATURES = "http://www.campusslate.com/category/features/feed/";
		public static final String URL_SPORTS = "http://www.campusslate.com/category/sports/feed/";
		public static final String URL_EDITORIALS = "http://www.campusslate.com/category/editorials/feed/";
		public static final String URL_STAFF = "http://www.campusslate.com/category/staff/feed/";
		
		public static final String URL = "http://www.campusslate.com/category/";
		
		// Campus Slate RSS feed URLS
		public static final String[] URLS = { 
			"http://www.campusslate.com/category/news/feed/",
			"http://www.campusslate.com/category/features/feed/",
			"http://www.campusslate.com/category/sports/feed/",
			"http://www.campusslate.com/category/editorials/feed/",
			"http://www.campusslate.com/category/staff/feed/" };
		
		// RSS feed identifiers
		public static final int NEWS = 0;
		public static final int FEATURES = 1;
		public static final int SPORTS = 2;
		public static final int EDITORIALS = 3;
		public static final int STAFF = 4;
		
		public static final String[] PAGE_NAMES = {
			"News",
			"Features",
			"Sports",
			"Editorials",
			"Staff"
		};
		
		// Database column names
		public static final String[] COLUMN_NAMES = {
			"title",
			"link",
			"publication_date",
			"creator",
			"category",
			"description",
			"content",
			"image_url",
			"bookmarked"
		};
		
		// Column name index identifiers
		public static final int TITLE = 0;
		public static final int LINK = 1;
		public static final int PUB_DATE = 2;
		public static final int CREATOR = 3;
		public static final int CATEGORY = 4;
		public static final int DESCRIPTION = 5;
		public static final int CONTENT = 6;
		public static final int IMAGE_URL = 7;
		public static final int BOOKMARKED = 8;
		
		// Database table names
		public static final String[] TABLE_NAMES = {
			"news",
			"features",
			"sports",
			"editorials",
			"staff",
			"bookmarks"
		};
		
	}
}
