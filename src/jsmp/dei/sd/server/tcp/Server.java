package jsmp.dei.sd.server.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Hashtable;
import java.util.Vector;

import jsmp.dei.sd.client.rmi.IClient;
import jsmp.dei.sd.db.Database;
import jsmp.dei.sd.server.failover.Heartbeat;
import jsmp.dei.sd.server.rmi.RMIConnection;
import jsmp.dei.sd.server.rmi.RMIConnectionImpl;
import jsmp.dei.sd.utils.ServerMessage;
import jsmp.dei.sd.utils.User;
import jsmp.dei.sd.utils.Utils;
import jsmp.dei.sd.utils.Utils.MessageCode;

public class Server {

	protected Hashtable<String, Object> clients;
	protected MatchHandler matchHandler;
	private int serverPort;
	public int alivePort;
	public int sAlivePort;
	public int registryPort;
	private ServerSocket listenSocket;
	protected Database db;
	public String role;
	
	public Server(int serverPort, int registryPort, int alivePort) {
		if (serverPort == 6000) {
			role = "primary";
			sAlivePort = 7777;
		} else {
			role = "secondary";
			sAlivePort = 6666;
		}
		this.serverPort = serverPort;
		this.registryPort = registryPort;
		this.alivePort = alivePort;
		clients = new Hashtable<String, Object>();
		new Heartbeat(this);
	}
	
	public Database getDB() {
		return db;
	}
	
	public void boot() {
		bootRMI();
		bootTCP();
		db = new Database(); // start db thread
		matchHandler = new MatchHandler(this); // start matchHandler, where periodic job resides
	}
	
	private void bootTCP() {
		try {

			listenSocket = new ServerSocket(serverPort);
			System.out.println("TCP Server ready.");
			System.out.println("Heartbeat at "+alivePort);
			System.out.println("LISTEN SOCKET="+listenSocket);
			
			while(true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("CLIENT_SOCKET (created at accept())="+clientSocket);
				
				synchronized (clients) {
					ServerConnection conn = new ServerConnection(this, clientSocket);
					conn.setSCID(addClient(conn));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void bootRMI() {
		try {
			RMIConnection rc = new RMIConnectionImpl(this);
			Registry r = LocateRegistry.createRegistry(registryPort);
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
	
	public String addClient(Object client) {
		String scid = Utils.generateUID().toString();
		clients.put(scid, client);
		
		return scid;
	}
	
	public void removeClient(String scid) {
		clients.remove(scid);
	}
	
	/**
	 * It makes more sense to cycle through online users and fetch
	 * associated connection from clients list to send message.
	 * There might be entries in the clients list that do not
	 * correspond to online users, so sending messages would be wrong.
	 */
	public void broadcast(String message) {
		Vector<User> onlineUsers = db.doGetOnlineUsers();               
		
		if (onlineUsers.size() != 0) {
			
			for (User user : onlineUsers) {
				send(user.getScid(), message);
			}
		}
	}
	
	/**
	 * Send a message to the connection identified by scid
	 */
	public void send(String scid, String message) {
		Object co = clients.get(scid);
		
		if (co != null) {
			if (co instanceof ServerConnection) {
				try {
					// FIXME object might be cached, but there's considerable overhead in doing this for every write
					((ServerConnection) co).out.reset();
					((ServerConnection) co).out.writeObject(new ServerMessage(MessageCode.NOTIFY, message));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else if(co instanceof IClient) {
				try {
					((IClient) co).message_client(message, true);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
	}
	
	public static void main(String args[]) {
		Server server = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		
		if (server.role == "primary")
			server.boot();
		else
			System.out.println("Secondary server idleing");
	}
}


