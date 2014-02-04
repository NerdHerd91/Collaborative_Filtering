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
		// Maps that map a user id to their mean.
		Map<Integer, Double> testMean = new HashMap<Integer, Double>();

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

		// Fill Map of means.
		for (Integer uid : trainRatings.keySet()) {
			testMean.put(uid, calculateMean(new HashSet<Rating>(trainRatings.get(uid).values())));
		}

		int k = 1;
		int total = 0;
		double errorSum = 0;
		// Predict ratings for users.
		for (Integer uid : testRatings.keySet()) {
			for (Integer mid : testRatings.get(uid).keySet()) {
				Rating r = testRatings.get(uid).get(mid);
				errorSum += Math.abs(r.getRating() - calculateWeightedSum(trainRatings, new HashSet<Rating>(trainRatings.get(uid).values()), testMean, uid,  mid, k));
				total++;
			}
		}

		// Compute accuracy of algorithm.
		System.out.printf("Percent Error: %.2f%%\n", (errorSum / total * 100));
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
	* @param means Map from uid to mean value.
	* @param uidA Integer user id.
	* @param mid Integer movie id.
	* @param k Integer normalization factor.
	* @return Double indicated weighted sum.
	*/
	public static double calculateWeightedSum(Map<Integer, Map<Integer, Rating>> train, Set<Rating> test, Map<Integer, Double> means, int uidA, int mid, int k) {
		double sum = 0;
		for (Integer uid : train.keySet()) {
			Map<Integer, Rating> user = train.get(uid);
			Set<Rating> ratings = new HashSet<Rating>(user.values());
			if (user.containsKey(mid)) {
				sum += calculateWeight(user, test, means, uidA, uid) * (user.get(mid).getRating() - means.get(uid));
			}
		}
		return means.get(uidA) + k * sum;
	}

	/**
	* Calculates the weight for two users over all items they share recorded ratings for.
	*
	* @param train Map from mid to Rating for a particular user (i).
	* @param test Set of Ratings for a particular active user.
	* @param means Map from uid to mean.
	* @param uid1 Integer user id for test.
	* @param uid2 Integer user id for train
	* @return Returns a Double representing the computed weight.
	*/
	public static double calculateWeight(Map<Integer, Rating> train, Set<Rating> test, Map<Integer, Double>means, int uid1, int uid2) {
		double numSum = 0;
		double denTestSum = 0;
		double denTrainSum = 0;

		for (Rating r : test) {
			if (train.containsKey(r.getMovieId())) {
				numSum += (r.getRating() - means.get(uid1)) * (train.get(r.getMovieId()).getRating() - means.get(uid2));
				denTestSum += Math.pow((r.getRating() - means.get(uid1)), 2);
				denTrainSum += Math.pow((train.get(r.getMovieId()).getRating() - means.get(uid2)), 2);
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
}
