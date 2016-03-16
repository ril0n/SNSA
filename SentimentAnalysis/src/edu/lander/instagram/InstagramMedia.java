package edu.lander.instagram;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Devin Tinsley on 11/24/2015.
 * Here I will create a class that gathers all of the needed json for Rob's sentiment analysis
 */


public class InstagramMedia {

    String m_sCaption, m_sMediaType;
    ArrayList<String> m_alComments = new ArrayList<>();
    ArrayList<MediaImage> m_mediaImage = new ArrayList<>();
    ArrayList<String> m_alHashtags = new ArrayList<>();
    int m_iLikesCount;
    Location m_location;


    public InstagramMedia() {

    }

    public Object getCaption(JSONObject jsonObject) throws JSONException {
         Object oCaption  = new Object();
         JSONObject json = new JSONObject();
        try{
        json = (JSONObject)jsonObject.get("caption");
        oCaption = json.get("text");
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return oCaption;
    }

    public Object getMediaType(JSONObject jsonObject) throws JSONException {
        Object oMediaType = jsonObject.get("type");
        return oMediaType;
    }

    public Object getComments(JSONObject jsonObject) throws JSONException {
        Object oComments = jsonObject.get("comments");
        return oComments;
    }

    public Object getHashtags (JSONObject jsonObject) throws JSONException {
        Object oHashtags = jsonObject.get("tags");
        return oHashtags;
    }

    public Object getLikesCount(JSONObject jsonObject) throws JSONException {
        Object oLikesCount = jsonObject.get("likes");
        return oLikesCount;
    }

    public static ArrayList<InstagramMedia> parseJSON(String sJSON) throws JSONException {
        JSONObject jsonObject = new JSONObject(sJSON);
        ArrayList<InstagramMedia> alInstaMedia = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            InstagramMedia instagramMedia = new InstagramMedia();
            //lastTimeStamp = obj.getString("created_time");
            //call set methods...create get/set methods first
            //Methods: getCaption, getMediaType, getComments, getMediaImage, getHashtags, getLikesCount, getLocation

            alInstaMedia.add(instagramMedia);
        }

        return alInstaMedia;
    }
}

class Location {
    double m_dLat, m_dLng;
    String m_sName;

    public Object getLocation (JSONObject jsonObject) throws JSONException {
        Object oLocation = jsonObject.get("location");
        return oLocation;
    }
}

class MediaImage {
    String m_sUrl;
    int m_iWidth, m_iHeight;
}
