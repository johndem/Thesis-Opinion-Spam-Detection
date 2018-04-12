import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ChartUtilities;

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
		FindIterable<Document> iterable = mongo.retrieveSmallProductsCollection();
		
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				int reviews = Integer.parseInt(document.get("reviews").toString());
				counter += reviews;
			}
		});
		
		System.out.println("Total amount of reviews (S): " + counter);
		
		counter = 0;
		FindIterable<Document> iter = mongo.retrieveMediumProductsCollection();
		
		iter.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				int reviews = Integer.parseInt(document.get("reviews").toString());
				counter += reviews;
			}
		});
		
		System.out.println("Total amount of reviews (M): " + counter);
		
		counter = 0;
		FindIterable<Document> it = mongo.retrieveLargeProductsCollection();
		
		it.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				int reviews = Integer.parseInt(document.get("reviews").toString());
				counter += reviews;
			}
		});
		
		System.out.println("Total amount of reviews (L): " + counter);
	}
	
	public void measureReviews() {
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
	
	public void countReviewers() {
		FindIterable<Document> iterable = mongo.retrieveAnnotatedReviews().noCursorTimeout(true);
		
		HashSet<String> reviewerSet = new HashSet<String>();
		
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String reviewerId = document.get("userid").toString();
				reviewerSet.add(reviewerId);
			}
		});
		
		System.out.println("Total amount of reviewers: " + reviewerSet.size());
	}
	
	public void createScoreChart() throws IOException {//max=46.7
		FindIterable<Document> iterable = mongo.retrieveSpamDocuments().noCursorTimeout(true);
		
		ArrayList<Integer> scoreLeft = new ArrayList<Integer>();
		ArrayList<Integer> scoreRight = new ArrayList<Integer>();
		ArrayList<Integer> scoreDistribution = new ArrayList<Integer>();
		
		for (int i = 3; i < 47; i=i+2) {
			scoreLeft.add(i);
			scoreRight.add(i+2);
			scoreDistribution.add(0);
			//System.out.println(i);
		}
		
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				double score = Double.parseDouble(document.get("score2").toString());
				
				for (int i = 0; i < scoreLeft.size(); i++) {
					if (score >= scoreLeft.get(i) && score < scoreRight.get(i)) {
						int sum = scoreDistribution.get(i) + 1;
						scoreDistribution.set(i, sum);
						break;
					}
				}
			}
		});
		
		
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
		
		for (int i = 0; i < scoreLeft.size(); i++) {
			String label = String.valueOf(scoreLeft.get(i)) + "-" + String.valueOf(scoreRight.get(i));
			dataset.addValue( scoreDistribution.get(i) , "Spam Score" ,  label );
			System.out.println(label + " -> " + scoreDistribution.get(i));
		}

	    JFreeChart barChart = ChartFactory.createBarChart("", "Range", "Sum", dataset,PlotOrientation.VERTICAL, true, false, false);
	         
	    int width = 640;    /* Width of the image */
	    int height = 480;   /* Height of the image */ 
	    File BarChart = new File( "BarChart.jpeg" ); 
	    ChartUtilities.saveChartAsJPEG( BarChart , barChart , width , height );
	}
	
	public void findTopReviews() {
		HashMap<String, String> topReviews = new HashMap<String, String>();
		HashMap<String, Double> topReviewScores = new HashMap<String, Double>();
		
		FindIterable<Document> iter = mongo.retrieveTopKDocuments(1000).noCursorTimeout(true);
		iter.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String userid = document.get("userid").toString();
				String info = document.get("info3").toString();
				double score = Double.parseDouble(document.get("score2").toString());
				
				if (topReviews.size() < 10 && !topReviews.containsKey(userid)) {
					topReviews.put(userid, info);
					topReviewScores.put(userid, score);
				}
			}
					
		});
		
		for (String key : topReviews.keySet()) {
			System.out.println(topReviews.get(key));
			System.out.println(topReviewScores.get(key));
			System.out.println("-------------------------------------------");
		}
			
	}
	
	public void measureTotalmReviews() {
		FindIterable<Document> iterable = mongo.retrieveProductsCollection();
		
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				int reviews = Integer.parseInt(document.get("reviews").toString());
				String mProduct = document.get("mProduct").toString();
				if (mProduct.equals("1") && reviews > 3)
					counter += reviews;
			}
		});
		
		System.out.println("Total amount of M reviews: " + counter);
	}

}
