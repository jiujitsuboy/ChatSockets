package co.com.globlant.chat.server;

/**
 * 
 *
 */
public class App {
	public static void main(String[] args) {
		int portNumber = 1202;
		try {
			if (args.length == 1) {

				portNumber = Integer.parseInt(args[0]);
			}
			new Server(portNumber);			
		} catch (NumberFormatException nfe) {
			System.out.println(nfe.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	
}
