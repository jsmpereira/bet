package jsmp.dei.sd.server.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import jsmp.dei.sd.client.rmi.IClient;
import jsmp.dei.sd.utils.Bet;
import jsmp.dei.sd.utils.ClientMessage;
import jsmp.dei.sd.utils.User;

public interface RMIConnection extends Remote {
	
	public User sLogin(ClientMessage message, String scid) throws RemoteException;
	public void sRegister(User user) throws RemoteException;
	public void sCredits(String login) throws RemoteException;
	public void sReset(String login) throws RemoteException;
	public void sBet(Bet bet) throws RemoteException;
	public void sMatches() throws RemoteException;
	public void sWho(String login) throws RemoteException;
	public void sPrivate(String login, String target, String message) throws RemoteException;
	public void sPublic(String login, String message) throws RemoteException;
	public void sLogout(String string) throws RemoteException;
	
	public String subscribe(IClient client) throws RemoteException;
	
}
