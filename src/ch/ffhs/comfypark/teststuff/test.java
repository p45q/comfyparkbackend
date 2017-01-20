package ch.ffhs.comfypark.teststuff;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import com.sun.jmx.snmp.tasks.Task;

import ch.ffhs.comfypark.model.AtGateRequest;
import ch.ffhs.comfypark.model.AtGateResponse;
import ch.ffhs.comfypark.model.db.Parking;
import ch.ffhs.comfypark.model.db.User;
import sun.security.action.GetBooleanAction;

public class test {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		Sql2o sql2o = new Sql2o("jdbc:mysql://comfyparkdb.cjn6mrex9bqq.eu-west-1.rds.amazonaws.com:3306/comfypark", "ffhs", "glauer.ch");
		String sql = "SELECT * FROM parking";
	}

	
	private static String getTime(){
		Calendar currentdate = Calendar.getInstance();
		
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("CET"));
        
        return formatter.format(currentdate.getTime());
	}

}
