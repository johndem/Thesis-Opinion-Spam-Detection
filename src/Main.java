import java.io.IOException;


public class Main {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		// Process and store review dataset in database
		PreprocessDataset pData = new PreprocessDataset();
//		pData.createEvalProductSample();
//		pData.createLargeProductSample();
//		pData.createMediumProductSample();
//		pData.createSmallProductSample();
//		try {
//			pData.processReviews();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		pData.processProducts();
		//pData.processMproducts();
		//pData.removeFaultyReviews();
		
		ReviewFilter rFilter = new ReviewFilter();
		try {
			rFilter.filterProductReviews();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
//		DatasetInfo di = new DatasetInfo();
//		di.countReviewers();
//		di.findTopReviews();
//		di.createScoreChart();
		
		/*
		// Experiment on a single product
		MongoDB mongo= new MongoDB();
		SpamDetector sd = new SpamDetector(mongo, "B0002Z1EG2"); // "B0000TB03W"
		try {
			sd.performSpamDetection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

}
