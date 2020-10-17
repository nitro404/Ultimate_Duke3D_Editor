package gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import exception.*;
import settings.*;
import utilities.*;
import action.*;
import item.*;
import group.*;

public abstract class GroupPanel extends ItemPanel implements Scrollable, ActionListener, MouseListener, GroupSortListener, Updatable {
	
	protected Group m_group;
	protected boolean m_initialized;
	protected boolean m_updating;
	protected Vector<GroupActionListener> m_groupActionListeners;
	
	protected JPopupMenu m_groupPanelPopupMenu;
	protected JMenu m_selectPopupMenu;
	protected JMenuItem m_selectInversePopupMenuItem;
	protected JMenuItem m_selectRandomPopupMenuItem;
	protected JMenuItem m_selectAllPopupMenuItem;
	protected JMenuItem m_selectNonePopupMenuItem;
	protected JMenu m_sortPopupMenu;
	protected JMenu m_sortTargetPopupMenu;
	protected JMenu m_sortDirectionPopupMenu;
	protected JMenu m_sortTypePopupMenu;
	protected JRadioButtonMenuItem m_sortAllGroupsPopupMenuItem;
	protected JRadioButtonMenuItem m_sortPerGroupSortingPopupMenuItem;
	protected JMenuItem m_sortManualSortPopupMenuItem;
	protected JCheckBoxMenuItem m_sortAutoSortPopupMenuItem;
	protected JRadioButtonMenuItem[] m_sortDirectionPopupMenuItems;
	protected JRadioButtonMenuItem[] m_sortTypePopupMenuItems;
	protected ButtonGroup m_sortDirectionButtonGroup;
	protected ButtonGroup m_sortTypeButtonGroup;
	protected JMenuItem m_savePopupMenuItem;
	protected JMenuItem m_saveAsPopupMenuItem;
	protected JMenuItem m_addFilesPopupMenuItem;
	protected JMenuItem m_removeFilesPopupMenuItem;
	protected JMenuItem m_replaceFilePopupMenuItem;
	protected JMenuItem m_renameFilePopupMenuItem;
	protected JMenuItem m_extractFilesPopupMenuItem;
	protected JMenuItem m_importPopupMenuItem;
	protected JMenuItem m_exportPopupMenuItem;
	protected JMenuItem m_closePopupMenuItem;
	protected JMenuItem m_closeAllPopupMenuItem;
	protected JMenuItem m_canelPopupMenuItem;
	
	private static final long serialVersionUID = 6360144485605480625L;
	
	public GroupPanel() {
		this(null);
	}

	public GroupPanel(Item item) throws IllegalArgumentException {
		super(item);

		if(!(item instanceof Group)) {
			throw new IllegalArgumentException("Invalid item, expected group instance!");
		}

		m_initialized = false;
		m_updating = false;
		m_groupActionListeners = new Vector<GroupActionListener>();
		
		initPopupMenu();
		
		addMouseListener(this);
	}
	
	public abstract boolean init();
	
