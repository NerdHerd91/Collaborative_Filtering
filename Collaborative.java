import java.util.*;
import java.io.File;
import java.io.PrintWriter;

public class Collaborative {
	public static void main(String[] args) {
		Map<Integer, Movie> movies = new HashMap<Integer, Movie>();
		Set<Rating> trainRatings = new HashSet<Rating>();
		Set<Rating> testRatings = new HashSet<Rating>();

		// Parse the data from the files in ./DataSet directory
		try {
			Scanner sc = new Scanner(new File("./DataSet/movie_titles.txt"));
			while (sc.hasNextLine()) {
				String[] tokens = sc.nextLine().split(",");
				int id = Integer.parseInt(tokens[0]);
				int year = (tokens[1].equals("NULL")) ? -1 : Integer.parseInt(tokens[1]);
				String title = tokens[2];
				movies.put(id, new Movie(id, year, title));
			}
			parseRatings(trainRatings, "./DataSet/TrainingRatings.txt");
			parseRatings(testRatings, "./DataSet/TestingRatings.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	* Parses a file containing ratings for movies.
	*
	* @param ratings Reference to the set to place Ratings we create into.
	* @param fileName File path to the file containing the ratings to parse.
	*/
	public static void parseRatings(Set<Rating> ratings, String fileName) {
		try {
			Scanner sc = new Scanner(new File(fileName));
			while (sc.hasNextLine()) {
				String[] tokens = sc.nextLine().split(",");
				int mid = Integer.parseInt(tokens[0]);
				int uid = Integer.parseInt(tokens[1]);
				double rating = Double.parseDouble(tokens[2]);
				ratings.add(new Rating(mid, uid, rating));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
