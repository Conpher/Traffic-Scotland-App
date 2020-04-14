package org.me.gcu.trafficscotlandapp;

/**
 * Christopher Conlan
 * Created On: 14/04/2020
 * Student No: S1512271
 * Mobile Platform Development Coursework
 */

public class RssFeedModel {

    public String title;
    public String description;
    public String link;

    public RssFeedModel(String title, String link, String description) {
        this.title = title;
        this.description = description;
        this.link = link;
    }
}