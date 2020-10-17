package art;

import java.util.*;
import java.io.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import exception.*;
import palette.*;
import utilities.*;
import org.json.*;

public class Tile implements TileAttributeChangeListener {
	
	protected int m_number;
	protected int m_width;
	protected int m_height;
	protected byte m_data[];
	protected TileAttributes m_attributes;
	protected Art m_parent;
	protected Vector<TileChangeListener> m_tileChangeListeners;

	public static final String NUMBER_ATTRIBUTE_NAME = "number";
	public static final String ATTRIBUTES_ATTRIBUTE_NAME = "attributes";

	public static final int EMPTY_IMAGE_SIZE = 64;
	protected static BufferedImage EMPTY_IMAGE;

	public static final HashMap<Integer, Integer> SPECIAL_TILE_PALETTE_LOOKUPS = new HashMap<Integer, Integer>() {
		private static final long serialVersionUID = -4916728297639427954L;
		{
			put(2492, 3); // 3D Realms Logo
			put(2493, 2); // Title Screen Background
			put(2497, 2); // Duke Nukem
			put(2498, 2); // 3D
			put(3260, 4); // Episode 1 Ending Animation
			put(3261, 4); 
			put(3262, 4);
			put(3263, 4);
			put(3264, 4);
			put(3265, 4);
			put(3266, 4);
			put(3267, 4);
			put(3268, 4);
		}
	};

	Tile(int number, Art parent) throws IllegalArgumentException {
		this(number, 0, 0, null, 0, parent);
	}

	Tile(int number, int width, int height, byte data[], int attributes, Art parent) throws IllegalArgumentException {
		this(number, width, height, data, TileAttributes.unpack(attributes), parent);
	}

	Tile(int number, int width, int height, byte data[], byte xOffset, byte yOffset, byte numberOfFrames, byte animationType, byte animationSpeed, Art parent) throws IllegalArgumentException {
		this(number, width, height, data, new TileAttributes(xOffset, yOffset, numberOfFrames, animationType < 0 || animationType >= TileAnimationType.numberOfAnimationTypes() ? TileAnimationType.Invalid : TileAnimationType.values()[animationType], animationSpeed, (byte) 0), parent);
	}

	Tile(int number, int width, int height, byte data[], byte xOffset, byte yOffset, byte numberOfFrames, TileAnimationType animationType, byte animationSpeed, Art parent) throws IllegalArgumentException {
		this(number, width, height, data, new TileAttributes(xOffset, yOffset, numberOfFrames, animationType, animationSpeed, (byte) 0), parent);
	}

	Tile(int number, int width, int height, byte data[], byte xOffset, byte yOffset, byte numberOfFrames, byte animationType, byte animationSpeed, byte extra, Art parent) throws IllegalArgumentException {
		this(number, width, height, data, new TileAttributes(xOffset, yOffset, numberOfFrames, animationType < 0 || animationType >= TileAnimationType.numberOfAnimationTypes() ? TileAnimationType.Invalid : TileAnimationType.values()[animationType], animationSpeed, extra), parent);
	}

	Tile(int number, int width, int height, byte data[], byte xOffset, byte yOffset, byte numberOfFrames, TileAnimationType animationType, byte animationSpeed, byte extra, Art parent) throws IllegalArgumentException {
		this(number, width, height, data, new TileAttributes(xOffset, yOffset, numberOfFrames, animationType, animationSpeed, extra), parent);
	}

	Tile(int number, int width, int height, byte data[], TileAttributes attributes, Art parent) throws IllegalArgumentException {
		if(number < 0) { throw new IllegalArgumentException("Invalid tile number value: " + number + "."); }
		if(width < 0) { throw new IllegalArgumentException("Invalid tile width value: " + number + "."); }
		if(height < 0) { throw new IllegalArgumentException("Invalid tile height value: " + number + "."); }

		m_number = number;
		m_width = width;
		m_height = height;
		m_attributes = attributes.clone();
		m_attributes.addTileAttributeChangeListener(this);
		m_tileChangeListeners = new Vector<TileChangeListener>();

		if(data != null) {
			m_data = new byte[data.length];
			System.arraycopy(data, 0, m_data, 0, data.length);
		}

		setParent(parent);
	}

