package settings;

import java.awt.*;
import utilities.*;
import variable.*;
import group.*;

public class SettingsManager {
	
	public static SettingsManager instance = null;
	
	private VariableCollection m_settings;
	
	public String settingsFileName = defaultSettingsFileName;
	public String versionFileURL = defaultVersionFileURL;
	public String previousOpenDirectory;
	public String previousSaveDirectory;
	public String previousGroupFileDirectory;
	public String previousExtractDirectory;
	public String previousProcessingDirectory;
	public boolean sortAllGroups;
	public boolean autoSortFiles;
	public GroupFileSortType sortType;
	public SortDirection sortDirection;
	public boolean autoSaveSettings;
	public boolean autoLoadPlugins;
	public String pluginDirectoryName;
	public String consoleLogFileName;
	public String logDirectoryName;
	public boolean logConsole;
	public boolean supressUpdates;
	public int windowPositionX;
	public int windowPositionY;
	public int windowWidth;
	public int windowHeight;
	public boolean autoScrollConsole;
	public int maxConsoleHistory;
	
	public static final String defaultSettingsFileName = "Group Manager.ini";
	public static final String defaultVersionFileURL = "http://www.nitro404.com/version/duke3d_group_manager.xml";
	public static final String defaultOpenDirectory = System.getProperty("user.dir");
	public static final String defaultSaveDirectory = System.getProperty("user.dir");
	public static final String defaultGroupFileDirectory = System.getProperty("user.dir");
	public static final String defaultExtractDirectory = System.getProperty("user.dir");
	public static final String defaultProcessingDirectory = System.getProperty("user.dir");
	public static final boolean defaultSortAllGroups = false;
	public static final boolean defaultAutoSortFiles = true;
	public static final GroupFileSortType defaultSortType = GroupFileSortType.defaultSortType;
	public static final SortDirection defaultSortDirection = SortDirection.defaultDirection;
	public static final boolean defaultAutoSaveSettings = true;
	public static final boolean defaultAutoLoadPlugins = true;
	public static final String defaultPluginDirectoryName = "Plugins";
	public static final String defaultConsoleLogFileName = "Console.log";
	public static final String defaultLogDirectoryName = "Logs";
	public static final boolean defaultLogConsole = false;
	public static final boolean defaultSupressUpdates = false;
	public static final int defaultWindowPositionX = 0;
	public static final int defaultWindowPositionY = 0;
	public static final int defaultWindowWidth = 800;
	public static final int defaultWindowHeight = 600;
	public static final boolean defaultAutoScrollConsole = true;
	public static final int defaultMaxConsoleHistory = 512;
	
	public SettingsManager() {
		if(instance == null) {
			updateInstance();
		}
		
		m_settings = new VariableCollection();
		
		reset();
	}
	
	public void updateInstance() {
		instance = this;
	}
	
	public void reset() {
		versionFileURL = defaultVersionFileURL;
		previousOpenDirectory = defaultOpenDirectory;
		previousSaveDirectory = defaultSaveDirectory;
		previousGroupFileDirectory = defaultGroupFileDirectory;
		previousExtractDirectory = defaultExtractDirectory;
		previousProcessingDirectory = defaultProcessingDirectory;
		sortAllGroups = defaultSortAllGroups;
		autoSortFiles = defaultAutoSortFiles;
		sortType = defaultSortType;
		sortDirection = defaultSortDirection;
		autoSaveSettings = defaultAutoSaveSettings;
		autoLoadPlugins = defaultAutoLoadPlugins;
		pluginDirectoryName = defaultPluginDirectoryName;
		consoleLogFileName = defaultConsoleLogFileName;
		logDirectoryName = defaultLogDirectoryName;
		logConsole = defaultLogConsole;
		supressUpdates = defaultSupressUpdates;
		windowPositionX = defaultWindowPositionX;
		windowPositionY = defaultWindowPositionY;
		windowWidth = defaultWindowWidth;
		windowHeight = defaultWindowHeight;
		autoScrollConsole = defaultAutoScrollConsole;
		maxConsoleHistory = defaultMaxConsoleHistory;
	}
	
