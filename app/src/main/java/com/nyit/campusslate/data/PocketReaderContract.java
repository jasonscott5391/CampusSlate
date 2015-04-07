/**
 * Copyright (C) 2014 Jason Scott
 */
package com.nyit.campusslate.data;

import android.provider.BaseColumns;

/**
 * <p>Title: PocketReaderContract.</p>
 * <p>Description:</p>
 * @author jasonscott
 */
public class PocketReaderContract {
    //COMPLETE
    public PocketReaderContract() {
    } // Empty Constructor

    /**
     * <p>Title: SlateEntry.</p>
     * <p>Inner class that defines the tables contents.</p>
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

        // SimpleDateFormat Pattern
        public static final String PATTERN = "EEE, d MMM yyyy HH:mm:ss Z";

    }
}
