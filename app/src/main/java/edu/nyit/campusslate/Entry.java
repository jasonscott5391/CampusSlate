/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate;
/**
 * <p>Title: Entry.java</p>
 * @author jasonscott
 *
 */
public class Entry {

	public String id;
	public String title;
	public String link;
	public String pubDate;
	public String creator;
	public String category;
	public String description;
	public String content;
	public String imageUrl;
	public String bookmarked;

	public Entry(String i, String t, String l, String p, String cre, String cat, String d, String con, String iUrl, String b) {
		id = i;
		title = t;
		link = l;
		pubDate = p;
		creator = cre;
		category = cat;
		description = d;
		content = con;
		imageUrl = iUrl;
		bookmarked = b;

	}

	/**
	 * For testing.
	 */
	public String toString() {
		return 	"Id: " + id +
				"\nTitle: " + title +
				"\nLink: " + link +
				"\nPublication Date: " + pubDate +
				"\nCreator: " + creator +
				"\nCategory: " + category +
				"\nDescription: " + description +
				"\nContent: " + content +
				"\nImage URL: " + imageUrl +
				"\nBookmaked: " + bookmarked;
	}
}
