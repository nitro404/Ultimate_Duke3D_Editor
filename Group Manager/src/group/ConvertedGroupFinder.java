package group;

import java.util.*;
import javax.swing.*;
import console.*;

public class ConvertedGroupFinder extends GroupProcessor {
	
	protected Vector<Group> m_convertedGroups;
	
	public static final String atomicConFileName = "ATOMIC.CON";
	
	public ConvertedGroupFinder() {
		m_convertedGroups = new Vector<Group>();
	}
	
	public Vector<Group> getConvertedGroups() {
		return m_convertedGroups;
	}
	
	public void processGroup(Group group) {
		if(group == null) { return; }
		
		if(group.hasFile(atomicConFileName)) {
			m_convertedGroups.add(group);
		}
	}

	public void groupProcessingCompleted() {
		if(m_convertedGroups.size() == 0) {
			SystemConsole.getInstance().writeLine("No converted groups found.");
			
			JOptionPane.showMessageDialog(null, "No converted groups found.", "No Converted Groups", JOptionPane.OK_OPTION);
		}
		else {
			SystemConsole.getInstance().writeLine("Found " + m_convertedGroups.size() + " convered groups:");
			
			for(int i=0;i<m_convertedGroups.size();i++) {
				if(m_convertedGroups.elementAt(i).getFile() != null) {
					SystemConsole.getInstance().writeLine((i + 1) + ": " + m_convertedGroups.elementAt(i).getFile().getName());
				}
			}
			
			JOptionPane.showMessageDialog(null, "Found " + m_convertedGroups.size() + " convered groups, check console for details.", m_convertedGroups.size() + " Converted Groups", JOptionPane.OK_OPTION);
		}
	}
	
}
