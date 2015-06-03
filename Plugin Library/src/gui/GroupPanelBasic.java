package gui;

import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import group.*;
import console.*;

public class GroupPanelBasic extends GroupPanel implements ListSelectionListener {
	
	protected JList<GroupFile> m_fileList;
	protected JScrollPane m_fileListScrollPane;
	protected GroupFile m_selectedGroupFiles[];
	protected boolean m_updatingWindow;
	
	private static final long serialVersionUID = -53066122432650525L;
	
	public GroupPanelBasic() {
		super(null);
		
		m_updatingWindow = false;
	}
	
	public GroupPanelBasic(Group group) {
		super(group);
		
		m_updatingWindow = false;
	}
	
	public boolean init() {
		return init(true);
	}
	
	public boolean init(boolean setInitialized) {
		if(m_initialized) { return true; }
		
		setLayout(new GridLayout(1, 1));
		
		m_fileList = new JList<GroupFile>();
		m_fileList.addListSelectionListener(this);
		m_fileList.addMouseListener(this);
		m_fileListScrollPane = new JScrollPane(m_fileList);
		add(m_fileListScrollPane);
		
		if(setInitialized) {
			m_initialized = true;
		}
		
		updateWindow();
		
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
			SystemConsole.instance.writeLine("Cannot select inverse, file list is not up to date. Check for missing update calls in code.");
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
				SystemConsole.instance.writeLine("Inverse state index is out of range: " + selectedIndicies[i] + ", expected value in range of 0 to " + inverseState.length + ".");
				return;
			}
			
			inverseState[selectedIndicies[i]] = false;
		}
		
		int c = 0;
		for(int i=0;i<inverseState.length;i++) {
			if(inverseState[i]) {
				if(c >= inverseIndicies.length) {
					SystemConsole.instance.writeLine("Too many inverse indicies encountered when inversing selection.");
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
	
	public boolean backupSelectedGroupFiles() {
		if(m_selectedGroupFiles != null) { return false; }
		
		List<GroupFile> selectedGroupFiles = m_fileList.getSelectedValuesList();
		
		m_selectedGroupFiles = new GroupFile[selectedGroupFiles.size()];
		for(int i=0;i<selectedGroupFiles.size();i++) {
			m_selectedGroupFiles[i] = selectedGroupFiles.get(i);
		}
		
		return true;
	}
	
	public boolean restoreSelectedGroupFiles() {
		if(m_selectedGroupFiles == null) { return false; }
		
		Vector<Integer> selectedGroupFileIndexCollection = new Vector<Integer>();
		
		int groupFileIndex = -1;
		for(int i=0;i<m_selectedGroupFiles.length;i++) {
			groupFileIndex = m_group.indexOfFile(m_selectedGroupFiles[i]);
			if(groupFileIndex < 0) { continue; }
			
			selectedGroupFileIndexCollection.add(groupFileIndex);
		}
		
		int selectedGroupFileIndicies[] = new int[selectedGroupFileIndexCollection.size()];
		
		for(int i=0;i<selectedGroupFileIndexCollection.size();i++) {
			selectedGroupFileIndicies[i] = selectedGroupFileIndexCollection.elementAt(i);
		}
		
		m_fileList.setSelectedIndices(selectedGroupFileIndicies);
		
		m_selectedGroupFiles = null;
		
		return true;
	}
	
	public void updateGroup() {
		
	}
	
	public void updateWindow() {
		if(!m_initialized || m_updating || m_updatingWindow) { return; }
		
		m_updatingWindow = true;
		
		if(m_group != null) {
			if(m_selectedGroupFiles == null) {
				backupSelectedGroupFiles();
			}
		}
		else {
			m_selectedGroupFiles = null;
		}
		
		m_fileList.clearSelection();
		
		if(m_group != null) {
			m_fileList.setListData(m_group.getFiles());
			
			if(m_selectedGroupFiles != null) {
				restoreSelectedGroupFiles();
			}
			
			updateLayout();
		}
		
		m_updatingWindow = false;
	}
	
	public void updateLayout() {
		
	}
	
	public void valueChanged(ListSelectionEvent e) {
		if(e == null) { return; }
		
		if(e.getSource() == m_fileList) {
			notifyUpdateWindow();
		}
	}
	
}
