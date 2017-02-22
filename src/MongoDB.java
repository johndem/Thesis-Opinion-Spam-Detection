import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDB {
	
	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> reviewsCol; // Collection containing product reviews and their associated information
	private MongoCollection<Document> reviewersCol; // Collection containing reviewers and their associated information
	private MongoCollection<Document> productsCol; // Collection containing unique products
	private MongoCollection<Document> mProductsCol; // Collection containing mProducts
	
	public MongoDB() {
		mongoClient = new MongoClient("localhost");
		database = mongoClient.getDatabase("opinionSpamDetectionDb");
		reviewsCol = database.getCollection("reviews1");
		reviewersCol = database.getCollection("reviewers1");
		productsCol = database.getCollection("products");
		mProductsCol = database.getCollection("mProducts");
	}
	
	// Insert review in reviews collection
	public void insertReview(Document doc) {
		reviewsCol.insertOne(doc);
	}
	
	// Insert reviewer in reviewers collection
	public void insertReviewer(Document doc) {
		reviewersCol.insertOne(doc);
	}
	
	// Insert reviewer in reviewers collection
	public void insertProduct(Document doc) {
		productsCol.insertOne(doc);
	}
	
	// Update review spam score in reviews collection
	public void updateReviewScore(String id, double score) {
		reviewsCol.updateOne(new Document("_id", new ObjectId(id)), new Document("$set", new Document("score2", score)));
	}
	
	// Update reviewer history score in reviewers collection
	public void updateReviewerScore(String userid, String score) {
		reviewersCol.updateOne(new Document("userid", userid), new Document("$set", new Document("score2", score)));
	}
	
	// Return all reviews for product ID
	public FindIterable<Document> retrieveProductReviews(String pid) {
		FindIterable<Document> iterable = reviewsCol.find(new Document("pid", pid));
		
		return iterable;
	}
	
	// Return all reviews by user ID
	public FindIterable<Document> retrieveUserReviews(String userid) {
		FindIterable<Document> iterable = reviewsCol.find(new Document("userid", userid));
		
		return iterable;
	}
	
	// Return all reviews from reviews collection
	public FindIterable<Document> retrieveReviewsCollection() {
		FindIterable<Document> iterable = reviewsCol.find();
		
		return iterable;
	}
	
	// Return reviewer based on user ID
	public FindIterable<Document> retrieveReviewer(String userid) {
		FindIterable<Document> iterable = reviewersCol.find(new Document("userid", userid));
		
		return iterable;
	}
	
	// Return all reviewers from reviewers collection
	public FindIterable<Document> retrieveReviewersCollection() {
		FindIterable<Document> iterable = reviewersCol.find();
		
		return iterable;
	}
	
	// Return all products from products collection
	public FindIterable<Document> retrieveProductsCollection() {
		FindIterable<Document> iterable = productsCol.find(new Document("reviews", new Document("$gt", 50)));
		
		return iterable;
	}
	
	// Return top-K reviews according to assigned spam score
	public FindIterable<Document> retrieveTopKDocuments(int k) {
		FindIterable<Document> iterable = reviewsCol.find().sort(new Document("score2", -1)).limit(k);
		
		return iterable;
	}
	
	// Return bottom-K reviews according to assigned spam score
	public FindIterable<Document> retrieveBottomKDocuments(int k) {
		FindIterable<Document> iterable = reviewsCol.find(new Document("score2", new Document("$gt", 0.0))).sort(new Document("score", 1)).limit(k);
		
		return iterable;
	}
	
	
	public void insertMproduct(Document doc) {
		mProductsCol.insertOne(doc);
	}
	
	public FindIterable<Document> retrieveMproductsCollection() {
		FindIterable<Document> iterable = mProductsCol.find();
		
		return iterable;
	}
	
	public long checkProduct(String pid) {
		return productsCol.count(new Document("pid", pid));
	}
	
	public void updateProduct(String pid) {
		productsCol.updateOne(new Document("pid", pid), new Document("$set", new Document("mProduct", "1")));
	}

}
