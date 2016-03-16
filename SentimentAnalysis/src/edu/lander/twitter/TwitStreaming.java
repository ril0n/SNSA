/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.lander.twitter;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import com.mongodb.DBCursor;
import java.util.regex.Pattern;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
/**
 *
 * @author Administrator
 */

public class TwitStreaming {

    Mongo mongo;
    DB db;
    DBCollection collection;
    String consumerKey;
    String consumerSecret;
    String accessToken;
    String accessTokenSecret;
    String mongoDBURL;
    int mongoDBPort;
    String mongoDBName;
    String mongoDBCollecion;
    long nTweetDownloadLimit;
    long timeTweetDownloadLimit;
    static final int TIME_LIMIT = 0, NUMBER_LIMIT = 1;   
    int type = TIME_LIMIT; 
    TweetDownloadListener listener = null;
    TwitterStream twitterStream = null;
    

    public int getType(){
        return type;
    }
    
    
    
    public void setConsumerKey(String EnterConsumerKey){
        consumerKey = EnterConsumerKey;
    }
    
    public String getConsumerKey(){
        return consumerKey;
    }
    
    public void setConsumerSecret(String EnterConsumerSecret){
        consumerSecret = EnterConsumerSecret;
    }
    
    public String getConsumerSecret(){
        return consumerSecret;
    }
    
    public void setAccessToken(String EnterAccessToken){
        accessToken = EnterAccessToken;
    }
    
    public String getAccessToken(){
        return accessToken;
    }
    
    public void setAccessTokenSecret(String EnterAccessTokenSecret){
        accessTokenSecret = EnterAccessTokenSecret;
    }   
    
    public String getAccessTokenSecret(){
        return accessTokenSecret;
    }
    
    public void setMongoDBURL(String newMongoDBURL, int newMongoDBPort){
        mongoDBURL = newMongoDBURL;
        mongoDBPort = newMongoDBPort;
    }
    
    public String getMongoDBURL(){
        return mongoDBURL;
    }
    
    public void setMongoDBPort(int EnterMongoDBURLNO){
        mongoDBPort = EnterMongoDBURLNO;
    }
    
    public int getMongoDBPort(){
        return mongoDBPort;
    }
    
    public void setMongoDBName(String EnterMongoDBName){
        mongoDBName = EnterMongoDBName;
    }
    
    public String getMongoDBName(){
        return mongoDBName;
    }
    
    public void setMongoDBCollecion(String EnterMongoDBCollecion){
        mongoDBCollecion = EnterMongoDBCollecion;
    }
    
    public String getMongoDBCollecion(){
        return mongoDBCollecion;
    }
    
    public void setsampleAndStoreByNumber(int EnterNumber){
        nTweetDownloadLimit = EnterNumber;
    }
    
    public long getsampleAndStoreByNumber(){
        return nTweetDownloadLimit;
    }
    
    public void setsampleAndStoreByMilisec(long EnterMilisec){
        timeTweetDownloadLimit = EnterMilisec;
    }
    
    public long getsampleAndStoreByMilisec(){
        return timeTweetDownloadLimit;
    }
    
    public void connectMongoDB(){
        try {
            mongo = new Mongo(getMongoDBURL(), getMongoDBPort());
            db = mongo.getDB(getMongoDBName());
            collection = db.getCollection(getMongoDBCollecion());
 } catch (MongoException e) {
                    e.printStackTrace();
        }
    }
    
    public void prepareDownloadTweets(int newType, long limit){
        setType(newType);
        if(getType() == TIME_LIMIT)
        {
            timeTweetDownloadLimit = limit;
        } else if(getType() == NUMBER_LIMIT){
            nTweetDownloadLimit = limit;
        }
        
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey(getConsumerKey())
          .setOAuthConsumerSecret(getConsumerSecret())
          .setOAuthAccessToken(getAccessToken())
          .setOAuthAccessTokenSecret(getAccessTokenSecret())
          .setJSONStoreEnabled(true);
        connectMongoDB();
        listener = new TweetDownloadListener(this);
        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        twitterStream.addListener(listener);
    }
    
    public void downloadStoreTweets(){
        listener.initialize();
        twitterStream.sample();  
    }
    
    public void setType(int newType){
        if(newType == TIME_LIMIT || newType == NUMBER_LIMIT)
            type = newType;
    }
    
    public DBObject selectFirstRecordInCollection(DBCollection collection) {
        DBObject dbObject = collection.findOne();
        return dbObject;
    }
    
