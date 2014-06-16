/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.nyit.campusslate.Entry;
import edu.nyit.campusslate.utils.PocketReaderContract.SlateEntry;
/**
 * <p>Title: PocketDbHelper.java</p>
 * @author jasonscott
 *
 */
public class PocketDbHelper extends SQLiteOpenHelper {
	//TODO deleteEntry(), correctEntries(), searchEntries(), onUpgrade()
	private static PocketDbHelper sInstance = null;
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "CampusSlate.db";
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	//private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ";
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
	 *	getInstance - Use instead of constructor. 
	 * @param c - Application Context
	 * @return sInstance - static instance of applications database
	 */
	public static PocketDbHelper getInstance(Context c) {
		if(sInstance == null) {
			sInstance = new PocketDbHelper(c.getApplicationContext());
		}

		return sInstance;
	}

	/**
	 * Private constructor only to be used in this class.  Use 
	 * getInstance method to get Applications database.
	 * @param context
	 */
	private PocketDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Overridden methods

	@Override
	public void onCreate(SQLiteDatabase db) {
		for(String table : SlateEntry.TABLE_NAMES) {
			db.execSQL("CREATE TABLE " + table + SQL_CREATE_ENTRIES);
		}
	}

	// TODO Write onUpgrade to handle inserts and updates for new content from RSS feed
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// Database Table operations

	/**
	 * Inserts an Entry into a table of the database.
	 * @param entry - Entry to be inserted
	 * @param table - String for table to insert
	 * @return the row ID of entry inserted
	 */
	public long insertEntry(Entry entry, String table) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(SlateEntry.COLUMN_NAMES[SlateEntry.TITLE], entry.title);
		values.put(SlateEntry.COLUMN_NAMES[SlateEntry.LINK], entry.link);
		values.put(SlateEntry.COLUMN_NAMES[SlateEntry.PUB_DATE], entry.pubDate);
		values.put(SlateEntry.COLUMN_NAMES[SlateEntry.CREATOR], entry.creator);
		values.put(SlateEntry.COLUMN_NAMES[SlateEntry.CATEGORY], entry.category);
		values.put(SlateEntry.COLUMN_NAMES[SlateEntry.DESCRIPTION], entry.description);
		values.put(SlateEntry.COLUMN_NAMES[SlateEntry.CONTENT], entry.content);
		values.put(SlateEntry.COLUMN_NAMES[SlateEntry.IMAGE_URL], entry.imageUrl);
		values.put(SlateEntry.COLUMN_NAMES[SlateEntry.BOOKMARKED], entry.bookmarked);

		long inserted = db.insert(table, null, values);
		//db.close();
		//Log.d("db.insert() returns ", String.valueOf(inserted));
		return inserted;
	}

	/**
	 * Updates a specified column of a specified entry of a specified table 
	 * in the database.
	 * @param table - table of database to find entry
	 * @param column - column of entry to be updated
	 * @param value - new text to be stored in column of entry
	 * @param id - entries identifier
	 */
	public int updateEntry(String table, String column, String value, String id) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(column, value);

		String selection = SlateEntry._ID + " = ?";
		String[] selectionArgs = { String.valueOf(id) };

		return db.update(table, values, selection, selectionArgs);

	}

	/**
	 * Retrieves an entry from the database.
	 * @param table - name of table to pull item
	 * @param id - entry identifier in table
	 * @return Entry
	 */
	public Entry retrieveEntry(String table, String id) {
		SQLiteDatabase db = getReadableDatabase();

		Entry entry = null;

		String selection = SlateEntry._ID + " = ?";
		String[] selectionArgs = { String.valueOf(id) };

		Cursor cursor = db.query(table, null, selection, selectionArgs, null, null, null);

		if(cursor.moveToFirst()) {
			do {
				entry = new Entry(
						cursor.getString(0), 	// Identifier
						cursor.getString(1),	// Title
						cursor.getString(2),	// Link
						cursor.getString(3),	// Publication Date
						cursor.getString(4),	// Creator
						cursor.getString(5),	// Category
						cursor.getString(6),	// Description
						cursor.getString(7),	// Content
						cursor.getString(8),	// Image URL
						cursor.getString(9)		// Bookmarked
						);
			} while(cursor.moveToNext()); 	
			cursor.close();
		}
		//db.close();

		return entry;
	}

	//TODO deleteEntry

	/**
	 * Deletes an entry from the database.
	 * @param table - entry's table name
	 * @param id - entry's id
	 * @return int, number of rows affected
	 */
	public int deleteEntry(String table, String id) {
		//TODO Algorithm to correct _ID column for unaffected rows
		SQLiteDatabase db = getWritableDatabase();

		String selection = SlateEntry._ID + " = ?";
		String[] selectionArgs = { String.valueOf(id) };

		return db.delete(table, selection, selectionArgs);

	}

	public void correctEntries(String table, String id) {
		//long size = DatabaseUtils.queryNumEntries(getReadableDatabase(), table);
		//long affectedRows = size - Long.valueOf(id);


	}

	//TODO searchEntries

	public Cursor searchEntries(String query) {
		Cursor cursor = null;
		return cursor;
	}

	/**
	 * Queries Database table for number of entries.
	 * @param table - Name of table.
	 * @return int - Number of entries.
	 */
	public int getNumEntries(String table) {
		return (int)DatabaseUtils.queryNumEntries(getReadableDatabase(), table);
	}
}
