/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication2;
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
    /**
     * @param args the command line arguments
     */
    
    
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
 
            /*DBObject dbObject = (DBObject) JSON
                                            .parse("{'name':'mkyong', 'age':30}");
            collection.insert(dbObject);

            DBCursor cursorDoc = collection.find();
            while (cursorDoc.hasNext()) {
                                    System.out.println(cursorDoc.next());
            }

            System.out.println("Done");*/

            } catch (MongoException e) {
                    e.printStackTrace();
        }
        
        //MongoClient mongoClient = new MongoClient();

        //or
        //MongoClient mongoClient = new MongoClient( "localhost" );
        // or
        //MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        // or, to connect to a replica set, with auto-discovery of the primary, supply a seed list of members
        /*MongoClient mongoClient = new MongoClient(Arrays.asList(new ServerAddress("localhost", 27017),
                                              new ServerAddress("localhost", 27018),
                                              new ServerAddress("localhost", 27019)));*/
        // or use a connection string
        //MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017,localhost:27018,localhost:27019"));

        //MongoDatabase database = mongoClient.getDatabase("mydb");
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
        //TwitterFactory tf = new TwitterFactory(cb.build());
        //Twitter twitter = tf.getInstance();

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
        // finds all records with "name" matching /^Mon.*/
        DBCursor cursor = collection.find(query, fields);
        return cursor;
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
        DBCursor cursor = collection.find(query, fields);
        return cursor;
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
        DBCursor cursor = collection.find(getQuery, fields);
        return cursor;
    }
    
    public DBCursor selectTweetsFromCountry(String country){
        Pattern p = Pattern.compile(".*"+country+".*");
        BasicDBObject query = new BasicDBObject("place.country", p);
        
       // query.put("place.country", country);
        BasicDBObject fields = new BasicDBObject();
        fields.put("user.name", 1);
        fields.put("place.country_code", 1);
        fields.put("place.country", 1);
        fields.put("place.name", 1);
        fields.put("place.full_name", 1);
        fields.put("entities.hashtags", 1);
        fields.put("text", 1);
        DBCursor cursor = collection.find(query, fields);
        return cursor;
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
        DBCursor cursor = collection.find(query, fields);
        return cursor;
    }
    
    TwitStreaming(){
        consumerKey = "";
        consumerSecret = "";
        accessToken = "";
        accessTokenSecret = "";        
    }
        
    TwitStreaming(String newConsumerKey, String newConsumerSecret, String newAccessToken, String newAccessTokenSecret, 
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
    
        
    public static void main(String args[]) throws Exception{
    // The factory instance is re-useable and thread safe.
    
        TwitStreaming Twit = new TwitStreaming();
        Twit.consumerKey = "Chqwt8ePgqEqYJOittmYwN9is";
        Twit.consumerSecret = "NIpYjtdLuoWT4nCKxdnUrV4Fpct7nxA4fRAdl45whqY07N1Dkc";
        Twit.accessToken = "2984561007-xXisXhTyvWHePjRHBlD5sEOD3PJdrIqF5AO7QBM";
        Twit.accessTokenSecret = "7HmRBnh8NZUd0mSCUKkoKPyCueoLZy1ONANYva8zYJG5Q";
        Twit.mongoDBURL = "localhost";
        Twit.mongoDBPort = 27017;
        Twit.mongoDBName = "mydb";
        Twit.mongoDBCollecion = "TweetData";
        
        Twit.connectMongoDB();
        
        Twit.prepareDownloadTweets(TwitStreaming.TIME_LIMIT,600000);
        
        Twit.downloadStoreTweets();
        
        System.out.println(Twit.collection.count());
        Twit.selectFirstRecordInCollection(Twit.collection);
        //Twit.selectUserFollowers();
        DBCursor cursor1 = Twit.selectTop10PopularUsers();
        while(cursor1.hasNext()){
            DBObject dbo = cursor1.next();
            System.out.println(((DBObject)dbo.get("user")).get("followers_count"));
        }
        
        DBCursor cursor = Twit.selectTweetsFromStateInUS("CA");
        while(cursor.hasNext()){
            DBObject dbo = cursor.next();
            System.out.println(((DBObject)dbo.get("place")).get("full_name"));
        }
        
        DBCursor cursor2 = Twit.selectTweetsWithWordsInText("like");
        while(cursor2.hasNext()){
            DBObject dbo = cursor2.next();
            System.out.println(dbo.get("text"));
            System.out.println(((DBObject)dbo.get("entities")).get("hashtags"));
        }
        
        DBCursor cursor3 = Twit.selectTweetsWithHashtags("ILoveObama");
        while(cursor3.hasNext()){
            DBObject dbo = cursor3.next();
            System.out.println(((DBObject)dbo.get("entities")).get("hashtags"));
            System.out.println(dbo.get("text"));
        }       
        //Twit.findLocations();
        
        
        /*System.out.println(Twit.collection.find(and(gt("user.followers_count", 50), lte("user.followers_count", 100))));
        System.out.println(Twit.collection.find(eq("i", 10)));
        Twit.query();
        
        DBCursor myDoc = Twit.collection.find();
        DBObject dbobj = myDoc.next();
        System.out.println(dbobj.get("id"));
        System.out.println(myDoc.sort(new BasicDBObject("user.followers_count",-1)).limit(10));*/
        
        

        // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
          
        //Status status = twitter.updateStatus("Hello world! Testing Twitter4j!");
        //System.out.println("Successfully updated the status to [" + status.getText() + "].");
        //System.exit(0);
      
        System.out.println("Finished!");
    }

      /*private static AccessToken loadAccessToken(int useId){
        String token = "78188209-bqZh254oGiM9YHyle9nFCWyMEg82xSwterlL3EcWu";
        String tokenSecret = "RlLw0qf291Lw64sIsRn83LbtWtzpGQXdQbEjv1ncZYCxt";
        return new AccessToken(token, tokenSecret);
      }

      // 2. Orrdered bulk operation - order is guarenteed
      collection.bulkWrite(Arrays.asList(new InsertOneModel<Document>(new Document("_id", 4)),
                                        new InsertOneModel<Document>(new Document("_id", 5)),
                                        new InsertOneModel<Document>(new Document("_id", 6)),
                                        new UpdateOneModel<Document>(new Document("_id", 1),
                                                                     new Document("$set", new Document("x", 2))),
                                        new DeleteOneModel<Document>(new Document("_id", 2)),
                                        new ReplaceOneModel<Document>(new Document("_id", 3),
                                                                      new Document("_id", 3).append("x", 4))));*/
}


