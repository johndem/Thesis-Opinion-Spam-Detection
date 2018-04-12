import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

public class PreprocessDataset {
	
	private MongoDB mongo;
	private static final String filePath = "D:\\Opinion Spam Detection data\\lie_dataset\\reviewsNew.txt";
	
	private List<String> reviewsToRemove = new ArrayList<String>();
	
	public PreprocessDataset() {
		mongo = new MongoDB();
	}
	
	public void processReviews() throws IOException {

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		    	// Process the line	    	
		    	String[] lineTokens = line.split("\\t");
		    	
		    	String reviewerId = lineTokens[0];
		    	String productId = lineTokens[1];
		    	String creationDate = lineTokens[2];
		    	String rating = lineTokens[5];
		    	String reviewText = lineTokens[7].trim();
		    	
		    	
		    	if (!reviewerId.equals("") && !productId.equals("") && !creationDate.equals("") && !rating.equals("") && !reviewText.equals("")) {
		    		Document review = new Document();
			    	review.put("pid", productId);
			    	review.put("userid", reviewerId);
			    	review.put("date", creationDate);
			    	review.put("rating", rating);
			    	review.put("content", reviewText);
			    	review.put("score", 0.0);
			    	review.put("info", "");
			    	review.put("score2", 0.0);
			    	review.put("score14", 0.0);
			    	review.put("score30", 0.0);
			    	review.put("scorem", 0.0);
			    	review.put("info2", "");
			    	review.put("scorem2", 0.0);
			    	review.put("scorem14", 0.0);
			    	review.put("scorem30", 0.0);
			    	review.put("scores", 0.0);
			    	review.put("info3", "");
			    	review.put("scores2", 0.0);
			    	review.put("scores14", 0.0);
			    	review.put("scores30", 0.0);
			    	
			    	mongo.insertReview(review);
		    	}
		    	else
		    		continue;
	
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Finished preprocessing reviews collection!");
		
	}
	
	public void processReviewers() {
		
		HashSet<String> reviewerSet = new HashSet<String>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		    	// Process the line
		    	String[] lineTokens = line.split("\\t");
		    	
		    	String reviewerId = lineTokens[0];
		    	String productId = lineTokens[1];
		    	String creationDate = lineTokens[2];
		    	String rating = lineTokens[5];
		    	String reviewText = lineTokens[7].trim();
		    	
		    	if (!reviewerId.equals("") && !productId.equals("") && !creationDate.equals("") && !rating.equals("") && !reviewText.equals(""))
		    		reviewerSet.add(reviewerId);
		    	else
		    		continue;
		    }
		    
		    for (String reviewer : reviewerSet) {
		    	Document doc = new Document();
		    	doc.put("userid", reviewer);
		    	doc.put("score", "0.0");
		    	
		    	mongo.insertReviewer(doc);
		    }
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void processProducts() {
		
		HashMap<String, Integer> productSet = new HashMap<String, Integer>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		    	// Process the line
		    	String[] lineTokens = line.split("\\t");
		    	
		    	String reviewerId = lineTokens[0];
		    	String productId = lineTokens[1];
		    	String creationDate = lineTokens[2];
		    	String rating = lineTokens[5];
		    	String reviewText = lineTokens[7].trim();
		    	
	
		    	if (!reviewerId.equals("") && !productId.equals("") && !creationDate.equals("") && !rating.equals("") && !reviewText.equals("")) {
		    		if (!productSet.containsKey(productId)) {
		    			productSet.put(productId, 1);
		    		}
		    		else {
		    			int counter = productSet.get(productId);
						counter++;
						productSet.put(productId, counter);
		    		}
		    		
		    	}	
		    	else
		    		continue;
		    }
		    
