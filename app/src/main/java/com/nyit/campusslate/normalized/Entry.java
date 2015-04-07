/**
 * Copyright (C) 2014 Jason Scott
 */
package com.nyit.campusslate.normalized;
/**
 * <p>Title: Entry.</p>
 * @author jasonscott
 *
 */
public class Entry {

	private int id;
	private String title;
	private String link;
	private long publicationDate;
	private String creator;
	private String category;
	private String description;
	private String content;
	private String imageUrl;
	private int bookmarked;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public long getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(long publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getBookmarked() {
		return bookmarked;
	}

	public void setBookmarked(int bookmarked) {
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
