package palette;

import java.io.*;
import item.*;

public class PaletteDATLookup extends PaletteDAT{

	public static final String LOOKUP_DAT_DESCRIPTION[] = { "Underwater", "Night Vision", "Title Screen", "3D Realms Logo", "Episode 1 Ending Animation" };

	public static ItemFileType[] FILE_TYPES = {
		new ItemFileType("Duke Nukem 3D Lookup DAT", "DAT")
	};

	public PaletteDATLookup() {
		this(null);
	}
	
	public PaletteDATLookup(File file) {
		super(file);
	}

	public ItemFileType getDefaultFileType() {
		return FILE_TYPES[0];
	}
	
	public String getDefaultFileExtension() {
		return FILE_TYPES[0].getExtension();
	}

	public ItemFileType[] getFileTypes() {
		return FILE_TYPES;
	}

	public int numberOfPalettes() {
// TODO:
return -1;
	}

	public String getPaletteDescription(int index) {
		if(index < 0 || index >= numberOfPalettes()) { return null; }

		return LOOKUP_DAT_DESCRIPTION[index];
	}

}
