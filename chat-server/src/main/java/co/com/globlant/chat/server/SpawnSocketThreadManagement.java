package co.com.globlant.chat.server;

import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;

import co.com.globlant.chat.abstraction.Ilog;
import co.com.globlant.chat.common.IConstants;
import co.com.globlant.chat.concrete.ConsoleLog;

/***
 * 
 * @author JoseAlejandro
 *
 *         Store every thread that is created to attend a Socket, allowing
 *         access to every thread when is necesary to enable comunnitacion
 *         between clientes
 */
public final class SpawnSocketThreadManagement {

	public static SpawnSocketThreadManagement threadSocketManagement = new SpawnSocketThreadManagement();

	private HashSet<Runnable> spawnSocketThreadSet;
	private Ilog log;

	/**
	 * private constructor which initialize the collection to hold all the spawn
	 * sockets threads
	 */
	private SpawnSocketThreadManagement() {
		spawnSocketThreadSet = new HashSet<>(IConstants.MIN_NUMBER_CLIENTS);
		log = new ConsoleLog(IConstants.DEBUG_INFO, IConstants.DEBUG_INFO_STYLE);
	}

	/**
	 * Gets the collection of spawned socket threads
	 * 
	 * @return {@link HashSet}
	 */
	public HashSet<Runnable> getSpawnSocketThreadSet() {
		return spawnSocketThreadSet;
	}

	/***
	 * Returns the singleton ThreadSocketManagement
	 * 
	 * @return ThreadSocketManagement
	 */
	public static SpawnSocketThreadManagement getThreadSocketManagementInstance() {
		return threadSocketManagement;
	}

	/***
	 * Add a new thread to the collection
	 * 
	 * @param server
	 *            ServerSocket reference
	 * @param clientSocket
	 *            client socket reference
	 * @return ServerClientListenerOriginal
	 */
	public SpawnSocketThread addSpawnSocketThread(Socket clientSocket) {
		int idClientThread = clientSocket.hashCode();
		SpawnSocketThread spawnSocketThread = new SpawnSocketThread(idClientThread, clientSocket);
		spawnSocketThreadSet.add(spawnSocketThread);
		return spawnSocketThread;
	}

	/**
	 * Allow to send one messague to every socket in the tread collection
	 * 
	 * @param message
	 *            String to send
	 * @param serverClientSender
	 *            identifier of the socket who send the message
	 * @throws Exception
	 */
	public void sendMessageToAll(String message, int serverClientSender) throws Exception {

		log.writeLog("Enviando mensaje BroadCast");
		if (!spawnSocketThreadSet.isEmpty()) {

			Iterator<Runnable> iterator = spawnSocketThreadSet.iterator();
			while (iterator.hasNext()) {
				SpawnSocketThread spawnSocketThread = (SpawnSocketThread) iterator.next();
				if (spawnSocketThread.getId() != serverClientSender) {
					log.writeLog("message send to client id " + spawnSocketThread.getId());
					spawnSocketThread.sendMessage(String.format("[%d] Says: %s%n", serverClientSender, message));
				}
			}
		}

	}

	/**
	 * Kill every spawn socket in the collection
	 */
	public void killAllSpawnSocketThread() {
		log.writeLog("Killing every spawn socket thread....");
		if (!spawnSocketThreadSet.isEmpty()) {

			Iterator<Runnable> iterator = spawnSocketThreadSet.iterator();
			while (iterator.hasNext()) {
				SpawnSocketThread spawnSocketThread = (SpawnSocketThread) iterator.next();
				spawnSocketThread.terminate();
			}
		}
	}

}
