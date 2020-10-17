package palette;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import item.*;
import exception.*;
import utilities.*;

public class PaletteImage extends Palette {
	
	protected BufferedImage m_image;
	
	public static final String PALETTE_DESCRIPTION = "Default";

	public static ItemFileType[] FILE_TYPES = {
		new ItemFileType("Portable Network Graphics Image Palette", "PNG"),
		new ItemFileType("Graphics Interchange Format Image Palette", "GIF"),
		new ItemFileType("Bitmap Image Palette", "BMP")
	};
	
	public static HashMap<String, Integer> BPP = new HashMap<String, Integer>() {
		private static final long serialVersionUID = -7559912519313982496L;

		{
			put("PNG", 4);
			put("GIF", 1);
			put("BMP", 3);
		}
	};
	
	public PaletteImage() {
		this(null);
	}
	
	public PaletteImage(File file) {
		super(file);
		m_image = null;
	}
	
	public boolean isInitialized() {
		return m_image != null;
	}

	public int getPaletteFileSize() {
		return -1;
	}

	public int numberOfBytesPerPixel() {
		ItemFileType fileType = null;

		for(int i = 0; i < FILE_TYPES.length; i++) {
			fileType = FILE_TYPES[i];

			if(m_fileType.equals(fileType)) {
				return BPP.get(fileType.getExtension()).intValue();
			}
		}

		return -1;
	}

	public ItemFileType getDefaulFileType() {
		return FILE_TYPES[0];
	}
	
	public String getDefaultFileExtension() {
		return FILE_TYPES[0].getExtension();
	}

	public ItemFileType[] getFileTypes() {
		return FILE_TYPES;
	}
	
	public BufferedImage getImage() {
		return m_image;
	}

	public String getPaletteDescription(int index) {
		return index == 0 ? PALETTE_DESCRIPTION : null;
	}
	
	public Color getPixel(int x, int y, int index) {
		if(!isInitialized() || x < 0 || y < 0 || x > PALETTE_WIDTH - 1 || y > PALETTE_HEIGHT - 1 || index != 0) { return null; }
		
		// get the pixel colour at the specified position
		return new Color(m_image.getRGB(x, y));
	}
	
	public boolean updatePixel(int x, int y, Color c, int index) {
		if(!isInitialized() || c == null || x < 0 || y < 0 || x > PALETTE_WIDTH - 1 || y > PALETTE_HEIGHT - 1 || index != 0) { return false; }
		
		// update the pixel colour at the specified position
		m_image.setRGB(x, y, c.getRGB());
		
		return true;
	}

	public Color[] getColourData(int index) {
		if(!isInitialized() || index != 0) { return null; }
		
		// iterate over the entire palette and copy each colour into the colour data array
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
				m_image = new BufferedImage(PALETTE_WIDTH, PALETTE_HEIGHT, BufferedImage.TYPE_INT_RGB);
			}
			else {
				return false;
			}
		}
		
		// iterate over the entire image and replace each pixel with
		// the corresponding colour in the new colour data array
		for(int j=0;j<PALETTE_HEIGHT;j++) {
			for(int i=0;i<PALETTE_WIDTH;i++) {
				m_image.setRGB(i, j, colourData[dataOffset + (j * PALETTE_WIDTH) + i].getRGB());
			}
		}
		
		return true;
	}
	
	public boolean fillWithColour(Color c, int index) {
		if(index > 0) { return false; }
		
		// if the palette is not already loaded / initialized, and is instantiable
		// initialize the data and set it to default values
		if(!isInitialized()) {
			if(isInstantiable()) {
				m_image = new BufferedImage(PALETTE_WIDTH, PALETTE_HEIGHT, BufferedImage.TYPE_INT_RGB);
			}
			else {
				return false;
			}
		}
		
		// iterate over the entire image and replace each pixel with the replacement colour
		for(int j=0;j<PALETTE_HEIGHT;j++) {
			for(int i=0;i<PALETTE_WIDTH;i++) {
				m_image.setRGB(i, j, c.getRGB());
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
		ItemFileType fileType = getFirstFileTypeWithExtension(extension);
		
		if(fileType == null) {
			m_loading = false;
			throw new PaletteReadException("File " + m_file.getName() +  " has unsupported extension: " + extension);
		}
		
		// read the palette image into memory using ImageIO
		try { m_image = ImageIO.read(m_file); }
		catch(IOException e) {
			m_loading = false;
			throw new PaletteReadException("ImageIO failed to load image: " + m_file.getName());
		}

		if(m_image == null) {
			m_loading = false;
			throw new PaletteReadException("Null image returned, ImageIO failed to load image: " + m_file.getName());
		}

		setFileType(fileType);

		// verify that the image is square
		if(m_image.getWidth() != m_image.getHeight()) {
			m_loading = false;
			throw new PaletteReadException("Palette image \"" + m_file.getName() + "\" must be square.");
		}

		// check that the image is the expected size
		if(m_image.getWidth() != PALETTE_WIDTH || m_image.getHeight() != PALETTE_HEIGHT) {
			m_loading = false;
			throw new PaletteReadException("Palette image \"" + m_file.getName() + "\" dimensions must be " + PALETTE_WIDTH + " x " + PALETTE_HEIGHT + ".");
		}
		
		m_loading = false;
		
		return true;
	}

	public boolean save() throws PaletteWriteException {
		if(m_file == null) {
			return false;
		}
		
		// check that the palette has a file set and that the palette is loaded
		if(!isInitialized()) {
			throw new PaletteWriteException("Palette image has no data to save.");
		}
		
		// write the image to the specified file
		try {
			return ImageIO.write(m_image, m_fileType.getExtension().toLowerCase(), m_file);
		}
		catch(IOException e) {
			throw new PaletteWriteException("Failed to save palette image to file: \"" + m_file.getName() + "\".");
		}
	}
	
}