	private void initPopupMenu() {
		m_groupPanelPopupMenu = new JPopupMenu();
		
		m_selectPopupMenu = new JMenu("Select");
		m_selectInversePopupMenuItem = new JMenuItem("Inverse");
		m_selectRandomPopupMenuItem = new JMenuItem("Random");
		m_selectAllPopupMenuItem = new JMenuItem("All");
		m_selectNonePopupMenuItem = new JMenuItem("None");
		
		m_sortPopupMenu = new JMenu("Sort");
		m_sortTargetPopupMenu = new JMenu("Target");
		m_sortDirectionPopupMenu = new JMenu("Direction");
		m_sortTypePopupMenu = new JMenu("Type");
		m_sortAllGroupsPopupMenuItem = new JRadioButtonMenuItem("Sort All Groups");
		m_sortPerGroupSortingPopupMenuItem = new JRadioButtonMenuItem("Per Group Sorting");
		m_sortManualSortPopupMenuItem = new JMenuItem("Manual Sort");
		m_sortAutoSortPopupMenuItem = new JCheckBoxMenuItem("Auto-Sort Group Files");
		m_sortDirectionPopupMenuItems = new JRadioButtonMenuItem[SortDirection.numberOfSortDirections()];
		m_sortDirectionButtonGroup = new ButtonGroup();
		for(int i=0;i<m_sortDirectionPopupMenuItems.length;i++) {
			m_sortDirectionPopupMenuItems[i] = new JRadioButtonMenuItem(SortDirection.displayNames[i]);
			m_sortDirectionButtonGroup.add(m_sortDirectionPopupMenuItems[i]);
		}
		m_sortTypePopupMenuItems = new JRadioButtonMenuItem[GroupFileSortType.numberOfSortTypes()];
		m_sortTypeButtonGroup = new ButtonGroup();
		for(int i=0;i<m_sortTypePopupMenuItems.length;i++) {
			m_sortTypePopupMenuItems[i] = new JRadioButtonMenuItem(GroupFileSortType.displayNames[i]);
			m_sortTypeButtonGroup.add(m_sortTypePopupMenuItems[i]);
		}
		
		m_savePopupMenuItem = new JMenuItem("Save");
		m_saveAsPopupMenuItem = new JMenuItem("Save As");
		m_addFilesPopupMenuItem = new JMenuItem("Add Files");
		m_removeFilesPopupMenuItem = new JMenuItem("Remove Files");
		m_replaceFilePopupMenuItem = new JMenuItem("Replace File");
		m_renameFilePopupMenuItem = new JMenuItem("Rename File");
		m_extractFilesPopupMenuItem = new JMenuItem("Extract Files");
		m_importPopupMenuItem = new JMenuItem("Import");
		m_exportPopupMenuItem = new JMenuItem("Export");
		m_closePopupMenuItem = new JMenuItem("Close");
		m_closeAllPopupMenuItem = new JMenuItem("Close All");
		m_canelPopupMenuItem = new JMenuItem("Cancel");
		
		m_selectInversePopupMenuItem.addActionListener(this);
		m_selectRandomPopupMenuItem.addActionListener(this);
		m_selectAllPopupMenuItem.addActionListener(this);
		m_selectNonePopupMenuItem.addActionListener(this);
		m_sortAllGroupsPopupMenuItem.addActionListener(this);
		m_sortPerGroupSortingPopupMenuItem.addActionListener(this);
		m_sortManualSortPopupMenuItem.addActionListener(this);
		m_sortAutoSortPopupMenuItem.addActionListener(this);
		for(int i=0;i<m_sortDirectionPopupMenuItems.length;i++) {
			m_sortDirectionPopupMenuItems[i].addActionListener(this);
		}
		for(int i=0;i<m_sortTypePopupMenuItems.length;i++) {
			m_sortTypePopupMenuItems[i].addActionListener(this);
		}
		m_savePopupMenuItem.addActionListener(this);
		m_saveAsPopupMenuItem.addActionListener(this);
		m_addFilesPopupMenuItem.addActionListener(this);
		m_removeFilesPopupMenuItem.addActionListener(this);
		m_replaceFilePopupMenuItem.addActionListener(this);
		m_renameFilePopupMenuItem.addActionListener(this);
		m_extractFilesPopupMenuItem.addActionListener(this);
		m_importPopupMenuItem.addActionListener(this);
		m_exportPopupMenuItem.addActionListener(this);
		m_closePopupMenuItem.addActionListener(this);
		m_closeAllPopupMenuItem.addActionListener(this);
		m_canelPopupMenuItem.addActionListener(this);
		
		m_selectPopupMenu.add(m_selectInversePopupMenuItem);
		m_selectPopupMenu.add(m_selectRandomPopupMenuItem);
		m_selectPopupMenu.add(m_selectAllPopupMenuItem);
		m_selectPopupMenu.add(m_selectNonePopupMenuItem);
		
		m_sortTargetPopupMenu.add(m_sortAllGroupsPopupMenuItem);
		m_sortTargetPopupMenu.add(m_sortPerGroupSortingPopupMenuItem);
		m_sortPopupMenu.add(m_sortTargetPopupMenu);
		for(int i=0;i<m_sortDirectionPopupMenuItems.length;i++) {
			m_sortDirectionPopupMenu.add(m_sortDirectionPopupMenuItems[i]);
		}
		m_sortPopupMenu.add(m_sortDirectionPopupMenu);
		for(int i=0;i<m_sortTypePopupMenuItems.length;i++) {
			m_sortTypePopupMenu.add(m_sortTypePopupMenuItems[i]);
		}
		m_sortPopupMenu.add(m_sortTypePopupMenu);
		m_sortPopupMenu.add(m_sortManualSortPopupMenuItem);
		m_sortPopupMenu.add(m_sortAutoSortPopupMenuItem);
		
		m_groupPanelPopupMenu.add(m_selectPopupMenu);
		m_groupPanelPopupMenu.add(m_sortPopupMenu);
		m_groupPanelPopupMenu.add(m_savePopupMenuItem);
		m_groupPanelPopupMenu.add(m_saveAsPopupMenuItem);
		m_groupPanelPopupMenu.add(m_addFilesPopupMenuItem);
		m_groupPanelPopupMenu.add(m_removeFilesPopupMenuItem);
		m_groupPanelPopupMenu.add(m_replaceFilePopupMenuItem);
		m_groupPanelPopupMenu.add(m_renameFilePopupMenuItem);
		m_groupPanelPopupMenu.add(m_extractFilesPopupMenuItem);
		m_groupPanelPopupMenu.add(m_importPopupMenuItem);
		m_groupPanelPopupMenu.add(m_exportPopupMenuItem);
		m_groupPanelPopupMenu.add(m_closePopupMenuItem);
		m_groupPanelPopupMenu.add(m_closeAllPopupMenuItem);
		m_groupPanelPopupMenu.addSeparator();
		m_groupPanelPopupMenu.add(m_canelPopupMenuItem);
	}

