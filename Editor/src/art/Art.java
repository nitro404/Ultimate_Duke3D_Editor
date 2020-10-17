package art;

import java.io.*;
import java.util.*;
import console.*;
import editor.*;
import exception.*;
import utilities.*;
import item.*;
import palette.*;
import settings.*;
import org.json.*;

public class Art extends Item implements TileChangeListener {
	
// TODO: make this abstract so that the ART plugin can implement this
// TODO: replacing, clearing, updating tile attributes doesn't trigger changed event?
// TODO: see editart controls for parity: http://dukertcm.com/knowledge-base/documents-online/art-editart-guide2.htm
// TODO: allow names.h override
	
	protected int m_localTileStart;
	protected int m_localTileEnd;
	protected int m_legacyTileCount; // note: this field is no longer used and is inaccurate
	protected Tile m_tiles[];
	protected boolean m_initialized;
	protected Palette m_palette;
	protected Palette m_lookup;
	protected Vector<ArtChangeListener> m_artChangeListeners;

	public final static byte VERSION = 1;
	public final static int HEADER_LENGTH = 4 * 4; // 16 bytes
	public final static int DEFAULT_NUMBER_OF_TILES = 256;
	public static final Endianness FILE_ENDIANNESS = Endianness.LittleEndian;
	
	public static final String LOCAL_TILE_START_ATTRIBUTE_NAME = "localTileStart";
	public static final String LOCAL_TILE_END_ATTRIBUTE_NAME = "localTileEnd";
	public static final String LEGACY_TILE_COUNT_ATTRIBUTE_NAME = "legacyTileCount";
	public static final String TILES_ATTRIBUTE_NAME = "tiles";

	public static ItemFileType[] FILE_TYPES = {
		new ItemFileType("Duke Nukem 3D Art", "ART")
	};

	public Art() throws IllegalArgumentException, InvalidFileTypeException, UnsupportedFileTypeException {
		this(0);
	}
	
	public Art(String fileName) throws IllegalArgumentException, InvalidFileTypeException, UnsupportedFileTypeException {
		this(fileName, 0);
	}
	
	public Art(File file) throws IllegalArgumentException, InvalidFileTypeException, UnsupportedFileTypeException {
		this(file, 0);
	}

	public Art(int localTileStart) throws IllegalArgumentException, InvalidFileTypeException, UnsupportedFileTypeException {
		this(localTileStart, localTileStart + DEFAULT_NUMBER_OF_TILES - 1);
	}
	
	public Art(String fileName, int localTileStart) throws IllegalArgumentException, InvalidFileTypeException, UnsupportedFileTypeException {
		this(fileName, localTileStart, localTileStart + DEFAULT_NUMBER_OF_TILES - 1);
	}
	
	public Art(File file, int localTileStart) throws IllegalArgumentException, InvalidFileTypeException, UnsupportedFileTypeException {
		this(file, localTileStart, localTileStart + DEFAULT_NUMBER_OF_TILES - 1);
	}

	public Art(int localTileStart, int localTileEnd) throws IllegalArgumentException, InvalidFileTypeException, UnsupportedFileTypeException {
		this(localTileStart, localTileEnd, 0);
	}
	
	public Art(String fileName, int localTileStart, int localTileEnd) throws IllegalArgumentException, InvalidFileTypeException, UnsupportedFileTypeException {
		this(fileName, localTileStart, localTileEnd, 0);
	}
	
	public Art(File file, int localTileStart, int localTileEnd) throws IllegalArgumentException, InvalidFileTypeException, UnsupportedFileTypeException {
		this(file, localTileStart, localTileEnd, 0);
	}

	public Art(int localTileStart, int localTileEnd, int legacyTileCount) throws IllegalArgumentException, InvalidFileTypeException, UnsupportedFileTypeException {
		super(FILE_TYPES[0]);
		initialize(localTileStart, localTileEnd, legacyTileCount);
	}
	
	public Art(String fileName, int localTileStart, int localTileEnd, int legacyTileCount) throws IllegalArgumentException, InvalidFileTypeException, UnsupportedFileTypeException {
		super(fileName, FILE_TYPES[0]);
		initialize(localTileStart, localTileEnd, legacyTileCount);
	}
	
	public Art(File file, int localTileStart, int localTileEnd, int legacyTileCount) throws IllegalArgumentException, InvalidFileTypeException, UnsupportedFileTypeException {
		super(file, FILE_TYPES[0]);
		initialize(localTileStart, localTileEnd, legacyTileCount);
	}
	
