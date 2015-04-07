/**
 * Copyright (C) 2014 Jason Scott
 */
package com.nyit.campusslate.utils;

import com.nyit.campusslate.data.PocketDbHelper;
import com.nyit.campusslate.data.PocketReaderContract;
import com.nyit.campusslate.normalized.Entry;
import com.nyit.campusslate.exceptions.PocketBuildException;

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
 * <p>Title: PocketXmlParser.</p>
 * <p>Description: Custom XML parser for consuming RSS
 * feed from Campus Slate.</p>
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
            throws PocketBuildException {

        PocketDbHelper dbHelper = PocketDbHelper.getInstance(context);

        // Get the first entry from the database
        Entry entry = dbHelper.retrieveEntry(section, "1");

        // Set the date limit for when to stop.
        long limit = 0L;
        if (entry != null) {
            limit = entry.getPublicationDate();

        }

        ArrayList<Entry> entries = new ArrayList<Entry>();

        XmlPullParserFactory factory;
        XmlPullParser parser;


        String title = null;
        String link = null;
        long pubDate = 0L;
        String creator = null;
        String category = null;
        String description = null;
        String content = null;
        String imageUrl = null;
        int bookmarked = 0;

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
                                throw new PocketBuildException(String.format("LastBuildDate: %s, LastRefresh: %s", new Date(build), new Date(lastRefresh)));
                            }
                        }
                        if (tagName.equalsIgnoreCase("item")) {

                            entry = new Entry();
                            entry.setTitle(title);
                            entry.setLink(link);
                            entry.setPublicationDate(pubDate);
                            entry.setCreator(creator);
                            entry.setCategory(category);
                            entry.setDescription(description);
                            entry.setContent(content);
                            entry.setImageUrl(imageUrl);
                            entry.setBookmarked(bookmarked);

                            entries.add(entry);
                        } else if (tagName.equalsIgnoreCase("title")) {
                            title = eventText;
                        } else if (tagName.equalsIgnoreCase("link")) {
                            link = eventText;
                        } else if (tagName.equalsIgnoreCase("pubDate")) {
                            if (eventText != null) {
                                Date date = simpleDateFormat.parse(eventText);
                                long current = date.getTime();
                                // The current article date is equal to the latest.
                                if (current == limit) {
                                    break parse;
                                }
                                pubDate = date.getTime();
                            }
                        } else if (tagName.equalsIgnoreCase("creator")) {
                            creator = eventText;
                        } else if (tagName.equalsIgnoreCase("category")) {
                            category = eventText;
                        } else if (tagName.equalsIgnoreCase("description")) {
                            description = eventText;
                        } else if (tagName
                                .equalsIgnoreCase("encoded")) {
                            String regexImage = "(?<=<img src=\")[^\"]*.jpg|"
                                    + "(?<=<img src=\")[^\"]*.png|"
                                    + "(?<=<img src=\")[^\"]*.jpeg|"
                                    //TODO (jasonscott) Check for missing image extensions.
                                    + "(?<=<img src=\")[^\"]*.jpg|"
                                    + "(?<=<img src=\")[^\"]*.gif|"
                                    + "(?<=<img src=\")[^\"]*.bmp";
                            Pattern pattern = Pattern.compile(regexImage);
                            Matcher matcher = pattern.matcher(eventText);
                            while (matcher.find()) {
                                imageUrl = matcher.group();
                            }
                            content = eventText.replaceAll("\\<.*?>", "");
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dbHelper.insertEntries(entries, section);
    }

}