	protected static void initializeEmptyImage() {
		if(EMPTY_IMAGE != null) {
			return;
		}

		EMPTY_IMAGE = new BufferedImage(EMPTY_IMAGE_SIZE, EMPTY_IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
		
		int magenta = Color.MAGENTA.getRGB();
		int black = Color.BLACK.getRGB();

		for(int x = 0; x < EMPTY_IMAGE_SIZE; x++) {
			for(int y = 0; y < EMPTY_IMAGE_SIZE; y++) {
				if(((x / 4) % 2) == 0) {
					EMPTY_IMAGE.setRGB(x, y, (y / 4 % 2 == 0) ? magenta : black);
				}
				else {
					EMPTY_IMAGE.setRGB(x, y, (y / 4 % 2 == 0) ? black : magenta);
				}
			}
		}
	}
	
	public static BufferedImage getEmptyImage() {
		initializeEmptyImage();

		return EMPTY_IMAGE;
	}

	public static int getPaletteLookupIndexForTileWithNumber(int tileNumber) {
		if(SPECIAL_TILE_PALETTE_LOOKUPS.containsKey(tileNumber)) {
			return SPECIAL_TILE_PALETTE_LOOKUPS.get(tileNumber);
		}
		
		return -1;
	}
	
	public boolean isEmpty() {
		return m_data == null ||
			   m_data.length == 0;
	}
	
	public int getSize() {
		return m_data == null ? 0 : m_data.length;
	}

	public int getNumber() {
		return m_number;
	}
	
	public boolean setNumber(int number) {
		if(number < 0) {
			return false;
		}
		
		m_number = number;

		notifyTileChanged();
		
		return true;
	}
	
	public boolean hasName() {
		return TileNames.DEFAULT_TILE_NAMES.hasNameForTileNumber(m_number);
	}
	
	public String getName() {
		return TileNames.DEFAULT_TILE_NAMES.getTileName(m_number);
	}
	
	public String getDisplayName() {
		return "TILE" + Utilities.addLeadingZeroes(m_number, 4);
	}

	public int getWidth() {
		return m_width;
	}
	
	public boolean setWidth(int width) {
		if(width < 0) {
			return false;
		}
		
		m_width = width;

		notifyTileChanged();
		
		return true;
	}

	public int getHeight() {
		return m_height;
	}
	
	public boolean setHeight(int height) {
		if(height < 0) {
			return false;
		}
		
		m_height = height;

		notifyTileChanged();
		
		return true;
	}
	
	public byte[] getData() {
		return m_data;
	}
	
	public void setData(byte data[]) {
		m_data = data;

		notifyTileChanged();
	}

	public BufferedImage getImage() {
		return getImage(true);
	}
	
	public BufferedImage getImage(boolean transparent) {
		if(m_width == 0 || m_height == 0 || m_data == null) {
			return null;
		}
	
		int paletteLookupIndex = getPaletteLookupIndexForTileWithNumber(m_number);
		IndexColorModel colourModel = null;

		if(paletteLookupIndex == -1) {
			colourModel = (m_parent == null ? Palette.DEFAULT_PALETTE : m_parent.getPalette()).getIndexColourModel(transparent);
		}
		else {
			colourModel = (m_parent == null ? Palette.DEFAULT_LOOKUP : m_parent.getLookup()).getIndexColourModel(paletteLookupIndex, transparent);
		}

		BufferedImage image = new BufferedImage(m_width, m_height, BufferedImage.TYPE_BYTE_INDEXED, colourModel);

		for(int x = 0; x < m_width; x++) {
			for(int y = 0; y < m_height; y++) {
				image.getRaster().getDataBuffer().setElem(0,  (y * (m_width * 1)) + (x * 1), m_data[(m_height * x) + y] & 0xff);
			}
		}

		return image;
	}
	
	public void setImage(String filePath) throws IllegalArgumentException, TileReadException {
		if(filePath == null || filePath.isEmpty()) {
			throw new IllegalArgumentException("Empty image file path.");
		}
		
		setImage(new File(filePath));
	}
	
	public void setImage(File file) throws IllegalArgumentException, TileReadException {
		if(file == null) {
			throw new IllegalArgumentException("Invalid image file.");
		}
		
		try {
			setImage(ImageIO.read(file));
		}
		catch(IOException e) {
			throw new TileReadException("ImageIO failed to load image: \"" + file.getName() + "\".");
		}
	}
	
	public void setImage(BufferedImage image) throws IllegalArgumentException, TileReadException {
		switch(image.getType()) {
			// palette indexed
			case BufferedImage.TYPE_BYTE_INDEXED:
				int width = image.getWidth();
				int height = image.getHeight();
				IndexColorModel colourModel = (IndexColorModel) image.getColorModel();
				int offset = colourModel.getTransparentPixel() == 0 ? 1 : 0;
				byte data[] = new byte[width * height];

				for(int x = 0; x < width; x++) {
					for(int y = 0; y < height; y++) {
						int pixel = image.getRaster().getDataBuffer().getElem(0, (y * (width * 1)) + (x * 1)) - offset;

						if(pixel == -1) {
							pixel = 255;
						}
						
						if(pixel < 0 || pixel > 255) {
							throw new TileReadException("Invalid indexed pixel at (" + x + ", " + y + "): " + pixel + ", expected value between 0 and 255.");
						}
						
						data[(height * x) + y] = (byte) pixel;
					}
				}
				
				m_width = width;
				m_height = height;
				m_data = data;

				notifyTileChanged();

				break;
				
			// rgb
			case BufferedImage.TYPE_INT_RGB:
			case BufferedImage.TYPE_INT_BGR:
			case BufferedImage.TYPE_3BYTE_BGR:
			case BufferedImage.TYPE_USHORT_565_RGB:
			case BufferedImage.TYPE_USHORT_555_RGB:
// TODO: dither non-palette indexed image
			
			// alpha
			case BufferedImage.TYPE_INT_ARGB:
			case BufferedImage.TYPE_INT_ARGB_PRE:
			case BufferedImage.TYPE_4BYTE_ABGR:
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
// TODO: dither & force opaque non-palette indexed image

			// grayscale
			case BufferedImage.TYPE_BYTE_GRAY:
			case BufferedImage.TYPE_USHORT_GRAY:
// TODO: dither non-palette indexed image
			
			// other
			case BufferedImage.TYPE_BYTE_BINARY:
			case BufferedImage.TYPE_CUSTOM:
			default:
				throw new IllegalArgumentException("Unsupported image type.");
		}
	}
	
	public TileAttributes getAttributes() {
		return m_attributes;
	}

	public void setAttributes(int attributes) throws IllegalArgumentException {
		m_attributes.setAttributes(attributes);
	}

	public void setAttributes(byte xOffset, byte yOffset, byte numberOfFrames, byte animationType, byte animationSpeed) throws IllegalArgumentException {
		setAttributes(xOffset, yOffset, numberOfFrames, animationType, animationSpeed, (byte) 0);
	}

	public void setAttributes(byte xOffset, byte yOffset, byte numberOfFrames, TileAnimationType animationType, byte animationSpeed) throws IllegalArgumentException {
		setAttributes(xOffset, yOffset, numberOfFrames, animationType, animationSpeed, (byte) 0);
	}
	
	public void setAttributes(byte xOffset, byte yOffset, byte numberOfFrames, byte animationType, byte animationSpeed, byte extra) throws IllegalArgumentException {
		setAttributes(xOffset, yOffset, numberOfFrames, animationType < 0 || animationType >= TileAnimationType.numberOfAnimationTypes() ? TileAnimationType.Invalid : TileAnimationType.values()[animationType], animationSpeed, extra);
	}

	public void setAttributes(byte xOffset, byte yOffset, byte numberOfFrames, TileAnimationType animationType, byte animationSpeed, byte extra) throws IllegalArgumentException {
		m_attributes.setAttributes(xOffset, yOffset, numberOfFrames, animationType, animationSpeed, extra);
	}
	
	public void setAttributes(TileAttributes attributes) throws IllegalArgumentException {
		m_attributes.setAttributes(attributes);
	}
	
	public Art getParent() {
		return m_parent;
	}
	
	protected void setParent(Art parent) {
		if(m_parent != null) {
			removeTileChangeListener(m_parent);
		}
		
		m_parent = parent;
		
		if(m_parent != null) {
			addTileChangeListener(m_parent);
		}
	}
	
	public void clear() {
		if(isEmpty()) {
			return;
		}
		
		m_width = 0;
		m_height = 0;
		m_data = null;
		m_attributes.reset();

		notifyTileChanged();
	}
	
	public JSONObject getMetadata() {
		JSONObject metadata = new JSONObject();
		metadata.put(NUMBER_ATTRIBUTE_NAME, m_number);
		metadata.put(ATTRIBUTES_ATTRIBUTE_NAME, m_attributes.getMetadata());
		return metadata;
	}
	
	public void applyMetadata(JSONObject tileMetadata) throws IllegalArgumentException, InvalidMetadataException, JSONException {
		validateMetadata(tileMetadata);

		m_number = tileMetadata.getInt(NUMBER_ATTRIBUTE_NAME);
		m_attributes.applyMetadata(tileMetadata.getJSONObject(ATTRIBUTES_ATTRIBUTE_NAME));
	}
	
	public static JSONObject readMetadataFrom(String tileMetadataFilePath) throws IllegalArgumentException, MissingMetadataException, InvalidMetadataException, JSONException, IOException {
		if(tileMetadataFilePath == null || tileMetadataFilePath.trim().isEmpty()) {
			throw new MissingMetadataException("Missing or invalid tile metadata file path!");
		}
		
		return readMetadataFrom(new File(tileMetadataFilePath));
	}
	
	public static JSONObject readMetadataFrom(File tileMetadataFile) throws IllegalArgumentException, MissingMetadataException, InvalidMetadataException, JSONException, IOException {
		if(tileMetadataFile == null || !tileMetadataFile.isFile() || !tileMetadataFile.exists()) {
			throw new MissingMetadataException("Missing or invalid tile metadata file!");
		}

		JSONObject tileMetadata = new JSONObject(new JSONTokener(new InputStreamReader(new FileInputStream(tileMetadataFile))));
		
		validateMetadata(tileMetadata);
		
		return tileMetadata;
	}

	public void writeMetadataTo(String directoryPath) throws IllegalArgumentException, MetadataWriteException, IOException {
		writeMetadataTo(directoryPath, true);
	}

	public void writeMetadataTo(File directory) throws IllegalArgumentException, MetadataWriteException, IOException {
		writeMetadataTo(directory, true);
	}

	public void writeMetadataTo(String directoryPath, boolean overwrite) throws IllegalArgumentException, MetadataWriteException, IOException {
		if(directoryPath == null || directoryPath.trim().isEmpty()) {
			throw new IllegalArgumentException("Missing or invalid directory path!");
		}

		writeMetadataTo(new File(directoryPath));
	}

	public void writeMetadataTo(File directory, boolean overwrite) throws IllegalArgumentException, MetadataWriteException, IOException {
		if(directory != null) {
			if(!directory.exists()) {
				try {
					if(!directory.mkdirs()) {
						throw new MetadataWriteException("Failed to create directory structure.");
					}
				}
				catch(SecurityException exception) {
					throw new MetadataWriteException("Failed to create directory structure due to security exception: " + exception.getMessage());
				}
			}
		}
		
		File metadataFile = new File(Utilities.joinPaths(directory == null ? "" : directory.getPath(), getDisplayName() + ".JSON"));
		
		if(metadataFile.exists() && !overwrite) {
			throw new MetadataWriteException("Metadata file already exists, must specify overwrite in order to replace it.");
		}
		
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(metadataFile));
		out.write(getMetadata().toString(4));
		out.close();
	}
	