	protected boolean initialize(int localTileStart, int localTileEnd, int legacyTileCount) throws IllegalArgumentException {
		if(m_tiles != null) {
			return false;
		}
		
		if(m_initialized) {
			return true;
		}

		if(localTileStart < 0) { throw new IllegalArgumentException("Invalid local tile start value, expected non-negative integer."); }
		if(localTileEnd < 0) { throw new IllegalArgumentException("Invalid local tile end value, expected non-negative integer."); }
		if(localTileEnd < localTileStart) { throw new IllegalArgumentException("Invalid local tile end value: " + localTileEnd + ", expected value to be larger than or equal to the local tile start value: " + localTileStart + "."); }

		m_localTileStart = localTileStart;
		m_localTileEnd = localTileEnd;
		m_legacyTileCount = legacyTileCount;
		m_artChangeListeners = new Vector<ArtChangeListener>();
		
		if(localTileStart != localTileEnd) {
			m_tiles = new Tile[m_localTileEnd - m_localTileStart + 1];
			
			for(int i = 0; i < m_tiles.length; i++) {
				m_tiles[i] = new Tile(m_localTileStart + i, this);
			}
		}
		
// TODO: this needs to respect the relative automatic override setting without knowing about the settings.. maybe pass palettes in instead?:

		if(m_file != null && m_file.isFile()) {
			if(SettingsManager.instance.automaticRelativePaletteOverride) {
				File relativePaletteFile = new File(Utilities.joinPaths(Utilities.getFilePath(m_file), "PALETTE.DAT"));
				m_palette = Editor.loadPalette(relativePaletteFile, false);
			}

			if(SettingsManager.instance.automaticRelativeLookupOverride) {
				File relativeLookupFile = new File(Utilities.joinPaths(Utilities.getFilePath(m_file), "LOOKUP.DAT"));
				m_lookup = Editor.loadPalette(relativeLookupFile, false);
			}
		}

		if(m_palette == null) {
			m_palette = Editor.palette;
		}
		
		if(m_lookup == null) {
			m_lookup = Editor.lookup;
		}

		m_initialized = true;
		
		return true;
	}
	
	public boolean isInstantiable() {
		return true;
	}
	
	public boolean isInitialized() {
		return m_initialized;
	}

	public Endianness getFileEndianness() {
		return FILE_ENDIANNESS;
	}

	public ItemFileType[] getFileTypes() {
		return FILE_TYPES;
	}

	public Palette getPalette() {
		return m_palette;
	}

	public Palette getLookup() {
		return m_lookup;
	}
	
	public int getNumber() {
		return (int) Math.floor(m_localTileStart / numberOfTiles());
	}
	
	public void setNumber(int number) throws IllegalArgumentException {
		if(number < 0) {
			throw new IllegalArgumentException("Invalid art number: " + number + ", expected a non-negative integer.");
		}
		
		setLocalTileStart(number * numberOfTiles());
	}
	
	public String getDisplayName() {
		return "TILES" + Utilities.addLeadingZeroes(getNumber(), 3);
	}
	
	public int getLocalTileStart() {
		return m_localTileStart;
	}

	public void setLocalTileStart(int localTileStart) throws IllegalArgumentException {
		if(m_localTileStart == localTileStart) {
			return;
		}
		
		if(localTileStart < 0) {
			throw new IllegalArgumentException("Invalid local tile start value: " + localTileStart + ", expected a non-negative integer.");
		}
		
		int numberOfTiles = numberOfTiles();
		
		m_localTileStart = localTileStart;
		m_localTileEnd = m_localTileStart + numberOfTiles - 1;
		
		for(int i = 0; i < m_tiles.length; i++) {
			m_tiles[i].setNumber(m_localTileStart + i);
		}

		setChanged(true);
	}
	
	public int getLocalTileEnd() {
		return m_localTileEnd;
	}
	
	public void setLocalTileEnd(int localTileEnd) throws IllegalArgumentException {
		if(m_localTileEnd == localTileEnd) {
			return;
		}
		
		if(localTileEnd < m_localTileStart) {
			throw new IllegalArgumentException("Invalid local tile end value: " + localTileEnd + ", expected value greater than or equal to local tile start value: " + m_localTileStart + ".");
		}
		
		Tile newTiles[] = new Tile[localTileEnd - m_localTileStart + 1];
		
		// grow the tile array to the desired length, populating the new space with empty tiles
		if(localTileEnd > m_localTileEnd) {
			for(int i = 0; i < m_tiles.length; i++) {
				newTiles[i] = m_tiles[i];
			}
			
			for(int i = m_tiles.length; i < newTiles.length; i++) {
				newTiles[i] = new Tile(m_localTileStart + i, this);
			}
		}
		// truncate the tile array to the desired length
		else if(localTileEnd < m_localTileEnd) {
			for(int i = 0; i < newTiles.length; i++) {
				newTiles[i] = m_tiles[i];
			}
		}
		
		m_localTileEnd = localTileEnd;
		m_tiles = newTiles;
		
		setChanged(true);
		notifyNumberOfTilesChanged();
	}

	public int getLegacyTileCount() {
		return m_legacyTileCount;
	}
	
	public void setLegacyTileCount(int legacyTileCount) {
		if(m_legacyTileCount == legacyTileCount) {
			return;
		}
		
		m_legacyTileCount = legacyTileCount;
		
		setChanged(true);
	}
	
	public boolean hasEmptyTile() {
		if(m_tiles == null) {
			return true;
		}

		for(int i = 0; i < m_tiles.length; i++) {
			if(m_tiles[i].isEmpty()) {
				return true;
			}
		}

		return false;
	}
	
	public boolean hasNonEmptyTile() {
		if(m_tiles == null) {
			return false;
		}
		
		for(int i = 0; i < m_tiles.length; i++) {
			if(!m_tiles[i].isEmpty()) {
				return true;
			}
		}

		return false;
	}
	
	public int numberOfTiles() {
		return m_localTileEnd - m_localTileStart + 1;
	}
	
	public void setNumberOfTiles(int numberOfTiles) throws IllegalArgumentException {
		if(numberOfTiles < 1) {
			throw new IllegalArgumentException("Invalid number of tiles: " + numberOfTiles + ", expected positive integer value.");
		}
		
		setLocalTileStart(getNumber() * numberOfTiles);
		setLocalTileEnd(m_localTileStart + numberOfTiles - 1);
	}

