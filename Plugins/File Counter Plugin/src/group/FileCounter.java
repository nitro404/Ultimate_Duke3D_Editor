package group;


import javax.swing.*;
import console.*;

public class FileCounter extends GroupProcessor {
	
	protected int m_numberOfFiles;
	
	public FileCounter() {
		m_numberOfFiles = 0;
	}
	
	public int getNumberOfFiles() {
		return m_numberOfFiles;
	}
	
	public boolean processGroup(Group group) {
		if(group == null) { return false; }
		
		m_numberOfFiles += group.numberOfFiles();
		
		return true;
	}

	public void groupProcessingCompleted() {
		JOptionPane.showMessageDialog(null, m_numberOfFiles, "Number of Files", JOptionPane.OK_OPTION);
		
		SystemConsole.instance.writeLine("Number of files: " + m_numberOfFiles);
	}
	
}
