package palette;

import java.io.*;
import item.*;

public class PaletteDATPalette extends PaletteDAT {

	public static final String PALETTE_DAT_DESCRIPTION = "Normal";

	public static ItemFileType[] FILE_TYPES = {
		new ItemFileType("Duke Nukem 3D Palette DAT", "DAT")
	};

	public PaletteDATPalette() {
		this(null);
	}
	
	public PaletteDATPalette(File file) {
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
		return 1;
	}

	public String getPaletteDescription(int index) {
		if(index != 0) { return null; }

		return PALETTE_DAT_DESCRIPTION;
	}

}
