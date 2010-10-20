package jsmp.dei.sd.client.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * Client callback interface
 * 
 * @author josesantos
 *
 */
public interface IClient extends Remote {
	
	public void message_client(String message, boolean notify) throws RemoteException;
}
