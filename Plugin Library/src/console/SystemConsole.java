package console;

import java.io.*;
import java.text.*;
import java.util.*;
import utilities.*;
import settings.*;

public class SystemConsole {
	
	public static SystemConsole instance;
	
    private Vector<SystemConsoleEntry> m_consoleEntries;
    private Vector<Updatable> m_targets;
    private boolean m_headerAddedToLogFile;
    
    public static final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");

    public SystemConsole() {
    	instance = this;
    	
    	m_consoleEntries = new Vector<SystemConsoleEntry>();
    	m_targets = new Vector<Updatable>();
    	
    	m_headerAddedToLogFile = false;
    }
    
    public static SystemConsole getInstance() {
    	return instance;
    }
    
    // adds a target which displays the contents of the console 
    public void addTarget(Updatable target) {
    	if(target == null) { return; }
    	
    	if(!m_targets.contains(target)) {
    		m_targets.add(target);
    	}
    }
    
    // removes the specified target
    public void removeTarget(Updatable target) {
    	if(target == null) { return; }
    	
    	m_targets.remove(target);
    }
    
    // removes all targets
    public void clearTargets() {
    	m_targets.clear();
    }
    
    // updates all targets so they reflect the new console data
    private void updateTargets() {
    	for(int i=0;i<m_targets.size();i++) {
    		m_targets.elementAt(i).update();
    	}
    }
    
    // returns the number of entries in the console
    public int size() {
    	return m_consoleEntries.size();
    }
    
    // returns a specific entry from the console
    public SystemConsoleEntry getConsoleEntry(int index) {
		if(index < 0 || index >= m_consoleEntries.size()) {
		    return null;
		}
		return m_consoleEntries.elementAt(index);
    }

    public void writeLine(boolean b) {
    	writeLine(b ? "true" : "false");
    }
    
    public void writeLine(short s) {
    	writeLine(Short.toString(s));
    }
    
    public void writeLine(int i) {
    	writeLine(Integer.toString(i));
    }
    
    public void writeLine(long l) {
    	writeLine(Long.toString(l));
    }
    
    public void writeLine(float f) {
    	writeLine(Float.toString(f));
    }
    
    public void writeLine(double d) {
    	writeLine(Double.toString(d));
    }
    
    public void writeLine(char c) {
    	writeLine(Character.toString(c));
    }
    
    public void writeLine(Object o) {
    	writeLine(o == null ? "null" : o.toString());
    }
    
    public void writeLine(String text) {
    	writeLine(text, false);
    }
    
    // writes a new entry to the console
    private void writeLine(String text, boolean ignoreLog) {
    	SystemConsoleEntry newConsoleEntry = new SystemConsoleEntry(text);
		m_consoleEntries.add(newConsoleEntry);
		while(m_consoleEntries.size() > SettingsManager.instance.maxConsoleHistory) {
			m_consoleEntries.remove(0);
		}
		
		if(SettingsManager.instance.logConsole && !ignoreLog) {
			addHeaderToLogFile();
			if(!appendToLogFile(newConsoleEntry.getTimeAsString() + ": " + newConsoleEntry.getText())) {
				writeLine("Failed to write text to console log file", true);
			}
		}
		
		updateTargets();
    }
    
    // clears the console
    public void clear() {
    	m_consoleEntries.clear();
    	
    	updateTargets();
    }
    
    public boolean createLogDirectory() {
		if(SettingsManager.instance.logDirectoryName.length() == 0) { return true; }
		
		File logDirectory = new File(SettingsManager.instance.logDirectoryName);
		
		if(!logDirectory.exists()) {
			try {
				return logDirectory.mkdirs();
			}
			catch(SecurityException e) {
				SystemConsole.instance.writeLine("Failed to create log directory, check read / write permissions.");
				return false;
			}
		}
		
		return true;
	}
    
    public void resetConsoleLogFileHeader() {
    	m_headerAddedToLogFile = false;
    }
    
    private boolean addHeaderToLogFile() {
		if(m_headerAddedToLogFile) { return false; }
		
		createLogDirectory();
		
		File logFile = new File((SettingsManager.instance.logDirectoryName.length() == 0 ? "" : SettingsManager.instance.logDirectoryName + (SettingsManager.instance.logDirectoryName.charAt(SettingsManager.instance.logDirectoryName.length() - 1) == '/' || SettingsManager.instance.logDirectoryName.charAt(SettingsManager.instance.logDirectoryName.length() - 1) == '\\' ? "" : "/")) + SettingsManager.instance.consoleLogFileName);
		
		PrintWriter out = null;
		
		boolean fileExists = logFile.exists();
		
		try {
			out = new PrintWriter(new FileWriter(logFile, true));
			
			if(fileExists) { out.println(); }
			
			out.println("[Console Output from " + LOG_DATE_FORMAT.format(Calendar.getInstance().getTime()) + "]");
		}
		catch(IOException e) { return false; }
		
		if(out != null) { out.close(); }
		
		m_headerAddedToLogFile = true;
		
		return true;
	}
    
    private boolean appendToLogFile(String text) {
		if(text == null) { return false; }
		
		createLogDirectory();
		
		File logFile = new File((SettingsManager.instance.logDirectoryName.length() == 0 ? "" : SettingsManager.instance.logDirectoryName + (SettingsManager.instance.logDirectoryName.charAt(SettingsManager.instance.logDirectoryName.length() - 1) == '/' || SettingsManager.instance.logDirectoryName.charAt(SettingsManager.instance.logDirectoryName.length() - 1) == '\\' ? "" : "/")) + SettingsManager.instance.consoleLogFileName);
		
		PrintWriter out = null;
		
		try {
			out = new PrintWriter(new FileWriter(logFile, true));
			
			out.println(text);
		}
		catch(IOException e) { return false; }
		
		if(out != null) { out.close(); }
		
		return true;
	}
    
    // writes the contents of the console to a string
    public String toString() {
    	StringBuffer data = new StringBuffer(512);
    	SystemConsoleEntry e = null;
		for(int i=0;i<m_consoleEntries.size();i++) {
			e = m_consoleEntries.elementAt(i);
			data.append(e.getTimeAsString() + ": " + e.getText());
			if(i<m_consoleEntries.size() - 1) {
				data.append("\n");
			}
		}
		return data.toString();
    }
    
}
