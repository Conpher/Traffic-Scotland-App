package org.me.gcu.trafficscotlandapp.Enum;

/**
 * Christopher Conlan
 * Created On: 17/04/2020
 * Student No: S1512271
 * Mobile Platform Development Coursework
 */

//Store all RSS Feed Urls as an enum.
public enum SourceUrl {
    CURRENT_INCIDENTS("https://trafficscotland.org/rss/feeds/currentincidents.aspx",0),
    ROADWORKS("https://trafficscotland.org/rss/feeds/roadworks.aspx",1),
    PLANNED_ROADWORKS("https://trafficscotland.org/rss/feeds/plannedroadworks.aspx",2);

    private String stringValue;
    private int intValue;
    SourceUrl(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }
    //Convert URL to a string.
    @Override
    public String toString(){
        return stringValue;
    }

}
