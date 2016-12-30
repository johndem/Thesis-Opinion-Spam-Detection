import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

public class SpamDetector {
	
	protected MongoDB mongo;
	
	private List<Review> reviewList; // List storing all reviews
	private int ReviewIdIterator;
	
	public SpamDetector() {
		mongo = new MongoDB();
		reviewList = new ArrayList<Review>();
		ReviewIdIterator = 0;
	}
	
	private void readReviewInput() {
		FindIterable<Document> iterable = mongo.retrieveProductReviews("B000059H9C");
		
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				double rating = Double.parseDouble(document.get("rating").toString());
				String reviewerId = document.get("userid").toString();
				String reviewerText = document.get("content").toString();
				String creationDate = document.get("date").toString();
				
				ReviewIdIterator++;
				
				reviewList.add(new Review(ReviewIdIterator, reviewerId, rating, creationDate, reviewerText));
			}
		});
	}
	
	public void performSpamDetection() {
		readReviewInput();
	}
	
	

}
