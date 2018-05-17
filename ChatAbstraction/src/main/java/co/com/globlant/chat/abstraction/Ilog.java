package co.com.globlant.chat.abstraction;

/**
 * Represents a log mechanism
 * @author JoseAlejandro
 *
 */
public interface Ilog {
	
	/**
	 * Write in debug mode
	 * @param message String
	 */
	void writeLog(String message);
	/**
	 * Write directly to the output
	 * @param message
	 */
	void write(String message);

}
