public class Rating {
	private int movieId;
	private int userId;
	private double rating;

	public Rating(int movieId, int userId, double rating) {
		this.movieId = movieId;
		this.userId = userId;
		this.rating = rating;
	}

	public int getMovieId() {
		return this.movieId;
	}

	public int getUserId() {
		return this.userId;
	}

	public double getRating() {
		return this.rating;
	}
}
