import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TestReview {
	
	private int id;
	private String product_id;
	private String reviewer_id;
	
	private String creationDate;
	
	private String content;
	private double rating;
	
	public TestReview(int id, double rating, String creationDate) {
		this.id = id;
		this.rating = rating;
		this.creationDate = creationDate;
	}
	
	public int getId() {
		return id;
	}
	
	public double getRating() {
		return rating;
	}
	
	public String getDate() {
		return creationDate;
	}

}
