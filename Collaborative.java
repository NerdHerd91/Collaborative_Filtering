import java.util.*;
import java.io.File;
import java.io.PrintWriter;

public class Collaborative {
	public static void main(String[] args) {
		// Map that holds all movies in the database.
		Map<Integer, Movie> movies = new HashMap<Integer, Movie>();
		// Maps that maps a user id to a subset of ratings given by that user.
		Map<Integer, Map<Integer, Rating>> trainRatings = new HashMap<Integer, Map<Integer, Rating>>();
		Map<Integer, Map<Integer, Rating>> testRatings = new HashMap<Integer, Map<Integer, Rating>>();

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

		int k = 1;
		int matches = 0;
		// Predict ratings for users.
		for (Integer uid : testRatings.keySet()) {
			for (Integer mid : testRatings.get(uid).keySet()) {
				Rating r = testRatings.get(uid).get(mid);
				double pred = calculateWeightedSum(trainRatings, new HashSet<Rating>(trainRatings.get(uid).values()), mid, k);
				if (r.getRating() == pred) {
					matches++;
				}
			}
		}

		// Compute accuracy of algorithm.
		System.out.println("Matches: " + matches);
	}

	/**
	* Parses a file containing ratings for movies.
	*
	* @param ratings Reference to the map to place Ratings we create into.
	* @param fileName File path to the file containing the ratings to parse.
	*/
	public static void parseRatings(Map<Integer, Map<Integer, Rating>> ratings, String fileName) {
		try {
			Scanner sc = new Scanner(new File(fileName));
			while (sc.hasNextLine()) {
				String[] tokens = sc.nextLine().split(",");
				int mid = Integer.parseInt(tokens[0]);
				int uid = Integer.parseInt(tokens[1]);
				double rating = Double.parseDouble(tokens[2]);

				if (!ratings.containsKey(uid)) {
					ratings.put(uid, new HashMap<Integer, Rating>());
				}
				ratings.get(uid).put(mid, new Rating(mid, uid, rating));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Computes the weighted sum for a particular user on a particular item.
	*
	* @param train Map from userId to Map of ratings for that user.
	* @param test Set of ratings for the active user.
	* @param mid Integer movie id.
	* @param k Integer normalization factor.
	* @return Double indicated weighted sum.
	*/
	public static double calculateWeightedSum(Map<Integer, Map<Integer, Rating>> train, Set<Rating> test, int mid, int k) {
		double meanTest = calculateMean(test);
		double sum = 0;
		for (Integer uid : train.keySet()) {
			Set<Rating> ratings = new HashSet<Rating>(train.get(uid).values());
			if (train.get(uid).containsKey(mid)) {
				sum += calculateWeight(train.get(uid), test) * (train.get(uid).get(mid).getRating() - calculateMean(ratings));
			}
		}
		return meanTest + k * sum;
	}

	/**
	* Calculates the weight for two users over all items they share recorded ratings for.
	*
	* @param train Map from mid to Rating for a particular user (i).
	* @param test Set of Ratings for a particular active user.
	* @return Returns a Double representing the computed weight.
	*/
	public static double calculateWeight(Map<Integer, Rating> train, Set<Rating> test) {
		double numSum = 0;
		double denTestSum = 0;
		double denTrainSum = 0;
		double meanTest = calculateMean(test);
		double meanTrain = calculateMean(new HashSet<Rating>(train.values()));

		for (Rating r : test) {
			if (train.containsKey(r.getMovieId())) {
				numSum += (r.getRating() - meanTest) * (train.get(r.getMovieId()).getRating() - meanTrain);
				denTestSum += Math.pow((r.getRating() - meanTest), 2);
				denTrainSum += Math.pow((train.get(r.getMovieId()).getRating() - meanTrain), 2);
			}
		}
		return numSum / Math.sqrt(denTestSum * denTrainSum);
	}

	/**
	* Computes the mean rating that a user has given to movies.
	*
	* @param ratings Set of ratings for a particular user.
	* @return Returns a double indicating the average rating.
	*/
	public static double calculateMean(Set<Rating> ratings) {
		double sum = 0;
		for (Rating r : ratings) {
			sum += r.getRating();
		}
		return sum / ratings.size();
	}

	/**
	* Determines if a set of ratings contains a certain movie.
	*
	* @param mid Integer movie id.
	* @param ratings Set of ratings for a particular user.
	* @return Boolean indicating if movie was found in the set.
	*/
	private static boolean containsMovie(int mid, Set<Rating> ratings) {
		for (Rating r : ratings) {
			if (r.getMovieId() == mid) {
				return true;
			}
		}
		return false;
	}
}
