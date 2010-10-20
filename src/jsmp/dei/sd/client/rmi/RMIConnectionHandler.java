package jsmp.dei.sd.client.rmi;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;

import jsmp.dei.sd.server.rmi.RMIConnection;
import jsmp.dei.sd.utils.ConnectionHandler;

public class RMIConnectionHandler extends ConnectionHandler {

	private RMIClient client;
	
	public RMIConnectionHandler(RMIClient client) {
		this.client = client;
	    this.start();
	}
	
	public void run() {
		while (reconnection_attempts < MAX_ATTEMPTS) {
			
			try {
				//client.getSocket().close();
				
				client.rc = (RMIConnection) LocateRegistry.getRegistry(7000).lookup("rmi://localhost/rmiconnect");
				System.out.println("\n\n\t\t\t\t\t\t ... and we're back");
				
				client.subscribe();
				return;
			} catch (IOException e) {
				reconnect_interval *= 2;
				System.out.println("\n\n\t\t\t\t\t\t Trying again in ... " + reconnect_interval * seconds + " seconds");
				reconnection_attempts++;
				try {
					Thread.sleep(reconnect_interval*seconds*1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 		
			
		}
		
		if (reconnection_attempts == MAX_ATTEMPTS)
			System.out.println("\n\n\t\t\t\t\t\t Connection attempts failed. Maybe check your internet connection?");
	}
}