	public int numberOfNonEmptyTiles() {
		if(m_tiles == null) {
			return 0;
		}
		
		int tileCount = 0;

		for(int i = 0; i < m_tiles.length; i++) {
			if(!m_tiles[i].isEmpty()) {
				tileCount++;
			}
		}

		return tileCount;
	}

	public int numberOfEmptyTiles() {
		if(m_tiles == null) {
			return 0;
		}
		
		int tileCount = 0;

		for(int i = 0; i < m_tiles.length; i++) {
			if(m_tiles[i].isEmpty()) {
				tileCount++;
			}
		}

		return tileCount;
	}

	public Tile getTileByIndex(int tileIndex) {
		if(m_tiles == null) {
			return null;
		}
		
		if(tileIndex < 0 || tileIndex >= m_tiles.length) {
			return null;
		}

		return m_tiles[tileIndex];
	}

	public Tile getTileByNumber(int tileNumber) {
		if(m_tiles == null) {
			return null;
		}
		
		return getTileByIndex(tileNumber - m_localTileStart);
	}

	protected boolean setTile(Tile tile) {
		return setTile(tile, tile.getNumber());
	}

	protected boolean setTile(Tile tile, int tileNumber) {
		if(m_tiles == null) {
			return false;
		}
		
		int tileIndex = tileNumber - m_localTileStart;

		if(tileIndex < 0 || tileIndex >= m_tiles.length) {
			return false;
		}

		tile.setNumber(tileNumber);

		m_tiles[tileIndex] = tile;
		tile.setParent(this);
		
		return true;
	}

	public boolean replaceTile(Tile tile) {
		if(m_tiles == null || tile == null) {
			return false;
		}

		return replaceTile(tile, tile.getNumber());
	}
	
	public boolean replaceTile(Tile tile, int tileNumber) {
		if(m_tiles == null) {
			return false;
		}
		
		int tileIndex = tileNumber - m_localTileStart;

		if(tileIndex < 0 || tileIndex >= m_tiles.length) {
			return false;
		}

		Tile newTile = tile.clone();
		newTile.setNumber(tileNumber);

		m_tiles[tileIndex] = newTile;
		newTile.setParent(this);
		
		return true;
	}

	public boolean clearTile(int tileNumber) {
		if(m_tiles == null) {
			return false;
		}
		
		int tileIndex = tileNumber - m_localTileStart;

		if(tileIndex < 0 || tileIndex >= m_tiles.length) {
			return false;
		}

		m_tiles[tileIndex].clear();
		
		return true;
	}

	public Vector<Tile> getNonEmptyTiles() {
		if(m_tiles == null) {
			return null;
		}
		
		Tile tile = null;
		Vector<Tile> tiles = new Vector<Tile>();

		for(int i = 0; i < m_tiles.length; i++) {
			tile = m_tiles[i];

			if(!tile.isEmpty()) {
				tiles.add(tile);
			}
		}

		return tiles;
	}

	public Vector<Tile> getEmptyTiles() {
		if(m_tiles == null) {
			return null;
		}
		
		Tile tile = null;
		Vector<Tile> tiles = new Vector<Tile>();

		for(int i = 0; i < m_tiles.length; i++) {
			tile = m_tiles[i];

			if(tile.isEmpty()) {
				tiles.add(tile);
			}
		}

		return tiles;
	}
	
// TODO: add art compareTo function

	public int getArtFileSize() {
		if(m_tiles == null) {
			return 0;
		}
		
		int size = HEADER_LENGTH + (m_tiles.length * 8);

		for(int i = 0; i < m_tiles.length; i++) {
			size += m_tiles[i].getSize();
		}

		return size;
	}

	public boolean extractTileAtIndex(int index, String directoryPath, boolean overwrite, boolean transparent, TileFormat format, boolean includeMetadata) throws IllegalArgumentException, TileWriteException, MetadataWriteException, IOException {
		return extractTileAtIndex(index, new File(directoryPath), overwrite, transparent, format, includeMetadata);
	}
	
	public boolean extractTileAtIndex(int index, File directory, boolean overwrite, boolean transparent, TileFormat format, boolean includeMetadata) throws IllegalArgumentException, TileWriteException, MetadataWriteException, IOException {
		if(index < 0 || index >= m_tiles.length) {
			throw new IllegalArgumentException("Tile index " + index + " is out of range, expected value between 0 and " + (m_tiles.length - 1) + ", inclusively.");
		}
		
		return m_tiles[index].writeTo(directory, overwrite, transparent, format, includeMetadata);
	}

	public boolean extractTileByNumber(int number, String directoryPath, boolean overwrite, boolean transparent, TileFormat format, boolean includeMetadata) throws IllegalArgumentException, TileWriteException, MetadataWriteException, IOException {
		return extractTileByNumber(number, new File(directoryPath), overwrite, transparent, format, includeMetadata);
	}
	
	public boolean extractTileByNumber(int number, File directory, boolean overwrite, boolean transparent, TileFormat format, boolean includeMetadata) throws IllegalArgumentException, TileWriteException, MetadataWriteException, IOException {
		int tileIndex = number - m_localTileStart;
		
		if(tileIndex < 0 || tileIndex >= m_tiles.length) {
			throw new IllegalArgumentException("Tile number " + number + " is out of range, expected value between " + m_localTileStart + " and " + m_localTileEnd + ", inclusively.");
		}
		
		return extractTileAtIndex(tileIndex, directory, overwrite, transparent, format, includeMetadata);
	}
	
