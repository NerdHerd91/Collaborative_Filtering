import java.util.*;
import java.io.File;
import java.io.PrintWriter;

public class Collaborative {
	public static void main(String[] args) {
		// Map that holds all movies in the database.
		Map<Integer, Movie> movies = new HashMap<Integer, Movie>();
		// Maps that maps a user id to a subset of ratings given by that user.
		Map<Integer, Set<Rating>> trainRatings = new HashMap<Integer, Set<Rating>>();
		Map<Integer, Set<Rating>> testRatings = new HashMap<Integer, Set<Rating>>();

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
	* @param ratings Reference to the map to place Ratings we create into.
	* @param fileName File path to the file containing the ratings to parse.
	*/
	public static void parseRatings(Map<Integer, Set<Rating>> ratings, String fileName) {
		try {
			Scanner sc = new Scanner(new File(fileName));
			while (sc.hasNextLine()) {
				String[] tokens = sc.nextLine().split(",");
				int mid = Integer.parseInt(tokens[0]);
				int uid = Integer.parseInt(tokens[1]);
				double rating = Double.parseDouble(tokens[2]);

				if (!ratings.containsKey(uid)) {
					ratings.put(uid, new HashSet<Rating>());
				}
				ratings.get(uid).add(new Rating(mid, uid, rating));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double calculateWeightedSum() {
		return 0;
	}

	/**
	* Computes the mean rating that a user has given to movies.
	*
	* @param ratings Set of ratings for a particular user.
	* @return Returns a double indicating the average rating.
	*/
	public double calculateMean(Set<Rating> ratings) {
		double sum = 0;
		for (Rating r : ratings) {
			sum += r.getRating();
		}
		return sum / ratings.size();
	}

	public double calculateWeight() {
		return 0;
	}
}
