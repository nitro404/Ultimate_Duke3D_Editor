package map;

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import console.*;
import exception.*;
import item.*;
import utilities.*;
import org.json.*;

public class Map extends Item implements PlayerSpawnChangeListener, SectorChangeListener, WallChangeListener, SpriteChangeListener {

	protected int m_version;
	protected PlayerSpawn m_playerSpawn;
	protected Vector<Sector> m_sectors;
	protected Vector<Wall> m_walls;
	protected Vector<Sprite> m_sprites;
	protected byte[] m_trailingData;
	protected boolean m_initialized;

	public static final int DEFAULT_VERSION = 7;

	public static final Endianness FILE_ENDIANNESS = Endianness.LittleEndian;

	public static final String VERSION_ATTRIBUTE_NAME = "version";
	public static final String PLAYER_SPAWN_ATTRIBUTE_NAME = "playerSpawn";
	public static final String SECTORS_ATTRIBUTE_NAME = "sectors";
	public static final String WALLS_ATTRIBUTE_NAME = "walls";
	public static final String SPRITES_ATTRIBUTE_NAME = "sprites";
	public static final String TRAILING_DATA_ATTRIBUTE_NAME = "trailingData";

	public static ItemFileType[] FILE_TYPES = {
		new ItemFileType("Duke Nukem 3D Map", "MAP")
	};

	public Map() {
		super();

		initialize();
	}
	
	public Map(String fileName) {
		super(fileName);

		initialize();
	}
	
	public Map(File file) {
		super(file);

		initialize();
	}
	
	public Map(ItemFileType fileType) throws InvalidFileTypeException, UnsupportedFileTypeException {
		super(fileType);

		initialize();
	}

	public Map(String fileName, ItemFileType fileType) throws InvalidFileTypeException, UnsupportedFileTypeException {
		super(fileName, fileType);

		initialize();
	}
	
	public Map(File file, ItemFileType fileType) throws InvalidFileTypeException, UnsupportedFileTypeException {
		super(file, fileType);

		initialize();
	}

	public boolean isInstantiable() {
		return true;
	}

	public boolean isInitialized() {
		return m_initialized;
	}

	protected void initialize() {
		if(m_initialized) {
			return;
		}

		m_version = DEFAULT_VERSION;
		m_playerSpawn = new PlayerSpawn();
		m_sectors = new Vector<Sector>();
		m_walls = new Vector<Wall>();
		m_sprites = new Vector<Sprite>();
		m_trailingData = new byte[0];
		
		m_initialized = true;
	}

	public Endianness getFileEndianness() {
		return FILE_ENDIANNESS;
	}

	public ItemFileType[] getFileTypes() {
		return FILE_TYPES;
	}

	public int getMapFileSize() {
		return 10 + PlayerSpawn.SIZE + (Sector.getSizeForVersion(m_version) * m_sectors.size()) + (Wall.SIZE * m_walls.size()) + (Sprite.SIZE * m_sprites.size()) + m_trailingData.length;
	}

	public String getMapFileSizeString() {
		int size = getMapFileSize();

		if(size < 1000) {
			return size + " B";
		}
		else if(size < 1000000) {
			return String.format("%.2f", size / 1000.0) + " KB";
		}
		else {
			return String.format("%.2f", size / 1000000.0) + " MB";
		}
	}

	public int getVersion() {
		return m_version;
	}

	public void setVersion(int version) throws IllegalArgumentException {
		if(version < 1) {
			throw new IllegalArgumentException("Invalid map version, expected positive number.");
		}

		if(m_version != version) {
			m_version = version;

			setChanged(true);
		}
	}

	public int getPlayerSpawnX() {
		return m_playerSpawn.getX();
	}

	public void setPlayerSpawnX(int x) {
		m_playerSpawn.setX(x);
	}

	public int getPlayerSpawnY() {
		return m_playerSpawn.getY();
	}

