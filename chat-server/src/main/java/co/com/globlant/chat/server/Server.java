package co.com.globlant.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import co.com.globlant.chat.abstraction.IServer;
import co.com.globlant.chat.abstraction.Ilog;
import co.com.globlant.chat.common.IConstants;
import co.com.globlant.chat.concrete.ConsoleLog;

/**
 * Represent a Server side
 * 
 * @author JoseAlejandro
 *
 */
public class Server implements IServer {	

	private ServerSocket serverSocket;
	private int portNumber;
	private Ilog log;
	private ExecutorService workersPool;
	private BufferedReader consoleStdInput;
	private Thread readerListener;
	private SpawnSocketThreadManagement spawnSocketThreadManagement;

	private boolean working;

	public Server(int portNumber) {
		this.portNumber = portNumber;		
		workersPool = Executors.newFixedThreadPool(IConstants.MIN_NUMBER_CLIENTS);
		log = new ConsoleLog(IConstants.DEBUG_INFO,IConstants.DEBUG_INFO_STYLE);
		spawnSocketThreadManagement = SpawnSocketThreadManagement.getThreadSocketManagementInstance();
		initializate();
	}

	/***
	 * Create a thread in charges of monitoring the input from the console
	 */
	private void readerListenerSetUp() {
		log.writeLog("readerListenerSetUp() - rising up consoleReaderListener Thread");
		readerListener = new Thread(new ConsoleServerReaderListener());
		readerListener.start();
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

	/**
	 * Gets the collection of all the socket's threads available
	 */
	@Override
	public HashSet<Runnable> getAllSocketsThreads() {
		return spawnSocketThreadManagement.getSpawnSocketThreadSet();
	}

	/**
	 * Create a new socket thread to the incoming socket connection request
	 */
	@Override
	public Runnable spawnNewSocketThread(Socket clientSocket) {
		int idClientSocket = clientSocket.hashCode();
		return new SpawnSocketThread(idClientSocket, clientSocket);
	}

	@Override
	public void addSpawnedSocketToDirectory(Runnable spawnSocketThread) {		
		log.writeLog("spawnSocketThread created");
		Set<Runnable> spawnSocketThreads =  getAllSocketsThreads();
		
		spawnSocketThreads.add(spawnSocketThread);
		log.writeLog("Numero de hilos creados " + spawnSocketThreads.size());
	}
	
	@Override
	public void initializate() {
		try {
			log.write("Server Up......");
			log.writeLog("initializate()");
			working = true;
			readerListenerSetUp();
			serverSocket = new ServerSocket(portNumber);
			log.writeLog("Server Socket created");
			log.write("Address: " + serverSocket.getInetAddress().getHostName() + " Port: " + serverSocket.getLocalPort());
			getConsoleStdInput();
			listenForConnection();			
		} catch (Exception e) {
			log.writeLog(e.getMessage());
		} finally {
			terminate();
		}

	}

	@Override
	public void terminate() {
		log.writeLog("terminate() - Terminando la rutina");
		working = false;		
		try {
			workersPool.shutdown();
			serverSocket.close();
			spawnSocketThreadManagement.killAllSpawnSocketThread();
		} catch (IOException e) {
			log.writeLog(e.getMessage());
		}
		log.write("Server Down.....");
	}

	private void listenForConnection() throws InterruptedException, IOException {	
		log.writeLog("listenForConnection()");
		while (working) {
			log.writeLog("Listening for new conections");
			Socket clientSocket = serverSocket.accept();
			
			log.writeLog("New conection request");
			Runnable spawnSocketThread = spawnNewSocketThread(clientSocket);
			
			addSpawnedSocketToDirectory(spawnSocketThread);
			
			log.writeLog("spawnSocketThread add to collection");
			log.writeLog("spawnSocketThread ready to run " + + ((SpawnSocketThread) spawnSocketThread).getId());
			
			workersPool.execute(spawnSocketThread);										
		}
	}

	class ConsoleServerReaderListener implements Runnable {

		private static final String STOP_SERVER = ".";

		@Override
		public void run() {
			String messageRead = null;
			try {
				while (working && (messageRead = getConsoleStdInput().readLine()) != null) {
					log.writeLog("ConsoleServerReaderListener - Mensaje recibido por consola en el server");
					log.write(messageRead);
					if (messageRead.equals(STOP_SERVER)) {
						terminate();
						break;
					}
				}
			} catch (Exception e) {
				log.writeLog(e.getMessage());
			}
			log.writeLog("Saliendo del run() de ConsoleServerReaderListener");
		}

	}
}
