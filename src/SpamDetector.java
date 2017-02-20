import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

public class SpamDetector {
	
	private MongoDB mongo;
	private String productToFilter;
	
	private List<Review> reviewList; // List storing all reviews
	private HashMap<String, Reviewer> reviewers; // Store reviewer information
	
	private Classification classifier;
	private RatingDeviation rd;
	private BurstPattern bp;
	
	String randomKey;
	
	public SpamDetector(MongoDB mongo, String productToFilter) {
		this.mongo = mongo;
		this.productToFilter = productToFilter;
		
		reviewList = new ArrayList<Review>();
		reviewers = new HashMap<String, Reviewer>();
		
		classifier = new Classification(1);
		bp = new BurstPattern();
	}
	
	private void readReviewInput() {
		
		FindIterable<Document> iterable = mongo.retrieveProductReviews(productToFilter);
		
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String mongo_id = document.get("_id").toString();
				double rating = Double.parseDouble(document.get("rating").toString());
				String reviewerId = document.get("userid").toString();
				String reviewText = document.get("content").toString();
				String creationDate = document.get("date").toString();
				
				// Add review to List
		    	Review review = new Review(mongo_id, reviewerId, rating, creationDate, reviewText);
		    	reviewList.add(review);
				
		    	if (!reviewers.containsKey(reviewerId)) { // If encounter reviewer for first time, add to HashMap
					reviewers.put(reviewerId, new Reviewer());
					reviewers.get(reviewerId).addReview(review);
				}
				else { // If reviewer already exists, simply add their review
					reviewers.get(reviewerId).addReview(review);
				}
				
			}
		});
		
