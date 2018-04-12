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
	
	private MongoCollection<Document> lProductsCol;
	private MongoCollection<Document> medProductsCol;
	private MongoCollection<Document> sProductsCol;
	
	public MongoDB() {
		mongoClient = new MongoClient("localhost");
		database = mongoClient.getDatabase("osdDb");
		reviewsCol = database.getCollection("reviews");
		reviewersCol = database.getCollection("reviewers");
		productsCol = database.getCollection("products");
		mProductsCol = database.getCollection("mProducts");
		
		lProductsCol = database.getCollection("lProds");
		medProductsCol = database.getCollection("mProds");
		sProductsCol = database.getCollection("sProds");
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
	
	////////////////////////////////////////////////////////////
	
	public void insertLProduct(Document doc) {
		lProductsCol.insertOne(doc);
	}
	
	public void insertMedProduct(Document doc) {
		medProductsCol.insertOne(doc);
	}
	
	public void insertSProduct(Document doc) {
		sProductsCol.insertOne(doc);
	}
	
	public FindIterable<Document> retrieveLProductsCollection() {
		FindIterable<Document> iterable = lProductsCol.find();
		
		return iterable;
	}
	
	public FindIterable<Document> retrieveMedProductsCollection() {
		FindIterable<Document> iterable = medProductsCol.find();
		
		return iterable;
	}
	
	public FindIterable<Document> retrieveSProductsCollection() {
		FindIterable<Document> iterable = sProductsCol.find();
		
		return iterable;
	}
	
	public FindIterable<Document> retrieveAnnotatedReviews() {
		FindIterable<Document> iterable = reviewsCol.find(new Document("scorem2", new Document("$gt", 0)));
		
		return iterable;
	}
	
	public void updateReviewInfos(String id, String info2, String info3) {
		reviewsCol.updateOne(new Document("_id", new ObjectId(id)), new Document("$set", new Document("info2", info2).append("info3", info3)));
	}
	///////////////////////////////////////////////////////////////
	
	// Update review spam score in reviews collection
	public void updateReviewScore(String id, double score) {
		reviewsCol.updateOne(new Document("_id", new ObjectId(id)), new Document("$set", new Document("scorem30", score)));
	}
	
	// Return spam reviews depending on their score exceeding the threshold
	public FindIterable<Document> retrieveSpamDocuments() {
		FindIterable<Document> iterable = reviewsCol.find(new Document("scorem30", new Document("$gt", 1.5)));
		
		return iterable;
	}
	
	// Return bottom-K reviews according to assigned spam score
	public FindIterable<Document> retrieveBottomKDocuments(int k) {
		FindIterable<Document> iterable = reviewsCol.find(new Document("scorem30", new Document("$gt", 0.0))).sort(new Document("scorem30", 1)).limit(k);
		
		return iterable;
	}
	
	// Update review spam score and details in reviews collection
	public void updateReviewScore(String id, double score, String info) {
		reviewsCol.updateOne(new Document("_id", new ObjectId(id)), new Document("$set", new Document("score2", score).append("info", info)));
	}
	
	// Remove review from reviews collection
	public void removeReview(String id) {
		reviewsCol.deleteOne(new Document("_id", new ObjectId(id)));
	}
	
	// Update reviewer history score in reviewers collection
	public void updateReviewerScore(String userid, String score) {
		reviewersCol.updateOne(new Document("userid", userid), new Document("$set", new Document("score", score)));
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
	
	public FindIterable<Document> retrieveProductsCollection() {
		FindIterable<Document> iterable = productsCol.find(new Document("reviews", new Document("$gt", 4)));
		
		return iterable;
	}
	
	// Return all products from products collection with more than 50 reviews
	public FindIterable<Document> retrieveLargeProductsCollection() {
		FindIterable<Document> iterable = productsCol.find(new Document("reviews", new Document("$gt", 50)));
		
		return iterable;
	}
	
	// Return all products from products collection with more than 20 and less than 51 reviews
	public FindIterable<Document> retrieveMediumProductsCollection() {
		FindIterable<Document> iterable = productsCol.find(new Document("reviews", new Document("$gt", 20).append("$lt", 51)));
		
		return iterable;
	}
		
	// Return all products from products collection with more than 5 and less than 21 reviews
	public FindIterable<Document> retrieveSmallProductsCollection() {
		FindIterable<Document> iterable = productsCol.find(new Document("reviews", new Document("$gt", 5).append("$lt", 21)));
		
		return iterable;
	}
	
	// Return top-K reviews according to assigned spam score
	public FindIterable<Document> retrieveTopKDocuments(int k) {
		FindIterable<Document> iterable = reviewsCol.find().sort(new Document("scorem2", -1)).limit(k);
		
		return iterable;
	}
	
	public void insertMproduct(Document doc) {
		mProductsCol.insertOne(doc);
	}
	
	public FindIterable<Document> retrieveMproductsCollection() {
		//FindIterable<Document> iterable = mProductsCol.find();
		FindIterable<Document> iterable = productsCol.find(new Document("mProduct", "1"));
		
		return iterable;
	}
	
	public long checkProduct(String pid) {
		return productsCol.count(new Document("pid", pid));
	}
	
	public void updateProduct(String pid) {
		productsCol.updateOne(new Document("pid", pid), new Document("$set", new Document("mProduct", "1")));
	}
	
}
