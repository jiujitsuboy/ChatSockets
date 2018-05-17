package co.com.globlant.chat.abstraction;

import java.net.Socket;
import java.util.Set;

/***
 * Represent the basic functionality of a Chat Server
 * @author JoseAlejandro
 *
 */
public interface IServer extends IService {
	
	Set<Runnable> getAllSocketsThreads();
	Runnable spawnNewSocketThread(Socket clientSocket);
	void addSpawnedSocketToDirectory(Runnable spawnSocketThread);	
}