	public String getTabName() {
		String fileName = m_group.getFile() == null ? null : m_group.getFile().getName();
		return fileName == null ? "NEW " + m_group.getFileExtension() + " *" : fileName + (m_group == null || !m_group.isChanged() ? "" : " *");
	}
	
	public String getTabDescription() {
		String fileName = m_group.getFile() == null ? null : m_group.getFile().getName();
		return "Group " + m_itemNumber + (fileName == null ? "" : " (" + fileName + ")");
	}
	
	public Group getGroup() {
		return m_group;
	}
	
	public boolean setGroup(Group group) {
		return setItem(group);
	}

	public boolean setItem(Item item) {
		if(item != null && !(item instanceof Group)) {
			return false;
		}
		
		if(m_group != null) {
			m_group.removeGroupSortListener(this);
		}
		
		if(!super.setItem(item)) {
			return false;
		}
		
		m_group = (Group) item;
		
		if(m_group != null) {
			m_group.addGroupSortListener(this);
		}
		
		return true;
	}
	
	public String getFileExtension() {
		return m_group.getFileExtension();
	}
	
	public abstract int numberOfSelectedFiles();
	
	public abstract Vector<GroupFile> getSelectedFiles();
	
	public int numberOfGroupActionListeners() {
		return m_groupActionListeners.size();
	}
	
	public GroupActionListener getGroupActionListener(int index) {
		if(index < 0 || index >= m_groupActionListeners.size()) { return null; }
		
		return m_groupActionListeners.elementAt(index);
	}
	
	public boolean hasGroupActionListener(GroupActionListener a) {
		return m_groupActionListeners.contains(a);
	}
	
	public int indexOfGroupActionListener(GroupActionListener a) {
		return m_groupActionListeners.indexOf(a);
	}
	
	public boolean addGroupActionListener(GroupActionListener a) {
		if(a == null || m_groupActionListeners.contains(a)) { return false; }
		
		m_groupActionListeners.add(a);
		
		return true;
	}
	
	public boolean removeGroupActionListener(int index) {
		if(index < 0 || index >= m_groupActionListeners.size()) { return false; }
		
		m_groupActionListeners.remove(index);
		
		return true;
	}
	
	public boolean removeGroupActionListener(GroupActionListener a) {
		if(a == null) { return false; }
		
		return m_groupActionListeners.remove(a);
	}
	
	public void clearGroupActionListeners() {
		m_groupActionListeners.clear();
	}
	
	public void dispatchGroupAction(GroupAction action) {
		if(!GroupAction.isvalid(action)) { return; }
		
		for(int i=0;i<m_groupActionListeners.size();i++) {
			m_groupActionListeners.elementAt(i).handleGroupAction(action);
		}
	}
	
	public void handleGroupSortStateChanged(Group group) {
		if(group == null || m_group != group) { return; }
		
		update();
		updateWindow();
		
		notifyUpdateWindow();
	}
	
	public void handleGroupSortStarted(Group group) {
		if(group == null || m_group != group) { return; }
		
		backupSelectedGroupFiles();
	}
	