	public boolean extractAllTiles(File directory, boolean overwrite, boolean transparent, TileFormat format, boolean includeMetadata) throws IllegalArgumentException, TileWriteException, MetadataWriteException, IOException {
		for(int i = 0; i < m_tiles.length; i++) {
			if(!extractTileAtIndex(i, directory, overwrite, transparent, format, includeMetadata)) {
				return false;
			}
		}
		
		if(includeMetadata) {
			writeMetadataTo(directory);
		}
		
		return true;
	}

	public JSONObject getMetadata() {
		return getMetadata(false);
	}

	public JSONObject getMetadata(boolean includeTiles) {
		JSONObject metadata = new JSONObject();
		metadata.put(LOCAL_TILE_START_ATTRIBUTE_NAME, m_localTileStart);
		metadata.put(LOCAL_TILE_END_ATTRIBUTE_NAME, m_localTileEnd);
		metadata.put(LEGACY_TILE_COUNT_ATTRIBUTE_NAME, m_legacyTileCount);
		
		if(includeTiles) {
			JSONArray tilesMetadata = new JSONArray();
			
			for(int i = 0; i < m_tiles.length; i++) {
				tilesMetadata.put(m_tiles[i].getMetadata());
			}
			
			metadata.put(TILES_ATTRIBUTE_NAME, tilesMetadata);
		}

		return metadata;
	}
	
	public void applyMetadata(JSONObject artMetadata) throws IllegalArgumentException, InvalidMetadataException, JSONException {
		if(artMetadata == null) {
			throw new IllegalArgumentException("Missing art metadata.");
		}
		
		int localTileStart = artMetadata.getInt(LOCAL_TILE_START_ATTRIBUTE_NAME);

		if(localTileStart < 0) {
			throw new InvalidMetadataException("Invalid local tile start metadata value: " + localTileStart + ", expected non-negative integer.");
		}
		
		if(localTileStart != m_localTileStart) {
			throw new InvalidMetadataException("Local tile start value does not match metadata.");
		}
		
		int localTileEnd = artMetadata.getInt(LOCAL_TILE_END_ATTRIBUTE_NAME);
		
		if(localTileEnd < 0) { 
			throw new InvalidMetadataException("Invalid local tile end metadata value: " + localTileEnd + ", expected non-negative integer.");
		}

		if(localTileStart != m_localTileEnd) {
			throw new InvalidMetadataException("Local tile end value does not match metadata.");
		}
		
		if(localTileEnd < localTileStart) {
			throw new InvalidMetadataException("Invalid local tile end metadata value: " + localTileEnd + ", expected value to be larger than or equal to the local tile start value: " + localTileStart + ".");
		}

		int legacyTileCount = artMetadata.getInt(LEGACY_TILE_COUNT_ATTRIBUTE_NAME);
		
		JSONArray tilesMetadata = artMetadata.getJSONArray(TILES_ATTRIBUTE_NAME);

		if(tilesMetadata != null) {
			int numberOfTiles = localTileEnd - localTileStart + 1;
			
			if(tilesMetadata.length() != numberOfTiles) {
				throw new InvalidMetadataException("Incomplete tile metadata, expected " + numberOfTiles + " entries, but found " + tilesMetadata.length() + " instead.");
			}
			
			JSONObject tileMetadata = null;
			HashMap<Integer, TileAttributes> tilesAttributes = new HashMap<Integer, TileAttributes>(numberOfTiles);
			
			for(int i = 0; i < tilesMetadata.length(); i++) {
				tileMetadata = tilesMetadata.getJSONObject(i);
				
				if(tileMetadata == null) {
					throw new InvalidMetadataException("Tile metadata at index " + i + " is missing or invalid.");
				}
	
				int number = tileMetadata.getInt(Tile.NUMBER_ATTRIBUTE_NAME);
				
				if(number < 0) {
					throw new InvalidMetadataException("Tile metadata at index " + i + " has an invalid tile number, expected non-negative integer.");
				}
				
				if(getTileByNumber(number) == null) {
					throw new InvalidMetadataException("Tile #" + number + " does not exist in current art object.");
				}
				
				if(tilesAttributes.get(number) != null) {
					throw new InvalidMetadataException("Duplicate tile #" + number + " encountered in tile metadata.");
				}
				
				TileAttributes tileAttributes = TileAttributes.parseFromMetadata(artMetadata.getJSONObject(Tile.ATTRIBUTES_ATTRIBUTE_NAME));
				
				if(tileAttributes == null) {
					throw new InvalidMetadataException("Missing or invalid tile attributes metadata for tile #" + number + ".");
				}
				
				tilesAttributes.put(number, tileAttributes);
			}
			
			Iterator<Map.Entry<Integer, TileAttributes>> tilesAttributesIterator = tilesAttributes.entrySet().iterator();
			
			while(tilesAttributesIterator.hasNext()) {
				Map.Entry<Integer, TileAttributes> tileAttributesEntry = tilesAttributesIterator.next();
				getTileByNumber(tileAttributesEntry.getKey()).setAttributes(tileAttributesEntry.getValue());
			}
		}
		
		m_localTileStart = localTileStart;
		m_localTileEnd = localTileEnd;
		m_legacyTileCount = legacyTileCount;
	}

