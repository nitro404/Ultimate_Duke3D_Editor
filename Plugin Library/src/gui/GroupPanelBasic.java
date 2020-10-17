package gui;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import action.*;
import item.*;
import group.*;
import console.*;

public class GroupPanelBasic extends GroupPanel implements ListSelectionListener {

	protected GroupMetadataPanel m_groupMetadataPanel;
	protected JPanel m_mainPanel;
	protected JList<GroupFile> m_fileList;
	protected JScrollPane m_fileListScrollPane;
	protected GroupFile m_selectedGroupFiles[];
	protected JPanel m_controlsPanel;
	protected JButton m_saveButton;
	protected JButton m_saveAsButton;
	protected JButton m_addFilesButton;
	protected JButton m_removeFilesButton;
	protected JButton m_replaceFileButton;
	protected JButton m_renameFileButton;
	protected JButton m_extractFilesButton;
	protected JButton m_extractAllFilesButton;
	protected JButton m_importButton;
	protected JButton m_exportButton;
	protected JButton m_closeButton;
	
	private static final long serialVersionUID = -53066122432650525L;
	
	public GroupPanelBasic() {
		super(null);
	}
	
	public GroupPanelBasic(Item item) throws IllegalArgumentException {
		super(item);
	}

	public boolean init() {
		if(m_initialized) { return true; }
		
		setLayout(new BorderLayout());
		
		m_groupMetadataPanel = new GroupMetadataPanel(m_group);
		add(m_groupMetadataPanel, BorderLayout.PAGE_START);
		
		m_mainPanel = new JPanel();
		m_mainPanel.setLayout(new GridLayout());
		add(m_mainPanel, BorderLayout.CENTER);
		
		m_fileList = new JList<GroupFile>();
		m_fileList.addListSelectionListener(this);
		m_fileList.addMouseListener(this);
		m_fileListScrollPane = new JScrollPane(m_fileList);
		m_mainPanel.add(m_fileListScrollPane);

		m_controlsPanel = new JPanel();
		m_controlsPanel.setLayout(new WrapLayout());
		add(m_controlsPanel, BorderLayout.PAGE_END);

		m_saveButton = new JButton("Save");
		m_saveButton.addActionListener(this);
		m_controlsPanel.add(m_saveButton);

		m_saveAsButton = new JButton("Save As");
		m_saveAsButton.addActionListener(this);
		m_controlsPanel.add(m_saveAsButton);

		m_addFilesButton = new JButton("Add Files");
		m_addFilesButton.addActionListener(this);
		m_controlsPanel.add(m_addFilesButton);

		m_removeFilesButton = new JButton("Remove Files");
		m_removeFilesButton.addActionListener(this);
		m_controlsPanel.add(m_removeFilesButton);

		m_replaceFileButton = new JButton("Replace File");
		m_replaceFileButton.addActionListener(this);
		m_controlsPanel.add(m_replaceFileButton);

		m_renameFileButton = new JButton("Rename File");
		m_renameFileButton.addActionListener(this);
		m_controlsPanel.add(m_renameFileButton);

		m_extractFilesButton = new JButton("Extract Files");
		m_extractFilesButton.addActionListener(this);
		m_controlsPanel.add(m_extractFilesButton);

		m_extractAllFilesButton = new JButton("Extract All Files");
		m_extractAllFilesButton.addActionListener(this);
		m_controlsPanel.add(m_extractAllFilesButton);

		m_importButton = new JButton("Import");
		m_importButton.addActionListener(this);
		m_controlsPanel.add(m_importButton);

		m_exportButton = new JButton("Export");
		m_exportButton.addActionListener(this);
		m_controlsPanel.add(m_exportButton);

		m_closeButton = new JButton("Close");
		m_closeButton.addActionListener(this);
		m_controlsPanel.add(m_closeButton);

		m_initialized = true;
		
		update();
		
		return true;
	}
	
	public int numberOfSelectedFiles() {
		return m_fileList.getSelectedValuesList().size();
	}
	
	public Vector<GroupFile> getSelectedFiles() {
		Vector<GroupFile> selectedFiles = new Vector<GroupFile>();
		
		List<GroupFile> selectedValues = m_fileList.getSelectedValuesList();
		if(selectedValues.isEmpty()) { return selectedFiles; }
		
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

	public void cleanup() { }

	public void updateGroup() {
		
	}
	
	public void update() {
		if(!m_initialized || m_updating) { return; }
		
		super.update();
		
		m_updating = true;
		
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

		m_removeFilesButton.setText("Remove File" + (numberOfSelectedFiles() == 1 ? "" : "s"));
		m_extractFilesButton.setText("Extract File" + (numberOfSelectedFiles() == 1 ? "" : "s"));
		
		m_removeFilesButton.setEnabled(numberOfSelectedFiles() != 0);
		m_replaceFileButton.setEnabled(numberOfSelectedFiles() == 1);
		m_renameFileButton.setEnabled(numberOfSelectedFiles() == 1);
		m_extractFilesButton.setEnabled(numberOfSelectedFiles() != 0);
		m_extractAllFilesButton.setEnabled(m_group != null && m_group.numberOfFiles() != 0);
		
		m_updating = false;
	}
	
	public void updateLayout() {
		
	}

	public void actionPerformed(ActionEvent e) {
		if(m_group == null || m_updating || e == null || e.getSource() == null) { return; }
		
		super.actionPerformed(e);
		
		if(e.getSource() == m_saveButton) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.Save));
		}
		else if(e.getSource() == m_saveAsButton) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.SaveAs));
		}
		else if(e.getSource() == m_addFilesButton) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.AddFiles));
		}
		else if(e.getSource() == m_removeFilesButton) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.RemoveFiles));
		}
		else if(e.getSource() == m_replaceFileButton) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.ReplaceFile));
		}
		else if(e.getSource() == m_renameFileButton) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.RenameFile));
		}
		else if(e.getSource() == m_extractFilesButton) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.ExtractFiles));
		}
		else if(e.getSource() == m_extractAllFilesButton) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.ExtractAllFiles));
		}
		else if(e.getSource() == m_importButton) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.Import));
		}
		else if(e.getSource() == m_exportButton) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.Export));
		}
		else if(e.getSource() == m_closeButton) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.Close));
		}
	}
	
	public void valueChanged(ListSelectionEvent e) {
		if(e == null) { return; }
		
		if(e.getSource() == m_fileList) {
			update();
			notifyUpdateWindow();
		}
	}
	
}
