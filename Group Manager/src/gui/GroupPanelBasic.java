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
	
	public void selectInverse() {
		if(m_group.numberOfFiles() == 0) { return; }
		
		if(m_fileList.getModel().getSize() != m_group.numberOfFiles()) {
			GroupManager.console.writeLine("Cannot select inverse, file list is not up to date. Check for missing update calls in code.");
			return;
		}
		
		int[] selectedIndicies = m_fileList.getSelectedIndices();
		
		boolean[] inverseState = new boolean[m_group.numberOfFiles()];
		
		int[] inverseIndicies = new int[m_group.numberOfFiles() - selectedIndicies.length];
		
		for(int i=0;i<inverseState.length;i++) {
			inverseState[i] = true;
		}
		
		for(int i=0;i<selectedIndicies.length;i++) {
			if(selectedIndicies[i] < 0 || selectedIndicies[i] >= inverseState.length) {
				GroupManager.console.writeLine("Inverse state index is out of range: " + selectedIndicies[i] + ", expected value in range of 0 to " + inverseState.length + ".");
				return;
			}
			
			inverseState[selectedIndicies[i]] = false;
		}
		
		int c = 0;
		for(int i=0;i<inverseState.length;i++) {
			if(inverseState[i]) {
				if(c >= inverseIndicies.length) {
					GroupManager.console.writeLine("Too many inverse indicies encountered when inversing selection.");
					return;
				}
				
				inverseIndicies[c++] = i;
			}
		}
		
		m_fileList.setSelectedIndices(inverseIndicies);
	}
	
	public void selectRandom() {
		if(m_group.numberOfFiles() == 0) { return; }
		
		boolean[] randomState = new boolean[m_group.numberOfFiles()];
		
		int numberOfIndicies = 0;
		for(int i=0;i<randomState.length;i++) {
			randomState[i] = ((int) (Math.random() * 2)) == 0;
			
			if(randomState[i]) {
				numberOfIndicies++;
			}
		}
		
		int[] randomIndicies = new int[numberOfIndicies];
		
		int c = 0;
		for(int i=0;i<randomState.length;i++) {
			if(randomState[i]) {
				randomIndicies[c++] = i;
			}
		}
		
		m_fileList.setSelectedIndices(randomIndicies);
	}

	public void selectAll() {
		if(m_group.numberOfFiles() == 0) { return; }
		
		int[] allIndicies = new int[m_group.numberOfFiles()];
		
		for(int i=0;i<allIndicies.length;i++) {
			allIndicies[i] = i;
		}
		
		m_fileList.setSelectedIndices(allIndicies);
	}

	public void clearSelection() {
		if(m_group.numberOfFiles() == 0) { return; }
		
		m_fileList.setSelectedIndices(new int[0]);
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