	public boolean load() { return loadFrom(settingsFileName); }
	
	public boolean save() { return saveTo(settingsFileName); }
	
	public boolean loadFrom(String fileName) {
		VariableCollection variables = VariableCollection.readFrom(fileName);
		if(variables == null) { return false; }
		
		m_settings = variables;
		
		int tempInt = -1;
		String tempString = null;
		Point tempPoint = null;
		Dimension tempDimension = null;
		
		// parse version file URL
		tempString = m_settings.getValue("Version File URL", "Paths");
		if(tempString != null) {
			versionFileURL = tempString;
		}
		
		// parse open directory path
		tempString = m_settings.getValue("Open Directory", "Paths");
		if(tempString != null) {
			previousOpenDirectory = tempString;
		}

		// parse save directory path
		tempString = m_settings.getValue("Save Directory", "Paths");
		if(tempString != null) {
			previousSaveDirectory = tempString;
		}

		// parse group file directory path
		tempString = m_settings.getValue("Group File Directory", "Paths");
		if(tempString != null) {
			previousGroupFileDirectory = tempString;
		}

		// parse extract directory path
		tempString = m_settings.getValue("Extract Directory", "Paths");
		if(tempString != null) {
			previousExtractDirectory = tempString;
		}
		
		// parse processing directory path
		tempString = m_settings.getValue("Open Directory", "Paths");
		if(tempString != null) {
			previousProcessingDirectory = tempString;
		}
		
		// parse sort all groups flag
		tempString = m_settings.getValue("Sort All Groups", "Sorting");
		if(tempString != null) {
			if(tempString.equalsIgnoreCase("true")) {
				sortAllGroups = true;
			}
			else if(tempString.equalsIgnoreCase("false")) {
				sortAllGroups = false;
			}
		}
		
		// parse auto-sort files flag
		tempString = m_settings.getValue("Auto-Sort Files", "Sorting");
		if(tempString != null) {
			if(tempString.equalsIgnoreCase("true")) {
				autoSortFiles = true;
			}
			else if(tempString.equalsIgnoreCase("false")) {
				autoSortFiles = false;
			}
		}

		// parse file sort type
		tempString = m_settings.getValue("Sort Type", "Sorting");
		if(tempString != null) {
			GroupFileSortType newSortType = GroupFileSortType.parseFrom(tempString);
			
			if(newSortType.isValid()) {
				sortType = newSortType;
			}
		}
		
		// parse file sort direction
		tempString = m_settings.getValue("Sort Direction", "Sorting");
		if(tempString != null) {
			SortDirection newSortDirection = SortDirection.parseFrom(tempString);
			
			if(newSortDirection.isValid()) {
				sortDirection = newSortDirection;
			}
		}
		
		// parse auto-save settings value
		tempString = m_settings.getValue("Auto-Save Settings", "Interface");
		if(tempString != null) {
			tempString = tempString.toLowerCase();
			if(tempString.equals("true")) {
				autoSaveSettings = true;
			}
			else if(tempString.equals("false")) {
				autoSaveSettings = false;
			}
		}
		
		// parse auto-load plugins value
		tempString = m_settings.getValue("Auto-Load Plugins", "Interface");
		if(tempString != null) {
			if(tempString.equalsIgnoreCase("true")) {
				autoLoadPlugins = true;
			}
			else if(tempString.equalsIgnoreCase("false")) {
				autoLoadPlugins = false;
			}
		}
		
		// parse plugin directory name
		tempString = m_settings.getValue("Plugin Directory Name", "Paths");
		if(tempString != null) {
			pluginDirectoryName = tempString;
		}
		
		// parse console log file name
		tempString = m_settings.getValue("Console Log File Name", "Paths");
		if(tempString != null) {
			consoleLogFileName = tempString;
		}
		
		// parse log directory name
		tempString = m_settings.getValue("Log Directory Name", "Paths");
		if(tempString != null) {
			logDirectoryName = tempString;
		}
		
		// parse log console value
		tempString = m_settings.getValue("Log Console", "Console");
		if(tempString != null) {
			if(tempString.equalsIgnoreCase("true")) {
				logConsole = true;
			}
			else if(tempString.equalsIgnoreCase("false")) {
				logConsole = false;
			}
		}
		
		// parse supress update notifications value
		tempString = m_settings.getValue("Supress Update Notifications", "Interface");
		if(tempString != null) {
			if(tempString.equalsIgnoreCase("true")) {
				supressUpdates = true;
			}
			else if(tempString.equalsIgnoreCase("false")) {
				supressUpdates = false;
			}
		}
		
		// parse window position
		tempPoint = Utilities.parsePoint(m_settings.getValue("Window Position", "Interface"));
		if(tempPoint != null && tempPoint.x > 0 && tempPoint.y > 0) {
			windowPositionX = tempPoint.x;
			windowPositionY = tempPoint.y;
		}
		
		// parse window size
		tempDimension = Utilities.parseDimension(m_settings.getValue("Window Size", "Interface"));
		if(tempDimension != null && tempDimension.width > 0 && tempDimension.height > 0) {
			windowWidth = tempDimension.width;
			windowHeight = tempDimension.height;
		}
		
		// parse console auto-scrolling
		tempString = m_settings.getValue("Auto-Scroll Console", "Console");
		if(tempString != null) {
			if(tempString.equalsIgnoreCase("true")) {
				autoScrollConsole = true;
			}
			else if(tempString.equalsIgnoreCase("false")) {
				autoScrollConsole = false;
			}
		}

		// parse max console history
		tempInt = -1;
		try { tempInt = Integer.parseInt(m_settings.getValue("Max Console History", "Console")); } catch(NumberFormatException e) { } 
		if(tempInt >= 1) { maxConsoleHistory = tempInt; }
		
		GroupPluginManager.instance.setPreferredPlugins(m_settings, true);
		
		return true;
	}
	
