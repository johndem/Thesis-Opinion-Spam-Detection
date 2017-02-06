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
	
	private List<String> nGramList;
	private HashMap<String, Integer> tempBag;
	private int N;
	private String pathToTrainingSet = "C:\\Users\\John\\Documents\\Πανεπιστήμιο\\Διπλωματική\\Datasets\\TrainingDataset\\positive";
	
	public FeatureExtraction(int N) {
		nGramList = new ArrayList<String>();
		tempBag = new HashMap<String, Integer>();
		this.N = N;
	}
	
	public List<String> getNgramList() {
		return nGramList;
	}
	
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public void nGrams(String sent) {
		
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
			if (tempBag.containsKey(s)) {
				int counter = tempBag.get(s);
				counter++;
				tempBag.put(s, counter);
			}
			else {
				tempBag.put(s, 1);
			}
		}
		
		// Print n-grams
//		for (String item : wordNgramList)
//			System.out.println(item);
		
	}
	
	public void generateNgrams() {
		
		try(Stream<Path> paths = Files.walk(Paths.get(pathToTrainingSet))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		            //System.out.println(filePath);
		        	try {
		        		nGrams(readFile(filePath.toString(), StandardCharsets.UTF_8));
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
		
		for (HashMap.Entry<String, Integer> entry : tempBag.entrySet()) {
			if (entry.getValue() > 1)
				nGramList.add(entry.getKey());
		}

	}
	
	private HashMap<Integer, String> featureVector(String text, String textClass) {
		
		HashMap<Integer, String> vector = new HashMap<>();
		
		if (!textClass.equals(""))
			vector.put(-1, textClass);
		
		for (int i = 0; i < nGramList.size(); i++)
			vector.put(i, "0");
		
		String[] tokens = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+"); // Remove unwanted characters and split sentence into word tokens
		
		// Generate the n-grams
		for(int k = 0; k < (tokens.length-N+1); k++) {
			String s = "";
			int start = k;
			int end = k + N;
			for(int j = start; j < end; j++) {
				s = s + "" + tokens[j];
			}
			
			if (nGramList.contains(s)) {
				if (vector.containsKey(nGramList.indexOf(s))) {
					int value = Integer.valueOf(vector.get(nGramList.indexOf(s)));
					value++;
					vector.put(nGramList.indexOf(s), String.valueOf(value));
				}
				else {
					vector.put(nGramList.indexOf(s), "1");
				}
			}	
			
//			if (nGramList.contains(s))
//				vector.put(nGramList.indexOf(s), "1");
			
		}
		
//		System.out.println(vector);
		
		return vector;
		
	}
	
	public List<HashMap<Integer, String>> generateFeatures(boolean annotated, List<String> docs) {
		
		List<HashMap<Integer, String>> featureVectorList = new ArrayList<HashMap<Integer, String>>();
		
		if (annotated) {
			try(Stream<Path> paths = Files.walk(Paths.get(pathToTrainingSet))) {
			    paths.forEach(filePath -> {
			        if (Files.isRegularFile(filePath)) {
			            //System.out.println(filePath);
			            String reviewClass = "";
		            	String[] tokens = filePath.toString().split("\\\\");
			            char c = tokens[tokens.length-1].charAt(0);
			            //System.out.println(c);
		            	if (c == 'd')
		            		reviewClass = "spam";
		            	else if (c == 't')
		            		reviewClass = "honest";
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
		}
		else {
			featureVectorList.add(featureVector(docs.get(0), ""));
		}
		
		return featureVectorList;

	}

}
