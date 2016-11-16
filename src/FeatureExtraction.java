import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class FeatureExtraction {
	
	private List<String> wordNgramList;
	private List<String> characterNgramList;
	
	private int N;
	
	private String pathToTrainingSet = "C:\\Users\\John\\Documents\\Πανεπιστήμιο\\Διπλωματική\\Datasets\\TrainingDataset";
	private String pathToDataSet = "C:\\Users\\John\\Documents\\Πανεπιστήμιο\\Διπλωματική\\Datasets\\Test";
	
	public FeatureExtraction(int N) {
		
		wordNgramList = new ArrayList<String>();
		characterNgramList = new ArrayList<String>();
		this.N = N;
		
	}
	
	public List<String> getWordNgramList() {
		return wordNgramList;
	}
	
	public List<String> getCharacterNgramList() {
		return characterNgramList;
	}
	
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public void wordNgrams(String sent) {
		
		String[] tokens = sent.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+"); // Remove unwanted characters and split sentence into word tokens
	 
		// Generate the n-grams
		for(int k = 0; k < (tokens.length-N+1); k++) {
			String s = "";
			int start = k;
			int end = k + N;
			for(int j = start; j < end; j++) {
				s = s + "" + tokens[j];
			}
			// Add n-gram to a list
			if (!wordNgramList.contains(s))
				wordNgramList.add(s);
		}
		
		// Print n-grams
//		for (String item : wordNgramList)
//			System.out.println(item);
		
	}
	
	public void characterNgrams(String sent) {
		
		sent = sent.replaceAll("[^a-zA-Z]", "").toLowerCase(); // Remove unwanted characters from sentence
		String[] tokens = new String[sent.length()];
		
		// Split sentence into character tokens
		int counter = 0;
		for (char character : sent.toCharArray()) {
			tokens[counter] = String.valueOf(character);
			counter++;
		}
		
		// Generate the n-grams
		for(int k = 0; k < (tokens.length-N+1); k++) {
			String s = "";
			int start = k;
			int end = k + N;
			for(int j = start; j < end; j++) {
				s = s + "" + tokens[j];
			}
			// Add n-gram to a list
			if (!characterNgramList.contains(s))
				characterNgramList.add(s);
		}
		
		// Print n-grams
//		for (String item : characterNgramList)
//			System.out.println(item);
		
	}
	
	public void generateCharacterNgrams() {
		
		try(Stream<Path> paths = Files.walk(Paths.get(pathToTrainingSet))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		            //System.out.println(filePath);
		        	try {
		        		characterNgrams(readFile(filePath.toString(), StandardCharsets.UTF_8));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		    });
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private HashMap<Integer, String> featureVector(String text, String textClass) {
		
		HashMap<Integer, String> vector = new HashMap<>();
		
		if (!textClass.equals(""))
			vector.put(-1, textClass);
		
		for (int i = 0; i < characterNgramList.size(); i++)
			vector.put(i, "0");
		
		text = text.replaceAll("[^a-zA-Z]", "").toLowerCase(); // Remove unwanted characters from sentence
		String[] tokens = new String[text.length()];
		
		// Split sentence into character tokens
		int counter = 0;
		for (char character : text.toCharArray()) {
			tokens[counter] = String.valueOf(character);
			counter++;
		}
		
		// Generate the n-grams
		for(int k = 0; k < (tokens.length-N+1); k++) {
			String s = "";
			int start = k;
			int end = k + N;
			for(int j = start; j < end; j++) {
				s = s + "" + tokens[j];
			}
			
			if (characterNgramList.contains(s))
				vector.put(characterNgramList.indexOf(s), "1");
		}
		
//		System.out.println(vector);
		
		return vector;
		
	}
	
	public List<HashMap<Integer, String>> generateFeatures(int N, boolean annotated) {
		
		List<HashMap<Integer, String>> featureVectorList = new ArrayList<HashMap<Integer, String>>();
		String pathToSet = "";
		
		if (annotated) {
			pathToSet = pathToTrainingSet;
		}
		else {
			pathToSet = pathToDataSet;
		}
		
		/* For each review */
		
		try(Stream<Path> paths = Files.walk(Paths.get(pathToSet))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		            //System.out.println(filePath);
		            String reviewClass = "";
		            if (annotated) {
		            	String[] tokens = filePath.toString().split("\\\\");
			            char c = tokens[tokens.length-1].charAt(0);
			            //System.out.println(c);
		            	if (c == 'd')
		            		reviewClass = "spam";
		            	else if (c == 't')
		            		reviewClass = "honest";
		            }
		            //System.out.println(reviewClass);
		        	try {
		        		featureVectorList.add(featureVector(readFile(filePath.toString(), StandardCharsets.UTF_8), reviewClass));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		    });
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* For each review */
		
		return featureVectorList;

	}

}
