import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BurstPattern {
	
	private int numOfReviews; // Number of reviews of a product
	private int len; // Duration (in days) from first created review to newest
	private int dt;	// Window size to divide time into intervals
	private int numOfIntervals;	// Number of intervals in the product review timeline
	private int avgReviewsInt; // Average expected number of reviews per interval
	
	int count = 0;
	
	public BurstPattern() {
		len = 0;
		dt = 14;
	}
	
	public List<Interval> detectBurstPatterns(List<Review> reviews) {
	
		numOfReviews = reviews.size();

		/*
		// Display product reviews
		for (Review review : reviews) {
	    	System.out.println(review.getId() + "\t" + review.getRating() + "\t" + review.getDate());
	    }
		*/
		
		len = (int) ChronoUnit.DAYS.between(reviews.get(0).getDate(), reviews.get(reviews.size()-1).getDate());
    	//System.out.println("Duration (len) from: " + reviews.get(0).getDate() + " to " + reviews.get(reviews.size()-1).getDate() + " is " + len);
    	
    	numOfIntervals = len / dt;
    	//System.out.println("Number of intevals: " + numOfIntervals);
    	
    	avgReviewsInt = numOfReviews / numOfIntervals;
    	//System.out.println("Average number of reviews per inteval: " + avgReviewsInt);
    	
    	
    	List<Interval> intervals = new ArrayList<Interval>();
    	
    	// Model and store intervals of product's timeline determining start and end date
    	for (int i = 1; i <= numOfIntervals; i++) {
    		if (i == 1) {
    			intervals.add(new Interval(i, reviews.get(0).getDate(), reviews.get(0).getDate().plusDays(dt-1)));
    		}
    		else if (i == numOfIntervals) {
    			intervals.add(new Interval(i, intervals.get(i-2).getEndDate().plusDays(1), reviews.get(reviews.size()-1).getDate()));
    		}
    		else {
    			intervals.add(new Interval(i, intervals.get(i-2).getEndDate().plusDays(1), intervals.get(i-2).getEndDate().plusDays(dt)));
    		}
    	}
    	
    	
    	// Count amount of reviews for each interval
    	for (int j = 0; j < reviews.size(); j++) {
    		for (int i = 0; i < intervals.size(); i++) {
    			if (reviews.get(j).getDate().isEqual(intervals.get(i).getStartDate()) || reviews.get(j).getDate().isEqual(intervals.get(i).getEndDate()) || 
    					(reviews.get(j).getDate().isAfter(intervals.get(i).getStartDate()) && reviews.get(j).getDate().isBefore(intervals.get(i).getEndDate()))) {
    				reviews.get(j).setId(j);
    				intervals.get(i).addReview(reviews.get(j));
    				break;
    			}
    		}
    	}
//    	for (Review review : reviews) {
//    		for (int i = 0; i < intervals.size(); i++) {
//    			if (review.getDate().isEqual(intervals.get(i).getStartDate()) || review.getDate().isEqual(intervals.get(i).getEndDate()) || 
//    					(review.getDate().isAfter(intervals.get(i).getStartDate()) && review.getDate().isBefore(intervals.get(i).getEndDate()))) {
//    				intervals.get(i).addReview(review);
//    				break;
//    			}
//    		}
//    	}
    	
    	/*
    	// Display detected intervals
    	for (Interval interval : intervals) {
	    	System.out.println(interval.getIntervalId() + "\t" + interval.getStartDate() + "\t" +interval.getEndDate() + "\t" + interval.getReviewSum());
	    	//intervals.get(0).setAsSuspicious();
    	}
    	*/
    	
    	// Check if first interval's number of reviews exceeds that of its neighbor
    	if (intervals.get(0).getReviewSum() > intervals.get(1).getReviewSum() && intervals.get(0).getReviewSum() > avgReviewsInt) {
    		//System.out.println("Interval " + intervals.get(0).getIntervalId() + " matches the pattern!");
    		intervals.get(0).setAsSuspicious();
    	}
    	
    	// Iterate each interval and check if its number of reviews exceeds that of its neighboring intervals
    	for (int i = 1; i < intervals.size()-1; i++) {
    		if (intervals.get(i).getReviewSum() > intervals.get(i-1).getReviewSum() && intervals.get(i).getReviewSum() > intervals.get(i+1).getReviewSum() && intervals.get(i).getReviewSum() > avgReviewsInt) {
    			//System.out.println("Interval " + intervals.get(i).getIntervalId() + " matches the pattern!");
    			intervals.get(i).setAsSuspicious();
    		}
    	}
    	
    	// Check if last interval's number of reviews exceeds that of its neighbor
    	if (intervals.get(intervals.size()-1).getReviewSum() > intervals.get(intervals.size()-2).getReviewSum() && intervals.get(intervals.size()-1).getReviewSum() > avgReviewsInt) {
    		//System.out.println("Interval " + intervals.get(intervals.size()-1).getIntervalId() + " matches the pattern!");
    		intervals.get(intervals.size()-1).setAsSuspicious();
    	}
    	
    	return intervals;
	}

}
