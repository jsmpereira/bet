package jsmp.dei.sd.utils;

public abstract class ConnectionHandler extends Thread {

	protected final int MAX_ATTEMPTS = 5;
	protected int seconds = 5;
	protected int reconnect_interval = 1;
	protected int reconnection_attempts = 0;
	
	public ConnectionHandler() {
		System.out.println("\t\t\t\t\t\t !WARNING! Server is not responding.");
		System.out.println("\t\t\t\t\t\t Relax and watch some Kendo http://www.youtube.com/watch?v=4XRZF7IqakI while we try to reach the Server.");
	}
}
