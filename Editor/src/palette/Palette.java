package palette;

import java.util.*;
import java.io.*;
import java.awt.*;
import exception.*;
import item.*;

public abstract class Palette extends Item {
	
	public static final int PALETTE_WIDTH = 16;
	public static final int PALETTE_HEIGHT = 16;
	public static final int NUMBER_OF_COLOURS = PALETTE_WIDTH * PALETTE_HEIGHT;
	
	public Palette() {
		super();
	}

	public Palette(String fileName) {
		super(fileName);
	}

	public Palette(File file) {
		super(file);
	}

	public Palette(ItemFileType fileType) throws InvalidFileTypeException, UnsupportedFileTypeException {
		super(fileType);
	}

	public Palette(String fileName, ItemFileType fileType) throws InvalidFileTypeException, UnsupportedFileTypeException {
		super(fileName, fileType);
	}
	
	public Palette(File file, ItemFileType fileType) throws InvalidFileTypeException, UnsupportedFileTypeException {
		super(file, fileType);
	}

	public int numberOfPalettes() {
		return 1;
	}
	
	public abstract String getPaletteDescription(int index);
	
	public String getPaletteDescriptionsAsString() {
		String paletteDescriptions = new String();
		
		for(int i=0;i<numberOfPalettes();i++) {
			if(paletteDescriptions.length() < 0) {
				paletteDescriptions += ", ";
			}
			
			paletteDescriptions += getPaletteDescription(i);
		}
		
		return paletteDescriptions;
	}
	
	public Vector<String> getPaletteDescriptions() {
		Vector<String> paletteDescriptions = new Vector<String>();
		
		for(int i=0;i<numberOfPalettes();i++) {
			paletteDescriptions.add(getPaletteDescription(i));
		}
		
		return paletteDescriptions;
	}
	
	public boolean isInstantiable() {
		return true;
	}
	
	public Color getPixel(int x, int y) {
		return getPixel(0, x, y);
	}
	
	public abstract Color getPixel(int index, int x, int y);

	public boolean updatePixel(int x, int y, Color c) {
		return updatePixel(x, y, c, 0);
	}
	
	public abstract boolean updatePixel(int x, int y, Color c, int index);
	
	public Color[] getColourData() {
		return getColourData(0);
	}
	
	public abstract Color[] getColourData(int index);
	
	public Color[] getAllColourData() {
		return getColourData(0);
	}
	
	public boolean updateColourData(Color colourData[]) {
		return updateColourData(0, 0, colourData);
	}
	
	public boolean updateColourData(int index, Color colourData[]) {
		return updateColourData(index, 0, colourData);
	}
	
	public abstract boolean updateColourData(int index, int dataIndex, Color colourData[]);
	
	public boolean updateAllColourData(Color colourData[]) {
		return updateColourData(0, 0, colourData);
	}
	
	public boolean fillWithColour(Color c) {
		return fillWithColour(c, 0);
	}
	
	public abstract boolean fillWithColour(Color c, int index);
	
	public boolean fillAllWithColour(Color c) {
		return fillWithColour(c, -1);
	}
	
}
