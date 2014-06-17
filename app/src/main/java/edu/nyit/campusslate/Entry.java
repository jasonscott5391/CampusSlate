/**
 * Copyright (C) 2014 Jason Scott
 */
package edu.nyit.campusslate;
/**
 * <p>Title: Entry.</p>
 * @author jasonscott
 *
 */
public class Entry {

	public String id;
	public String title;
	public String link;
	public String publicationDate;
	public String creator;
	public String category;
	public String description;
	public String content;
	public String imageUrl;
	public String bookmarked;

    /**
     * Constructs an entry for the database.  Can be an
     * article, event, or staff member.
     * @param id
     * @param title
     * @param link
     * @param publicationDate
     * @param creator
     * @param category
     * @param description
     * @param content
     * @param imageUrl
     * @param bookmarked
     */
	public Entry(String id,
                 String title,
                 String link,
                 String publicationDate,
                 String creator,
                 String category,
                 String description,
                 String content,
                 String imageUrl,
                 String bookmarked) {
		this.id = id;
		this.title = title;
		this.link = link;
		this.publicationDate = publicationDate;
		this.creator = creator;
		this.category = category;
		this.description = description;
		this.content = content;
		this.imageUrl = imageUrl;
		this.bookmarked = bookmarked;

	}

	/**
	 * For testing.
	 */
	public String toString() {
		return 	"Id: " + id +
				"\nTitle: " + title +
				"\nLink: " + link +
				"\nPublication Date: " + publicationDate +
				"\nCreator: " + creator +
				"\nCategory: " + category +
				"\nDescription: " + description +
				"\nContent: " + content +
				"\nImage URL: " + imageUrl +
				"\nBookmaked: " + bookmarked;
	}
}
