import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDB {
	
	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> reviewsCol; // Collection containing product reviews and their associated information
	
	public MongoDB() {
		mongoClient = new MongoClient("localhost");
		database = mongoClient.getDatabase("opinionSpamDetectionDb");
		reviewsCol = database.getCollection("reviews");
	}
	
	// Insert review in reviews collection
	public void insertReview(Document doc) {
		reviewsCol.insertOne(doc);
	}
	
	// Return all reviews for product ID
	public FindIterable<Document> retrieveProductReviews(String pid) {
		FindIterable<Document> iterable = database.getCollection("reviews").find(new Document("pid", pid));
		
		return iterable;
	}
	
	// Return all reviews by user ID
	public FindIterable<Document> retrieveUserReviews(String userid) {
		FindIterable<Document> iterable = database.getCollection("reviews").find(new Document("userid", userid));
		
		return iterable;
	}
	
	// Return all reviews from reviews collection
	public FindIterable<Document> retrieveReviewsCollection() {
		FindIterable<Document> iterable = database.getCollection("reviews").find();
		
		return iterable;
	}

}
