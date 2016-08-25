
package edu.lander.sentiment;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 *
 * @author Rob Schultz
 * 
 * 
 * Sentiment analysis library with the choice of three different libraries able to use
 */
public class Analysis {
       
    private Map<String, List<Map>> dictonaries = new HashMap();
       private Map<String, Double> happy;
       private Map<String, Double> sad ;
       private Map<String, Double> sentiWord;
       final static int PLEASURE = 0;
       final static int AROUSAL = 1;
       final static int DOMINANCE = 2;
       
    //--------------------------------------------------------------------------------------------------------------\\
     //string or file
       //set config file
       //"C:\\Users\\rob\\Documents\\NetBeansProjects\\SentimentAnalysis\\src\\senti\\config.properties"
   //--------------------------------------------------------------------------------------------------------------\\
   
   /**
    * calling this will create all dictionaries and prepare you to be able to do analysis
    * @param filePath is the file path to your config file 
    */
    public Analysis(String filePath)
    {
        Properties prop = new Properties();
	InputStream input = null;
        try {
        	input = new FileInputStream(filePath);
                // load a properties file
		prop.load(input);
                if(prop.getProperty("english_anew")!= null)
                    anewDictCreation("english",prop.getProperty("english_anew"));
                if(prop.getProperty("davies")!= null)
                    daviesDictonaryBuilder(prop.getProperty("davies"));
                if(prop.getProperty("senti")!= null)
                    sentiWordNet(prop.getProperty("senti"));
                if(prop.getProperty("french_anew")!= null)
                    anewDictCreation("french",prop.getProperty("french_anew"));
                if(prop.getProperty("german_anew")!= null)
                    anewDictCreation("german",prop.getProperty("german_anew"));
                if(prop.getProperty("japanese_anew")!= null)
                    anewDictCreation("japanese",prop.getProperty("japanese_anew"));
                if(prop.getProperty("korean")!= null)
                    anewDictCreation("korean",prop.getProperty("korean_anew"));
                if(prop.getProperty("portugese_anew")!= null)
                    anewDictCreation("portugese",prop.getProperty("portugese_anew"));
		if(prop.getProperty("spanish_anew")!= null)
                    anewDictCreation("spanish",prop.getProperty("spanish_anew"));
                if(prop.getProperty("swedish_anew")!= null)
                    anewDictCreation("swedish",prop.getProperty("swedish_anew"));
 
	} catch (IOException ex) {
		ex.printStackTrace();
	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
                                System.out.println("Error in config file");
			}
		}
	}
    }   
    
    private void sentiWordNet(String pathToSWN)
            {	Map<String, Double> dictionary = new HashMap();
		HashMap<String, HashMap<Integer, Double>> tempDictionary = new HashMap();
		BufferedReader csv = null;
		try {
			csv = new BufferedReader(new FileReader(pathToSWN));
			int lineNumber = 0;
			String line;
			while ((line = csv.readLine()) != null) {
				lineNumber++;
				// If it's a comment, skip this line.
				if (!line.trim().startsWith("#")) {
					// We use tab separation
					String[] data = line.split("\t");
					String wordTypeMarker = data[0];
					// Example line:
					// POS ID PosS NegS SynsetTerm#sensenumber Desc
					// a 00009618 0.5 0.25 spartan#4 austere#3 ascetical#2
					// ascetic#2 practicing great self-denial;...etc

					// Is it a valid line? Otherwise, throw exception.
					if (data.length != 6) {
						throw new IllegalArgumentException(
								"Incorrect tabulation format in file, line: "
										+ lineNumber);
					}

					// Calculate synset score as score = PosS - NegS
					Double synsetScore = Double.parseDouble(data[2])
							- Double.parseDouble(data[3]);

					// Get all Synset terms
					String[] synTermsSplit = data[4].split(" ");

					// Go through all terms of current synset.
					for (String synTermSplit : synTermsSplit) {
						// Get synterm and synterm rank
						String[] synTermAndRank = synTermSplit.split("#");
						String synTerm = synTermAndRank[0] + "#"
								+ wordTypeMarker;

						int synTermRank = Integer.parseInt(synTermAndRank[1]);
						// What we get here is a map of the type:
						// term -> {score of synset#1, score of synset#2...}

						// Add map to term if it doesn't have one
						if (!tempDictionary.containsKey(synTerm)) {
							tempDictionary.put(synTerm,
									new HashMap());
						}

						// Add synset link to synterm
						tempDictionary.get(synTerm).put(synTermRank,
								synsetScore);
					}
				}
			}

                     // Go through all the terms.
                        tempDictionary
                                .entrySet().stream().forEach((entry) -> {
                                    String word = entry.getKey();
                                    Map<Integer, Double> synSetScoreMap = entry.getValue();
                                    // Calculate weighted average. Weigh the synsets according to their rank.
                                    // Score= 1/2*first + 1/3*second + 1/4*third ..... etc.
                                    // Sum = 1/1 + 1/2 + 1/3 ...
                                    double score = 0.0;
                                    double sum = 0.0;
                                    for (Map.Entry<Integer, Double> setScore : synSetScoreMap
                                            .entrySet()) {
                                        score += setScore.getValue() / (double) setScore.getKey();
                                        sum += 1.0 / (double) setScore.getKey();
                                    }
                                    score /= sum;
                                    dictionary.put(word, score);
                     });
		} catch (IOException | IllegalArgumentException e) {
		} finally {
			if (csv != null) {
                            try{
				csv.close();
                            }
                            catch(Exception e)
                            {System.out.println(e);
                            }
			}
                        sentiWord= dictionary;
		}
            }
    
    private void  daviesDictonaryBuilder(String pathToSWN)
    {
         ap<String, Double> happy_log_probs = new HashMap<>();
         String tokens = "";
         BufferedReader csv = null;
         try {
			csv = new BufferedReader(new FileReader(pathToSWN));
			String line;
                        csv.readLine();// skip the header
			while ((line = csv.readLine()) != null) {
				String[] data = line.split(",");
				tokens = data[0];
                                happy_log_probs.put(tokens, Double.parseDouble(data[1]));
                                        }
        
    }
         catch(IOException | NumberFormatException e)
         {
         }
         finally 
         {
             if (csv != null) {
                try{
                    csv.close();
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
                happy = happy_log_probs;
            }
             
         }
         Map<String, Double> sad_log_probs = new HashMap<String, Double>();
        String sadTokens = "";
        
         BufferedReader sadReader = null;
         try {sadReader = new BufferedReader(new FileReader(pathToSWN));
			
			String line;
                        sadReader.readLine();// skip the header
			while ((line = sadReader.readLine()) != null) {
				String[] data = line.split(",");
				tokens = data[0];
                                sad_log_probs.put(sadTokens, Double.parseDouble(data[2]));
                                        }
        
    }
         catch(IOException | NumberFormatException e)
         {
         }
         finally{
             if (sadReader != null) {
                try{
                    sadReader.close();
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
            }
             sad = sad_log_probs;
         }
}
    
    private void  anewDictCreation(String dictName,String pathToSWN)
    {
        Map<String, Double> pleasureDict = new HashMap();
        Map<String, Double> arousalDict = new HashMap();
        Map<String, Double> dominanceDict = new HashMap() ;
        Map<String, Double> anewPDict = new HashMap();
        String tokens = "";
        
         BufferedReader csv = null;
         try {
			csv = new BufferedReader(new FileReader(pathToSWN));
			String line;
                        csv.readLine();// skip the header
			while ((line = csv.readLine()) != null) {
				String[] data = line.split("\t");
				tokens = data[0];
                                anewPDict.put(tokens, Double.parseDouble(data[2]));
                                
                                        }
        
    }
         catch(IOException | NumberFormatException e)
         {
             
         }
         finally 
         {
             if (csv != null) {
                try{
                    csv.close();
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
               pleasureDict =anewPDict;
               
         }
        
         Map<String, Double> anewADict = new HashMap();
        String anewTokens = "";
        
         BufferedReader aDict;
        aDict = null;
         try {
			aDict = new BufferedReader(new FileReader(pathToSWN));
			
			String line;
                        aDict.readLine();// skip the header
			while ((line = aDict.readLine()) != null) {
				String[] data = line.split("\t");
				anewTokens = data[0];
                                anewADict.put(anewTokens, Double.parseDouble(data[4]));
                                        }
        
    }
         catch(IOException | NumberFormatException e)
         {
             
         }
         finally 
         {
             if (aDict != null) {
                try{
                    aDict.close();
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
               arousalDict= anewADict;
         }
         
        Map<String, Double> anewDDict = new HashMap();
        String dominanceTokens = "";
        
         BufferedReader dominanceRead = null;
         try {
			dominanceRead = new BufferedReader(new FileReader(pathToSWN));
			
			String line;
                        dominanceRead.readLine();// skip the header
			while ((line = dominanceRead.readLine()) != null) {
				String[] data = line.split("\t");
				dominanceTokens = data[0];
                                anewDDict.put(dominanceTokens, Double.parseDouble(data[2]));
                                        }
        
                        }
         catch(Exception e)
         {
             
         }
         finally 
         {
             if (dominanceRead != null) {
                try{
                    dominanceRead.close();
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
                dominanceDict= anewDDict;
                
                List<Map> dicts = new ArrayList<Map>();
                
                dicts.add(pleasureDict);
                dicts.add(arousalDict);
                dicts.add(dominanceDict);
                dictonaries.put(dictName, dicts);
             }
                        }
                }
         }
    }
    
    /**
     * Calculation for the pleasure part of the ANEW English dictionary 
     * 
     * 
     * @param words Sentence that you are wanting to calculate with anew pleasure dictionary
     * 
     * @return pleasure score of word given using default language of English
     */
     public double pleasureCalc(String words)
    {
        return pleasureCalc("english",words);
    }
     
     
     /**
     * Calculation for the arousal part of the ANEW English dictionary 
     * 
     * 
     * @param words Sentence that you are wanting to calculate with anew arousal dictionary
     * 
     * @return pleasure score of word given using default language of English
     */
     public double arousalCalc(String words)
    {
        return arousalCalc("english",words);
    }
     
     
     /**
     * Calculation for the dominance part of the ANEW English dictionary 
     * 
     * 
     * @param words Sentence that you are wanting to calculate with anew dominance dictionary
     * 
     * @return pleasure score of word given using default language of English
     */
     public double dominanceCalc(String words)
    {
        return dominanceCalc("english",words);
    }
    
   
    /**
     * Calculation for the pleasure part of the ANEW dictionary
     * 
     * 
     * @param words Sentence that you are wanting to calculate with anew pleasure dictionary
     * @param lang language that is being used 
     * @return this will return a double that is the calculation for the Pleasure part of the sentence 
     */
    public double pleasureCalc(String lang, String words)
    {
        String word[] = words.split(" ");
        
       List<Double> probability = new ArrayList<Double>();
       List<Map> getPDict = dictonaries.get(lang);
       Map<String, Double> pleasureDict = getPDict.get(PLEASURE);
       
        int amount = 0;
        
        for (String word1 : word) {
            Iterator it = pleasureDict.entrySet().iterator();
            for (int i = 0; i<pleasureDict.size(); i++) {
                Map.Entry pair = (Map.Entry)it.next();
                if (pair.getKey().equals(word1)) {
                    probability.add((double)pair.getValue());
                    amount++;
                }
            }
            
        }
         
        double calc = 0;
        calc = probability.stream().map((probability1) -> probability1).reduce(calc, (accumulator, _item) -> accumulator + _item);
        calc = calc/amount;
        return calc;
        
           
        
    }
    /**
     * Arousal calculation of the ANEW dictionary
     * 
     * @param words Sentence that you are wanting to calculate  with anew arousal dictionary
     * @param lang
     * @return this will return a double that is the calculation for the Arousal part of the sentence
     */
    public double arousalCalc(String lang, String words)
    {
        String word[] = words.split(" ");
        
        List<Double> probability = new ArrayList<Double>();
        List<Map> getADict = dictonaries.get(lang);
        Map<String, Double> arousalDict = getADict.get(AROUSAL);
     
        
        int amount = 0;
        for (String word1 : word) {
            Iterator it = arousalDict.entrySet().iterator();
            for (int i = 0; i<arousalDict.size(); i++) {
                Map.Entry pair = (Map.Entry)it.next();
                if (pair.getKey().equals(word1)) {
                    probability.add((double)pair.getValue());
                    amount ++;
                }
            }
            
        }
         
        double calc = 0;
        calc = probability.stream().map((probability1) -> probability1).reduce(calc, (accumulator, _item) -> accumulator + _item);
        calc = calc/amount;
        return calc;
    }
    
    /**
     * Dominance calculation of the ANEW dictionary
     * @param words Sentence that you are wanting to calculate with anew dominance 
     * @param lang
     * @return this will return a double that is the calculation for the Dominance part of the sentence
     */
    public double dominanceCalc(String lang, String words)
    {
        String word[] = words.split(" ");
        
        List<Double> probability = new ArrayList<Double>();
        List<Map> getDDict = dictonaries.get(lang);
        Map<String, Double> dominanceDict = getDDict.get(DOMINANCE);
        
     
        int amount = 0;
        for (String word1 : word) {
            Iterator it = dominanceDict.entrySet().iterator();
            for (int i = 0; i<dominanceDict.size(); i++) {
                Map.Entry pair = (Map.Entry)it.next();
                if (pair.getKey().equals(word1)) {
                    probability.add((double)pair.getValue());
                    amount ++;
                }
            }
          
        }
         
        double calc = 0;
        calc = probability.stream().map((probability1) -> probability1).reduce(calc, (accumulator, _item) -> accumulator + _item);
        calc = calc/amount;
        return calc;
    }
    
    
    private double probHappy(String words, Map<String, Double> happy)
    {   
        String word[] = words.split(" ");
        
        List<Double> probability = new ArrayList<Double>();
        
        
        for (String word1 : word) {
            Iterator it = happy.entrySet().iterator();
            for (int i = 0; i<happy.size(); i++) {
                Map.Entry pair = (Map.Entry)it.next();
                if (pair.getKey().equals(word1)) {
                    probability.add((double)pair.getValue());
                }
            }
            
        }
         
        double calc = 0;
        calc = probability.stream().map((probability1) -> probability1).reduce(calc, (accumulator, _item) -> accumulator + _item);
        return calc;
        
           
    }
    private double probSad(String words, Map<String, Double> sad)
    {   
        String word[] = words.split(" ");
        
        List<Double> probability = new ArrayList<Double>();
        
        
        for (String word1 : word) {
            Iterator iter = sad.entrySet().iterator();
            for (int i = 0; i<sad.size(); i++) {
                Map.Entry pair = (Map.Entry)iter.next();
                if (pair.getKey().equals(word1)) {
                    probability.add((double)pair.getValue());
                }
            }
            
        }
         
        double calc = 0;
        calc = probability.stream().map((probability1) -> probability1).reduce(calc, (accumulator, _item) -> accumulator + _item);
        return calc;
        
          }
    private double happyCalc(double happy, double sad)
    {
        //np.reciprocal(np.exp(tweet_sad_log_prob - tweet_happy_log_prob) + 1)
        double happyCalc = 1/ (Math.exp(sad - happy) + 1);
        return happyCalc;
    }
    private double sadCalc(double happy, double sad, double probHappy)
    {
        double sadCalc = 1 - probHappy;
        return sadCalc;
    }
    /**
     * This method is to calculate the probability that the sentence is happy
     * this uses the alex davies database 
     * @param tweet Sentence that you are wanting to calculate with davies 
     * @return returns a double which is the calculation of how happy it is, scaled between 1-0 
     */
    public double calculateProbHappy( String tweet)
    {
        double happyCalc = probHappy(tweet, happy);
        double sadCalc = probSad(tweet, sad);
        
        double probHappy = happyCalc(happyCalc, sadCalc);
       // double probSad = sadCalc(happyCalc, sadCalc, probHappy);
        return probHappy;
        
    }
    /**
     * This method is to calculate the probability that the sentence is sad
     * this uses the alex davies database 
     * @param tweet Sentence that you are wanting to calculate  with davies 
     * @return returns a double which is the calculation of how sad it is, scaled between 1-0 
     */
    public double calculateProbSad( String tweet)
    {
        double happyCalc = probHappy(tweet, happy);
        double sadCalc = probSad(tweet, sad);
        
        double probHappy = happyCalc(happyCalc, sadCalc);
        double probSad = sadCalc(happyCalc, sadCalc, probHappy);
        return probSad;
        
    }
    /**
     * this is for the calculation of whether a sentence is happy or sad will return a double 
     * uses the sentiwordnet dictonary
     * @param words Sentence that you are wanting to calculate with sentiword
     * @return returns a double that is the calculation for the words that are given
     */
 public double sentiWordCalc(String words) {
                String word[] = words.split(" ");
                double calculation = 0;
                boolean a =false, b = false , c = false ,d = false;
        for (String word1 : word) {
            if (sentiWord.get(word1 + "#" + "a") != null) {
                calculation += sentiWord.get(word1 + "#" + "a");
                a = true;
            }
            if (sentiWord.get(word1 + "#" + "n") != null) {
                calculation += sentiWord.get(word1 + "#" + "n");
                b = true;
            }
            if (sentiWord.get(word1 + "#" + "v") != null) {
                calculation += sentiWord.get(word1 + "#" + "v");
                c = true;
            }
            if (sentiWord.get(word1 + "#" + "r") != null) {
                calculation += sentiWord.get(word1 + "#" + "r");
                d = true;
            }
        }
                int amount = 0;
                if(a == true) amount++;
                if(b == true) amount++;
                if(c == true) amount++;
                if(d == true) amount++;
                
                calculation = calculation/amount;
		return calculation;
	} 
 
 
 /**
  * User will supply a dbObject containing the user information and the count on the amount of retweets and favorites
  * use only for twitter
  * @param dbo database object for twitter
  * @return users media significance 
  */
 public double mediaSigCalc(DBObject dbo)
 {
        BasicDBObject userInfo = (BasicDBObject)dbo.get("user");

        Integer rtCount = (Integer) dbo.get("retweet_count");
        Integer followerCount =(Integer) userInfo.get("followers_count");
        Integer favCount = (Integer)dbo.get("favorite_count");

        double influentiality = (double)Math.log(followerCount)/ Math.log(1000);
        double RTratio =  ((double) rtCount/(double)followerCount);
        double FCratio =  ((double) favCount/(double)followerCount);

        return ((RTratio+FCratio)*influentiality*influentiality*influentiality)+1;
                        
 }
 
 /**
  *  calculate a users media significance by using the data in the order of 
  * 
  * @param rtCount the amount of followers they have
  * @param followerCount the amount of times the data has been shared publicly
  * @param favCount how many times the data has been liked privately but not shared publicly
  * @return user media significance
  */
 
 public double mediaSigCalc( int followerCount,int rtCount, int favCount)
 {
        double influentiality = (double)Math.log(followerCount)/ Math.log(1000);
        double RTratio =  ((double) rtCount/(double)followerCount);
        double FCratio =  ((double) favCount/(double)followerCount);

        return (RTratio+FCratio)*influentiality*influentiality*influentiality;
                        
 }
}
