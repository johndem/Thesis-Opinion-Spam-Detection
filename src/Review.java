import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Review {
	
	private int id;
	private String product_id;
	private String reviewer_id;
	
	DateTimeFormatter formatter;
	private LocalDate creationDate;
	
	private String content;
	private double rating;
	
	public Review(int id, double rating, String creationDate) {
		this.id = id;
		this.rating = rating;
		
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

}
