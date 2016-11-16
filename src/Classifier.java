import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class Classifier {
	
	private int N;
	private FeatureExtraction fExtract;
	private ArrayList<String> classValues;
	
	public Classifier(int N) {
		
		this.N = N;
		
		fExtract = new FeatureExtraction(N);
		fExtract.generateCharacterNgrams();
		
		// Declare class values
		classValues = new ArrayList<String>(2);
		classValues.add("spam");
		classValues.add("honest");
		
	}
	
	private Instances getClassifierInstances(boolean annotated, List<HashMap<Integer, String>> featureVectors) {
		
		ArrayList<Attribute>  atts = new ArrayList<Attribute>(); // Declare the feature vector
		
		Attribute classAtt = new Attribute("theClass", classValues); // Declare class attribute
		
		// Declare an attribute for each dimension of the n-gram corpus
		for (String nGram : fExtract.getCharacterNgramList()) {
			atts.add(new Attribute(nGram));
		}
		
		atts.add(classAtt);
		
		// Create an empty training set
		Instances instances = new Instances("MyRelation", atts, 0);
		
		// Set class index
		instances.setClassIndex(instances.numAttributes() - 1);
		
		// Create an instance for each feature vector
		for (HashMap<Integer, String> fVector : featureVectors) {
			
			double[] vals = new double[instances.numAttributes()];

			if (annotated) { // for annotated data add its class label
				vals[instances.numAttributes() - 1] = classValues.indexOf(fVector.get(-1));
			}
			
			for (int i = 0; i < instances.numAttributes() - 1; i++) {
				if (fVector.containsKey(i)) {
					vals[i] = Double.parseDouble(fVector.get(i)); // Fill with feature vector values
				}
			}
			
			instances.add(new DenseInstance(1.0, vals)); // Add the instance
			
		}
		
		return instances;
		
	}
	
	public void classifyReviews() {
		
		List<HashMap<Integer, String>> trainingFeatureVectors = fExtract.generateFeatures(N, true); // List of the training feature vectors
		
		Instances isTrainingSet = getClassifierInstances(true, trainingFeatureVectors); // Training set of instances for the classifier
		
		// Build and train Naive Bayes classifier
		AbstractClassifier NB = (AbstractClassifier) new NaiveBayes();
		try {
			NB.buildClassifier(isTrainingSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Test the model
//		 Evaluation eTest = new Evaluation(isTrainingSet);
//		 eTest.evaluateModel(NB, isTestingSet);
		
		List<HashMap<Integer, String>> reviewFeatureVectors = fExtract.generateFeatures(N, false); // List of the review feature vectors to be classified
		
		Instances isDataset = getClassifierInstances(false, reviewFeatureVectors); // Dataset of review text instances for the classifier
		
		for (Instance instance : isDataset) {
			try {
				double prediction = NB.classifyInstance(instance);
				System.out.println(classValues.get((int) prediction));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
