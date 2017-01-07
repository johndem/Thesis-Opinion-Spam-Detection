import java.util.ArrayList;
import java.util.List;

public class Reviewer {
	
	private float spamicity;
	private List<Review> reviews;
	private double avgRatingDeviation;
	
	public Reviewer() {
		spamicity = 0;
		reviews = new ArrayList<Review>();
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

}