	public static boolean isValidMetadata(JSONObject tileMetadata) {
		try {
			return validateMetadata(tileMetadata, false);
		}
		catch(InvalidMetadataException exception) {
			return false;
		}
	}

	public static boolean validateMetadata(JSONObject tileMetadata) throws InvalidMetadataException {
		return validateMetadata(tileMetadata, true);
	}

	public static boolean validateMetadata(JSONObject tileMetadata, boolean throwErrors) throws InvalidMetadataException {
		try {
			if(tileMetadata == null) {
				throw new InvalidMetadataException("Missing tile metadata.");
			}
			
			if(tileMetadata.has(NUMBER_ATTRIBUTE_NAME)) {
				throw new InvalidMetadataException("Missing tile number metadata attribute.");
			}
			
			try {
				int number = tileMetadata.getInt(NUMBER_ATTRIBUTE_NAME);
				
				if(number < 0) {
					throw new InvalidMetadataException("Invalid tile number metadata value, expected non-negative integer.");
				}
			}
			catch(JSONException exception) {
				throw new InvalidMetadataException("Invalid tile number metadata value type, expected integer.");
			}
			
			if(!tileMetadata.has(ATTRIBUTES_ATTRIBUTE_NAME)) {
				throw new InvalidMetadataException("Missing tile attributes metadata attribute.");
			}
			
			try {
				if(!TileAttributes.validateMetadata(tileMetadata.getJSONObject(ATTRIBUTES_ATTRIBUTE_NAME), throwErrors)) {
					return false;
				}
			}
			catch(JSONException exception) {
				if(!throwErrors) {
					return false;
				}

				throw new InvalidMetadataException("Invalid tile attributes metadata value type, expected object.");
			}
		}
		catch(InvalidMetadataException exception) {
			if(!throwErrors) {
				return false;
			}
			
			throw exception;
		}
		
		return true;
	}

