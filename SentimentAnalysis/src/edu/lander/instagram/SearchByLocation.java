package edu.lander.instagram;
import com.mongodb.*;
import com.mongodb.util.JSON;
import org.jinstagram.Instagram;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Devin Tinsley on 11/10/2015.
 */
public class SearchByLocation {

    Vector<JSONObject> m_vsData = new Vector<>();
    String lastTimeStamp;
    DBCollection collection;
    Instagram insta;
    int numOfPosts = 0;
    public static final int LAT = 0;
    public static final int LNG = 1;
    
    public Vector<JSONObject> getData()
    {
        return m_vsData;
    }

    public SearchByLocation()
    {
        
    }
    public  SearchByLocation(DBCollection db_Collection, Instagram instagram) {
        setCollection(db_Collection);
        setInstagram(instagram);
    }

    public void setCollection(DBCollection db_collection) {
        collection = db_collection;
    }

    public void setInstagram(Instagram instagram){
        insta = instagram;
    }

    public double[] getCoordsFromUserInput (String uInput) {
        //https://maps.googleapis.com/maps/api/geocode/output?parameters
        double[] latLng = new double[2];
        String address = uInput.replace(' ', '+');
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address;
        try {
            URL urlObject = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
            conn.setUseCaches(false);
            System.out.println("Connecting: " + url);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(urlObject.openStream()))) {
                String inputLine = "";
                String temp = "";
                while((inputLine = in.readLine()) != null){
                    //store json to mongodb
                    temp += inputLine.replaceAll(" ", "").trim();
                }
                DBObject latLngJson = (DBObject) JSON.parse(temp);
                // build a JSON object
                JSONObject obj = new JSONObject(temp);

                // get the first result
                JSONObject res = obj.getJSONArray("results").getJSONObject(0);
                System.out.println("Queried location: " + res.getString("formatted_address"));
                JSONObject loc = res.getJSONObject("geometry").getJSONObject("location");
                System.out.println("lat: " + loc.getDouble("lat") + ", lng: " + loc.getDouble("lng"));
                latLng[LAT] = loc.getDouble("lat");
                latLng[LNG] = loc.getDouble("lng");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return latLng;
    }

    /*public void SearchByCoords(double latitude, double longitude, long max_timestamp){
        if(insta == null){
            System.out.println("Instagram is not connected. Call connectInstagram to connect.");
            return;
        }
        try {

            String url = "https://api.instagram.com/v1/media/search?lat=" + latitude +
                    "&lng=" + longitude +
                    "&max_timestamp=" + max_timestamp +
                    "&access_token=" + parseAccessToken(insta.getAccessToken().toString());
            URL urlObject = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
            conn.setUseCaches(false);
            System.out.println("Connecting: " + url);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(urlObject.openStream()))) {
                String inputLine;
                String sOutput = "";
                DBObject dbObject;
                inputLine = in.readLine();
                //while((inputLine = in.readLine()) != null){
                System.out.println(inputLine);
                JSONObject jsonObject = new JSONObject(inputLine);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    lastTimeStamp = obj.getString("created_time");
                }
                //store to m_vsData
                m_vsData.add(inputLine);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void searchByLocation(double latitude, double longitude, long max_timestamp){
        if(insta == null){
            System.out.println("Instagram is not connected. Call connectInstagram to connect.");
            return;
        }
        try { //get individual Instagram posts
            String url = "https://api.instagram.com/v1/media/search?lat=" + latitude +
                    "&lng=" + longitude +
                    "&max_timestamp=" + max_timestamp +
                    "&access_token=" + parseAccessToken(insta.getAccessToken().toString());
            URL urlObject = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
            conn.setUseCaches(false);
            System.out.println("Connecting: " + url);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(urlObject.openStream()))) {
                String inputLine;
                String sOutput = "";
                DBObject dbObject;
                inputLine = in.readLine();
                //while((inputLine = in.readLine()) != null){
                System.out.println(inputLine);

                JSONObject jsonObject = new JSONObject(inputLine);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for(int i = 0; i < 10 /*Making this less than 10, then it should only return 10 posts jsonArray.length()*/; i++){
                    //loop through individual posts
                    //if numOfPosts < 20 then do this
                    JSONObject obj = jsonArray.getJSONObject(i);
                    sOutput = obj.toString();
                    System.out.println(sOutput);
                    m_vsData.add(obj);
                    //if numOfPost > 20 then do something else
                }
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    lastTimeStamp = obj.getString("created_time");
                }
                //store to m_vsData
                //m_vsData.add(inputLine);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parseAccessToken(String accessToken) {
        String parsedAccessToken = accessToken.substring(accessToken.indexOf('[') + 1,
                accessToken.indexOf(',') - 1);
        return parsedAccessToken;
    }

    public Long lastTimestamp(){
        BasicDBObject getQuery = new BasicDBObject();
        BasicDBObject fields = new BasicDBObject();
        fields.put("data.created_time", 1);
        fields.put("_id", 0);
        //Here the json is parsed to get the last timestamp from the set of data pulled from instagram api
        if (collection!= null) {
            DBCursor cursor = collection.find(getQuery, fields);
            BasicDBObject groupFields = new BasicDBObject("_id", "$data");
            DBObject projectedFields = new BasicDBObject("_id", 0);
            projectedFields.put("data.created_time", 1);
            String created_time = null;
            String temp = null;
            DBObject project = new BasicDBObject("$project", projectedFields);
            DBObject unwind = new BasicDBObject("$unwind", "$data");
            AggregationOutput aggregate;
            aggregate = collection.aggregate(unwind,project);
            for(Object o : aggregate.results()){
                created_time = o.toString();
                //example of what is stored in temp
                //{ "data" : { "created_time" : "1445571411"}}
                temp = created_time;
            }
            //what should look like after trimmed and tightened up
            ////{"data":{"created_time":"1445571411"}}
            temp = temp.trim().replaceAll(" ", "");
            int lastNum = temp.length();
            lastNum = lastNum - 3;
            temp = temp.substring(25, lastNum);
            Long time_stamp = Long.parseLong(temp);
            return time_stamp;
        }
        else {
            System.out.println("Collection is null");
            return null;
        }
    }

    public long getLastTimestamp() {
        return Long.parseLong(lastTimeStamp);
    }
}