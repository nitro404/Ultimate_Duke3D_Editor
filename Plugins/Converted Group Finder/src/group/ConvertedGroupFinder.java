package group;

import java.util.*;
import java.io.*;
import javax.swing.*;
import console.*;

public class ConvertedGroupFinder extends GroupProcessor {
	
	protected Vector<File> m_convertedGroups;
	
	public static final String atomicConFileName = "ATOMIC.CON";
	
	public ConvertedGroupFinder() {
		m_convertedGroups = new Vector<File>();
	}

	public boolean initialize() {
		if(m_initialized) { return true; }
		
		m_initialized = true;
		
		return true;
	}
	
	public Vector<File> getConvertedGroups() {
		return m_convertedGroups;
	}
	
	public boolean processGroup(Group group) {
		if(group == null) { return false; }
		
		if(group.hasFile(atomicConFileName)) {
			if(group.getFile() != null) {
				m_convertedGroups.add(group.getFile());
			}
		}
		
		SystemConsole.instance.writeLine("Finished processing group file: " + group + ".");
		
		return true;
	}

	public void groupProcessingCompleted() {
		if(m_convertedGroups.size() == 0) {
			SystemConsole.instance.writeLine("No converted groups found.");
			
			JOptionPane.showMessageDialog(null, "No converted groups found.", "No Converted Groups", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			SystemConsole.instance.writeLine("Found " + m_convertedGroups.size() + " convered groups:");
			
			for(int i=0;i<m_convertedGroups.size();i++) {
				if(m_convertedGroups.elementAt(i) != null) {
					SystemConsole.instance.writeLine((i + 1) + ": " + m_convertedGroups.elementAt(i).getName());
				}
			}
			
			JOptionPane.showMessageDialog(null, "Found " + m_convertedGroups.size() + " convered groups, check console for details.", m_convertedGroups.size() + " Converted Groups", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
}
