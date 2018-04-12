import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Review {
	
	private String mongo_id;
	
	private int id;
	private String product_id;
	private String reviewer_id;
	
	DateTimeFormatter formatter;
	private LocalDate creationDate;
	private String testDate;
	
	private String reviewText;
	private double rating;
	
	private double contentLabel;
	private double contentSimilarityInBurst;
	private double ratingDeviation;
	private double reviewSpamScore;
	
	public Review(String mongo_id, String reviewer_id, double rating, String creationDate, String reviewText) {
		this.mongo_id = mongo_id;
		
		this.rating = rating;
		this.reviewer_id = reviewer_id;
		this.reviewText = reviewText;
		
		formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
		this.creationDate = LocalDate.parse(creationDate, formatter);
		
		contentLabel = 0.0;
		contentSimilarityInBurst = 0.0;
		ratingDeviation = 0.0;
		reviewSpamScore = 0.0;
		
		testDate = creationDate;
	}
	
	public Review(double rating, String creationDate, String product_id, String reviewText) {
		this.rating = rating;
		this.product_id = product_id;
		this.reviewText = reviewText;
		
		formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
		this.creationDate = LocalDate.parse(creationDate, formatter);
		
		testDate = creationDate;
	}
	
	public String getMongoId() {
		return mongo_id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public double getRating() {
		return rating;
	}
	
	public LocalDate getDate() {
		return creationDate;
	}
	
	public String getReviewText() {
		return reviewText;
	}
	
	public String getReviewerId() {
		return reviewer_id;
	}
	
	public String getProductId() {
		return product_id;
	}
	
	public void setContentLabel(double contentLabel) {
		this.contentLabel = contentLabel;
	}
	
	public void setContentSimilarityInBurst(double contentSimilarityInBurst) {
		this.contentSimilarityInBurst = contentSimilarityInBurst;
	}
	
	public void setRatingDeviation(double dev) {
		ratingDeviation = dev;
	}
	
	public double getRatingDeviation() {
		return ratingDeviation;
	}
	
	public double calculateReviewSpamScore(double reviewerScore) {
		//reviewSpamScore = 0.5 * contentLabel + 1 * ratingDeviation + 2 * contentSimilarityInBurst + reviewerScore;
		reviewSpamScore = 0.5 * contentLabel + 1 * ratingDeviation + reviewerScore;
		return reviewSpamScore;
	}
	
	public double getReviewSpamScore() {
		return reviewSpamScore;
	}
	
	public String getTestDate() {
		return testDate;
	}
	
	public void printReviewStats() {
		//System.out.println("Content Label: " + contentLabel);
		System.out.println("CSB: " + contentSimilarityInBurst);
		System.out.println("Rating Deviation: " + ratingDeviation);
	}
	
	public String getReviewStats() {
		return "CSB: " + contentSimilarityInBurst + " (" + 2 * contentSimilarityInBurst + ")" + "/" +
			"RD: " + ratingDeviation + " (" + 1 * ratingDeviation + ")" + "/";
	}

}
