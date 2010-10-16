package jsmp.dei.sd.client.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClient extends Remote {
	
	public void message_client(String message) throws RemoteException;
}
