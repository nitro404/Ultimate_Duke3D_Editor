package palette;

import java.io.*;
import java.awt.*;
import item.*;
import exception.*;

public class GenericPalette extends Palette {
	
	protected byte m_data[];
	
	public static final int BPP = 3;
	public static final int PALETTE_SIZE_RGB = NUMBER_OF_COLOURS * BPP;
	public static final String PALETTE_DESCRIPTION = "Generic Palette";

	protected static final byte[] PALETTE_DATA = new byte[] {
		(byte)   0, (byte)   0, (byte)   0,  (byte)   4, (byte)   4, (byte)   4,  (byte)  16, (byte)  12, (byte)  16,  (byte)  24, (byte)  24, (byte)  24,  (byte)  36, (byte)  32, (byte)  32,  (byte)  44, (byte)  40, (byte)  44,  (byte)  56, (byte)  52, (byte)  52,  (byte)  64, (byte)  60, (byte)  60,  (byte)  76, (byte)  72, (byte)  72,  (byte)  88, (byte)  80, (byte)  80,  (byte)  96, (byte)  88, (byte)  92,  (byte) 108, (byte)  96, (byte) 100,  (byte) 116, (byte) 108, (byte) 112,  (byte) 128, (byte) 116, (byte) 120,  (byte) 136, (byte) 124, (byte) 128,  (byte) 148, (byte) 136, (byte) 140,
		(byte) 152, (byte) 140, (byte) 144,  (byte) 160, (byte) 148, (byte) 152,  (byte) 168, (byte) 156, (byte) 160,  (byte) 172, (byte) 164, (byte) 168,  (byte) 176, (byte) 172, (byte) 172,  (byte) 184, (byte) 176, (byte) 180,  (byte) 192, (byte) 184, (byte) 184,  (byte) 200, (byte) 188, (byte) 192,  (byte) 204, (byte) 196, (byte) 200,  (byte) 212, (byte) 204, (byte) 208,  (byte) 216, (byte) 212, (byte) 216,  (byte) 224, (byte) 216, (byte) 220,  (byte) 228, (byte) 224, (byte) 228,  (byte) 236, (byte) 232, (byte) 236,  (byte) 244, (byte) 240, (byte) 244,  (byte) 252, (byte) 252, (byte) 252,
		(byte)   0, (byte)   0, (byte)   0,  (byte)   4, (byte)   4, (byte)   0,  (byte)  12, (byte)   4, (byte)   4,  (byte)  24, (byte)  12, (byte)   4,  (byte)  32, (byte)  16, (byte)   8,  (byte)  40, (byte)  20, (byte)  12,  (byte)  48, (byte)  28, (byte)  16,  (byte)  60, (byte)  32, (byte)  20,  (byte)  68, (byte)  36, (byte)  24,  (byte)  76, (byte)  44, (byte)  24,  (byte)  84, (byte)  48, (byte)  28,  (byte)  96, (byte)  52, (byte)  32,  (byte) 104, (byte)  60, (byte)  36,  (byte) 112, (byte)  64, (byte)  40,  (byte) 120, (byte)  68, (byte)  44,  (byte) 132, (byte)  76, (byte)  48,
		(byte) 140, (byte)  84, (byte)  56,  (byte) 144, (byte)  92, (byte)  60,  (byte) 152, (byte) 100, (byte)  68,  (byte) 160, (byte) 108, (byte)  72,  (byte) 168, (byte) 116, (byte)  80,  (byte) 172, (byte) 124, (byte)  88,  (byte) 176, (byte) 132, (byte)  96,  (byte) 184, (byte) 144, (byte) 104,  (byte) 188, (byte) 152, (byte) 112,  (byte) 196, (byte) 160, (byte) 120,  (byte) 204, (byte) 172, (byte) 128,  (byte) 212, (byte) 176, (byte) 140,  (byte) 216, (byte) 184, (byte) 148,  (byte) 220, (byte) 196, (byte) 156,  (byte) 228, (byte) 204, (byte) 168,  (byte) 236, (byte) 216, (byte) 176,
		(byte)  80, (byte)  96, (byte) 148,  (byte)  84, (byte) 104, (byte) 152,  (byte)  92, (byte) 112, (byte) 160,  (byte)  96, (byte) 120, (byte) 168,  (byte) 104, (byte) 128, (byte) 172,  (byte) 112, (byte) 136, (byte) 180,  (byte) 120, (byte) 144, (byte) 188,  (byte) 124, (byte) 152, (byte) 196,  (byte) 132, (byte) 164, (byte) 204,  (byte) 140, (byte) 172, (byte) 208,  (byte) 148, (byte) 176, (byte) 216,  (byte) 156, (byte) 184, (byte) 220,  (byte) 164, (byte) 196, (byte) 228,  (byte) 172, (byte) 204, (byte) 236,  (byte) 180, (byte) 212, (byte) 244,  (byte) 188, (byte) 220, (byte) 252,
		(byte)   0, (byte)   0, (byte)   0,  (byte)   4, (byte)   4, (byte)   4,  (byte)   4, (byte)   8, (byte)  16,  (byte)  12, (byte)  12, (byte)  24,  (byte)  16, (byte)  20, (byte)  32,  (byte)  20, (byte)  24, (byte)  44,  (byte)  24, (byte)  32, (byte)  52,  (byte)  32, (byte)  40, (byte)  64,  (byte)  36, (byte)  44, (byte)  72,  (byte)  40, (byte)  52, (byte)  80,  (byte)  44, (byte)  56, (byte)  92,  (byte)  52, (byte)  64, (byte) 100,  (byte)  56, (byte)  68, (byte) 108,  (byte)  60, (byte)  76, (byte) 120,  (byte)  64, (byte)  80, (byte) 128,  (byte)  72, (byte)  88, (byte) 140,
		(byte)   0, (byte)   0, (byte)   0,  (byte)   4, (byte)   4, (byte)   4,  (byte)  12, (byte)  12, (byte)   8,  (byte)  24, (byte)  20, (byte)  12,  (byte)  32, (byte)  32, (byte)  16,  (byte)  40, (byte)  40, (byte)  24,  (byte)  48, (byte)  48, (byte)  28,  (byte)  60, (byte)  56, (byte)  36,  (byte)  68, (byte)  64, (byte)  40,  (byte)  76, (byte)  76, (byte)  44,  (byte)  84, (byte)  84, (byte)  52,  (byte)  96, (byte)  92, (byte)  56,  (byte) 104, (byte) 100, (byte)  64,  (byte) 112, (byte) 108, (byte)  68,  (byte) 120, (byte) 116, (byte)  72,  (byte) 132, (byte) 128, (byte)  80,
		(byte) 140, (byte) 136, (byte)  84,  (byte) 148, (byte) 148, (byte)  84,  (byte) 152, (byte) 156, (byte)  88,  (byte) 156, (byte) 164, (byte)  92,  (byte) 160, (byte) 172, (byte)  96,  (byte) 164, (byte) 176, (byte)  96,  (byte) 168, (byte) 184, (byte) 100,  (byte) 172, (byte) 192, (byte) 104,  (byte) 172, (byte) 200, (byte) 104,  (byte) 172, (byte) 208, (byte) 108,  (byte) 176, (byte) 216, (byte) 108,  (byte) 176, (byte) 220, (byte) 112,  (byte) 176, (byte) 228, (byte) 112,  (byte) 176, (byte) 236, (byte) 116,  (byte) 176, (byte) 244, (byte) 116,  (byte) 176, (byte) 252, (byte) 120,
		(byte)   0, (byte)   0, (byte)   0,  (byte)  12, (byte)   0, (byte)   0,  (byte)  24, (byte)   4, (byte)   0,  (byte)  40, (byte)   4, (byte)   0,  (byte)  52, (byte)   8, (byte)   4,  (byte)  68, (byte)  12, (byte)   4,  (byte)  84, (byte)  16, (byte)   4,  (byte)  96, (byte)  20, (byte)   4,  (byte) 112, (byte)  24, (byte)   8,  (byte) 128, (byte)  28, (byte)   8,  (byte) 140, (byte)  32, (byte)  12,  (byte) 156, (byte)  36, (byte)  12,  (byte) 172, (byte)  40, (byte)  12,  (byte) 184, (byte)  44, (byte)  16,  (byte) 200, (byte)  48, (byte)  16,  (byte) 216, (byte)  52, (byte)  20,
		(byte) 100, (byte)  68, (byte)  24,  (byte) 104, (byte)  80, (byte)   4,  (byte) 116, (byte)  88, (byte)   4,  (byte) 128, (byte) 100, (byte)   4,  (byte) 140, (byte) 108, (byte)   8,  (byte) 148, (byte) 120, (byte)   8,  (byte) 160, (byte) 128, (byte)   8,  (byte) 172, (byte) 140, (byte)   8,  (byte) 176, (byte) 148, (byte)  16,  (byte) 188, (byte) 160, (byte)  16,  (byte) 200, (byte) 172, (byte)  16,  (byte) 212, (byte) 180, (byte)  20,  (byte) 216, (byte) 192, (byte)  20,  (byte) 228, (byte) 200, (byte)  20,  (byte) 240, (byte) 216, (byte)  20,  (byte) 252, (byte) 224, (byte)  24,
		(byte)   0, (byte)   0, (byte)   0,  (byte)   4, (byte)   4, (byte)   0,  (byte)   8, (byte)   8, (byte)   4,  (byte)  16, (byte)  12, (byte)   8,  (byte)  20, (byte)  16, (byte)  16,  (byte)  28, (byte)  20, (byte)  20,  (byte)  32, (byte)  28, (byte)  24,  (byte)  40, (byte)  32, (byte)  28,  (byte)  48, (byte)  36, (byte)  32,  (byte)  52, (byte)  44, (byte)  40,  (byte)  60, (byte)  48, (byte)  44,  (byte)  64, (byte)  52, (byte)  48,  (byte)  72, (byte)  60, (byte)  52,  (byte)  76, (byte)  64, (byte)  56,  (byte)  84, (byte)  68, (byte)  64,  (byte)  92, (byte)  76, (byte)  68,
		(byte) 100, (byte)  84, (byte)  76,  (byte) 108, (byte)  92, (byte)  80,  (byte) 120, (byte) 100, (byte)  88,  (byte) 128, (byte) 108, (byte)  96,  (byte) 140, (byte) 116, (byte) 104,  (byte) 148, (byte) 124, (byte) 112,  (byte) 156, (byte) 132, (byte) 120,  (byte) 168, (byte) 140, (byte) 124,  (byte) 172, (byte) 148, (byte) 132,  (byte) 180, (byte) 156, (byte) 140,  (byte) 192, (byte) 164, (byte) 148,  (byte) 200, (byte) 172, (byte) 156,  (byte) 212, (byte) 176, (byte) 164,  (byte) 216, (byte) 184, (byte) 172,  (byte) 224, (byte) 196, (byte) 176,  (byte) 236, (byte) 204, (byte) 184,
		(byte)  52, (byte)  28, (byte)   0,  (byte)  68, (byte)  40, (byte)   0,  (byte)  88, (byte)  48, (byte)   0,  (byte) 108, (byte)  56, (byte)   0,  (byte) 128, (byte)  64, (byte)   0,  (byte) 144, (byte)  72, (byte)   0,  (byte) 164, (byte)  80, (byte)   0,  (byte) 180, (byte)  84, (byte)   4,  (byte) 200, (byte)  92, (byte)   4,  (byte) 204, (byte) 104, (byte)  24,  (byte) 212, (byte) 116, (byte)  48,  (byte) 216, (byte) 132, (byte)  68,  (byte) 220, (byte) 144, (byte)  88,  (byte) 228, (byte) 160, (byte) 112,  (byte) 236, (byte) 172, (byte) 136,  (byte) 244, (byte) 192, (byte) 160,
		(byte) 216, (byte)  64, (byte)  24,  (byte) 216, (byte)  76, (byte)  28,  (byte) 220, (byte)  92, (byte)  40,  (byte) 224, (byte) 104, (byte)  48,  (byte) 228, (byte) 120, (byte)  56,  (byte) 232, (byte) 132, (byte)  64,  (byte) 236, (byte) 144, (byte)  72,  (byte) 240, (byte) 160, (byte)  80,  (byte) 240, (byte) 172, (byte)  92,  (byte) 240, (byte) 184, (byte) 100,  (byte) 244, (byte) 196, (byte) 116,  (byte) 244, (byte) 208, (byte) 124,  (byte) 248, (byte) 216, (byte) 140,  (byte) 248, (byte) 224, (byte) 148,  (byte) 248, (byte) 232, (byte) 164,  (byte) 252, (byte) 240, (byte) 172,
		(byte)   8, (byte)  16, (byte)  52,  (byte)   8, (byte)   8, (byte)  64,  (byte)  20, (byte)   8, (byte)  76,  (byte)  28, (byte)   8, (byte)  88,  (byte)  48, (byte)   8, (byte)  92,  (byte)  64, (byte)  16, (byte) 100,  (byte)  84, (byte)  20, (byte) 104,  (byte) 104, (byte)  24, (byte) 112,  (byte) 124, (byte)  24, (byte) 120,  (byte) 136, (byte)  32, (byte) 116,  (byte) 152, (byte)  32, (byte) 108,  (byte) 164, (byte)  24, (byte)  96,  (byte) 176, (byte)  40, (byte)  76,  (byte) 188, (byte)  44, (byte)  56,  (byte) 204, (byte)  48, (byte)  24,  (byte) 216, (byte)  52, (byte)  20,
		(byte)  68, (byte)  68, (byte)   0,  (byte) 164, (byte) 164, (byte)   0,  (byte) 252, (byte) 252, (byte)   0,  (byte)  68, (byte)   0, (byte)  68,  (byte) 164, (byte)   0, (byte) 164,  (byte) 252, (byte)   0, (byte) 252,  (byte)  88, (byte)   0, (byte)   0,  (byte) 172, (byte)   0, (byte)   0,  (byte) 252, (byte)   0, (byte)   0,  (byte)   0, (byte)  68, (byte)   0,  (byte)   0, (byte) 164, (byte)   0,  (byte)   0, (byte) 252, (byte)   0,  (byte)   0, (byte)   0, (byte)  68,  (byte)   0, (byte)   0, (byte) 164,  (byte)   0, (byte)   0, (byte) 252,  (byte) 252, (byte)   0, (byte) 252
	};

