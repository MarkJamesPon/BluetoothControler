package personal.yccho.BluetoothControler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Tools {
	public static String GenerateRandomString() {
		int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 10;
	    Random random = new Random();

	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	    return generatedString;
	}
	
	public static String formatTime(Date date, String format){
		DateFormat dateFormat = new SimpleDateFormat(format);		
		return dateFormat.format(date);
	}
	
	public static void showLog(int typeid, String logContent){
		String type = "";
		switch(typeid) {
			case 0:
				type = "Info";
				break;
			case 1:
				type = "Error";
				break;			
			default:
				break;
		}
		String showContent = "["+Tools.formatTime(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")+" ("+type+")]: "+logContent;
		System.out.println(showContent);
	}
	
	public static void showBaseException(Exception e) {							   
		Tools.showLog(1, "msg " + e.getMessage());
		Tools.showLog(1, "loc " + e.getLocalizedMessage());
		Tools.showLog(1, "cause " + e.getCause());
		Tools.showLog(1, "excep " + e +", ERROR STACKTRACE:");
		e.printStackTrace(); 		
	}
	
	public static JSONObject readJSONFile(String filename) {
		JSONObject obj = null;
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader(filename)) {
            obj = (JSONObject) jsonParser.parse(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
		return obj;
	}

}
