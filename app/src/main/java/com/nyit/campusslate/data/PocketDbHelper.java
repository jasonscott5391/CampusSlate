/**
 * Copyright (C) 2014 Jason Scott
 */
package com.nyit.campusslate.data;

import com.nyit.campusslate.normalized.Entry;
import com.nyit.campusslate.data.PocketReaderContract.SlateEntry;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

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
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            " (" +
                    SlateEntry._ID + INT_TYPE + " PRIMARY KEY," +
                    SlateEntry.COLUMN_NAMES[SlateEntry.TITLE]
                    + TEXT_TYPE + COMMA_SEP
                    + SlateEntry.COLUMN_NAMES[SlateEntry.LINK]
                    + TEXT_TYPE + COMMA_SEP
                    + SlateEntry.COLUMN_NAMES[SlateEntry.PUB_DATE]
                    + INT_TYPE + COMMA_SEP
                    + SlateEntry.COLUMN_NAMES[SlateEntry.CREATOR]
                    + TEXT_TYPE + COMMA_SEP
                    + SlateEntry.COLUMN_NAMES[SlateEntry.CATEGORY]
                    + TEXT_TYPE + COMMA_SEP
                    + SlateEntry.COLUMN_NAMES[SlateEntry.DESCRIPTION]
                    + TEXT_TYPE + COMMA_SEP
                    + SlateEntry.COLUMN_NAMES[SlateEntry.CONTENT]
                    + TEXT_TYPE + COMMA_SEP
                    + SlateEntry.COLUMN_NAMES[SlateEntry.IMAGE_URL]
                    + TEXT_TYPE + COMMA_SEP
                    + SlateEntry.COLUMN_NAMES[SlateEntry.BOOKMARKED]
                    + INT_TYPE + " )";


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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Database Table operations

    /**
     * Returns the number of successful inserts of the specified
     * Entries into the specified table of the database.
     *
     * @param entries Specified Entries.
     * @param table   Specified table.
     * @return int of successful inserts.
     */
    public int insertEntries(ArrayList<Entry> entries, String table) {

        SQLiteDatabase database = getWritableDatabase();

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ")
                .append(table)
                .append(" VALUES (?,?,?,?,?,?,?,?,?,?);");

        SQLiteStatement statement = database.compileStatement(sql.toString());

        database.beginTransaction();

        int entryCount = getNumEntries(table);

        for (Entry entry : entries) {
            statement.clearBindings();
            statement.bindNull(1);
            statement.bindString(2, entry.getTitle());
            statement.bindString(3, entry.getLink());
            statement.bindLong(4, entry.getPublicationDate());
            statement.bindString(5, entry.getCreator());
            statement.bindString(6, entry.getCategory());
            statement.bindString(7, entry.getDescription());
            statement.bindString(8, entry.getContent());
            statement.bindString(9, entry.getImageUrl());
            statement.bindLong(10, entry.getBookmarked());
            statement.execute();

            entryCount++;
        }

        database.setTransactionSuccessful();
        database.endTransaction();

        return entryCount;
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

        Cursor cursor =
                getReadableDatabase().query(
                        table,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);

        if (cursor.moveToFirst()) {
            do {

                entry = getEntryFromCursor(cursor);

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
                    entries.add(getEntryFromCursor(cursor));
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
        return (int) DatabaseUtils.queryNumEntries(
                getReadableDatabase(),
                table);
    }

    /**
     * Returns an Entry with values from the specified Cursor.
     *
     * @param cursor Specified Cursor.
     * @return Campus Slate Entry.
     */
    private Entry getEntryFromCursor(Cursor cursor) {
        Entry entry = new Entry();

        entry.setId(cursor.getInt(0));                      // Identifier
        entry.setTitle(cursor.getString(1));                // Title
        entry.setLink(cursor.getString(2));                 // Link
        entry.setPublicationDate(cursor.getLong(3));      // Publication Date
        entry.setCreator(cursor.getString(4));              // Creator
        entry.setCategory(cursor.getString(5));             // Category
        entry.setDescription(cursor.getString(6));          // Description
        entry.setContent(cursor.getString(7));              // Content
        entry.setImageUrl(cursor.getString(8));             // Image URL
        entry.setBookmarked(cursor.getInt(9));           // Bookmarked

        return entry;
    }
}
