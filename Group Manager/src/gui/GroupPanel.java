package gui;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import exception.*;
import group.*;
import action.*;

public abstract class GroupPanel extends JPanel implements Scrollable, ActionListener, MouseListener {
	
	protected int m_groupNumber;
	protected Group m_group;
	protected Vector<GroupChangeListener> m_groupChangeListeners;
	protected Vector<GroupActionListener> m_groupActionListeners;
	protected boolean m_changed;
	protected boolean m_initialized;
	
	protected JPopupMenu m_groupPanelPopupMenu;
	protected JMenuItem m_savePopupMenuItem;
	protected JMenuItem m_saveAsPopupMenuItem;
	protected JMenuItem m_importPopupMenuItem;
	protected JMenuItem m_exportPopupMenuItem;
	protected JMenuItem m_closePopupMenuItem;
	protected JMenuItem m_canelPopupMenuItem;
	
	private static final long serialVersionUID = 6360144485605480625L;

	public GroupPanel() {
		this(null);
	}
	
	public GroupPanel(Group group) {
		m_groupChangeListeners = new Vector<GroupChangeListener>();
		m_groupActionListeners = new Vector<GroupActionListener>();
		m_changed = false;
		m_initialized = false;
		
		setGroup(group);
		
		initPopupMenu();
		
		addMouseListener(this);
	}
	
	public abstract boolean init();
	
	public void initPopupMenu() {
		m_groupPanelPopupMenu = new JPopupMenu();
		
		m_savePopupMenuItem = new JMenuItem("Save");
		m_saveAsPopupMenuItem = new JMenuItem("Save As");
		m_importPopupMenuItem = new JMenuItem("Import");
		m_exportPopupMenuItem = new JMenuItem("Export");
		m_closePopupMenuItem = new JMenuItem("Close");
		m_canelPopupMenuItem = new JMenuItem("Cancel");
		
		m_savePopupMenuItem.addActionListener(this);
		m_saveAsPopupMenuItem.addActionListener(this);
		m_importPopupMenuItem.addActionListener(this);
		m_exportPopupMenuItem.addActionListener(this);
		m_closePopupMenuItem.addActionListener(this);
		m_canelPopupMenuItem.addActionListener(this);
		
		m_groupPanelPopupMenu.add(m_savePopupMenuItem);
		m_groupPanelPopupMenu.add(m_saveAsPopupMenuItem);
		m_groupPanelPopupMenu.add(m_importPopupMenuItem);
		m_groupPanelPopupMenu.add(m_exportPopupMenuItem);
		m_groupPanelPopupMenu.add(m_closePopupMenuItem);
		m_groupPanelPopupMenu.addSeparator();
		m_groupPanelPopupMenu.add(m_canelPopupMenuItem);
	}
	
	public int getGroupNumber() {
		return m_groupNumber;
	}
	
	public String getTabName() {
		String fileName = m_group.getFile() == null ? null : m_group.getFile().getName();
		return fileName == null ? "NEW " + m_group.getFileExtension() + " *" : fileName + (m_changed ? " *" : "");
	}
	
	public String getTabDescription() {
		String fileName = m_group.getFile() == null ? null : m_group.getFile().getName();
		return "Group " + m_groupNumber + (fileName == null ? "" : " (" + fileName + ")");
	}
	
	public Group getGroup() {
		return m_group;
	}
	
	public String getFileExtension() {
		return m_group.getFileExtension();
	}
	
	public boolean isChanged() {
		return m_changed;
	}
	
	public void setGroupNumber(int groupNumber) {
		m_groupNumber = groupNumber;
	}
	
	public void setChanged(boolean changed) {
		m_changed = changed;
		
		if(m_changed) {
			handleGroupChange();
		}
	}
	
	public boolean isSameFile(File file) {
		if(file == null || m_group.getFile() == null) { return false; }
		
		File localCanonicalFile = null;
		File externalCanonicalFile = null;
		try {
			localCanonicalFile = m_group.getFile().getCanonicalFile();
			externalCanonicalFile = file.getCanonicalFile();
			
			return localCanonicalFile.equals(externalCanonicalFile);
		}
		catch(IOException e) {
			return m_group.getFile().equals(file);
		}
	}
	
	public int numberOfGroupChangeListeners() {
		return m_groupChangeListeners.size();
	}
	
	public GroupChangeListener getGroupChangeListener(int index) {
		if(index < 0 || index >= m_groupChangeListeners.size()) { return null; }
		return m_groupChangeListeners.elementAt(index);
	}
	
	public boolean hasGroupChangeListener(GroupChangeListener a) {
		return m_groupChangeListeners.contains(a);
	}
	
	public int indexOfGroupChangeListener(GroupChangeListener a) {
		return m_groupChangeListeners.indexOf(a);
	}
	
	public boolean addGroupChangeListener(GroupChangeListener a) {
		if(a == null || m_groupChangeListeners.contains(a)) { return false; }
		
		m_groupChangeListeners.add(a);
		
		return true;
	}
	
	public boolean removeGroupChangeListener(int index) {
		if(index < 0 || index >= m_groupChangeListeners.size()) { return false; }
		m_groupChangeListeners.remove(index);
		return true;
	}
	
	public boolean removeGroupChangeListener(GroupChangeListener a) {
		if(a == null) { return false; }
		return m_groupChangeListeners.remove(a);
	}
	
	public void clearGroupChangeListeners() {
		m_groupChangeListeners.clear();
	}
	
	public void handleGroupChange() {
		for(int i=0;i<m_groupChangeListeners.size();i++) {
			m_groupChangeListeners.elementAt(i).notifyGroupChanged(this);
		}
	}
	
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
	
	public void handleGroupAction(GroupAction action) {
		if(!GroupAction.isvalid(action)) { return; }
		
		for(int i=0;i<m_groupActionListeners.size();i++) {
			m_groupActionListeners.elementAt(i).handleGroupAction(action);
		}
	}
	
	public boolean setGroup(Group group) {
		m_group = group;
		
		updateLayout();
		
		return true;
	}
	
	public boolean save() throws GroupWriteException {
		if(m_group == null) { return false; }
		
		updateGroup();
		
		boolean saved = m_group.save();
		if(saved) { setChanged(false); }
		return saved;
	}
	
	public abstract void updateGroup();
	
	public abstract void updateWindow();
	
	public abstract void updateLayout();
	
	public void actionPerformed(ActionEvent e) {
		if(m_group == null || e == null || e.getSource() == null) { return; }
		
		if(e.getSource() == m_savePopupMenuItem) {
			handleGroupAction(new GroupAction(this, GroupActionType.Save));
		}
		else if(e.getSource() == m_saveAsPopupMenuItem) {
			handleGroupAction(new GroupAction(this, GroupActionType.SaveAs));
		}
		else if(e.getSource() == m_importPopupMenuItem) {
			handleGroupAction(new GroupAction(this, GroupActionType.Import));
		}
		else if(e.getSource() == m_exportPopupMenuItem) {
			handleGroupAction(new GroupAction(this, GroupActionType.Export));
		}
		else if(e.getSource() == m_closePopupMenuItem) {
			handleGroupAction(new GroupAction(this, GroupActionType.Close));
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