	public static JSONObject readMetadataFrom(String artMetadataFilePath) throws IllegalArgumentException, MissingMetadataException, InvalidMetadataException, JSONException, IOException {
		if(artMetadataFilePath == null || artMetadataFilePath.trim().isEmpty()) {
			throw new MissingMetadataException("Missing or art invalid art metadata file path!");
		}
		
		return readMetadataFrom(new File(artMetadataFilePath));
	}

	public static JSONObject readMetadataFrom(File artMetadataFile) throws IllegalArgumentException, MissingMetadataException, InvalidMetadataException, JSONException, IOException {
		if(artMetadataFile == null || !artMetadataFile.isFile() || !artMetadataFile.exists()) {
			throw new MissingMetadataException("Missing or invalid art metadata file!");
		}
		
		JSONObject artMetadata = new JSONObject(new JSONTokener(new InputStreamReader(new FileInputStream(artMetadataFile))));
		
		if(!artMetadata.has(TILES_ATTRIBUTE_NAME)) {
			int localTileStart = artMetadata.getInt(LOCAL_TILE_START_ATTRIBUTE_NAME);

			if(localTileStart < 0) {
				throw new InvalidMetadataException("Invalid local tile start metadata value: " + localTileStart + ", expected non-negative integer.");
			}
			
			int localTileEnd = artMetadata.getInt(LOCAL_TILE_END_ATTRIBUTE_NAME);
			
			if(localTileEnd < 0) { 
				throw new InvalidMetadataException("Invalid local tile end metadata value: " + localTileEnd + ", expected non-negative integer.");
			}

			if(localTileEnd < localTileStart) {
				throw new InvalidMetadataException("Invalid local tile end metadata value: " + localTileEnd + ", expected value to be larger than or equal to the local tile start value: " + localTileStart + ".");
			}

			int numberOfTiles = localTileEnd - localTileStart + 1;
			int tileNumber = 0;
			File tileMetadataFile = null;
			JSONArray tilesMetadata = new JSONArray(numberOfTiles);
			
// TODO: make sure this art metadata path is correct:
			String basePath = Utilities.getFilePath(artMetadataFile);

			for(int i = 0; i < numberOfTiles; i++) {
// TODO: re-factor this into readFrom in Tile:
				tileNumber = localTileStart + i;
				tileMetadataFile = new File(Utilities.joinPaths(basePath, "TILE" + Utilities.addLeadingZeroes(tileNumber, 4) + ".JSON"));
				
				if(!tileMetadataFile.exists() || !tileMetadataFile.isFile()) {
					throw new MissingMetadataException("Missing tile metadata file for tile #" + tileNumber + " at: '" + tileMetadataFile.getPath() + "'.");
				}

				tilesMetadata.put(i, new JSONObject(new JSONTokener(new InputStreamReader(new FileInputStream(tileMetadataFile)))));
			}
			
			artMetadata.put(TILES_ATTRIBUTE_NAME, tilesMetadata);
		}
		
		validateMetadata(artMetadata);
		
		return artMetadata;
	}

	public void writeMetadataTo(String directoryPath) throws IllegalArgumentException, MetadataWriteException, IOException {
		writeMetadataTo(directoryPath, true);
	}
	
	public void writeMetadataTo(File directory) throws IllegalArgumentException, MetadataWriteException, IOException {
		writeMetadataTo(directory, true);
	}

	public void writeMetadataTo(String directoryPath, boolean overwrite) throws IllegalArgumentException, MetadataWriteException, IOException {
		writeMetadataTo(directoryPath, overwrite, TileMetadataType.External);
	}
	
	public void writeMetadataTo(File directory, boolean overwrite) throws IllegalArgumentException, MetadataWriteException, IOException {
		writeMetadataTo(directory, overwrite, TileMetadataType.External);
	}

	public void writeMetadataTo(String directoryPath, boolean overwrite, TileMetadataType metadataType) throws IllegalArgumentException, MetadataWriteException, IOException {
		if(directoryPath == null || directoryPath.trim().isEmpty()) {
			throw new IllegalArgumentException("Missing or invalid directory path!");
		}

		writeMetadataTo(new File(directoryPath), true, metadataType);
	}

	public void writeMetadataTo(File directory, boolean overwrite, TileMetadataType metadataType) throws IllegalArgumentException, MetadataWriteException, IOException {
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
		out.write(getMetadata(metadataType == TileMetadataType.Embedded).toString(4));
		out.close();
		
		if(metadataType == TileMetadataType.External) {
			int numberOfTiles = numberOfTiles();

			for(int i = 0; i < numberOfTiles; i++) {
				m_tiles[i].writeMetadataTo(directory);
			}
		}
	}

	public static boolean isValidMetadata(JSONObject artMetadata) {
		return isValidMetadata(artMetadata, false);
	}
		
	public static boolean isValidMetadata(JSONObject artMetadata, boolean requireTilesMetadata) {
		try {
			return validateMetadata(artMetadata, false, requireTilesMetadata);
		}
		catch(InvalidMetadataException exception) {
			return false;
		}
	}

	public static boolean validateMetadata(JSONObject artMetadata) throws InvalidMetadataException {
		return validateMetadata(artMetadata, true);
	}

	public static boolean validateMetadata(JSONObject artMetadata, boolean throwErrors) throws InvalidMetadataException {
		return validateMetadata(artMetadata, throwErrors, true);
	}
	
