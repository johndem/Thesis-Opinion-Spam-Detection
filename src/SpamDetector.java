import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

public class SpamDetector {
	
	protected MongoDB mongo;
	
	private List<Review> reviewList; // List storing all reviews
	private HashMap<String, Reviewer> reviewers; // Store reviewer information
	
	private int ReviewIdIterator;
	
	private RatingDeviation rd;
	private BurstPattern bp;
	
	public SpamDetector() {
		mongo = new MongoDB();
		reviewList = new ArrayList<Review>();
		reviewers = new HashMap<String, Reviewer>();
		ReviewIdIterator = 0;
		
		bp = new BurstPattern();
	}
	
	private void readReviewInput() {
		/*
		FindIterable<Document> iterable = mongo.retrieveProductReviews("1400046610");
		
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				double rating = Double.parseDouble(document.get("rating").toString());
				String reviewerId = document.get("userid").toString();
				String reviewText = document.get("content").toString();
				String creationDate = document.get("date").toString();
				
				ReviewIdIterator++;
				
				reviewList.add(new Review(ReviewIdIterator, reviewerId, rating, creationDate, reviewText));
				
				if (!reviewers.containsKey(reviewerId)) {
					reviewers.put(reviewerId, new Reviewer());
					reviewers.get(reviewerId).addReview(ReviewIdIterator);
				}
				else {
					reviewers.get(reviewerId).addReview(ReviewIdIterator);
				}
				
			}
		});
		
		try{
		    PrintWriter writer = new PrintWriter("testing.txt", "UTF-8");
		    
		    for (Review review : reviewList) {
		    	System.out.println(review.getReviewerId() + "\t" + review.getTestDate() + "\t" + review.getRating());
		    	writer.println(review.getReviewerId() + "\t" + review.getTestDate() + "\t" + review.getRating() + "\t" + review.getReviewText());
		    }
		    
		    writer.close();
		} catch (IOException e) {
		   // do something
		}
		*/
		
		// Read review and reviewer data for a specific product and store in a respective structures
		try (BufferedReader br = new BufferedReader(new FileReader("testing.txt"))) {
		    String line;
		    int counter = 0;
		    while ((line = br.readLine()) != null) {
		    	// Process the line
		    	String[] lineTokens = line.split("\\t");
		    	
		    	String reviewerId = lineTokens[0];
		    	String creationDate = lineTokens[1];
		    	double rating = Double.parseDouble(lineTokens[2]);
		    	String reviewText = lineTokens[3];
		    	
		    	
		    	// Add review to List
		    	Review review = new Review(counter, reviewerId, rating, creationDate, reviewText);
		    	reviewList.add(review);
		    	
		    	if (!reviewers.containsKey(reviewerId)) { // If encounter reviewer for first time, add to HashMap
					reviewers.put(reviewerId, new Reviewer());
					reviewers.get(reviewerId).addReview(review);
				}
				else { // If reviewer already exists, simply add their review
					reviewers.get(reviewerId).addReview(review);
				}
		    	
		    	counter++;
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Sort reviews in List from oldest to latest
		Collections.sort(reviewList, new Comparator<Review>() {
			  public int compare(Review o1, Review o2) {
				  if (o1.getDate() == null || o2.getDate() == null)
					  return 0;
			      return o1.getDate().compareTo(o2.getDate());
			  }
		});
		
//		for (HashMap.Entry<String, Reviewer> entry : reviewers.entrySet()) {
//			System.out.println(entry.getKey());
//			for (int i = 0; i < entry.getValue().getReviews().size(); i++) {
//				System.out.println(entry.getValue().getReviews().get(i).getRating() + " -> " + entry.getValue().getReviews().get(i).getTestDate() + " (" + entry.getValue().getReviews().get(i).getId() + " - " + entry.getValue().getReviews().get(i).getReviewerId() + ")");
//			}
//		}
	}
	
	public void performSpamDetection() {
		readReviewInput();
		
		/*
		// Perform rating deviation analysis on each reviewer
		rd = new RatingDeviation(5, reviewList);
		
		System.out.println("Mean Rating is: " + rd.getMeanRating());
		
		for (HashMap.Entry<String, Reviewer> entry : reviewers.entrySet()) {
			entry.getValue().setAvgRatingDeviation(rd.analyzeRatings(entry.getValue().getReviews()));
			
			System.out.println(entry.getKey() + " has written " + entry.getValue().getReviews().size() + " reviews and has a norm rating deviation of " + entry.getValue().getAvgRatingDeviation());
			System.out.println("Ratings: ");
			for (int i = 0; i < entry.getValue().getReviews().size(); i++) {
				System.out.println(entry.getValue().getReviews().get(i).getRating() + " -> " + entry.getValue().getReviews().get(i).getTestDate() + " (" + entry.getValue().getReviews().get(i).getId() + " - " + entry.getValue().getReviews().get(i).getReviewerId() + ")");
			}
		}
		*/
		System.out.println("HAHA");
		bp.detectBurstPatterns(reviewList);

	}
	
	

}
