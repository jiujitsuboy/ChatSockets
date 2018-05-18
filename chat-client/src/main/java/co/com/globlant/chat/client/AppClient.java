package co.com.globlant.chat.client;

/**
 * Hello world!
 *
 */
public class AppClient {
	public static void main(String[] args) {
		String hostName = "localhost";
		int portNumber = 1202;
		try {
			if (args.length == 2) {

				hostName = args[0];
				portNumber = Integer.parseInt(args[1]);
			}
			new Client(hostName, portNumber);			
		} catch (NumberFormatException nfe) {
			System.out.println(nfe.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