	public static ItemFileType[] FILE_TYPES = {
		new ItemFileType("Generic Palette", "GPL")
	};
	
	public GenericPalette() {
		this(null);
	}
	
	public GenericPalette(File file) {
		super(file);
		
		m_data = PALETTE_DATA;
	}

	public boolean isInstantiable() {
		return false;
	}
	
	public boolean isInitialized() {
		return m_data != null;
	}

	public int getPaletteFileSize() {
		return m_data == null ? 0 : m_data.length;
	}

	public int numberOfBytesPerPixel() {
		return BPP;
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
	
	public String getPaletteDescription(int index) {
		return index == 0 ? PALETTE_DESCRIPTION : null;
	}
	
	public Color getPixel(int x, int y, int index) {
		if(x < 0 || y < 0 || x > PALETTE_WIDTH - 1 || y > PALETTE_HEIGHT - 1 || index != 0) { return null; }
		
		// compute the offset in the palette data array, accounting for the the y position and x position
		// note that each pixel is 3 bytes (RGB), hence BBP (bytes per pixel)
		int offset = (y * PALETTE_HEIGHT * BPP) + (x * BPP);
		
		// convert each unsigned byte to an integer
		int r = m_data[offset    ] & 0xFF;
		int g = m_data[offset + 1] & 0xFF;
		int b = m_data[offset + 2] & 0xFF;
		
		return new Color(r < 0 ? 0 : (r > 255 ? 255 : r),  g < 0 ? 0 : (g > 255 ? 255 : g), b < 0 ? 0 : (b > 255 ? 255 : b));
	}
	
	public boolean updatePixel(int x, int y, Color c, int index) {
		return false;
	}

	public Color[] getColourData(int index) {
		if(index != 0) { return null; }
		
		// iterate over the entire palette and convert each piece of data to a Color object
		Color colourData[] = new Color[NUMBER_OF_COLOURS];
		for(int j=0;j<PALETTE_HEIGHT;j++) {
			for(int i=0;i<PALETTE_WIDTH;i++) {
				colourData[(j * PALETTE_WIDTH) + i] = getPixel(i, j);
			}
		}
		return colourData;
	}
	
	public boolean updateColourData(int index, int dataIndex, Color colourData[]) {
		return false;
	}

	public boolean fillWithColour(Color c, int index) {
		return false;
	}
	
	public boolean load() throws PaletteReadException {
		return false;
	}

	public boolean save() throws PaletteWriteException {
		return false;
	}
	
}
