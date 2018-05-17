package co.com.globlant.chat.concrete;

import java.io.PrintStream;

import co.com.globlant.chat.abstraction.Ilog;

/**
 * Concrete class which log info to the stdout
 * @author JoseAlejandro
 *
 */
public class ConsoleLog implements Ilog {

	private PrintStream printStream;
	private boolean printDebugInfo;
	private boolean debugInfoStyle;

	/**
	 * Constructor 1 Set log flags to it default value. (false)
	 */
	public ConsoleLog() {
		this(false, false);
	}

	/**
	 * Constructor 2
	 * @param printDebugInfo prints text according to the printDebugInfo flag
	 */
	public ConsoleLog(boolean printDebugInfo) {
		this(printDebugInfo, false);
	}

	/**
	 * Constructor 3
	 * @param printDebugInfo prints text according to the printDebugInfo flag
	 * @param debugInfoStyle prints debugging extra info  according to the debugInfoStyle flag
	 */
	public ConsoleLog(boolean printDebugInfo, boolean debugInfoStyle) {
		this.printDebugInfo = printDebugInfo;
		this.debugInfoStyle = debugInfoStyle;
		printStream = System.out;
	}

	@Override
	public void writeLog(String message) {
		if (printDebugInfo) {
			write(message);
		}
	}

	@Override
	public void write(String message) {
		if(debugInfoStyle) {
			debugInfoStyle();
		}
		printStream.println(message);
	}
	
	private void debugInfoStyle() {
		printStream.print("Thread Name: [" + Thread.currentThread().getName() + "]");
		printStream.print(" ");
	}

}
