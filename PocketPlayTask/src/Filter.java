/**
 * interface to filter out LogEntry List
 * 
 * @author 157462
 * 
 */
public interface Filter<T, E> {
	/**
	 * 
	 * @param object
	 *            : (here) meant for the LogEntry Object
	 * @param text
	 *            : (here) meant for the path
	 * @param type
	 *            : (here) meant for method (POST/GET)
	 * @return whether, there is a successful match
	 */
	public boolean isMatched(T object, E text, E type);
}