	public void setPlayerSpawnY(int y) {
		m_playerSpawn.setY(y);
	}

	public int getPlayerSpawnZ() {
		return m_playerSpawn.getZ();
	}

	public void setPlayerSpawnZ(int z) {
		m_playerSpawn.setZ(z);
	}

	public Position getPlayerSpawnPosition() {
		return m_playerSpawn.getPosition();
	}

	public void setPlayerSpawnPosition(Position position) throws IllegalArgumentException {
		m_playerSpawn.setPosition(position);
	}

	public short getPlayerSpawnAngle() {
		return m_playerSpawn.getAngle();
	}

	public void setPlayerSpawnAngle(short angle) {
		m_playerSpawn.setAngle(angle);
	}

	public short getPlayerSpawnSectorNumber() {
		return m_playerSpawn.getSectorNumber();
	}

	public void setPlayerSpawnSectorNumber(short sectorNumber) {
		m_playerSpawn.setSectorNumber(sectorNumber);
	}

	public PlayerSpawn getPlayerSpawn() {
		return m_playerSpawn;
	}

	public void setPlayerSpawn(PlayerSpawn playerSpawn) throws IllegalArgumentException {
		if(playerSpawn == null) {
			throw new IllegalArgumentException("Invalid player spawn.");
		}

		if(!m_playerSpawn.equals(playerSpawn)) {
			m_playerSpawn = playerSpawn.clone();

			setChanged(true);
		}
	}

	public int numberOfSectors() {
		return m_sectors.size();
	}

	public boolean hasSector(Sector sector) {
		if(sector == null) {
			return false;
		}

		for(int i = 0; i < m_sectors.size(); i++) {
			if(m_sectors.elementAt(i).equals(sector)) {
				return true;
			}
		}

		return false;
	}

	public int indexOfSector(Sector sector) {
		if(sector == null) {
			return -1;
		}

		for(int i = 0; i < m_sectors.size(); i++) {
			if(m_sectors.elementAt(i).equals(sector)) {
				return i;
			}
		}

		return -1;
	}

	public Sector getSector(short sectorNumber) {
		if(sectorNumber < 0 || sectorNumber >= m_sectors.size()) {
			return null;
		}

		return m_sectors.elementAt(sectorNumber);
	}

	public boolean addSector(Sector sector) throws MaxSectorsExceededException {
		if(sector == null) {
			return false;
		}

		if(m_sectors.size() >= BuildConstants.MAX_NUMBER_OF_SECTORS) {
			throw new MaxSectorsExceededException("Failed to add sector, maximum number of sectors reached in current map.");
		}

		Sector newSector = sector.clone();
		newSector.setMap(this);
		m_sectors.add(newSector);

		setChanged(true);

		return true;
	}

	public boolean addSectors(Vector<Sector> sectors) throws MaxSectorsExceededException {
		if(sectors == null) {
			return false;
		}

		boolean sectorAdded = false;

		for(int i = 0; i < sectors.size(); i++) {;
			if(addSector(sectors.elementAt(i))) {
				sectorAdded = true;
			}
		}

		return sectorAdded;
	}

	public int numberOfWalls() {
		return m_walls.size();
	}

	public boolean hasWall(Wall wall) {
		if(wall == null) {
			return false;
		}

		for(int i = 0; i < m_walls.size(); i++) {
			if(m_walls.elementAt(i).equals(wall)) {
				return true;
			}
		}

		return false;
	}

	public int indexOfWall(Wall wall) {
		if(wall == null) {
			return -1;
		}

		for(int i = 0; i < m_walls.size(); i++) {
			if(m_walls.elementAt(i).equals(wall)) {
				return i;
			}
		}

		return -1;
	}

	public Wall getWall(short wallNumber) {
		if(wallNumber < 0 || wallNumber >= m_walls.size()) {
			return null;
		}

		return m_walls.elementAt(wallNumber);
	}

