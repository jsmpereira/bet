package jsmp.dei.sd.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import jsmp.dei.sd.db.Database;

public class Server {

	protected static ArrayList<ServerConnection> clients;
	protected static MatchHandler matchHandler;
	private int serverPort;
	private ServerSocket listenSocket;
	protected Database db;
	private int thread_id;
	
	public Server() {
		thread_id = 0;
		clients = new ArrayList<ServerConnection>();
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
				thread_id++;
				synchronized (clients) {
					ServerConnection conn = new ServerConnection(thread_id, db, matchHandler, clientSocket);
					clients.add(conn);
				}
				
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


