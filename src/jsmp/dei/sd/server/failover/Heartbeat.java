package jsmp.dei.sd.server.failover;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import jsmp.dei.sd.server.tcp.Server;

public class Heartbeat extends Thread {
	
	private Timer timer;
	private Server server;
	private MulticastSocket socket;
	private int seconds = 5;
	DatagramPacket alive_packet = null;
	InetAddress group = null;
	
	public Heartbeat(Server server) { 
		this.server = server;
		try {
			group = InetAddress.getByName("239.255.255.255");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		byte[] buf = new byte[1024];
	    alive_packet = new DatagramPacket(buf, buf.length);
		
		try {
			socket = new MulticastSocket(server.sAlivePort);
			socket.joinGroup(group);
			//socket.setSoTimeout(1000);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.timer = new Timer();
	    this.timer.scheduleAtFixedRate(new HeartbeatTask(), 10*1000, this.seconds*1000);
	    this.start();
	}	

	public void run() {
		
		while(true) {
			try {
				socket.receive(alive_packet);
				
				String s= new String(alive_packet.getData(), 0, alive_packet.getLength());
				
				System.out.println("Server Recebeu: " + s + " ~ from "+alive_packet.getSocketAddress().toString());	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	class HeartbeatTask extends TimerTask {
		@Override
		public void run() {

			String s = "ALIVE "+server.role;
			byte [] buffer = s.getBytes();
			try {

			DatagramPacket send_alive = new DatagramPacket(buffer, 
					buffer.length, group, server.alivePort);
			
				socket.send(send_alive);
			} catch (SocketTimeoutException e) {
				
				//e.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
    }

	public void setServer(Server server) {
		this.server = server;
	}

	public Server getServer() {
		return server;
	}
	
}