	public boolean addWall(Wall wall) throws MaxWallsExceededException {
		if(wall == null) {
			return false;
		}

		if(m_walls.size() >= BuildConstants.MAX_NUMBER_OF_WALLS) {
			throw new MaxWallsExceededException("Failed to add wall, maximum number of walls reached in current map.");
		}

		Wall newWall = wall.clone();
		newWall.setMap(this);
		m_walls.add(newWall);

		setChanged(true);

		return true;
	}

	public boolean addWalls(Vector<Wall> walls) throws MaxWallsExceededException {
		if(walls == null) {
			return false;
		}

		boolean wallAdded = false;

		for(int i = 0; i < walls.size(); i++) {;
			if(addWall(walls.elementAt(i))) {
				wallAdded = true;
			}
		}

		return wallAdded;
	}

	public int numberOfSprites() {
		return m_sprites.size();
	}

	public boolean hasSprite(Sprite sprite) {
		if(sprite == null) {
			return false;
		}

		for(int i = 0; i < m_sprites.size(); i++) {
			if(m_sprites.elementAt(i).equals(sprite)) {
				return true;
			}
		}

		return false;
	}

	public int indexOfSprite(Sprite sprite) {
		if(sprite == null) {
			return -1;
		}

		for(int i = 0; i < m_sprites.size(); i++) {
			if(m_sprites.elementAt(i).equals(sprite)) {
				return i;
			}
		}

		return -1;
	}

	public Sprite getSprite(short spriteNumber) {
		if(spriteNumber < 0 || spriteNumber >= m_sprites.size()) {
			return null;
		}

		return m_sprites.elementAt(spriteNumber);
	}

	public boolean addSprite(Sprite sprite) throws MaxSpritesExceededException {
		if(sprite == null) {
			return false;
		}

		if(m_sprites.size() >= BuildConstants.MAX_NUMBER_OF_SPRITES) {
			throw new MaxSpritesExceededException("Failed to add sprite, maximum number of sprites reached in current map.");
		}

		Sprite newSprite = sprite.clone();
		newSprite.setMap(this);
		m_sprites.add(newSprite);

		setChanged(true);

		return true;
	}

	public boolean addSprites(Vector<Sprite> sprites) throws MaxSpritesExceededException {
		if(sprites == null) {
			return false;
		}

		boolean spriteAdded = false;

		for(int i = 0; i < sprites.size(); i++) {;
			if(addSprite(sprites.elementAt(i))) {
				spriteAdded = true;
			}
		}

		return spriteAdded;
	}

	public byte[] getTrailingData() {
		return m_trailingData;
	}

	public void setTrailingData(byte [] trailingData) {
		if(trailingData == null) {
			m_trailingData = new byte[0];
		}
		else {
			m_trailingData = Arrays.copyOfRange(trailingData, 0, trailingData.length);
		}
	}

	public void clearTrailingData() {
		setTrailingData(null);
	}

	public Vector<Sound> getSounds() {
		Vector<Sound> sounds = new Vector<Sound>();
		Sector sector = null;
		Sprite sprite = null;

		for(int i = 0; i < m_sectors.size(); i++) {
			sector = m_sectors.elementAt(i);

			if(sector.hasOneTimeSound()) {
				sounds.add(new OneTimeSectorSound(sector));
			}
		}

		for(int i = 0; i < m_sprites.size(); i++) {
			sprite = m_sprites.elementAt(i);

			if(sprite.hasPrimarySound()) {
				if(sprite.isSwitchWithSoundOverride()) {
					sounds.add(new SwitchSpriteSound(sprite));
				}
				else {
					sounds.add(new PrimarySpriteSound(sprite));
				}
			}

			if(sprite.hasSecondarySound()) {
				sounds.add(new SecondarySpriteSound(sprite));
			}
		}

		return sounds;
	}