		    for (HashMap.Entry<String, Integer> product : productSet.entrySet()) {
	    		Document doc = new Document();
		    	doc.put("pid", product.getKey());
		    	doc.put("reviews", product.getValue());
		    	//doc.put("mProduct", "0");
		    	
		    	mongo.insertProduct(doc);
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Finished preprocessing products collection!");
		
	}
	
	
	public void createEvalProductSample() {
		ArrayList<String> myPIDList = new ArrayList<String>();
		ArrayList<Integer> myRevList = new ArrayList<Integer>();
		
		FindIterable<Document> iterable = mongo.retrieveProductsCollection().noCursorTimeout(true);
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String product_id = document.get("pid").toString();
				myPIDList.add(product_id);
				int sum = Integer.parseInt(document.get("reviews").toString());
				myRevList.add(sum);
			}
		});
		
		int size = myPIDList.size();
		
		int count_revs = 0;
		
		HashSet<Integer> triedIndexes = new HashSet<Integer>();
		
		int k = 13758;
		int counter = 0;
		while (true) {
			int item = new Random().nextInt(size);
			if (!triedIndexes.contains(item)) {
				triedIndexes.add(item);
				counter++;
				if (counter == k)
					break;
			}
		}
		
		for (Integer i : triedIndexes) {
			String pid = myPIDList.get(i);
			int reviews = myRevList.get(i);
			count_revs += reviews;
			Document doc = new Document();
	    	doc.put("pid", pid);
	    	doc.put("reviews", pid);
	    	mongo.insertLProduct(doc);
		}
		
		System.out.println("#Reviews: " + count_revs);
	}
	
	
	public void createLargeProductSample() {
		ArrayList<String> myPIDList = new ArrayList<String>();
		ArrayList<Integer> myRevList = new ArrayList<Integer>();
		
		FindIterable<Document> iterable = mongo.retrieveLargeProductsCollection().noCursorTimeout(true);
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String product_id = document.get("pid").toString();
				myPIDList.add(product_id);
				int sum = Integer.parseInt(document.get("reviews").toString());
				myRevList.add(sum);
			}
		});
		
		int size = myPIDList.size();
		
		int count_revs = 0;
		
		HashSet<Integer> triedIndexes = new HashSet<Integer>();
		
		int k = 1679;
		int counter = 0;
		while (true) {
			int item = new Random().nextInt(size);
			if (!triedIndexes.contains(item)) {
				triedIndexes.add(item);
				counter++;
				if (counter == k)
					break;
			}
		}
		
		for (Integer i : triedIndexes) {
			String pid = myPIDList.get(i);
			int reviews = myRevList.get(i);
			count_revs += reviews;
			Document doc = new Document();
	    	doc.put("pid", pid);
	    	doc.put("reviews", pid);
	    	mongo.insertLProduct(doc);
		}
		
		System.out.println("Large: " + count_revs);
	}
	
	public void createMediumProductSample() {
		ArrayList<String> myPIDList = new ArrayList<String>();
		ArrayList<Integer> myRevList = new ArrayList<Integer>();
		
		FindIterable<Document> iterable = mongo.retrieveMediumProductsCollection().noCursorTimeout(true);
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String product_id = document.get("pid").toString();
				myPIDList.add(product_id);
				int sum = Integer.parseInt(document.get("reviews").toString());
				myRevList.add(sum);
			}
		});
		
		int size = myPIDList.size();
		
		int count_revs = 0;
		
		HashSet<Integer> triedIndexes = new HashSet<Integer>();
		
		int k = 5982;
		int counter = 0;
		while (true) {
			int item = new Random().nextInt(size);
			if (!triedIndexes.contains(item)) {
				triedIndexes.add(item);
				counter++;
				if (counter == k)
					break;
			}
		}
		
		for (Integer i : triedIndexes) {
			String pid = myPIDList.get(i);
			int reviews = myRevList.get(i);
			count_revs += reviews;
			Document doc = new Document();
	    	doc.put("pid", pid);
	    	doc.put("reviews", pid);
	    	mongo.insertMedProduct(doc);
		}
		
		System.out.println("Medium: " + count_revs);
	}
	
	public void createSmallProductSample() {
		ArrayList<String> myPIDList = new ArrayList<String>();
		ArrayList<Integer> myRevList = new ArrayList<Integer>();
		
		FindIterable<Document> iterable = mongo.retrieveSmallProductsCollection().noCursorTimeout(true);
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String product_id = document.get("pid").toString();
				myPIDList.add(product_id);
				int sum = Integer.parseInt(document.get("reviews").toString());
				myRevList.add(sum);
			}
		});
		
		int size = myPIDList.size();
		
		int count_revs = 0;
		
		HashSet<Integer> triedIndexes = new HashSet<Integer>();
		
		int k = 19589;
		int counter = 0;
		while (true) {
			int item = new Random().nextInt(size);
			if (!triedIndexes.contains(item)) {
				triedIndexes.add(item);
				counter++;
				if (counter == k)
					break;
			}
		}
		
		for (Integer i : triedIndexes) {
			String pid = myPIDList.get(i);
			int reviews = myRevList.get(i);
			count_revs += reviews;
			Document doc = new Document();
	    	doc.put("pid", pid);
	    	doc.put("reviews", pid);
	    	mongo.insertSProduct(doc);
		}
		
		System.out.println("Small: " + count_revs);
	}
	
	public void removeFaultyReviews() {
		FindIterable<Document> iter = mongo.retrieveReviewsCollection();
		iter.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String mongo_id = document.get("_id").toString();
				String creationDate = document.get("date").toString();
				
				if (creationDate.substring(0, 1).equals(" ")) {
					reviewsToRemove.add(mongo_id);
					System.out.println(creationDate + " -> Remove!");
				}
			}
		});
		
		for (String id : reviewsToRemove)
			mongo.removeReview(id);
	}
	
	
	public void processMproducts() {
		
		try (BufferedReader br = new BufferedReader(new FileReader("D:\\Opinion Spam Detection data\\lie_dataset\\productInfoXML-reviewed-mProducts.txt"))) {
		    String line;
		    
		    int cursor_pos = 0;
		    while ((line = br.readLine()) != null) {
		    	// Process the line
		    	if (line.equals("BREAK-REVIEWED")) {
		    		cursor_pos = 0;
		    		continue;
		    	}
		    	
		    	if (cursor_pos == 0) {
		    		String[] lineTokens = line.split("\\t");	    	
			    	String productId = lineTokens[0];
			    	
//			    	Document mProduct = new Document();
//			    	mProduct.put("pid", productId);
//			    	mongo.insertMproduct(mProduct);
			    	
			    	mongo.updateProduct(productId);
		    	}
		    	
		    	cursor_pos++;
		    	if (cursor_pos == 2)
		    		cursor_pos = 0;
	
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
