import java.util.Map;

public class User {
	private int id;
	private Map<Integer, Rating> ratings;
	private double mean;

	public User(int id, Map<Integer, Rating> ratings) {
		this.id = id;
		this.ratings = ratings;
	}

	public int getId() {
		return this.id;
	}

	public Map<Integer, Rating> getRatings() {
		return this.ratings;
	}

	public double getMean() {
		return this.mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}
}
