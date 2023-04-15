package palette;

import java.io.*;
import java.awt.*;
import utilities.*;
import item.*;
import exception.*;

public class PalettePAL extends Palette {
	
	protected byte m_data[];
	
	public static final int BPP = 4;
	public static final int PALETTE_RIFF_OFFSET = 0; // 0
	public static final int PALETTE_PAL_TEXT_OFFSET = PALETTE_RIFF_OFFSET + 8; // 8
	public static final int PALETTE_DATA_TEXT_OFFSET = PALETTE_PAL_TEXT_OFFSET + 4; // 12
	public static final int PALETTE_VERSION_OFFSET = PALETTE_DATA_TEXT_OFFSET + 8; // 20
	public static final int PALETTE_NUMBER_OF_COLOURS_OFFSET = PALETTE_VERSION_OFFSET + 2; // 22
	public static final int PALETTE_COLOUR_OFFSET = PALETTE_NUMBER_OF_COLOURS_OFFSET + 2; // 24
	public static final int HEADER_SIZE = PALETTE_COLOUR_OFFSET;  // 24
	public static final int PALETTE_SIZE_RGB = PALETTE_WIDTH * PALETTE_HEIGHT * (BPP - 1); // 16x16x3 = 768
	public static final int PALETTE_SIZE_RGBA = PALETTE_WIDTH * PALETTE_HEIGHT * BPP; // 16x16x4 = 1024
	public static final String PALETTE_DESCRIPTION = "Default";
	public static final String HEADER_RIFF_TEXT = "RIFF";
	public static final String HEADER_PAL_TEXT = "PAL";
	public static final String HEADER_DATA_TEXT = "data";
	public static final short PALETTE_VERSION = 3;
	
	public static final byte BLANK_HEADER_DATA[] = new byte[] {
		0x52, 0x49, 0x46, 0x46, // "RIFF" (single-byte chars) (4 bytes)
		0x10, 0x4,  0x0,  0x0,  // blank (4 bytes)
		0x50, 0x41, 0x4c, 0x20, // "PAL " (single-byte chars) (4 bytes)
		0x64, 0x61, 0x74, 0x61, // "data" (single-byte chars) (4 bytes)
		0x4,  0x4,  0x0,  0x0,  // blank (4 bytes)
		0x0,  0x3,              // 3 (version number) (short) (2 bytes)
		0x0,  0x1               // 256 (16 x 16) (number of colours) (unsigned short) (2 bytes)
	};

	public static ItemFileType[] FILE_TYPES = {
		new ItemFileType("PAL Palette", "PAL")
	};
	
	public PalettePAL() {
		this(null);
	}
	
	public PalettePAL(File file) {
		super(file);
		m_data = null;
	}

	public boolean isInstantiable() {
		return true;
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
		if(!isInitialized() || x < 0 || y < 0 || x > PALETTE_WIDTH - 1 || y > PALETTE_HEIGHT - 1 || index != 0) { return null; }
		
		// compute the offset in the palette data array, accounting for the header size, the y position and x position
		// note that each pixel is 4 bytes (RGBA), hence BBP (bytes per pixel)
		int offset = PALETTE_COLOUR_OFFSET + (y * PALETTE_HEIGHT * BPP) + (x * BPP);
		
		// convert each unsigned byte to an integer
		int r = (m_data[offset    ] & 0xFF);
		int g = (m_data[offset + 1] & 0xFF);
		int b = (m_data[offset + 2] & 0xFF);
		int a = (m_data[offset + 3] & 0xFF);
		
		// check that each colour channel is between 0 and 255
		if(r < 0 || r > 255) { System.out.println(  "Red channel exceeded 0-255 boundary in PAL file \"" + m_file.getName() + "\" with value: " + r + " at offset: " + offset); }
		if(g < 0 || g > 255) { System.out.println("Green channel exceeded 0-255 boundary in PAL file \"" + m_file.getName() + "\" with value: " + g + " at offset: " + offset + 1); }
		if(b < 0 || b > 255) { System.out.println( "Blue channel exceeded 0-255 boundary in PAL file \"" + m_file.getName() + "\" with value: " + b + " at offset: " + offset + 2); }
		if(a < 0 || a > 255) { System.out.println("Alpha channel exceeded 0-255 boundary in PAL file \"" + m_file.getName() + "\" with value: " + r + " at offset: " + offset + 3); }
		
		return new Color(r < 0 ? 0 : (r > 255 ? 255 : r),  g < 0 ? 0 : (g > 255 ? 255 : g), b < 0 ? 0 : (b > 255 ? 255 : b), a < 0 ? 0 : (a > 255 ? 255 : a));
	}
	
