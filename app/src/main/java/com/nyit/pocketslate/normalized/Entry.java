/**
 * Copyright (C) 2014 Jason Scott
 */
package com.nyit.pocketslate.normalized;

import java.io.Serializable;

/**
 * <p>Entry.java</p>
 * <p><t>An Entry represents a record published in the Campus Slate.</t></p>
 *
 * @author jasonscott
 */
public class Entry implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String link;
    private long publicationDate;
    private String creator;
    private String category;
    private String description;
    private String content;
    private String imageUrl;

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

    /**
     * For testing.
     */
    public String toString() {
        return "Id: " + id +
                "\nTitle: " + title +
                "\nLink: " + link +
                "\nPublication Date: " + publicationDate +
                "\nCreator: " + creator +
                "\nCategory: " + category +
                "\nDescription: " + description +
                "\nContent: " + content +
                "\nImage URL: " + imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entry entry = (Entry) o;

        if (id != entry.id) return false;
        if (publicationDate != entry.publicationDate) return false;
        if (title != null ? !title.equals(entry.title) : entry.title != null) return false;
        if (link != null ? !link.equals(entry.link) : entry.link != null) return false;
        if (creator != null ? !creator.equals(entry.creator) : entry.creator != null) return false;
        if (category != null ? !category.equals(entry.category) : entry.category != null)
            return false;
        if (description != null ? !description.equals(entry.description) : entry.description != null)
            return false;
        return !(content != null ? !content.equals(entry.content) : entry.content != null) && !(imageUrl != null ? !imageUrl.equals(entry.imageUrl) : entry.imageUrl != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (int) (publicationDate ^ (publicationDate >>> 32));
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        return result;
    }
}
