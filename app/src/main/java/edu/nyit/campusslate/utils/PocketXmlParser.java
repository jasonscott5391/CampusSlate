/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate.utils;

import edu.nyit.campusslate.Entry;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Title: PocketXmlParser.</p>
 * <p>Description:</p>
 *
 * @author jasonscott
 */
public class PocketXmlParser {

    //TODO(jasonscott) parseBuildDate - static method to retrieve build date from xml feed

    /**
     * Parses an InputStream from the Campus Slate web site and
     * calls addEntry method when an article is parsed.
     *
     * @param in      - InputStream to be parsed.
     * @param context - Application Context needed to access database in addEntry method.
     * @param section - String for the specific section RSS feed.
     * @return boolean for the success or failure of parsing
     */
    public static Integer parse(InputStream in, Context context, String section) {
        Integer count = 0;
        Entry entry;

        XmlPullParserFactory factory;
        XmlPullParser parser;


        String title = null;
        String link = null;
        String pubDate = null;
        String creator = null;
        String category = null;
        String description = null;
        String content = null;
        String imageUrl = null;
        String bookmarked = "no";

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            parser = factory.newPullParser();
            parser.setInput(in, null);

            String eventText = null;
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.TEXT:
                        eventText = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase("item")) {

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

                            if (addEntry(entry, context, section) != -1) {
                                Log.d("Parser parse()", "Article # " + count + " added to " + section + "!");
                                count++;
                            } else {
                                //TODO(jasonscott) Handle error inserting database
                                Log.d("Parser parse()", "Error inserting to db, count is " + String.valueOf(count));
                            }
                        } else if (tagName.equalsIgnoreCase("title")) {
                            title = eventText;
                        } else if (tagName.equalsIgnoreCase("link")) {
                            link = eventText;
                        } else if (tagName.equalsIgnoreCase("pubDate")) {
                            if (eventText != null) {
//							pubDate = eventText.substring(0, eventText.length() - 6);
                                SimpleDateFormat simpleDateFormat =
                                        new SimpleDateFormat(PocketReaderContract.SlateEntry.PATTERN);
                                Date date = simpleDateFormat.parse(eventText);
                                pubDate = String.valueOf(date.getTime());
                            }
                        } else if (tagName.equalsIgnoreCase("creator")) {
                            creator = eventText;
                        } else if (tagName.equalsIgnoreCase("category")) {
                            category = eventText;
                        } else if (tagName.equalsIgnoreCase("description")) {
                            description = eventText;
                        } else if (tagName.equalsIgnoreCase("encoded")) { // "content:encoded"
                            content = eventText;
                            //TODO(jasonscott) Parse content for image URL
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            //TODO(jasonscott) Handle XmlPullParserException
        } catch (IOException e) {
            //TODO(jasonscott) Handle IOException accordingly
        } catch (ParseException e) {
            //TODO(jasonscott) Handle ParseException accordingly
        }
        return count;
    }

    /**
     * Calls insertEntry method of PocketDbHelper to effectively
     * add an article to the database.
     *
     * @param entry   - Entry to be inserted.
     * @param context - Applications Context to access database
     * @param section - String for the specific section RSS feed.
     * @return long for row ID of inserted Entry or -1 if error occurred
     */
    private static long addEntry(Entry entry, Context context, String section) {
        return PocketDbHelper.getInstance(context).insertEntry(entry, section);

    }

}
