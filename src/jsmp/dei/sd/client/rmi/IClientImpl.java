package jsmp.dei.sd.client.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class IClientImpl extends UnicastRemoteObject implements IClient {

	private static final long serialVersionUID = 1L;

	protected IClientImpl() throws RemoteException {
		super();
	}

	public void message_client(String message) throws RemoteException {
		System.out.println("\n\n\t\t\t\t\t\t "+message);
	}

	
}
