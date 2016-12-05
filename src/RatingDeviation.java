import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

public class RatingDeviation extends SpamDetector {
	
	private double meanRating;
	private int numOfReviews;
	
	public RatingDeviation() {
		meanRating = 3.7741935;
		numOfReviews = 31;
		
		/*
		FindIterable<Document> iterable = mongo.retrieveProductReviews("B000059H9C");
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				System.out.println(document.get("rating").toString());
				meanRating = meanRating + Double.parseDouble(document.get("rating").toString());
				numOfReviews++;
			}
		});
		
		meanRating = meanRating / numOfReviews;
		*/
	}
	
	public void analyzeRating() {
		System.out.println("Mean rating on this set of " + numOfReviews + " reviews is: " + meanRating);
		
		FindIterable<Document> iterable = mongo.retrieveProductReviews("B000059H9C");
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				double deviation = Math.abs(meanRating - Double.parseDouble(document.get("rating").toString()));
				System.out.println("Rating " + document.get("rating").toString() + " has a deviation of " + deviation);
			}
		});
	}

}
