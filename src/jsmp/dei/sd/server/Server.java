package jsmp.dei.sd.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.UUID;

import jsmp.dei.sd.db.Database;
import jsmp.dei.sd.utils.Utils;

public class Server {

	protected static Hashtable<String, ServerConnection> clients;
	protected static MatchHandler matchHandler;
	private int serverPort;
	private ServerSocket listenSocket;
	protected Database db;
	private UUID scid; // ServerConnection ID - to act as key on connections hash
	
	public Server() {
		clients = new Hashtable<String, ServerConnection>();
		db = new Database(); // start db thread
		matchHandler = new MatchHandler(db, clients); // start handler, where periodic job resides
	}
	
	public void boot() {
		
		try {
			serverPort = 6000;

			listenSocket = new ServerSocket(serverPort);
			System.out.println("Server is alive.");
			System.out.println("LISTEN SOCKET="+listenSocket);
			while(true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("CLIENT_SOCKET (created at accept())="+clientSocket);
				scid = Utils.generateUID();
				System.out.println("New client connection: "+scid.toString());
				synchronized (clients) {
					ServerConnection conn = new ServerConnection(scid.toString(), db, matchHandler, clientSocket);
					clients.put(scid.toString(), conn);
				}
				scid = null; // reset				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		Server server = new Server();
		server.boot();
	}
}