	public boolean writeTo(String directoryPath, boolean overwrite, boolean transparent, TileFormat format, boolean includeMetadata) throws IllegalArgumentException, TileWriteException, MetadataWriteException, IOException {
		return writeTo(new File(directoryPath), overwrite, transparent, format, includeMetadata);
	}
	
	public boolean writeTo(File directory, boolean overwrite, boolean transparent, TileFormat format, boolean includeMetadata) throws IllegalArgumentException, TileWriteException, MetadataWriteException, IOException {
		if(isEmpty()) { return false; }
		
		if(directory == null) { throw new IllegalArgumentException("Invalid file path."); }
		if(format == null) { throw new IllegalArgumentException("Invalid tile format."); }

		File file = new File(directory, "TILE" + Utilities.addLeadingZeroes(m_number, 4) + "." + format.getExtension());
		
		if(file.exists() && !overwrite) {
			throw new TileWriteException("Image file already exists, must specify overwrite in order to replace it.");
		}

		try {
// TODO: non-opaque pixels in the palette are re-ordered, may need custom handling to retain transparent pixel at byte 255: https://stackoverflow.com/questions/6495518/writing-image-metadata-in-java-preferably-png
			if(!ImageIO.write(getImage(transparent), format.getExtension(), file)) {
				return false;
			}
		}
		catch(IOException e) {
			throw new TileWriteException("Failed to save tile image to file: \"" + file.getAbsolutePath() + "\".");
		}
		
		if(includeMetadata) {
			writeMetadataTo(directory, overwrite);
		}
		
		return true;
	}