class TweetDownloadListener implements StatusListener {
    long numTweetsSoFar = 0;
    //long numTweetLimit;
    //long timeLimit;
    long timeStart;
    TwitStreaming twitStreaming;

    void initialize(){
        timeStart = System.currentTimeMillis();
        numTweetsSoFar = 0;
    }
    
    TweetDownloadListener(TwitStreaming tws){
        twitStreaming =tws;
    }
    
    /*public void initializeByNumber(long numMaxTweets){
        numTweetLimit = numMaxTweets;
        type = NUMBER_LIMIT;
    }
    
    public void initializeByTime(long numMilisec){
        timeLimit = numMilisec;
        type = TIME_LIMIT;
    }*/
    
    
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
        /*
        //Status To JSON String
        String statusJson = DataObjectFactory.getRawJSON(status);

        //JSON String to JSONObject
        JSONObject JSON_complete = new JSONObject(statusJson);

        //We get another JSONObject
        JSONObject JSON_user = JSON_complete.getJSONObject("user");

        //We get a field in the second JSONObject
        String languageTweet = JSON_user.getString("lang");
        /*
        */
        String statusJson = TwitterObjectFactory.getRawJSON(status);
            DBObject dbObject = (DBObject) JSON
                                    .parse(statusJson);
            twitStreaming.collection.insert(dbObject);
        System.out.println("Tweet count so far: "+ twitStreaming.collection.count());

        System.out.println("@" + status.getUser().getScreenName() + " - " + statusJson);
            //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());


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