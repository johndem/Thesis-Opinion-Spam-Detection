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
	
	private List<Review> reviews;
	
	private double reviewsScore;
	
	private double avgRatingDeviation;
	private double reviewContentSimilarity;
	
	private int totalBurstyReviews;
	
	private double burstyReviewer;
	private double averateReviewsPerProduct;
	private double exRatingRatio;
	
	
	private List<Review> reviewingHistory;
	private DateTimeFormatter formatter;
	
	private int normalActivityDuration = 60;
	
	public Reviewer() {
		spamicity = 0.0;
		reviews = new ArrayList<Review>();
		
		reviewsScore = 0.0;
		reviewContentSimilarity = 0.0;
		burstyReviewer = 0.0;
		totalBurstyReviews = 0;
		exRatingRatio = 0.0;
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
	
	public void addReviewScore(double score) {
		reviewsScore = reviewsScore + score;
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
	
	public void measureReviewingBurstiness() {
		// Sort review dates in reviewer's history from oldest to latest
		Collections.sort(reviewingHistory, new Comparator<Review>() {
			  public int compare(Review o1, Review o2) {
				  if (o1.getDate() == null || o2.getDate() == null)
					  return 0;
			      return o1.getDate().compareTo(o2.getDate());
			  }
		});
		
		System.out.println("Reviewer has written " + reviewingHistory.size() + " reviews.");
		
		// Calculate duration between reviewer's first and last review
		int daysOfActivity = (int) ChronoUnit.DAYS.between(reviewingHistory.get(0).getDate(), reviewingHistory.get(reviewingHistory.size()-1).getDate());
		if (daysOfActivity < normalActivityDuration) {
			burstyReviewer = 1.0;
		}
		
		System.out.println("Reviewer has " + daysOfActivity + " days of activity.");
	}
	
	public void extractAverageProliferation() {
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
			System.out.println(entry.getKey() + " - " + entry.getValue());
			sum = sum + entry.getValue();
		}
		 averateReviewsPerProduct = (double) sum / reviewsPerProduct.size();
		
		System.out.println("Reviewer writes on average " + averateReviewsPerProduct + " reviews per product.");
	}
	
	public void calculateRatingExtremity() {
		int extremeRatings = 0;
		for (Review review : reviewingHistory) {
			if (review.getRating() == 1.0 || review.getRating() == 5.0)
				extremeRatings++;
		}
		
		exRatingRatio = (double) extremeRatings / reviewingHistory.size();
		
		System.out.println("Reviewer has an extreme rating ratio of " + exRatingRatio);
	}
	
	public void analyzeReviewingHistory() {
		
		// Display reviewing history and burstiness score
		for (Review review : reviewingHistory) {
			System.out.println(review.getProductId() + "	" + review.getRating() + "	" + review.getDate());
		}
		
		measureReviewingBurstiness();
		
		extractAverageProliferation();
		
		calculateRatingExtremity();
	}
	
	public void measureReviewerSpamicity() {
		reviewsScore = (double) reviewsScore / reviews.size();
		spamicity = reviewsScore + avgRatingDeviation + reviewContentSimilarity + reviews.size() + (totalBurstyReviews / reviews.size()) + burstyReviewer + averateReviewsPerProduct + exRatingRatio;
	}
	
	public double getSpamicity() {
		return spamicity;
	}

}
