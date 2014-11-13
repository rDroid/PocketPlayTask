import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class for carrying out analysis on a file.
 * 
 * @author 157462
 * 
 */
public class LogAnalyzer {

	// ------------define constants------------------------------
	private static final String PATH_FILTER = "/api/users/";
	private static final String PATH_COUNT_PENDING_MESSAGES = "/api/users/%s/count_pending_messages";
	private static final String PATH_GET_MESSAGES = "/api/users/%s/get_messages";
	private static final String PATH_GET_FRIENDS_PROGRESS = "/api/users/%s/get_friends_progress";
	private static final String PATH_GET_FRIENDS_SCORE = "/api/users/%s/get_friends_score";
	private static final String PATH_USER = "/api/users/%s";

	// ------------end define constants------------------------------

	/**
	 * 
	 * @param filePath
	 *            : the path of file
	 * @param parser
	 *            : an object of PocketPlayParser
	 * @return
	 * @throws IOException
	 */
	public List<LogEntry> LoadLogsFromFile(String filePath,
			PocketPlayLogParser parser) throws IOException {
		if (filePath == null || filePath.trim().length() == 0) {
			throw new IllegalArgumentException("filePath not specified");
		}
		if (parser == null) {
			throw new IllegalArgumentException(
					"An instance of PocketPlayParser is required");
		}
		List<LogEntry> logs = new ArrayList<>();
		File mFile = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(mFile));
		String line;
		while ((line = br.readLine()) != null) {
			// process the line.
			LogEntry entry = parser.parse(line);
			if (entry != null) {
				if (entry.getPath().startsWith(PATH_FILTER))
					logs.add(entry);
			}
		}
		br.close();
		return logs;
	}

	// define our filter
	static Filter<LogEntry, String> filterPath = new Filter<LogEntry, String>() {
		/**
		 * 
		 * @param object
		 *            : the logEntry Object
		 * @param text
		 *            : the path
		 * @param type
		 *            : method (POST/GET)
		 * @return
		 */
		public boolean isMatched(LogEntry object, String text, String type) {
			return object.getGenericPath().equals(text);
		}
	};

	/**
	 * performs analysis on given paths/methods combo
	 * 
	 * @param logs
	 *            : the list of log entries
	 */
	public void analyzeLogs(List<LogEntry> logs) {
		// analysis for GET /api/users/{user}/count_pending_messages
		performAnalysis(logs, PATH_COUNT_PENDING_MESSAGES, "GET");

		// analysis for GET GET /api/users/{user_id}/get_messages
		performAnalysis(logs, PATH_GET_MESSAGES, "GET");

		// analysis for GET /api/users/{user_id}/get_friends_progress
		performAnalysis(logs, PATH_GET_FRIENDS_PROGRESS, "GET");

		// analysis for GET /api/users/{user_id}/get_friends_score
		performAnalysis(logs, PATH_GET_FRIENDS_SCORE, "GET");

		// analysis for POST /api/users/{user_id}

		performAnalysis(logs, PATH_USER, "POST");

		// analysis for GET /api/users/{user_id}
		performAnalysis(logs, PATH_USER, "GET");
	}

	/**
	 * Performs analysis and prints the results on the console
	 * 
	 * @param logs
	 *            : the list of log entries
	 * @param path
	 *            : the path which needs to be compared
	 * @param method
	 *            : GET or POST only.
	 */
	private void performAnalysis(List<LogEntry> logs, String path, String method) {
		List<LogEntry> filteredList = new FilterList<String>().filterList(logs,
				filterPath, path, method);
		System.out
				.println("------------------------------------------------------");
		System.out.println(method + " " + String.format(path, "{user}"));
		System.out
				.println("------------------------------------------------------");
		System.out.println("Total number of calls to the endpoint"
				+ filteredList.size());
		double mean = calculateMean(filteredList);
		System.out.println("Mean response time=" + mean + "ms");

		double median = calculateMedian(filteredList);
		System.out.println("Median response time=" + median + "ms");

		System.out.println("Calculating mode response time...");
		double mode = calculateMode(filteredList);
		System.out.println("Mode response time=" + mode + "ms");

		System.out.println("Calculating most active dyno...");
		String dynoName = getMaxServedDyno(logs);
		System.out.println("Dyno that server most=" + dynoName);

		System.out
				.println("------------------------------------------------------");
		System.out
				.println("------------------------------------------------------");
	}

	/**
	 * 
	 * @param logs
	 *            : list of log entries
	 * @return : the mean of response times.
	 */
	public double calculateMean(List<LogEntry> logs) {
		int mean = 0;
		if (logs != null) {
			int count = logs.size();
			if (count > 0) {
				for (LogEntry entry : logs) {
					mean += entry.getTotalTime();
				}
				mean = mean / count;
			}
		}
		return mean;
	}

	/**
	 * 
	 * @param logs
	 *            : list of log entries
	 * @return : the median value of response times
	 */
	public double calculateMedian(List<LogEntry> logs) {
		// sort List
		double median = 0;
		if (logs != null) {
			Collections.sort(logs);
			int count = logs.size();
			if (count > 0) {
				if (count % 2 == 0) {
					int indexA = (count - 1) / 2;
					int indexB = count / 2;
					median = (logs.get(indexA).getTotalTime() + logs
							.get(indexB).getTotalTime()) / 2;
				} else {
					int index = (count - 1) / 2;
					median = logs.get(index).getTotalTime();
				}
			}
		}
		return median;

	}

	/**
	 * note: the collections.frequency calculates the frequency of response time
	 * instead of frequency of object, due to modification of equals function in
	 * LogEntry class.
	 * 
	 * @param logs
	 *            : the list of logentries
	 * @return the response time that occurs most times.
	 */
	public double calculateMode(List<LogEntry> logs) {
		double mode = 0;
		int maxFrequency = 0;
		for (LogEntry entry : logs) {
			int frequency = Collections.frequency(logs, entry);
			if (frequency >= maxFrequency) {
				maxFrequency = frequency;
				mode = entry.getTotalTime();
			}
		}
		return mode;
	}

	/**
	 * 
	 * @param logs
	 *            : list of LogEntries
	 * @return : mode based on dyno.
	 */
	public String getMaxServedDyno(List<LogEntry> logs) {

		String dynoName = null;
		int maxFrequency = 0;
		for (LogEntry entry : logs) {
			int frequency = calculateDynoFrequency(logs, entry);
			if (frequency >= maxFrequency) {
				maxFrequency = frequency;
				dynoName = entry.getDyno();
			}
		}
		return dynoName;

	}

	/**
	 * This method performs same function as Collections.frequency()
	 * 
	 * @param logs
	 *            : the list to analyze
	 * @param entry
	 *            ; the object to get frequency of.F
	 * @return
	 */
	private int calculateDynoFrequency(List<LogEntry> logs, LogEntry entry) {

		int result = 0;
		if (entry == null) {
			for (LogEntry e : logs)
				if (e == null)
					result++;
		} else {
			for (LogEntry e : logs)
				if (entry.getDyno().equals(e.getDyno()))
					result++;
		}
		return result;

	}
}
