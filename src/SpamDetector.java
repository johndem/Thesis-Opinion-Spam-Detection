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
	
	protected MongoDB mongo;
	
	private List<Review> reviewList; // List storing all reviews
	private HashMap<String, Reviewer> reviewers; // Store reviewer information
	
	private int ReviewIdIterator;
	
	private Classification classifier;
	private RatingDeviation rd;
	private BurstPattern bp;
	
	String randomKey;
	
	public SpamDetector() {
		mongo = new MongoDB();
		reviewList = new ArrayList<Review>();
		reviewers = new HashMap<String, Reviewer>();
		ReviewIdIterator = 0;
		
		classifier = new Classification(1);
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
			
		
//		for (HashMap.Entry<String, Reviewer> entry : reviewers.entrySet()) {
//			System.out.println(entry.getKey());
//			for (int i = 0; i < entry.getValue().getReviews().size(); i++) {
//				System.out.println(entry.getValue().getReviews().get(i).getRating() + " -> " + entry.getValue().getReviews().get(i).getTestDate() + " (" + entry.getValue().getReviews().get(i).getId() + " - " + entry.getValue().getReviews().get(i).getReviewerId() + ")");
//			}
//		}

		
		// Collect each reviewer's reviewing history
		//for (HashMap.Entry<String, Reviewer> entry : reviewers.entrySet()) {
		//FindIterable<Document> iterable = mongo.retrieveUserReviews(entry.getKey());
		long startTime = System.nanoTime();
		
		FindIterable<Document> iterable = mongo.retrieveUserReviews("A1B2ZJQTVK7BWX");
		
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String creationDate = document.get("date").toString();
				double rating = Double.parseDouble(document.get("rating").toString());
				String product_id = document.get("pid").toString();
				
				//entry.getValue().addToHistory(new Review(rating, creationDate, product_id));
				reviewers.get("A1B2ZJQTVK7BWX").addToHistory(new Review(rating, creationDate, product_id));
			}
		});
		//}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
		System.out.println("Extracting history duration: " + duration);
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
					
					reviewList.get(entry.getKey()).setContentSimilarityInBurst(reviewSimilarityScore);
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
			
			review.calculateReviewSpamScore();
		}
		
		
		rd = new RatingDeviation(5, reviewList);
		
		// Analyze each reviewer's activity and assign spam score
		for (HashMap.Entry<String, Reviewer> entry : reviewers.entrySet()) {
			
			// Perform rating deviation analysis
			entry.getValue().setAvgRatingDeviation(rd.analyzeRatings(entry.getValue().getReviews()));
			
			
			// Check similarity between each reviewer's reviews
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
			double reviewerSimilarityScore = 0.0;
			int counter = 0;
			for (HashMap.Entry<Integer, List<Double>> reviewEntry : reviewsCS.entrySet()) {
				for (Double score : reviewEntry.getValue()) {
					reviewerSimilarityScore = reviewerSimilarityScore + score;
					counter++;
				}
			}
			reviewerSimilarityScore = reviewerSimilarityScore / counter;
			
			entry.getValue().setReviewContentSimilarity(reviewerSimilarityScore);
//			System.out.println("Overall similarity score of reviewer " + reviewerEntry.getKey() + "'s reviews is " + reviewerSimilarityScore);
			
			
			// Examine reviewer's general activity and reviewing history
			entry.getValue().analyzeReviewingHistory();
		}
		
		
		
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
		
		
		// Perform burst detection
		//List<Interval> intervals = bp.detectBurstPatterns(reviewList);
		
		/*
//		for (Interval interval : intervals) {
//			if (interval.isSuspicious()) {
//				System.out.println(interval.getStartDate() + " - " + interval.getEndDate());
//			}
//		}
		
		// For every reviewer compare their reviews with the reviews found in bursts
		for (HashMap.Entry<String, Reviewer> entry : reviewers.entrySet()) {
			if (entry.getValue().getReviews().size() > 1) {
				for (int i = 0; i < entry.getValue().getReviews().size(); i++) {
					for (Interval interval : intervals) {
						// Count reviewer's total reviews in suspicious/bursty intervals
						if (interval.isSuspicious() && interval.getReviews().contains(entry.getValue().getReviews().get(i))) {
							entry.getValue().incrementBurstyReviews();
							System.out.println(entry.getKey() + " -- " + entry.getValue().getReviews().get(i).getRating() + " - " + entry.getValue().getReviews().get(i).getTestDate());
						}
					}
				}
			}
		}
		
//		for (int i = 0; i < intervals.get(4).getReviews().size(); i++) {
//			System.out.println(reviewList.get(intervals.get(4).getReviews().get(i)).getId() + " - " + reviewList.get(intervals.get(4).getReviews().get(i)).getTestDate());
//		}
		*/
		
		/*
		// Perform content similarity check
		ContentSimilarity cs = new ContentSimilarity();
		
//		List<String> test = new ArrayList<String>();
//		test.add("The game of life is a game of everlasting learning");
//		test.add("The unexamined game of life is only for learning");
//		test.add("Never stop learning");
//		
//		List<Integer> ids = new ArrayList<Integer>();
//		ids.add(12);
//		ids.add(3);
//		ids.add(2);
		
		// Check similarity between each reviewer's reviews
		for (HashMap.Entry<String, Reviewer> reviewerEntry : reviewers.entrySet()) {
			// Collect reviewer's reviews for a given product
			List<String> reviewContents = new ArrayList<String>();
			List<Integer> ids = new ArrayList<Integer>();
			for (int i = 0; i < reviewerEntry.getValue().getReviews().size(); i++) {
				reviewContents.add(reviewerEntry.getValue().getReviews().get(i).getReviewText());
				ids.add(1);
			}
			
			// Calculate similarity scores
			HashMap<Integer, List<Double>> reviewsCS = cs.calculateSimilarityScore(reviewContents, ids);
			
			// Get the average similarity score for the reviewer's content
			double reviewerSimilarityScore = 0.0;
			int counter = 0;
			for (HashMap.Entry<Integer, List<Double>> reviewEntry : reviewsCS.entrySet()) {
				for (Double score : reviewEntry.getValue()) {
					reviewerSimilarityScore = reviewerSimilarityScore + score;
					counter++;
				}
			}
			reviewerSimilarityScore = reviewerSimilarityScore / counter;
			
			reviewerEntry.getValue().setReviewContentSimilarity(reviewerSimilarityScore);
//			System.out.println("Overall similarity score of reviewer " + reviewerEntry.getKey() + "'s reviews is " + reviewerSimilarityScore);
		}
		
		// Check similarity between reviews of a burst
		for (Interval interval : intervals) {
			if (interval.isSuspicious()) {
				List<String> reviewContents = new ArrayList<String>();
				List<Integer> ids = new ArrayList<Integer>();
				for (int i = 0; i < interval.getReviews().size(); i++) {
					reviewContents.add(interval.getReviews().get(i).getReviewText());
					ids.add(interval.getReviews().get(i).getId());
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
					
					reviewList.get(entry.getKey()).setContentSimilarityInBurst(reviewSimilarityScore);
				}
				
			}
		}
		*/		
		
		/*
		// Calculate reviewing burstiness for each reviewer
		for (HashMap.Entry<String, Reviewer> entry : reviewers.entrySet()) {
			entry.getValue().measureReviewingBurstiness();
		}
		//reviewers.get(randomKey).measureReviewingBurstiness();
		*/
		
		//reviewers.get("A1B2ZJQTVK7BWX").analyzeReviewingHistory();
		
	}
	
	

}
