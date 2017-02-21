import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

public class ReviewFilter {
	
	private MongoDB mongo;

	public ReviewFilter() {
		mongo = new MongoDB();
	}
	
	public void filterProductReviews() throws IOException {
//		FindIterable<Document> iterable = mongo.retrieveProductsCollection().noCursorTimeout(true);
//		iterable.forEach(new Block<Document>() {
//			@Override
//			public void apply(final Document document) {
//				String product_id = document.get("pid").toString();
//				String numOfReviews = document.get("#reviews").toString();
//				//System.out.println("Annotating reviews for product: " + product_id);
//				
//				try {
//					new SpamDetector(mongo, product_id).performSpamDetection();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//		});
		
		
		
		// Extract top-K and bottom-K reviews for review text classification evaluation purposes
		Results res = new Results();
		int K = 10000;
		
		// Collect top K reviews with highest spam score to be used as spam class
		List<String> topKreviews = new ArrayList<String>();
		FindIterable<Document> iter = mongo.retrieveTopKDocuments(K);
		iter.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String content = document.get("content").toString();
				double score = Double.parseDouble(document.get("score").toString());
				//System.out.println(score);
				if (!topKreviews.contains(content)) // Omit if duplicate
					topKreviews.add(content);
			}
		});
		
		// Collect bottom K reviews with lowest spam score to be used as honest class
		List<String> bottomKreviews = new ArrayList<String>();
		FindIterable<Document> it = mongo.retrieveBottomKDocuments(K);
		it.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String content = document.get("content").toString();
				double score = Double.parseDouble(document.get("score").toString());
				//System.out.println(score);
				bottomKreviews.add(content);
				if (!bottomKreviews.contains(content)) // Omit if duplicate
					bottomKreviews.add(content);
			}
		});
		
		// Equalize sizes of spam and honest collections
		if (topKreviews.size() - bottomKreviews.size() != 0) {
			if (topKreviews.size() > bottomKreviews.size()) {
				int reviewsToDelete = topKreviews.size() - bottomKreviews.size();
				int counter = 0;
				int position = topKreviews.size() - 1;
				while (counter < reviewsToDelete) {
					topKreviews.remove(position);
					position--;
					counter++;
				}
			}
			else {
				int reviewsToDelete = bottomKreviews.size() - topKreviews.size();
				int counter = 0;
				int position = bottomKreviews.size() - 1;
				while (counter < reviewsToDelete) {
					bottomKreviews.remove(position);
					position--;
					counter++;
				}
			}
		}
		
		
		System.out.println("Top K list has " + topKreviews.size() + " reviews, while bottom K list has " + bottomKreviews.size() + " reviews.");
		res.saveReviewInstances(topKreviews, true);
		res.saveReviewInstances(bottomKreviews, false);
		
	}
	
}