	public int numberOfTileChangeListeners() {
		return m_tileChangeListeners.size();
	}
	
	public TileChangeListener getTileChangeListener(int index) {
		if(index < 0 || index >= m_tileChangeListeners.size()) { return null; }
		
		return m_tileChangeListeners.elementAt(index);
	}
	
	public boolean hasTileChangeListener(TileChangeListener c) {
		return m_tileChangeListeners.contains(c);
	}
	
	public int indexOfTileChangeListener(TileChangeListener c) {
		return m_tileChangeListeners.indexOf(c);
	}
	
	public boolean addTileChangeListener(TileChangeListener c) {
		if(c == null || m_tileChangeListeners.contains(c)) { return false; }
		
		m_tileChangeListeners.add(c);
		
		return true;
	}
	
	public boolean removeTileChangeListener(int index) {
		if(index < 0 || index >= m_tileChangeListeners.size()) { return false; }
		
		m_tileChangeListeners.remove(index);
		
		return true;
	}
	
	public boolean removeTileChangeListener(TileChangeListener c) {
		if(c == null) { return false; }
		
		return m_tileChangeListeners.remove(c);
	}
	
	public void clearTileChangeListeners() {
		m_tileChangeListeners.clear();
	}
	
	public void notifyTileChanged() {
		for(int i=0;i<m_tileChangeListeners.size();i++) {
			m_tileChangeListeners.elementAt(i).handleTileChange(this);
		}
	}
	
