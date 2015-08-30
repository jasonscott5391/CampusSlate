/**
 * Copyright (C) 2014 Jason Scott
 */
package com.nyit.pocketslate.utils;

import com.nyit.pocketslate.data.PocketDbHelper;
import com.nyit.pocketslate.data.PocketReaderContract;
import com.nyit.pocketslate.normalized.Entry;
import com.nyit.pocketslate.exceptions.PocketSlateException;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>PocketXmlParser.java</p>
 * <p><t>Custom XML parser for consuming RSS
 * feed from Campus Slate.</t></p>
 *
 * @author jasonscott
 */
public class PocketXmlParser {

    /**
     * Parses an InputStream from the Campus Slate web site and
     * calls addEntry method when an article is parsed.
     *
     * @param in      - InputStream to be parsed.
     * @param context - Application Context needed to access database in
     *                addEntry method.
     * @param section - String for the specific section RSS feed.
     * @return boolean for the success or failure of parsing
     */
    public static int parse(
            InputStream in,
            Context context,
            String section,
            long lastRefresh)
            throws PocketSlateException {

        PocketDbHelper dbHelper = PocketDbHelper.getInstance(context);

        // Get the first entry from the database
        Entry entry = dbHelper.retrieveEntry(section, "1");

        // Set the date limit for when to stop.
        long limit = 0L;
        if (entry != null) {
            limit = entry.getPublicationDate();

        }

        ArrayList<Entry> entries = new ArrayList<>();

        XmlPullParserFactory factory;
        XmlPullParser parser;


        entry = new Entry();

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat(
                            PocketReaderContract.SlateEntry.PATTERN, Locale.US);

            parser = factory.newPullParser();
            parser.setInput(in, null);

            String eventText = null;
            String tagName;
            int eventType = parser.getEventType();

            parse:
            while (eventType != XmlPullParser.END_DOCUMENT) {
                tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.TEXT:
                        eventText = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagName.equals("lastBuildDate")) {
                            long build =
                                    simpleDateFormat.parse(eventText).getTime();
                            if (build < lastRefresh) {
                                throw new PocketSlateException(String.format("LastBuildDate: %s, LastRefresh: %s", new Date(build), new Date(lastRefresh)));
                            }
                        }
                        if (tagName.equalsIgnoreCase("item")) {
                            entries.add(entry);
                            entry = new Entry();
                        } else if (tagName.equalsIgnoreCase("title")) {
                            entry.setTitle(eventText);
                        } else if (tagName.equalsIgnoreCase("link")) {
                            entry.setLink(eventText);
                        } else if (tagName.equalsIgnoreCase("pubDate")) {
                            if (eventText != null) {
                                Date date = simpleDateFormat.parse(eventText);
                                long current = date.getTime();
                                // The current article date is equal to the latest.
                                if (current == limit) {
                                    break parse;
                                }
                                entry.setPublicationDate(date.getTime());
                            }
                        } else if (tagName.equalsIgnoreCase("creator")) {
                            entry.setCreator(eventText);
                        } else if (tagName.equalsIgnoreCase("category")) {
                            entry.setCategory(eventText);
                        } else if (tagName.equalsIgnoreCase("description")) {
                            entry.setDescription(eventText);
                        } else if (tagName
                                .equalsIgnoreCase("encoded")) {
                            String regexImage = "(?<=<img src=\")[^\"]*.jpg|"
                                    + "(?<=<img src=\")[^\"]*.png|"
                                    + "(?<=<img src=\")[^\"]*.jpeg|"
                                    + "(?<=<img src=\")[^\"]*.jpg|"
                                    + "(?<=<img src=\")[^\"]*.gif|"
                                    + "(?<=<img src=\")[^\"]*.bmp";
                            Pattern pattern = Pattern.compile(regexImage);
                            Matcher matcher = null;

                            if (eventText != null) {
                                matcher = pattern.matcher(eventText);
                            }

                            if (matcher != null) {

                                if (matcher.find()) {
                                    entry.setImageUrl(matcher.group());
                                } else {
                                    entry.setImageUrl("");

                                }
                            }

                            if (eventText != null) {
                                entry.setContent(eventText.replaceAll("<(?>/?)(?:[^pP]|[pP][^\\s>/])[^>]*>", ""));
                            }
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException | ParseException e) {
            e.printStackTrace();
        }

        return dbHelper.insertEntries(entries, section);
    }

}
