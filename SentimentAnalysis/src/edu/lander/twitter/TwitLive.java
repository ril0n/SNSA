package edu.lander.twitter;


import com.mongodb.BasicDBObject;
import java.util.List;
import java.util.Map;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;
import com.mongodb.util.JSON;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import twitter4j.TwitterObjectFactory;

/**
 * 
 * @author Robert Schultz robert.schultz@lander.edu
 */

public class TwitLive {
    
    private String mongoDBURL = "";
    private int mongoDBPort = 0;
    private String mongoDBName = "";
    private String mongoDBCollection = "";
    private Mongo mongo;
    private DB db;
    private DBCollection collection;
    
    private String oAuthkey = "";
    private String oAuthSecret = "";
    private String oAuthToken = "";
    private String oAuthTokenSecret = "";
    
    
    
    public void connectMongoDB(){
        try {
            mongo = new Mongo(mongoDBURL, mongoDBPort);
            db = mongo.getDB(mongoDBName);
            collection = db.getCollection(mongoDBCollection);
            } catch (MongoException e) {
                    e.printStackTrace();
        }
    }
    
    
    /**
     * 
     * @return 
     */
    public DBCursor testing()
    {
        
        DBCursor cursor = collection.find();
        return cursor;
    }
    
    /**
     * 
     * @param before
     * @return 
     */
    public DBCursor dateBeforeSearch(Date before)
    {
        DBObject getQuery = new BasicDBObject();
        try{
            getQuery = QueryBuilder.start().put("dateQuery").lessThan(before).get();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return collection.find(getQuery);
                
                
    }
    
    /**
     * 
     * @param after
     * @param before
     * @return 
     */
    public DBCursor dateBetweenSearch(Date after, Date before)
    {
        DBObject getQuery = new BasicDBObject();
        try{
            getQuery = QueryBuilder.start().put("dateQuery").lessThan(before).greaterThan(after).get();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return collection.find(getQuery);
                
                
    }
    
    /**
     * 
     * @param after
     * @return 
     */
    public DBCursor dateAfterSearch(Date after)
    {
        DBObject getQuery = new BasicDBObject();
        try{
            getQuery = QueryBuilder.start().put("dateQuery").greaterThan(after).get();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return collection.find(getQuery);
                
                
    }
    
    
    public List<Status> searchTweetsReturn(String q )
    {   
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey(oAuthkey)
          .setOAuthConsumerSecret(oAuthSecret)
          .setOAuthAccessToken(oAuthToken)
          .setOAuthAccessTokenSecret(oAuthTokenSecret)
          .setJSONStoreEnabled(true)
          .setApplicationOnlyAuthEnabled(true)
                ; 
        
        /*
        
        .setOAuthConsumerKey("SIUcyItvuhMnFOIYzsw7CgAcs")
          .setOAuthConsumerSecret("6ifz7TtXS1pKJ30c6pg8GCrpvxADpV4OocEcP4kGpwn4HJTSGq")
          .setOAuthAccessToken("80479311-VoeFB2GWxeDRcHrNkFnDzbQTG257JcfqlbzEK77my")
          .setOAuthAccessTokenSecret("k631YLjCzkyidv7yNwAIepTd6w5Yywh6mA2bvz5KfIpjT)
       
        .setOAuthConsumerKey("YVyMX8six6OIIkbFnMcRapN3i");
        .setOAuthConsumerSecret("iNC2AAMChczsoyay92GVLKiTCD2vdYZIQt5NhBDe70mhRt7Tgh");
        .setOAuthAccessToken("80479311-j4Bd6VmYhMICMifDcnDstFM6okCL3m6qT3hAD4qEz");
        .setOAuthAccessTokenSecret("wkmWyo6xEexJxxdnW4fQkloLJERYa0DVMn7Oyg42BYes7");
          
        
        .setOAuthConsumerKey("Chqwt8ePgqEqYJOittmYwN9is");
        .setOAuthConsumerSecret("NIpYjtdLuoWT4nCKxdnUrV4Fpct7nxA4fRAdl45whqY07N1Dkc");
        .setOAuthAccessToken("2984561007-xXisXhTyvWHePjRHBlD5sEOD3PJdrIqF5AO7QBM");
        .setOAuthAccessTokenSecret("7HmRBnh8NZUd0mSCUKkoKPyCueoLZy1ONANYva8zYJG5Q");
        */
         Twitter twit =  new TwitterFactory(cb.build()).getInstance();
         List<Status> tweets =new ArrayList<Status>();
         try{
            OAuth2Token token = twit.getOAuth2Token();
           
            Map<String, RateLimitStatus> rateLimitStatus = twit.getRateLimitStatus("search");
            RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");
            System.out.println(searchTweetsRateLimit.getLimit());
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        int totalTweets = 0;
         try {
             for(int a=0; a<10; a++){
            Query query = new Query(q);
            QueryResult result;
            result = twit.search(query);
             tweets = result.getTweets();
            System.out.println("Number of tweets: " + tweets.size());
             }
            for (Status tweet : tweets) {
                totalTweets++;
                System.out.println("@" + tweet.getUser().getScreenName() + "-" + tweet.getText());
        
            }
             
        } catch (TwitterException se) {
            se.printStackTrace();
            System.out.println("Failed to search tweets: " + se.getMessage());
        }
         catch(Exception e)
         {
             e.printStackTrace();
         }
        return tweets;
    }
    
    
    
    /**
     * 
     * @param q
     * @return 
     */
    public int searchTweetsStore(String q )
    {   
        int amount = 0;
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey(oAuthkey)
          .setOAuthConsumerSecret(oAuthSecret)
          .setOAuthAccessToken(oAuthToken)
          .setOAuthAccessTokenSecret(oAuthTokenSecret)
          .setJSONStoreEnabled(true)
          .setApplicationOnlyAuthEnabled(true)
                ; 
        
        /*
        
        .setOAuthConsumerKey("SIUcyItvuhMnFOIYzsw7CgAcs")
          .setOAuthConsumerSecret("6ifz7TtXS1pKJ30c6pg8GCrpvxADpV4OocEcP4kGpwn4HJTSGq")
          .setOAuthAccessToken("80479311-VoeFB2GWxeDRcHrNkFnDzbQTG257JcfqlbzEK77my")
          .setOAuthAccessTokenSecret("k631YLjCzkyidv7yNwAIepTd6w5Yywh6mA2bvz5KfIpjT)
       
        .setOAuthConsumerKey("YVyMX8six6OIIkbFnMcRapN3i");
        .setOAuthConsumerSecret("iNC2AAMChczsoyay92GVLKiTCD2vdYZIQt5NhBDe70mhRt7Tgh");
        .setOAuthAccessToken("80479311-j4Bd6VmYhMICMifDcnDstFM6okCL3m6qT3hAD4qEz");
        .setOAuthAccessTokenSecret("wkmWyo6xEexJxxdnW4fQkloLJERYa0DVMn7Oyg42BYes7");
          
        
        .setOAuthConsumerKey("Chqwt8ePgqEqYJOittmYwN9is");
        .setOAuthConsumerSecret("NIpYjtdLuoWT4nCKxdnUrV4Fpct7nxA4fRAdl45whqY07N1Dkc");
        .setOAuthAccessToken("2984561007-xXisXhTyvWHePjRHBlD5sEOD3PJdrIqF5AO7QBM");
        .setOAuthAccessTokenSecret("7HmRBnh8NZUd0mSCUKkoKPyCueoLZy1ONANYva8zYJG5Q");
        */
         Twitter twit =  new TwitterFactory(cb.build()).getInstance();
        try{
            OAuth2Token token = twit.getOAuth2Token();
           
            Map<String, RateLimitStatus> rateLimitStatus = twit.getRateLimitStatus("search");
            RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");
            System.out.println(searchTweetsRateLimit.getLimit());
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        int totalTweets = 0;
         try {
            Query query = new Query(q);
            QueryResult result;
            //do{
                result = twit.search(query);
                List<Status> tweets = result.getTweets();
                System.out.println("Number of tweets: " + tweets.size());

                for (Status tweet : tweets) {
                    totalTweets++;
                 System.out.println("@" + tweet.getUser().getScreenName() + "-" + tweet.getText());
                
                 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH); //creating date by date format that works with mongodb
                Date date = new Date(); // create todays date
                
                 String statusJson = TwitterObjectFactory.getRawJSON(tweet);
                 
                DBObject dbObject = (DBObject) JSON.parse( statusJson); // the data that is being inserted into db
                dbObject.put("dateQuery", dateFormat.parse(dateFormat.format(date))); // adding in the date into the data that will be in the db will put it in last 
               


                collection.insert(dbObject);
        
                }
           //}while(result.hasNext());
             
        } catch (TwitterException se) {
            se.printStackTrace();
            System.out.println("Failed to search tweets: " + se.getMessage());
        }
         catch(Exception e)
         {
             e.printStackTrace();
         }
         System.out.println("Total tweets: "+ totalTweets);
        return amount;
    }
    
    
    public void setMongoURL(String url)
    {
        mongoDBURL = url;
    }
    public void setMongoPort(int port)
    {
    mongoDBPort = port;
    }
    public void setMongoName(String name)
    {
        mongoDBName = name;
    }
    public void setMongoCollection(String collec)
    {
        mongoDBCollection = collec;
    }
    
    public void setAuthKey(String key)
    {
        oAuthkey = key;
    }
    public void setAuthSecret(String sec)
    {
        oAuthSecret = sec;
    }
    public void setAuthToken(String token)
    {
        oAuthToken = token;
    }
    public void setAuthTokenSecret(String sec)
    {
        oAuthTokenSecret = sec;
    }
   }