	public void handleGroupSortFinished(Group group) {
		if(group == null || m_group != group) { return; }
		
		restoreSelectedGroupFiles();
	}
	
	public abstract void selectInverse();
	
	public abstract void selectRandom();

	public abstract void selectAll();

	public abstract void clearSelection();
	
	public abstract boolean backupSelectedGroupFiles();
	
	public abstract boolean restoreSelectedGroupFiles();
	
	public boolean save() throws ItemWriteException {
		if(m_group == null) { return false; }
		
		updateGroup();
		
		return m_group.save();
	}
	
	public void update() {
		if(!m_initialized || m_updating) { return; }
		
		m_updating = true;
		
		m_selectInversePopupMenuItem.setEnabled(m_group != null && m_group.numberOfFiles() > 0);
		m_selectRandomPopupMenuItem.setEnabled(m_group != null && m_group.numberOfFiles() > 0);
		m_selectAllPopupMenuItem.setEnabled(m_group != null && m_group.numberOfFiles() > 0);
		m_selectNonePopupMenuItem.setEnabled(m_group != null && m_group.numberOfFiles() > 0);
		
		m_sortAllGroupsPopupMenuItem.setSelected(SettingsManager.instance.sortAllGroups);
		m_sortPerGroupSortingPopupMenuItem.setSelected(!SettingsManager.instance.sortAllGroups);
		if(SettingsManager.instance.sortAllGroups) {
			if(SettingsManager.instance.sortDirection.isValid()) {
				m_sortDirectionPopupMenuItems[SettingsManager.instance.sortDirection.ordinal()].setSelected(true);
			}
			if(SettingsManager.instance.sortType.isValid()) {
				m_sortTypePopupMenuItems[SettingsManager.instance.sortType.ordinal()].setSelected(true);
			}
			m_sortAutoSortPopupMenuItem.setSelected(SettingsManager.instance.autoSortFiles);
		}
		else {
			if(m_group != null) {
				if(m_group.getSortDirection().isValid()) {
					m_sortDirectionPopupMenuItems[m_group.getSortDirection().ordinal()].setSelected(true);
				}
				if(m_group.getSortType().isValid()) {
					m_sortTypePopupMenuItems[m_group.getSortType().ordinal()].setSelected(true);
				}
				m_sortAutoSortPopupMenuItem.setSelected(m_group.getAutoSortFiles());
			}
		}
		m_sortManualSortPopupMenuItem.setEnabled(m_group == null ? false : m_group.shouldSortFiles());
		
		m_removeFilesPopupMenuItem.setText("Remove File" + (numberOfSelectedFiles() == 1 ? "" : "s"));
		m_extractFilesPopupMenuItem.setText("Extract File" + (numberOfSelectedFiles() == 1 ? "" : "s"));
		
		m_removeFilesPopupMenuItem.setEnabled(numberOfSelectedFiles() > 0);
		m_replaceFilePopupMenuItem.setEnabled(numberOfSelectedFiles() == 1);
		m_renameFilePopupMenuItem.setEnabled(numberOfSelectedFiles() == 1);
		m_extractFilesPopupMenuItem.setEnabled(numberOfSelectedFiles() > 0);
		
		m_updating = false;
	}
	
	public abstract void updateGroup();
	
