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
		
		FindIterable<Document> iterable = mongo.retrieveProductsCollection().noCursorTimeout(true);
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String product_id = document.get("pid").toString();
				//int numOfReviews = Integer.parseInt(document.get("reviews").toString());
				//String mProduct = document.get("mProduct").toString();
				//System.out.println(product_id + " -> " + numOfReviews);
				
				try {
					new SpamDetector(mongo, product_id).performSpamDetection();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		System.out.println("1st phase completed");
		
		
		
		
		// Extract top-K and bottom-K reviews for review text classification evaluation purposes
		Results res = new Results();
		int K = 6000;
		
		// Collect top K reviews with highest spam score to be used as spam class
		List<String> topKreviews = new ArrayList<String>();
		FindIterable<Document> iter = mongo.retrieveTopKDocuments(K).noCursorTimeout(true);
		iter.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String content = document.get("content").toString();
//				double score = Double.parseDouble(document.get("score").toString());

				boolean toBeAdded = true;
				int count = topKreviews.size() - 1;
				while (count > -1 && toBeAdded) {
					if (ContentSimilarity.similar(content, topKreviews.get(count))) {
						toBeAdded = false;
					}
					count--;
				}
				if (toBeAdded) {
					topKreviews.add(content);
				}
			}
		});
		
		System.out.println("Done with top-K!");
		
		// Collect bottom K reviews with lowest spam score to be used as honest class
		List<String> bottomKreviews = new ArrayList<String>();
		FindIterable<Document> it = mongo.retrieveBottomKDocuments(K).noCursorTimeout(true);
		it.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String content = document.get("content").toString();
//				double score = Double.parseDouble(document.get("score").toString());

				if (!bottomKreviews.contains(content)) { // Omit if duplicate
					bottomKreviews.add(content);
					//System.out.println(id + " -> " + score);
				}
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
		
		res.saveReviewInstances(topKreviews, true);
		res.saveReviewInstances(bottomKreviews, false);
		
		System.out.println("2nd phase completed");
		
	}
	
}
