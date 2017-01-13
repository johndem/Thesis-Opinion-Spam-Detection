import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContentSimilarity {
	
	public ContentSimilarity() {
		
	}
	
	public double cosineSimilarity(List<Double> doc1, List<Double> doc2, int len) {
		double sum1 = 0, sum2 = 0, sum3 = 0;
		
		for (int i = 0; i < len; i++) {
			sum1 += doc1.get(i)*doc2.get(i);
			sum2 += doc1.get(i)*doc1.get(i);
			sum3 += doc2.get(i)*doc2.get(i);
		}
		sum2 = (double) Math.sqrt(sum2);
		sum3 = (double) Math.sqrt(sum3);
		
		return sum1/(sum2*sum3);
	}
	
	public double tf(List<String> doc, String term) {
	    double result = 0;
	    for (String word : doc) {
	       if (term.equals(word))
	              result++;
	       }
	    return result / doc.size();
	}
	
	public double idf(List<List<String>> docs, String term) {
	    double n = 0;
	    for (List<String> doc : docs) {
	        for (String word : doc) {
	            if (term.equals(word)) {
	                n++;
	                break;
	            }
	        }
	    }
	    return 1 + Math.log(docs.size() / n);
	}
	
	public double tfIdf(List<String> doc, List<List<String>> docs, String term) {
		//System.out.println("For word " + term + " TF is " + tf(doc, term) + " and IDF is " + idf(docs, term));
	    return tf(doc, term) * idf(docs, term);
	}
	
	public HashMap<Integer, List<Double>> calculateSimilarityScore(List<String> docs, List<Integer> ids) {
		List<String> bagOfWords = new ArrayList<String>();
		List<List<String>> docTermsList = new ArrayList<List<String>>();
		
		// Create bag of words from all documents
		for (String doc : docs) {
			String[] tokenizedTerms = doc.toLowerCase().replaceAll("[^a-zA-Z ]", " ").trim().split("\\W+");
			List<String> docTerms = new ArrayList<String>();
		    for (String term : tokenizedTerms) {
		    	if (!bagOfWords.contains(term)) {
		    		bagOfWords.add(term);
		    	}
		    	docTerms.add(term); // Add each doc's terms to a respective list
		    }
		    docTermsList.add(docTerms);
		}
		
		/*
		// Display bag of words
		System.out.println("Bag of words has the following words:");
		for (String word : bagOfWords) {
			System.out.println(bagOfWords.indexOf(word) + ". " + word);
		}
		
		// Display each document's terms
		for (List<String> list : docTermsList) {
			System.out.println(docTermsList.indexOf(list) + " has the following terms:");
			for (String term : list) {
				System.out.println(term);
			}
		}
		*/
		
		List<List<Double>> vectors = new ArrayList<List<Double>>();
		
		// Initialize document vectors
		for (int i = 0; i < docs.size(); i++) {
			List<Double> vec = new ArrayList<Double>();
			vectors.add(vec);
		}
		
		// Create vector for each document based on TFxIDF scores
		for (String word : bagOfWords) {
			for (int i = 0; i < docs.size(); i++) {
				double value = 0.0;
				if (docTermsList.get(i).contains(word)) {
					value = tfIdf(docTermsList.get(i), docTermsList, word);
				}
				vectors.get(i).add(value);
			}
		}
		
		/*
		// Display document vectors
		for (int i = 0; i < docs.size(); i++) {
			System.out.println("-----------------------------------------------------------------");
			for (int j = 0; j < bagOfWords.size(); j++) {
				System.out.println(j + " - " + bagOfWords.get(j) + " ------ " + vectors.get(i).get(j));
			}
		}
		*/
		
		// Calculate cosine similarity between each document and all other documents
		double[][] scores = new double[docs.size()][docs.size()];
		
		for (int i = 0; i < docs.size(); i++)
			scores[i][i] = -1;
		
		for (int i = 0; i < docs.size()-1; i++) {
			List<Double> currentVec = vectors.get(i);
			for (int j = i+1; j < docs.size(); j++) {
				scores[i][j] = cosineSimilarity(currentVec, vectors.get(j), bagOfWords.size());
			}
		}
		
		for (int i = 1; i < docs.size(); i++) {
			for (int j = 0; j < i; j++) {
				scores[i][j] = scores[j][i];
			}
		}
		
		// Link each review id with its associated similarity scores with other reviews
		HashMap<Integer, List<Double>> reviewsCS = new HashMap<Integer, List<Double>>();
		for (int i = 0; i < docs.size(); i++) {
			List<Double> reviewCSlist = new ArrayList<Double>();
			
			for (int j = 0; j < docs.size(); j++) {
				if (scores[i][j] != -1.0) {
					reviewCSlist.add(scores[i][j]);
				}
			}
			
			reviewsCS.put(ids.get(i), reviewCSlist);
		}
		
		/*
		// Display each review id with its associated similarity scores with other reviews
		for (HashMap.Entry<Integer, List<Double>> entry : reviewsCS.entrySet()) {
			System.out.println("Review with ID " + entry.getKey() + " has similarity scores:");
			for (Double score : entry.getValue()) {
				System.out.println(score);
			}
		}
		*/
		
		return reviewsCS;
	}

}
