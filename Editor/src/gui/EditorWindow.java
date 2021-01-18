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
import item.*;
import group.*;
import map.*;
import map.Map;
import palette.*;
import action.*;
import editor.*;
import plugin.*;
import version.*;

public class EditorWindow implements WindowListener, ComponentListener, ChangeListener, ActionListener, GroupActionListener, PaletteActionListener, MapActionListener, UpdateListener, Updatable {
	
	private JFrame m_frame;
	private JTabbedPane m_mainTabbedPane;
	private Vector<ItemPanel> m_itemPanels;
	private JTextArea m_consoleText;
	private Font m_consoleFont;
	private JScrollPane m_consoleScrollPane;

	private ProgressDialog m_progressDialog;
	private PreferredPluginEditorDialog m_preferredPluginEditorDialog;
	private OffsetMapSoundRangeDialog m_offsetMapSoundRangeDialog;
	
	private JMenuBar m_menuBar;
	private JMenu m_fileMenu;
	private JMenuItem m_fileNewMenuItem;
	private JMenuItem m_fileOpenMenuItem;
	private JMenuItem m_fileSaveMenuItem;
	private JMenuItem m_fileSaveAsMenuItem;
	private JMenuItem m_fileSaveAllMenuItem;
	private JMenuItem m_fileImportMenuItem;
	private JMenuItem m_fileExportMenuItem;
	private JMenuItem m_fileCloseMenuItem;
	private JMenuItem m_fileCloseAllMenuItem;
	private JMenuItem m_fileExitMenuItem;
	private JMenu m_mapMenu;
	private JMenuItem m_mapOffsetSoundRangeMenuItem;
	private JMenu m_groupMenu;
	private JMenuItem m_groupAddFilesMenuItem;
	private JMenuItem m_groupRemoveFilesMenuItem;
	private JMenuItem m_groupReplaceFileMenuItem;
	private JMenuItem m_groupRenameFileMenuItem;
	private JMenuItem m_groupExtractFilesMenuItem;
	private JMenuItem m_groupExtractAllFilesMenuItem;
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
	private JMenu m_processMenu;
	private JMenuItem m_processGroupFilesMenuItem;
	private JMenuItem m_processOpenGroupsMenuItem;
	private JMenu m_settingsMenu;
	private JMenuItem m_settingsPluginDirectoryNameMenuItem;
	private JMenuItem m_settingsConsoleLogFileNameMenuItem;
	private JMenuItem m_settingsLogDirectoryNameMenuItem;
	private JMenuItem m_settingsVersionFileURLMenuItem;
	private JMenuItem m_settingsBackgroundColourMenuItem;
	private JCheckBoxMenuItem m_settingsAutoScrollConsoleMenuItem;
	private JMenuItem m_settingsMaxConsoleHistoryMenuItem;
	private JCheckBoxMenuItem m_settingsLogConsoleMenuItem;
	private JCheckBoxMenuItem m_settingsSuppressUpdatesMenuItem;
	private JCheckBoxMenuItem m_settingsAutoSaveSettingsMenuItem;
	private JMenuItem m_settingsSaveSettingsMenuItem;
	private JMenuItem m_settingsReloadSettingsMenuItem;
	private JMenuItem m_settingsResetSettingsMenuItem;
	private JMenu m_pluginsMenu;
	private JMenuItem m_pluginsPreferredEditorMenuItem;
	private JMenuItem m_pluginsListLoadedMenuItem;
	private JMenuItem m_pluginsLoadMenuItem;
	private JMenuItem m_pluginsLoadAllMenuItem;
	private JCheckBoxMenuItem m_pluginsAutoLoadMenuItem;
	private JMenu m_windowMenu;
	private JMenuItem m_buttonSizeMenuItem;
	private JMenuItem m_paletteSpacingMenuItem;
	private JMenuItem m_windowResetPositionMenuItem;
	private JMenuItem m_windowResetSizeMenuItem;
	private JMenu m_helpMenu;
	private JMenuItem m_helpCheckVersionMenuItem;
	private JMenuItem m_helpAboutMenuItem;
	
	private boolean m_initialized;
	private boolean m_updating;
	
	public static final int SCROLL_INCREMENT = 16;
	
	private TransferHandler m_transferHandler = new TransferHandler() {
		
		private static final long serialVersionUID = 533089044121501451L;

		public void exportToClipboard(JComponent component, Clipboard clipboard, int action) throws IllegalStateException {
			StringSelection selectedText = new StringSelection(((JTextArea) component).getSelectedText());
			clipboard.setContents(selectedText, selectedText);
		}

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
				loadItems(((java.util.List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor)).toArray(new File[1]));
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
	
	public EditorWindow() {
		m_frame = new JFrame("Ultimate Duke Nukem 3D Editor");
		m_frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		m_frame.setMinimumSize(new Dimension(320, 240));
		m_frame.setLocation(SettingsManager.defaultWindowPositionX, SettingsManager.defaultWindowPositionY);
		m_frame.setSize(SettingsManager.defaultWindowWidth, SettingsManager.defaultWindowHeight);
		m_frame.addWindowListener(this);
		m_frame.addComponentListener(this);
		m_frame.setTransferHandler(m_transferHandler);

		m_frame.getRootPane().registerKeyboardAction(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateAll();

					SystemConsole.instance.writeLine("Refreshed window.");
				}
			},
			KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
			JComponent.WHEN_IN_FOCUSED_WINDOW
		);

		m_itemPanels = new Vector<ItemPanel>();
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
		m_preferredPluginEditorDialog = new PreferredPluginEditorDialog(m_frame);
		m_offsetMapSoundRangeDialog = new OffsetMapSoundRangeDialog(m_frame);
		
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
		m_fileImportMenuItem = new JMenuItem("Import");
		m_fileExportMenuItem = new JMenuItem("Export");
		m_fileCloseMenuItem = new JMenuItem("Close");
		m_fileCloseAllMenuItem = new JMenuItem("Close All");
		m_fileExitMenuItem = new JMenuItem("Exit");
		
		m_fileNewMenuItem.setMnemonic('N');
		m_fileOpenMenuItem.setMnemonic('O');
		m_fileSaveMenuItem.setMnemonic('S');
		m_fileImportMenuItem.setMnemonic('I');
		m_fileExportMenuItem.setMnemonic('P');
		m_fileExitMenuItem.setMnemonic('Q');
		
