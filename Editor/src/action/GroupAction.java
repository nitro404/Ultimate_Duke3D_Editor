package action;

import java.util.*;
import group.*;
import gui.*;

public class GroupAction {
	
	protected GroupPanel m_source;
	protected GroupActionType m_action;
	protected Vector<GroupFile> m_selectedFiles;
	
	public GroupAction(GroupPanel source, GroupActionType action) {
		setSource(source);
		setAction(action);
		m_selectedFiles = new Vector<GroupFile>();
	}
	
	public GroupAction(GroupPanel source, GroupActionType action, GroupFile[] files) {
		setSource(source);
		setAction(action);
		m_selectedFiles = new Vector<GroupFile>();
		addSelectedFiles(files);
	}
	
	public GroupAction(GroupPanel source, GroupActionType action, Vector<GroupFile> files) {
		setSource(source);
		setAction(action);
		m_selectedFiles = new Vector<GroupFile>();
		addSelectedFiles(files);
	}
	
	public GroupPanel getSource() {
		return m_source;
	}
	
	public void setSource(GroupPanel source) {
		m_source = source;
	}
	
	public GroupActionType getAction() {
		return m_action;
	}
	
	public void setAction(GroupActionType action) {
		m_action = action;
	}
	
	public int numberOfSelectedFiles() {
		return m_selectedFiles.size();
	}
	
	public boolean hasSelectedFile(GroupFile file) {
		if(file == null || !file.isValid()) { return false; }
		
		for(int i=0;i<m_selectedFiles.size();i++) {
			if(file.equals(m_selectedFiles.elementAt(i))) {
				return true;
			}
		}
		return false;
	}

	public int indexOfSelectedFile(GroupFile file) {
		if(file == null || !file.isValid()) { return -1; }
		
		for(int i=0;i<m_selectedFiles.size();i++) {
			if(file.equals(m_selectedFiles.elementAt(i))) {
				return i;
			}
		}
		return -1;
	}
	
	public GroupFile getSelectedFile(int index) {
		if(index < 0 || index >= m_selectedFiles.size()) { return null; }
		
		return m_selectedFiles.elementAt(index);
	}
	
	public Vector<GroupFile> getSelectedFiles() {
		return m_selectedFiles;
	}
	
	public boolean addSelectedFile(GroupFile file) {
		if(file == null || !file.isValid() || hasSelectedFile(file)) { return false; }
		
		m_selectedFiles.add(file);
		
		return true;
	}
	
	public int addSelectedFiles(GroupFile[] files) {
		if(files == null || files.length == 0) { return 0; }
		
		int numberOfFilesAdded = 0;
		
		for(int i=0;i<files.length;i++) {
			if(addSelectedFile(files[i])) {
				numberOfFilesAdded++;
			}
		}
		
		return numberOfFilesAdded;
	}

	public int addSelectedFiles(Vector<GroupFile> files) {
		if(files == null || files.isEmpty()) { return 0; }
		
		int numberOfFilesAdded = 0;
		
		for(int i=0;i<files.size();i++) {
			if(addSelectedFile(files.elementAt(i))) {
				numberOfFilesAdded++;
			}
		}
		
		return numberOfFilesAdded;
	}
	
	public boolean removeSelectedFile(GroupFile file) {
		if(file == null || !file.isValid()) { return false; }
		
		for(int i=0;i<m_selectedFiles.size();i++) {
			if(file.equals(m_selectedFiles.elementAt(i))) {
				m_selectedFiles.remove(i);
				
				return true;
			}
		}
		return false;
	}
	
	public void clearSelectedFiles() {
		m_selectedFiles.clear();
	}
	
	public boolean isValid() {
		return m_source != null &&
			   m_action.isValid() &&
			   m_action != GroupActionType.DoNothing;
	}
	
	public static boolean isvalid(GroupAction action) {
		return action != null && action.isValid();
	}
	
}
