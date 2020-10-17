package console;

import java.text.*;
import java.util.*;

public class SystemConsoleEntry {
	
	private String m_text;
    private Calendar m_time;
    final private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");
    
    public SystemConsoleEntry(String text) {
		m_time = Calendar.getInstance();
		m_text = text == null ? "null" : text;
    }
    
    // returns the text stored in the console entry
    public String getText() {
    	return m_text;
    }
    
    // returns the time stamp of the console entry
    public Calendar getTime() {
    	return m_time;
    }
    
    // returns a string representation of the console entry
    public String getTimeAsString() {
    	return dateFormat.format(m_time.getTime());
    }
    
}
