import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Review {
	
	private int id;
	private String product_id;
	private String reviewer_id;
	
	DateTimeFormatter formatter;
	private LocalDate creationDate;
	
	private String reviewText;
	private double rating;
	
	public Review(int id, String reviewer_id, double rating, String creationDate, String reviewText) {
		this.id = id;
		this.rating = rating;
		this.reviewer_id = reviewer_id;
		this.reviewText = reviewText;
		
		formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
		this.creationDate = LocalDate.parse(creationDate, formatter);
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

}
