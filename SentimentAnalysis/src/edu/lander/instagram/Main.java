package edu.lander.instagram;
import com.mongodb.BasicDBObject;

import java.util.Scanner;
import org.json.JSONObject;

/**
 * Created by Devin Tinsley on 11/10/2015.
 */
public class Main {
    public static void main(String args[]) {
        String sUserInput = "";

        Scanner kb = new Scanner(System.in);
        MongoDB_CnS conn = new MongoDB_CnS();
        conn.setHostName("localhost");
        conn.setPort(27017);
        conn.setCollectionName("InstaCollection");
        conn.setDbName("InstaDB");
        //conn.setDBCollection("InstaCollection");

        try {
            conn.ConnectToMongoDB();
            System.out.println("Success");
        } catch (Exception ex) {
            System.out.println("Error");
            ex.printStackTrace();
        }

        InstaConnect Insta = new InstaConnect();
        Insta.setApiKey("98984b29f7014bdd841440acc2f6c012");
        Insta.setApiSecret("9253129215df44a0b1dfbd2e95be6fee");
        //Enter Username and password for Instagram here
        Insta.setUserName("Username");
        Insta.setPassword("Password");

        InstagramPrepareDownloadAndStore instaDNS = new InstagramPrepareDownloadAndStore();
        instaDNS.instaConnect = Insta;
        instaDNS.connectInstagram();
        SearchByLocation sbl = new SearchByLocation(conn.getDBCollection("InsataCollection"), instaDNS.insta);
        //sbl.setInstagram(instaDNS.insta);
        //Testing
        double[] latlng;
        try{
            Thread.sleep(1000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.print("Enter location: ");
        sUserInput = kb.nextLine();
        latlng = sbl.getCoordsFromUserInput(sUserInput);
        //System.out.print("Enter minimum number of pages to load(up to 20 per page): ");
        //int x = Integer.parseInt(kb.nextLine());
        Long unixTime = System.currentTimeMillis() / 1000L;
        sbl.searchByLocation(latlng[SearchByLocation.LAT], latlng[SearchByLocation.LNG], unixTime);
//        for(int i = 1; i < x; i++) {
//            //there are a few problems here
//            unixTime = sbl.getLastTimestamp();
//            sbl.searchByLocation(latlng[0], latlng[1], unixTime);
//        }
        System.out.print("Do you wish to store to mongodb? 1 for yes, and 0 for no. -> ");
        if(kb.nextLine().equals("1")){
            //store to mongodb
            for(JSONObject toStore : sbl.m_vsData) {
                conn.storeStringToMongoDB(BasicDBObject.parse(toStore.toString()));
            }
            int numOfInserts = sbl.m_vsData.size();
            System.out.println(numOfInserts + " records added.");
            System.out.println("Thanks for using this api!");
        }
        else {
            //don't store to mongodb
            System.out.println("Thanks for using this api!");
        }
    }
}
