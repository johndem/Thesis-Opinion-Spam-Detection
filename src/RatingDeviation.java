import java.util.List;

public class RatingDeviation {
	
	private double meanRating; // Mean rating of all reviews of a product
	private int numOfReviews; // Number of reviews of a product
	private int scale; // Rating scale (1-5), (1-10), etc.
	
	public RatingDeviation(int scale, List<Review> reviews) {
		meanRating = 0.0;
		numOfReviews = reviews.size();
		this.scale = scale;
		
		calculateMeanRating(reviews);
	}
	
	public void calculateMeanRating(List<Review> reviews) {
		for (Review review : reviews) {
			meanRating = meanRating + review.getRating();
		}
	}
	
	public double getMeanRating() {
		return meanRating / numOfReviews;
	}
	
	public double analyzeRatings(List<Review> reviews, double score) {
		/*
		double totalNormDeviation = 0.0;
		double newMeanRating = meanRating;
		for (Review review : reviews) {
			newMeanRating = newMeanRating - review.getRating();
		}
		
		if (numOfReviews - reviews.size() != 0) {
			newMeanRating = newMeanRating / (numOfReviews - reviews.size());
			
			for (Review review : reviews) {
				double rating = review.getRating();
				double deviation = Math.abs(newMeanRating - rating);
				double normalDeviation = deviation / (scale-1);
				totalNormDeviation = totalNormDeviation + normalDeviation;
				//System.out.println("Rating " + rating + " has a deviation of " + deviation + " (norm. " + normalDeviation + ")");
			}
			
			totalNormDeviation = totalNormDeviation / reviews.size();
		}
		*/
		
		double normalDeviation = 0.0;
		double newMeanRating = meanRating;
		
		for (Review review : reviews) {
			newMeanRating = newMeanRating - review.getRating();
		}
		
		if (numOfReviews - reviews.size() != 0) {
			newMeanRating = newMeanRating / (numOfReviews - reviews.size());
			
			double deviation = Math.abs(newMeanRating - score);
			normalDeviation = deviation / (scale-1);
		}
			
		return normalDeviation; // Instead of total
	}

}
