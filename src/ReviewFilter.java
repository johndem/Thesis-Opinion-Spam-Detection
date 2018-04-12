import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

import java.util.Map;

public class ReviewFilter {
	
	private MongoDB mongo;
	
	int count_p = 0, count_r = 0;

	public ReviewFilter() {
		mongo = new MongoDB();
	}
	
	public void filterProductReviews() throws IOException {
		
		FindIterable<Document> iterable = mongo.retrieveSProductsCollection().noCursorTimeout(true);
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String product_id = document.get("pid").toString();
//				int sum = Integer.parseInt(document.get("reviews").toString());
//				
//				if (sum > 5) {
					try {
						new SpamDetector(mongo, product_id).performSpamDetection();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					count_p++;
//					count_r += sum;
//				}
				
			}
		});
		System.out.println("1st phase completed");
		
		extractRangeK();
		System.out.println("2nd phase completed");
		
	}
	
	public void extractRangeK() throws IOException {
		// Extract top-K and bottom-K reviews for review text classification evaluation purposes
		Results res = new Results();
		int K = 2000;
		
		// Collect top K reviews with highest spam score to be used as spam class
		List<MyEntry<Double, EvaluationInfo>> spamR = new ArrayList<MyEntry<Double, EvaluationInfo>>();
		List<String> topKreviews = new ArrayList<String>();
		HashSet<String> retRevs = new HashSet<String>();
		FindIterable<Document> iter = mongo.retrieveSpamDocuments().noCursorTimeout(true);
		iter.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String content = document.get("content").toString();
				String info = document.get("pid").toString();
				double score = Double.parseDouble(document.get("scorem30").toString());
				
				if (!retRevs.contains(content)) {
					spamR.add(new MyEntry(score, new EvaluationInfo(info, content)));
					retRevs.add(content);
				}
					
			}
		});
		
		Collections.sort(spamR, new Comparator<MyEntry<Double, EvaluationInfo>>() {
			  public int compare(MyEntry<Double, EvaluationInfo> o1, MyEntry<Double, EvaluationInfo> o2) {
				  if (o1.getKey() == null || o2.getKey() == null)
					  return 0;
			      return o2.getKey().compareTo(o1.getKey());
			  }
		});
		
		
		for (int i = 0; i < K; i++) {
			if (i % 1 == 0) {
				topKreviews.add(spamR.get(i).getValue().getContent());
				//System.out.println(spamR.get(i).getKey() + "	" + spamR.get(i).getValue().getInfo());
			}
		}
		
		
//		try {
//			PrintWriter writer = new PrintWriter("results.txt", "UTF-8");
//			
//			for (int i = 0; i < K; i++) {
//				if (i % 1 == 0) {
//					//topKreviews.add(spamR.get(i).getValue().getContent());
//					writer.println("----------------------------- " + topKreviews.size() + ". SCORE: " + spamR.get(i).getKey() + "-----------------------------------");
//					writer.println(spamR.get(i).getValue().getInfo());
//				}
//			}
//		} catch (IOException e) {
//			   // do something
//		}
		
		System.out.println(topKreviews.size());
		
		
		// Collect bottom K reviews with lowest spam score to be used as honest class
		List<MyEntry<Double, EvaluationInfo>> honestR = new ArrayList<MyEntry<Double, EvaluationInfo>>();
		List<String> bottomKreviews = new ArrayList<String>();
		FindIterable<Document> it = mongo.retrieveBottomKDocuments(K).noCursorTimeout(true);
		it.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String content = document.get("content").toString();
				String info = document.get("info").toString();
				double score = Double.parseDouble(document.get("scorem30").toString());
				
				honestR.add(new MyEntry(score, new EvaluationInfo(info, content)));
			}
		});
		
		for (int i = 0; i < K; i++) {
			if (i % 1 == 0) {
				bottomKreviews.add(honestR.get(i).getValue().getContent());
				//System.out.println(honestR.get(i).getKey());
			}
		}
		
		System.out.println(bottomKreviews.size());
		
		res.saveReviewInstances(topKreviews, true, "output3/");
		res.saveReviewInstances(bottomKreviews, false, "output3/");
	}
	
	public void extractTopBottomK() throws IOException {
		// Extract top-K and bottom-K reviews for review text classification evaluation purposes
		Results res = new Results();
		int K = 3000;
		
		// Collect top K reviews with highest spam score to be used as spam class
		List<String> topKreviews = new ArrayList<String>();
		FindIterable<Document> iter = mongo.retrieveTopKDocuments(K).noCursorTimeout(true);
		iter.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String content = document.get("content").toString();

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

				if (!bottomKreviews.contains(content)) { // Omit if duplicate
					bottomKreviews.add(content);
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
		
		res.saveReviewInstances(topKreviews, true, "output3/");
		res.saveReviewInstances(bottomKreviews, false, "output3/");
	}
	
	final class MyEntry<K, V> implements Map.Entry<K, V> {
	    private final K key;
	    private V value;

	    public MyEntry(K key, V value) {
	        this.key = key;
	        this.value = value;
	    }

	    @Override
	    public K getKey() {
	        return key;
	    }

	    @Override
	    public V getValue() {
	        return value;
	    }

	    @Override
	    public V setValue(V value) {
	        V old = this.value;
	        this.value = value;
	        return old;
	    }
	}
	
}