	public Vector<Sound> offsetSoundRange(short soundRangeStart, short soundRangeEnd, short soundRangeOffset) throws IllegalArgumentException, SoundNumberUnderflowException, SoundNumberOverflowException {
		return offsetSoundRange(soundRangeStart, soundRangeEnd, soundRangeOffset, false);
	}

	public Vector<Sound> offsetSoundRange(short soundRangeStart, short soundRangeEnd, short soundRangeOffset, boolean verbose) throws IllegalArgumentException, SoundNumberUnderflowException, SoundNumberOverflowException {
		if(soundRangeStart < 0 || soundRangeStart > BuildConstants.MAX_NUMBER_OF_SOUNDS) {
			throw new IllegalArgumentException("Invalid sound range start, expected number between 0 and " + BuildConstants.MAX_NUMBER_OF_SOUNDS + ", inclusively.");
		}

		if(soundRangeEnd < 0 || soundRangeEnd > BuildConstants.MAX_NUMBER_OF_SOUNDS) {
			throw new IllegalArgumentException("Invalid sound range end, expected number between 0 and " + BuildConstants.MAX_NUMBER_OF_SOUNDS + ", inclusively.");
		}

		if(soundRangeOffset < -BuildConstants.MAX_NUMBER_OF_SOUNDS || soundRangeOffset > BuildConstants.MAX_NUMBER_OF_SOUNDS) {
			throw new IllegalArgumentException("Invalid sound range offset, expected number between " + (-BuildConstants.MAX_NUMBER_OF_SOUNDS) + " and " + BuildConstants.MAX_NUMBER_OF_SOUNDS + ", inclusively.");
		}

		if(soundRangeStart > soundRangeEnd) {
			throw new IllegalArgumentException("Invalid sound range.");
		}

		short soundNumber = -1;
		short newSoundNumber = -1;
		Sound sound = null;
		Vector<Sound> sounds = getSounds();
		Vector<Sound> offsetSounds = new Vector<Sound>();

		for(int i = 0; i < sounds.size(); i++) {
			sound = sounds.elementAt(i);
			soundNumber = sound.getSoundNumber();

			if(soundNumber < soundRangeStart || soundNumber > soundRangeEnd) {
				continue;
			}

			newSoundNumber = (short) (soundNumber + soundRangeOffset);

			if(newSoundNumber < 0) {
				throw new SoundNumberUnderflowException("Sound #" + (i + 1) + " with value " + soundNumber + " would underflow to " + soundNumber + soundRangeOffset + ".");
			}
			else if(newSoundNumber > BuildConstants.MAX_NUMBER_OF_SOUNDS) {
				throw new SoundNumberOverflowException("Sound #" + (i + 1) + " with value " + soundNumber + " would overflow to " + newSoundNumber + ", which exceeds the max sound value of " + BuildConstants.MAX_NUMBER_OF_SOUNDS + ".");
			}
		}

		for(int i = 0; i < sounds.size(); i++) {
			sound = sounds.elementAt(i);
			soundNumber = sound.getSoundNumber();

			if(soundNumber < soundRangeStart || soundNumber > soundRangeEnd) {
				continue;
			}

			newSoundNumber = (short) (soundNumber + soundRangeOffset);

// TODO: better way to do this?

			if(verbose) {
				SystemConsole.instance.writeLine("Offsetting sound #" + (i + 1) + " from value " + soundNumber + " to " + newSoundNumber + ".");
			}

			sound.setSoundNumber(newSoundNumber);

// TODO: does not mark level as changed

			offsetSounds.add(sound);
		}

		if(verbose) {
			SystemConsole.instance.writeLine("Offset " + offsetSounds.size() + " sound" + (offsetSounds.size() == 1 ? "" : "s") + ".");
		}

		return offsetSounds;
	}

