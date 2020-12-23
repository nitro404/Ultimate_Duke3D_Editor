package palette;

import java.util.*;
import java.io.*;
import java.awt.*;
import item.*;
import exception.*;
import utilities.*;

public class PaletteDATPalette extends PaletteDAT {

	public static final String PALETTE_DAT_DESCRIPTION = "Main";
	public static final int NUMBER_OF_LOOKUPS_OFFSET = PALETTE_SIZE_RGB;
	public static final int LOOKUPS_OFFSET = NUMBER_OF_LOOKUPS_OFFSET + 2;

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

	public int getPaletteOffset() {
		return 0;
	}
	
	public short getNumberOfLookups() {
		return Serializer.deserializeShort(Arrays.copyOfRange(m_data, NUMBER_OF_LOOKUPS_OFFSET, NUMBER_OF_LOOKUPS_OFFSET + 2), Endianness.LittleEndian);
	}
	
	public boolean setNumberOfLookups(short numberOfLookups) {
		byte [] numberOfLookupsData = Serializer.serializeShort(numberOfLookups, Endianness.LittleEndian);
		
		for(int i = 0; i < 2; i ++) {
			m_data[PALETTE_SIZE_RGB + i] = numberOfLookupsData[i];
		}
		
		return true;
	}

	public boolean updatePixel(int x, int y, Color c, int index) {
		if(!super.updatePixel(x, y, c, index)) {
			return false;
		}
		
// TODO
		
		return true;
	}

	public boolean updateColourData(int index, int dataIndex, Color colourData[]) {
		if(!super.updateColourData(index, dataIndex, colourData)) {
			return false;
		}
		
// TODO
		
		return true;
	}

	public boolean updateAllColourData(Color colourData[]) {
		if(!super.updateAllColourData(colourData)) {
			return false;
		}
		
// TODO
		
		return true;
	}

	public boolean fillWithColour(Color c, int index) {
		if(!super.fillWithColour(c, index)) {
			return false;
		}
		
// TODO
		
		return true;
	}

	public boolean load() throws PaletteReadException {
		if(!super.load()) {
			return false;
		}
		
// TODO?
		
		int offset = LOOKUPS_OFFSET;
		for(int i = 0; i < getNumberOfLookups(); i++) {
			String s = "";

			for(int j = 0; j < 256; j++) {
				if(!s.isEmpty()) {
					s += ", ";
				}
				
				int value = m_data[offset++] & 0xFF;
				int numberLength = Utilities.intLength(value);
				
				for(int k = 0; k < 4 - numberLength; k++) {
					s += " ";
				}

				s += value;
			}
			
			System.out.println(s);
		}
		
		System.out.println();
		
		for(int i = 0; i < 256; i++) {
			String s = "";
			
			for(int j = 0; j < 256; j++) {
				if(!s.isEmpty()) {
					s += ", ";
				}
				
				int value = m_data[offset++] & 0xFF;
				int numberLength = Utilities.intLength(value);
				
				for(int k = 0; k < 4 - numberLength; k++) {
					s += " ";
				}

				s += value;
			}

			System.out.println(s);
		}
		
		return true;
	}
	
}