	public boolean saveTo(String fileName) {
		// update variables collection
		m_settings.setValue("Version File URL", versionFileURL, "Paths");
		m_settings.setValue("Open Directory", previousOpenDirectory, "Paths");
		m_settings.setValue("Save Directory", previousSaveDirectory, "Paths");
		m_settings.setValue("Group File Directory", previousGroupFileDirectory, "Paths");
		m_settings.setValue("Extract Directory", previousExtractDirectory, "Paths");
		m_settings.setValue("Processing Directory", previousProcessingDirectory, "Paths");
		m_settings.setValue("Sort All Groups", sortAllGroups, "Sorting");
		m_settings.setValue("Auto-Sort Files", autoSortFiles, "Sorting");
		m_settings.setValue("Sort Type", sortType.getDisplayName(), "Sorting");
		m_settings.setValue("Sort Direction", sortDirection.getDisplayName(), "Sorting");
		m_settings.setValue("Auto-Save Settings", autoSaveSettings, "Interface");
		m_settings.setValue("Auto-Load Plugins", autoLoadPlugins, "Interface");
		m_settings.setValue("Plugin Directory Name", pluginDirectoryName, "Paths");
		m_settings.setValue("Console Log File Name", consoleLogFileName, "Paths");
		m_settings.setValue("Log Directory Name", logDirectoryName, "Paths");
		m_settings.setValue("Log Console", logConsole, "Console");
		m_settings.setValue("Supress Update Notifications", supressUpdates, "Interface");
		m_settings.setValue("Window Position", windowPositionX + ", " + windowPositionY, "Interface");
		m_settings.setValue("Window Size", windowWidth + ", " + windowHeight, "Interface");
		m_settings.setValue("Auto-Scroll Console", autoScrollConsole, "Console");
		m_settings.setValue("Max Console History", maxConsoleHistory, "Console");
		m_settings.addVariables(GroupPluginManager.instance.getPreferredPluginsAsVariableCollection(), true);
		
		m_settings.sort();
		
		return m_settings.writeTo(fileName);
	}
	
}
