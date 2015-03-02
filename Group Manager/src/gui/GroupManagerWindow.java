package gui;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import exception.*;
import utilities.*;
import settings.*;
import console.*;
import group.*;
import action.*;
import version.*;

public class GroupManagerWindow implements WindowListener, ComponentListener, ChangeListener, ActionListener, GroupActionListener, UpdateListener, Updatable {
	
	private JFrame m_frame;
	private JTabbedPane m_mainTabbedPane;
	private Vector<GroupPanel> m_groupPanels;
	private JTextArea m_consoleText;
	private Font m_consoleFont;
	private JScrollPane m_consoleScrollPane;
	
	private ProgressDialog m_progressDialog;
	
	private JMenuBar m_menuBar;
	private JMenu m_fileMenu;
	private JMenuItem m_fileNewMenuItem;
	private JMenuItem m_fileOpenMenuItem;
	private JMenuItem m_fileSaveMenuItem;
	private JMenuItem m_fileSaveAsMenuItem;
	private JMenuItem m_fileSaveAllMenuItem;
	private JMenuItem m_fileAddFilesMenuItem;
	private JMenuItem m_fileRemoveFilesMenuItem;
	private JMenuItem m_fileReplaceFileMenuItem;
	private JMenuItem m_fileExtractFilesMenuItem;
	private JMenuItem m_fileImportMenuItem;
	private JMenuItem m_fileExportMenuItem;
	private JMenuItem m_fileCloseMenuItem;
	private JMenuItem m_fileCloseAllMenuItem;
	private JMenuItem m_fileExitMenuItem;
	private JMenu m_selectMenu;
	private JMenuItem m_selectInverseMenuItem;
	private JMenuItem m_selectRandomMenuItem;
	private JMenuItem m_selectAllMenuItem;
	private JMenuItem m_selectNoneMenuItem;
	private JMenu m_sortMenu;
	private JMenu m_sortTargetMenu;
	private JMenu m_sortDirectionMenu;
	private JMenu m_sortTypeMenu;
	private JRadioButtonMenuItem m_sortAllGroupsMenuItem;
	private JRadioButtonMenuItem m_sortPerGroupSortingMenuItem;
	private JMenuItem m_sortManualSortMenuItem;
	private JCheckBoxMenuItem m_sortAutoSortMenuItem;
	private JRadioButtonMenuItem[] m_sortDirectionMenuItems;
	private JRadioButtonMenuItem[] m_sortTypeMenuItems;
	private ButtonGroup m_sortDirectionButtonGroup;
	private ButtonGroup m_sortTypeButtonGroup;
	private JMenu m_settingsMenu;
	private JMenuItem m_settingsPluginDirectoryNameMenuItem;
	private JMenuItem m_settingsConsoleLogFileNameMenuItem;
	private JMenuItem m_settingsLogDirectoryNameMenuItem;
	private JMenuItem m_settingsVersionFileURLMenuItem;
	private JCheckBoxMenuItem m_settingsAutoScrollConsoleMenuItem;
	private JMenuItem m_settingsMaxConsoleHistoryMenuItem;
	private JCheckBoxMenuItem m_settingsLogConsoleMenuItem;
	private JCheckBoxMenuItem m_settingsSupressUpdatesMenuItem;
	private JCheckBoxMenuItem m_settingsAutoSaveSettingsMenuItem;
	private JMenuItem m_settingsSaveSettingsMenuItem;
	private JMenuItem m_settingsReloadSettingsMenuItem;
	private JMenuItem m_settingsResetSettingsMenuItem;
	private JMenu m_pluginsMenu;
	private JMenuItem m_pluginsListLoadedMenuItem;
	private JMenuItem m_pluginsLoadMenuItem;
	private JMenuItem m_pluginsLoadAllMenuItem;
	private JCheckBoxMenuItem m_pluginsAutoLoadMenuItem;
	private JMenu m_windowMenu;
	private JMenuItem m_windowResetPositionMenuItem;
	private JMenuItem m_windowResetSizeMenuItem;
	private JMenu m_helpMenu;
	private JMenuItem m_helpCheckVersionMenuItem;
	private JMenuItem m_helpAboutMenuItem;
	
	private boolean m_initialized;
	private boolean m_updating;
	
	public static final int SCROLL_INCREMENT = 16;
	
	private TransferHandler m_transferHandler = new TransferHandler() {
		
		private static final long serialVersionUID = 3187590873158737811L;

		public boolean canImport(TransferHandler.TransferSupport support) {
			if(!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				return false;
			}
			
			support.setDropAction(COPY);
			
			return true;
		}
		
		@SuppressWarnings("unchecked")
		public boolean importData(TransferHandler.TransferSupport support) {
			if(!canImport(support)) {
				return false;
			}
			
			try {
				loadGroups(((java.util.List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor)).toArray(new File[1]));
			}
			catch(UnsupportedFlavorException e) {
				return false;
			}
			catch(IOException e) {
				return false;
			}
			
			return true;
		}
	};
	
	public GroupManagerWindow() {
		m_frame = new JFrame("Group Manager Window");
		m_frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		m_frame.setMinimumSize(new Dimension(320, 240));
		m_frame.setLocation(SettingsManager.defaultWindowPositionX, SettingsManager.defaultWindowPositionY);
		m_frame.setSize(SettingsManager.defaultWindowWidth, SettingsManager.defaultWindowHeight);
		m_frame.addWindowListener(this);
		m_frame.addComponentListener(this);
		m_frame.setTransferHandler(m_transferHandler);
		
		m_groupPanels = new Vector<GroupPanel>();
		m_initialized = false;
		m_updating = false;
		
		initMenu();
 		initComponents();
 		
 		update();
	}
	
	public boolean initialize() {
		if(m_initialized) { return false; }
		
		updateWindow();
		
		m_frame.setLocation(SettingsManager.instance.windowPositionX, SettingsManager.instance.windowPositionY);
		m_frame.setSize(SettingsManager.instance.windowWidth, SettingsManager.instance.windowHeight);
		
		// update and show the gui window
		update();
		m_frame.setVisible(true);
		
		m_progressDialog = new ProgressDialog(m_frame);
		
		m_initialized = true;
		
		update();
		
		return true;
	}
	