	public static boolean validateMetadata(JSONObject artMetadata, boolean throwErrors, boolean requireTilesMetadata) throws InvalidMetadataException {
		try {
			if(artMetadata == null) {
				throw new IllegalArgumentException("Missing art metadata.");
			}

			if(!artMetadata.has(LOCAL_TILE_START_ATTRIBUTE_NAME)) {
				throw new InvalidMetadataException("Missing 'localTileStart' metadata value.");
			}

			int localTileStart = 0;
			
			try {
				localTileStart = artMetadata.getInt(LOCAL_TILE_START_ATTRIBUTE_NAME);
			}
			catch(JSONException exception) {
				throw new InvalidMetadataException("Invalid 'localTileStart' metadata value type, expected integer.");
			}

			if(localTileStart < 0) {
				throw new InvalidMetadataException("Invalid 'localTileStart' metadata value: " + localTileStart + ", expected non-negative integer.");
			}
			
			if(!artMetadata.has(LOCAL_TILE_END_ATTRIBUTE_NAME)) {
				throw new InvalidMetadataException("Missing 'localTileStart' metadata value.");
			}
			
			int localTileEnd = 0;

			try {
				localTileEnd = artMetadata.getInt(LOCAL_TILE_END_ATTRIBUTE_NAME);
			}
			catch(JSONException exception) {
				throw new InvalidMetadataException("Invalid 'localTileEnd' metadata value type, expected integer.");
			}
			
			if(localTileEnd < 0) { 
				throw new InvalidMetadataException("Invalid 'localTileEnd' metadata value: " + localTileEnd + ", expected non-negative integer.");
			}

			if(localTileEnd < localTileStart) {
				throw new InvalidMetadataException("Invalid 'localTileEnd' metadata value: " + localTileEnd + ", expected value to be larger than or equal to the 'localTileStart' value: " + localTileStart + ".");
			}

			if(!artMetadata.has(LEGACY_TILE_COUNT_ATTRIBUTE_NAME)) {
				throw new InvalidMetadataException("Missing 'legacyTileCount' metadata value.");
			}
			
			try {
				artMetadata.getInt(LEGACY_TILE_COUNT_ATTRIBUTE_NAME);
			}
			catch(JSONException exception) {
				throw new InvalidMetadataException("Invalid 'legacyTileCount' metadata value type, expected integer.");
			}

			if(artMetadata.has(TILES_ATTRIBUTE_NAME)) {
				JSONArray tilesMetadata = null;
				
				try {
					tilesMetadata = artMetadata.getJSONArray(TILES_ATTRIBUTE_NAME);
				}
				catch(JSONException exception) {
					if(!throwErrors) {
						return false;
					}
					
					throw new InvalidMetadataException("Art metadata has an invalid 'tiles' metadata value type.");
				}

				int numberOfTiles = localTileEnd - localTileStart + 1;
				
				if(tilesMetadata.length() != numberOfTiles) {
					throw new InvalidMetadataException("Incomplete tile metadata, expected " + numberOfTiles + " entries, but found " + tilesMetadata.length() + " instead.");
				}
				
				JSONObject tileMetadata = null;
				HashMap<Integer, Boolean> validatedTilesAttributes = new HashMap<Integer, Boolean>(numberOfTiles);
				
				for(int i = 0; i < tilesMetadata.length(); i++) {
					try {
						tileMetadata = tilesMetadata.getJSONObject(i);
					}
					catch(JSONException exception) {
						if(!throwErrors) {
							return false;
						}
						
						throw new InvalidMetadataException("Tile metadata at index " + i + " is not an object.");
					}
					
					try {
						if(!Tile.validateMetadata(tileMetadata, throwErrors)) {
							return false;
						}
					}
					catch(InvalidMetadataException exception) {
						if(!throwErrors) {
							return false;
						}
						
						throw new InvalidMetadataException("Tile metadata at index " + i + " is invalid: " + exception.getMessage());
					}
					
					int number = 0;

					try {
						number = tileMetadata.getInt(Tile.NUMBER_ATTRIBUTE_NAME);
					}
					catch(JSONException exception) {
						if(!throwErrors) {
							return false;
						}
						
						throw new InvalidMetadataException("Tile metadata at index " + i + " has an invalid 'number' metadata value type.");
					}

					if(number < localTileStart || number > localTileEnd) {
						throw new InvalidMetadataException("Tile metadata at index " + i + " has an invalid tile number, expected number between " + localTileStart + " and " + localTileEnd + ", inclusively.");
					}
					
					if(validatedTilesAttributes.containsKey(number)) {
						throw new InvalidMetadataException("Duplicate tile #" + number + " encountered in tile metadata.");
					}
					
					validatedTilesAttributes.put(number, true);
				}
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
	
	public byte[] serialize() {
		if(m_tiles == null) {
			return null;
		}

		byte data[] = new byte[getArtFileSize()];
		int offset = 0;
		Tile tile = null;

		System.arraycopy(Serializer.serializeInteger(VERSION, FILE_ENDIANNESS), 0, data, offset, 4);
		offset += 4;
		
		System.arraycopy(Serializer.serializeInteger(m_legacyTileCount, FILE_ENDIANNESS), 0, data, offset, 4);
		offset += 4;
		
		System.arraycopy(Serializer.serializeInteger(m_localTileStart, FILE_ENDIANNESS), 0, data, offset, 4);
		offset += 4;
		
		System.arraycopy(Serializer.serializeInteger(m_localTileEnd, FILE_ENDIANNESS), 0, data, offset, 4);
		offset += 4;
		
		for(int i = 0; i < m_tiles.length; i++) {
			System.arraycopy(Serializer.serializeShort((short) m_tiles[i].getWidth(), FILE_ENDIANNESS), 0, data, offset, 2);
			offset += 2;
		}

		for(int i = 0; i < m_tiles.length; i++) {
			System.arraycopy(Serializer.serializeShort((short) m_tiles[i].getHeight(), FILE_ENDIANNESS), 0, data, offset, 2);
			offset += 2;
		}

		for(int i = 0; i < m_tiles.length; i++) {
			System.arraycopy(Serializer.serializeInteger(m_tiles[i].getAttributes().pack(), FILE_ENDIANNESS), 0, data, offset, 4);
			offset += 4;
		}

		for(int i = 0; i < m_tiles.length; i++) {
			tile = m_tiles[i];

			if(tile.isEmpty()) {
				continue;
			}

			System.arraycopy(tile.getData(), 0, data, offset, tile.getSize());
			offset += tile.getSize();
		}

		return data;
	}
	
	public static Art deserialize(byte data[]) throws ArtDeserializationException {
		if(data == null) {
			throw new ArtDeserializationException("Invalid art file data.");
		}
		
		int offset = 0;
		
		// verify that the data is long enough to contain header information
		if(data.length < offset + HEADER_LENGTH) {
			throw new ArtDeserializationException("Art data is incomplete or corrupted: missing header data.");
		}

		// read and verify the art file version
		int version = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + 4), FILE_ENDIANNESS);
		offset += 4;

		if(version != VERSION) { 
			throw new ArtDeserializationException("Invalid or unsupported art file version: " + version + ", expected " + VERSION + ".");
		}

		SystemConsole.instance.writeLine("Verified art file version.");
		
		// read legacy tile count value
		int legacyTileCount = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + 4), FILE_ENDIANNESS);
		offset += 4;

