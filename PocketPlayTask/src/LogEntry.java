import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to represent a single log entry,
 * 
 * @author 157462
 * @note: implements Comparable, so we can sort the List of logs using the total
 *        response time.
 */
/**
 * @author 157462
 *
 */
/**
 * @author 157462
 * 
 */
public class LogEntry implements Comparable<LogEntry> {

	private String method;
	private String path;
	private String dyno;
	private double connectTime;
	private double serviceTime;
	private double totalTime;
	private String username;
	private String genericPath;
	private static Pattern p;
	private static final String USER_REGEX = "(\\d+)";

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public LogEntry() {
		p = Pattern.compile(USER_REGEX);
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	/**
	 * replaces the userID to a generic string, so we can run on path
	 * 
	 * @param path
	 *            : the path in the log entry
	 */
	public void setPath(String path) {
		this.path = path;
		if (path != null) {
			Matcher userMatcher = p.matcher(path);
			if (userMatcher.find()) {
				setUsername(userMatcher.group(1));
				setGenericPath(userMatcher.replaceAll("%s"));
			}
		}

	}

	/**
	 * @return the genericPath
	 */
	public String getGenericPath() {
		return genericPath;
	}

	/**
	 * generic path will be devoid of userID information
	 * 
	 * @param genericPath
	 *            the genericPath to set
	 */
	public void setGenericPath(String genericPath) {
		this.genericPath = genericPath;
	}

	public String getDyno() {
		return dyno;
	}

	public void setDyno(String dyno) {
		this.dyno = dyno;
	}

	public double getConnectTime() {
		return connectTime;
	}

	public void setConnectTime(String connectTime) {
		try {
			this.connectTime = Double.parseDouble(connectTime);
			calculateTotalTime();
		} catch (Exception e) {
			this.connectTime = 0;
		}

	}

	public double getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(String serviceTime) {
		try {
			this.serviceTime = Double.parseDouble(serviceTime);
			calculateTotalTime();
		} catch (Exception e) {
			this.serviceTime = 0;
		}
	}

	public void calculateTotalTime() {
		totalTime = serviceTime + connectTime;
	}

	public double getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}

	@Override
	public int compareTo(LogEntry o) {
		// TODO Auto-generated method stub
		return (int) (this.getTotalTime() - o.getTotalTime());
	}

	/**
	 * Modified equals method, for calculation of mode. So that w can get
	 * frequency of item in collection using Collections.frequency() method
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		} else {
			return this.getTotalTime() == ((LogEntry) obj).getTotalTime();
		}
		// return super.equals(obj);
	}

}
