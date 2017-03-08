import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

public class DatasetInfo {
	
	private MongoDB mongo;
	private HashMap<String, List<String>> reviewsPerAuthor;
	private HashMap<String, List<Double>> authorRatings;
	private int counter;
	
	public DatasetInfo() {
		mongo = new MongoDB();
		reviewsPerAuthor = new HashMap<String, List<String>>();
		authorRatings = new HashMap<String, List<Double>>();
		counter = 0;
	}
	
	public void findAverageReviewerProliferation() {
		FindIterable<Document> iterable = mongo.retrieveReviewsCollection();
		
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String reviewerId = document.get("userid").toString();
				String productId = document.get("pid").toString();
				
				if (!reviewsPerAuthor.containsKey(reviewerId)) {
					List<String> reviews = new ArrayList<String>();
					reviews.add(productId);
					reviewsPerAuthor.put(reviewerId, reviews);
				}
				else {
					reviewsPerAuthor.get(reviewerId).add(productId);
				}
				
			}
		});
		
		double avgProliferation = 0.0;
		for (HashMap.Entry<String, List<String>> entry : reviewsPerAuthor.entrySet()) {
			
			HashMap<String, Integer> reviewsPerProduct = new HashMap<String, Integer>();
			
			for (String product : entry.getValue()) {
				if (reviewsPerProduct.containsKey(product)) {
					int value = reviewsPerProduct.get(product);
					value++;
					reviewsPerProduct.put(product, value);
				}
				else {
					reviewsPerProduct.put(product, 1);
				}
			}
			
			int sum = 0;
			for (HashMap.Entry<String, Integer> entryR : reviewsPerProduct.entrySet()) {
				sum = sum + entryR.getValue();
			}
			avgProliferation = avgProliferation + (double) sum / reviewsPerProduct.size();
			
		}
		
		System.out.println("Average number of reviews that a reviewer creates per product: " + (double) avgProliferation / reviewsPerAuthor.size());
	}
	
	public void findAverageReviewerExtremeRatingRatio() {
		FindIterable<Document> iterable = mongo.retrieveReviewsCollection();
		
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String reviewerId = document.get("userid").toString();
				double rating = Double.parseDouble(document.get("rating").toString());
				
				if (!authorRatings.containsKey(reviewerId)) {
					List<Double> ratings = new ArrayList<Double>();
					ratings.add(rating);
					authorRatings.put(reviewerId, ratings);
				}
				else {
					authorRatings.get(reviewerId).add(rating);
				}
				
			}
		});
		
		double avgExtremeRatingRatio = 0.0;
		for (HashMap.Entry<String, List<Double>> entry : authorRatings.entrySet()) {
			
			int extremeRatings = 0;
			for (Double rating : entry.getValue()) {
				if (rating == 1.0 || rating == 5.0)
					extremeRatings++;
			}
			
			avgExtremeRatingRatio = avgExtremeRatingRatio + (double) extremeRatings / entry.getValue().size();
			
		}
		
		System.out.println("Average extreme rating ratio among authors: " + (double) avgExtremeRatingRatio / authorRatings.size());
	}
	
	public void measureTotalReviews() {
		FindIterable<Document> iterable = mongo.retrieveProductsCollection();
		
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				int reviews = Integer.parseInt(document.get("reviews").toString());
				counter += reviews;
			}
		});
		
		System.out.println("Total amount of reviews: " + counter);
	}

}
