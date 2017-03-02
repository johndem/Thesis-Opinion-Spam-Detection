import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Reviewer {
	
	private double spamicity;
	private double historyScore;
	
	private List<Review> reviews;
	
	private double avgRatingDeviation;
	private double reviewContentSimilarity;
	
	private int totalBurstyReviews;
	
	private double burstyReviewer;
	private double averateReviewsPerProduct;
	private double exRatingRatio;
	private double histReviewContentSimilarity;
	
	
	private List<Review> reviewingHistory;
	private DateTimeFormatter formatter;
	
	private int normalActivityDuration = 30;
	
	public Reviewer() {
		spamicity = 0.0;
		historyScore = 0;
		reviews = new ArrayList<Review>();
		
		reviewContentSimilarity = 0.0;
		burstyReviewer = 0.0;
		totalBurstyReviews = 0;
		exRatingRatio = 0.0;
		histReviewContentSimilarity = 0.0;
		averateReviewsPerProduct = 0.0;
		
		reviewingHistory = new ArrayList<Review>();
		formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
	}
	
	public List<Review> getReviews() {
		return reviews;
	}
	
	public void addReview(Review review) {
		reviews.add(review);
	}
	
	public void setAvgRatingDeviation(double dev) {
		avgRatingDeviation = dev;
	}
	
	public double getAvgRatingDeviation() {
		return avgRatingDeviation;
	}
	
	public void incrementBurstyReviews() {
		totalBurstyReviews++;
	}
	
	public void setReviewContentSimilarity(double reviewContentSimilarity) {
		this.reviewContentSimilarity = reviewContentSimilarity;
	}
	
	public void addToHistory(Review review) {
		reviewingHistory.add(review);
	}
	
	private void measureReviewingBurstiness() {
		// Sort review dates in reviewer's history from oldest to latest
		Collections.sort(reviewingHistory, new Comparator<Review>() {
			  public int compare(Review o1, Review o2) {
				  if (o1.getDate() == null || o2.getDate() == null)
					  return 0;
			      return o1.getDate().compareTo(o2.getDate());
			  }
		});
		
		//System.out.println("Reviewer has written " + reviewingHistory.size() + " reviews.");
		
		// Calculate duration between reviewer's first and last review
		int daysOfActivity = (int) ChronoUnit.DAYS.between(reviewingHistory.get(0).getDate(), reviewingHistory.get(reviewingHistory.size()-1).getDate());
		if (reviews.size() > 5 && daysOfActivity < normalActivityDuration) {
			burstyReviewer = 1.0;
		}
		
		//System.out.println("Reviewer has " + daysOfActivity + " days of activity.");
	}
	
	private void extractAverageProliferation() {
		HashMap<String, Integer> reviewsPerProduct = new HashMap<String, Integer>();
		
		for (Review review : reviewingHistory) {
			if (reviewsPerProduct.containsKey(review.getProductId())) {
				int value = reviewsPerProduct.get(review.getProductId());
				value++;
				reviewsPerProduct.put(review.getProductId(), value);
			}
			else {
				reviewsPerProduct.put(review.getProductId(), 1);
			}
		}
		
		int sum = 0;
		for (HashMap.Entry<String, Integer> entry : reviewsPerProduct.entrySet()) {
			//System.out.println(entry.getKey() + " - " + entry.getValue());
			sum = sum + entry.getValue();
		}
		
		if (reviews.size() != reviewingHistory.size())
			averateReviewsPerProduct = (double) sum / reviewsPerProduct.size();
		else
			averateReviewsPerProduct = 1.0;
		
		//System.out.println("Reviewer writes on average " + averateReviewsPerProduct + " reviews per product.");
	}
	
	private void calculateRatingExtremity() {
		int extremeRatings = 0;
		for (Review review : reviewingHistory) {
			if (review.getRating() == 1.0 || review.getRating() == 5.0)
				extremeRatings++;
		}
		
		exRatingRatio = (double) extremeRatings / reviewingHistory.size();
		
		//System.out.println("Reviewer has an extreme rating ratio of " + exRatingRatio);
	}
	
	private void measureHistoryContentSimilarity() {
		if (reviewingHistory.size() > 1) {
			List<String> reviewContents = new ArrayList<String>();
			List<Integer> ids = new ArrayList<Integer>();
			// Collect reviewer's reviews for a given product
			for (int i = 0; i < reviewingHistory.size(); i++) {
				reviewContents.add(reviewingHistory.get(i).getReviewText());
				ids.add(1);
			}
			
			// Calculate similarity scores
			HashMap<Integer, List<Double>> reviewsCS = ContentSimilarity.calculateSimilarityScore(reviewContents, ids);
			
			// Get the average similarity score for the reviewer's content
			int counter = 0;
			for (HashMap.Entry<Integer, List<Double>> reviewEntry : reviewsCS.entrySet()) {
				for (Double score : reviewEntry.getValue()) {
					histReviewContentSimilarity = histReviewContentSimilarity + score;
					counter++;
				}
			}
			histReviewContentSimilarity = histReviewContentSimilarity / counter;
		}
	}
	
	public double analyzeReviewingHistory() {
		//printReviewerHist();
		
		measureReviewingBurstiness();
		
		extractAverageProliferation();
		
		calculateRatingExtremity();
		
		historyScore = burstyReviewer + 0.5 * averateReviewsPerProduct + 0.5 * exRatingRatio;
		return historyScore;
	}
	
	public void setHistoryScore(double score) {
		historyScore = score;
	}
	
	public void printReviewerHist() {
		// Display reviewing history
		for (Review review : reviewingHistory) {
			System.out.println(review.getProductId() + "	" + review.getRating() + "	" + review.getDate());
		}
	}
	
	public double getHistoryScore() {
		return historyScore;
	}
	
	public void measureReviewerSpamicity() {
		if (reviews.size() < 3)
			totalBurstyReviews = 0;
		
		spamicity = 0.125 * avgRatingDeviation + 1.5 * reviewContentSimilarity + 1.5 * ((double) totalBurstyReviews / reviews.size()) + 0.5 * reviews.size() + historyScore;
	}
	
	public double getSpamicity() {
		return spamicity;
	}
	
	public void printReviewingStats() {
		System.out.println("Average Rating Deviation: " + avgRatingDeviation);
		System.out.println("Review Content Similarity: " + reviewContentSimilarity);
		System.out.println("Number of Reviews: " + reviews.size());
		System.out.println("Ratio of Bursty Reviews: " + totalBurstyReviews + " / " + reviews.size() + " = " + ((double) totalBurstyReviews / reviews.size()));
		System.out.println("(H) Bursty Reviewing: " + burstyReviewer);
		System.out.println("(H) Average Number of Reviews per Product: " + averateReviewsPerProduct);
		System.out.println("(H) Extreme Rating Ratio: " + exRatingRatio);
		System.out.println("H: " + historyScore + " (" + reviewingHistory.size() + ")");
	}

}
