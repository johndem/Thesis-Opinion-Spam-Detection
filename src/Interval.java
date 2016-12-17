import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Interval {
	
	private int intId;
	private LocalDate startDate;
	private LocalDate endDate;
	private List<Integer> reviews;
	private boolean suspicious;
	
	public Interval(int intId, LocalDate startDate, LocalDate endDate) {
		this.intId = intId;
		this.startDate = startDate;
		this.endDate = endDate;
		
		reviews = new ArrayList<Integer>();
		
		suspicious = false;
	}
	
	public int getIntervalId() {
		return intId;
	}
	
	public LocalDate getStartDate() {
		return startDate;
	}
	
	public LocalDate getEndDate() {
		return endDate;
	}
	
	public void addReviewId(int id) {
		reviews.add(id);
	}
	
	public List<Integer> getReviews() {
		return reviews;
	}
	
	public int getReviewSum() {
		return reviews.size();
	}
	
	public void setAsSuspicious() {
		suspicious = true;
	}

}
