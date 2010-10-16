package jsmp.dei.sd.client.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionHandler extends Thread{
		
	private final int MAX_ATTEMPTS = 5;
	private int seconds = 5;
	private int reconnect_interval = 1;
	private int reconnection_attempts = 0;
	private Client client;

	public ConnectionHandler(Client client) {
		this.client = client;
	    this.start();
	}
	
	public void run() {
		while (reconnection_attempts < MAX_ATTEMPTS) {
			
			try {
				//client.getSocket().close();
				
				client.setSocket(new Socket(client.getHostname(), client.getServerPort()));
				client.setOut(new ObjectOutputStream(client.getSocket().getOutputStream()));
				client.setIn(new ObjectInputStream(client.getSocket().getInputStream()));
				System.out.print("\n\n\t\t\t\t\t\t ... and we're back");
								
				for (Thread t : client.getThreadList()) {
					// tell threads they should finish
					if (t instanceof Reader)
						((Reader) t).setOnline(false);
					else
						((CLI) t).setOnline(false);
					// wait for them to finish
					t.join();

					System.out.println("joined thread "+t.getName());
				}
				
				client.getThreadList().removeAllElements();
				Reader r = new Reader(client);
				CLI cli = new CLI(client);
				client.getThreadList().add(r);
				client.getThreadList().add(cli);
				
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
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			
		}
		
		if (reconnection_attempts == MAX_ATTEMPTS)
			System.out.println("\n\n\t\t\t\t\t\t Connection attempts failed. Maybe check your internet connection?");
	}
}
