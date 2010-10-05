package jsmp.dei.sd.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {
	
	public static enum Commands {
		HELP, REGISTER, LOGIN, LOGOUT, CREDITS, MATCHES, RESET, BET, WHO, NIL;
		
		public static Commands toOption(String str){
			try {
				return valueOf(str);
			} catch (Exception e) {
				return NIL;
			}
		}		
	}
	
	public static enum MessageCode {
		OK, FAIL;
	}
	
	public final static int CREDITS = 100;
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static String timeNow() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(cal.getTime());
	}

}