	public void notifyTileAttributeChanged(TileAttributes attributes, TileAttribute attribute, byte value) {
		for(int i=0;i<m_tileChangeListeners.size();i++) {
			m_tileChangeListeners.elementAt(i).handleTileChange(this);
			m_tileChangeListeners.elementAt(i).handleTileAttributeChange(attributes, attribute, value);
		}
	}

	public void handleTileAttributeChange(TileAttributes attributes, TileAttribute attribute, byte value) {
		notifyTileAttributeChanged(attributes, attribute, value);
	}

	public void apply(Tile tile) {
		apply(tile, false, false);
	}
	
	public void apply(Tile tile, boolean replaceParent, boolean replaceTileChangeListeners) {
		m_number = tile.m_number;
		m_width = tile.m_width;
		m_height = tile.m_height;
		m_data = null;
		m_attributes.setAttributes(tile.m_attributes);

		if(tile.m_data != null) {
			m_data = new byte[tile.m_data.length];
			System.arraycopy(tile.m_data, 0, m_data, 0, tile.m_data.length);
		}

		if(replaceParent) {
			m_parent = tile.getParent();
		}
		
		if(replaceTileChangeListeners) {
			m_tileChangeListeners.clear();
			
			for(int i = 0; i < tile.m_tileChangeListeners.size(); i++) {
				m_tileChangeListeners.add(tile.m_tileChangeListeners.elementAt(i));
			}
		}

		notifyTileChanged();
	}
	
	public Tile clone() {
		return clone(true, true);
	}
	
	public Tile clone(boolean reassignParent, boolean reassignTileChangeListeners) {
		Tile newTile = new Tile(m_number, m_width, m_height, m_data, m_attributes, reassignParent ? m_parent : null);

		if(reassignTileChangeListeners) {
			newTile.m_tileChangeListeners = new Vector<TileChangeListener>(m_tileChangeListeners.size());
			
			for(int i = 0; i < m_tileChangeListeners.size(); i++) {
				newTile.m_tileChangeListeners.add(m_tileChangeListeners.elementAt(i));
			}
		}

		return newTile;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Tile)) {
			return false;
		}
		
		Tile t = (Tile) o;
		
		return m_number == t.m_number &&
			   m_width == t.m_width &&
			   m_height == t.m_height &&
			   m_attributes.equals(t.m_attributes) &&
			   Arrays.equals(m_data, t.m_data);
	}
	
	public String toString() {
		return getDisplayName() + " (" + m_width + " x " + m_height + ")";
	}
	
}