	public boolean updatePixel(int x, int y, Color c, int index) {
		if(!isInitialized() || c == null || x < 0 || y < 0 || x > PALETTE_WIDTH - 1 || y > PALETTE_HEIGHT - 1 || index != 0) { return false; }
		
		// compute the offset in the palette data array, accounting for the header size, the y position and x position
		// note that each pixel is 4 bytes (RGBA), hence BBP (bytes per pixel)
		int offset = PALETTE_COLOUR_OFFSET + (y * PALETTE_HEIGHT * BPP) + (x * BPP);
		
		// convert each integer to a byte
		m_data[offset    ] = (byte) (c.getRed());
		m_data[offset + 1] = (byte) (c.getGreen());
		m_data[offset + 2] = (byte) (c.getBlue());
		m_data[offset + 3] = (byte) (c.getAlpha());
		
		return true;
	}

	public Color[] getColourData(int index) {
		if(index != 0 || !isInitialized()) { return null; }
		
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
		if(index != 0) { return false; }
		
		// verify that the colour data is not truncated
		int dataOffset = (dataIndex * NUMBER_OF_COLOURS);
		if(colourData.length - dataOffset < NUMBER_OF_COLOURS) { return false; }
		
		// if the palette is not already loaded / initialized, and is instantiable
		// initialize the data and set it to default values
		if(!isInitialized()) {
			if(isInstantiable()) {
				m_data = new byte[HEADER_SIZE + PALETTE_SIZE_RGBA];
				
				for(int i=0;i<BLANK_HEADER_DATA.length;i++) {
					m_data[i] = BLANK_HEADER_DATA[i];
				}
			}
			else {
				return false;
			}
		}
		
		// iterate over the entire data array and replace each byte with the bytes corresponding to each pixel
		// in the new colour data array
		int offset = 0;
		int pixelIndex = 0;
		for(int j=0;j<PALETTE_HEIGHT;j++) {
			for(int i=0;i<PALETTE_WIDTH;i++) {
				// compute the offsets for the data array and colour data array
				offset = PALETTE_COLOUR_OFFSET + (j * PALETTE_HEIGHT * BPP) + (i * BPP);
				pixelIndex = dataOffset + (j * PALETTE_WIDTH) + i;
				
				// convert each integer to a byte
				m_data[offset    ] = (byte) (colourData[pixelIndex].getRed());
				m_data[offset + 1] = (byte) (colourData[pixelIndex].getGreen());
				m_data[offset + 2] = (byte) (colourData[pixelIndex].getBlue());
				m_data[offset + 3] = (byte) (colourData[pixelIndex].getAlpha());
			}
		}
		
		return true;
	}

	public boolean fillWithColour(Color c, int index) {
		if(index > 0 || c == null) { return false; }
		
		// if the palette is not already loaded / initialized, and is instantiable
		// initialize the data and set it to default values
		if(!isInitialized()) {
			if(isInstantiable()) {
				m_data = new byte[HEADER_SIZE + PALETTE_SIZE_RGBA];
				
				for(int i=0;i<BLANK_HEADER_DATA.length;i++) {
					m_data[i] = BLANK_HEADER_DATA[i];
				}
			}
			else {
				return false;
			}
		}
		
		// iterate over the entire data array and replace each byte with the bytes corresponding to
		// the bytes that make up the replacement colour
		int offset = 0;
		for(int j=0;j<PALETTE_HEIGHT;j++) {
			for(int i=0;i<PALETTE_WIDTH;i++) {
				// compute the offset for the data array
				offset = PALETTE_COLOUR_OFFSET + (j * PALETTE_HEIGHT * BPP) + (i * BPP);
				
				// convert each integer to a byte
				m_data[offset    ] = (byte) (c.getRed());
				m_data[offset + 1] = (byte) (c.getGreen());
				m_data[offset + 2] = (byte) (c.getBlue());
				m_data[offset + 3] = (byte) ((c.getAlpha()));
			}
		}
		
		return true;
	}
	
