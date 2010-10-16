package jsmp.dei.sd.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class Utils {
	
	public static enum Commands {
		HELP, REGISTER, LOGIN, LOGOUT, CREDITS, MATCHES, RESET, BET, WHO, MESSAGE, BROADCAST, NIL;
		
		public static Commands toOption(String str){
			try {
				return valueOf(str);
			} catch (Exception e) {
				return NIL;
			}
		}		
	}
	
	public static enum MessageCode {
		OK, FAIL, NOTIFY, MESSAGE, BROADCAST;
	}
	
	public final static int CREDITS = 100;
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static String timeNow() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(cal.getTime());
	}
	
	public static UUID generateUID() {
		return UUID.randomUUID();
	}
	
	public static void optionHelp() {
		System.out.println(
				"\nThis is help." +
				"\n\nAvailable commands:" +
				
				"\n\n Require no authentication:" +
				"\n\t register - create a new account" +
				"\n\t login - enter your credentials to authenticate" +
				
				"\n\n Require authentication: " +
				"\n\t credits - send a tweet" +
				"\n\t reset - list all tweets (you + following)" +
				"\n\t matches - list users you are following" +
				"\n\t bet - list users that follow you" +
				"\n\t who - follow a given user" +
				"\n\t message - user search" +				
				"\n\t broadcast - terminate your session" +
				
				"\n\n Other:" +
				"\n\t help - print this help message" +
				"\n\t date - print the current date and time" +
				"\n\t quit - exit the client\n\n");
	}
}
