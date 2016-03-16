package edu.lander.instagram;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;

/**
 * Created by Devin Tinsley on 11/10/2015.
 */
public class MongoDB_CnS {

    public String m_sCollection, m_sHost, m_DbName;
    public int m_port = -1;
    MongoClient mongoClient;
    MongoCollection m_MongoCollection;
    DBCollection DBCollection;
    DB db;

    public void MongoDB_CnS() {
        new MongoDB_CnS();
        mongoClient = new MongoClient();
    }

    public void ConnectToMongoDB(String host, int port, String DBName, String mongoCollection) {
        m_sHost = host;
        m_port = port;
        m_DbName = DBName;
        m_sCollection = mongoCollection;
        mongoClient = new MongoClient(host, port);
        try {
            db = mongoClient.getDB(DBName);
            DBCollection = db.getCollection(mongoCollection);
            //m_MongoDB = mongoClient.getDatabase(DBName);
            //m_MongoCollection = m_MongoDB.getCollection(mongoCollection);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void ConnectToMongoDB(){
        if(m_sHost != "" && m_port != -1) {
            mongoClient = new MongoClient(m_sHost, m_port);
            try {
                db = mongoClient.getDB(m_DbName);
                DBCollection = db.getCollection(m_sCollection);
                //m_MongoDB = mongoClient.getDatabase(m_DbName);
                //m_MongoDB.getCollection(m_sCollection);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else {
            System.out.println("Host and port are not set. \nPlease make sure you have set these parameters\ncall setHostName(String) and setPort(Int)");;
        }

    }

    public String getHostName() {
        String host = m_sHost;
        return host;
    }

    public void setHostName(String host) {
        m_sHost = host;
    }

    public void setPort(int port) {
        m_port = port;
    }

    public int getPort() {
        try{
            return m_port;
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    public void setDbName(String dbName) {
        m_DbName = dbName;
        //mongoClient.getDB(dbName);
    }

    public String getDbName() {
        return m_DbName;
    }

    public String getCollectionName() {
        return m_sCollection;
    }

    public void setCollectionName(String collection) {
        m_sCollection = collection;
    }

    public DBCollection getDBCollection(String dbCollection) {
        return db.getCollection(dbCollection);
    }

    public void storeArrayListToMongoDB(ArrayList<String> toStore) {
        if(db!=null) {
            for (String store : toStore) {
                m_MongoCollection.insertOne(store);
            }
        }
    }

    public void storeStringToMongoDB(BasicDBObject toStore) {
        if(db!=null) {
            DBCollection.insert(toStore);
        }
    }
}