		if(legacyTileCount < 0) { 
			throw new ArtDeserializationException("Invalid legacy tile count value: " + legacyTileCount + ", expected non-negative integer.");
		}

		// read local tile start value
		int localTileStart = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + 4), FILE_ENDIANNESS);
		offset += 4;

		if(localTileStart < 0) { 
			throw new ArtDeserializationException("Invalid local tile start value: " + localTileStart + ", expected non-negative integer.");
		}

		// read local tile end value
		int localTileEnd = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + 4), FILE_ENDIANNESS);
		offset += 4;

		if(localTileEnd < 0) { 
			throw new ArtDeserializationException("Invalid local tile end value: " + localTileEnd + ", expected non-negative integer.");
		}
		
		if(localTileEnd < localTileStart) {
			throw new ArtDeserializationException("Invalid local tile end value: " + localTileEnd + ", expected value to be larger than or equal to the local tile start value: " + localTileStart + ".");
		}

		SystemConsole.instance.writeLine("Verified art file header.");
		
		int numberOfTiles = localTileEnd - localTileStart + 1;
		
		if(data.length - offset < numberOfTiles * 8) {
			throw new ArtDeserializationException("Art data corrupted or invalid, missing full sprite property data.");
		}
		
		int tileWidths[] = new int[numberOfTiles];
		int tileHeights[] = new int[numberOfTiles];
		int tileAttributes[] = new int[numberOfTiles];
		
		// read tile widths
		for(int i = 0; i < numberOfTiles; i++) {
			tileWidths[i] = Short.toUnsignedInt(Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), FILE_ENDIANNESS));
			offset += 2;
		}

		// read tile heights
		for(int i = 0; i < numberOfTiles; i++) {
			tileHeights[i] = Short.toUnsignedInt(Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), FILE_ENDIANNESS));
			offset += 2;
		}

		// read tile attributes
		for(int i = 0; i < numberOfTiles; i++) {
			tileAttributes[i] = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + 4), FILE_ENDIANNESS);
			offset += 4;
		}
		
		// read tile data and instantiate tile objects
		Art art;
		try {
			art = new Art(localTileStart, localTileEnd, legacyTileCount);
		}
		catch(InvalidFileTypeException error) {
			throw new ArtDeserializationException(error.getMessage());
		}
		catch(UnsupportedFileTypeException error) {
			throw new ArtDeserializationException(error.getMessage());
		}

		for(int i = 0; i < numberOfTiles; i++) {
			int numberOfPixels = tileWidths[i] * tileHeights[i];

			if(data.length - offset < numberOfPixels) {
				throw new ArtDeserializationException("Art data corrupted or invalid, missing tile #" + (localTileStart + i) + " pixel data.");
			}
			
			if(!art.setTile(new Tile(localTileStart + i, tileWidths[i], tileHeights[i], Arrays.copyOfRange(data, offset, offset + numberOfPixels), tileAttributes[i], art))) {
				throw new ArtDeserializationException("Failed to set tile #" + (localTileStart + i) + "!");
			}
			
			offset += numberOfPixels;
		}
		
		int remainingBytes = data.length - offset;
		
		if(remainingBytes != 0) {
			SystemConsole.instance.writeLine("Additional " + remainingBytes + " found at the end of the art file data.");
		}
		
		return art;
	}

