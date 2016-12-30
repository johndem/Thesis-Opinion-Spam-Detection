import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		SpamDetector sd = new SpamDetector();
		sd.performSpamDetection();
		
		/*
		// Process and store review dataset in database
		PreprocessDataset pData = new PreprocessDataset();
		pData.process();
		*/
		
		/*
		// Perform supervised learning (NB) to annotate review text as spam or honest
		Classifier classifier = new Classifier(3);
		classifier.classifyReviews();
		*/
		
		/*
		// Calculate rating deviation of each review on a 5-star rating system
		RatingDeviation rd = new RatingDeviation(5);
		rd.analyzeRating();
		*/
		
		/*
		// Detect reviews posted in suspicious time intervals
		BurstPattern bp = new BurstPattern();
		bp.detectBurstPatters();
		*/
		
	}

}
