import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
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
		
		RatingDeviation rd = new RatingDeviation();
		rd.analyzeRating();
		
	}

}
