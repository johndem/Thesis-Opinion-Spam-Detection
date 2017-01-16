import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Reviewer {
	
	private float spamicity;
	private List<Review> reviews;
	private double avgRatingDeviation;
	
	private List<LocalDate> reviewingHistory;
	private DateTimeFormatter formatter;
	
	public Reviewer() {
		spamicity = 0;
		reviews = new ArrayList<Review>();
		
		reviewingHistory = new ArrayList<LocalDate>();
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
	
	public void addToHistory(String date) {
		reviewingHistory.add(LocalDate.parse(date, formatter));
	}
	
	public void measureReviewingBurstiness() {
		// Sort review dates in reviewer's history from oldest to latest
		Collections.sort(reviewingHistory, new Comparator<LocalDate>() {
			  public int compare(LocalDate o1, LocalDate o2) {
				  if (o1 == null || o2 == null)
					  return 0;
			      return o1.compareTo(o2);
			  }
		});
		
		System.out.println("Reviewer has written " + reviewingHistory.size() + " reviews.");
		
		// Calculate duration between reviewer's first and last review
		int daysOfActivity = (int) ChronoUnit.DAYS.between(reviewingHistory.get(0), reviewingHistory.get(reviewingHistory.size()-1));
		
		/*
		// Display reviewing history and burstiness score
		for (LocalDate date : reviewingHistory) {
			System.out.println(date);
		}
		System.out.println("Reviewer has " + daysOfActivity + " days of activity.");
		*/
	}

}
