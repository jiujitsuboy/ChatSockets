package co.com.globlant.chat.abstraction;

/***
 * Represent the basic functionality of communication needed by a service
 * @author JoseAlejandro
 *
 */
public interface ICommunication {
	
	void sendMessage(String message) throws Exception;
	String receiveMessage() throws Exception;
	
}
