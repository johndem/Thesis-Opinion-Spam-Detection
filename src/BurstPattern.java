import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

public class BurstPattern extends SpamDetector {
	
	private List<Review> reviewList; // List storing all reviews
	private int numOfReviews; // Number of reviews of a product
	
	private int len; // Duration (in days) from first created review to newest
	private int dt;	// Window size to divide time into intervals
	private int numOfIntervals;	// Number of intervals in the product review timeline
	private int avgReviewsInt; // Average expected number of reviews per interval
	
	int count = 0;
	
	public BurstPattern() {
		reviewList = new ArrayList<Review>();
		
		len = 0;
		dt = 14;
		
//		FindIterable<Document> iterable = mongo.retrieveProductReviews("B000059H9C");
//		iterable.forEach(new Block<Document>() {
//			@Override
//			public void apply(final Document document) {
//				double rating = Double.parseDouble(document.get("rating").toString());
//				String creationDate = document.get("date").toString();
//				numOfReviews++;
//				reviewList.add(new Review(numOfReviews, rating, creationDate));
//			}
//		});
		
//		try{
//		    PrintWriter writer = new PrintWriter("burstest.txt", "UTF-8");
//		    
//		    for (Review review : reviewList) {
//		    	writer.println(review.getId() + "\t" + review.getRating() + "\t" + review.getDate());
//		    }
//		    
//		    writer.close();
//		} catch (IOException e) {
//		   // do something
//		}
		
		// Read reviews for a specific product and store in a List
		try (BufferedReader br = new BufferedReader(new FileReader("burstest.txt"))) {
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		    	// Process the line
		    	String[] lineTokens = line.split("\\t");
		    	
		    	int id = Integer.parseInt(lineTokens[0]);
		    	double rating = Double.parseDouble(lineTokens[1]);
		    	String creationDate = lineTokens[2];
		    	
		    	// Add review to List
		    	reviewList.add(new Review(id, rating, creationDate));
		    	
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		numOfReviews = reviewList.size();
		
		// Sort reviews in List from oldest to latest
		Collections.sort(reviewList, new Comparator<Review>() {
			  public int compare(Review o1, Review o2) {
				  if (o1.getDate() == null || o2.getDate() == null)
					  return 0;
			      return o1.getDate().compareTo(o2.getDate());
			  }
		});
	}
	
	public void detectBurstPatters() {

		// Display product reviews
		for (Review review : reviewList) {
	    	System.out.println(review.getId() + "\t" + review.getRating() + "\t" + review.getDate());
	    }
		
		len = (int) ChronoUnit.DAYS.between(reviewList.get(0).getDate(), reviewList.get(reviewList.size()-1).getDate());
    	System.out.println("Duration (len) from: " + reviewList.get(0).getDate() + " to " + reviewList.get(reviewList.size()-1).getDate() + " is " + len);
    	
    	numOfIntervals = len / dt;
    	System.out.println("Number of intevals: " + numOfIntervals);
    	
    	avgReviewsInt = numOfReviews / numOfIntervals;
    	System.out.println("Average number of reviews per inteval: " + avgReviewsInt);
		
	}

}
