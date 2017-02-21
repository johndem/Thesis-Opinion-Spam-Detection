import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class Classification {
	
	private int N;
	private FeatureExtraction features;
	private Classifier cls;
	private ArrayList<String> classValues;
	
	public Classification(int N) {
		this.N = N;
		features = new FeatureExtraction(N);
		features.generateNgrams();
		
		// Load classifier and classify test instances
		try {
			cls = (Classifier) weka.core.SerializationHelper.read("mlp.model");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Declare class values
		classValues = new ArrayList<String>(2);
		classValues.add("spam");
		classValues.add("honest");
	}
	
	private Instances getClassifierInstances(boolean annotated, List<List<String>> featureVectors) {
		
		ArrayList<Attribute>  atts = new ArrayList<Attribute>(); // Declare the feature vector
		
		Attribute classAtt = new Attribute("theClass", classValues); // Declare class attribute
		
		// Declare an attribute for each dimension of the n-gram corpus
		for (String nGram : features.getNgramList()) {
			atts.add(new Attribute(nGram));
		}
		
		atts.add(classAtt);
		
		// Create an empty training set
		Instances instances = new Instances("MyRelation", atts, 0);
		
		// Set class index
		instances.setClassIndex(instances.numAttributes() - 1);
		
		// Create an instance for each feature vector
		for (List<String> fVector : featureVectors) {
			
			double[] vals = new double[instances.numAttributes()];

			if (annotated) { // for annotated data add its class label
				vals[instances.numAttributes() - 1] = classValues.indexOf(fVector.get(0));
			}
			
			for (int i = 0; i < instances.numAttributes() - 1; i++) {
				vals[i] = Double.parseDouble(fVector.get(i+1)); // Fill with feature vector values
			}
			
			instances.add(new DenseInstance(1.0, vals)); // Add the instance
			
		}
		
		return instances;
		
	}
	
	public void MultiLayerPerceptron() throws Exception {
		
		// Load training instances
		List<List<String>> trainingFeatureVectors = features.generateFeatures(true, null); // List of the training feature vectors
		Instances isTrainingSet = getClassifierInstances(true, trainingFeatureVectors); // Training set of instances for the classifier
		
		MultilayerPerceptron mlp = new MultilayerPerceptron();
		
		mlp.setLearningRate(0.1);
		mlp.setMomentum(0.25);
		mlp.setTrainingTime(20);
		mlp.setHiddenLayers("100");
		mlp.setNormalizeAttributes(false);
		
		/*
		mlp.buildClassifier(isTrainingSet);
		
		// Save the classifier model
		weka.core.SerializationHelper.write("mlp.model", mlp);
		*/
		
		// Evaluate the model
		Evaluation eTest = new Evaluation(isTrainingSet);
		eTest.crossValidateModel(mlp, isTrainingSet, 10, new Random(1));
		 
		// Print the result à la Weka explorer:
		String strSummary = eTest.toSummaryString();
		System.out.println(strSummary);
		
		double fMeasure = 0.0;
		for (int i = 0; i < isTrainingSet.numClasses(); i++) {
			fMeasure = fMeasure + eTest.fMeasure(i);
			System.out.println(isTrainingSet.classAttribute().value(i) + " -> FMeasure: " + eTest.fMeasure(i));
		}
		System.out.println("Mean FMeasure: " + fMeasure/2);
		
	}
	
	public List<String> classifyReviews(List<String> documents) {
		List<String> classifiedDocs = new ArrayList<String>();
		
		List<List<String>> reviewFeatureVectors = features.generateFeatures(false, documents); // List of the review feature vectors to be classified
		
		Instances isDataset = getClassifierInstances(false, reviewFeatureVectors); // Dataset of review text instances for the classifier
		
		for (Instance instance : isDataset) {
			try {
				double prediction = cls.classifyInstance(instance);
				classifiedDocs.add(classValues.get((int) prediction));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return classifiedDocs;
		
	}

}
