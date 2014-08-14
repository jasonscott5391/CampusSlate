/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate.utils;

import edu.nyit.campusslate.Entry;
import edu.nyit.campusslate.exceptions.PocketBuildException;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static int parse(InputStream in, Context context, String section, long lastRefresh) throws PocketBuildException {
        PocketDbHelper dbHelper = PocketDbHelper.getInstance(context);
        Entry entry = dbHelper.retrieveEntry(section, "1");
        long limit = 0L;
        if (entry != null) {
            limit = Long.valueOf(entry.publicationDate);

        }
        ArrayList<Entry> entries = new ArrayList<Entry>();

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

            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat(PocketReaderContract.SlateEntry.PATTERN);

            parser = factory.newPullParser();
            parser.setInput(in, null);

            String eventText = null;
            String tagName;
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.TEXT:
                        eventText = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(tagName.equals("lastBuildDate")) {
                            long build = simpleDateFormat.parse(eventText).getTime();
                            if (build < lastRefresh) {
                                throw new PocketBuildException("Build");
                            }
                        }
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

                            entries.add(entry);
                        } else if (tagName.equalsIgnoreCase("title")) {
                            title = eventText;
                        } else if (tagName.equalsIgnoreCase("link")) {
                            link = eventText;
                        } else if (tagName.equalsIgnoreCase("pubDate")) {
                            if (eventText != null) {
                                Date date = simpleDateFormat.parse(eventText);
                                long current = date.getTime();
                                if(current == limit) {
                                    throw new PocketBuildException("Limit");
                                }
                                pubDate = String.valueOf(date.getTime());
                            }
                        } else if (tagName.equalsIgnoreCase("creator")) {
                            creator = eventText;
                        } else if (tagName.equalsIgnoreCase("category")) {
                            category = eventText;
                        } else if (tagName.equalsIgnoreCase("description")) {
                            description = eventText;
                        } else if (tagName.equalsIgnoreCase("encoded")) { // "content:encoded"
                            String regexImage = "(?<=<img src=\")[^\"]*.jpg|(?<=<img src=\")[^\"]*.png|(?<=<img src=\")[^\"]*.jpeg|(?<=<img src=\")[^\"]*.gif|(?<=<img src=\")[^\"]*.bmp";
                            Pattern pattern = Pattern.compile(regexImage);
                            Matcher matcher = pattern.matcher(eventText);
                            while (matcher.find()) {
                                imageUrl = matcher.group();
                            }
                            content = eventText.replaceAll("\\<.*?>", "");
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
        return dbHelper.insertEntries(entries, section);
    }

}