    public DBCursor findLocations(){
        BasicDBObject getQuery = new BasicDBObject();
        BasicDBObject fields = new BasicDBObject();
        fields.put("user.name", 1);
        fields.put("user.location", 1);
        DBCursor cursor = collection.find(getQuery, fields);
        return cursor;
    }
    public DBCursor findLocationsFull(){
        BasicDBObject getQuery = new BasicDBObject();
        BasicDBObject fields = new BasicDBObject();
        fields.put("user.name", 1);
        fields.put("user.location", 1);
        DBCursor cursor = collection.find(getQuery, fields);
        return cursor;
    }
    
    public DBCursor findLocations(BasicDBObject fields){
        BasicDBObject getQuery = new BasicDBObject();
        DBCursor cursor = collection.find(getQuery, fields);
        return cursor;
    }
    
    public DBCursor selectTweetsFromStateInUS(String sState)
    {
        Pattern p = Pattern.compile(sState+"$");
        BasicDBObject query = new BasicDBObject("place.full_name", p);
        BasicDBObject fields = new BasicDBObject();
        fields.put("user.name", 1);
        fields.put("place.country_code", 1);
        fields.put("place.country", 1);
        fields.put("place.name", 1);
        fields.put("place.full_name", 1);
        fields.put("entities.hashtags", 1);
        fields.put("text", 1);
        return collection.find(query,fields);
    }
    
    public DBCursor selectTweetsFromStateInUSFull(String sState)
    {
        Pattern p = Pattern.compile(sState+"$");
        BasicDBObject query = new BasicDBObject("place.full_name", p);
        return collection.find(query);
    }
    
    public DBCursor selectTweetsWithWordsInText(String sWords){
        Pattern p = Pattern.compile(".*"+sWords+".*");
        BasicDBObject query = new BasicDBObject("text", p);

        BasicDBObject fields = new BasicDBObject();
        fields.put("user.name", 1);
        fields.put("place.country_code", 1);
        fields.put("place.country", 1);
        fields.put("place.name", 1);
        fields.put("place.full_name", 1);
        fields.put("entities.hashtags", 1);
        fields.put("text", 1);
        return collection.find(query, fields);
    }
    
    public DBCursor selectTweetsWithWordsInTextFull(String sWords){
        Pattern p = Pattern.compile(".*"+sWords+".*");
        BasicDBObject query = new BasicDBObject("text", p);
        return collection.find(query);
    }
    
    public DBCursor selectTweetsFromCountryCode(String countryCode){
        BasicDBObject getQuery = new BasicDBObject();
        getQuery.put("place.country_code", countryCode);
        BasicDBObject fields = new BasicDBObject();
        fields.put("user.name", 1);
        fields.put("place.country_code", 1);
        fields.put("place.country", 1);
        fields.put("place.name", 1);
        fields.put("place.full_name", 1);
        fields.put("entities.hashtags", 1);
        fields.put("text", 1);
        return collection.find(getQuery, fields);
    }
    
    public DBCursor selectTweetsFromCountryCodeFull(String countryCode){
        BasicDBObject getQuery = new BasicDBObject();
        getQuery.put("place.country_code", countryCode);
        return collection.find(getQuery);
    }
    
    public DBCursor selectTweetsFromCountry(String country){
        Pattern p = Pattern.compile(".*"+country+".*");
        BasicDBObject query = new BasicDBObject("place.country", p);
        BasicDBObject fields = new BasicDBObject();
        fields.put("user.name", 1);
        fields.put("place.country_code", 1);
        fields.put("place.country", 1);
        fields.put("place.name", 1);
        fields.put("place.full_name", 1);
        fields.put("entities.hashtags", 1);
        fields.put("text", 1);
        return collection.find(query, fields);
    }
    
    public DBCursor selectTweetsFromCountryFull(String country){
        Pattern p = Pattern.compile(".*"+country+".*");
        BasicDBObject query = new BasicDBObject("place.country", p);
        return collection.find(query);
    }
    
    public DBCursor selectTweetsFromCountry(BasicDBObject fields, String countryCode){
        BasicDBObject getQuery = new BasicDBObject();
        getQuery.put("place.country_code", countryCode);
        DBCursor cursor = collection.find(getQuery, fields);
        return cursor;
    }
        