	public void autoSort() {
		if(m_group == null) { return; }
		
		if(m_group.shouldAutoSortFiles()) {
			m_group.sortFiles();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(m_group == null || e == null || e.getSource() == null || m_updating) { return; }
		
		if(e.getSource() == m_selectInversePopupMenuItem) {
			selectInverse();
		}
		else if(e.getSource() == m_selectRandomPopupMenuItem) {
			selectRandom();
		}
		else if(e.getSource() == m_selectAllPopupMenuItem) {
			selectAll();
		}
		else if(e.getSource() == m_selectNonePopupMenuItem) {
			clearSelection();
		}
		// enable sorting for all groups
		else if(e.getSource() == m_sortAllGroupsPopupMenuItem) {
			if(SettingsManager.instance.sortAllGroups) { return; }
			
			SettingsManager.instance.sortAllGroups = true;
			
			notifyUpdateAll();
		}
		// enable per-group sorting
		else if(e.getSource() == m_sortPerGroupSortingPopupMenuItem) {
			if(!SettingsManager.instance.sortAllGroups) { return; }
			
			SettingsManager.instance.sortAllGroups = false;
			
			notifyUpdateAll();
		}
		// manual sorting
		else if(e.getSource() == m_sortManualSortPopupMenuItem) {
			if(m_group == null) { return; }
			
			m_group.sortFiles();
		}
		// toggle file auto-sorting
		else if(e.getSource() == m_sortAutoSortPopupMenuItem) {
			if(SettingsManager.instance.sortAllGroups) {
				SettingsManager.instance.autoSortFiles = m_sortAutoSortPopupMenuItem.isSelected();
				
				update();
				updateWindow();
				
				notifyUpdateWindow();
			}
			else {
				m_group.setAutoSortFiles(m_sortAutoSortPopupMenuItem.isSelected());
			}
		}
		else if(e.getSource() == m_savePopupMenuItem) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.Save));
		}
		else if(e.getSource() == m_saveAsPopupMenuItem) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.SaveAs));
		}
		else if(e.getSource() == m_addFilesPopupMenuItem) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.AddFiles));
		}
		else if(e.getSource() == m_removeFilesPopupMenuItem) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.RemoveFiles));
		}
		else if(e.getSource() == m_replaceFilePopupMenuItem) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.ReplaceFile));
		}
		else if(e.getSource() == m_renameFilePopupMenuItem) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.RenameFile));
		}
		else if(e.getSource() == m_extractFilesPopupMenuItem) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.ExtractFiles));
		}
		else if(e.getSource() == m_importPopupMenuItem) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.Import));
		}
		else if(e.getSource() == m_exportPopupMenuItem) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.Export));
		}
		else if(e.getSource() == m_closePopupMenuItem) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.Close));
		}
		else if(e.getSource() == m_closeAllPopupMenuItem) {
			dispatchGroupAction(new GroupAction(this, GroupActionType.CloseAll));
		}
		else {
			// change group file sort direction
			for(int i=0;i<m_sortDirectionPopupMenuItems.length;i++) {
				if(e.getSource() == m_sortDirectionPopupMenuItems[i]) {
					if(SettingsManager.instance.sortAllGroups) {
						if(SettingsManager.instance.sortDirection == SortDirection.values()[i]) { return; }
						
						SettingsManager.instance.sortDirection = SortDirection.values()[i];
						
						notifyUpdateAll();
					}
					else {
						m_group.setSortDirection(SortDirection.values()[i]);
					}
					
					return;
				}
			}
			
			// change group file sort type
			for(int i=0;i<m_sortTypePopupMenuItems.length;i++) {
				if(e.getSource() == m_sortTypePopupMenuItems[i]) {
					if(SettingsManager.instance.sortAllGroups) {
						if(SettingsManager.instance.sortType == GroupFileSortType.values()[i]) { return; }
						
						SettingsManager.instance.sortType = GroupFileSortType.values()[i];
						
						notifyUpdateAll();
					}
					else {
						m_group.setSortType(GroupFileSortType.values()[i]);
					}
					
					return;
				}
			}
		}
	}
	
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseMoved(MouseEvent e) { }
	public void mouseDragged(MouseEvent e) { }
	
	public void mouseReleased(MouseEvent e) {
		if(!m_initialized || m_group == null) { return; }
		
		if(e.getButton() == MouseEvent.BUTTON3) {
			showPopupMenu(MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y);
		}
	}
	
	private void showPopupMenu(int x, int y) {
		if(!m_initialized || m_group == null) { return; }
		
		update();
		
		m_groupPanelPopupMenu.show(this, x, y);
	}
	
	public Dimension getPreferredSize() {
		return getParent().getSize();
	}
	
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}
	
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		int currentPosition = 0;
		if(orientation == SwingConstants.HORIZONTAL) {
			currentPosition = visibleRect.x;
		}
		else {
			currentPosition = visibleRect.y;
		}
        
		int maxUnitIncrement = 40;
		if(direction < 0) {
			int newPosition = currentPosition -
							  (currentPosition / maxUnitIncrement)
                              * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        }
		else {
            return ((currentPosition / maxUnitIncrement) + 1)
                   * maxUnitIncrement
                   - currentPosition;
        }
	}
	
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if(orientation == SwingConstants.HORIZONTAL) {
			return visibleRect.width - 5;
		}
		else {
			return visibleRect.height - 5;
		}
	}
	
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}
	
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
}
