package jsmp.dei.sd.server.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Hashtable;
import java.util.UUID;
import jsmp.dei.sd.db.Database;
import jsmp.dei.sd.server.rmi.RMIConnection;
import jsmp.dei.sd.server.rmi.RMIConnectionImpl;
import jsmp.dei.sd.utils.Utils;

public class Server {

	protected Hashtable<String, ServerConnection> clients;
	protected MatchHandler matchHandler;
	private int serverPort = 6000; // FIXME move this to config
	private ServerSocket listenSocket;
	protected Database db;
	private UUID scid; // FIXME need better solution ~> ServerConnection ID - to act as key on connections hash
	
	public Server() {
		clients = new Hashtable<String, ServerConnection>();
		db = new Database(); // start db thread
		matchHandler = new MatchHandler(db, clients); // start matchHandler, where periodic job resides
	}
	
	public void boot() {
		bootRMI();
		bootTCP();
	}
	
	private void bootTCP() {
		try {

			listenSocket = new ServerSocket(serverPort);
			System.out.println("TCP Server ready.");
			System.out.println("LISTEN SOCKET="+listenSocket);
			
			while(true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("CLIENT_SOCKET (created at accept())="+clientSocket);
				scid = Utils.generateUID();
				System.out.println("New client connection: "+scid.toString());
				
				synchronized (clients) {
					ServerConnection conn = new ServerConnection(this, clientSocket);
					clients.put(scid.toString(), conn);
				}
				scid = null; // reset				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void bootRMI() {
		try {
			RMIConnection rc = new RMIConnectionImpl(db);
			Registry r = LocateRegistry.createRegistry(7000);
			r.rebind("rmi://localhost/rmiconnect", rc);
		} catch (AccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("RMI Server ready.");
	}
	
	public static void main(String args[]) {
		Server server = new Server();
		server.boot();
	}
}


