import java.util.*;
import java.io.File;
import java.io.PrintWriter;

public class Collaborative {
	public static final int NORMALIZING_FACTOR = 1;

	public static void main(String[] args) {
		// Maps that maps a user id to a subset of ratings given by that user.
		Map<Integer, User> trainRatings = new HashMap<Integer, User>();
		Map<Integer, User> testRatings = new HashMap<Integer, User>();

		// Parse training and test data into maps.
		System.out.println("1. Parsing DataSets");
		parseRatings(trainRatings, "./DataSet/TrainingRatings.txt");
		parseRatings(testRatings, "./DataSet/TestingRatings.txt");

		// Fill Map of means.
		System.out.println("2. Calculating Mean Values");
		for (Integer uid : trainRatings.keySet()) {
			trainRatings.get(uid).setMean(calculateMean(trainRatings.get(uid).getRatings()));
		}

		// Predict ratings for users.
		System.out.println("3. Predicting Ratings");
		int total = 0;
		double errorSum = 0;
		for (Integer uid : testRatings.keySet()) {
			// Pre-Compute Weights for each active user
			Map<Integer, Double> weights = new HashMap<Integer, Double>();
			for (Integer uid2 : trainRatings.keySet()) {
				weights.put(uid2, calculateWeight(trainRatings.get(uid2), trainRatings.get(uid)));
			}

			// Predict movie ratings and determine error summation
			for (Integer mid : testRatings.get(uid).getRatings().keySet()) {
				Rating r = testRatings.get(uid).getRatings().get(mid);
				errorSum += Math.abs(r.getRating() - calculateWeightedSum(trainRatings, trainRatings.get(uid), mid, weights));
				total++;
				if (total % 4000 == 0) { System.out.println("Total: " + total); }
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
	public static void parseRatings(Map<Integer, User> ratings, String fileName) {
		try {
			Scanner sc = new Scanner(new File(fileName));
			while (sc.hasNextLine()) {
				String[] tokens = sc.nextLine().split(",");
				int mid = Integer.parseInt(tokens[0]);
				int uid = Integer.parseInt(tokens[1]);
				double rating = Double.parseDouble(tokens[2]);

				if (!ratings.containsKey(uid)) {
					ratings.put(uid, new User(uid, new HashMap<Integer, Rating>()));
				}
				ratings.get(uid).getRatings().put(mid, new Rating(mid, uid, rating));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Computes the weighted sum for a particular user on a particular item.
	*
	* @param train Map from userId to User.
	* @param test User object for active user.
	* @param mid Integer movie id.
	* @param weights Map from uid to weight between uid and the active user.
	* @return Double indicated weighted sum.
	*/
	public static double calculateWeightedSum(Map<Integer, User> train, User test, int mid, Map<Integer, Double> weights) {
		double sum = 0;
		double sumK = 0;
		for (Integer uid : train.keySet()) {
			User user = train.get(uid);
			Map<Integer, Rating> ratings = user.getRatings();
			if (ratings.containsKey(mid)) {
				sum += weights.get(uid) * (ratings.get(mid).getRating() - user.getMean());
				sumK += Math.abs(weights.get(uid)); 
			}
		}
		return test.getMean() + (1 / sumK) * sum;
	}

	/**
	* Calculates the weight for two users over all items they share recorded ratings for.
	*
	* @param train User object for particular user in training data.
	* @param test User object for active user.
	* @return Returns a Double representing the computed weight.
	*/
	public static double calculateWeight(User train, User test) {
		double numSum = 0;
		double denTestSum = 0;
		double denTrainSum = 0;
		Map<Integer, Rating> ratings = train.getRatings();

		for (Rating r : test.getRatings().values()) {
			if (ratings.containsKey(r.getMovieId())) {
				numSum += (r.getRating() - test.getMean()) * (ratings.get(r.getMovieId()).getRating() - train.getMean());
				denTestSum += Math.pow(r.getRating() - test.getMean(), 2);
				denTrainSum += Math.pow(ratings.get(r.getMovieId()).getRating() - train.getMean(), 2);
			}
		}
		return (denTestSum == 0 || denTrainSum == 0) ? 0 : numSum / Math.sqrt(denTestSum * denTrainSum);
	}

	/**
	* Computes the mean rating that a user has given to movies.
	*
	* @param ratings Map of ratings for a particular user.
	* @return Returns a double indicating the average rating.
	*/
	public static double calculateMean(Map<Integer, Rating> ratings) {
		double sum = 0;
		for (Rating r : ratings.values()) {
			sum += r.getRating();
		}
		return sum / ratings.size();
	}
}