	public byte[] serialize() {
		byte data[] = new byte[getMapFileSize()];
		int offset = 0;

		// serialize the map version
		System.arraycopy(Serializer.serializeInteger(m_version, FILE_ENDIANNESS), 0, data, offset, 4);
		offset += 4;

		// serialize the player spawn information
		System.arraycopy(m_playerSpawn.serialize(FILE_ENDIANNESS), 0, data, offset, PlayerSpawn.SIZE);
		offset += PlayerSpawn.SIZE;

		// serialize the number of sectors
		System.arraycopy(Serializer.serializeShort((short) m_sectors.size(), FILE_ENDIANNESS), 0, data, offset, 2);
		offset += 2;

		// serialize the sectors
		int sectorSize = Sector.getSizeForVersion(m_version);

		for(int i = 0; i < m_sectors.size(); i++) {
			System.arraycopy(m_sectors.elementAt(i).serialize(m_version, FILE_ENDIANNESS), 0, data, offset, sectorSize);
			offset += sectorSize;
		}

		// serialize the number of walls
		System.arraycopy(Serializer.serializeShort((short) m_walls.size(), FILE_ENDIANNESS), 0, data, offset, 2);
		offset += 2;

		// serialize the walls
		for(int i = 0; i < m_walls.size(); i++) {
			System.arraycopy(m_walls.elementAt(i).serialize(m_version, FILE_ENDIANNESS), 0, data, offset, Wall.SIZE);
			offset += Wall.SIZE;
		}

		// serialize the number of sprites
		System.arraycopy(Serializer.serializeShort((short) m_sprites.size(), FILE_ENDIANNESS), 0, data, offset, 2);
		offset += 2;

		// serialize the sprites
		int spriteSize = Sprite.SIZE;

		for(int i = 0; i < m_sprites.size(); i++) {
			System.arraycopy(m_sprites.elementAt(i).serialize(m_version, FILE_ENDIANNESS), 0, data, offset, spriteSize);
			offset += spriteSize;
		}

		System.arraycopy(m_trailingData, 0, data, offset, m_trailingData.length);
		offset += m_trailingData.length;

		return data;
	}