	// initialize the menu
	private void initMenu() {
		m_menuBar = new JMenuBar();
		
		m_fileMenu = new JMenu("File");
		m_fileNewMenuItem = new JMenuItem("New");
		m_fileOpenMenuItem = new JMenuItem("Open");
		m_fileSaveMenuItem = new JMenuItem("Save");
		m_fileSaveAsMenuItem = new JMenuItem("Save As");
		m_fileSaveAllMenuItem = new JMenuItem("Save All");
		m_fileAddFilesMenuItem = new JMenuItem("Add Files");
		m_fileRemoveFilesMenuItem = new JMenuItem("Remove Files");
		m_fileReplaceFileMenuItem = new JMenuItem("Replace File");
		m_fileExtractFilesMenuItem = new JMenuItem("Extract Files");
		m_fileImportMenuItem = new JMenuItem("Import");
		m_fileExportMenuItem = new JMenuItem("Export");
		m_fileCloseMenuItem = new JMenuItem("Close");
		m_fileCloseAllMenuItem = new JMenuItem("Close All");
		m_fileExitMenuItem = new JMenuItem("Exit");
		
		m_fileNewMenuItem.setMnemonic('N');
		m_fileOpenMenuItem.setMnemonic('O');
		m_fileSaveMenuItem.setMnemonic('S');
		m_fileAddFilesMenuItem.setMnemonic('D');
		m_fileRemoveFilesMenuItem.setMnemonic('R');
		m_fileExtractFilesMenuItem.setMnemonic('E');
		m_fileImportMenuItem.setMnemonic('I');
		m_fileExportMenuItem.setMnemonic('P');
		m_fileExitMenuItem.setMnemonic('Q');
		
		m_fileNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK));
		m_fileOpenMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK));
		m_fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));
		m_fileAddFilesMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.Event.CTRL_MASK));
		m_fileRemoveFilesMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.Event.CTRL_MASK));
		m_fileExtractFilesMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.Event.CTRL_MASK));
		m_fileImportMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.Event.CTRL_MASK));
		m_fileExportMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.Event.CTRL_MASK));
		m_fileExitMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.Event.CTRL_MASK));
		
		m_selectMenu = new JMenu("Select");
		m_selectInverseMenuItem = new JMenuItem("Inverse");
		m_selectRandomMenuItem = new JMenuItem("Random");
		m_selectAllMenuItem = new JMenuItem("All");
		m_selectNoneMenuItem = new JMenuItem("None");
		
		m_sortMenu = new JMenu("Sort");
		m_sortTargetMenu = new JMenu("Target");
		m_sortDirectionMenu = new JMenu("Direction");
		m_sortTypeMenu = new JMenu("Type");
		m_sortAllGroupsMenuItem = new JRadioButtonMenuItem("Sort All Groups");
		m_sortPerGroupSortingMenuItem = new JRadioButtonMenuItem("Per Group Sorting");
		m_sortManualSortMenuItem = new JMenuItem("Manual Sort");
		m_sortAutoSortMenuItem = new JCheckBoxMenuItem("Auto-Sort Group Files");
		m_sortDirectionMenuItems = new JRadioButtonMenuItem[SortDirection.numberOfSortDirections()];
		m_sortDirectionButtonGroup = new ButtonGroup();
		for(int i=0;i<m_sortDirectionMenuItems.length;i++) {
			m_sortDirectionMenuItems[i] = new JRadioButtonMenuItem(SortDirection.displayNames[i]);
			m_sortDirectionButtonGroup.add(m_sortDirectionMenuItems[i]);
		}
		m_sortTypeMenuItems = new JRadioButtonMenuItem[GroupFileSortType.numberOfSortTypes()];
		m_sortTypeButtonGroup = new ButtonGroup();
		for(int i=0;i<m_sortTypeMenuItems.length;i++) {
			m_sortTypeMenuItems[i] = new JRadioButtonMenuItem(GroupFileSortType.displayNames[i]);
			m_sortTypeButtonGroup.add(m_sortTypeMenuItems[i]);
		}
		
		m_settingsMenu = new JMenu("Settings");
		m_settingsPluginDirectoryNameMenuItem = new JMenuItem("Plugin Directory Name");
		m_settingsConsoleLogFileNameMenuItem = new JMenuItem("Console Log File Name");
		m_settingsLogDirectoryNameMenuItem = new JMenuItem("Log Directory Name");
		m_settingsVersionFileURLMenuItem = new JMenuItem("Version File URL");
		m_settingsAutoScrollConsoleMenuItem = new JCheckBoxMenuItem("Auto-Scroll Console");
		m_settingsMaxConsoleHistoryMenuItem = new JMenuItem("Max Console History");
		m_settingsLogConsoleMenuItem = new JCheckBoxMenuItem("Log Console");
		m_settingsSupressUpdatesMenuItem = new JCheckBoxMenuItem("Supress Update Notifications");
		m_settingsAutoSaveSettingsMenuItem = new JCheckBoxMenuItem("Auto-Save Settings");
		m_settingsSaveSettingsMenuItem = new JMenuItem("Save Settings");
		m_settingsReloadSettingsMenuItem = new JMenuItem("Reload Settings");
		m_settingsResetSettingsMenuItem = new JMenuItem("Reset Settings");
		m_settingsAutoScrollConsoleMenuItem.setSelected(SettingsManager.defaultAutoScrollConsole);
		m_settingsAutoSaveSettingsMenuItem.setSelected(SettingsManager.defaultAutoSaveSettings);
		m_settingsLogConsoleMenuItem.setSelected(SettingsManager.defaultLogConsole);
		m_settingsSupressUpdatesMenuItem.setSelected(SettingsManager.defaultSupressUpdates);
		
		m_pluginsMenu = new JMenu("Plugins");
		m_pluginsListLoadedMenuItem = new JMenuItem("List Loaded Plugins");
		m_pluginsLoadMenuItem = new JMenuItem("Load Plugin");
		m_pluginsLoadAllMenuItem = new JMenuItem("Load All Plugins");
		m_pluginsAutoLoadMenuItem = new JCheckBoxMenuItem("Auto-Load Plugins");
		m_pluginsAutoLoadMenuItem.setSelected(SettingsManager.defaultAutoLoadPlugins);
		
		m_windowMenu = new JMenu("Window");
		m_windowResetPositionMenuItem = new JMenuItem("Reset Window Position");
		m_windowResetSizeMenuItem = new JMenuItem("Reset Window Size");
		
		m_helpMenu = new JMenu("Help");
		m_helpCheckVersionMenuItem = new JMenuItem("Check for Updates");
		m_helpAboutMenuItem = new JMenuItem("About");
		
		m_fileNewMenuItem.addActionListener(this);
		m_fileOpenMenuItem.addActionListener(this);
		m_fileSaveMenuItem.addActionListener(this);
		m_fileSaveAsMenuItem.addActionListener(this);
		m_fileSaveAllMenuItem.addActionListener(this);
		m_fileAddFilesMenuItem.addActionListener(this);
		m_fileRemoveFilesMenuItem.addActionListener(this);
		m_fileReplaceFileMenuItem.addActionListener(this);
		m_fileExtractFilesMenuItem.addActionListener(this);
		m_fileImportMenuItem.addActionListener(this);
		m_fileExportMenuItem.addActionListener(this);
		m_fileCloseMenuItem.addActionListener(this);
		m_fileCloseAllMenuItem.addActionListener(this);
		m_fileExitMenuItem.addActionListener(this);
		m_selectInverseMenuItem.addActionListener(this);
		m_selectRandomMenuItem.addActionListener(this);
		m_selectAllMenuItem.addActionListener(this);
		m_selectNoneMenuItem.addActionListener(this);
		m_sortAllGroupsMenuItem.addActionListener(this);
		m_sortPerGroupSortingMenuItem.addActionListener(this);
		m_sortManualSortMenuItem.addActionListener(this);
		m_sortAutoSortMenuItem.addActionListener(this);
		for(int i=0;i<m_sortDirectionMenuItems.length;i++) {
			m_sortDirectionMenuItems[i].addActionListener(this);
		}
		for(int i=0;i<m_sortTypeMenuItems.length;i++) {
			m_sortTypeMenuItems[i].addActionListener(this);
		}
		m_settingsPluginDirectoryNameMenuItem.addActionListener(this);
		m_settingsConsoleLogFileNameMenuItem.addActionListener(this);
		m_settingsLogDirectoryNameMenuItem.addActionListener(this);
		m_settingsVersionFileURLMenuItem.addActionListener(this);
		m_settingsAutoScrollConsoleMenuItem.addActionListener(this);
		m_settingsMaxConsoleHistoryMenuItem.addActionListener(this);
		m_settingsLogConsoleMenuItem.addActionListener(this);
		m_settingsSupressUpdatesMenuItem.addActionListener(this);
		m_settingsAutoSaveSettingsMenuItem.addActionListener(this);
		m_settingsSaveSettingsMenuItem.addActionListener(this);
		m_settingsReloadSettingsMenuItem.addActionListener(this);
		m_settingsResetSettingsMenuItem.addActionListener(this);
		m_pluginsListLoadedMenuItem.addActionListener(this);
		m_pluginsLoadMenuItem.addActionListener(this);
		m_pluginsLoadAllMenuItem.addActionListener(this);
		m_pluginsAutoLoadMenuItem.addActionListener(this);
		m_windowResetPositionMenuItem.addActionListener(this);
		m_windowResetSizeMenuItem.addActionListener(this);
		m_helpCheckVersionMenuItem.addActionListener(this);
		m_helpAboutMenuItem.addActionListener(this);
		
		m_fileMenu.add(m_fileNewMenuItem);
		m_fileMenu.add(m_fileOpenMenuItem);
		m_fileMenu.add(m_fileSaveMenuItem);
		m_fileMenu.add(m_fileSaveAsMenuItem);
		m_fileMenu.add(m_fileSaveAllMenuItem);
		m_fileMenu.add(m_fileAddFilesMenuItem);
		m_fileMenu.add(m_fileRemoveFilesMenuItem);
		m_fileMenu.add(m_fileReplaceFileMenuItem);
		m_fileMenu.add(m_fileExtractFilesMenuItem);
		m_fileMenu.add(m_fileImportMenuItem);
		m_fileMenu.add(m_fileExportMenuItem);
		m_fileMenu.add(m_fileCloseMenuItem);
		m_fileMenu.add(m_fileCloseAllMenuItem);
		m_fileMenu.add(m_fileExitMenuItem);
		
		m_selectMenu.add(m_selectInverseMenuItem);
		m_selectMenu.add(m_selectRandomMenuItem);
		m_selectMenu.add(m_selectAllMenuItem);
		m_selectMenu.add(m_selectNoneMenuItem);
		
		m_sortTargetMenu.add(m_sortAllGroupsMenuItem);
		m_sortTargetMenu.add(m_sortPerGroupSortingMenuItem);
		m_sortMenu.add(m_sortTargetMenu);
		for(int i=0;i<m_sortDirectionMenuItems.length;i++) {
			m_sortDirectionMenu.add(m_sortDirectionMenuItems[i]);
		}
		m_sortMenu.add(m_sortDirectionMenu);
		for(int i=0;i<m_sortTypeMenuItems.length;i++) {
			m_sortTypeMenu.add(m_sortTypeMenuItems[i]);
		}
		m_sortMenu.add(m_sortTypeMenu);
		m_sortMenu.add(m_sortManualSortMenuItem);
		m_sortMenu.add(m_sortAutoSortMenuItem);
		
		m_settingsMenu.add(m_settingsPluginDirectoryNameMenuItem);
		m_settingsMenu.add(m_settingsConsoleLogFileNameMenuItem);
		m_settingsMenu.add(m_settingsLogDirectoryNameMenuItem);
		m_settingsMenu.add(m_settingsVersionFileURLMenuItem);
		m_settingsMenu.add(m_settingsAutoScrollConsoleMenuItem);
		m_settingsMenu.add(m_settingsMaxConsoleHistoryMenuItem);
		m_settingsMenu.add(m_settingsLogConsoleMenuItem);
		m_settingsMenu.add(m_settingsSupressUpdatesMenuItem);
		m_settingsMenu.addSeparator();
		m_settingsMenu.add(m_settingsAutoSaveSettingsMenuItem);
		m_settingsMenu.add(m_settingsSaveSettingsMenuItem);
		m_settingsMenu.add(m_settingsReloadSettingsMenuItem);
		m_settingsMenu.add(m_settingsResetSettingsMenuItem);
		
		m_pluginsMenu.add(m_pluginsListLoadedMenuItem);
		m_pluginsMenu.add(m_pluginsLoadMenuItem);
		m_pluginsMenu.add(m_pluginsLoadAllMenuItem);
		m_pluginsMenu.add(m_pluginsAutoLoadMenuItem);
		
		m_windowMenu.add(m_windowResetPositionMenuItem);
		m_windowMenu.add(m_windowResetSizeMenuItem);
		
		m_helpMenu.add(m_helpCheckVersionMenuItem);
		m_helpMenu.add(m_helpAboutMenuItem);
		
		m_menuBar.add(m_fileMenu);
		m_menuBar.add(m_selectMenu);
		m_menuBar.add(m_sortMenu);
		m_menuBar.add(m_settingsMenu);
		m_menuBar.add(m_pluginsMenu);
		m_menuBar.add(m_windowMenu);
		m_menuBar.add(m_helpMenu);
		
		m_frame.setJMenuBar(m_menuBar);
	}

	// initialize the gui components
	private void initComponents() {
		// initialize the main tabbed pane
		m_mainTabbedPane = new JTabbedPane();
		
		// initialize the console tab
		m_consoleText = new JTextArea();
		m_consoleFont = new Font("Verdana", Font.PLAIN, 14);
		m_consoleText.setFont(m_consoleFont);
		m_consoleText.setEditable(false);
		m_consoleText.setTransferHandler(m_transferHandler);
		m_consoleScrollPane = new JScrollPane(m_consoleText);
		m_mainTabbedPane.add(m_consoleScrollPane);
		
		m_mainTabbedPane.addTab("Console", null, m_consoleScrollPane, "Displays debugging information from the application.");
		
		m_mainTabbedPane.addChangeListener(this);
		
		m_frame.add(m_mainTabbedPane);
	}
	
	public JFrame getFrame() {
		return m_frame;
	}
	
	public TransferHandler getTransferHandler() {
		return m_transferHandler;
	}
	
	public void addGroup(GroupPanel groupPanel) {
		if(groupPanel == null) { return; }
		
		groupPanel.setTransferHandler(m_transferHandler);
		groupPanel.addUpdateListener(this);
		m_groupPanels.add(groupPanel);
		
		JScrollPane groupScrollPane = new JScrollPane(groupPanel);
		groupScrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
		int index = m_mainTabbedPane.getTabCount() - 1;
		m_mainTabbedPane.insertTab(groupPanel.getTabName(), null, groupScrollPane, groupPanel.getTabDescription(), index);
		
		m_mainTabbedPane.setSelectedIndex(index);
	}
	
	public boolean unsavedChanges() {
		for(int i=0;i<m_groupPanels.size();i++) {
			if(m_groupPanels.elementAt(i).isChanged()) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean selectGroupPanel(GroupPanel groupPanel) {
		if(groupPanel == null) { return false; }
		
		GroupPanel p = null;
		for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
			p = getGroupPanelFrom(m_mainTabbedPane.getComponent(i));
			if(p == null) { continue; }
			
			if(groupPanel == p) {
				m_mainTabbedPane.setSelectedComponent(m_mainTabbedPane.getComponent(i));
				return true;
			}
		}
		return false;
	}

	protected GroupPanel getSelectedGroupPanel() {
		Component selectedComponent = m_mainTabbedPane.getSelectedComponent();
		if(selectedComponent == null || !(selectedComponent instanceof JScrollPane)) { return null; }
		JScrollPane selectedScrollPane = (JScrollPane) selectedComponent;
		JViewport selectedViewport = selectedScrollPane.getViewport();
		if(selectedViewport == null || selectedViewport.getComponentCount() < 1) { return null; }
		Component selectedScrollPaneComponent = selectedViewport.getComponent(0);
		if(selectedScrollPaneComponent == null || !(selectedScrollPaneComponent instanceof GroupPanel)) { return null; }
		return (GroupPanel) selectedScrollPaneComponent;
	}
	
	protected Group getSelectedGroup() {
		GroupPanel selectedGroupPanel = getSelectedGroupPanel();
		if(selectedGroupPanel == null) { return null; }
		return selectedGroupPanel.getGroup();
	}
	
	protected GroupPanel getGroupPanelFrom(Component component) {
		if(component == null || !(component instanceof JScrollPane)) { return null; }
		JScrollPane scrollPane = (JScrollPane) component;
		JViewport viewport = scrollPane.getViewport();
		if(viewport == null || viewport.getComponentCount() < 1) { return null; }
		Component scrollPaneComponent = viewport.getComponent(0);
		if(scrollPaneComponent == null || !(scrollPaneComponent instanceof GroupPanel)) { return null; }
		return (GroupPanel) scrollPaneComponent;
	}
	
	protected Group getGroupFrom(Component component) {
		GroupPanel groupPanel = getGroupPanelFrom(component);
		if(groupPanel == null) { return null; }
		return groupPanel.getGroup();
	}
	
	protected Component getTabComponentWith(GroupPanel groupPanel) {
		if(groupPanel == null) { return null; }
		Component component = null;
		for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
			component = m_mainTabbedPane.getComponent(i);
			if(!(component instanceof JScrollPane)) { continue; }
			JScrollPane scrollPane = (JScrollPane) component;
			JViewport viewport = scrollPane.getViewport();
			if(viewport == null || viewport.getComponentCount() < 1) { continue; }
			Component scrollPaneComponent = viewport.getComponent(0);
			if(scrollPaneComponent == null || !(scrollPaneComponent instanceof GroupPanel)) { continue; }
			if((GroupPanel) scrollPaneComponent == groupPanel) {
				return component;
			}
		}
		return null;
	}
	
	protected Component getTabComponentWith(Group group) {
		if(group == null) { return null; }
		Component component = null;
		for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
			component = m_mainTabbedPane.getComponent(i);
			if(!(component instanceof JScrollPane)) { continue; }
			JScrollPane scrollPane = (JScrollPane) component;
			JViewport viewport = scrollPane.getViewport();
			if(viewport == null || viewport.getComponentCount() < 1) { continue; }
			Component scrollPaneComponent = viewport.getComponent(0);
			if(scrollPaneComponent == null || !(scrollPaneComponent instanceof GroupPanel)) { continue; }
			GroupPanel groupPanel = (GroupPanel) scrollPaneComponent;
			if(group.equals(groupPanel.getGroup())) {
				return component;
			}
		}
		return null;
	}
	
	protected int indexOfTabComponentWith(GroupPanel groupPanel) {
		if(groupPanel == null) { return -1; }
		Component component = null;
		for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
			component = m_mainTabbedPane.getComponent(i);
			if(!(component instanceof JScrollPane)) { continue; }
			JScrollPane scrollPane = (JScrollPane) component;
			JViewport viewport = scrollPane.getViewport();
			if(viewport == null || viewport.getComponentCount() < 1) { continue; }
			Component scrollPaneComponent = viewport.getComponent(0);
			if(scrollPaneComponent == null || !(scrollPaneComponent instanceof GroupPanel)) { continue; }
			if((GroupPanel) scrollPaneComponent == groupPanel) {
				m_mainTabbedPane.indexOfComponent(scrollPaneComponent);
			}
		}
		return -1;
	}

	protected int indexOfTabComponentWith(Group group) {
		if(group == null) { return -1; }
		Component component = null;
		for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
			component = m_mainTabbedPane.getComponent(i);
			if(!(component instanceof JScrollPane)) { continue; }
			JScrollPane scrollPane = (JScrollPane) component;
			JViewport viewport = scrollPane.getViewport();
			if(viewport == null || viewport.getComponentCount() < 1) { continue; }
			Component scrollPaneComponent = viewport.getComponent(0);
			if(scrollPaneComponent == null || !(scrollPaneComponent instanceof GroupPanel)) { continue; }
			GroupPanel groupPanel = (GroupPanel) scrollPaneComponent;
			if(group.equals(groupPanel.getGroup())) {
				m_mainTabbedPane.indexOfComponent(scrollPaneComponent);
			}
		}
		return -1;
	}
	
	public boolean promptNewGroup() {
		Vector<GroupPlugin> loadedInstantiablePlugins = GroupManager.pluginManager.getLoadedInstantiablePlugins();
		if(loadedInstantiablePlugins.size() == 0) {
			String message = "No group plugins found that support instantiation. Perhaps you forgot to load all plugins?";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Plugins", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		int pluginIndex = -1;
		Object choices[] = loadedInstantiablePlugins.toArray();
		Object value = JOptionPane.showInputDialog(m_frame, "Choose a group type to create:", "Choose New Group Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
		if(value == null) { return false; }
		for(int i=0;i<choices.length;i++) {
			if(choices[i] == value) {
				pluginIndex = i;
				break;
			}
		}
		if(pluginIndex < 0 || pluginIndex >= loadedInstantiablePlugins.size()) { return false; }
		
		Group newGroup = null;
		try {
			newGroup = loadedInstantiablePlugins.elementAt(pluginIndex).getGroupInstance(null);
		}
		catch(GroupInstantiationException e) {
			String message = "Failed to create instance of \"" + loadedInstantiablePlugins.elementAt(pluginIndex).getName() + "\"!";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		SystemConsole.instance.writeLine(loadedInstantiablePlugins.elementAt(pluginIndex).getName() + " group created successfully!");
		
		int fileTypeIndex = 0;
		if(newGroup.numberOfGroupFileTypes() > 1) {
			Object selectedFileType = JOptionPane.showInputDialog(m_frame, "Choose a group file type to create:", "Choose Group File Type", JOptionPane.QUESTION_MESSAGE, null, newGroup.getGroupFileTypes(), newGroup.getGroupFileTypes()[0]);
			if(selectedFileType == null) { return false; }
			for(int i=0;i<newGroup.getGroupFileTypes().length;i++) {
				if(newGroup.getGroupFileTypes()[i] == selectedFileType) {
					fileTypeIndex = i;
					break;
				}
			}
			if(fileTypeIndex < 0 || fileTypeIndex >= newGroup.numberOfGroupFileTypes()) { return false; }
			newGroup.setGroupFileType(newGroup.getGroupFileTypes()[fileTypeIndex]);
		}
		
		GroupPanel newGroupPanel = null;
		try { newGroupPanel = loadedInstantiablePlugins.elementAt(pluginIndex).getGroupPanelInstance(newGroup); }
		catch(GroupPanelInstantiationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Group Panel Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		if(newGroupPanel == null) {
			String message = "Failed to instantiate group panel for \"" + loadedInstantiablePlugins.elementAt(pluginIndex).getName() + " plugin.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(!newGroupPanel.init()) {
			String message = "Failed to initialize group panel for \"" + loadedInstantiablePlugins.elementAt(pluginIndex).getName() + "\" plugin.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Group Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		newGroupPanel.addGroupActionListener(this);
		
		addGroup(newGroupPanel);
		
		newGroupPanel.setGroupNumber(GroupManager.getGroupNumber());
		newGroupPanel.setChanged(true);
		
		return true;
	}
	
	public void promptLoadGroups() {
		if(GroupManager.pluginManager.numberOfLoadedPlugins() == 0) {
			String message = "No group plugins loaded. You must have at least one group plugin loaded to open a group file.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Group Plugins Loaded", JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		fileChooser.setDialogTitle("Load Group Files");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(true);
		if(fileChooser.showOpenDialog(m_frame) != JFileChooser.APPROVE_OPTION) { return; }
		
		loadGroups(fileChooser.getSelectedFiles());
	}

	public int loadGroups(File[] files) {
		if(files == null || files.length == 0) { return -1; }
		
		int numberOfGroupsLoaded = 0; 
		for(int i=0;i<files.length;i++) {
			if(files[i] == null) { continue; }
			
			if(loadGroup(files[i])) {
				numberOfGroupsLoaded++;
			}
		}
		
		if(files.length > 0) {
			int numberOfGroupsFailed = files.length - numberOfGroupsLoaded;
			if(numberOfGroupsLoaded == 0 && numberOfGroupsFailed > 0) {
				SystemConsole.instance.writeLine(numberOfGroupsFailed + " group file" + (numberOfGroupsFailed == 1 ? "" : "s") + " failed to load, no group files loaded.");
			}
			else if(numberOfGroupsLoaded > 1) {
				SystemConsole.instance.writeLine(numberOfGroupsLoaded + " group files were loaded successfully" + (numberOfGroupsFailed == 0 ? "" : ", while " + numberOfGroupsFailed + " failed to load") + "!");
			}
		}
		
		return numberOfGroupsLoaded;
	}
	
	public boolean loadGroup(File file) {
		if(file == null || !file.exists()) {
			SystemConsole.instance.writeLine("File \"" + file.getName() + "\" does not exist.");
			return false;
		}
		
		for(int i=0;i<m_groupPanels.size();i++) {
			if(m_groupPanels.elementAt(i).isSameFile(file)) {
				selectGroupPanel(m_groupPanels.elementAt(i));
				
				String message = "Group file \"" + (file == null ? "null" : file.getName()) +  "\" already loaded!";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Already Loaded", JOptionPane.INFORMATION_MESSAGE);
				
				return true;
			}
		}
		
		String extension = Utilities.getFileExtension(file.getName());
		
		GroupPlugin plugin = GroupManager.pluginManager.getPluginForFileType(extension);
		if(plugin == null) {
			String message = "No plugin found to load " + extension + " file type. Perhaps you forgot to load all plugins?";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Plugin Found", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		Group group = null;
		try { group = plugin.getGroupInstance(file); }
		catch(GroupInstantiationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		if(group == null) {
			String message = "Failed to instantiate \"" + plugin.getName() + " (" + plugin.getSupportedGroupFileTypesAsString() + ")\" plugin when attempting to read group file: \"" + file.getName() + "\".";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		try {
			if(!group.load()) {
				String message = "Failed to load group: \"" + file.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedGroupFileTypesAsString() + ")\".";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Group Loading Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		catch(HeadlessException e) {
			String message = "Exception thrown while loading group: \"" + file.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedGroupFileTypesAsString() + "): " + e.getMessage();
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Group Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		catch(GroupReadException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Group Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		SystemConsole.instance.writeLine("Group file \"" + file.getName() +  "\" loaded successfully!");
		
		GroupPanel groupPanel = null;
		try { groupPanel = plugin.getGroupPanelInstance(group); }
		catch(GroupPanelInstantiationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Group Panel Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		if(groupPanel == null) {
			String message = "Failed to instantiate group panel for \"" + plugin.getName() + " plugin.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(!groupPanel.init()) {
			String message = "Failed to initialize group panel for \"" + plugin.getName() + "\" plugin.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Group Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		groupPanel.setGroupNumber(GroupManager.getGroupNumber());
		groupPanel.addGroupActionListener(this);
		addGroup(groupPanel);
		
		return true;
	}
	
	public boolean saveSelectedGroup() {
		return saveGroup(getSelectedGroupPanel(), false);
	}
	
	public boolean saveGroup(GroupPanel groupPanel) {
		return saveGroup(groupPanel, false);
	}
	
	public boolean saveGroup(GroupPanel groupPanel, boolean copy) {
		if(groupPanel == null) { return false; }
		
		if(!groupPanel.isChanged() && !copy) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "No changes detected, save group anyways?", "No Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.NO_OPTION) { return false; }
		}
		
		File groupFile = groupPanel.getGroup().getFile();
		
		if(groupFile == null) {
			return saveGroupAsNew(groupPanel);
		}
		
		try {
			if(groupPanel.save()) {
				SystemConsole.instance.writeLine("Group successfully updated and saved to file: " + groupFile.getName() + "!");
				
				update();
				
				return true;
			}
			else {
				String message = "Failed to update and save group!";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Save Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		catch(GroupWriteException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			return false;
		}
	}
	
	public boolean saveSelectedGroupAsNew() {
		return saveGroupAsNew(getSelectedGroupPanel());
	}
	
	public boolean saveGroupAsNew(GroupPanel groupPanel) {
		if(groupPanel == null) { return false; }
		
		File groupFile = groupPanel.getGroup().getFile();
		
		JFileChooser fileChooser = new JFileChooser(groupFile == null ? System.getProperty("user.dir") : Utilities.getFilePath(groupFile));
		fileChooser.setDialogTitle("Save Group File As");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if(groupFile != null) {
			String fileName = groupFile.getName();
			String extension = Utilities.getFileExtension(fileName);
			fileChooser.setSelectedFile(new File(Utilities.getFileNameNoExtension(fileName) + (Utilities.compareCasePercentage(fileName) < 0 ? "_copy" : "_COPY") + (extension == null ? "" : "." + extension)));
		}
		else {
			String extension = groupPanel.getFileExtension();
			fileChooser.setSelectedFile(new File("NEW" + (extension == null ? "" : "." + extension)));
		}
		
		while(true) {
			if(fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) { return false; }
			
			if(fileChooser.getSelectedFile().exists()) {
				int choice = JOptionPane.showConfirmDialog(m_frame, "The specified file already exists, are you sure you want to overwrite it?", "Overwrite File", JOptionPane.YES_NO_CANCEL_OPTION);
				if(choice == JOptionPane.CANCEL_OPTION) { return false; }
				else if(choice == JOptionPane.NO_OPTION) { continue; }
				
				break;
			}
			else {
				break;
			}
		}
		
		groupPanel.getGroup().setFile(fileChooser.getSelectedFile());
		
		return saveGroup(groupPanel, true);
	}
	
	public void saveAllGroups() {
		if(m_groupPanels.size() == 0) { return; }
		
		for(int i=0;i<m_groupPanels.size();i++) {
			saveGroup(m_groupPanels.elementAt(i));
		}
		
		update();
	}
	
	public int addFilesToSelectedGroup() {
		return addFilesToGroup(getSelectedGroupPanel());
	}
	
	public int addFilesToGroup(GroupPanel groupPanel) {
		if(groupPanel == null) { return 0; }
		
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		fileChooser.setDialogTitle("Select Files to Add");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(true);
		if(fileChooser.showOpenDialog(m_frame) != JFileChooser.APPROVE_OPTION || fileChooser.getSelectedFiles().length == 0) { return 0; }
		
		int numberOfFilesAdded = 0;
		String formattedFileName = null;
		DuplicateFileAction duplicateFileAction = DuplicateFileAction.Invalid;
		File[] selectedFiles = fileChooser.getSelectedFiles();
		
		for(int i=0;i<selectedFiles.length;i++) {
			formattedFileName = Utilities.truncateFileName(selectedFiles[i].getName(), GroupFile.MAX_FILE_NAME_LENGTH);
			
			if(duplicateFileAction != DuplicateFileAction.SkipAll && duplicateFileAction != DuplicateFileAction.ReplaceAll) {
				Object selection = JOptionPane.showInputDialog(m_frame, "File \"" + formattedFileName + "\" already exists, please choose an action:", "Duplicate File", JOptionPane.QUESTION_MESSAGE, null, DuplicateFileAction.getValidDisplayNames(), DuplicateFileAction.displayNames[DuplicateFileAction.defaultAction.ordinal()]);
				if(selection == null) { break; }
				
				duplicateFileAction = DuplicateFileAction.parseFrom(selection.toString());
			}
			
			if(groupPanel.getGroup().addFile(selectedFiles[i], duplicateFileAction == DuplicateFileAction.Replace || duplicateFileAction == DuplicateFileAction.ReplaceAll)) {
				numberOfFilesAdded++;
			}
			else {
				SystemConsole.instance.writeLine("Failed to add file " + formattedFileName + " to group" + (groupPanel.getGroup().getFile() == null ? "." : " \"" + groupPanel.getGroup().getFile().getName() + "\"."));
			}
		}
		
		if(numberOfFilesAdded == 0) {
			String message = "Failed to any files to directory: \"" + fileChooser.getSelectedFile().getName() + "\".";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Failed to Add Files", JOptionPane.ERROR_MESSAGE);
		}
		else if(numberOfFilesAdded != selectedFiles.length) {
			String message = "Only successfully added " + numberOfFilesAdded + " of " + selectedFiles.length + " selected files to group" + (groupPanel.getGroup().getFile() == null ? "." : " \"" + groupPanel.getGroup().getFile().getName() + "\".");
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Some Files Added", JOptionPane.WARNING_MESSAGE);
		}
		else {
			String message = "Successfully added all " + selectedFiles.length + " selected files to group" + (groupPanel.getGroup().getFile() == null ? "." : " \"" + groupPanel.getGroup().getFile().getName() + "\".");
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "All Files Added", JOptionPane.INFORMATION_MESSAGE);
		}
		
		return numberOfFilesAdded;
	}
	
	public int removeSelectedFilesFromSelectedGroup() {
		return removeSelectedFilesFromGroup(getSelectedGroupPanel());
	}
	
	public int removeSelectedFilesFromGroup(GroupPanel groupPanel) {
		if(groupPanel == null) { return 0; }
		
		Vector<GroupFile> selectedGroupFiles = groupPanel.getSelectedFiles();
		if(selectedGroupFiles.size() == 0) { return 0; }
		
		int choice = JOptionPane.showConfirmDialog(m_frame, "Are you sure you wish to remove the " + selectedGroupFiles.size() + " selected files" + (groupPanel.getGroup().getFile() == null ? "?" : " from group \"" + groupPanel.getGroup().getFile().getName() + "\"?"), "Remove Files?", JOptionPane.YES_NO_CANCEL_OPTION);
		if(choice == JOptionPane.NO_OPTION || choice == JOptionPane.CANCEL_OPTION) { return 0; }
		
		int numberOfFilesRemoved = groupPanel.getGroup().removeFiles(selectedGroupFiles);
		
		if(numberOfFilesRemoved == 0) {
			String message = "Failed to remove any files from group" + (groupPanel.getGroup().getFile() == null ? "." : " \"" + groupPanel.getGroup().getFile().getName() + "\".");
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Failed to Remove Files", JOptionPane.ERROR_MESSAGE);
		}
		else if(numberOfFilesRemoved != selectedGroupFiles.size()) {
			String message = "Only successfully removed " + numberOfFilesRemoved + " of " + selectedGroupFiles.size() + " files from group" + (groupPanel.getGroup().getFile() == null ? "." : " \"" + groupPanel.getGroup().getFile().getName() + "\".");
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Some Files Removed", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			String message = "Successfully removed " + numberOfFilesRemoved + " files from group" + (groupPanel.getGroup().getFile() == null ? "." : " \"" + groupPanel.getGroup().getFile().getName() + "\".");
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "All Selected Files Removed", JOptionPane.INFORMATION_MESSAGE);
		}
		
		return numberOfFilesRemoved;
	}
	
	public boolean replaceSelectedFileInSelectedGroup() {
		return replaceSelectedFileInGroup(getSelectedGroupPanel());
	}
	
	public boolean replaceSelectedFileInGroup(GroupPanel groupPanel) {
		if(groupPanel == null) { return false; }
		
		Vector<GroupFile> selectedGroupFiles = groupPanel.getSelectedFiles();
		if(selectedGroupFiles.size() != 1) { return false; }
		
		GroupFile selectedGroupFile = selectedGroupFiles.elementAt(0);
		
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		fileChooser.setDialogTitle("Select Replacement File");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if(fileChooser.showOpenDialog(m_frame) != JFileChooser.APPROVE_OPTION) { return false; }
		if(fileChooser.getSelectedFile() == null || !fileChooser.getSelectedFile().isFile() || !fileChooser.getSelectedFile().exists()) { return false; }
		
		boolean fileReplaced = groupPanel.getGroup().replaceFile(selectedGroupFile, fileChooser.getSelectedFile());
		
		if(fileReplaced) {
			String message = "Successfully replaced selected file \"" + selectedGroupFile.getFileName() + "\" in group" + (groupPanel.getGroup().getFile() == null ? "." : " \"" + groupPanel.getGroup().getFile().getName() + "\".");
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "File Replaced", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			String message = "Failed to replace selected file \"" + selectedGroupFile.getFileName() + "\" in group" + (groupPanel.getGroup().getFile() == null ? "." : " \"" + groupPanel.getGroup().getFile().getName() + "\".");
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "File Replacement Failed", JOptionPane.ERROR_MESSAGE);
		}
		
		return fileReplaced;
	}

	public int extractSelectedFilesFromSelectedGroup() {
		return extractSelectedFilesFromGroup(getSelectedGroupPanel());
	}
	
	public int extractSelectedFilesFromGroup(GroupPanel groupPanel) {
		if(groupPanel == null) { return 0; }

		Vector<GroupFile> selectedGroupFiles = groupPanel.getSelectedFiles();
		if(selectedGroupFiles.size() == 0) { return 0; }
		
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		fileChooser.setDialogTitle("Extract Files");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if(fileChooser.showOpenDialog(m_frame) != JFileChooser.APPROVE_OPTION) { return 0; }
		if(fileChooser.getSelectedFile() == null || !fileChooser.getSelectedFile().isDirectory()) {
			String message = "Selected directory \"" + fileChooser.getSelectedFile().getName() + "\" is not a directory.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Invalid Directory", JOptionPane.ERROR_MESSAGE);
			
			return 0;
		}
		
		if(!fileChooser.getSelectedFile().exists()) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "The specified directory does not exist, create it?", "Non-Existant Directory", JOptionPane.YES_NO_CANCEL_OPTION);
			if(choice == JOptionPane.NO_OPTION || choice == JOptionPane.CANCEL_OPTION) { return 0; }
			
			try {
				fileChooser.getSelectedFile().mkdirs();
			}
			catch(SecurityException e) {
				String message = "Failed to create the specified directory or directory structure, please ensure that you have write permission for this location. Exception message: " + e.getMessage();
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Directory Creation Failed", JOptionPane.ERROR_MESSAGE);
				
				return 0;
			}
		}
		
		int numberOfFilesExtracted = 0;
		
		for(int i=0;i<selectedGroupFiles.size();i++) {
			try {
				if(selectedGroupFiles.elementAt(i).writeTo(fileChooser.getSelectedFile())) {
					numberOfFilesExtracted++;
				}
				else {
					SystemConsole.instance.writeLine("Failed to extract file \"" + selectedGroupFiles.elementAt(i).getFileName() + "\" to directory: \"" + fileChooser.getSelectedFile().getName() + "\".");
				}
			}
			catch(IOException e) {
				SystemConsole.instance.writeLine("Exception thrown while extracting file \"" + selectedGroupFiles.elementAt(i).getFileName() + "\" to directory \"" + fileChooser.getSelectedFile().getName() + "\": " + e.getMessage());
			}
		}
		
		if(numberOfFilesExtracted == 0) {
			String message = "Failed to any files to directory: \"" + fileChooser.getSelectedFile().getName() + "\".";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Failed to Extract Files", JOptionPane.ERROR_MESSAGE);
		}
		else if(numberOfFilesExtracted != selectedGroupFiles.size()) {
			String message = "Only successfully extracted " + numberOfFilesExtracted + " of " + selectedGroupFiles.size() + " selected files to directory: \"" + fileChooser.getSelectedFile().getName() + "\".";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Some Files Extracted", JOptionPane.WARNING_MESSAGE);
		}
		else {
			String message = "Successfully extracted all " + selectedGroupFiles.size() + " selected files to directory: \"" + fileChooser.getSelectedFile().getName() + "\".";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "All Selected Files Extracted", JOptionPane.INFORMATION_MESSAGE);
		}
		
		return numberOfFilesExtracted;
	}
	
	public boolean importGroupIntoSelectedGroup() {
		return importGroupInto(getSelectedGroupPanel());
	}
	
	public boolean importGroupInto(GroupPanel groupPanel) {
		if(groupPanel == null) { return false; }
		
		Group group = groupPanel.getGroup();
		if(group == null) { return false; }
		
		JFileChooser fileChooser = new JFileChooser(group.getFile() == null ? System.getProperty("user.dir") : Utilities.getFilePath(group.getFile()));
		fileChooser.setDialogTitle("Import Group File");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if(fileChooser.showOpenDialog(m_frame) != JFileChooser.APPROVE_OPTION) { return false; }
		if(!fileChooser.getSelectedFile().isFile() || !fileChooser.getSelectedFile().exists()) {
			String message = "Selected group file \"" + fileChooser.getSelectedFile().getName() + "\" is not a file or does not exist.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Invalid or Missing File", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		File selectedFile = fileChooser.getSelectedFile();
		String extension = Utilities.getFileExtension(selectedFile.getName());
		
		GroupPlugin plugin = GroupManager.pluginManager.getPluginForFileType(extension);
		if(plugin == null) {
			String message = "No plugin found to import " + extension + " file type. Perhaps you forgot to load all plugins?";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Plugin Found", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		Group importedGroup = null;
		try { importedGroup = plugin.getGroupInstance(selectedFile); }
		catch(GroupInstantiationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		if(importedGroup == null) {
			String message = "Failed to instantiate \"" + plugin.getName() + " (" + plugin.getSupportedGroupFileTypesAsString() + ")\" plugin when attempting to import group file: \"" + selectedFile.getName() + "\".";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		try {
			if(!importedGroup.load()) {
				String message = "Failed to import group: \"" + selectedFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedGroupFileTypesAsString() + ")\".";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Group Loading Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		catch(HeadlessException e) {
			String message = "Exception thrown while importing group : \"" + selectedFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedGroupFileTypesAsString() + "): " + e.getMessage();
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Group Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		catch(GroupReadException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Group Importing Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(!importedGroup.verifyAllFiles()) {
			String message = "Found one or more invalid files when importing group: \"" + selectedFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedGroupFileTypesAsString() + ").";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Group Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		group.addFiles(importedGroup);
		
		SystemConsole.instance.writeLine("Group file \"" + selectedFile.getName() +  "\" imported successfully!");
		
		update();
		
		return true;
	}
	
	public boolean exportSelectedGroup() {
		return exportGroup(getSelectedGroupPanel());
	}
	
	public boolean exportGroup(GroupPanel groupPanel) {
		if(groupPanel == null) { return false; }
		
		Group group = groupPanel.getGroup();
		
		Vector<GroupPlugin> loadedInstantiablePlugins = GroupManager.pluginManager.getLoadedInstantiablePluginsExcluding(group.getFileExtension());
		if(loadedInstantiablePlugins.size() == 0) {
			String message = "No group plugins found that support instantiation / exporting. Perhaps you forgot to load all plugins?";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Plugins", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		int pluginIndex = -1;
		Object choices[] = loadedInstantiablePlugins.toArray();
		Object value = JOptionPane.showInputDialog(m_frame, "Choose a group type to export to:", "Choose Group Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
		if(value == null) { return false; }
		for(int i=0;i<choices.length;i++) {
			if(choices[i] == value) {
				pluginIndex = i;
				break;
			}
		}
		if(pluginIndex < 0 || pluginIndex >= loadedInstantiablePlugins.size()) { return false; }
		
		Group newGroup = null;
		try {
			newGroup = loadedInstantiablePlugins.elementAt(pluginIndex).getGroupInstance(null);
		}
		catch(GroupInstantiationException e) {
			String message = "Failed to create instance of export file: \"" + loadedInstantiablePlugins.elementAt(pluginIndex).getName() + " (" + loadedInstantiablePlugins.elementAt(pluginIndex).getSupportedGroupFileTypesAsString() + ")!";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		int fileTypeIndex = 0;
		if(newGroup.numberOfGroupFileTypes() > 1) {
			Object selectedFileType = JOptionPane.showInputDialog(m_frame, "Choose a group file type to export to:", "Choose Group File Type", JOptionPane.QUESTION_MESSAGE, null, newGroup.getGroupFileTypes(), newGroup.getGroupFileTypes()[0]);
			if(selectedFileType == null) { return false; }
			for(int i=0;i<newGroup.getGroupFileTypes().length;i++) {
				if(newGroup.getGroupFileTypes()[i] == selectedFileType) {
					fileTypeIndex = i;
					break;
				}
			}
			if(fileTypeIndex < 0 || fileTypeIndex >= newGroup.numberOfGroupFileTypes()) { return false; }
			newGroup.setGroupFileType(newGroup.getGroupFileTypes()[fileTypeIndex]);
		}
		
		JFileChooser fileChooser = new JFileChooser(group.getFile() == null ? System.getProperty("user.dir") : Utilities.getFilePath(group.getFile()));
		fileChooser.setDialogTitle("Export Group File");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		String extension = loadedInstantiablePlugins.elementAt(pluginIndex).getSupportedGroupFileType(fileTypeIndex);
		if(group.getFile() != null) {
			String fileName = group.getFile().getName();
			fileChooser.setSelectedFile(new File(Utilities.getFileNameNoExtension(fileName) + (Utilities.compareCasePercentage(fileName) < 0 ? "_copy" : "_COPY") + "." + (Utilities.compareCasePercentage(fileName) < 0 ? extension.toLowerCase() : extension.toUpperCase())));
		}
		else {
			fileChooser.setSelectedFile(new File("NEW." + extension));
		}
		
		while(true) {
			if(fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) { return false; }
			
			if(fileChooser.getSelectedFile().exists()) {
				int choice = JOptionPane.showConfirmDialog(m_frame, "The specified file already exists, are you sure you want to overwrite it?", "Overwrite File", JOptionPane.YES_NO_CANCEL_OPTION);
				if(choice == JOptionPane.CANCEL_OPTION) { return false; }
				else if(choice == JOptionPane.NO_OPTION) { continue; }
				
				break;
			}
			else {
				break;
			}
		}
		
		newGroup.setFile(fileChooser.getSelectedFile());
		newGroup.addFiles(group);
		
		try {
			if(newGroup.save()) {
				SystemConsole.instance.writeLine("Group successfully exported to new file: " + newGroup.getFile().getName() + "!");
			}
			else {
				String message = "Failed to export group!";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Export Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		catch(GroupWriteException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			return false;
		}
		
		return true;
	}
	
	public boolean closeSelectedGroup() {
		return closeGroup(getSelectedGroupPanel());
	}
	
	public boolean closeGroup(GroupPanel groupPanel) {
		if(groupPanel == null) { return false; }
		
		Component tabComponent = getTabComponentWith(groupPanel);
		if(tabComponent == null) { return false; }
		m_mainTabbedPane.setSelectedComponent(tabComponent);
		
		if(groupPanel.isChanged()) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "Would you like to save your changes?", "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
			if(choice == JOptionPane.CANCEL_OPTION) { return false; }
			if(choice == JOptionPane.YES_OPTION) {
				if(!saveSelectedGroup()) {
					return false;
				}
			}
		}
		
		
		m_mainTabbedPane.remove(tabComponent);
		int indexOfGroup = m_groupPanels.indexOf(groupPanel);
		m_groupPanels.remove(groupPanel);
		if(m_groupPanels.size() > 0) {
			m_mainTabbedPane.setSelectedComponent(getTabComponentWith(m_groupPanels.elementAt(indexOfGroup < m_groupPanels.size() ? indexOfGroup : indexOfGroup - 1)));
		}
		
		update();
		
		return true;
	}
	
	public boolean closeAllGroups() {
		if(m_mainTabbedPane.getComponentCount() > 1) {
			m_mainTabbedPane.setSelectedComponent(m_mainTabbedPane.getComponent(m_mainTabbedPane.getComponentCount() - 2));
		}
		
		for(int i=m_groupPanels.size()-1;i>=0;i--) {
			if(!closeGroup(m_groupPanels.elementAt(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	public void processGroupDirectory(final File directory, final GroupProcessor processor) {
		processGroupDirectory(directory, processor, GroupProcessor.DEFAULT_RECURSIVE);
	}
	
	public void processGroupDirectory(final File directory, final GroupProcessor processor, boolean recursive) {
		if(directory == null || !directory.exists() || !directory.isDirectory() || processor == null) { return; }
		
		int numberOfGroupsInDirectory = processor.numberOfGroupsInDirectory(directory, recursive);
		
		if(numberOfGroupsInDirectory > 0) {
			final Task task = new Task(numberOfGroupsInDirectory, m_progressDialog);
			
			Thread groupProcessorThread = new Thread(new Runnable() {
				public void run() {
					processor.processGroups(directory, task);
				}
			});
			
			processGroupsHelper(task, groupProcessorThread);
		}
		else {
			SystemConsole.instance.writeLine("No groups files to process.");
		}
	}
	
	public void processGroups(final GroupCollection groups, final GroupProcessor processor) {
		if(groups == null || processor == null) { return; }
		
		processGroups(groups.getGroups(), processor);
	}
	
	public void processOpenGroups(final GroupProcessor processor) {
		if(processor == null) { return; }
		
		final Vector<Group> openGroups = new Vector<Group>(m_groupPanels.size());
		for(int i=0;i<m_groupPanels.size();i++) {
			openGroups.add(getGroupFrom(m_groupPanels.elementAt(i)));
		}
		
		processGroups(openGroups, processor);
	}
	
	public void processGroups(final Vector<Group> groups, final GroupProcessor processor) {
		if(groups == null || processor == null) { return; }
		
		if(groups.size() > 0) {
			final Task task = new Task(groups.size(), m_progressDialog);
			
			Thread groupProcessorThread = new Thread(new Runnable() {
				public void run() {
					processor.processGroups(groups, task);
				}
			});
			
			processGroupsHelper(task, groupProcessorThread);
		}
		else {
			SystemConsole.instance.writeLine("No groups to process.");
		}
	}
	
	private void processGroupsHelper(Task task, Thread groupProcessorThread) {
		if(task == null || task.isCancelled() || task.isCompleted() || groupProcessorThread == null || groupProcessorThread.isAlive()) { return; }
		
		groupProcessorThread.start();
		
		m_progressDialog.display("Processing", "Processing groups...", 0, task.getTaskLength());
		
		if(m_progressDialog.userCancelled() || !task.isCompleted()) {
			task.cancel();
			
			groupProcessorThread.interrupt();
			try { groupProcessorThread.join(); } catch(InterruptedException e) { }
			
			m_progressDialog.clear();
		}
		
		int numberOfGroupsProcessed = task.getProgress();
		
		if(numberOfGroupsProcessed == 0) {
			SystemConsole.instance.writeLine("No groups were processed.");
		}
		else {
			SystemConsole.instance.writeLine("Successfully processed " + numberOfGroupsProcessed + " group" + (numberOfGroupsProcessed == 1 ? "" : "s") + ".");
			
			updateAll();
		}
	}
	
	public void updateAll() {
		if(m_updating) { return; }
		
		updateWindow();
		
		m_updating = true;
		
		for(int i=0;i<m_groupPanels.size();i++) {
			m_groupPanels.elementAt(i).autoSort();
			m_groupPanels.elementAt(i).updateWindow();
		}
		
		m_updating = false;
	}
	
	public void updateWindow() {
		if(m_updating) { return; }
		
		m_updating = true;
		
		m_settingsAutoScrollConsoleMenuItem.setSelected(SettingsManager.instance.autoScrollConsole);
		m_settingsLogConsoleMenuItem.setSelected(SettingsManager.instance.logConsole);
		m_settingsSupressUpdatesMenuItem.setSelected(SettingsManager.instance.supressUpdates);
		m_pluginsAutoLoadMenuItem.setSelected(SettingsManager.instance.autoLoadPlugins);
		m_settingsAutoSaveSettingsMenuItem.setSelected(SettingsManager.instance.autoSaveSettings);
		
		GroupPanel selectedGroupPanel = getSelectedGroupPanel();
		Group selectedGroup = selectedGroupPanel != null ? selectedGroupPanel.getGroup() : null;
		boolean groupTabSelected = m_mainTabbedPane.getSelectedIndex() != m_mainTabbedPane.getTabCount() - 1;
		m_fileSaveMenuItem.setEnabled(groupTabSelected);
		m_fileSaveAsMenuItem.setEnabled(groupTabSelected);
		m_fileSaveAllMenuItem.setEnabled(m_groupPanels.size() > 0);
		m_fileAddFilesMenuItem.setEnabled(groupTabSelected);
		
		m_fileRemoveFilesMenuItem.setText("Remove File" + (selectedGroupPanel != null && selectedGroupPanel.numberOfSelectedFiles() == 1 ? "" : "s"));
		m_fileExtractFilesMenuItem.setText("Extract File" + (selectedGroupPanel != null && selectedGroupPanel.numberOfSelectedFiles() == 1 ? "" : "s"));
		
		m_fileRemoveFilesMenuItem.setEnabled(selectedGroupPanel != null && selectedGroupPanel.numberOfSelectedFiles() > 0);
		m_fileReplaceFileMenuItem.setEnabled(selectedGroupPanel != null && selectedGroupPanel.numberOfSelectedFiles() == 1);
		m_fileExtractFilesMenuItem.setEnabled(selectedGroupPanel != null && selectedGroupPanel.numberOfSelectedFiles() > 0);
		
		m_fileRemoveFilesMenuItem.setEnabled(groupTabSelected);
		m_fileReplaceFileMenuItem.setEnabled(groupTabSelected);
		m_fileExtractFilesMenuItem.setEnabled(groupTabSelected);
		
		m_fileImportMenuItem.setEnabled(groupTabSelected);
		m_fileExportMenuItem.setEnabled(groupTabSelected);
		m_fileCloseMenuItem.setEnabled(groupTabSelected);
		m_fileCloseAllMenuItem.setEnabled(m_groupPanels.size() > 0);
		
		boolean groupHasFiles = selectedGroup != null && selectedGroup.numberOfFiles() > 0;
		m_selectInverseMenuItem.setEnabled(groupHasFiles);
		m_selectRandomMenuItem.setEnabled(groupHasFiles);
		m_selectAllMenuItem.setEnabled(groupHasFiles);
		m_selectNoneMenuItem.setEnabled(groupHasFiles);
		
		m_sortAllGroupsMenuItem.setSelected(SettingsManager.instance.sortAllGroups);
		m_sortPerGroupSortingMenuItem.setSelected(!SettingsManager.instance.sortAllGroups);
		if(SettingsManager.instance.sortAllGroups) {
			if(SettingsManager.instance.sortDirection.isValid()) {
				m_sortDirectionMenuItems[SettingsManager.instance.sortDirection.ordinal()].setSelected(true);
			}
			if(SettingsManager.instance.sortType.isValid()) {
				m_sortTypeMenuItems[SettingsManager.instance.sortType.ordinal()].setSelected(true);
			}
			m_sortAutoSortMenuItem.setSelected(SettingsManager.instance.autoSortFiles);
		}
		else {
			if(selectedGroup != null) {
				if(selectedGroup.getSortDirection().isValid()) {
					m_sortDirectionMenuItems[selectedGroup.getSortDirection().ordinal()].setSelected(true);
				}
				if(selectedGroup.getSortType().isValid()) {
					m_sortTypeMenuItems[selectedGroup.getSortType().ordinal()].setSelected(true);
				}
				m_sortAutoSortMenuItem.setSelected(selectedGroup.getAutoSortFiles());
			}
		}
		m_sortManualSortMenuItem.setEnabled(selectedGroup == null ? false : selectedGroup.shouldSortFiles());
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GroupPanel groupPanel = null;
				for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
					groupPanel = getGroupPanelFrom(m_mainTabbedPane.getComponentAt(i));
					if(groupPanel == null) { continue; }
					
					m_mainTabbedPane.setTitleAt(i, groupPanel.getTabName());
					m_mainTabbedPane.setToolTipTextAt(i, groupPanel.getTabDescription());
				}
			}
		});
		
		updateLayout();
		
		m_updating = false;
		
		m_mainTabbedPane.revalidate();
	}
	
	public void updateLayout() {
		for(int i=0;i<m_groupPanels.size();i++) {
			m_groupPanels.elementAt(i).updateLayout();
		}
	}
	
	// update the server window
	public void update() {
		if(!m_initialized || m_updating) { return; }
		
		// update and automatically scroll to the end of the text
		m_consoleText.setText(GroupManager.console.toString());
		
		if(SettingsManager.instance.autoScrollConsole) {
			JScrollBar hScrollBar = m_consoleScrollPane.getHorizontalScrollBar();
			JScrollBar vScrollBar = m_consoleScrollPane.getVerticalScrollBar();
			
			if(!hScrollBar.getValueIsAdjusting() && !vScrollBar.getValueIsAdjusting()) {
				hScrollBar.setValue(hScrollBar.getMinimum());
				vScrollBar.setValue(vScrollBar.getMaximum());
			}
		}
		
		m_updating = true;
		
		for(int i=0;i<m_groupPanels.size();i++) {
			m_groupPanels.elementAt(i).update();
		}
		
		m_updating = false;
		
		updateWindow();
	}
	
	public void resetWindowPosition() {
		SettingsManager.instance.windowPositionX = SettingsManager.defaultWindowPositionX;
		SettingsManager.instance.windowPositionY = SettingsManager.defaultWindowPositionY;
		
		m_frame.setLocation(SettingsManager.instance.windowPositionX, SettingsManager.instance.windowPositionY);
	}
	
	public void resetWindowSize() {
		SettingsManager.instance.windowWidth = SettingsManager.defaultWindowWidth;
		SettingsManager.instance.windowHeight = SettingsManager.defaultWindowHeight;
		
		m_frame.setSize(SettingsManager.instance.windowWidth, SettingsManager.instance.windowHeight);
	}
	
	public void windowActivated(WindowEvent e) { }
	public void windowClosed(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }
	
	public void windowClosing(WindowEvent e) {
		if(e.getSource() == m_frame) {
			close();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(m_updating) { return; }
		
		// create new group
		if(e.getSource() == m_fileNewMenuItem) {
			promptNewGroup();
		}
		// load group
		else if(e.getSource() == m_fileOpenMenuItem) {
			promptLoadGroups();
		}
		// save selected group
		else if(e.getSource() == m_fileSaveMenuItem) {
			saveSelectedGroup();
		}
		// save selected group as new
		else if(e.getSource() == m_fileSaveAsMenuItem) {
			saveSelectedGroupAsNew();
		}
		// save all groups
		else if(e.getSource() == m_fileSaveAllMenuItem) {
			saveAllGroups();
		}
		// add files to selected group
		else if(e.getSource() == m_fileAddFilesMenuItem) {
			addFilesToSelectedGroup();
		}
		// remove files from selected group
		else if(e.getSource() == m_fileRemoveFilesMenuItem) {
			removeSelectedFilesFromSelectedGroup();
		}
		// replace file in selected group
		else if(e.getSource() == m_fileReplaceFileMenuItem) {
			replaceSelectedFileInSelectedGroup();
		}
		// extract files from selected group
		else if(e.getSource() == m_fileExtractFilesMenuItem) {
			extractSelectedFilesFromSelectedGroup();
		}
		// import group
		else if(e.getSource() == m_fileImportMenuItem) {
			importGroupIntoSelectedGroup();
		}
		// export group
		else if(e.getSource() == m_fileExportMenuItem) {
			exportSelectedGroup();
		}
		// close current group
		else if(e.getSource() == m_fileCloseMenuItem) {
			closeSelectedGroup();
		}
		// close all group
		else if(e.getSource() == m_fileCloseAllMenuItem) {
			closeAllGroups();
		}
		// close the program
		else if(e.getSource() == m_fileExitMenuItem) {
			close();
		}
		// inverse file selection
		else if(e.getSource() == m_selectInverseMenuItem) {
			GroupPanel selectedGroupPanel = getSelectedGroupPanel();
			if(selectedGroupPanel == null || selectedGroupPanel.getGroup().numberOfFiles() == 0) { return; }
			
			selectedGroupPanel.selectInverse();
		}
		// randomize file selection
		else if(e.getSource() == m_selectRandomMenuItem) {
			GroupPanel selectedGroupPanel = getSelectedGroupPanel();
			if(selectedGroupPanel == null || selectedGroupPanel.getGroup().numberOfFiles() == 0) { return; }
			
			selectedGroupPanel.selectRandom();
		}
		// select all files
		else if(e.getSource() == m_selectAllMenuItem) {
			GroupPanel selectedGroupPanel = getSelectedGroupPanel();
			if(selectedGroupPanel == null || selectedGroupPanel.getGroup().numberOfFiles() == 0) { return; }
			
			selectedGroupPanel.selectAll();
		}
		// clear file selection
		else if(e.getSource() == m_selectNoneMenuItem) {
			GroupPanel selectedGroupPanel = getSelectedGroupPanel();
			if(selectedGroupPanel == null || selectedGroupPanel.getGroup().numberOfFiles() == 0) { return; }
			
			selectedGroupPanel.clearSelection();
		}
		// enable sorting for all groups
		else if(e.getSource() == m_sortAllGroupsMenuItem) {
			if(SettingsManager.instance.sortAllGroups) { return; }
			
			SettingsManager.instance.sortAllGroups = true;
			
			updateAll();
		}
		// enable per-group sorting
		else if(e.getSource() == m_sortPerGroupSortingMenuItem) {
			if(!SettingsManager.instance.sortAllGroups) { return; }
			
			SettingsManager.instance.sortAllGroups = false;
			
			updateAll();
		}
		// manual sorting
		else if(e.getSource() == m_sortManualSortMenuItem) {
			Group selectedGroup = getSelectedGroup();
			if(selectedGroup == null) { return; }
			
			selectedGroup.sortFiles();
		}
		// toggle file auto-sorting
		else if(e.getSource() == m_sortAutoSortMenuItem) {
			if(SettingsManager.instance.sortAllGroups) {
				SettingsManager.instance.autoSortFiles = m_sortAutoSortMenuItem.isSelected();
				
				updateAll();
			}
			else {
				GroupPanel selectedGroupPanel = getSelectedGroupPanel();
				if(selectedGroupPanel == null) { return; }
				
				selectedGroupPanel.getGroup().setAutoSortFiles(m_sortAutoSortMenuItem.isSelected());
			}
		}
		// change the plugins folder name
		else if(e.getSource() == m_settingsPluginDirectoryNameMenuItem) {
			// prompt for the plugin directory name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the plugin directory name:", SettingsManager.instance.pluginDirectoryName);
			if(input == null) { return; }
			
			String newPluginDirectoryName = input.trim();
			if(newPluginDirectoryName.length() == 0) { return; }
			
			if(!newPluginDirectoryName.equalsIgnoreCase(SettingsManager.instance.pluginDirectoryName)) {
				SettingsManager.instance.pluginDirectoryName = newPluginDirectoryName;
			}
		}
		// change the console log file name
		else if(e.getSource() == m_settingsConsoleLogFileNameMenuItem) {
			// prompt for the console log file name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the console log file name:", SettingsManager.instance.consoleLogFileName);
			if(input == null) { return; }
			
			String newConsoleLogFileName = input.trim();
			if(newConsoleLogFileName.length() == 0) { return; }
			
			if(!newConsoleLogFileName.equalsIgnoreCase(SettingsManager.instance.consoleLogFileName)) {
				GroupManager.console.resetConsoleLogFileHeader();
				
				SettingsManager.instance.consoleLogFileName = newConsoleLogFileName;
			}
		}
		// change the log directory name
		else if(e.getSource() == m_settingsLogDirectoryNameMenuItem) {
			// prompt for the log directory name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the log directory name:", SettingsManager.instance.logDirectoryName);
			if(input == null) { return; }
			
			String newLogDirectoryName = input.trim();
			if(newLogDirectoryName.length() == 0) { return; }
			
			if(!newLogDirectoryName.equalsIgnoreCase(SettingsManager.instance.logDirectoryName)) {
				SettingsManager.instance.logDirectoryName = newLogDirectoryName;
			}
		}
		else if(e.getSource() == m_settingsVersionFileURLMenuItem) {
			// prompt for the version file url
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the version file URL:", SettingsManager.instance.versionFileURL);
			if(input == null) { return; }
			
			String newVersionFileURL = input.trim();
			if(newVersionFileURL.length() == 0) { return; }
			
			if(!newVersionFileURL.equalsIgnoreCase(SettingsManager.instance.versionFileURL)) {
				SettingsManager.instance.versionFileURL = newVersionFileURL;
			}
		}
		// change the console auto scrolling
		else if(e.getSource() == m_settingsAutoScrollConsoleMenuItem) {
			SettingsManager.instance.autoScrollConsole = m_settingsAutoScrollConsoleMenuItem.isSelected();
		}
		// change the maximum number of elements the console can hold
		else if(e.getSource() == m_settingsMaxConsoleHistoryMenuItem) {
			// prompt for the maximum console history size
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the maximum console history size:", SettingsManager.instance.maxConsoleHistory);
			if(input == null) { return; }
			
			// set the new console history size
			int maxConsoleHistory = -1;
			try {
				maxConsoleHistory = Integer.parseInt(input);
			}
			catch(NumberFormatException e2) {
				JOptionPane.showMessageDialog(m_frame, "Invalid number entered for maximum console history.", "Invalid Number", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if(maxConsoleHistory > 1) {
				SettingsManager.instance.maxConsoleHistory = maxConsoleHistory;
			}
		}
		// change console logging
		else if(e.getSource() == m_settingsLogConsoleMenuItem) {
			SettingsManager.instance.logConsole = m_settingsLogConsoleMenuItem.isSelected();
		}
		// change update notification supressing
		else if(e.getSource() == m_settingsSupressUpdatesMenuItem) {
			SettingsManager.instance.supressUpdates = m_settingsSupressUpdatesMenuItem.isSelected();
		}
		// toggle auto-save settings
		else if(e.getSource() == m_settingsAutoSaveSettingsMenuItem) {
			SettingsManager.instance.autoSaveSettings = m_settingsAutoSaveSettingsMenuItem.isSelected();
		}
		// save all settings to file
		else if(e.getSource() == m_settingsSaveSettingsMenuItem) {
			if(SettingsManager.instance.save()) {
				String message = "Successfully saved settings to file: " + SettingsManager.instance.settingsFileName;
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				String message = "Failed to save settings to file: " + SettingsManager.instance.settingsFileName;
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Settings Not Saved", JOptionPane.ERROR_MESSAGE);
			}
		}
		// reload all settings from file
		else if(e.getSource() == m_settingsReloadSettingsMenuItem) {
			if(SettingsManager.instance.load()) {
				update();
				
				String message = "Settings successfully loaded from file: " + SettingsManager.instance.settingsFileName;
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Settings Loaded", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				String message = "Failed to load settings from file: " + SettingsManager.instance.settingsFileName;
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Settings Not Loaded", JOptionPane.ERROR_MESSAGE);
			}
		}
		// reset all settings
		else if(e.getSource() == m_settingsResetSettingsMenuItem) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "Are you sure you wish to reset all settings?", "Reset All Settings", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if(choice == JOptionPane.YES_OPTION) {
				SettingsManager.instance.reset();
				
				update();
				
				SystemConsole.instance.writeLine("All settings reset to default values");
			}
		}
		// display a list of loaded plugins
		else if(e.getSource() == m_pluginsListLoadedMenuItem) {
			GroupPluginManager.instance.displayLoadedPlugins();
		}
		// prompt for a plugin to load
		else if(e.getSource() == m_pluginsLoadMenuItem) {
			GroupPluginManager.instance.loadPluginPrompt();
		}
		// load all plugins
		else if(e.getSource() == m_pluginsLoadAllMenuItem) {
			GroupPluginManager.instance.loadPlugins();
		}
		// toggle auto-loading of plugins
		else if(e.getSource() == m_pluginsAutoLoadMenuItem) {
			SettingsManager.instance.autoLoadPlugins = m_pluginsAutoLoadMenuItem.isSelected();
			
			update();
		}
		// reset the window position
		else if(e.getSource() == m_windowResetPositionMenuItem) {
			resetWindowPosition();
		}
		// reset the window size
		else if(e.getSource() == m_windowResetSizeMenuItem) {
			resetWindowSize();
		}
		// check program version
		else if(e.getSource() == m_helpCheckVersionMenuItem) {
			VersionChecker.checkVersion();
		}
		// display help message
		else if(e.getSource() == m_helpAboutMenuItem) {
			JOptionPane.showMessageDialog(m_frame, "Group Manager Version " + GroupManager.VERSION + "\nCreated by Kevin Scroggins (a.k.a. nitro_glycerine)\nE-Mail: nitro404@gmail.com\nWebsite: http://www.nitro404.com", "About Group Manager", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			// change group file sort direction
			for(int i=0;i<m_sortDirectionMenuItems.length;i++) {
				if(e.getSource() == m_sortDirectionMenuItems[i]) {
					if(SettingsManager.instance.sortAllGroups) {
						if(SettingsManager.instance.sortDirection == SortDirection.values()[i]) { return; }
						
						SettingsManager.instance.sortDirection = SortDirection.values()[i];
						
						updateAll();
					}
					else {
						GroupPanel selectedGroupPanel = getSelectedGroupPanel();
						if(selectedGroupPanel == null) { return; }
						
						selectedGroupPanel.getGroup().setSortDirection(SortDirection.values()[i]);
					}
					
					return;
				}
			}
			
			// change group file sort type
			for(int i=0;i<m_sortTypeMenuItems.length;i++) {
				if(e.getSource() == m_sortTypeMenuItems[i]) {
					if(SettingsManager.instance.sortAllGroups) {
						if(SettingsManager.instance.sortType == GroupFileSortType.values()[i]) { return; }
						
						SettingsManager.instance.sortType = GroupFileSortType.values()[i];
						
						updateAll();
					}
					else {
						GroupPanel selectedGroupPanel = getSelectedGroupPanel();
						if(selectedGroupPanel == null) { return; }
						
						selectedGroupPanel.getGroup().setSortType(GroupFileSortType.values()[i]);
					}
					
					return;
				}
			}
		}
	}
	
	public boolean handleGroupAction(GroupAction action) {
		if(!GroupAction.isvalid(action)) { return false; }
		
		switch(action.getAction()) {
			case Save:
				saveGroup(action.getSource());
				break;
				
			case SaveAs:
				saveGroupAsNew(action.getSource());
				break;
				
			case AddFiles:
				addFilesToGroup(action.getSource());
				break;
				
			case RemoveFiles:
				removeSelectedFilesFromGroup(action.getSource());
				break;
				
			case ReplaceFile:
				replaceSelectedFileInGroup(action.getSource());
				break;
			
			case ExtractFiles:
				extractSelectedFilesFromGroup(action.getSource());
				break;
				
			case Import:
				importGroupInto(action.getSource());
				break;
				
			case Export:
				exportGroup(action.getSource());
				break;
				
			case Close:
				closeGroup(action.getSource());
				break;
				
			default:
				return false;
		}
		
		return true;
	}
	
	public void stateChanged(ChangeEvent e) {
		if(m_updating) { return; }
		
		if(e.getSource() == m_mainTabbedPane) {
			if(m_mainTabbedPane.getSelectedIndex() >= 0 && m_mainTabbedPane.getSelectedIndex() < m_mainTabbedPane.getTabCount()) {
				update();
			}
		}
		
		for(int i=0;i<m_groupPanels.size();i++) {
			m_groupPanels.elementAt(i).updateLayout();
		}
	}
	
	public void componentShown(ComponentEvent e) { }
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	
	public void componentResized(ComponentEvent e) {
		updateLayout();
	}
	
	public void close() {
		if(!closeAllGroups()) {
			return;
		}
		
		// reset initialization variables
		m_initialized = false;
		
		SettingsManager.instance.windowPositionX = m_frame.getX();
		SettingsManager.instance.windowPositionY = m_frame.getY();
		SettingsManager.instance.windowWidth = m_frame.getWidth();
		SettingsManager.instance.windowHeight = m_frame.getHeight();
		
		// close the server
		GroupManager.instance.close();
		
		m_frame.dispose();
		
		System.exit(0);
	}
	
}
