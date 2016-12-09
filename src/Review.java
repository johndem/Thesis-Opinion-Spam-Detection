
public class Review {
	
	private int id;
	private String product_id;
	private String reviewer_id;
	private String creationDate;
	private String content;
	private double rating;
	
	public Review(int id, double rating) {
		this.id = id;
		this.rating = rating;
	}
	
	public double getRating() {
		return rating;
	}

}
