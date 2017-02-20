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
		long startTime = System.currentTimeMillis();
		FindIterable<Document> iterable = mongo.retrieveProductsCollection();
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String product_id = document.get("pid").toString();
				String numOfReviews = document.get("#reviews").toString();
				System.out.println("Annotating reviews for product: " + product_id);
				
				if (Integer.parseInt(numOfReviews) > 50) {
					try {
						new SpamDetector(mongo, product_id).performSpamDetection();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		});
		System.out.println("\nDuration: " + String.format("%.3g", (System.currentTimeMillis() - startTime) / 1000.0) + "s");
		
		
//		Results res = new Results();
//		
//		List<String> topKreviews = new ArrayList<String>();
//		FindIterable<Document> iter = mongo.retrieveTopKDocuments(10);
//		iter.forEach(new Block<Document>() {
//			@Override
//			public void apply(final Document document) {
//				String content = document.get("content").toString();
//				double score = Double.parseDouble(document.get("score").toString());
//				System.out.println(score);
//				topKreviews.add(content);
//			}
//		});
//		
//		res.saveReviewInstances(topKreviews, true);
//		
//		List<String> bottomKreviews = new ArrayList<String>();
//		FindIterable<Document> it = mongo.retrieveBottomKDocuments(10);
//		it.forEach(new Block<Document>() {
//			@Override
//			public void apply(final Document document) {
//				String content = document.get("content").toString();
//				double score = Double.parseDouble(document.get("score").toString());
//				System.out.println(score);
//				bottomKreviews.add(content);
//			}
//		});
//		
//		res.saveReviewInstances(bottomKreviews, false);
		
	}
	
}
