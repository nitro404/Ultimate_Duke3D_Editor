package gui;

import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import group.*;

public class GroupPanelBasic extends GroupPanel {
	
	protected JList<GroupFile> m_fileList;
	protected JScrollPane m_fileListScrollPane;
	
	private static final long serialVersionUID = -53066122432650525L;
	
	public GroupPanelBasic() {
		super(null);
	}
	
	public GroupPanelBasic(Group group) {
		super(group);
	}
	
	public boolean init() {
		return init(true);
	}
	
	public boolean init(boolean setInitialized) {
		if(m_initialized) { return true; }
		
		setLayout(new GridLayout(1, 1));
		
		m_fileList = new JList<GroupFile>();
		m_fileList.addMouseListener(this);
		m_fileListScrollPane = new JScrollPane(m_fileList);
		add(m_fileListScrollPane);
		
		if(setInitialized) {
			m_initialized = true;
		}
		
		updateWindow();
		
		return true;
	}
	
	public boolean setGroup(Group group) {
		return setGroup(group, true);
	}
	
	public boolean setGroup(Group group, boolean updateWindow) {
		m_group = group;
		
		if(updateWindow) {
			updateWindow();
		}
		
		return true;
	}
	
	public int numberOfSelectedFiles() {
		return m_fileList.getSelectedValuesList().size();
	}
	
	public Vector<GroupFile> getSelectedFiles() {
		Vector<GroupFile> selectedFiles = new Vector<GroupFile>();
		
		List<GroupFile> selectedValues = m_fileList.getSelectedValuesList();
		if(selectedValues.size() == 0) { return selectedFiles; }
		
		for(int i=0;i<selectedValues.size();i++) {
			if(selectedValues.get(i) instanceof GroupFile) {
				selectedFiles.add((GroupFile) selectedValues.get(i));
			}
		}
		
		return selectedFiles;
	}
	
	public void updateGroup() {
		
	}
	
	public void updateWindow() {
		updateWindow(true);
	}
	
	public void updateWindow(boolean updateLayout) {
		if(!m_initialized) { return; }
		
		m_fileList.clearSelection();
		
		if(m_group != null) {
			m_fileList.setListData(m_group.getFiles());
			
			if(updateLayout) {
				updateLayout();
			}
		}
	}
	
	public void updateLayout() {
		
	}
	
}
