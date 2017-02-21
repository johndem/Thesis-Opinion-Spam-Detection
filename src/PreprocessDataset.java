import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bson.Document;

public class PreprocessDataset {
	
	private MongoDB mongo;
	private static final String filePath = "D:\\Opinion Spam Detection data\\lie_dataset\\reviewsNew.txt"; // "C:\\Users\\John\\Documents\\Πανεπιστήμιο\\Διπλωματική\\Datasets\\Reviews\\reviews.txt"
	
	
	public PreprocessDataset() {
		mongo = new MongoDB();
	}
	
	public void processReviews() {
		
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		    	// Process the line
		    	String[] lineTokens = line.split("\\t");
		    	
		    	String reviewerId = lineTokens[0];
		    	String productId = lineTokens[1];
		    	String creationDate = lineTokens[2];
		    	String rating = lineTokens[5];
		    	String reviewText = lineTokens[7].trim();
		    	
		    	if (!reviewerId.equals("") && !productId.equals("") && !creationDate.equals("") && !rating.equals("") && !reviewText.equals("")) {
		    		Document review = new Document();
			    	review.put("pid", productId);
			    	review.put("userid", reviewerId);
			    	review.put("date", creationDate);
			    	review.put("rating", rating);
			    	review.put("content", reviewText);
			    	review.put("score", 0.0);
			    	
			    	mongo.insertReview(review);
		    	}
		    	else
		    		continue;
	
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void processReviewers() {
		
		HashSet<String> reviewerSet = new HashSet<String>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		    	// Process the line
		    	String[] lineTokens = line.split("\\t");
		    	
		    	String reviewerId = lineTokens[0];
		    	String productId = lineTokens[1];
		    	String creationDate = lineTokens[2];
		    	String rating = lineTokens[5];
		    	String reviewText = lineTokens[7].trim();
		    	
		    	if (!reviewerId.equals("") && !productId.equals("") && !creationDate.equals("") && !rating.equals("") && !reviewText.equals(""))
		    		reviewerSet.add(reviewerId);
		    	else
		    		continue;
		    }
		    
		    for (String reviewer : reviewerSet) {
		    	Document doc = new Document();
		    	doc.put("userid", reviewer);
		    	doc.put("score", "0.0");
		    	
		    	mongo.insertReviewer(doc);
		    }
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void processProducts() {
		
		HashMap<String, Integer> productSet = new HashMap<String, Integer>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		    	// Process the line
		    	String[] lineTokens = line.split("\\t");
		    	
		    	String reviewerId = lineTokens[0];
		    	String productId = lineTokens[1];
		    	String creationDate = lineTokens[2];
		    	String rating = lineTokens[5];
		    	String reviewText = lineTokens[7].trim();

		    	
		    	if (!reviewerId.equals("") && !productId.equals("") && !creationDate.equals("") && !rating.equals("") && !reviewText.equals("")) {
		    		if (!productSet.containsKey(productId)) {
		    			productSet.put(productId, 1);
		    		}
		    		else {
		    			int counter = productSet.get(productId);
						counter++;
						productSet.put(productId, counter);
		    		}
		    	}	
		    	else
		    		continue;
		    }
		    
		    for (HashMap.Entry<String, Integer> product : productSet.entrySet()) {
	    		Document doc = new Document();
		    	doc.put("pid", product.getKey());
		    	doc.put("reviews", product.getValue());
		    	
		    	mongo.insertProduct(doc);
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