// TODO: all items should probably be void and throw exceptions instead, will need to update calls to save, also add writeTo / readFrom abstractions?
	public boolean load() throws ArtReadException, ArtDeserializationException {
		if(m_file == null || !m_file.exists()) { return false; }
		
		// verify that the file has an extension
		String extension = Utilities.getFileExtension(m_file.getName());
		if(extension == null) {
			throw new ArtReadException("File \"" + m_file.getName() + "\" has no extension.");
		}
		
		// verify that the file extension is supported
		if(!hasFileTypeWithExtension(extension)) {
			throw new ArtReadException("File \"" + m_file.getName() +  "\" has unsupported extension: " + extension);
		}
		
		// check to make sure that the file is not too big to be stored in memory
		if(m_file.length() > Integer.MAX_VALUE) {
			throw new ArtReadException("File \"" + m_file.getName() +  "\" is too large to store in memory.");
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
			throw new ArtReadException("File \"" + m_file.getName() +  "\" not found.");
		}
		catch(IOException e) {
			throw new ArtReadException("Error reading file \"" + m_file.getName() +  "\": " + e.getMessage());
		}
		
		SystemConsole.instance.writeLine("Opened " + FILE_TYPES[0].getName() + " file: \"" + m_file.getName() + "\", loaded " + data.length + " bytes into memory.");
		
		Art art = Art.deserialize(data);
		m_localTileStart = art.m_localTileStart;
		m_localTileEnd = art.m_localTileEnd;
		m_legacyTileCount = art.m_legacyTileCount;
		m_tiles = art.m_tiles;
		
		for(int i = 0; i < m_tiles.length; i++) {
			m_tiles[i].setParent(this);
		}

// TODO: ideally this should be in the initialize function, but it shows up twice when deserializing.. maybe find a way to re-factor that...:
		if(m_palette == Palette.DEFAULT_PALETTE) {
			SystemConsole.instance.writeLine(toString() + " using default palette.");
		}
		else if(m_palette == Editor.palette) {
			SystemConsole.instance.writeLine(toString() + " using custom palette.");
		}
		else {
			SystemConsole.instance.writeLine(toString() + " using automatic relative palette override.");
		}
		
		if(m_lookup == Palette.DEFAULT_LOOKUP) {
			SystemConsole.instance.writeLine(toString() + " using default lookup.");
		}
		else if(m_lookup == Editor.lookup) {
			SystemConsole.instance.writeLine(toString() + " using custom lookup.");
		}
		else {
			SystemConsole.instance.writeLine(toString() + " using automatic relative lookup override.");
		}

		SystemConsole.instance.writeLine(FILE_TYPES[0].getName() + " file parsed successfully, " + m_tiles.length + " tiles loaded into memory.");
		
		return true;
	}
	
// TODO: all items should probably be void and throw exceptions instead, will need to update calls to save, also add writeTo / readFrom abstractions?
	public boolean save() throws ArtWriteException {
		if(m_file == null || m_tiles ==  null) { return false; }
		
		byte data[] = serialize();
		
		SystemConsole.instance.writeLine("Writing " + FILE_TYPES[0].getName() + " data to file: \"" + m_file.getName() + "\".");
		
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(m_file));
			out.write(data);
			out.close();
		}
		catch(IOException e) {
			throw new ArtWriteException("Error writing to file: " + m_file.getName() +  ": " + e.getMessage());
		}
		
		SystemConsole.instance.writeLine(FILE_TYPES[0].getName() + " file writing complete!");
		
		setChanged(false);
		
		return true;
	}

	public int numberOfArtChangeListeners() {
		return m_artChangeListeners.size();
	}
	
	public ArtChangeListener getArtChangeListener(int index) {
		if(index < 0 || index >= m_artChangeListeners.size()) { return null; }
		
		return m_artChangeListeners.elementAt(index);
	}
	
	public boolean hasArtChangeListener(ArtChangeListener c) {
		return m_artChangeListeners.contains(c);
	}
	
	public int indexOfArtChangeListener(ArtChangeListener c) {
		return m_artChangeListeners.indexOf(c);
	}
	
	public boolean addArtChangeListener(ArtChangeListener c) {
		if(c == null || m_artChangeListeners.contains(c)) { return false; }
		
		m_artChangeListeners.add(c);
		
		return true;
	}
	
	public boolean removeArtChangeListener(int index) {
		if(index < 0 || index >= m_artChangeListeners.size()) { return false; }
		
		m_artChangeListeners.remove(index);
		
		return true;
	}
	
	public boolean removeArtChangeListener(ArtChangeListener c) {
		if(c == null) { return false; }
		
		return m_artChangeListeners.remove(c);
	}
	
	public void clearArtChangeListeners() {
		m_artChangeListeners.clear();
	}
	
	public void notifyNumberOfTilesChanged() {
		for(int i=0;i<m_artChangeListeners.size();i++) {
			m_artChangeListeners.elementAt(i).handleNumberOfTilesChanged(this);
		}
	}
	
	public void handleTileChange(Tile tile) {
		setChanged(true);
	}

	public void handleTileAttributeChange(TileAttributes attributes, TileAttribute attribute, byte value) { }

	public boolean equals(Object o) {
		if(o == null || !(o instanceof Art)) {
			return false;
		}
		
		Art a = (Art) o;
		
		if(m_localTileStart != a.m_localTileStart ||
		   m_localTileEnd != a.m_localTileEnd ||
		   m_tiles.length != a.m_tiles.length) {
			return false;
		}
		
		for(int i = 0; i < m_tiles.length; i++) {
			if(!m_tiles[i].equals(a.m_tiles[i])) {
				return false;
			}
		}
		
		return true;
	}
	
	public String toString() {
		return getDisplayName();
	}
	
}