    public DBCursor selectTop10PopularUsers(){
        BasicDBObject getQuery = new BasicDBObject();
        //getQuery.put("user.followers_count", new BasicDBObject("$gt", 0));
        BasicDBObject fields = new BasicDBObject();
        fields.put("user.name", 1);
        fields.put("user.followers_count", 1);
        fields.put("place.country_code", 1);
        fields.put("place.country", 1);
        fields.put("place.name", 1);
        fields.put("place.full_name", 1);
        fields.put("entities.hashtags", 1);
        fields.put("text", 1);
        DBCursor cursor = collection.find(getQuery, fields).sort(new BasicDBObject("user.followers_count",-1)).limit(10);
        return cursor;
    }

    public DBCursor selectTopPopularUsers(BasicDBObject fields, int count){
        BasicDBObject getQuery = new BasicDBObject();
        DBCursor cursor = collection.find(getQuery, fields).sort(new BasicDBObject("user.followers_count",-1)).limit(count);
        return cursor;
    }
    
    public DBCursor selectTweetsWithHashtags(String sHashtag){
        if(!sHashtag.startsWith("#")){
            sHashtag = "#" + sHashtag;
        }
        Pattern p = Pattern.compile(sHashtag);
        BasicDBObject query = new BasicDBObject("text", p);

        BasicDBObject fields = new BasicDBObject();
        fields.put("user.name", 1);
        fields.put("place.country_code", 1);
        fields.put("place.country", 1);
        fields.put("place.name", 1);
        fields.put("place.full_name", 1);
        fields.put("entities.hashtags", 1);
        fields.put("text", 1);
        return collection.find(query, fields);
    }
    
    public DBCursor selectTweetsWithHashtagsFull(String sHashtag){
        if(!sHashtag.startsWith("#")){
            sHashtag = "#" + sHashtag;
        }
        Pattern p = Pattern.compile(sHashtag);
        BasicDBObject query = new BasicDBObject("text", p);
        return collection.find(query);
    }
    
    /**
     * 
     */
    public TwitStreaming(){
        consumerKey = "";
        consumerSecret = "";
        accessToken = "";
        accessTokenSecret = "";        
    }
    
    /**
     * 
     * @param newConsumerKey
     * @param newConsumerSecret
     * @param newAccessToken
     * @param newAccessTokenSecret
     * @param newMongoDBURL
     * @param newMongoDBPort
     * @param newMongoDBName
     * @param newMongoDBCollecion 
     */
    public TwitStreaming(String newConsumerKey, String newConsumerSecret, String newAccessToken, String newAccessTokenSecret, 
                    String newMongoDBURL, int newMongoDBPort, String newMongoDBName, String newMongoDBCollecion)
    {
        consumerKey = newConsumerKey;
        consumerSecret = newConsumerSecret;
        accessToken = newAccessToken;
        accessTokenSecret = newAccessTokenSecret;
        mongoDBURL = newMongoDBURL;
        mongoDBPort = newMongoDBPort;
        mongoDBName = newMongoDBName;
        mongoDBCollecion = newMongoDBCollecion;
    }
    
    
class TweetDownloadListener implements StatusListener {
    long numTweetsSoFar = 0;
    long timeStart;
    TwitStreaming twitStreaming;

    void initialize(){
        timeStart = System.currentTimeMillis();
        numTweetsSoFar = 0;
    }
    
    TweetDownloadListener(TwitStreaming tws){
        twitStreaming =tws;
    }
    
        @Override
    public void onStatus(Status status) {
        if(twitStreaming.type == TwitStreaming.NUMBER_LIMIT)
        {
            numTweetsSoFar++;

            if(numTweetsSoFar > twitStreaming.getsampleAndStoreByNumber())
            {
                return;
            }
        } else if(twitStreaming.type == TwitStreaming.TIME_LIMIT)
        {
            long currentTime;
            // timeStart = 1000
            currentTime = System.currentTimeMillis(); 
            long timeElapsed = currentTime-timeStart;    
            
            if(timeElapsed >= twitStreaming.getsampleAndStoreByMilisec())
            {
                return;
            }
        }
        
        String statusJson = TwitterObjectFactory.getRawJSON(status);
            DBObject dbObject = (DBObject) JSON
                                    .parse(statusJson);
            twitStreaming.collection.insert(dbObject);
        System.out.println("Tweet count so far: "+ twitStreaming.collection.count());

        System.out.println("@" + status.getUser().getScreenName() + " - " + statusJson);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
        System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
    }

    @Override
    public void onStallWarning(StallWarning warning) {
        System.out.println("Got stall warning:" + warning);
    }

    @Override
    public void onException(Exception ex) {
        ex.printStackTrace();
    }
    }
}