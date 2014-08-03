/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate.utils;

import edu.nyit.campusslate.Entry;
import edu.nyit.campusslate.utils.PocketReaderContract.SlateEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * <p>Title: PocketDbHelper.</p>
 * <p>Description:</p>
 *
 * @author jasonscott
 */
public class PocketDbHelper extends SQLiteOpenHelper {

    private static PocketDbHelper sInstance = null;
    public static final int DATABASE_VERSION = 1;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            " (" +
                    SlateEntry._ID + " INTEGER PRIMARY KEY," +
                    SlateEntry.COLUMN_NAMES[SlateEntry.TITLE] + TEXT_TYPE + COMMA_SEP +
                    SlateEntry.COLUMN_NAMES[SlateEntry.LINK] + TEXT_TYPE + COMMA_SEP +
                    SlateEntry.COLUMN_NAMES[SlateEntry.PUB_DATE] + TEXT_TYPE + COMMA_SEP +
                    SlateEntry.COLUMN_NAMES[SlateEntry.CREATOR] + TEXT_TYPE + COMMA_SEP +
                    SlateEntry.COLUMN_NAMES[SlateEntry.CATEGORY] + TEXT_TYPE + COMMA_SEP +
                    SlateEntry.COLUMN_NAMES[SlateEntry.DESCRIPTION] + TEXT_TYPE + COMMA_SEP +
                    SlateEntry.COLUMN_NAMES[SlateEntry.CONTENT] + TEXT_TYPE + COMMA_SEP +
                    SlateEntry.COLUMN_NAMES[SlateEntry.IMAGE_URL] + TEXT_TYPE + COMMA_SEP +
                    SlateEntry.COLUMN_NAMES[SlateEntry.BOOKMARKED] + TEXT_TYPE + " )";


    /**
     * getInstance - Use instead of constructor.
     *
     * @param c - Application Context
     * @return sInstance - static instance of applications database
     */
    public static PocketDbHelper getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new PocketDbHelper(c.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Private constructor only to be used in this class.  Use
     * getInstance method to get Applications database.
     *
     * @param context
     */
    private PocketDbHelper(Context context) {
        super(context, SlateEntry.DATABASE_NAME, null, DATABASE_VERSION);
        getWritableDatabase();
    }

    // Overridden methods

    @Override
    public void onCreate(SQLiteDatabase db) {
//        mDatabase = db;
        for (String table : SlateEntry.TABLE_NAMES) {
            db.execSQL("CREATE TABLE " + table + SQL_CREATE_ENTRIES);
        }
    }

    // TODO(jasonscott)(jasonscott) Write onUpgrade to handle inserts and updates for new content from RSS feed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Database Table operations

    /**
     * Inserts a collection of Entries into a table of the database.
     *
     * @param entries - ArrayList of type Entry to be inserted.
     * @param table - String for table to insert.
     * @return number of rows inserted.
     */
    public int insertEntries(ArrayList<Entry> entries, String table) {
        int count = 0;
        for (Entry entry : entries) {
            ContentValues values = new ContentValues();

            values.put(SlateEntry.COLUMN_NAMES[SlateEntry.TITLE], entry.title);
            values.put(SlateEntry.COLUMN_NAMES[SlateEntry.LINK], entry.link);
            values.put(SlateEntry.COLUMN_NAMES[SlateEntry.PUB_DATE], entry.publicationDate);
            values.put(SlateEntry.COLUMN_NAMES[SlateEntry.CREATOR], entry.creator);
            values.put(SlateEntry.COLUMN_NAMES[SlateEntry.CATEGORY], entry.category);
            values.put(SlateEntry.COLUMN_NAMES[SlateEntry.DESCRIPTION], entry.description);
            values.put(SlateEntry.COLUMN_NAMES[SlateEntry.CONTENT], entry.content);
            values.put(SlateEntry.COLUMN_NAMES[SlateEntry.IMAGE_URL], entry.imageUrl);
            values.put(SlateEntry.COLUMN_NAMES[SlateEntry.BOOKMARKED], entry.bookmarked);

            if((getWritableDatabase().insert(table, null, values)) != -1) {
                count++;
            }
        }
        return count;
    }

    /**
     * Retrieves an entry from the database.
     *
     * @param table - name of table to pull item
     * @param id    - entry identifier in table
     * @return Entry
     */
    public Entry retrieveEntry(String table, String id) {

        Entry entry = null;

        String selection = SlateEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = getReadableDatabase().query(table, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                entry = new Entry(
                        cursor.getString(0),    // Identifier
                        cursor.getString(1),    // Title
                        cursor.getString(2),    // Link
                        cursor.getString(3),    // Publication Date
                        cursor.getString(4),    // Creator
                        cursor.getString(5),    // Category
                        cursor.getString(6),    // Description
                        cursor.getString(7),    // Content
                        cursor.getString(8),    // Image URL
                        cursor.getString(9)        // Bookmarked
                );
            } while (cursor.moveToNext());
            cursor.close();
        }

        return entry;
    }

    /**
     * @param table
     * @return
     */
    public ArrayList<Entry> retrieveTable(String table) {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.query(
                    table,
                    null,
                    null,
                    null,
                    null,
                    null,
                    SlateEntry.COLUMN_NAMES[SlateEntry.PUB_DATE] + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    entries.add(new Entry(
                            cursor.getString(0),    // Identifier
                            cursor.getString(1),    // Title
                            cursor.getString(2),    // Link
                            cursor.getString(3),    // Publication Date
                            cursor.getString(4),    // Creator
                            cursor.getString(5),    // Category
                            cursor.getString(6),    // Description
                            cursor.getString(7),    // Content
                            cursor.getString(8),    // Image URL
                            cursor.getString(9)     // Bookmarked
                    ));
                } while (cursor.moveToNext());
                cursor.close();
            }
            return entries;
        }
        return null;
    }

    /**
     * Queries Database table for number of entries.
     *
     * @param table - Name of table.
     * @return int - Number of entries.
     */
    public int getNumEntries(String table) {
        return (int) DatabaseUtils.queryNumEntries(getReadableDatabase(), table);
    }
}
