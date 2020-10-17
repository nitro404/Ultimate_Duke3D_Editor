package group;

import java.io.*;
import plugin.*;

public class GroupPlugin extends FilePlugin {
	
	public static final String PLUGIN_TYPE = "Group";
	
	public GroupPlugin(String pluginName, String pluginVersion, String jarFileName, String configFileName, String directoryPath) {
		super(pluginName, pluginVersion, jarFileName, configFileName, directoryPath);
	}
	
	public String getType() {
		return PLUGIN_TYPE;
	}

	public static boolean isGroupPlugin(File file) {
		return Plugin.isPluginOfType(file, PLUGIN_TYPE);
	}
	
}
