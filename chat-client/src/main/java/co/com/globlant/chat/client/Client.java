package co.com.globlant.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import co.com.globlant.chat.abstraction.IClient;
import co.com.globlant.chat.abstraction.Ilog;
import co.com.globlant.chat.common.IConstants;
import co.com.globlant.chat.concrete.ConsoleLog;

/***
 * Represent a client peer
 * 
 * @author JoseAlejandro
 *
 */
public class Client implements IClient {

	public final static int MAN_THREAD_DELAY = 5000;

	private Socket clientSocket;
	private String serverHostName;
	private int serverPortNumber;
	private Ilog log;
	private volatile boolean working;

	private BufferedReader inStream;
	private PrintWriter outStream;
	private BufferedReader consoleStdInput;

	private Thread consoleReaderListener;
	private Thread readerListener;

	public Client(String serverHostName, int serverPortNumber) {
		this.serverHostName = serverHostName;
		this.serverPortNumber = serverPortNumber;
		log = new ConsoleLog(IConstants.DEBUG_INFO);
		initializate();
	}

	/***
	 * Create a thread in charges of monitoring the input from the console
	 */
	private void consoleReaderListenerSetUp() {
		log.writeLog("consoleReaderListenerSetUp() - rising up consoleReaderListener Thread");
		consoleReaderListener = new Thread(new ConsoleClientReaderListener());
		consoleReaderListener.start();
	}

	/***
	 * Create a thread in charges of monitoring the input from the socket
	 */
	private void readerListenerSetUp() {
		log.writeLog("readerListenerSetUp() - rising up socketReaderListener Thread");
		readerListener = new Thread(new SocketReaderListener());
		readerListener.start();
	}

	/**
	 * Gets the socket's inputStream. Useful to read incoming data from the socket
	 * 
	 * @return {@link BufferedReader}
	 * @throws IOException
	 */
	public BufferedReader getInStream() throws IOException {
		if (inStream == null) {
			inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		}
		return inStream;
	}

	/**
	 * Gets the socket's outputStream. Useful to write to the other end of the
	 * socket
	 * 
	 * @return {@link PrintWriter}
	 * @throws IOException
	 */
	public PrintWriter getOutStream() throws IOException {
		if (outStream == null) {
			outStream = new PrintWriter(clientSocket.getOutputStream(), true);
		}
		return outStream;
	}

	/**
	 * Gets the console inputStream. Useful to read incoming data from the console
	 * 
	 * @return {@link BufferedReader}
	 */
	public BufferedReader getConsoleStdInput() {
		if (consoleStdInput == null) {
			consoleStdInput = new BufferedReader(new InputStreamReader(System.in));
		}
		return consoleStdInput;
	}

	@Override
	public void initializate() {
		try {
			log.write("Client Up.....");
			log.writeLog("initializate()");
			working = true;
			clientSocket = new Socket(serverHostName, serverPortNumber);
			log.writeLog("Client Socket created");
			log.writeLog(clientSocket.getLocalAddress() + " : " + clientSocket.getLocalPort());
			consoleReaderListenerSetUp();
			readerListenerSetUp();
			while (working) {				
				Thread.sleep(Client.MAN_THREAD_DELAY);
			}

		} catch (Exception e) {
			log.write(e.getMessage());
		} finally {
			terminate();
		}

	}

	@Override
	public void terminate() {
		try {
			working = false;
			log.writeLog("teminate() - called");
			log.writeLog("closing client socket");
			clientSocket.close();
		} catch (Exception e) {
			log.write("Exception terminate() - " + e.getMessage());
		}
		log.write("Client Down.....");
	}

	@Override
	public void sendMessage(String message) throws Exception {
		log.writeLog("sendMessage() - sending message through the socket: " + message);
		getOutStream().println(message);
	}

	@Override
	public String receiveMessage() throws Exception {
		log.writeLog("waiting for a receive a message");		
		return getInStream().readLine();
	}

	/**
	 * Inner class in charge of monitoring the console input
	 * 
	 * @author JoseAlejandro
	 *
	 */
	class ConsoleClientReaderListener implements Runnable {

		@Override
		public void run() {
			String messageRead = null;
			try {
				log.writeLog("ConsoleClientReaderListener -Entrando loop flujo de entrada de la console");
				while (working && (messageRead = getConsoleStdInput().readLine()) != null) {
					log.writeLog("ConsoleClientReaderListener - console message received - " + messageRead);
					getOutStream().println(messageRead);
				}
			} catch (Exception e) {
				log.write("ConsoleClientReaderListener - Exception while loop ConsoleClientReaderListener " + e.getMessage());
			}
			log.writeLog("ConsoleClientReaderListener - Saliendo del run() de ConsoleClientReaderListener");
		}

	}

	/**
	 * Inner class in charge of monitoring the socket input
	 * 
	 * @author JoseAlejandro
	 *
	 */
	class SocketReaderListener implements Runnable {

		@Override
		public void run() {
			String messageRead = null;
			try {
				log.writeLog("SocketReaderListener - getting in the loop from socket incoming messaging");
				while (working && (messageRead = receiveMessage()) != null) {
					log.write(messageRead);
					log.writeLog("SocketReaderListener - mesage received " + messageRead);
				}
			} catch (Exception e) {
				log.write("SocketReaderListener -Exception while loop " + e.getMessage());
			}
			finally {
				terminate();
			}
			log.writeLog("SocketReaderListener - Saliendo del run() de SocketReaderListener");
		}

	}	
}