	public static Map deserialize(byte data[]) throws DeserializationException, MaxSectorsExceededException, MaxWallsExceededException, MaxSpritesExceededException {
		if(data == null) {
			throw new MapDeserializationException("Invalid map data.");
		}

		// verify that the data is long enough to contain required information
		if(data.length < PlayerSpawn.SIZE + 4) {
			throw new MapDeserializationException("Map data is incomplete or corrupted, missing version, player spawn and number of sectors information.");
		}

		int offset = 0;

		// deserialize the map version
		int version = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + 4), FILE_ENDIANNESS);
		offset += 4;

		// deserialize the player spawn information
		PlayerSpawn playerSpawn = PlayerSpawn.deserialize(data, offset, FILE_ENDIANNESS);
		offset += PlayerSpawn.SIZE;

		// deserialize the number of sectors
		int numberOfSectors = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), FILE_ENDIANNESS) & 0xffff;
		offset += 2;

		// verify that maximum number of sectors is not exceeded
		if(numberOfSectors > BuildConstants.MAX_NUMBER_OF_SECTORS) {
			throw new MaxSectorsExceededException("Maximum number of sectors exceeded.");
		}

		// deserialize the sectors
		int sectorSize = Sector.getSizeForVersion(version);
		Vector<Sector> sectors = new Vector<Sector>(numberOfSectors);

		for(int i = 0; i < numberOfSectors; i++) {
			if(data.length - offset < sectorSize) {
				throw new MapDeserializationException("Map data is incomplete or corrupted, missing sector #" + (i + 1) + " information.");
			}

			// deserialize the sector
			sectors.add(Sector.deserialize(data, version, offset, FILE_ENDIANNESS));
			offset += sectorSize;
		}

		if(data.length - offset < 2) {
			throw new MapDeserializationException("Map data is incomplete or corrupted, missing number of walls information.");
		}

		// deserialize the number of walls
		int numberOfWalls = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), FILE_ENDIANNESS) & 0xffff;
		offset += 2;

		// verify that maximum number of walls is not exceeded
		if(numberOfWalls > BuildConstants.MAX_NUMBER_OF_WALLS) {
			throw new MaxWallsExceededException("Maximum number of walls exceeded.");
		}

		// deserialize the walls
		Vector<Wall> walls = new Vector<Wall>(numberOfWalls);

		for(int i = 0; i < numberOfWalls; i++) {
			if(data.length - offset < Wall.SIZE) {
				throw new MapDeserializationException("Map data is incomplete or corrupted, missing wall #" + (i + 1) + " information.");
			}

			// deserialize the wall
			walls.add(Wall.deserialize(data, version, offset, FILE_ENDIANNESS));
			offset += Wall.SIZE;
		}

		if(data.length - offset < 2) {
			throw new MapDeserializationException("Map data is incomplete or corrupted, missing number of sprites information.");
		}

		// deserialize the number of sprites
		int numberOfSprites = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), FILE_ENDIANNESS) & 0xffff;
		offset += 2;

		// verify that maximum number of sprites is not exceeded
		if(numberOfSprites > BuildConstants.MAX_NUMBER_OF_SPRITES) {
			throw new MaxSpritesExceededException("Maximum number of sprites exceeded.");
		}

		// deserialize the sprites
		int spriteSize = Sprite.SIZE;
		Vector<Sprite> sprites = new Vector<Sprite>(numberOfSprites);

		for(int i = 0; i < numberOfSprites; i++) {
			if(data.length - offset < spriteSize) {
				throw new MapDeserializationException("Map data is incomplete or corrupted, missing sprite #" + (i + 1) + " information.");
			}

			// deserialize the sprite
			sprites.add(Sprite.deserialize(data, version, offset, FILE_ENDIANNESS));
			offset += spriteSize;
		}

		byte trailingData[] = Arrays.copyOfRange(data, offset, data.length);
		offset += trailingData.length;

		Map map = new Map();
		map.setVersion(version);
		map.setPlayerSpawn(playerSpawn);
		map.addSectors(sectors);
		map.addWalls(walls);
		map.addSprites(sprites);
		map.setTrailingData(trailingData);

		return map;
	}

	public JSONObject toJSONObject() {
		JSONObject map = new JSONObject();
		map.put(VERSION_ATTRIBUTE_NAME, m_version);
		map.put(PLAYER_SPAWN_ATTRIBUTE_NAME, m_playerSpawn.toJSONObject());

		JSONArray sectors = new JSONArray();

		for(int i = 0; i < m_sectors.size(); i++) {
			sectors.put(m_sectors.elementAt(i).toJSONObject());
		}

		map.put(SECTORS_ATTRIBUTE_NAME, sectors);

		JSONArray walls = new JSONArray();

		for(int i = 0; i < m_walls.size(); i++) {
			walls.put(m_walls.elementAt(i).toJSONObject());
		}

		map.put(WALLS_ATTRIBUTE_NAME, walls);

		JSONArray sprites = new JSONArray();

		for(int i = 0; i < m_sprites.size(); i++) {
			sprites.put(m_sprites.elementAt(i).toJSONObject());
		}

		map.put(SPRITES_ATTRIBUTE_NAME, sprites);

		JSONArray trailingData = new JSONArray();

		for(int i = 0; i < m_trailingData.length; i++) {
			trailingData.put(m_trailingData[i]);
		}

		map.put(TRAILING_DATA_ATTRIBUTE_NAME, trailingData);

		return map;
	}

	public static Map fromJSONObject(JSONObject map) throws JSONException, MalformedItemAttributeException, MaxSectorsExceededException, MaxWallsExceededException, MaxSpritesExceededException {
		if(map == null) {
			throw new IllegalArgumentException("Map JSON data cannot be null.");
		}

		int version = map.getInt(VERSION_ATTRIBUTE_NAME);

		PlayerSpawn playerSpawn = PlayerSpawn.fromJSONObject(map.getJSONObject(PLAYER_SPAWN_ATTRIBUTE_NAME));

		JSONArray sectorsData = map.getJSONArray(SECTORS_ATTRIBUTE_NAME);
		Vector<Sector> sectors = new Vector<Sector>(sectorsData.length());

		for(int i = 0; i < sectorsData.length(); i++) {
			sectors.add(Sector.fromJSONObject(sectorsData.getJSONObject(i)));
		}

		JSONArray wallsData = map.getJSONArray(WALLS_ATTRIBUTE_NAME);
		Vector<Wall> walls = new Vector<Wall>(wallsData.length());

		for(int i = 0; i < wallsData.length(); i++) {
			walls.add(Wall.fromJSONObject(wallsData.getJSONObject(i)));
		}

		JSONArray spritesData = map.getJSONArray(SPRITES_ATTRIBUTE_NAME);
		Vector<Sprite> sprites = new Vector<Sprite>(spritesData.length());

		for(int i = 0; i < spritesData.length(); i++) {
			sprites.add(Sprite.fromJSONObject(spritesData.getJSONObject(i)));
		}

		byte trailingData[] = null;

		if(map.has(TRAILING_DATA_ATTRIBUTE_NAME)) {
			JSONArray trailingJSONData = map.getJSONArray(TRAILING_DATA_ATTRIBUTE_NAME);
			trailingData = new byte[trailingJSONData.length()];

			for(int i = 0; i < trailingJSONData.length(); i++) {
				trailingData[i] = (byte) trailingJSONData.getInt(i);
			}
		}

		Map newMap = new Map();
		newMap.setVersion(version);
		newMap.setPlayerSpawn(playerSpawn);
		newMap.addSectors(sectors);
		newMap.addWalls(walls);
		newMap.addSprites(sprites);
		newMap.setTrailingData(trailingData);

		return newMap;
	}

	public boolean load() throws MapReadException, DeserializationException {
		if(m_file == null || !m_file.exists()) { return false; }

		m_loading = true;

		// verify that the file has an extension
		String extension = Utilities.getFileExtension(m_file.getName());
		if(extension == null) {
			m_loading = false;
			throw new MapReadException("File \"" + m_file.getName() + "\" has no extension.");
		}
		
		// verify that the file extension is supported
		if(!hasFileTypeWithExtension(extension)) {
			m_loading = false;
			throw new MapReadException("File \"" + m_file.getName() +  "\" has unsupported extension: " + extension);
		}
		
		// check to make sure that the file is not too big to be stored in memory
		if(m_file.length() > Integer.MAX_VALUE) {
			m_loading = false;
			throw new MapReadException("File \"" + m_file.getName() +  "\" is too large to store in memory.");
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
			throw new MapReadException("File \"" + m_file.getName() +  "\" not found.");
		}
		catch(IOException e) {
			m_loading = false;
			throw new MapReadException("Error reading file \"" + m_file.getName() +  "\": " + e.getMessage());
		}

		SystemConsole.instance.writeLine("Opened " + FILE_TYPES[0].getName() + " file: \"" + m_file.getName() + "\", loaded " + data.length + " bytes into memory.");

		Map map = null;
		try {
			map = Map.deserialize(data);
			setVersion(map.m_version);
			setPlayerSpawn(map.m_playerSpawn);
			addSectors(map.m_sectors);
			addWalls(map.m_walls);
			addSprites(map.m_sprites);
			setTrailingData(map.m_trailingData);
		}
		catch(MaxSectorsExceededException e) {
			m_loading = false;
			throw new MapReadException(e.getMessage());
		}
		catch(MaxWallsExceededException e) {
			m_loading = false;
			throw new MapReadException(e.getMessage());
		}
		catch(MaxSpritesExceededException e) {
			m_loading = false;
			throw new MapReadException(e.getMessage());
		}

		m_loading = false;

		return true;
	}

	public boolean save() throws MapWriteException {
		if(m_file == null) { return false; }
		
		byte data[] = serialize();
		
		SystemConsole.instance.writeLine("Writing " + FILE_TYPES[0].getName() + " data to file: \"" + m_file.getName() + "\".");
		
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(m_file));
			out.write(data);
			out.close();
		}
		catch(IOException e) {
			throw new MapWriteException("Error writing to file: " + m_file.getName() +  ": " + e.getMessage());
		}
		
		SystemConsole.instance.writeLine(FILE_TYPES[0].getName() + " file writing complete!");
		
		setChanged(false);
		
		return true;
	}

	public Map clone() {
		Map map = new Map();

		map.setVersion(m_version);
		map.setPlayerSpawn(m_playerSpawn);
		map.setTrailingData(m_trailingData);

		try {
			map.addSectors(m_sectors);
			map.addWalls(m_walls);
			map.addSprites(m_sprites);
		}
		catch(MaxSectorsExceededException e) {
			return null;
		}
		catch(MaxWallsExceededException e) {
			return null;
		}
		catch(MaxSpritesExceededException e) {
			return null;
		}

		return map;
	}

	public void handlePlayerSpawnChange(PlayerSpawn playerSpawn) {
		setChanged(true);
	}

	public void handleMapComponentChange(MapComponent mapComponent) {
		setChanged(true);
	}

	public void handleSectorChange(Sector sector) { }

	public void handleWallChange(Wall wall) { }

	public void handleSpriteChange(Sprite sprite) { }

	public void paintMap(Graphics g, JComponent target) {
		if(!(g instanceof Graphics2D)) {
			return;
		}

		paintMap((Graphics2D) g, target, 1.0);
	}

	public void paintMap(Graphics2D g, JComponent target) {
		paintMap(g, target, 1.0);
	}

	public void paintMap(Graphics g, JComponent target, double zoom) {
		if(!(g instanceof Graphics2D)) {
			return;
		}

		paintMap((Graphics2D) g, target, zoom);
	}

	public void paintMap(Graphics2D g, JComponent target, double zoom) {
		if(g == null || target == null || zoom <= 0.0) {
			return;
		}

		for(int i = 0; i < m_walls.size(); i++) {
			m_walls.elementAt(i).paintWall(g, zoom);
		}

		for(int i = 0; i < m_walls.size(); i++) {
			m_walls.elementAt(i).paintVertex(g, zoom);
		}

		for(int i = 0; i < m_sprites.size(); i++) {
			m_sprites.elementAt(i).paintSprite(g, zoom);
		}

		m_playerSpawn.paintPlayerSpawn(g, zoom);
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof Map)) {
			return false;
		}

		Map m = (Map) o;

		if(m_sectors.size() != m.m_sectors.size() ||
		   m_walls.size() != m.m_walls.size() ||
		   m_sprites.size() != m.m_sprites.size()) {
			return false;
		}

		for(int i = 0; i < m_sectors.size(); i++) {
			if(!m_sectors.elementAt(i).equals(m.m_sectors.elementAt(i))) {
				return false;
			}
		}

		for(int i = 0; i < m_walls.size(); i++) {
			if(!m_walls.elementAt(i).equals(m.m_walls.elementAt(i))) {
				return false;
			}
		}

		for(int i = 0; i < m_sprites.size(); i++) {
			if(!m_sprites.elementAt(i).equals(m.m_sprites.elementAt(i))) {
				return false;
			}
		}

		return m_version == m.m_version &&
			   m_playerSpawn.equals(m.m_playerSpawn);
	}
	
	public String toString() {
		return getFileType().getName() + (m_file == null ? "" : " \"" + m_file.getName() + "\"");
	}

}
