package palette;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import exception.*;
import item.*;
import utilities.*;

public class PaletteDAT extends Palette {
	
	protected byte m_data[];
	protected DATType m_type;
	
	public static final int BPP = 3;
	public static final int COLOUR_SCALE = 4;
	public static final int PALETTE_OFFSET[] = {0, 6426};
	public static final int PALETTE_SIZE_RGB = NUMBER_OF_COLOURS * BPP;
	public static final int NUMBER_OF_DAT_PALETTES[] = {1, 5};
	public static final String PALETTE_DAT_DESCRIPTION = "Normal";
	public static final String LOOKUP_DAT_DESCRIPTION[] = { "Underwater", "Night Vision", "Title Screen", "3D Realms Logo", "Episode 1 Ending Animation" };

	public static ItemFileType[] FILE_TYPES = {
		new ItemFileType("Duke Nukem 3D DAT Palette", "DAT")
	};
	
	public PaletteDAT() {
		this(null);
	}
	
	public PaletteDAT(File file) {
		super(file);
		m_data = null;
		m_type = DATType.Unknown;
	}

	public boolean isInstantiable() {
		return false;
	}
	
	public boolean isInitialized() {
		return m_data != null &&
			   m_type != DATType.Unknown;
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
	
	public int numberOfPalettes() {
		return m_type == DATType.Unknown ? -1 : NUMBER_OF_DAT_PALETTES[m_type.ordinal()];
	}
	
	public String getPaletteDescription(int index) {
		if(index < 0 || index >= numberOfPalettes()) { return null; }
		
		// return a description of the corresponding sub-palette for each DAT type
		if(m_type == DATType.Palette) {
			return PALETTE_DAT_DESCRIPTION;
		}
		else if(m_type == DATType.Lookup) {
			return LOOKUP_DAT_DESCRIPTION[index];
		}
		
		return null;
	}
	
	public DATType getDATType() {
		// return the type of DAT file (PALETTE / LOOKUP)
		return m_type;
	}
	
	public Color getPixel(int x, int y, int index) {
		if(!isInitialized() || x < 0 || y < 0 || x > PALETTE_WIDTH - 1 || y > PALETTE_HEIGHT - 1 || index < 0 || index >= numberOfPalettes()) { return null; }
		
		// calculate the offset in the palette data array
		// account for the offset in each DAT type,
		// the index representing the local sub palette,
		// the x and y position of the pixel
		// and the number of bytes per pixel (3)
		int offset = PALETTE_OFFSET[m_type.ordinal()] + (index * PALETTE_SIZE_RGB) + (y * PALETTE_HEIGHT * BPP) + (x * BPP);
		
		// convert each unsigned byte to an integer, scaled upwards by the colour scale (4)
		int r = (m_data[offset    ] & 0xFF) * COLOUR_SCALE;
		int g = (m_data[offset + 1] & 0xFF) * COLOUR_SCALE;
		int b = (m_data[offset + 2] & 0xFF) * COLOUR_SCALE;
		
		// check that each colour channel is between 0 and 255
		if(r < 0 || r > 255) { System.out.println(  "Red channel exceeded 0-255 boundary in DAT file \"" + m_file.getName() + "\" with value: " + r + " at offset: " + offset); }
		if(g < 0 || g > 255) { System.out.println("Green channel exceeded 0-255 boundary in DAT file \"" + m_file.getName() + "\" with value: " + g + " at offset: " + offset + 1); }
		if(b < 0 || b > 255) { System.out.println( "Blue channel exceeded 0-255 boundary in DAT file \"" + m_file.getName() + "\" with value: " + b + " at offset: " + offset + 2); }
		
		return new Color(r < 0 ? 0 : (r > 255 ? 255 : r),  g < 0 ? 0 : (g > 255 ? 255 : g), b < 0 ? 0 : (b > 255 ? 255 : b));
	}

	public boolean updatePixel(int x, int y, Color c, int index) {
		if(!isInitialized() || c == null || x < 0 || y < 0 || x > PALETTE_WIDTH - 1 || y > PALETTE_HEIGHT - 1 || index < 0 || index >= numberOfPalettes()) { return false; }
		
		// calculate the offset in the palette data array
		// account for the offset in each DAT type,
		// the index representing the selected local sub palette,
		// the x and y position of the pixel
		// and the number of bytes per pixel (3)
		int offset = PALETTE_OFFSET[m_type.ordinal()] + (index * PALETTE_SIZE_RGB) + (y * PALETTE_HEIGHT * BPP) + (x * BPP);
		
		// divide each colour channel by the colour scale (4) and then convert it to a byte
		m_data[offset    ] = (byte) (c.getRed() / COLOUR_SCALE);
		m_data[offset + 1] = (byte) (c.getGreen() / COLOUR_SCALE);
		m_data[offset + 2] = (byte) (c.getBlue() / COLOUR_SCALE);
		
		return true;
	}
	
	public Color[] getColourData(int index) {
		if(!isInitialized() || m_type == DATType.Unknown || index < 0 || index >= numberOfPalettes()) { return null; }
		
		// iterate over the data for the corresponding sub-palette and convert each pixel to a Color object
		Color colourData[] = new Color[NUMBER_OF_COLOURS];
		for(int j=0;j<PALETTE_HEIGHT;j++) {
			for(int i=0;i<PALETTE_WIDTH;i++) {
				colourData[(j * PALETTE_WIDTH) + i] = getPixel(i, j, index);
			}
		}
		return colourData;
	}
	
	public Color[] getAllColourData() {
		if(!isInitialized() || m_type == DATType.Unknown) { return null; }
		
		// iterate over the data for the all sub-palette and convert each pixel to a Color object
		Color colourData[] = new Color[NUMBER_OF_COLOURS * numberOfPalettes()];
		for(int p=0;p<numberOfPalettes();p++) {
			for(int j=0;j<PALETTE_HEIGHT;j++) {
				for(int i=0;i<PALETTE_WIDTH;i++) {
					colourData[(p * NUMBER_OF_COLOURS) + (j * PALETTE_WIDTH) + i] = getPixel(i, j, p);
				}
			}
		}
		return colourData;
	}
	
	public boolean updateColourData(int index, int dataIndex, Color colourData[]) {
		if(!isInitialized() || m_type == DATType.Unknown) { return false; }
		
		// verify that the colour data is not truncated
		int dataOffset = (dataIndex * NUMBER_OF_COLOURS);
		if(colourData.length - dataOffset < NUMBER_OF_COLOURS) { return false; }
		
		// iterate over the section of the data array corresponding to the specified sub-palette
		// and replace each pixel with the specified information in the new colour data array at the corresponding
		// offset to that of the sub-palette in the external colour data
		int offset = 0;
		int pixelIndex = 0;
		for(int j=0;j<PALETTE_HEIGHT;j++) {
			for(int i=0;i<PALETTE_WIDTH;i++) {
				// calculate the offset in the palette data array
				// account for the offset in each DAT type,
				// the index representing the selected local sub palette,
				// the x and y position of the pixel
				// and the number of bytes per pixel (3)
				offset = PALETTE_OFFSET[m_type.ordinal()] + (index * PALETTE_SIZE_RGB) + (j * PALETTE_HEIGHT * BPP) + (i * BPP);
				
				// calculate the index in the colour data array corresponding
				// to the pixel to be replaced in the local palette data array
				pixelIndex = dataOffset + (j * PALETTE_WIDTH) + i;
				
				// divide each colour channel by the colour scale and convert it to a byte
				m_data[offset    ] = (byte) (colourData[pixelIndex].getRed() / COLOUR_SCALE);
				m_data[offset + 1] = (byte) (colourData[pixelIndex].getGreen() / COLOUR_SCALE);
				m_data[offset + 2] = (byte) (colourData[pixelIndex].getBlue() / COLOUR_SCALE);
			}
		}
		
		return true;
	}
	
	public boolean updateAllColourData(Color colourData[]) {
		if(!isInitialized() || m_type == DATType.Unknown) { return false; }
		
		// verify that the colour data is not truncated
		if(colourData.length < NUMBER_OF_COLOURS * numberOfPalettes()) { return false; }
		
		// iterate over all local palette data for all sub-palettes and
		// replace it with the corresponding data in the new colour data array
		int offset = 0;
		int pixelIndex = 0;
		for(int p=0;p<numberOfPalettes();p++) {
			for(int j=0;j<PALETTE_HEIGHT;j++) {
				for(int i=0;i<PALETTE_WIDTH;i++) {
					// calculate the offset in the palette data array
					// account for the offset in each DAT type,
					// the index representing the current local sub palette,
					// the x and y position of the pixel
					// and the number of bytes per pixel (3)
					offset = PALETTE_OFFSET[m_type.ordinal()] + (p * PALETTE_SIZE_RGB) + (j * PALETTE_HEIGHT * BPP) + (i * BPP);
					
					// calculate the index in the colour data array corresponding
					// to the current pixel to be replaced in the local palette data array
					pixelIndex = (p * NUMBER_OF_COLOURS) + (j * PALETTE_WIDTH) + i;
					
					// divide each colour channel by the colour scale and convert it to a byte
					m_data[offset    ] = (byte) (colourData[pixelIndex].getRed() / COLOUR_SCALE);
					m_data[offset + 1] = (byte) (colourData[pixelIndex].getGreen() / COLOUR_SCALE);
					m_data[offset + 2] = (byte) (colourData[pixelIndex].getBlue() / COLOUR_SCALE);
				}
			}
		}
		
		return true;
	}

	public boolean fillWithColour(Color c, int index) {
		if(!isInitialized() || m_type == DATType.Unknown || c == null) { return false; }
		
		// iterate over all local palette data for all sub-palettes and
		// replace it with the corresponding colour value
		int offset = 0;
		for(int p=(index < 0 ? 0 : index);p<(index < 0 ? numberOfPalettes() : index + 1);p++) {
			for(int j=0;j<PALETTE_HEIGHT;j++) {
				for(int i=0;i<PALETTE_WIDTH;i++) {
					// calculate the offset in the palette data array
					// account for the offset in each DAT type,
					// the index representing the current local sub palette,
					// the x and y position of the pixel
					// and the number of bytes per pixel (3)
					offset = PALETTE_OFFSET[m_type.ordinal()] + (p * PALETTE_SIZE_RGB) + (j * PALETTE_HEIGHT * BPP) + (i * BPP);
					
					// divide each colour channel for the specified colour by the colour scale and convert it to a byte
					m_data[offset    ] = (byte) (c.getRed() / COLOUR_SCALE);
					m_data[offset + 1] = (byte) (c.getGreen() / COLOUR_SCALE);
					m_data[offset + 2] = (byte) (c.getBlue() / COLOUR_SCALE);
				}
			}
		}
		
		return true;
	}
	
	public boolean load() throws PaletteReadException {
		if(m_file == null || !m_file.exists()) { return false; }

		m_loading = true;
		
		// verify that the file has an extension and a valid name
		String fileName = Utilities.getFileNameNoExtension(m_file.getName());
		String extension = Utilities.getFileExtension(m_file.getName());
		if(fileName == null) {
			m_loading = false;
			throw new PaletteReadException("Unable to determine filename for file: \"" + m_file.getName() +  "\".");
		}
		if(extension == null) {
			m_loading = false;
			throw new PaletteReadException("File \"" + m_file.getName() + "\" has no extension.");
		}

		// verify that the file extension is supported
		if(!hasFileTypeWithExtension(extension)) {
			m_loading = false;
			throw new PaletteReadException("File \"" + m_file.getName() +  "\" has unsupported extension: " + extension);
		}
		
		// attempt to determine the DAT type
		// if it is unknown, prompt the user to specify the type
		m_type = DATType.parseFrom(fileName);

		if(m_type == DATType.Unknown) {
			String datTypes[] = new String[DATType.Unknown.ordinal()];
			for(int i=0;i<datTypes.length;i++) {
				datTypes[i] = DATType.getDisplayName(DATType.values()[i]);
			}
			Object value = JOptionPane.showInputDialog(null, "Unable to determine DAT type.\nPlease choose a DAT type from the list:", "Identify DAT Type", JOptionPane.QUESTION_MESSAGE, null, datTypes, datTypes[0]);
			if(value == null) { return false; }
			
			for(int i=0;i<datTypes.length;i++) {
				if(datTypes[i] == value) {
					m_type = DATType.values()[i];
					break;
				}
			}
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
		
		// update the local memory to the data read in from file
		m_data = data;
		
		return true;
	}
	
	public boolean save() throws PaletteWriteException {
		if(m_file == null) {
			return false;
		}
		
		// check that the palette has a file set and that the palette is loaded
		if(!isInitialized()) {
			throw new PaletteWriteException("Palette DAT has no data to save.");
		}
		
		// write the data to the specified file
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(m_file));
			out.write(m_data);
			out.close();
		}
		catch(IOException e) {
			throw new PaletteWriteException("Error writing to file " + m_file.getName() +  ": " + e.getMessage());
		}
		
		return true;
	}
	
}