	public boolean load() throws PaletteReadException {
		if(m_file == null || !m_file.exists()) { return false; }

		m_loading = true;
		
		// verify that the file has an extension
		String extension = Utilities.getFileExtension(m_file.getName());
		if(extension == null) {
			m_loading = false;
			throw new PaletteReadException("File " + m_file.getName() + " has no extension.");
		}

		// verify that the file extension is supported
		if(!hasFileTypeWithExtension(extension)) {
			m_loading = false;
			throw new PaletteReadException("File \"" + m_file.getName() +  "\" has unsupported extension: " + extension);
		}
		
		// check to make sure that the file is not too big to be stored in memory
		if(m_file.length() > Integer.MAX_VALUE) {
			m_loading = false;
			throw new PaletteReadException("File \"" + m_file.getName() +  "\" is too large to store in memory.");
		}
		
		// read the file into memory
		InputStream in = null;
		byte data[] = new byte[(int) m_file.length()];
		try {
			in = new FileInputStream(m_file);
			in.read(data);
			in.close();
		}
		catch(FileNotFoundException e) {
			m_loading = false;
			throw new PaletteReadException("File \"" + m_file.getName() +  "\" not found.");
		}
		catch(IOException e) {
			m_loading = false;
			throw new PaletteReadException("Error reading file \"" + m_file.getName() +  "\": " + e.getMessage());
		}
		
		// verify that the data is long enough to contain header information
		if(data.length <= PALETTE_NUMBER_OF_COLOURS_OFFSET + 2) {
			m_loading = false;
			throw new PaletteReadException("Palette file \"" + m_file.getName() + " is incomplete or corrupted.");
		}
		
		// verify that RIFF is specified in the header
		byte riffTextData[] = new byte[] { data[PALETTE_RIFF_OFFSET], data[PALETTE_RIFF_OFFSET + 1], data[PALETTE_RIFF_OFFSET + 2], data[PALETTE_RIFF_OFFSET + 3] };
		String riffText = Serializer.deserializeByteString(riffTextData);
		if(!riffText.trim().equalsIgnoreCase(HEADER_RIFF_TEXT)) {
			m_loading = false;
			throw new PaletteReadException("Palette file \"" + m_file.getName() +  "\" is not a valid format, missing " + HEADER_RIFF_TEXT + " specification in header.");
		}

		// verify that PAL is specified in the header
		byte palTextData[] = new byte[] { data[PALETTE_PAL_TEXT_OFFSET], data[PALETTE_PAL_TEXT_OFFSET + 1], data[PALETTE_PAL_TEXT_OFFSET + 2], data[PALETTE_PAL_TEXT_OFFSET + 3] };
		String palText = Serializer.deserializeByteString(palTextData);
		if(!palText.trim().equalsIgnoreCase(HEADER_PAL_TEXT)) {
			m_loading = false;
			throw new PaletteReadException("Palette file \"" + m_file.getName() +  "\" is not a valid format, missing " + HEADER_PAL_TEXT + " specification in header.");
		}
		
		// verify that data is specified in the header
		byte dataTextData[] = new byte[] { data[PALETTE_DATA_TEXT_OFFSET], data[PALETTE_DATA_TEXT_OFFSET + 1], data[PALETTE_DATA_TEXT_OFFSET + 2], data[PALETTE_DATA_TEXT_OFFSET + 3] };
		String dataText = Serializer.deserializeByteString(dataTextData);
		if(!dataText.trim().equalsIgnoreCase(HEADER_DATA_TEXT)) {
			m_loading = false;
			throw new PaletteReadException("Palette file \"" + m_file.getName() +  "\" is not a valid format, missing " + HEADER_DATA_TEXT + " specification in header.");
		}

		// verify that the data format version is correct
		byte versionData[] = new byte[] { data[PALETTE_VERSION_OFFSET], data[PALETTE_VERSION_OFFSET + 1] };
		short version = Serializer.deserializeShort(versionData);
		if(version != PALETTE_VERSION) {
			m_loading = false;
			throw new PaletteReadException("Palette file \"" + m_file.getName() + "\" is version " + version + ", only palettes with version " + PALETTE_VERSION + " are supported.");
		}
		
		// verify the number of colours (note that this is an unsigned short and will need to be properly converted)
		byte numberOfColoursData[] = new byte[] { data[PALETTE_NUMBER_OF_COLOURS_OFFSET + 1], data[PALETTE_NUMBER_OF_COLOURS_OFFSET] };
		short numberOfColours = Serializer.deserializeShort(numberOfColoursData);
		if(numberOfColours != NUMBER_OF_COLOURS) {
			m_loading = false;
			throw new PaletteReadException("Palette file \"" + m_file.getName() + "\" has " + numberOfColours + " colour" + (numberOfColours == 1 ? "" : "s") + ", only palettes with " + NUMBER_OF_COLOURS + " colours (" + PALETTE_WIDTH + " * " + PALETTE_HEIGHT + ") are supported.");
		}
		
		// verify that the data is not missing any information, and contains all required colour data
		if(data.length < HEADER_SIZE + PALETTE_SIZE_RGBA) {
			m_loading = false;
			throw new PaletteReadException("Palette file is corrupted or missing data, expected " + (HEADER_SIZE + PALETTE_SIZE_RGBA) + " bytes, found " + data.length + " bytes.");
		}
		
		// update the local memory to the data read in from file
		m_data = data;
		
		m_loading = false;
		
		return true;
	}

	public boolean save() throws PaletteWriteException {
		if(m_file == null) {
			return false;
		}
		
		// check that the palette has a file set and that the palette is loaded
		if(!isInitialized()) {
			throw new PaletteWriteException("Palette PAL has no data to save.");
		}
		
		// write the data to the specified file
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(m_file));
			out.write(m_data);
			out.close();
			
			return true;
		}
		catch(IOException e) {
			throw new PaletteWriteException("Error writing to file " + m_file.getName() +  ": " + e.getMessage());
		}
	}
	
}
