using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsoleApplication1
{
    class Analysis
    {
        private Dictionary<string, double> pleasureDict;
        private Dictionary<string, double> arousalDict;
        private Dictionary<string, double> dominanceDict;
        private Dictionary<string, double> happy;
        private Dictionary<string, double> sad;
        private Dictionary<string, double> sentiWord;

        public Analysis(String filePath)
        {
            try
            {

            }
            catch(InvalidCastException e)
            {
                //do nothing
            }

        }

        private void sentiWordNet(string filePath)
        {
            Dictionary<string, double> dictionary = new Dictionary<string, double>();
            int lineNumber = 0;
            string line;
            System.IO.StreamReader csv;
            try {
            // Read the file and display it line by line.
            
            while ((line = csv.ReadLine()) != null)
            {   lineNumber++;
                csv = new System.IO.StreamReader(@filePath);
                if(line.Trim().StartsWith("#")){
                // We use tab separation
					string[] data = line.Split('\t');
					String wordTypeMarker = data[0];

					// Example line:
					// POS ID PosS NegS SynsetTerm#sensenumber Desc
					// a 00009618 0.5 0.25 spartan#4 austere#3 ascetical#2
					// ascetic#2 practicing great self-denial;...etc

					// Is it a valid line? Otherwise, through exception.
					if (data.Length != 6) {
						throw new IllegalArgumentException(
								"Incorrect tabulation format in file, line: "
										+ lineNumber);
					}

					// Calculate synset score as score = PosS - NegS
					Double synsetScore = Convert.ToDouble(data[2]) - Convert.ToDouble(data[3]);

					// Get all Synset terms
					String[] synTermsSplit = data[4].Split(' ');

					// Go through all terms of current synset.
					foreach (string synTermSplit in synTermsSplit) {
						// Get synterm and synterm rank
						String[] synTermAndRank = synTermSplit.Split('#');
						String synTerm = synTermAndRank[0] + "#"
								+ wordTypeMarker;

						int synTermRank = Convert.ToInt32(synTermAndRank[1]);
						// What we get here is a map of the type:
						// term -> {score of synset#1, score of synset#2...}

						// Add map to term if it doesn't have one
						if (!dictionary.containsKey(synTerm)) {
							dictionary.put(synTerm,
									new HashMap());
						}

						// Add synset link to synterm
						dictionary.get(synTerm).put(synTermRank,
								synsetScore);
					}
				}
			}

                     // Go through all the terms.
                        dictionary.entrySet().stream().forEach((entry) -> {
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
}
            catch (Exception e) {
                        } 
            finally {
			if (csv != null) {
                try{
				    csv.Dispose();
                }
                catch(Exception e)
                {
                               
                }
			}
                        sentiWord= dictionary;
		}
                
        }

    }
}