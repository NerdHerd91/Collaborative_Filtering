public class Movie {
	private int id;
	private int year;
	private String title;

	public Movie(int id, int year, String title) {
		this.id = id;
		this.year = year;
		this.title = title;
	}

	public int getId() {
		return this.id;
	}

	public int getYear() {
		return this.year;
	}

	public String getTitle() {
		return this.title;
	}
}
