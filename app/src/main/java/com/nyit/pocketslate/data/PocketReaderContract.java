/**
 * Copyright (C) 2014 Jason Scott
 */
package com.nyit.pocketslate.data;

import android.provider.BaseColumns;

/**
 * <p>PocketReaderContract.java</p>
 * <p><t>Container class for constants that define names for
 * URIs, tables, and columns for SQLite database.</t></p>
 *
 * @author jasonscott
 */
public class PocketReaderContract {

    public PocketReaderContract() {
    } // Explicitly define empty constructor

    /**
     * <p>SlateEntry</p>
     * <p><t>Inner class that defines the tables contents.</t></p>
     */
    public abstract static class SlateEntry implements BaseColumns {

        public static final String DATABASE_NAME = "CampusSlate.db";

        // Url for the campus slate
        public static final String URL = "http://www.campusslate.com/category/";

        // Campus Slate page names
        public static final String[] PAGE_NAMES = {
                "News",
                "Features",
                "Sports",
                "Editorials",
                "Staff",
                "Saved",
                "Search"
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
                "image_url"
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

        // Database table names
        public static final String[] TABLE_NAMES = {
                "news",
                "features",
                "sports",
                "editorials",
                "staff",
                "saved",
                "search"
        };

        // SimpleDateFormat Pattern
        public static final String PATTERN = "EEE, d MMM yyyy HH:mm:ss Z";

    }
}
