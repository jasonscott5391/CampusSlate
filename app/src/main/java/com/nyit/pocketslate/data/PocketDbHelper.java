/**
 * Copyright (C) 2014 Jason Scott
 */
package com.nyit.pocketslate.data;

import com.nyit.pocketslate.normalized.Entry;
import com.nyit.pocketslate.data.PocketReaderContract.SlateEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

/**
 * <p>PocketDbHelper.java</p>
 * <p><t>SQL helper for maintaining database and tables.</t></p>
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
                    + TEXT_TYPE + " )";


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
     * @param context Specified context.
     */
    private PocketDbHelper(Context context) {
        super(context, SlateEntry.DATABASE_NAME, null, DATABASE_VERSION);

        try {
            getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(String.format("%s", getClass().getSimpleName()), String.format("%s: ERROR Failed to create database!", e.getMessage()));
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String table : SlateEntry.TABLE_NAMES) {
            db.execSQL("CREATE TABLE " + table + SQL_CREATE_ENTRIES);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Returns the number of successful inserts of the specified
     * Entries into the specified table of the database.
     *
     * @param entries Specified Entries.
     * @param table   Specified table.
     * @return int of successful inserts.
     */
    public int insertEntries(ArrayList<Entry> entries, String table) {

        int entryCount = -1;

        try {

            SQLiteDatabase db = getWritableDatabase();

            SQLiteStatement statement = db.compileStatement("INSERT INTO " + table + " VALUES (?,?,?,?,?,?,?,?,?);");

            db.beginTransaction();

            entryCount = getNumEntries(table);

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
                statement.execute();

                entryCount++;
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(String.format("%s.insertEntries", getClass().getSimpleName()), String.format("%s: ERROR inserting Entry: %s", e.getMessage(), entries.get(entryCount - 1)));
        }

        Log.d(String.format("%s.insertEntries", getClass().getSimpleName()), String.format("%s entries inserted for table %s.", entryCount, table));

        return entryCount;
    }

    /**
     * Inserts the specified entry into the specified table.
     *
     * @param entry Specified Entry.
     * @param table Specified table.
     * @return Returns if the insert was successful.
     */
    public boolean insertEntry(Entry entry, String table) {

        boolean result = false;

        try {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(SlateEntry._ID, entry.getId());
            contentValues.put(SlateEntry.COLUMN_NAMES[SlateEntry.TITLE], entry.getTitle());
            contentValues.put(SlateEntry.COLUMN_NAMES[SlateEntry.LINK], entry.getLink());
            contentValues.put(SlateEntry.COLUMN_NAMES[SlateEntry.PUB_DATE], entry.getPublicationDate());
            contentValues.put(SlateEntry.COLUMN_NAMES[SlateEntry.CREATOR], entry.getCreator());
            contentValues.put(SlateEntry.COLUMN_NAMES[SlateEntry.CATEGORY], entry.getCategory());
            contentValues.put(SlateEntry.COLUMN_NAMES[SlateEntry.DESCRIPTION], entry.getDescription());
            contentValues.put(SlateEntry.COLUMN_NAMES[SlateEntry.CONTENT], entry.getContent());
            contentValues.put(SlateEntry.COLUMN_NAMES[SlateEntry.IMAGE_URL], entry.getImageUrl());

            long rowId = db.insert(table, null, contentValues);

            result = rowId != -1;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(String.format("%s.insertEntry", getClass().getSimpleName()), String.format("%s: ERROR inserting Entry: %s", e.getMessage(), entry));
        }

        return result;
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

        try {
            SQLiteDatabase db = getReadableDatabase();
            String selection = SlateEntry._ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};

            Cursor cursor =
                    db.query(
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
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(String.format("%s.retrieveEntry", getClass().getSimpleName()), String.format("%s: ERROR failed to retrieve entry for table %s with ID %s!", e.getMessage(), table, id));

        }

        return entry;
    }

    /**
     * Returns all entries from the specified table.
     *
     * @param table Specified table.
     * @return ArrayList of entries.
     */
    public ArrayList<Entry> retrieveEntries(String table) {
        ArrayList<Entry> entries = new ArrayList<>();

        try {

            SQLiteDatabase db = getReadableDatabase();

            Cursor cursor = db.query(
                    table,
                    null,
                    null,
                    null,
                    null,
                    null,
                    (table.equalsIgnoreCase("saved") ? SlateEntry._ID : SlateEntry.COLUMN_NAMES[SlateEntry.PUB_DATE] + " DESC"));

            if (cursor.moveToFirst()) {
                do {
                    entries.add(getEntryFromCursor(cursor));
                } while (cursor.moveToNext());
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(String.format("%s.retrieveEntries", getClass().getSimpleName()), String.format("%s: ERROR failed to retrieve entries for table %s!", e.getMessage(), table));
        }

        return entries;
    }

    /**
     * Removes an Entry specified by the publication date and the table.
     *
     * @param table           Specified table.
     * @param publicationDate Specified publication date.
     * @return Whether or not 1 row was deleted.
     */
    public boolean deleteEntry(String table, String publicationDate) {

        boolean result = false;

        try {

            SQLiteDatabase db = getWritableDatabase();

            String whereClause = SlateEntry.COLUMN_NAMES[SlateEntry.PUB_DATE] + " = ?";

            String[] whereArgs = new String[]{publicationDate};

            int numRows = db.delete(table, whereClause, whereArgs);

            result = numRows == 1;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(String.format("%s.deleteEntry", getClass().getSimpleName()), String.format("%s: ERROR checking if entry with publication date %s is bookmarked", e.getMessage(), publicationDate));
        }

        return result;
    }

    /**
     * Returns whether or not the supplied pubDate matches
     * a record inside the saved table.
     *
     * @param publicationDate Specified pubDate.
     * @return Whether an Entry is in the database or not.
     */
    public boolean isBookmarked(String publicationDate) {

        boolean bookmarked = false;

        try {
            SQLiteDatabase db = getReadableDatabase();

            String table = "saved";

            String selection = SlateEntry.COLUMN_NAMES[SlateEntry.PUB_DATE] + " = ?";

            String[] args = new String[]{publicationDate};

            Cursor cursor = db.query(table,
                    null,
                    selection,
                    args,
                    null,
                    null,
                    null);


            if (cursor.moveToFirst()) {
                bookmarked = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(String.format("%s.isBookmarked", getClass().getSimpleName()), String.format("%s: ERROR checking if entry with publication date %s is bookmarked", e.getMessage(), publicationDate));
        }

        return bookmarked;

    }

    /**
     * Returns the number of entries in the specified table.
     *
     * @param table Name of table.
     * @return Number of entries.
     */
    public int getNumEntries(String table) {
        int numEntries = -1;
        try {
            SQLiteDatabase db = getReadableDatabase();

            numEntries = (int) DatabaseUtils.queryNumEntries(db, table);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(String.format("%s.getNumEntries", getClass().getSimpleName()), String.format("%s: ERROR deleting table: %s", e.getMessage(), table));
        }

        return numEntries;
    }

    /**
     * Delete the specified table from the database.
     *
     * @param table Specified table.
     */
    public void deleteTable(String table) {
        try {
            getWritableDatabase().delete(table, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(String.format("%s.deleteTable", getClass().getSimpleName()), String.format("%s: ERROR deleting table: %s", e.getMessage(), table));
        }
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
        entry.setPublicationDate(cursor.getLong(3));        // Publication Date
        entry.setCreator(cursor.getString(4));              // Creator
        entry.setCategory(cursor.getString(5));             // Category
        entry.setDescription(cursor.getString(6));          // Description
        entry.setContent(cursor.getString(7));              // Content
        entry.setImageUrl(cursor.getString(8));             // Image URL

        return entry;
    }

    /**
     * Returns an ArrayList of Entries that matched the specified
     * query in the specified column from the specified table if any.
     *
     * @param table  Specified table name.
     * @param column Specified column name.
     * @param query  Specified query String.
     * @return ArrayList of type Entry.
     */
    public ArrayList<Entry> getEntriesMatching(String table, String column, String query) {

        ArrayList<Entry> entries = new ArrayList<>();

        String selection = String.format("%s LIKE ?", column);
        String[] selectionArgs = new String[]{"%" + query + "%"};

        try {

            SQLiteDatabase db = getReadableDatabase();

            Cursor cursor = db.query(table, null, selection, selectionArgs, null, null, null);

            if (cursor != null
                    && cursor.moveToFirst()) {
                do {
                    entries.add(getEntryFromCursor(cursor));
                } while (cursor.moveToNext());
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(String.format("%s.query", getClass().getSimpleName()), String.format("%s: ERROR querying table %s with selection %s and selection arguments %s!",
                    e.getMessage(),
                    table,
                    selection,
                    selectionArgs));
        }


        Log.d(String.format("%s.getEntriesMatching", getClass().getSimpleName()), String.format("Query %s on table %s and column %s returned %s results!", query, table, column, entries.size()));

        return entries;
    }
}
