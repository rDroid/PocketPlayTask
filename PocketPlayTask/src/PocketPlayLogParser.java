import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to parse the log entry using regex.
 * 
 * @author 157462
 * 
 */
public class PocketPlayLogParser {

	// --------------define constants------------------------
	static final String logEntryPattern = "^([.\\d-T:+]+) ([\\w\\[\\]]+)(:) (at=)(\\w+) (method=)(\\w+) (path=)([/\\w]+) (host=)([\\w.]+) (fwd=\"[\\d.]+\") (dyno=)([\\w.]+) (connect=)([\\d]+)(ms) (service=)(\\d+)(ms) (status=)(\\d+) (bytes=)(\\d+)";
	static int INDEXOFMETHOD = 7;
	static int INDEXOFPATH = 9;
	static int INDEXOFDYNO = 14;
	static int INDEXOFCONNECT = 16;
	static int INDEXOFSERVICE = 19;

	static Pattern p;
	Matcher matcher;

	public PocketPlayLogParser() {
		// initialize the pattern object.
		p = Pattern.compile(logEntryPattern);
	}

	/**
	 * 
	 * @param textToParse
	 *            : the text to be matched against teh regex.
	 * @return : an Object of LogEntry containing the information required if
	 *         the regex matches.
	 */
	public LogEntry parse(String textToParse) {
		LogEntry mLogEntry = null;
		// System.out.println("using Pattern"+logEntryPattern);
		// System.out.println(textToParse);
		matcher = p.matcher(textToParse);
		if (matcher.find()) {
			mLogEntry = new LogEntry();
			mLogEntry.setMethod(matcher.group(INDEXOFMETHOD));
			mLogEntry.setPath(matcher.group(INDEXOFPATH));
			mLogEntry.setDyno(matcher.group(INDEXOFDYNO));
			mLogEntry.setConnectTime(matcher.group(INDEXOFCONNECT));
			mLogEntry.setServiceTime(matcher.group(INDEXOFSERVICE));
		}
		return mLogEntry;
	}

}