		m_fileNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK));
		m_fileOpenMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK));
		m_fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));
		m_fileImportMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.Event.CTRL_MASK));
		m_fileExportMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.Event.CTRL_MASK));
		m_fileCloseMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.Event.CTRL_MASK));
		m_fileExitMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.Event.CTRL_MASK));
		
		m_mapMenu = new JMenu("Map");
		m_mapOffsetSoundRangeMenuItem = new JMenuItem("Offset Sound Range");
		
		m_groupMenu = new JMenu("Group");
		m_groupAddFilesMenuItem = new JMenuItem("Add Files");
		m_groupRemoveFilesMenuItem = new JMenuItem("Remove Files");
		m_groupReplaceFileMenuItem = new JMenuItem("Replace File");
		m_groupRenameFileMenuItem = new JMenuItem("Rename File");
		m_groupExtractFilesMenuItem = new JMenuItem("Extract Files");
		m_groupExtractAllFilesMenuItem = new JMenuItem("Extract All Files");

		m_groupAddFilesMenuItem.setMnemonic('D');
		m_groupRemoveFilesMenuItem.setMnemonic('R');
		m_groupExtractFilesMenuItem.setMnemonic('E');

		m_groupAddFilesMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.Event.CTRL_MASK));
		m_groupRemoveFilesMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.Event.CTRL_MASK));
		m_groupExtractFilesMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.Event.CTRL_MASK));

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
		
		m_processMenu = new JMenu("Process");
		m_processGroupFilesMenuItem = new JMenuItem("Group Files");
		m_processOpenGroupsMenuItem = new JMenuItem("Open Groups");
		
		m_settingsMenu = new JMenu("Settings");
		m_settingsPluginDirectoryNameMenuItem = new JMenuItem("Plugin Directory Name");
		m_settingsConsoleLogFileNameMenuItem = new JMenuItem("Console Log File Name");
		m_settingsLogDirectoryNameMenuItem = new JMenuItem("Log Directory Name");
		m_settingsVersionFileURLMenuItem = new JMenuItem("Version File URL");
		m_settingsBackgroundColourMenuItem = new JMenuItem("Background Colour");
		m_settingsAutoScrollConsoleMenuItem = new JCheckBoxMenuItem("Auto-Scroll Console");
		m_settingsMaxConsoleHistoryMenuItem = new JMenuItem("Max Console History");
		m_settingsLogConsoleMenuItem = new JCheckBoxMenuItem("Log Console to File");
		m_settingsSuppressUpdatesMenuItem = new JCheckBoxMenuItem("Suppress Update Notifications");
		m_settingsAutoSaveSettingsMenuItem = new JCheckBoxMenuItem("Auto-Save Settings");
		m_settingsSaveSettingsMenuItem = new JMenuItem("Save Settings");
		m_settingsReloadSettingsMenuItem = new JMenuItem("Reload Settings");
		m_settingsResetSettingsMenuItem = new JMenuItem("Reset Settings");
		m_settingsAutoScrollConsoleMenuItem.setSelected(SettingsManager.defaultAutoScrollConsole);
		m_settingsAutoSaveSettingsMenuItem.setSelected(SettingsManager.defaultAutoSaveSettings);
		m_settingsLogConsoleMenuItem.setSelected(SettingsManager.defaultLogConsole);
		m_settingsSuppressUpdatesMenuItem.setSelected(SettingsManager.defaultSuppressUpdates);
		
		m_pluginsMenu = new JMenu("Plugins");
		m_pluginsPreferredEditorMenuItem = new JMenuItem("Edit Preferred Plugins");
		m_pluginsListLoadedMenuItem = new JMenuItem("List Loaded Plugins");
		m_pluginsLoadMenuItem = new JMenuItem("Load Plugin");
		m_pluginsLoadAllMenuItem = new JMenuItem("Load All Plugins");
		m_pluginsAutoLoadMenuItem = new JCheckBoxMenuItem("Auto-Load Plugins");
		m_pluginsAutoLoadMenuItem.setSelected(SettingsManager.defaultAutoLoadPlugins);
		
		m_windowMenu = new JMenu("Window");
		m_buttonSizeMenuItem = new JMenuItem("Pixel Button Size");
		m_paletteSpacingMenuItem = new JMenuItem("Palette Spacing");
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
		m_fileImportMenuItem.addActionListener(this);
		m_fileExportMenuItem.addActionListener(this);
		m_fileCloseMenuItem.addActionListener(this);
		m_fileCloseAllMenuItem.addActionListener(this);
		m_fileExitMenuItem.addActionListener(this);
		m_mapOffsetSoundRangeMenuItem.addActionListener(this);
		m_groupAddFilesMenuItem.addActionListener(this);
		m_groupRemoveFilesMenuItem.addActionListener(this);
		m_groupReplaceFileMenuItem.addActionListener(this);
		m_groupRenameFileMenuItem.addActionListener(this);
		m_groupExtractFilesMenuItem.addActionListener(this);
		m_groupExtractAllFilesMenuItem.addActionListener(this);
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
		m_processGroupFilesMenuItem.addActionListener(this);
		m_processOpenGroupsMenuItem.addActionListener(this);
		m_settingsPluginDirectoryNameMenuItem.addActionListener(this);
		m_settingsConsoleLogFileNameMenuItem.addActionListener(this);
		m_settingsLogDirectoryNameMenuItem.addActionListener(this);
		m_settingsVersionFileURLMenuItem.addActionListener(this);
		m_settingsBackgroundColourMenuItem.addActionListener(this);
		m_settingsAutoScrollConsoleMenuItem.addActionListener(this);
		m_settingsMaxConsoleHistoryMenuItem.addActionListener(this);
		m_settingsLogConsoleMenuItem.addActionListener(this);
		m_settingsSuppressUpdatesMenuItem.addActionListener(this);
		m_settingsAutoSaveSettingsMenuItem.addActionListener(this);
		m_settingsSaveSettingsMenuItem.addActionListener(this);
		m_settingsReloadSettingsMenuItem.addActionListener(this);
		m_settingsResetSettingsMenuItem.addActionListener(this);
		m_pluginsPreferredEditorMenuItem.addActionListener(this);
		m_pluginsListLoadedMenuItem.addActionListener(this);
		m_pluginsLoadMenuItem.addActionListener(this);
		m_pluginsLoadAllMenuItem.addActionListener(this);
		m_pluginsAutoLoadMenuItem.addActionListener(this);
		m_buttonSizeMenuItem.addActionListener(this);
		m_paletteSpacingMenuItem.addActionListener(this);
		m_windowResetPositionMenuItem.addActionListener(this);
		m_windowResetSizeMenuItem.addActionListener(this);
		m_helpCheckVersionMenuItem.addActionListener(this);
		m_helpAboutMenuItem.addActionListener(this);
		
		m_fileMenu.add(m_fileNewMenuItem);
		m_fileMenu.add(m_fileOpenMenuItem);
		m_fileMenu.add(m_fileSaveMenuItem);
		m_fileMenu.add(m_fileSaveAsMenuItem);
		m_fileMenu.add(m_fileSaveAllMenuItem);
		m_fileMenu.add(m_fileImportMenuItem);
		m_fileMenu.add(m_fileExportMenuItem);
		m_fileMenu.add(m_fileCloseMenuItem);
		m_fileMenu.add(m_fileCloseAllMenuItem);
		m_fileMenu.add(m_fileExitMenuItem);

		m_mapMenu.add(m_mapOffsetSoundRangeMenuItem);

		m_groupMenu.add(m_groupAddFilesMenuItem);
		m_groupMenu.add(m_groupRemoveFilesMenuItem);
		m_groupMenu.add(m_groupReplaceFileMenuItem);
		m_groupMenu.add(m_groupRenameFileMenuItem);
		m_groupMenu.add(m_groupExtractFilesMenuItem);
		m_groupMenu.add(m_groupExtractAllFilesMenuItem);
		
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
		
		m_processMenu.add(m_processGroupFilesMenuItem);
		m_processMenu.add(m_processOpenGroupsMenuItem);
		
		m_settingsMenu.add(m_settingsPluginDirectoryNameMenuItem);
		m_settingsMenu.add(m_settingsConsoleLogFileNameMenuItem);
		m_settingsMenu.add(m_settingsLogDirectoryNameMenuItem);
		m_settingsMenu.add(m_settingsVersionFileURLMenuItem);
		m_settingsMenu.add(m_settingsBackgroundColourMenuItem);
		m_settingsMenu.add(m_settingsAutoScrollConsoleMenuItem);
		m_settingsMenu.add(m_settingsMaxConsoleHistoryMenuItem);
		m_settingsMenu.add(m_settingsLogConsoleMenuItem);
		m_settingsMenu.add(m_settingsSuppressUpdatesMenuItem);
		m_settingsMenu.addSeparator();
		m_settingsMenu.add(m_settingsAutoSaveSettingsMenuItem);
		m_settingsMenu.add(m_settingsSaveSettingsMenuItem);
		m_settingsMenu.add(m_settingsReloadSettingsMenuItem);
		m_settingsMenu.add(m_settingsResetSettingsMenuItem);
		
		m_pluginsMenu.add(m_pluginsPreferredEditorMenuItem);
		m_pluginsMenu.add(m_pluginsListLoadedMenuItem);
		m_pluginsMenu.add(m_pluginsLoadMenuItem);
		m_pluginsMenu.add(m_pluginsLoadAllMenuItem);
		m_pluginsMenu.add(m_pluginsAutoLoadMenuItem);

		m_windowMenu.add(m_buttonSizeMenuItem);
		m_windowMenu.add(m_paletteSpacingMenuItem);
		m_windowMenu.add(m_windowResetPositionMenuItem);
		m_windowMenu.add(m_windowResetSizeMenuItem);
		
		m_helpMenu.add(m_helpCheckVersionMenuItem);
		m_helpMenu.add(m_helpAboutMenuItem);
		
		m_menuBar.add(m_fileMenu);
		m_menuBar.add(m_mapMenu);
		m_menuBar.add(m_groupMenu);
		m_menuBar.add(m_selectMenu);
		m_menuBar.add(m_sortMenu);
		m_menuBar.add(m_processMenu);
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

	public Vector<Group> getOpenGroups() {
		Vector<Group> groups = new Vector<Group>();
		Group group = null;
		for(int i=0;i<m_itemPanels.size();i++) {
			group = getGroupFrom(m_itemPanels.elementAt(i));
			if(group == null) { continue; }
			
			groups.add(group);
		}
		return groups;
	}
	
	public void addItem(ItemPanel itemPanel) {
		if(itemPanel == null) { return; }
		
		itemPanel.setTransferHandler(m_transferHandler);
		itemPanel.addUpdateListener(this);
		m_itemPanels.add(itemPanel);
		
		JScrollPane groupScrollPane = new JScrollPane(itemPanel);
		groupScrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
		int index = m_mainTabbedPane.getTabCount() - 1;
		m_mainTabbedPane.insertTab(itemPanel.getTabName(), null, groupScrollPane, itemPanel.getTabDescription(), index);
		
		m_mainTabbedPane.setSelectedIndex(index);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Editor.editorWindow.getSelectedItemPanel().updateLayout();
			}
		});
	}

	public boolean unsavedChanges() {
		for(int i=0;i<m_itemPanels.size();i++) {
			if(m_itemPanels.elementAt(i).isChanged()) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean selectItemPanel(ItemPanel panel) {
		if(panel == null) { return false; }
		
		ItemPanel p = null;
		for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
			p = getItemPanelFrom(m_mainTabbedPane.getComponent(i));
			if(p == null) { continue; }
			
			if(panel == p) {
				m_mainTabbedPane.setSelectedComponent(m_mainTabbedPane.getComponent(i));
				return true;
			}
		}
		return false;
	}

	protected ItemPanel getSelectedItemPanel() {
		Component selectedComponent = m_mainTabbedPane.getSelectedComponent();
		if(selectedComponent == null || !(selectedComponent instanceof JScrollPane)) { return null; }
		JScrollPane selectedScrollPane = (JScrollPane) selectedComponent;
		JViewport selectedViewport = selectedScrollPane.getViewport();
		if(selectedViewport == null || selectedViewport.getComponentCount() < 1) { return null; }
		Component selectedScrollPaneComponent = selectedViewport.getComponent(0);
		if(selectedScrollPaneComponent == null || !(selectedScrollPaneComponent instanceof ItemPanel)) { return null; }
		return (ItemPanel) selectedScrollPaneComponent;
	}

	protected Item getSelectedItem() {
		ItemPanel selectedItemPanel = getSelectedItemPanel();
		if(selectedItemPanel == null || !(selectedItemPanel instanceof ItemPanel)) { return null; }
		return selectedItemPanel.getItem();
	}

	protected Group getSelectedGroup() {
		ItemPanel selectedItemPanel = getSelectedItemPanel();
		if(selectedItemPanel == null || !(selectedItemPanel instanceof GroupPanel)) { return null; }
		return ((GroupPanel) selectedItemPanel).getGroup();
	}

	protected Palette getSelectedPalette() {
		ItemPanel selectedItemPanel = getSelectedItemPanel();
		if(selectedItemPanel == null || !(selectedItemPanel instanceof PalettePanel)) { return null; }
		return ((PalettePanel) selectedItemPanel).getPalette();
	}

	protected Item getItemFrom(Component component) {
		ItemPanel itemPanel = getItemPanelFrom(component);
		if(itemPanel == null || !(itemPanel instanceof ItemPanel)) { return null; }
		return itemPanel.getItem();
	}

	protected Group getGroupFrom(Component component) {
		ItemPanel itemPanel = getItemPanelFrom(component);
		if(itemPanel == null || !(itemPanel instanceof GroupPanel)) { return null; }
		return ((GroupPanel) itemPanel).getGroup();
	}

	protected Palette getPaletteFrom(Component component) {
		ItemPanel itemPanel = getItemPanelFrom(component);
		if(itemPanel == null || !(itemPanel instanceof PalettePanel)) { return null; }
		return ((PalettePanel) itemPanel).getPalette();
	}
	
	protected ItemPanel getItemPanelFrom(Component component) {
		if(component == null || !(component instanceof JScrollPane)) { return null; }
		JScrollPane scrollPane = (JScrollPane) component;
		JViewport viewport = scrollPane.getViewport();
		if(viewport == null || viewport.getComponentCount() < 1) { return null; }
		Component scrollPaneComponent = viewport.getComponent(0);
		if(scrollPaneComponent == null || !(scrollPaneComponent instanceof ItemPanel)) { return null; }
		return (ItemPanel) scrollPaneComponent;
	}
	
	protected Component getTabComponentWith(ItemPanel itemPanel) {
		if(itemPanel == null) { return null; }
		Component component = null;
		for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
			component = m_mainTabbedPane.getComponent(i);
			if(!(component instanceof JScrollPane)) { continue; }
			JScrollPane scrollPane = (JScrollPane) component;
			JViewport viewport = scrollPane.getViewport();
			if(viewport == null || viewport.getComponentCount() < 1) { continue; }
			Component scrollPaneComponent = viewport.getComponent(0);
			if(scrollPaneComponent == null || !(scrollPaneComponent instanceof ItemPanel)) { continue; }
			if((ItemPanel) scrollPaneComponent == itemPanel) {
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

	protected Component getTabComponentWith(Palette palette) {
		if(palette == null) { return null; }
		Component component = null;
		for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
			component = m_mainTabbedPane.getComponent(i);
			if(!(component instanceof JScrollPane)) { continue; }
			JScrollPane scrollPane = (JScrollPane) component;
			JViewport viewport = scrollPane.getViewport();
			if(viewport == null || viewport.getComponentCount() < 1) { continue; }
			Component scrollPaneComponent = viewport.getComponent(0);
			if(scrollPaneComponent == null || !(scrollPaneComponent instanceof PalettePanel)) { continue; }
			PalettePanel palettePanel = (PalettePanel) scrollPaneComponent;
			if(palette.equals(palettePanel.getPalette())) {
				return component;
			}
		}
		return null;
	}
	
	protected int indexOfTabComponentWith(ItemPanel itemPanel) {
		if(itemPanel == null) { return -1; }
		Component component = null;
		for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
			component = m_mainTabbedPane.getComponent(i);
			if(!(component instanceof JScrollPane)) { continue; }
			JScrollPane scrollPane = (JScrollPane) component;
			JViewport viewport = scrollPane.getViewport();
			if(viewport == null || viewport.getComponentCount() < 1) { continue; }
			Component scrollPaneComponent = viewport.getComponent(0);
			if(scrollPaneComponent == null || !(scrollPaneComponent instanceof GroupPanel)) { continue; }
			if((ItemPanel) scrollPaneComponent == itemPanel) {
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

	protected int indexOfTabComponentWith(Palette palette) {
		if(palette == null) { return -1; }
		Component component = null;
		for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
			component = m_mainTabbedPane.getComponent(i);
			if(!(component instanceof JScrollPane)) { continue; }
			JScrollPane scrollPane = (JScrollPane) component;
			JViewport viewport = scrollPane.getViewport();
			if(viewport == null || viewport.getComponentCount() < 1) { continue; }
			Component scrollPaneComponent = viewport.getComponent(0);
			if(scrollPaneComponent == null || !(scrollPaneComponent instanceof GroupPanel)) { continue; }
			PalettePanel palettePanel = (PalettePanel) scrollPaneComponent;
			if(palette.equals(palettePanel.getPalette())) {
				m_mainTabbedPane.indexOfComponent(scrollPaneComponent);
			}
		}
		return -1;
	}

	public boolean promptNewItem() {
		Vector<FilePlugin> loadedPlugins = EditorPluginManager.instance.getPlugins(FilePlugin.class);
		if(loadedPlugins.isEmpty()) {
			String message = "No plugins found that support instantiation. Perhaps you forgot to load all plugins?";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Plugins", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		FilePlugin currentPlugin = null;
		int previousNewFilePluginIndex = -1;

		for(int i = 0; i < loadedPlugins.size(); i++) {
			currentPlugin = loadedPlugins.elementAt(i);

			if(currentPlugin.getName().equalsIgnoreCase(SettingsManager.instance.previousNewFileType)) {
				previousNewFilePluginIndex = i;
				break;
			}
		}
		
		int pluginIndex = -1;
		Object choices[] = loadedPlugins.toArray();
		Object defaultChoice = previousNewFilePluginIndex == -1 ? choices[0] : choices[previousNewFilePluginIndex];
		Object value = JOptionPane.showInputDialog(m_frame, "Choose an item type to create:", "Choose New Item Type", JOptionPane.QUESTION_MESSAGE, null, choices, defaultChoice);
		if(value == null) { return false; }
		for(int i=0;i<choices.length;i++) {
			if(choices[i] == value) {
				pluginIndex = i;
				break;
			}
		}
		if(pluginIndex < 0 || pluginIndex >= loadedPlugins.size()) { return false; }
		FilePlugin plugin = loadedPlugins.elementAt(pluginIndex);
		
		Item newItem = null;
		try {
			newItem = plugin.getNewItemInstance(null);
		}
		catch(ItemInstantiationException e) {
			String message = "Failed to create instance of \"" + plugin.getName() + "\"!";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(!newItem.isInstantiable()) {
			String message = "\"" + plugin.getName() + "\" is not instantiable!";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Not Instantiable", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		SystemConsole.instance.writeLine(plugin.getName() + " item created successfully!");
		
		int fileTypeIndex = 0;
		if(newItem.numberOfFileTypes() > 1) {
			Object selectedFileType = JOptionPane.showInputDialog(m_frame, "Choose an item file type to create:", "Choose Item File Type", JOptionPane.QUESTION_MESSAGE, null, newItem.getFileTypes(), newItem.getFileTypes()[0]);
			if(selectedFileType == null) { return false; }
			for(int i=0;i<newItem.getFileTypes().length;i++) {
				if(newItem.getFileTypes()[i] == selectedFileType) {
					fileTypeIndex = i;
					break;
				}
			}
			if(fileTypeIndex < 0 || fileTypeIndex >= newItem.numberOfFileTypes()) { return false; }
			newItem.setFileType(newItem.getFileTypes()[fileTypeIndex]);
		}

		ItemPanel newItemPanel = null;
		try { newItemPanel = plugin.getNewItemPanelInstance(newItem); }
		catch(ItemPanelInstantiationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Item Panel Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		if(newItemPanel == null) {
			String message = "Failed to instantiate group panel for \"" + plugin.getName() + " plugin.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(!newItemPanel.init()) {
			String message = "Failed to initialize group panel for \"" + plugin.getName() + "\" plugin.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Item Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		SettingsManager.instance.previousNewFileType = plugin.getName();
		
		if(newItem instanceof Palette) {
			Color fillColour = JColorChooser.showDialog(null, "Choose Fill Colour", Color.BLACK);
			if(fillColour == null) { return false; }
			
			if(!((Palette) newItem).fillAllWithColour(fillColour)) {
				String message = "Failed to fill palette with specified colour!";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Palette Fill Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		else if(newItem instanceof Group) {
			((GroupPanel) newItemPanel).addGroupActionListener(this);
		}
		else if(newItem instanceof Map) {
			((MapPanel) newItemPanel).addMapActionListener(this);
		}
		else if(newItem instanceof Palette) {
			((PalettePanel) newItemPanel).addPaletteActionListener(this);
		}
		
		addItem(newItemPanel);
		
		newItemPanel.setItemNumber(Editor.getItemNumber());
		newItemPanel.setChanged(true);
		
		return true;
	}
	
	public void promptLoadItems() {
		if(EditorPluginManager.instance.numberOfPlugins(FilePlugin.class) == 0) {
			String message = "No file plugins loaded. You must have at least one file plugin loaded to open a file.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Plugins Loaded", JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		JFileChooser fileChooser = new JFileChooser(SettingsManager.instance.previousOpenDirectory);
		fileChooser.setDialogTitle("Load Files");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(true);
		if(fileChooser.showOpenDialog(m_frame) != JFileChooser.APPROVE_OPTION) { return; }
		
		SettingsManager.instance.previousOpenDirectory = Utilities.getFilePath(fileChooser.getCurrentDirectory());
		
		loadItems(fileChooser.getSelectedFiles());
	}

	public int loadItems(File[] files) {
		if(files == null || files.length == 0) { return -1; }
		
		int numberOfFilesLoaded = 0; 
		for(int i=0;i<files.length;i++) {
			if(files[i] == null) { continue; }
			
			if(loadItem(files[i])) {
				numberOfFilesLoaded++;
			}
		}
		
		if(files.length > 0) {
			int numberOfFilesFailed = files.length - numberOfFilesLoaded;
			if(numberOfFilesLoaded == 0 && numberOfFilesFailed > 0) {
				SystemConsole.instance.writeLine("Failed to load " + numberOfFilesFailed + " file" + (numberOfFilesFailed == 1 ? "" : "s") + ", no files loaded.");
			}
			else if(numberOfFilesLoaded > 1) {
				SystemConsole.instance.writeLine("Successfully loaded " + numberOfFilesLoaded + " files " + (numberOfFilesFailed == 0 ? "" : ", while " + numberOfFilesFailed + " failed to load") + "!");
			}
		}
		
		return numberOfFilesLoaded;
	}
	
	public boolean loadItem(File file) {
		if(file == null || !file.exists()) {
			SystemConsole.instance.writeLine("File \"" + file.getName() + "\" does not exist.");
			return false;
		}
		
		for(int i=0;i<m_itemPanels.size();i++) {
			if(m_itemPanels.elementAt(i).isSameFile(file)) {
				selectItemPanel(m_itemPanels.elementAt(i));
				
				String message = "File \"" + (file == null ? "null" : file.getName()) +  "\" already loaded!";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Already Loaded", JOptionPane.INFORMATION_MESSAGE);
				
				return true;
			}
		}
		
		String extension = Utilities.getFileExtension(file.getName());
		
		Vector<FilePlugin> plugins = EditorPluginManager.instance.getPluginsForFileFormat(extension);
		if(plugins == null || plugins.isEmpty()) {
			String message = "No plugin found to load " + extension + " file type. Perhaps you forgot to load all plugins?";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Plugin Found", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		FilePlugin plugin = EditorPluginManager.instance.getPreferredPluginPrompt(extension);
		if(plugin == null) { return false; }
		
		Item item = null;
		try { item = plugin.getNewItemInstance(file); }
		catch(ItemInstantiationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		if(item == null) {
			String message = "Failed to instantiate \"" + plugin.getName() + " (" + plugin.getSupportedFileFormatsAsString() + ")\" plugin when attempting to read file: \"" + file.getName() + "\".";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		try {
			if(!item.load()) {
				String message = "Failed to load file: \"" + file.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedFileFormatsAsString() + ")\".";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "File Loading Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		catch(HeadlessException e) {
			String message = "Exception thrown while loading file: \"" + file.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedFileFormatsAsString() + "): " + e.getMessage();
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "File Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		catch(ItemReadException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "File Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		catch(DeserializationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "File Deserialization Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		SystemConsole.instance.writeLine("File \"" + file.getName() +  "\" loaded successfully!");
		
		ItemPanel itemPanel = null;
		try { itemPanel = plugin.getNewItemPanelInstance(item); }
		catch(ItemPanelInstantiationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Item Panel Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		if(itemPanel == null) {
			String message = "Failed to instantiate item panel for \"" + plugin.getName() + " plugin.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(!itemPanel.init()) {
			String message = "Failed to initialize item panel for \"" + plugin.getName() + "\" plugin.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "File Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		itemPanel.setItemNumber(Editor.getItemNumber());
		
		if(item instanceof Group) {
			((GroupPanel) itemPanel).addGroupActionListener(this);
		}
		else if(item instanceof Map) {
			((MapPanel) itemPanel).addMapActionListener(this);
		}
		else if(item instanceof Palette) {
			((PalettePanel) itemPanel).addPaletteActionListener(this);
		}

		addItem(itemPanel);
		
		return true;
	}
	
	public boolean saveSelectedItem() {
		return saveItem(getSelectedItemPanel(), false);
	}
	
	public boolean saveItem(ItemPanel itemPanel) {
		return saveItem(itemPanel, false);
	}
	
	public boolean saveItem(ItemPanel itemPanel, boolean copy) {
		if(itemPanel == null) { return false; }
		
		if(!itemPanel.isChanged() && !copy) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "No changes detected, save item anyways?", "No Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.NO_OPTION) { return false; }
		}
		
		File itemFile = itemPanel.getItem().getFile();
		
		if(itemFile == null) {
			return saveItemAsNew(itemPanel);
		}
		
		try {
			if(itemPanel.save()) {
				SystemConsole.instance.writeLine("Item successfully updated and saved to file: " + itemFile.getName() + "!");
				
				update();
				
				return true;
			}
			else {
				String message = "Failed to update and save item!";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Save Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		catch(ItemWriteException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			return false;
		}
	}
	
	public boolean saveSelectedItemAsNew() {
		return saveItemAsNew(getSelectedItemPanel());
	}
	
	public boolean saveItemAsNew(ItemPanel itemPanel) {
		if(itemPanel == null) { return false; }
		
		File groupFile = itemPanel.getItem().getFile();
		
		JFileChooser fileChooser = new JFileChooser(groupFile == null ? SettingsManager.instance.previousSaveDirectory : Utilities.getFilePath(groupFile));
		fileChooser.setDialogTitle("Save File File As");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if(groupFile != null) {
			String fileName = groupFile.getName();
			String extension = Utilities.getFileExtension(fileName);
			fileChooser.setSelectedFile(new File(Utilities.getFileNameNoExtension(fileName) + (Utilities.compareCasePercentage(fileName) < 0 ? "_copy" : "_COPY") + (extension == null ? "" : "." + extension)));
		}
		else {
			String extension = itemPanel.getFileExtension();
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
		
		SettingsManager.instance.previousSaveDirectory = Utilities.getFilePath(fileChooser.getCurrentDirectory());
		
		itemPanel.getItem().setFile(fileChooser.getSelectedFile());
		
		return saveItem(itemPanel, true);
	}
	
	public void saveAllItems() {
		if(m_itemPanels.isEmpty()) { return; }
		
		for(int i=0;i<m_itemPanels.size();i++) {
			saveItem(m_itemPanels.elementAt(i));
		}
		
		update();
	}

	public Vector<Sound> offsetSoundRangeInSelectedMap() throws IllegalArgumentException, SoundNumberUnderflowException, SoundNumberOverflowException {
		ItemPanel itemPanel = getSelectedItemPanel();
		
		if(itemPanel == null || !(itemPanel instanceof MapPanel)) {
			return null;
		}
		
		return offsetSoundRangeInMap((MapPanel) itemPanel);
	}
	
	public Vector<Sound> offsetSoundRangeInMap(MapPanel mapPanel) throws IllegalArgumentException, SoundNumberUnderflowException, SoundNumberOverflowException {
		Map map = mapPanel.getMap();
		if(map == null) { return null; }

		m_offsetMapSoundRangeDialog.display();

		if(!m_offsetMapSoundRangeDialog.userSubmitted()) {
			return null;
		}

		return map.offsetSoundRange(m_offsetMapSoundRangeDialog.getSoundRangeStart(), m_offsetMapSoundRangeDialog.getSoundRangeEnd(), m_offsetMapSoundRangeDialog.getSoundRangeOffset());
	}

	public int addFilesToSelectedGroup() {
		ItemPanel itemPanel = getSelectedItemPanel();
		
		if(itemPanel == null || !(itemPanel instanceof GroupPanel)) {
			return 0;
		}
		
		return addFilesToGroup((GroupPanel) itemPanel);
	}
	
	public int addFilesToGroup(GroupPanel groupPanel) {
		if(groupPanel == null) { return 0; }
		
		Group group = groupPanel.getGroup();
		if(group == null) { return 0; }
		
		JFileChooser fileChooser = new JFileChooser(SettingsManager.instance.previousGroupFileDirectory);
		fileChooser.setDialogTitle("Select Files to Add");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(true);
		if(fileChooser.showOpenDialog(m_frame) != JFileChooser.APPROVE_OPTION || fileChooser.getSelectedFiles().length == 0) { return 0; }
		
		SettingsManager.instance.previousGroupFileDirectory = Utilities.getFilePath(fileChooser.getCurrentDirectory());
		
		int numberOfFilesAdded = 0;
		String formattedFileName = null;
		boolean duplicateFile = false;
		DuplicateFileAction duplicateFileAction = DuplicateFileAction.Invalid;
		File[] selectedFiles = fileChooser.getSelectedFiles();
		Vector<String> truncatedFileNames = new Vector<String>();
		
		for(int i=0;i<selectedFiles.length;i++) {
			formattedFileName = Utilities.truncateFileName(selectedFiles[i].getName(), GroupFile.MAX_FILE_NAME_LENGTH);
			
			if(selectedFiles[i].getName().length() > GroupFile.MAX_FILE_NAME_LENGTH) {
				truncatedFileNames.add(selectedFiles[i].getName());
			}
			
			duplicateFile = group.hasFile(formattedFileName);
			
			if(duplicateFile) {
				if(duplicateFileAction != DuplicateFileAction.SkipAll && duplicateFileAction != DuplicateFileAction.ReplaceAll) {
					Object selection = JOptionPane.showInputDialog(m_frame, "File \"" + formattedFileName + "\" already exists, please choose an action:", "Duplicate File", JOptionPane.QUESTION_MESSAGE, null, DuplicateFileAction.getValidDisplayNames(), DuplicateFileAction.displayNames[DuplicateFileAction.defaultAction.ordinal()]);
					if(selection == null) { break; }
					
					duplicateFileAction = DuplicateFileAction.parseFrom(selection.toString());
				}
			}
			
			if(!duplicateFile || (duplicateFile && duplicateFileAction != DuplicateFileAction.Skip && duplicateFileAction != DuplicateFileAction.SkipAll)) {
				if(group.addFile(selectedFiles[i], duplicateFileAction == DuplicateFileAction.Replace || duplicateFileAction == DuplicateFileAction.ReplaceAll)) {
					numberOfFilesAdded++;
				}
				else {
					SystemConsole.instance.writeLine("Failed to add file " + formattedFileName + " to group" + (group.getFile() == null ? "." : " \"" + group.getFile().getName() + "\"."));
				}
			}
		}
		
		if(!truncatedFileNames.isEmpty()) {
			String message = "Truncated " + truncatedFileNames.size() + " file name" + (truncatedFileNames.size() == 1 ? "" : "s") + " to " + GroupFile.MAX_FILE_NAME_LENGTH + " characters:";
			
			for(int i = 0; i < truncatedFileNames.size(); i++) {
				message += "\n" + (i + 1) + ". '" + truncatedFileNames.elementAt(i) + "' => '" + Utilities.truncateFileName(truncatedFileNames.elementAt(i).toUpperCase().toUpperCase(), GroupFile.MAX_FILE_NAME_LENGTH) + "'";
			}
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "File Names Truncated", JOptionPane.WARNING_MESSAGE);
		}
		
		if(numberOfFilesAdded == 0) {
			String message = "Failed to add any files to group" + (group.getFile() == null ? "." : " \"" + group.getFile().getName() + "\".");
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Failed to Add Files", JOptionPane.ERROR_MESSAGE);
		}
		else if(numberOfFilesAdded != selectedFiles.length) {
			String message = "Only successfully added " + numberOfFilesAdded + " of " + selectedFiles.length + " selected files to group" + (group.getFile() == null ? "." : " \"" + group.getFile().getName() + "\".");
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Some Files Added", JOptionPane.WARNING_MESSAGE);
		}
		else {
			String message = "Successfully added all " + selectedFiles.length + " selected files to group" + (group.getFile() == null ? "." : " \"" + group.getFile().getName() + "\".");
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "All Files Added", JOptionPane.INFORMATION_MESSAGE);
		}
		
		return numberOfFilesAdded;
	}
	
	public int removeSelectedFilesFromSelectedGroup() {
		ItemPanel itemPanel = getSelectedItemPanel();
		
		if(itemPanel == null || !(itemPanel instanceof GroupPanel)) {
			return 0;
		}
		
		return removeSelectedFilesFromGroup((GroupPanel) itemPanel);
	}
	
	public int removeSelectedFilesFromGroup(GroupPanel groupPanel) {
		if(groupPanel == null) { return 0; }
		
		Vector<GroupFile> selectedGroupFiles = groupPanel.getSelectedFiles();
		if(selectedGroupFiles.isEmpty()) { return 0; }
		
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
		ItemPanel itemPanel = getSelectedItemPanel();
		
		if(itemPanel == null || !(itemPanel instanceof GroupPanel)) {
			return false;
		}
		
		return replaceSelectedFileInGroup((GroupPanel) itemPanel);
	}
	
	public boolean replaceSelectedFileInGroup(GroupPanel groupPanel) {
		if(groupPanel == null) { return false; }
		
		Vector<GroupFile> selectedGroupFiles = groupPanel.getSelectedFiles();
		if(selectedGroupFiles.size() != 1) { return false; }
		
		GroupFile selectedGroupFile = selectedGroupFiles.elementAt(0);
		
		JFileChooser fileChooser = new JFileChooser(SettingsManager.instance.previousGroupFileDirectory);
		fileChooser.setDialogTitle("Select Replacement File");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if(fileChooser.showOpenDialog(m_frame) != JFileChooser.APPROVE_OPTION) { return false; }
		if(fileChooser.getSelectedFile() == null || !fileChooser.getSelectedFile().isFile() || !fileChooser.getSelectedFile().exists()) { return false; }
		
		SettingsManager.instance.previousGroupFileDirectory = Utilities.getFilePath(fileChooser.getCurrentDirectory());
		
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

	public boolean renameSelectedFileInSelectedGroup() {
		ItemPanel itemPanel = getSelectedItemPanel();
		
		if(itemPanel == null || !(itemPanel instanceof GroupPanel)) {
			return false;
		}

		return renameSelectedFileInGroup((GroupPanel) itemPanel);
	}

	public boolean renameSelectedFileInGroup(GroupPanel groupPanel) {
		if(groupPanel == null) { return false; }
		
		Group group = groupPanel.getGroup();
		if(group == null) { return false; }
		
		Vector<GroupFile> selectedGroupFiles = groupPanel.getSelectedFiles();
		if(selectedGroupFiles.size() != 1) { return false; }
		
		GroupFile selectedGroupFile = selectedGroupFiles.elementAt(0);
		
		String input = null;
		String formattedInput = null;
		String newFileName = null;
		while(true) {
			input = JOptionPane.showInputDialog(m_frame, "Enter a new file name:", selectedGroupFile.getFileName());
			if(input == null) { return false; }
			
			formattedInput = input.trim();
			newFileName = formattedInput;
			
			if(formattedInput.length() > GroupFile.MAX_FILE_NAME_LENGTH) {
				newFileName = Utilities.truncateFileName(newFileName, GroupFile.MAX_FILE_NAME_LENGTH);
				
				int choice = JOptionPane.showConfirmDialog(m_frame, "New file name is longer than the maximum group file name length of " + GroupFile.MAX_FILE_NAME_LENGTH + ", and will be truncated to: \"" + newFileName + "\". Would you like to use this file name or try another?", "Filename Too Long", JOptionPane.YES_NO_CANCEL_OPTION);
				
					 if(choice == JOptionPane.CANCEL_OPTION) { return false; }
				else if(choice == JOptionPane.NO_OPTION)     { continue; }
			}
			
			if(newFileName.equals(selectedGroupFile.getFileName())) {
				return true;
			}
			
			if(group.hasFile(newFileName)) {
				int choice = JOptionPane.showConfirmDialog(m_frame, "A file with the name \"" + newFileName + "\" already exists, would you like to replace it or try another file name?", "Duplicate File Name", JOptionPane.YES_NO_CANCEL_OPTION);
				
				     if(choice == JOptionPane.CANCEL_OPTION) { return false; }
				else if(choice == JOptionPane.NO_OPTION)     { continue; }
				else if(choice == JOptionPane.YES_OPTION) {
					group.removeFile(newFileName);
					
					break;
				}
			}
			else {
				break;
			}
		}
		
		group.renameFile(selectedGroupFile, newFileName);
		
		return true;
	}

	public int extractSelectedFilesFromSelectedGroup() {
		return extractFilesFromSelectedGroup(true);
	}

	public int extractAllFilesFromSelectedGroup() {
		return extractFilesFromSelectedGroup(false);
	}

	public int extractFilesFromSelectedGroup(boolean selectedOnly) {
		ItemPanel itemPanel = getSelectedItemPanel();
		
		if(itemPanel == null || !(itemPanel instanceof GroupPanel)) {
			return 0;
		}

		return extractSelectedFilesFromGroup((GroupPanel) itemPanel);
	}

	public int extractSelectedFilesFromGroup(GroupPanel groupPanel) {
		return extractFilesFromGroup(groupPanel, true);
	}

	public int extractAllFilesFromGroup(GroupPanel groupPanel) {
		return extractFilesFromGroup(groupPanel, false);
	}

	public int extractFilesFromGroup(GroupPanel groupPanel, boolean selectedOnly) {
		if(groupPanel == null) { return 0; }

		Vector<GroupFile> groupFilesToExtract = null;

		if(selectedOnly) {
			groupFilesToExtract = groupPanel.getSelectedFiles();
		}
		else {
			groupFilesToExtract = groupPanel.getGroup().getFiles();
		}

		if(groupFilesToExtract.isEmpty()) { return 0; }
		
		JFileChooser fileChooser = new JFileChooser(SettingsManager.instance.previousExtractGroupFileDirectory);
		fileChooser.setDialogTitle("Extract Files");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if(fileChooser.showSaveDialog(m_frame) != JFileChooser.APPROVE_OPTION) { return 0; }
		if(fileChooser.getSelectedFile() == null) {
			String message = "No directory selected!";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Invalid Directory", JOptionPane.ERROR_MESSAGE);
			
			return 0;
		}
		
		SettingsManager.instance.previousExtractGroupFileDirectory = fileChooser.getCurrentDirectory().getPath();
		
		if(!fileChooser.getSelectedFile().exists()) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "The specified directory does not exist, create it?", "Non-Existent Directory", JOptionPane.YES_NO_CANCEL_OPTION);
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
		
		for(int i=0;i<groupFilesToExtract.size();i++) {
			try {
				if(groupFilesToExtract.elementAt(i).writeTo(fileChooser.getSelectedFile())) {
					numberOfFilesExtracted++;
				}
				else {
					SystemConsole.instance.writeLine("Failed to extract file \"" + groupFilesToExtract.elementAt(i).getFileName() + "\" to directory: \"" + fileChooser.getSelectedFile().getName() + "\".");
				}
			}
			catch(IOException e) {
				SystemConsole.instance.writeLine("Exception thrown while extracting file \"" + groupFilesToExtract.elementAt(i).getFileName() + "\" to directory \"" + fileChooser.getSelectedFile().getName() + "\": " + e.getMessage());
			}
		}
		
		if(numberOfFilesExtracted == 0) {
			String message = "Failed to extract any files to directory: \"" + fileChooser.getSelectedFile().getName() + "\".";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Failed to Extract Files", JOptionPane.ERROR_MESSAGE);
		}
		else if(numberOfFilesExtracted != groupFilesToExtract.size()) {
			String message = "Only successfully extracted " + numberOfFilesExtracted + " of " + groupFilesToExtract.size() + " selected files to directory: \"" + fileChooser.getSelectedFile().getName() + "\".";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Some Files Extracted", JOptionPane.WARNING_MESSAGE);
		}
		else {
			String message = "Successfully extracted all " + groupFilesToExtract.size() + " selected files to directory: \"" + fileChooser.getSelectedFile().getName() + "\".";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "All Selected Files Extracted", JOptionPane.INFORMATION_MESSAGE);
		}
		
		return numberOfFilesExtracted;
	}
	
	public boolean importItemIntoSelectedItem() {
		ItemPanel itemPanel = getSelectedItemPanel();
		
		if(itemPanel == null) {
			return false;
		}
		
		if(itemPanel instanceof GroupPanel) {
			return importGroupInto((GroupPanel) itemPanel);
		}
		else if(itemPanel instanceof PalettePanel) {
			return importPaletteInto((PalettePanel) itemPanel);
		}
		
		return false;
	}
	
	public boolean importGroupInto(GroupPanel groupPanel) {
		if(groupPanel == null) { return false; }
		
		Group group = groupPanel.getGroup();
		if(group == null) { return false; }
		
		JFileChooser fileChooser = new JFileChooser(group.getFile() == null ? SettingsManager.instance.previousOpenDirectory : Utilities.getFilePath(group.getFile()));
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
		
		SettingsManager.instance.previousOpenDirectory = Utilities.getFilePath(fileChooser.getCurrentDirectory());
		
		File selectedFile = fileChooser.getSelectedFile();
		String extension = Utilities.getFileExtension(selectedFile.getName());
		
		Vector<FilePlugin> plugins = EditorPluginManager.instance.getPluginsForFileFormat(extension);
		if(plugins == null || plugins.isEmpty()) {
			String message = "No plugin found to import " + extension + " file type. Perhaps you forgot to load all plugins?";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Plugin Found", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		FilePlugin plugin = EditorPluginManager.instance.getPreferredPluginPrompt(extension);
		if(plugin == null || !(plugin instanceof GroupPlugin)) { return false; }
		
		Item newItem = null;
		try { newItem = plugin.getNewItemInstance(selectedFile); }
		catch(ItemInstantiationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		if(newItem == null || !((newItem instanceof Group))) {
			String message = "Failed to instantiate \"" + plugin.getName() + " (" + plugin.getSupportedFileFormatsAsString() + ")\" plugin when attempting to import group file: \"" + selectedFile.getName() + "\".";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		Group importedGroup = (Group) newItem;
		
		try {
			if(!importedGroup.load()) {
				String message = "Failed to import group: \"" + selectedFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedFileFormatsAsString() + ")\".";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Group Loading Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		catch(HeadlessException e) {
			String message = "Exception thrown while importing group : \"" + selectedFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedFileFormatsAsString() + "): " + e.getMessage();
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Group Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		catch(ItemReadException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Group Importing Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		catch(DeserializationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Group Deserialization Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(!importedGroup.verifyAllFiles()) {
			String message = "Found one or more invalid files when importing group: \"" + selectedFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedFileFormatsAsString() + ").";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Group Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		group.addFiles(importedGroup);
		
		SystemConsole.instance.writeLine("Group file \"" + selectedFile.getName() +  "\" imported successfully!");
		
		update();
		
		return true;
	}
	
	public boolean importPaletteInto(PalettePanel palettePanel) {
		if(palettePanel == null) { return false; }
		
		Palette palette = palettePanel.getPalette();
		if(palette == null) { return false; }
		
		JFileChooser fileChooser = new JFileChooser(palette.getFile() == null ? System.getProperty("user.dir") : Utilities.getFilePath(palette.getFile()));
		fileChooser.setDialogTitle("Import Palette File");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if(fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) { return false; }
		if(!fileChooser.getSelectedFile().isFile() || !fileChooser.getSelectedFile().exists()) {
			String message = "Selected palette file \"" + fileChooser.getSelectedFile().getName() + "\" is not a file or does not exist.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Invalid or Missing File", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		File selectedFile = fileChooser.getSelectedFile();
		String extension = Utilities.getFileExtension(selectedFile.getName());
		
		FilePlugin plugin = EditorPluginManager.instance.getPreferredPluginPrompt(extension);
		if(plugin == null || !(plugin instanceof PalettePlugin)) { return false; }
		
		Item newItem = null;
		try { newItem = plugin.getNewItemInstance(selectedFile); }
		catch(ItemInstantiationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		if(newItem == null || !(newItem instanceof Palette)) {
			String message = "Failed to instantiate \"" + plugin.getName() + " (" + plugin.getSupportedFileFormatsAsString() + ")\" plugin when attempting to import palette file: \"" + selectedFile.getName() + "\".";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		Palette importedPalette = (Palette) newItem;
		
		try {
			if(!importedPalette.load()) {
				String message = "Failed to import palette: \"" + selectedFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedFileFormatsAsString() + ")\".";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		catch(HeadlessException e) {
			String message = "Exception thrown while importing palette : \"" + selectedFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedFileFormatsAsString() + "): " + e.getMessage();
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		catch(ItemReadException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Palette Importing Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		catch(DeserializationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Palette Deserialization Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		int importedPaletteIndex = 0;
		if(importedPalette.numberOfPalettes() > 1) {
			if(palette.numberOfPalettes() == importedPalette.numberOfPalettes()) {
				int choice = JOptionPane.showConfirmDialog(m_frame, "The palette you are importing has the same number of sub palettes, would you like to import all sub palettes?", "Import All Sub-Palettes", JOptionPane.YES_NO_CANCEL_OPTION);
				if(choice == JOptionPane.CANCEL_OPTION) { return false; }
				if(choice == JOptionPane.YES_OPTION) { importedPaletteIndex = -1; }
			}
			
			if(importedPaletteIndex != -1) {
				Vector<String> importPaletteDescriptions = importedPalette.getPaletteDescriptions();
				Object choices[] = new Object[importPaletteDescriptions.size()];
				for(int i=0;i<importPaletteDescriptions.size();i++) {
					choices[i] = new String((i+1) + ": " + importPaletteDescriptions.elementAt(i));
				}
				Object value = JOptionPane.showInputDialog(m_frame, "Choose a sub palette to import from:", "Choose Sub-Palette Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
				if(value == null) { return false; }
				for(int i=0;i<choices.length;i++) {
					if(choices[i] == value) {
						importedPaletteIndex = i;
						break;
					}
				}
				if(importedPaletteIndex < 0 || importedPaletteIndex >= importPaletteDescriptions.size()) { return false; }
			}
		}
		
		int localPaletteIndex = 0;
		if(importedPaletteIndex == -1) {
			localPaletteIndex = -1;
		}
		else {
			if(palette.numberOfPalettes() > 1) {
				Vector<String> localPaletteDescriptions = palette.getPaletteDescriptions();
				Object choices[] = new Object[localPaletteDescriptions.size()];
				for(int i=0;i<localPaletteDescriptions.size();i++) {
					choices[i] = new String((i+1) + ": " + localPaletteDescriptions.elementAt(i));
				}
				Object value = JOptionPane.showInputDialog(m_frame, "Choose a sub palette to import to:", "Choose Sub-Palette Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
				if(value == null) { return false; }
				for(int i=0;i<choices.length;i++) {
					if(choices[i] == value) {
						localPaletteIndex = i;
						break;
					}
				}
				if(localPaletteIndex < 0 || localPaletteIndex >= localPaletteDescriptions.size()) { return false; }
			}
		}
		
		Color importedColourData[] = importedPaletteIndex == -1 ? importedPalette.getAllColourData() : importedPalette.getColourData(importedPaletteIndex);
		
		boolean importSuccessful = false;
		if(localPaletteIndex == -1) {
			importSuccessful = palette.updateAllColourData(importedColourData);
		}
		else {
			importSuccessful = palette.updateColourData(localPaletteIndex, 0, importedColourData);
		}
		
		if(importSuccessful) {
			palettePanel.setChanged(true);
			
			SystemConsole.instance.writeLine("Palette file \"" + selectedFile.getName() +  "\" imported successfully!");
			
			update();
		}
		else {
			SystemConsole.instance.writeLine("Failed to import palette file \"" + selectedFile.getName() +  "\" imported successfully!");
		}
		
		return importSuccessful;
	}
	
	public boolean exportSelectedItem() {
		ItemPanel itemPanel = getSelectedItemPanel();
		
		if(itemPanel == null) {
			return false;
		}
		
		if(itemPanel instanceof GroupPanel) {
			return exportGroup((GroupPanel) itemPanel);
		}
		else if(itemPanel instanceof PalettePanel) {
			return exportPalette((PalettePanel) itemPanel);
		}
		
		return false;
	}
	
	public boolean exportGroup(GroupPanel groupPanel) {
		if(groupPanel == null) { return false; }
		
		Group group = groupPanel.getGroup();
		
		Vector<GroupPlugin> loadedGroupPlugins = EditorPluginManager.instance.getGroupPluginsExcludingFileFormat(group.getFileExtension());
		
		if(loadedGroupPlugins.isEmpty()) {
			String message = "No group plugins found that support instantiation / exporting. Perhaps you forgot to load all plugins?";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Plugins", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		int pluginIndex = -1;
		Object choices[] = loadedGroupPlugins.toArray();
		Object value = JOptionPane.showInputDialog(m_frame, "Choose a group type to export to:", "Choose Group Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
		if(value == null) { return false; }
		for(int i=0;i<choices.length;i++) {
			if(choices[i] == value) {
				pluginIndex = i;
				break;
			}
		}
		if(pluginIndex < 0 || pluginIndex >= loadedGroupPlugins.size()) { return false; }
		
		Group newGroup = null;
		try {
			newGroup = (Group) loadedGroupPlugins.elementAt(pluginIndex).getNewItemInstance(null);
		}
		catch(ItemInstantiationException e) {
			String message = "Failed to create instance of export file: \"" + loadedGroupPlugins.elementAt(pluginIndex).getName() + " (" + loadedGroupPlugins.elementAt(pluginIndex).getSupportedFileFormatsAsString() + ")!";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(newGroup.numberOfFileTypes() > 1) {
			int fileTypeIndex = 0;
			ItemFileType fileTypes[] = newGroup.getFileTypes();
			Object selectedFileType = JOptionPane.showInputDialog(m_frame, "Choose a group file type to export to:", "Choose File Type", JOptionPane.QUESTION_MESSAGE, null, fileTypes, fileTypes[0]);
			if(selectedFileType == null) { return false; }
			for(int i=0;i<fileTypes.length;i++) {
				if(fileTypes[i] == selectedFileType) {
					fileTypeIndex = i;
					break;
				}
			}
			if(fileTypeIndex < 0 || fileTypeIndex >= newGroup.numberOfFileTypes()) { return false; }
			newGroup.setFileType(fileTypes[fileTypeIndex]);
		}
		
		JFileChooser fileChooser = new JFileChooser(group.getFile() == null ? SettingsManager.instance.previousSaveDirectory : Utilities.getFilePath(group.getFile()));
		fileChooser.setDialogTitle("Export Group File");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		String extension = newGroup.getFileType().getExtension();
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
		
		SettingsManager.instance.previousSaveDirectory = Utilities.getFilePath(fileChooser.getCurrentDirectory());
		
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
		catch(ItemWriteException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			return false;
		}
		
		return true;
	}
	
	public boolean exportPalette(PalettePanel palettePanel) {
		if(palettePanel == null) { return false; }
		
		Palette palette = palettePanel.getPalette();
		if(palette == null) { return false; }
		
		int paletteIndex = 0;
		if(palette.numberOfPalettes() > 1) {
			if(palette.numberOfPalettes() == palette.numberOfPalettes()) {
				int choice = JOptionPane.showConfirmDialog(m_frame, "The palette you are exporting has multple sub palettes, would you like to export all of them?", "Export All Sub-Palettes", JOptionPane.YES_NO_CANCEL_OPTION);
				if(choice == JOptionPane.CANCEL_OPTION) { return false; }
				if(choice == JOptionPane.YES_OPTION) { paletteIndex = -1; }
			}
			
			if(paletteIndex != -1) {
				Vector<String> selectedPaletteDescriptions = palette.getPaletteDescriptions();
				Object choices[] = new Object[selectedPaletteDescriptions.size()];
				for(int i=0;i<selectedPaletteDescriptions.size();i++) {
					choices[i] = new String((i+1) + ": " + selectedPaletteDescriptions.elementAt(i));
				}
				Object value = JOptionPane.showInputDialog(m_frame, "Choose a sub palette to export:", "Choose Sub-Palette Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
				if(value == null) { return false; }
				for(int i=0;i<choices.length;i++) {
					if(choices[i] == value) {
						paletteIndex = i;
						break;
					}
				}
				if(paletteIndex < 0 || paletteIndex >= selectedPaletteDescriptions.size()) { return false; }
			}
		}
		
		Vector<PalettePlugin> loadedPalettePlugins = Editor.pluginManager.getPalettePluginsExcludingFileFormat(palette.getFileExtension());
		if(loadedPalettePlugins.isEmpty()) {
			String message = "No palette plugins found that support instantiation / exporting. Perhaps you forgot to load all plugins?";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Plugins", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}

		Palette newPalette = null;
		int useSameExportSettings = -1;
		int pluginIndex = -1;
		int fileTypeIndex = 0;
		int numberOfPalettesExported = 0;
		int currentPaletteIndex = paletteIndex < 0 ? 0 : paletteIndex;
		while(true) {
			if(useSameExportSettings <= 0) {
				pluginIndex = -1;
				Object choices[] = loadedPalettePlugins.toArray();
				Object value = JOptionPane.showInputDialog(m_frame, "Choose a palette type to export to:", "Choose Palette Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
				if(value == null) { return false; }
				for(int i=0;i<choices.length;i++) {
					if(choices[i] == value) {
						pluginIndex = i;
						break;
					}
				}
				if(pluginIndex < 0 || pluginIndex >= loadedPalettePlugins.size()) { return false; }
				
				try {
					newPalette = (Palette) loadedPalettePlugins.elementAt(pluginIndex).getNewItemInstance(null);
				}
				catch(ItemInstantiationException e) {
					String message = "Failed to create instance of export file: \"" + loadedPalettePlugins.elementAt(pluginIndex).getName() + " (" + loadedPalettePlugins.elementAt(pluginIndex).getSupportedFileFormatsAsString() + ")!";
					
					SystemConsole.instance.writeLine(message);
					
					JOptionPane.showMessageDialog(m_frame, message, "Instantiation Failed", JOptionPane.ERROR_MESSAGE);
					
					return false;
				}

				if(newPalette.numberOfFileTypes() > 1) {
					fileTypeIndex = 0;
					ItemFileType fileTypes[] = newPalette.getFileTypes();
					Object selectedFileType = JOptionPane.showInputDialog(m_frame, "Choose a palette file type to export to:", "Choose File Type", JOptionPane.QUESTION_MESSAGE, null, fileTypes, fileTypes[0]);
					if(selectedFileType == null) { return false; }
					for(int i=0;i<fileTypes.length;i++) {
						if(fileTypes[i] == selectedFileType) {
							fileTypeIndex = i;
							break;
						}
					}
					if(fileTypeIndex < 0 || fileTypeIndex >= newPalette.numberOfFileTypes()) { return false; }
					newPalette.setFileType(fileTypes[fileTypeIndex]);
				}
			}
			
			if(palette.numberOfPalettes() > 1 && useSameExportSettings < 0 && paletteIndex == -1) {
				int choice = JOptionPane.showConfirmDialog(m_frame, "Would you like to use the same export settings for all sub-palettes?", "Use Same Export Settings?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(choice == JOptionPane.YES_OPTION) { useSameExportSettings = 1; }
				else if(choice == JOptionPane.NO_OPTION) { useSameExportSettings = 0; }
			}
			
			JFileChooser fileChooser = new JFileChooser(palette.getFile() == null ? SettingsManager.instance.previousSaveDirectory : Utilities.getFilePath(palette.getFile()));
			fileChooser.setDialogTitle("Export Palette File");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setMultiSelectionEnabled(false);
			String extension = newPalette.getFileType().getExtension();
			if(palette.getFile() != null) {
				String fileName = palette.getFile().getName();
				fileChooser.setSelectedFile(new File(Utilities.getFileNameNoExtension(fileName) + (palette.numberOfPalettes() > 1 ? "_" + (currentPaletteIndex + 1) : (Utilities.compareCasePercentage(fileName) < 0 ? "_copy" : "_COPY"))  + "." + (Utilities.compareCasePercentage(fileName) < 0 ? extension.toLowerCase() : extension.toUpperCase())));
			}
			else {
				fileChooser.setSelectedFile(new File("NEW" + (palette.numberOfPalettes() > 1 ? "_" + (currentPaletteIndex + 1) : "") +  "." + extension));
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
			
			SettingsManager.instance.previousSaveDirectory = Utilities.getFilePath(fileChooser.getCurrentDirectory());
			
			newPalette.setFile(fileChooser.getSelectedFile());
			
			if(!palettePanel.updatePaletteData()) {
				String message = "Failed to update palette data for source palette while attempting to export file: \"" + palette.getFile().getName() + "!";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Update Palette Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
			newPalette.updateColourData(palette.getColourData(currentPaletteIndex));
			try {
				if(newPalette.save()) {
					SystemConsole.instance.writeLine("Palette successfully exported to new file: " + newPalette.getFile().getName() + "!");
					
					numberOfPalettesExported++;
				}
				else {
					String message = "Failed to export palette!";
					
					SystemConsole.instance.writeLine(message);
					
					JOptionPane.showMessageDialog(m_frame, message, "Export Failed", JOptionPane.ERROR_MESSAGE);
					
					return false;
				}
			}
			catch(ItemWriteException e) {
				SystemConsole.instance.writeLine(e.getMessage());
				
				return false;
			}
			
			if(paletteIndex >= 0 || currentPaletteIndex >= palette.numberOfPalettes() - 1) {
				break;
			}
			
			currentPaletteIndex++;
		}
		
		if(numberOfPalettesExported > 1) {
			SystemConsole.instance.writeLine("Successfully exported " + numberOfPalettesExported + " sub-palettes from palette: \"" + palette.getFile().getName() + "\".");
		}
		
		return true;
	}
	
	public boolean closeSelectedItem() {
		return closeItem(getSelectedItemPanel());
	}
	
	public boolean closeItem(ItemPanel itemPanel) {
		if(itemPanel == null) { return false; }
		
		Component tabComponent = getTabComponentWith(itemPanel);
		if(tabComponent == null) { return false; }
		m_mainTabbedPane.setSelectedComponent(tabComponent);
		
		if(itemPanel.isChanged()) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "Would you like to save your changes?", "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
			if(choice == JOptionPane.CANCEL_OPTION) { return false; }
			if(choice == JOptionPane.YES_OPTION) {
				if(!saveSelectedItem()) {
					return false;
				}
			}
		}
		
		m_mainTabbedPane.remove(tabComponent);
		int indexOfGroup = m_itemPanels.indexOf(itemPanel);
		itemPanel.cleanup();
		m_itemPanels.remove(itemPanel);
		if(m_itemPanels.size() > 0) {
			m_mainTabbedPane.setSelectedComponent(getTabComponentWith(m_itemPanels.elementAt(indexOfGroup < m_itemPanels.size() ? indexOfGroup : indexOfGroup - 1)));
		}
		
		update();
		
		return true;
	}
	
	public boolean closeAllItems() {
		if(m_mainTabbedPane.getComponentCount() > 1) {
			m_mainTabbedPane.setSelectedComponent(m_mainTabbedPane.getComponent(m_mainTabbedPane.getComponentCount() - 2));
		}
		
		for(int i=m_itemPanels.size()-1;i>=0;i--) {
			if(!closeItem(m_itemPanels.elementAt(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	public GroupProcessor promptSelectGroupProcessor() {
		if(EditorPluginManager.instance.numberOfPlugins(GroupProcessorPlugin.class) == 0) {
			String message = "No group processor plugins loaded. You must have at least one group processor plugin loaded.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Group Processor Plugins Loaded", JOptionPane.ERROR_MESSAGE);
			
			return null;
		}
		
		Vector<GroupProcessorPlugin> loadedGroupProcessorPlugins = EditorPluginManager.instance.getPlugins(GroupProcessorPlugin.class);
		Object choices[] = loadedGroupProcessorPlugins.toArray();
		Object selectedChoice = JOptionPane.showInputDialog(m_frame, "Choose a group processor to use:", "Choose Group Processor", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
		if(selectedChoice == null) { return null; }
		
		GroupProcessorPlugin selectedGroupProcessorPlugin = (GroupProcessorPlugin) selectedChoice;
		
		GroupProcessor selectedGroupProcessor = null;
		
		try {
			selectedGroupProcessor = selectedGroupProcessorPlugin.getNewGroupProcessorInstance();
		}
		catch(GroupProcessorInstantiationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Group Processor Creation Failed", JOptionPane.ERROR_MESSAGE);
			
			return null;
		}
		
		return selectedGroupProcessor;
	}
	
	public boolean checkAndPromptForRecursion(File file) {
		if(file == null || !file.exists() || !file.isDirectory()) { return false; }
		
		return checkAndPromptForRecursion(new File[] { file });
	}
	
	public boolean checkAndPromptForRecursion(File files[]) {
		if(files == null || files.length == 0) { return false; }
		
		boolean hasSubDirectory = false;
		File contents[] = null;
		for(int i=0;i<files.length;i++) {
			if(files[i] == null || !files[i].exists() || !files[i].isDirectory()) { continue; }
			
			contents = files[i].listFiles();
			for(int j=0;j<contents.length;j++) {
				if(contents[j] == null || !contents[j].exists() || !contents[j].isDirectory()) { continue; }
				
				if(contents[j].isDirectory()) {
					hasSubDirectory = true;
					break;
				}
			}
		}
		
		boolean recursive = false;
		if(hasSubDirectory) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "Recursively process subdirectories?", "Recursive Processing", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(choice == JOptionPane.CANCEL_OPTION) { return false; }
			
			recursive = choice == JOptionPane.YES_OPTION;
		}
		
		return recursive;
	}
	
	public void promptRunGroupProcessorOnFiles() {
		GroupProcessor groupProcessor = promptSelectGroupProcessor();
		if(groupProcessor == null || !groupProcessor.initialize()) { return; }
		
		JFileChooser fileChooser = new JFileChooser(SettingsManager.instance.previousProcessingDirectory);
		fileChooser.setDialogTitle("Process Directory");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setMultiSelectionEnabled(true);
		if(fileChooser.showOpenDialog(m_frame) != JFileChooser.APPROVE_OPTION) { return; }
		
		SettingsManager.instance.previousProcessingDirectory = Utilities.getFilePath(fileChooser.getCurrentDirectory());
		
		File selectedFiles[] = fileChooser.getSelectedFiles();
		
		processGroups(selectedFiles, groupProcessor, checkAndPromptForRecursion(selectedFiles));
	}
	
	public void promptRunGroupProcessorOnOpenGroups() {
		GroupProcessor groupProcessor = promptSelectGroupProcessor();
		if(groupProcessor == null || !groupProcessor.initialize()) { return; }
		
		processOpenGroups(groupProcessor);
	}
	
	public void processGroups(final File[] files, final GroupProcessor groupProcessor) {
		processGroups(files, groupProcessor, GroupProcessor.DEFAULT_RECURSIVE);
	}
	
	public void processGroups(final File[] files, final GroupProcessor groupProcessor, final boolean recursive) {
		if(files == null || files.length == 0 || groupProcessor == null) { return; }
		
		int numberOfGroups = groupProcessor.numberOfGroupsInFiles(files, recursive);
		
		if(numberOfGroups > 0) {
			final Task task = new Task(numberOfGroups, m_progressDialog);
			
			Thread groupProcessorThread = new Thread(new Runnable() {
				public void run() {
					groupProcessor.processGroups(files, task, recursive);
				}
			});
			
			processGroupsHelper(task, groupProcessorThread);
		}
		else {
			SystemConsole.instance.writeLine("No groups files to process.");
		}
	}
	
	public void processGroups(final GroupCollection groups, final GroupProcessor groupProcessor) {
		if(groups == null || groupProcessor == null) { return; }
		
		processGroups(groups.getGroups(), groupProcessor);
	}
	
	public void processOpenGroups(final GroupProcessor groupProcessor) {
		if(groupProcessor == null) { return; }
		
		processGroups(getOpenGroups(), groupProcessor);
	}
	
	public void processGroups(final Vector<Group> groups, final GroupProcessor groupProcessor) {
		if(groups == null || groupProcessor == null) { return; }
		
		if(groups.size() > 0) {
			final Task task = new Task(groups.size(), m_progressDialog);
			
			Thread groupProcessorThread = new Thread(new Runnable() {
				public void run() {
					groupProcessor.processGroups(groups, task);
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
		
		m_progressDialog.display("Processing", "Processing groups...", 0, task.getTaskLength(), task, groupProcessorThread);
		
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
		
		ItemPanel itemPanel = null;
		
		for(int i=0;i<m_itemPanels.size();i++) {
			itemPanel = m_itemPanels.elementAt(i);
			
			if(itemPanel instanceof GroupPanel) {
				((GroupPanel) itemPanel).autoSort();
			}
			
			itemPanel.update();
		}
		
		m_updating = false;
	}
	
	public void updateWindow() {
		if(m_updating) { return; }
		
		m_updating = true;
		
		m_settingsAutoScrollConsoleMenuItem.setSelected(SettingsManager.instance.autoScrollConsole);
		m_settingsLogConsoleMenuItem.setSelected(SettingsManager.instance.logConsole);
		m_settingsSuppressUpdatesMenuItem.setSelected(SettingsManager.instance.suppressUpdates);
		m_pluginsAutoLoadMenuItem.setSelected(SettingsManager.instance.autoLoadPlugins);
		m_settingsAutoSaveSettingsMenuItem.setSelected(SettingsManager.instance.autoSaveSettings);
		
		ItemPanel selectedItemPanel = getSelectedItemPanel();
		MapPanel selectedMapPanel = (selectedItemPanel instanceof MapPanel) ? (MapPanel) selectedItemPanel : null;
		GroupPanel selectedGroupPanel = (selectedItemPanel instanceof GroupPanel) ? (GroupPanel) selectedItemPanel : null;
		Map selectedMap = selectedMapPanel == null ? null : selectedMapPanel.getMap();
		Group selectedGroup = selectedGroupPanel == null ? null : selectedGroupPanel.getGroup();
		boolean itemTabSelected = m_mainTabbedPane.getSelectedIndex() != m_mainTabbedPane.getTabCount() - 1;
		boolean groupHasFiles = selectedGroup != null && selectedGroup.numberOfFiles() != 0;
		m_fileSaveMenuItem.setEnabled(itemTabSelected);
		m_fileSaveAsMenuItem.setEnabled(itemTabSelected);
		m_fileSaveAllMenuItem.setEnabled(m_itemPanels.size() != 0);
		
		m_fileImportMenuItem.setEnabled(itemTabSelected);
		m_fileExportMenuItem.setEnabled(itemTabSelected);
		m_fileCloseMenuItem.setEnabled(itemTabSelected);
		m_fileCloseAllMenuItem.setEnabled(m_itemPanels.size() != 0);

		m_mapOffsetSoundRangeMenuItem.setEnabled(selectedMap != null);

		m_groupAddFilesMenuItem.setEnabled(selectedGroupPanel != null);
		m_groupRemoveFilesMenuItem.setText("Remove File" + (selectedGroupPanel != null && selectedGroupPanel.numberOfSelectedFiles() == 1 ? "" : "s"));
		m_groupExtractFilesMenuItem.setText("Extract File" + (selectedGroupPanel != null && selectedGroupPanel.numberOfSelectedFiles() == 1 ? "" : "s"));
		
		m_groupRemoveFilesMenuItem.setEnabled(selectedGroupPanel != null && selectedGroupPanel.numberOfSelectedFiles() != 0);
		m_groupReplaceFileMenuItem.setEnabled(selectedGroupPanel != null && selectedGroupPanel.numberOfSelectedFiles() == 1);
		m_groupRenameFileMenuItem.setEnabled(selectedGroupPanel != null && selectedGroupPanel.numberOfSelectedFiles() == 1);
		m_groupExtractFilesMenuItem.setEnabled(selectedGroupPanel != null && selectedGroupPanel.numberOfSelectedFiles() != 0);
		m_groupExtractAllFilesMenuItem.setEnabled(selectedGroup != null && selectedGroup.numberOfFiles() != 0);
		
		m_selectInverseMenuItem.setEnabled(groupHasFiles);
		m_selectRandomMenuItem.setEnabled(groupHasFiles);
		m_selectAllMenuItem.setEnabled(groupHasFiles);
		m_selectNoneMenuItem.setEnabled(groupHasFiles);
		
		m_sortAllGroupsMenuItem.setSelected(SettingsManager.instance.sortAllGroups);
		m_sortPerGroupSortingMenuItem.setSelected(!SettingsManager.instance.sortAllGroups);
		if(SettingsManager.instance.sortAllGroups || selectedGroup == null) {
			if(SettingsManager.instance.sortDirection.isValid()) {
				m_sortDirectionMenuItems[SettingsManager.instance.sortDirection.ordinal()].setSelected(true);
			}
			if(SettingsManager.instance.sortType.isValid()) {
				m_sortTypeMenuItems[SettingsManager.instance.sortType.ordinal()].setSelected(true);
			}
			m_sortAutoSortMenuItem.setSelected(SettingsManager.instance.autoSortFiles);
		}
		else {
			if(selectedGroup.getSortDirection().isValid()) {
				m_sortDirectionMenuItems[selectedGroup.getSortDirection().ordinal()].setSelected(true);
			}
			if(selectedGroup.getSortType().isValid()) {
				m_sortTypeMenuItems[selectedGroup.getSortType().ordinal()].setSelected(true);
			}
			m_sortAutoSortMenuItem.setSelected(selectedGroup.getAutoSortFiles());
		}
		m_sortManualSortMenuItem.setEnabled(selectedGroup == null ? false : selectedGroup.shouldSortFiles());
		
		int numberOfLoadedGroupProcessorPlugins = EditorPluginManager.instance.numberOfPlugins(GroupProcessorPlugin.class);
		
		m_processGroupFilesMenuItem.setEnabled(numberOfLoadedGroupProcessorPlugins != 0);
		m_processOpenGroupsMenuItem.setEnabled(numberOfLoadedGroupProcessorPlugins != 0 && m_itemPanels.size() != 0);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ItemPanel itemPanel = null;
				for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
					itemPanel = getItemPanelFrom(m_mainTabbedPane.getComponentAt(i));
					if(itemPanel == null) { continue; }
					
					m_mainTabbedPane.setTitleAt(i, itemPanel.getTabName());
					m_mainTabbedPane.setToolTipTextAt(i, itemPanel.getTabDescription());
				}
			}
		});
		
		updateLayout();
		
		m_updating = false;
		
		m_mainTabbedPane.revalidate();
	}
	
	public void updateLayout() {
		for(int i=0;i<m_itemPanels.size();i++) {
			m_itemPanels.elementAt(i).updateLayout();
		}
	}
	
	// update the server window
	public void update() {
		if(!m_initialized || m_updating) { return; }
		
		// update and automatically scroll to the end of the text
		m_consoleText.setText(Editor.console.toString());
		
		if(SettingsManager.instance.autoScrollConsole) {
			JScrollBar hScrollBar = m_consoleScrollPane.getHorizontalScrollBar();
			JScrollBar vScrollBar = m_consoleScrollPane.getVerticalScrollBar();
			
			if(!hScrollBar.getValueIsAdjusting() && !vScrollBar.getValueIsAdjusting()) {
				hScrollBar.setValue(hScrollBar.getMinimum());
				vScrollBar.setValue(vScrollBar.getMaximum());
			}
		}
		
		m_updating = true;
		
		for(int i=0;i<m_itemPanels.size();i++) {
			m_itemPanels.elementAt(i).update();
		}
		
		m_updating = false;
		
		updateWindow();
	}
	
	public void changeBackgroundColourPrompt() {
		Color newColour = JColorChooser.showDialog(null, "Choose background colour", SettingsManager.instance.backgroundColour);
		if(newColour == null) { return; }
		
		SettingsManager.instance.backgroundColour = newColour;
		
		update();
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
	
	public void changePixelButtonSizePrompt() {
		// prompt for pixel button size
		String input = JOptionPane.showInputDialog(m_frame, "Please enter the desired pixel button size:", SettingsManager.instance.pixelButtonSize);
		if(input == null) { return; }
		
		// set the new pixel button size
		int pixelButtonSize = -1;
		try {
			pixelButtonSize = Integer.parseInt(input);
		}
		catch(NumberFormatException e2) {
			JOptionPane.showMessageDialog(m_frame, "Invalid pixel button size entered.", "Invalid Number", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if(pixelButtonSize >= 1 && pixelButtonSize <= 64) {
			SystemConsole.instance.writeLine("Pixel button size changed from " + SettingsManager.instance.pixelButtonSize + " to " + pixelButtonSize + ".");
			
			SettingsManager.instance.pixelButtonSize = pixelButtonSize;
			
			update();
		}
		else {
			SystemConsole.instance.writeLine("Pixel button size must be between 1 and 64.");
		}
	}
	
	public void changePaletteSpacingPrompt() {
		// prompt for palette spacing
		String input = JOptionPane.showInputDialog(m_frame, "Please enter the distance for palette spacing:", SettingsManager.instance.paletteSpacing);
		if(input == null) { return; }
		
		// set the new palette spacing
		int paletteSpacing = -1;
		try {
			paletteSpacing = Integer.parseInt(input);
		}
		catch(NumberFormatException e2) {
			JOptionPane.showMessageDialog(m_frame, "Invalid palette spacing distance entered.", "Invalid Number", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if(paletteSpacing >= 0 && paletteSpacing <= 128) {
			SystemConsole.instance.writeLine("Palette spacing changed from " + SettingsManager.instance.paletteSpacing + " to " + paletteSpacing + ".");
			
			SettingsManager.instance.paletteSpacing = paletteSpacing;
			
			update();
		}
		else {
			SystemConsole.instance.writeLine("Palette spacing must be between 0 and 128.");
		}
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
		
		// create new item
		if(e.getSource() == m_fileNewMenuItem) {
			promptNewItem();
		}
		// load item
		else if(e.getSource() == m_fileOpenMenuItem) {
			promptLoadItems();
		}
		// save selected item
		else if(e.getSource() == m_fileSaveMenuItem) {
			saveSelectedItem();
		}
		// save selected item as new
		else if(e.getSource() == m_fileSaveAsMenuItem) {
			saveSelectedItemAsNew();
		}
		// save all items
		else if(e.getSource() == m_fileSaveAllMenuItem) {
			saveAllItems();
		}
		// import item
		else if(e.getSource() == m_fileImportMenuItem) {
			importItemIntoSelectedItem();
		}
		// export item
		else if(e.getSource() == m_fileExportMenuItem) {
			exportSelectedItem();
		}
		// close current item
		else if(e.getSource() == m_fileCloseMenuItem) {
			closeSelectedItem();
		}
		// close all items
		else if(e.getSource() == m_fileCloseAllMenuItem) {
			closeAllItems();
		}
		// close the program
		else if(e.getSource() == m_fileExitMenuItem) {
			close();
		}
		else if(e.getSource() == m_mapOffsetSoundRangeMenuItem) {
			try {
				Vector<Sound> offsetSounds = offsetSoundRangeInSelectedMap();

				if(offsetSounds != null) {
					JOptionPane.showMessageDialog(m_frame, "Offset " + offsetSounds.size() + " map sound" + (offsetSounds.size() == 1 ? "" : "s") + ".", "Sounds Offset", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			catch (IllegalArgumentException exception) {
				JOptionPane.showMessageDialog(m_frame, exception.getMessage(), "Sound Range Offsetting Failed", JOptionPane.ERROR_MESSAGE);
			}
			catch (SoundNumberUnderflowException exception) {
				JOptionPane.showMessageDialog(m_frame, exception.getMessage(), "Sound Range Offsetting Failed", JOptionPane.ERROR_MESSAGE);
			}
			catch (SoundNumberOverflowException exception) {
				JOptionPane.showMessageDialog(m_frame, exception.getMessage(), "Sound Range Offsetting Failed", JOptionPane.ERROR_MESSAGE);
			}
		}
		// add files to selected group
		else if(e.getSource() == m_groupAddFilesMenuItem) {
			addFilesToSelectedGroup();
		}
		// remove files from selected group
		else if(e.getSource() == m_groupRemoveFilesMenuItem) {
			removeSelectedFilesFromSelectedGroup();
		}
		// replace file in selected group
		else if(e.getSource() == m_groupReplaceFileMenuItem) {
			replaceSelectedFileInSelectedGroup();
		}
		// rename file in selected group
		else if(e.getSource() == m_groupRenameFileMenuItem) {
			renameSelectedFileInSelectedGroup();
		}
		// extract files from selected group
		else if(e.getSource() == m_groupExtractFilesMenuItem) {
			extractSelectedFilesFromSelectedGroup();
		}
		// extract all files from selected group
		else if(e.getSource() == m_groupExtractAllFilesMenuItem) {
			extractAllFilesFromSelectedGroup();
		}
		// inverse file selection
		else if(e.getSource() == m_selectInverseMenuItem) {
			ItemPanel selectedItemPanel = getSelectedItemPanel();
			if(selectedItemPanel == null || !(selectedItemPanel instanceof GroupPanel)) { return; }
			GroupPanel selectedGroupPanel = (GroupPanel) selectedItemPanel;
			if(selectedGroupPanel.getGroup().numberOfFiles() == 0) { return; }
			
			selectedGroupPanel.selectInverse();
		}
		// randomize file selection
		else if(e.getSource() == m_selectRandomMenuItem) {
			ItemPanel selectedItemPanel = getSelectedItemPanel();
			if(selectedItemPanel == null || !(selectedItemPanel instanceof GroupPanel)) { return; }
			GroupPanel selectedGroupPanel = (GroupPanel) selectedItemPanel;
			if(selectedGroupPanel.getGroup().numberOfFiles() == 0) { return; }
			
			selectedGroupPanel.selectRandom();
		}
		// select all files
		else if(e.getSource() == m_selectAllMenuItem) {
			ItemPanel selectedItemPanel = getSelectedItemPanel();
			if(selectedItemPanel == null || !(selectedItemPanel instanceof GroupPanel)) { return; }
			GroupPanel selectedGroupPanel = (GroupPanel) selectedItemPanel;
			if(selectedGroupPanel.getGroup().numberOfFiles() == 0) { return; }
			
			selectedGroupPanel.selectAll();
		}
		// clear file selection
		else if(e.getSource() == m_selectNoneMenuItem) {
			ItemPanel selectedItemPanel = getSelectedItemPanel();
			if(selectedItemPanel == null || !(selectedItemPanel instanceof GroupPanel)) { return; }
			GroupPanel selectedGroupPanel = (GroupPanel) selectedItemPanel;
			if(selectedGroupPanel.getGroup().numberOfFiles() == 0) { return; }
			
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
				ItemPanel selectedItemPanel = getSelectedItemPanel();
				if(selectedItemPanel == null || !(selectedItemPanel instanceof GroupPanel)) { return; }
				GroupPanel selectedGroupPanel = (GroupPanel) selectedItemPanel;
				
				selectedGroupPanel.getGroup().setAutoSortFiles(m_sortAutoSortMenuItem.isSelected());
			}
		}
		// prompt to run a group processor on a set of selected files and / or directories
		else if(e.getSource() == m_processGroupFilesMenuItem) {
			promptRunGroupProcessorOnFiles();
		}
		// prompt to run a group processor on currently open groups
		else if(e.getSource() == m_processOpenGroupsMenuItem) {
			promptRunGroupProcessorOnOpenGroups();
		}
		// change the plugins folder name
		else if(e.getSource() == m_settingsPluginDirectoryNameMenuItem) {
			// prompt for the plugin directory name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the plugin directory name:", SettingsManager.instance.pluginDirectoryName);
			if(input == null) { return; }
			
			String newPluginDirectoryName = input.trim();
			if(newPluginDirectoryName.isEmpty()) { return; }
			
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
			if(newConsoleLogFileName.isEmpty()) { return; }
			
			if(!newConsoleLogFileName.equalsIgnoreCase(SettingsManager.instance.consoleLogFileName)) {
				Editor.console.resetConsoleLogFileHeader();
				
				SettingsManager.instance.consoleLogFileName = newConsoleLogFileName;
			}
		}
		// change the log directory name
		else if(e.getSource() == m_settingsLogDirectoryNameMenuItem) {
			// prompt for the log directory name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the log directory name:", SettingsManager.instance.logDirectoryName);
			if(input == null) { return; }
			
			String newLogDirectoryName = input.trim();
			if(newLogDirectoryName.isEmpty()) { return; }
			
			if(!newLogDirectoryName.equalsIgnoreCase(SettingsManager.instance.logDirectoryName)) {
				SettingsManager.instance.logDirectoryName = newLogDirectoryName;
			}
		}
		// change the version file url setting
		else if(e.getSource() == m_settingsVersionFileURLMenuItem) {
			// prompt for the version file url
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the version file URL:", SettingsManager.instance.versionFileURL);
			if(input == null) { return; }
			
			String newVersionFileURL = input.trim();
			if(newVersionFileURL.isEmpty()) { return; }
			
			if(!newVersionFileURL.equalsIgnoreCase(SettingsManager.instance.versionFileURL)) {
				SettingsManager.instance.versionFileURL = newVersionFileURL;
			}
		}
		// prompt for a new background colour
		else if(e.getSource() == m_settingsBackgroundColourMenuItem) {
			changeBackgroundColourPrompt();
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
		// change update notification suppressing
		else if(e.getSource() == m_settingsSuppressUpdatesMenuItem) {
			SettingsManager.instance.suppressUpdates = m_settingsSuppressUpdatesMenuItem.isSelected();
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
		// display the preferred plugin editor dialog
		else if(e.getSource() == m_pluginsPreferredEditorMenuItem) {
			m_preferredPluginEditorDialog.display();
			
			if(m_preferredPluginEditorDialog.userSubmitted()) {
				SettingsManager.instance.save();
				if(SettingsManager.instance.save()) {
					SystemConsole.instance.writeLine("Successfully saved settings to file: " + SettingsManager.instance.settingsFileName);
				}
				else {
					SystemConsole.instance.writeLine("Failed to save settings to file: " + SettingsManager.instance.settingsFileName);
				}
			}
		}
		// display a list of loaded plugins
		else if(e.getSource() == m_pluginsListLoadedMenuItem) {
			EditorPluginManager.instance.displayPlugins();
		}
		// prompt for a plugin to load
		else if(e.getSource() == m_pluginsLoadMenuItem) {
			EditorPluginManager.instance.loadPluginPrompt();
		}
		// load all plugins
		else if(e.getSource() == m_pluginsLoadAllMenuItem) {
			EditorPluginManager.instance.loadPlugins();
		}
		// toggle auto-loading of plugins
		else if(e.getSource() == m_pluginsAutoLoadMenuItem) {
			SettingsManager.instance.autoLoadPlugins = m_pluginsAutoLoadMenuItem.isSelected();
			
			update();
		}
		// change the pixel button size
		else if(e.getSource() == m_buttonSizeMenuItem) {
			changePixelButtonSizePrompt();
		}
		// change the palette spacing
		else if(e.getSource() == m_paletteSpacingMenuItem) {
			changePaletteSpacingPrompt();
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
			JOptionPane.showMessageDialog(m_frame, "Ultimate Duke Nukem 3D Editor Version " + Editor.VERSION + "\nCreated by Kevin Scroggins (a.k.a. nitro_glycerine)\nE-Mail: nitro404@gmail.com\nWebsite: http://www.nitro404.com", "About Ultimate Duke 3D Editor", JOptionPane.INFORMATION_MESSAGE);
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
						ItemPanel selectedItemPanel = getSelectedItemPanel();
						if(selectedItemPanel == null || !(selectedItemPanel instanceof GroupPanel)) { return; }
						GroupPanel selectedGroupPanel = (GroupPanel) selectedItemPanel;
						
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
						ItemPanel selectedItemPanel = getSelectedItemPanel();
						if(selectedItemPanel == null || !(selectedItemPanel instanceof GroupPanel)) { return; }
						GroupPanel selectedGroupPanel = (GroupPanel) selectedItemPanel;
						
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
				saveItem(action.getSource());
				break;
				
			case SaveAs:
				saveItemAsNew(action.getSource());
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
				
			case RenameFile:
				renameSelectedFileInGroup(action.getSource());
				break;
				
			case ExtractFiles:
				extractSelectedFilesFromGroup(action.getSource());
				break;

			case ExtractAllFiles:
				extractAllFilesFromGroup(action.getSource());
				break;
				
			case Import:
				importGroupInto(action.getSource());
				break;
				
			case Export:
				exportGroup(action.getSource());
				break;
				
			case Close:
				closeItem(action.getSource());
				break;
				
			case CloseAll:
				closeAllItems();
				break;
				
			case DoNothing:
			case Invalid:
				return false;
		}
		
		return true;
	}
	
	public boolean handleMapAction(MapAction action) {
		if(!MapAction.isvalid(action)) { return false; }
		
		switch(action.getAction()) {
			case Save:
				saveItem(action.getSource());
				break;
				
			case SaveAs:
				saveItemAsNew(action.getSource());
				break;

			case Close:
				closeItem(action.getSource());
				break;
				
			case DoNothing:
			case Invalid:
				return false;
		}
		
		return true;
	}
	
	public boolean handlePaletteAction(PaletteAction action) {
		if(!PaletteAction.isvalid(action)) { return false; }
		
		switch(action.getAction()) {
			case Save:
				saveItem(action.getSource());
				break;
				
			case SaveAs:
				saveItemAsNew(action.getSource());
				break;
				
			case Import:
				importPaletteInto(action.getSource());
				break;
				
			case Export:
				exportPalette(action.getSource());
				break;
				
			case Close:
				closeItem(action.getSource());
				break;
				
			case DoNothing:
			case Invalid:
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
		
		for(int i=0;i<m_itemPanels.size();i++) {
			m_itemPanels.elementAt(i).updateLayout();
		}
	}
	
	public void componentShown(ComponentEvent e) { }
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	
	public void componentResized(ComponentEvent e) {
		updateLayout();
	}
	
	public void close() {
		if(!closeAllItems()) {
			return;
		}
		
		// reset initialization variables
		m_initialized = false;
		
		SettingsManager.instance.windowPositionX = m_frame.getX();
		SettingsManager.instance.windowPositionY = m_frame.getY();
		SettingsManager.instance.windowWidth = m_frame.getWidth();
		SettingsManager.instance.windowHeight = m_frame.getHeight();
		
		// close the server
		Editor.instance.close();
		
		m_frame.dispose();
		
		System.exit(0);
	}

}