//		try{
//		    PrintWriter writer = new PrintWriter("testing.txt", "UTF-8");
//		    
//		    for (Review review : reviewList) {
//		    	System.out.println(review.getReviewerId() + "\t" + review.getTestDate() + "\t" + review.getRating());
//		    	writer.println(review.getReviewerId() + "\t" + review.getTestDate() + "\t" + review.getRating() + "\t" + review.getReviewText());
//		    }
//		    
//		    writer.close();
//		} catch (IOException e) {
//		   // do something
//		}
		
		/*
		// Read review and reviewer data for a specific product and store in a respective structures
		try (BufferedReader br = new BufferedReader(new FileReader("testing.txt"))) {
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		    	// Process the line
		    	String[] lineTokens = line.split("\\t");
		    	
		    	String reviewerId = lineTokens[0];
		    	String creationDate = lineTokens[1];
		    	double rating = Double.parseDouble(lineTokens[2]);
		    	String reviewText = lineTokens[3];
		    	
		    	
		    	// Add review to List
		    	Review review = new Review(reviewerId, rating, creationDate, reviewText);
		    	reviewList.add(review);
		    	
		    	if (!reviewers.containsKey(reviewerId)) { // If encounter reviewer for first time, add to HashMap
					reviewers.put(reviewerId, new Reviewer());
					reviewers.get(reviewerId).addReview(review);
				}
				else { // If reviewer already exists, simply add their review
					reviewers.get(reviewerId).addReview(review);
				}
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		// Sort reviews in List from oldest to latest
		Collections.sort(reviewList, new Comparator<Review>() {
			  public int compare(Review o1, Review o2) {
				  if (o1.getDate() == null || o2.getDate() == null)
					  return 0;
			      return o1.getDate().compareTo(o2.getDate());
			  }
		});
		
		int counter = 0;
		for (Review review : reviewList) {
			review.setId(counter);
			counter++;
		}

		// Collect each reviewer's reviewing history
		for (HashMap.Entry<String, Reviewer> entry : reviewers.entrySet()) {
			FindIterable<Document> iter = mongo.retrieveUserReviews(entry.getKey());

			iter.forEach(new Block<Document>() {
				@Override
				public void apply(final Document document) {
					String creationDate = document.get("date").toString();
					double rating = Double.parseDouble(document.get("rating").toString());
					String product_id = document.get("pid").toString();
					
					entry.getValue().addToHistory(new Review(rating, creationDate, product_id));
				}
			});
		}
	}
	
	public void performSpamDetection() throws Exception {
		readReviewInput();
		
		ContentSimilarity cs = new ContentSimilarity();
		
		// Perform burst detection
		List<Interval> intervals = bp.detectBurstPatterns(reviewList);
		
		// Check similarity between reviews of a burst
		for (Interval interval : intervals) {
			if (interval.isSuspicious()) {
				List<String> reviewContents = new ArrayList<String>();
				List<Integer> ids = new ArrayList<Integer>();
				for (int i = 0; i < interval.getReviews().size(); i++) {
					reviewContents.add(interval.getReviews().get(i).getReviewText());
					ids.add(interval.getReviews().get(i).getId());
					
					// Increment reviewers' bursty review ratio
					reviewers.get(interval.getReviews().get(i).getReviewerId()).incrementBurstyReviews();
				}
					
				// Calculate similarity scores
				HashMap<Integer, List<Double>> reviewsCS = cs.calculateSimilarityScore(reviewContents, ids);
				
				// Get the average similarity score for the review
				for (HashMap.Entry<Integer, List<Double>> entry : reviewsCS.entrySet()) {
					double reviewSimilarityScore = 0.0;
					int count = 0;
					for (Double score : entry.getValue()) {
						reviewSimilarityScore = reviewSimilarityScore + score;
						count++;
					}
					reviewSimilarityScore = reviewSimilarityScore / count;
					//System.out.println("Overall similarity score of review with ID " + entry.getKey() + " is " + reviewSimilarityScore);
					
					reviewList.get(entry.getKey()).setContentSimilarityInBurst(Math.abs(reviewSimilarityScore - 0.5));
				}
				
			}
		}
		
		
		// Analyze each review body and assign spam score
		for (Review review : reviewList) {
			List<String> reviewToBeClassified = new ArrayList<String>();
			reviewToBeClassified.add(review.getReviewText());
			// Classify review content with MLP
			reviewToBeClassified = classifier.classifyReviews(reviewToBeClassified);
			
			if (reviewToBeClassified.get(0).equals("spam"))
				review.setContentLabel(1.0);
			else
				review.setContentLabel(0.0);
			
			//review.calculateReviewSpamScore();
		}
		
		
		rd = new RatingDeviation(5, reviewList);
		
		// Analyze each reviewer's activity and assign spam score
		for (HashMap.Entry<String, Reviewer> entry : reviewers.entrySet()) {
			
			// Perform rating deviation analysis
			entry.getValue().setAvgRatingDeviation(rd.analyzeRatings(entry.getValue().getReviews()));
			
			// Check similarity between each reviewer's reviews (if created more than one)
			double reviewerSimilarityScore = 0.0;
			if (entry.getValue().getReviews().size() > 1) {
				List<String> reviewContents = new ArrayList<String>();
				List<Integer> ids = new ArrayList<Integer>();
				// Collect reviewer's reviews for a given product
				for (int i = 0; i < entry.getValue().getReviews().size(); i++) {
					reviewContents.add(entry.getValue().getReviews().get(i).getReviewText());
					ids.add(1);
				}
				
				// Calculate similarity scores
				HashMap<Integer, List<Double>> reviewsCS = cs.calculateSimilarityScore(reviewContents, ids);
				
				// Get the average similarity score for the reviewer's content
				int counter = 0;
				for (HashMap.Entry<Integer, List<Double>> reviewEntry : reviewsCS.entrySet()) {
					for (Double score : reviewEntry.getValue()) {
						reviewerSimilarityScore = reviewerSimilarityScore + score;
						counter++;
					}
				}
				reviewerSimilarityScore = reviewerSimilarityScore / counter;
			}
			
			entry.getValue().setReviewContentSimilarity(reviewerSimilarityScore);
			
			
			// Examine reviewer's general activity and reviewing history
			FindIterable<Document> iterable = mongo.retrieveReviewer(entry.getKey());
			iterable.forEach(new Block<Document>() {
				@Override
				public void apply(final Document document) {
					double score = Double.parseDouble(document.get("score").toString());
					
					if (score == 0.0)
						mongo.updateReviewerScore(entry.getKey(), String.valueOf(entry.getValue().analyzeReviewingHistory()));
					else
						entry.getValue().setHistoryScore(score);
				}
			});
			
			
			// Measure reviewer's spam score
			entry.getValue().measureReviewerSpamicity();
		}
		
		
		for (Review review : reviewList)
			mongo.updateReviewScore(review.getMongoId(), String.valueOf(review.calculateReviewSpamScore(reviewers.get(review.getReviewerId()).getSpamicity())));
		
		
//		System.out.println("Number of reviews: " + reviewList.size());
//		System.out.println("Number of reviewers: " + reviewers.size());
		
		// Display review spam scores
		int counter = 1;
		for (Review review : reviewList) {
			//System.out.println(counter + ". " + review.getReviewSpamScore());
			//counter++;
			
			//if (review.getReviewSpamScore() > 3) {
				System.out.println(counter + ". Score: " + review.getReviewSpamScore() + " (" + review.getTestDate() + ")");
				System.out.println("Review stats:");
				review.printReviewStats();
				System.out.println("Reviewer " + review.getReviewerId() + " stats:");
				reviewers.get(review.getReviewerId()).printReviewingStats();
				System.out.println("------------------------------------------------------");
				counter++;
			//}
			
		}
		
		/*
		// Display reviewer spam scores
		int counter = 1;
		for (HashMap.Entry<String, Reviewer> entry : reviewers.entrySet()) {
			if (entry.getValue().getSpamicity() > 7.0) {
				System.out.println(counter + ". Reviewer " + entry.getKey() + " has a score of: " + entry.getValue().getSpamicity());
				entry.getValue().printReviewingStats();
				System.out.println("-------------------------------------------------------------------");
				counter++;
				
//				if (entry.getKey().equals("A6WXVSLYV2F45") || entry.getKey().equals("A1C9ZY8FRWQ0TX")) {
//					System.out.println(counter + ". Reviewer " + entry.getKey() + " has a score of: " + entry.getValue().getSpamicity());
//					entry.getValue().printReviewingStats();
//					entry.getValue().printReviewerHist();
//					System.out.println("-------------------------------------------------------------------");
//					counter++;
//				}
				
			}
			
		}
		*/
		
	}	

}
