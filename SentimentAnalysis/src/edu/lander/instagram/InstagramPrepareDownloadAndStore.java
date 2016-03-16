package edu.lander.instagram;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//import InstaConnect.InstaConnect;

import com.mongodb.*;
import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 *
 * @author Devin Tinsley
 */

public class InstagramPrepareDownloadAndStore extends Thread {

    private WebDriver m_driver = new HtmlUnitDriver();
    //Mongo mongo = new Mongo(getMongoDBURL(), getMongoDBPort());
    DB db;
    DBCollection collection;
    String mongoDBURL;
    int mongoDBPort;
    String mongoDBName;
    String mongoDBCollection;
    InstaConnect instaConnect;
    Instagram insta;
    int frequency = 0;
    long intervalMin = 0;

    public InstagramPrepareDownloadAndStore() {
        frequency = 0;
        intervalMin = 0;
    }

    /**
     * Create a connection with all data fields entered at this time
     *
     * @param newInstaConnect
     * @param newInstagram
     * @param newFrequency
     * @param newIntervalMin
     */
    @SuppressWarnings("JavaDoc")
    public InstagramPrepareDownloadAndStore(InstaConnect newInstaConnect, Instagram newInstagram, int newFrequency,
                                            long newIntervalMin) {
        instaConnect = newInstaConnect;
        insta = newInstagram;

        frequency = newFrequency;
        intervalMin = newIntervalMin;
    }

//    /**
//     * set current MongoDB URL
//     *
//     * @param newMongoDBURL: get the MongoDB URL from MongoDB Administration
//     */
//    public void setMongoDBURL(String newMongoDBURL) {
//        mongoDBURL = newMongoDBURL;
//    }
//
//    /**
//     * get current MongoDB URL that is set
//     *
//     * @return
//     */
//
//    public String getMongoDBURL() {
//        return mongoDBURL;
//    }
//
//    /**
//     * set current MongoDB URL Number
//     *
//     * @param EnterMongoDBURLNO: get the MongoDB Port from MongoDB Administration
//     */
//    public void setMongoDBPort(int EnterMongoDBURLNO) {
//        mongoDBPort = EnterMongoDBURLNO;
//    }
//
//    /**
//     * get current MongoDB URL Number that is set
//     *
//     * @return
//     */
//    public int getMongoDBPort() {
//        return mongoDBPort;
//    }
//
//    /**
//     * set current MongoDB Name
//     *
//     * @param EnterMongoDBName: define a new MongoDB name for storing Instagram user data
//     */
//    public void setMongoDBName(String EnterMongoDBName) {
//        mongoDBName = EnterMongoDBName;
//    }
//
//    /**
//     * get current MongoDB Name that is set
//     *
//     * @return
//     */
//    public String getMongoDBName() {
//        return mongoDBName;
//    }
//
//    /**
//     * set current MongoDB Collection name
//     *
//     * @param EnterMongoDBCollecion: : define a new MongoDB collection name
//     */
//    public void setMongoDBCollection(String EnterMongoDBCollecion) {
//        mongoDBCollection = EnterMongoDBCollecion;
//    }
//
//    /**
//     * get current MongoDB Collection name that is set
//     *
//     * @return
//     */
//    public String getMongoDBCollection() {
//        return mongoDBCollection;
//    }
//    /**
//     * get MongoDB connected
//     */
//    public void connectMongoDB() {
//        try {
//            db = mongo.getDB(getMongoDBName());
//            collection = db.getCollection(getMongoDBCollection());
//
//        } catch (MongoException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * set current frequency
     *
     * @param EnterFrequency: should be a number to download Instagram data
     */
    public void setFrequency(int EnterFrequency) {
        frequency = EnterFrequency;
    }

    /**
     * set current interval minute
     *
     * @param EnterIntervalMin: should be a interval time
     */
    public void setIntervalMin(long EnterIntervalMin) {
        intervalMin = EnterIntervalMin;
    }

    /**
     * get current frequency that is set
     *
     * @return
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * get current interval minute that is set
     *
     * @return
     */
    public long getIntervalMin() {
        return intervalMin;
    }

    /**
     * prepare for downloading Instagram users' data, set up download Frequency and IntervalMin, connect to m_MongoDB
     *
     * @param newFrequency
     * @param newIntervalMin
     */
    public void prepareDownloadInsta(int newFrequency, long newIntervalMin) {
        setFrequency(newFrequency);
        setIntervalMin(newIntervalMin);
    }

    /**
     * get Instagram connected
     */
    public void connectInstagram() {
        try {
            Token accessToken = instaConnect.AuthToken();
            System.out.println("Access token is: " + accessToken);
            insta = new Instagram(accessToken);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}


