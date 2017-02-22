import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

public class Main {
	
	static int total = 0;
	static int count = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ReviewFilter rFilter = new ReviewFilter();
		try {
			rFilter.filterProductReviews();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		// For testing purposes
		MongoDB mongo= new MongoDB();
		FindIterable<Document> iterable = mongo.retrieveProductsCollection();
		
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				int sum = Integer.parseInt(document.get("reviews").toString());
				String pid = document.get("pid").toString();
				String m = document.get("mProduct").toString();
				if (m.equals("1") && sum > 2) {
					count++;
					total = total + sum;
				}
			}
		});
		System.out.println("Products: " + count + ", Reviews: " + total);
		*/
		
		/*
		MongoDB mongo= new MongoDB();
		SpamDetector sd = new SpamDetector(mongo, "");
		try {
			sd.performSpamDetection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		/*
		// Process and store review dataset in database
		PreprocessDataset pData = new PreprocessDataset();
		pData.processReviewers();
		*/
		
	}

}
