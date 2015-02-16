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
	
	public void processGroup(Group group) {
		if(group == null) { return; }
		
		m_numberOfFiles += group.numberOfFiles();
	}

	public void groupProcessingCompleted() {
		JOptionPane.showMessageDialog(null, m_numberOfFiles, "Number of Files", JOptionPane.OK_OPTION);
		
		SystemConsole.instance.writeLine("Number of files: " + m_numberOfFiles);
	}
	
}
