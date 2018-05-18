package co.com.globlant.chat.concrete;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.com.globlant.chat.concrete.ConsoleLog;

public class ConsoleLogTest {
	
	ConsoleLog consoleLog;
	private static final String MESSAGE = "This is a test";
	private static final String DEBUG_INFO_MESSAGE = "Thread Name: [" + Thread.currentThread().getName() + "] ";
	private ByteArrayOutputStream outputStream;
	
	@Before
	public void setup() {		
		outputStream = new ByteArrayOutputStream();		
		System.setOut(new PrintStream(outputStream));
	}
	
	@After
	public void tearDown() {
		System.setOut(System.out);
	}
	
	@Test
	public void writeMessageToStdOutTest() {
		boolean printDebugInfo = true;
		consoleLog = new ConsoleLog(printDebugInfo);
		consoleLog.writeLog(MESSAGE);
		String result = outputStream.toString().trim();
		assertEquals(MESSAGE, result);
	}
	
	@Test
	public void nonWriteMessageToStdOutNoDebugFlagEnableTest() {
		boolean printDebugInfo = false;
		
		consoleLog = new ConsoleLog(printDebugInfo);
		consoleLog.writeLog(MESSAGE);
		String result = outputStream.toString().trim();
		assertEquals(0, result.length());
		assertNotEquals(MESSAGE, result);
	}
	
	@Test
	public void writeMessageToStdOutPlusDebugInfoTest() {
		boolean printDebugInfo = true;
		boolean debugInfoStyle = true;
		consoleLog = new ConsoleLog(printDebugInfo,debugInfoStyle);
		consoleLog.writeLog(MESSAGE);
		String result = outputStream.toString().trim();
		assertEquals(String.format("%s%s", DEBUG_INFO_MESSAGE, MESSAGE), result);
	}

}
