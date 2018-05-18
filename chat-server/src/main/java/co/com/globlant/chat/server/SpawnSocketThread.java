package co.com.globlant.chat.server;

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
 * Represent a new client socket executing in it own thread
 * 
 * @author JoseAlejandro
 *
 */
public class SpawnSocketThread implements IClient,Runnable {

	private int id;
	private Socket clientSocket;
	private Ilog log;
	private BufferedReader inStream;
	private PrintWriter outStream;
	private boolean working;
	private SpawnSocketThreadManagement spawnSocketThreadManagement;

	private Thread readerListener;

	public int getId() {
		return id;
	}

	/***
	 * Create a thread in charges of monitoring the input from the socket
	 */
	private void readerListenerSetUp() {
		log.writeLog("readerListenerSetUp() - rising up SocketServerReaderListener Thread");
		readerListener = new Thread(new SocketServerReaderListener());
		readerListener.start();
	}

	/***
	 * Constructor
	 * 
	 * @param clientSocket
	 */
	public SpawnSocketThread(int id, Socket clientSocket) {
		this.id = id;
		this.clientSocket = clientSocket;
		log = new ConsoleLog(IConstants.DEBUG_INFO,IConstants.DEBUG_INFO_STYLE);
		spawnSocketThreadManagement = SpawnSocketThreadManagement.getThreadSocketManagementInstance();
	}

	public void initializate() {
		try {			
			log.writeLog("SpawnSocketThread - initializate()");
			working=true;
			log.writeLog("SpawnSocketThread - while loop");
			log.write("Spawn Socket ID" + id + " Up.....");
			readerListenerSetUp();
			while (working) {								
				Thread.sleep(IConstants.MAN_THREAD_DELAY);
			}
		} catch (Exception e) {
			log.write(e.getMessage());
		}
		finally {
			terminate();
		}
	}
	
	/**
	 * Message that is send to every spawn socket
	 * @param message text message
	 */
	private void broadCastReceivedMessage(String message) throws Exception  {
		log.writeLog("Sending message to BroadCasting : " + message);
		spawnSocketThreadManagement.sendMessageToAll(message, id);
	}

	@Override
	public void sendMessage(String message) throws Exception {
		log.writeLog("Sending message " + message + " to client id: " + id);
		getOutStream().println(message);
	}

	@Override
	public String receiveMessage() throws Exception {
		log.writeLog("waitin to receive a message");
		return getInStream().readLine();
	}

	/**
	 * Gets the socket's inputStream. Useful to read incoming data from the socket
	 * @return
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
	 * @return
	 * @throws IOException
	 */
	public PrintWriter getOutStream() throws IOException {
		if (outStream == null) {
			outStream = new PrintWriter(clientSocket.getOutputStream(), true);
		}
		return outStream;
	}

	@Override
	public void terminate() {		
		try {
			log.writeLog("terminate()");
			working=false;
			clientSocket.close();
		} catch (Exception e) {
			log.write(e.getMessage());
		}
		log.write("Spawn Socket ID " + id + " Down.....");
	}

	@Override
	public void run() {
		log.writeLog("run() - Executing spawn thread");
		initializate();		
	}
	
	class SocketServerReaderListener implements Runnable {

		@Override
		public void run() {
			String messageRead = null;
			try {
				log.writeLog("SocketServerReaderListener - getting in the loop from socket incoming messaging");
				while (working && (messageRead = receiveMessage()) != null) {
					log.writeLog("SocketServerReaderListener - Message received by the socket");
					log.write("Message send by " +id+". Ready to Broadcast");
					broadCastReceivedMessage(messageRead);
				}
			} catch (Exception e) {
				log.write("SocketServerReaderListener - Exception while loop " + e.getMessage());
			}
			finally {
				terminate();
			}
			log.writeLog("SpawnSocketThread.ReaderListener thread finalizing");
		}

	}
}
