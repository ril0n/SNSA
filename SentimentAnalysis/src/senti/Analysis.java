
package senti;

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
 * Sentiment analysis library with the choice of three different libraries able to use
 */
public class Analysis {
       private Map<String, Double> pleasureDict;
       private Map<String, Double> arousalDict;
       private Map<String, Double> dominanceDict;
       private Map<String, Double> happy;
       private Map<String, Double> sad;
       private Map<String, Double> sentiWord; 
       
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
                if(prop.getProperty("anew")!= null)
                    anewDictCreation(prop.getProperty("anew"));
                if(prop.getProperty("davies")!= null)
                    daviesDictonaryBuilder(prop.getProperty("davies"));
                if(prop.getProperty("senti")!= null)
                    sentiWordNet(prop.getProperty("senti"));
	} catch (IOException ex) {
		ex.printStackTrace();
	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    }   
     
    private void sentiWordNet(String pathToSWN)
            {
                 Map<String, Double> dictionary = new HashMap();
		// From String to list of doubles.
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

					// Is it a valid line? Otherwise, through exception.
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
                                    
                                    // Calculate weighted average. Weigh the synsets according to
                                    // their rank.
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
                            {
                                System.out.println(e);
                            }
			}
                        sentiWord= dictionary;
		}
                
            }
    
    private void  daviesDictonaryBuilder(String pathToSWN)
    {
        Map<String, Double> happy_log_probs = new HashMap<>();
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
    
    private void  anewDictCreation(String pathToSWN)
    {
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
                            }
                        }
                }
         }
    }

         
    
   
    /**
     * Calculation for the pleasure part of the ANEW dictionary
     * 
     * 
     * @param words Sentence that you are wanting to calculate with anew pleasure dictionary
     * 
     * @return this will return a double that is the calculation for the Pleasure part of the sentence 
     */
    public double pleasureCalc(String words)
    {
        String word[] = words.split(" ");
        
        List<Double> probability = new ArrayList<Double>();
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
     * 
     * @return this will return a double that is the calculation for the Arousal part of the sentence
     */
    public double arousalCalc(String words)
    {
        String word[] = words.split(" ");
        
        List<Double> probability = new ArrayList<Double>();
        
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
     * 
     * @return this will return a double that is the calculation for the Dominance part of the sentence
     */
    public double dominanceCalc(String words)
    {
        String word[] = words.split(" ");
        
        List<Double> probability = new ArrayList<Double>();
        
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
	 * Used to check whether or not the pleasure part of the ANEW dictionary is avaliable or not
	 * 
	 * @return will return true or false depending on whether the dictionary is avalliable or not: true avalabile, false not avalabile
	 */
	public boolean pDictIsAvalabile()
       {
       	if(pleasureDict != null)
       	return true;
       	else return false;
       }
       
       /**
	 * Used to check whether or not the arousal part of the ANEW dictionary is avaliable or not
	 * 
	 * @return will return true or false depending on whether the dictionary is avalliable or not: true avalabile, false not avalabile
	 */
       public boolean aDictIsAvalabile()
       {
       	if(arousalDict != null)
       	return true;
       	else return false;
       }
       
       /**
	 * Used to check whether or not the dominance part of the ANEW dictionary is avaliable or not
	 * 
	 * @return will return true or false depending on whether the dictionary is avalliable or not: true avalabile, false not avalabile
	 */
       public boolean dDictIsAvalabile()
       {
       	if(dominanceDict != null)
       	return true;
       	else return false;
       }
       
       /**
	 * Used to check whether or not the happy part of the Alex Davies dictionary is avaliable or not
	 * 
	 * @return will return true or false depending on whether the dictionary is avalliable or not: true avalabile, false not avalabile
	 */
       public boolean happyDictAvaliable()
       {
       	 	if(happy != null)
       	 		return true;
		else return false;
       }
       
       /**
	 * Used to check whether or not the sad part of the Alex Davies dictionary is avaliable or not
	 * 
	 * @return will return true or false depending on whether the dictionary is avalliable or not: true avalabile, false not avalabile
	 */
       public boolean sadDictAvaliable()
       {
       	 	if(sad != null)
       	 		return true;
		else return false;
       }
       
       /**
	 * Used to check whether or not the sentiWord dictionary is avaliable or not
	 * 
	 * @return will return true or false depending on whether the dictionary is avalliable or not: true avalabile, false not avalabile
	 */
       public boolean sentiDictAvaliable()
       {
       	 	if(sentiWord != null)
       	 		return true;
		else return false;
       }
}
