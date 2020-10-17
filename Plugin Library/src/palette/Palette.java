package palette;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import exception.*;
import item.*;

public abstract class Palette extends Item {
	
	public static final int PALETTE_WIDTH = 16;
	public static final int PALETTE_HEIGHT = 16;
	public static final int NUMBER_OF_COLOURS = PALETTE_WIDTH * PALETTE_HEIGHT;

	public static final Palette DEFAULT_PALETTE = new GenericPalette();
	public static final Palette DEFAULT_LOOKUP = new GenericLookup();
	
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
	
	public abstract int getPaletteFileSize();
	
	public abstract int numberOfBytesPerPixel();
	
	public Color getPixel(int x, int y) {
		return getPixel(x, y, 0);
	}
	
	public abstract Color getPixel(int x, int y, int index);


	public Color lookupPixel(byte value) {
		return lookupPixel(value, 0);
	}

	public Color lookupPixel(byte value, int index) {
		int adjustedValue = value & 0xff;
		
		if(adjustedValue < 0 || adjustedValue >= Palette.NUMBER_OF_COLOURS) {
			return null;
		}

		return getPixel(adjustedValue % Palette.PALETTE_WIDTH, (int) Math.floor(adjustedValue / Palette.PALETTE_HEIGHT), index);
	}

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

	public IndexColorModel getIndexColourModel() {
		return getIndexColourModel(0, true);
	}

	public IndexColorModel getIndexColourModel(int index) {
		return getIndexColourModel(index, true);
	}

	public IndexColorModel getIndexColourModel(boolean transparent) {
		return getIndexColourModel(0, transparent);
	}

	public IndexColorModel getIndexColourModel(int index, boolean transparent) {
		int numberOfColours = PALETTE_HEIGHT * PALETTE_WIDTH;
		Color c = null;
		int pixelIndex = -1;
		byte r[] = new byte[numberOfColours];
		byte g[] = new byte[numberOfColours];
		byte b[] = new byte[numberOfColours];
		
		for(int j=0;j<PALETTE_HEIGHT;j++) {
			for(int i=0;i<PALETTE_WIDTH;i++) {
				pixelIndex = (j * PALETTE_WIDTH) + i;
				c = getPixel(i, j, index);
				
				r[pixelIndex] = (byte) c.getRed();
				g[pixelIndex] = (byte) c.getGreen();
				b[pixelIndex] = (byte) c.getBlue();
			}
		}
		
		return transparent
			? new IndexColorModel(8, numberOfColours, r, g, b, 255)
			: new IndexColorModel(8, numberOfColours, r, g, b);
	}
	
	public IndexColorModel[] getAllIndexColourModels(boolean transparent) {
		IndexColorModel palettes[] = new IndexColorModel[numberOfPalettes()];

		for(int i = 0; i < numberOfPalettes(); i++) {
			palettes[i] = getIndexColourModel(i, transparent);
		}
		
		return palettes;
	}
	
	public static boolean isValidIndexColourModel(IndexColorModel colourModel) {
		return colourModel != null &&
			   colourModel.getPixelSize() == 8 &&
			   (colourModel.getTransparentPixel() == -1 || colourModel.getTransparentPixel() == 255) &&
			   colourModel.getMapSize() == NUMBER_OF_COLOURS &&
			   colourModel.isValid();
	}
	
}
