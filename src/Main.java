import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		SpamDetector sd = new SpamDetector();
		try {
			sd.performSpamDetection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		// Process and store review dataset in database
		PreprocessDataset pData = new PreprocessDataset();
		pData.process();
		*/
		
	}

}
