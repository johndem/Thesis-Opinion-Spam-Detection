import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

public class Main {

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
		SpamDetector sd = new SpamDetector();
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
		pData.processReviews();
		*/
		
	}

}
