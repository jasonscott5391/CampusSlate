/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate.utils;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import edu.nyit.campusslate.Entry;

/**
 * <p>Title: PocketXmlParser.java</p>
 * @author jasonscott
 *
 */
public class PocketXmlParser {

	//TODO parseBuildDate - static method to retrieve build date from xml feed
	
	/**
	 * Parses an InputStream from the Campus Slate web site and
	 * calls addEntry method when an article is parsed.
	 * @param in - InputStream to be parsed.
	 * @param context - Application Context needed to access database in addEntry method.
	 * @param section - String for the specific section RSS feed.
	 * @return boolean for the success or failure of parsing
	 */
	public static Integer parse(InputStream in, Context context, String section) {
		Integer count = new Integer(0);
		Entry entry = null;

		XmlPullParserFactory factory = null;
		XmlPullParser parser = null;


		String title = null;
		String link = null;
		String pubDate = null;
		String creator = null;
		String category = null;
		String description = null;
		String content = null;
		String imageUrl = null;
		String bookmarked = new String("no");			// Initially each entry is not stored in user's bookmarks

		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);

			parser = factory.newPullParser();
			parser.setInput(in, null);

			String eventText = null;
			int eventType = parser.getEventType();

			while(eventType != XmlPullParser.END_DOCUMENT) {
				String tagName = parser.getName();     

				switch(eventType) {
				case XmlPullParser.TEXT:
					eventText = parser.getText();
					break;
				case XmlPullParser.END_TAG:
					if(tagName.equalsIgnoreCase("item")) {

						entry = new Entry(null,
								title,
								link,
								pubDate,
								creator,
								category,
								description,
								content,
								imageUrl,
								bookmarked);

						if(addEntry(entry, context, section) != -1) {
							count++;
						} else {
							//TODO Handle error inserting database
						}
					} else if(tagName.equalsIgnoreCase("title")) {
						title = eventText;
					} else if(tagName.equalsIgnoreCase("link")) {
						link = eventText;
					} else if(tagName.equalsIgnoreCase("pubDate")) {
						if(eventText != null) {
							pubDate = eventText.substring(0, eventText.length() - 6);
						}
					} else if(tagName.equalsIgnoreCase("creator")) {
						creator = eventText;
					} else if(tagName.equalsIgnoreCase("category")) {
						category = eventText;
					} else if(tagName.equalsIgnoreCase("description")) {
						description = eventText;
					} else if(tagName.equalsIgnoreCase("encoded")) {					// From "content:encoded"
						content = eventText;
						//TODO Parse content for image URL
					}
					break;
				default:
					break;
				}
				eventType = parser.next();
			}
		} catch(XmlPullParserException e) {
			//TODO Handle XmlPullParserException
		} catch(IOException e) {
			//TODO Handle IOException accordingly
		}
		return count;
	}

	/**
	 * Calls insertEntry method of PocketDbHelper to effectively
	 * add an article to the database.
	 * @param entry - Entry to be inserted.
	 * @param context - Applications Context to access database
	 * @param section - String for the specific section RSS feed.
	 * @return long for row ID of inserted Entry or -1 if error occurred
	 */
	private static long addEntry(Entry entry, Context context, String section) {
		long insert = PocketDbHelper.getInstance(context).insertEntry(entry, section);

		return insert;

	}

}
