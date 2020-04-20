package org.me.gcu.trafficscotlandapp;

/**
 * Christopher Conlan
 * Created On: 14/04/2020
 * Student No: S1512271
 * Mobile Platform Development Coursework
 */

public class ItemModel {

    //Initialise variables.

    public String title;
    public String description;
    public String link;


    //List of Strings into model.

    public ItemModel(String title, String link, String description) {
        this.title = title;
        this.description = description;
        this.link = link;
    }

    //Create Setters and Getters for list items.

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}