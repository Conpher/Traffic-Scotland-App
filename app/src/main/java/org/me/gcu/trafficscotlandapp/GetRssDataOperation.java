package org.me.gcu.trafficscotlandapp;

/**
 * Christopher Conlan
 * Created On: 20/04/2020
 * Student No: S1512271
 * Mobile Platform Development Coursework
 */

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GetRssDataOperation {

    private String S1512271_StudentNo;

    //Parse List Item Model using XMLPullParser. Done using parseFeed method.
    public List<ItemModel> parseFeed(InputStream inputStream) throws XmlPullParserException, IOException {
        String title = null;
        String link = null;
        String description = null;
        boolean isItem = false;
        List<ItemModel> items = new ArrayList<>();

        try {
            //Filter through TAGs to retrieve specific items from the RSS feed.
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MainActivity", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                //Get title.
                if (name.equalsIgnoreCase("title")) {
                    title = result;

                    //Get description and remove html break tags from result.
                } else if (name.equalsIgnoreCase("description")) {
                    String updatedResult = result.replaceAll("<br />", "\n");
                    description = updatedResult;

                    //Get link.
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                }

                //If items in the list != null then add item.
                if (title != null && link != null && description != null) {
                    if(isItem) {
                        ItemModel item = new ItemModel(title, link, description);
                        items.add(item);
                    }

                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }
            //If items size is greater than 0 remove first line of list.
            if (items.size() > 0) {
                items.remove(0);
            }

            //Return items from the list.
            return items;

        } finally {
            inputStream.close();
        }
    }
}
