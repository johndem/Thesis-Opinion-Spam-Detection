import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

public class RatingDeviation extends SpamDetector {
	
	private List<Review> reviewList; // List storing all reviews
	private double meanRating; // Mean rating of all reviews of a product
	private int numOfReviews; // Number of reviews of a product
	private int scale; // Rating scale (1-5), (1-10), etc.
	
	public RatingDeviation(int scale) {
		reviewList = new ArrayList<Review>();
		meanRating = 3.7741935;
		numOfReviews = 31;
		this.scale = scale;
		
		
		FindIterable<Document> iterable = mongo.retrieveProductReviews("B000059H9C");
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				double rating = Double.parseDouble(document.get("rating").toString());
				System.out.println(rating);
				
				String reviewerId = document.get("userid").toString();
				String reviewerText = document.get("content").toString();
				String creationDate = document.get("date").toString();
				
				meanRating = meanRating + rating;
				numOfReviews++;
				reviewList.add(new Review(numOfReviews, reviewerId, rating, creationDate, reviewerText));
			}
		});
		
	}
	
	public void analyzeRating() {
		System.out.println("Overall mean rating on this set of " + numOfReviews + " reviews is: " + meanRating);
		
		for (Review review : reviewList) {
			double rating = review.getRating();
			meanRating = (meanRating - rating) / (numOfReviews - 1);
			double deviation = Math.abs(meanRating - rating);
			double normalDeviation = deviation / (scale-1);
			System.out.println("Rating " + rating + " has a deviation of " + deviation + " (norm. " + normalDeviation + ")");
		}
		
//		FindIterable<Document> iterable = mongo.retrieveProductReviews("B000059H9C");
//		iterable.forEach(new Block<Document>() {
//			@Override
//			public void apply(final Document document) {
//				double rating = Double.parseDouble(document.get("rating").toString());
//				meanRating = (meanRating - rating) / (numOfReviews - 1);
//				double deviation = Math.abs(meanRating - rating);
//				double normalDeviation = deviation / (scale-1);
//				System.out.println("Rating " + rating + " has a deviation of " + deviation + " (norm. " + normalDeviation + ")");
//			}
//		});
	}

}
