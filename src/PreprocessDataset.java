import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.bson.Document;

public class PreprocessDataset {
	
	private MongoDB mongo;
	private static final String filePath = "D:\\Opinion Spam Detection data\\lie_dataset\\reviewsNew.txt"; // "C:\\Users\\John\\Documents\\Πανεπιστήμιο\\Διπλωματική\\Datasets\\Reviews\\reviews.txt"
	
	
	public PreprocessDataset() {
		mongo = new MongoDB();
	}
	
	public void process() {
		
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
		    	
		    	/* Display review information
		    	System.out.println("--------------------------------------------------------");
		    	System.out.println("Reviewer ID: " + reviewerId);
		    	System.out.println("Product ID: " + productId);
		    	System.out.println("Review created on: " + creationDate);
		    	System.out.println("Rating: " + rating);
		    	System.out.println("Content:");
		    	System.out.println(reviewText); */
		    	
		    	if (!reviewerId.equals("") && !productId.equals("") && !creationDate.equals("") && !rating.equals("") && !reviewText.equals("")) {
		    		Document review = new Document();
			    	review.put("pid", productId);
			    	review.put("userid", reviewerId);
			    	review.put("date", creationDate);
			    	review.put("rating", rating);
			    	review.put("content", reviewText);
			    	
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

}
