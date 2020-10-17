package palette;

import java.io.*;
import plugin.*;

public class PalettePlugin extends FilePlugin {
	
	public static final String PLUGIN_TYPE = "Palette";
	
	public PalettePlugin(String pluginName, String pluginVersion, String jarFileName, String configFileName, String directoryPath) {
		super(pluginName, pluginVersion, jarFileName, configFileName, directoryPath);
	}
	
	public String getType() {
		return PLUGIN_TYPE;
	}

	public static boolean isPalettePlugin(File file) {
		return Plugin.isPluginOfType(file, PLUGIN_TYPE);
	}
	
}
