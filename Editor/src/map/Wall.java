package map;

import java.util.*;
import java.awt.*;
import item.*;
import exception.*;
import utilities.*;
import org.json.*;

public class Wall extends TaggedMapComponent implements ItemAttributeChangeListener {

	protected int m_x;
	protected int m_y;
	protected short m_nextWallIndex;
	protected short m_adjacentWallIndex;
	protected short m_nextSectorIndex;
	protected WallAttributes m_attributes;
	protected short m_tileNumber;
	protected short m_maskedTileNumber;
	protected byte m_shade;
	protected short m_paletteLookupTableNumber;
	protected short m_xRepeat;
	protected short m_yRepeat;
	protected short m_xPanning;
	protected short m_yPanning;
	protected Vector<WallChangeListener> m_wallChangeListeners;

	public static final int SIZE = 32;
	
	protected static final int WALL_VERTEX_SIZE = 4;

	public static final String X_ATTRIBUTE_NAME = "x";
	public static final String Y_ATTRIBUTE_NAME = "y";
	public static final String NEXT_WALL_INDEX_ATTRIBUTE_NAME = "nextWallIndex";
	public static final String ADJACENT_WALL_INDEX_ATTRIBUTE_NAME = "adjacentWallIndex";
	public static final String NEXT_SECTOR_INDEX_ATTRIBUTE_NAME = "nextSectorIndex";
	public static final String ATTRIBUTES_ATTRIBUTE_NAME = "attributes";
	public static final String TILE_NUMBER_ATTRIBUTE_NAME = "tileNumber";
	public static final String MASKED_TILE_NUMBER_ATTRIBUTE_NAME = "maskedTileNumber";
	public static final String SHADE_ATTRIBUTE_NAME = "shade";
	public static final String PALETTE_LOOKUP_TABLE_NUMBER_ATTRIBUTE_NAME = "paletteLookupTableNumber";
	public static final String X_REPEAT_ATTRIBUTE_NAME = "xRepeat";
	public static final String Y_REPEAT_ATTRIBUTE_NAME = "yRepeat";
	public static final String X_PANNING_ATTRIBUTE_NAME = "xPanning";
	public static final String Y_PANNING_ATTRIBUTE_NAME = "yPanning";
	public static final String TAG_INFORMATION_ATTRIBUTE_NAME = "tagInformation";

	public Wall(int x, int y, short nextWallIndex, short adjacentWallIndex, short nextSectorIndex, short attributes, short tileNumber, short maskedTileNumber, byte shade, short paletteLookupTableNumber, short xRepeat, short yRepeat, short xPanning, short yPanning, int lowTag, int highTag, short extra) {
		this(x, y, nextWallIndex, adjacentWallIndex, nextSectorIndex, WallAttributes.unpack(attributes), tileNumber, maskedTileNumber, shade, paletteLookupTableNumber, xRepeat, yRepeat, xPanning, yPanning, new TagInformation(lowTag, highTag, extra));
	}

	public Wall(int x, int y, short nextWallIndex, short adjacentWallIndex, short nextSectorIndex, WallAttributes attributes, short tileNumber, short maskedTileNumber, byte shade, short paletteLookupTableNumber, short xRepeat, short yRepeat, short xPanning, short yPanning, int lowTag, int highTag, short extra) {
		this(x, y, nextWallIndex, adjacentWallIndex, nextSectorIndex, attributes, tileNumber, maskedTileNumber, shade, paletteLookupTableNumber, xRepeat, yRepeat, xPanning, yPanning, new TagInformation(lowTag, highTag, extra));
	}

	public Wall(int x, int y, short nextWallIndex, short adjacentWallIndex, short nextSectorIndex, short attributes, short tileNumber, short maskedTileNumber, byte shade, short paletteLookupTableNumber, short xRepeat, short yRepeat, short xPanning, short yPanning, TagInformation tagInformation) {
		this(x, y, nextWallIndex, adjacentWallIndex, nextSectorIndex, WallAttributes.unpack(attributes), tileNumber, maskedTileNumber, shade, paletteLookupTableNumber, xRepeat, yRepeat, xPanning, yPanning, tagInformation);
	}

	public Wall(int x, int y, short nextWallIndex, short adjacentWallIndex, short nextSectorIndex, WallAttributes attributes, short tileNumber, short maskedTileNumber, byte shade, short paletteLookupTableNumber, short xRepeat, short yRepeat, short xPanning, short yPanning, TagInformation tagInformation) {
		super(tagInformation);

		if(attributes == null) { throw new IllegalArgumentException("Wall attributes cannot be null."); }
		if(paletteLookupTableNumber < 0 || paletteLookupTableNumber > 255) { throw new IllegalArgumentException("Invalid palette lookup table number value: " + paletteLookupTableNumber + ", expected value between 0 and 255."); }
		if(xRepeat < 0 || xRepeat > 255) { throw new IllegalArgumentException("Invalid x repeat value: " + xRepeat + ", expected value between 0 and 255."); }
		if(yRepeat < 0 || yRepeat > 255) { throw new IllegalArgumentException("Invalid y repeat value: " + yRepeat + ", expected value between 0 and 255."); }
		if(xPanning < 0 || xPanning > 255) { throw new IllegalArgumentException("Invalid x pannning value: " + xPanning + ", expected value between 0 and 255."); }
		if(yPanning < 0 || yPanning > 255) { throw new IllegalArgumentException("Invalid y pannning value: " + yPanning + ", expected value between 0 and 255."); }

		m_x = x;
		m_y = y;
		m_nextWallIndex = nextWallIndex;
		m_adjacentWallIndex = adjacentWallIndex;
		m_nextSectorIndex = nextSectorIndex;
		m_tileNumber = tileNumber;
		m_maskedTileNumber = maskedTileNumber;
		m_shade = shade;
		m_paletteLookupTableNumber = paletteLookupTableNumber;
		m_xRepeat = xRepeat;
		m_yRepeat = yRepeat;
		m_xPanning = xPanning;
		m_yPanning = yPanning;
		m_tagInformation = tagInformation;
		m_wallChangeListeners = new Vector<WallChangeListener>();

		setAttributes(attributes);
	}

	public int getX() {
		return m_x;
	}

	public void setX(int x) {
		if(m_x == x) {
			return;
		}

		m_x = x;

		notifyWallChanged();
	}

	public int getY() {
		return m_y;
	}

	public void setY(int y) {
		if(m_y == y) {
			return;
		}

		m_y = y;

		notifyWallChanged();
	}

	public short getNextWallIndex() {
		return m_nextWallIndex;
	}

	public Wall getNextWall() {
		if(m_map == null) {
			return null;
		}

		return m_map.getWall(m_nextWallIndex);
	}

	public void setNextWallIndex(short nextWallIndex) {
		if(m_nextWallIndex == nextWallIndex) {
			return;
		}

		m_nextWallIndex = nextWallIndex;

		notifyWallChanged();
	}

	public short getAdjacentWallIndex() {
		return m_adjacentWallIndex;
	}

	public Wall getAdjacentWall() {
		if(m_map == null) {
			return null;
		}

		return m_map.getWall(m_adjacentWallIndex);
	}

	public void setAdjacentWallIndex(short adjacentWallIndex) {
		if(m_adjacentWallIndex == adjacentWallIndex) {
			return;
		}

		m_adjacentWallIndex = adjacentWallIndex;

		notifyWallChanged();
	}

	public short getNextSectorIndex() {
		return m_nextSectorIndex;
	}

	public Sector getNextSector() {
		if(m_map == null) {
			return null;
		}

		return m_map.getSector(m_nextSectorIndex);
	}

	public void setNextSectorIndex(short nextSectorIndex) {
		if(m_nextSectorIndex == nextSectorIndex) {
			return;
		}

		m_nextSectorIndex = nextSectorIndex;

		notifyWallChanged();
	}

	public boolean getBlockClipping() {
		return m_attributes.getBlockClipping();
	}

	public boolean setBlockClipping(boolean blockClipping) {
		return m_attributes.setBlockClipping(blockClipping);
	}

	public boolean getInvisibleWallBottomSwapped() {
		return m_attributes.getInvisibleWallBottomSwapped();
	}

	public boolean setInvisibleWallBottomSwapped(boolean invisibleWallBottomSwapped) {
		return m_attributes.setInvisibleWallBottomSwapped(invisibleWallBottomSwapped);
	}

	public boolean getTextureAlignBottom() {
		return m_attributes.getTextureAlignBottom();
	}

	public boolean setTextureAlignBottom(boolean textureAlignBottom) {
		return m_attributes.setTextureAlignBottom(textureAlignBottom);
	}

	public boolean getXFlipped() {
		return m_attributes.getXFlipped();
	}

	public boolean setXFlipped(boolean xFlipped) {
		return m_attributes.setXFlipped(xFlipped);
	}

	public boolean getMasked() {
		return m_attributes.getMasked();
	}

	public boolean setMasked(boolean masked) {
		return m_attributes.setMasked(masked);
	}

	public boolean getOneWay() {
		return m_attributes.getOneWay();
	}

	public boolean setOneWay(boolean oneWay) {
		return m_attributes.setOneWay(oneWay);
	}

	public boolean getBlockHitscan() {
		return m_attributes.getBlockHitscan();
	}

	public boolean setBlockHitscan(boolean blockHitscan) {
		return m_attributes.setBlockHitscan(blockHitscan);
	}

	public boolean getTranslucent() {
		return m_attributes.getTranslucent();
	}

	public boolean setTranslucent(boolean translucent) {
		return m_attributes.setTranslucent(translucent);
	}

	public boolean getYFlipped() {
		return m_attributes.getYFlipped();
	}

	public boolean setYFlipped(boolean yFlipped) {
		return m_attributes.setYFlipped(yFlipped);
	}

	public boolean getReverseTranslucent() {
		return m_attributes.getReverseTranslucent();
	}

	public boolean setReverseTranslucent(boolean reverseTranslucent) {
		return m_attributes.setReverseTranslucent(reverseTranslucent);
	}

	public WallAttributes getAttributes() {
		return m_attributes;
	}

	public void setAttributes(short attributes) throws IllegalArgumentException {
		setAttributes(WallAttributes.unpack(attributes));
	}

	public void setAttributes(WallAttributes attributes) throws IllegalArgumentException {
		if(attributes == null) { throw new IllegalArgumentException("Attributes cannot be null."); }

		if(m_attributes !=  null) {
			if(m_attributes.equals(attributes)) {
				return;
			}

			m_attributes.removeItemAttributeChangeListener(this);
		}

		m_attributes = (WallAttributes) attributes.clone();
		m_attributes.addItemAttributeChangeListener(this);

		notifyWallChanged();
	}

	public short getTileNumber() {
		return m_tileNumber;
	}

	public void setTileNumber(short tileNumber) {
		if(m_tileNumber == tileNumber) {
			return;
		}

		m_tileNumber = tileNumber;

		notifyWallChanged();
	}

	public short getMaskedTileNumber() {
		return m_maskedTileNumber;
	}

	public void setMaskedTileNumber(short maskedTileNumber) {
		if(m_maskedTileNumber == maskedTileNumber) {
			return;
		}

		m_maskedTileNumber = maskedTileNumber;

		notifyWallChanged();
	}

	public byte getShade() {
		return m_shade;
	}

	public void setShade(byte shade) {
		if(m_shade == shade) {
			
		}

		m_shade = shade;

		notifyWallChanged();
	}

	public short getPaletteLookupTableNumber() {
		return m_paletteLookupTableNumber;
	}

	public void setPaletteLookupTableNumber(short paletteLookupTableNumber) {
		if(m_paletteLookupTableNumber == paletteLookupTableNumber) {
			return;
		}

		m_paletteLookupTableNumber = paletteLookupTableNumber;

		notifyWallChanged();
	}

	public short getXRepeat() {
		return m_xPanning;
	}

	public void setXPanning(short xPanning) throws IllegalArgumentException {
		if(xPanning < 0 || xPanning > 255) { throw new IllegalArgumentException("Invalid x panning value: " + xPanning + ", expected value between 0 and 255."); }

		if(m_xPanning == xPanning) {
			return;
		}

		m_xPanning = xPanning;

		notifyWallChanged();
	}

	public short getYPanning() {
		return m_yPanning;
	}

	public void setYPanning(short yPanning) throws IllegalArgumentException {
		if(yPanning < 0 || yPanning > 255) { throw new IllegalArgumentException("Invalid y panning value: " + yPanning + ", expected value between 0 and 255."); }

		if(m_yPanning == yPanning) {
			return;
		}

		m_yPanning = yPanning;

		notifyWallChanged();
	}

	public void setMap(Map map) {
		if(m_map != null) {
			removeWallChangeListener(map);
		}

		super.setMap(map);

		if(m_map != null) {
			addWallChangeListener(map);
		}
	}

	public byte[] serialize(int version) {
		return serialize(version, Endianness.LittleEndian);
	}

	public byte[] serialize(int version, Endianness endianness) {
		byte data[] = new byte[SIZE];
		int offset = 0;

		// serialize the wall x co-ordinate
		System.arraycopy(Serializer.serializeInteger(m_x, endianness), 0, data, offset, 4);
		offset += 4;

		// serialize the wall y co-ordinate
		System.arraycopy(Serializer.serializeInteger(m_y, endianness), 0, data, offset, 4);
		offset += 4;

		// serialize the next wall index
		System.arraycopy(Serializer.serializeShort(m_nextWallIndex, endianness), 0, data, offset, 2);
		offset += 2;

		if(version == 6) {
			// serialize the next sector index
			System.arraycopy(Serializer.serializeShort(m_nextSectorIndex, endianness), 0, data, offset, 2);
			offset += 2;
		}

		// serialize the adjacent wall index
		System.arraycopy(Serializer.serializeShort(m_adjacentWallIndex, endianness), 0, data, offset, 2);
		offset += 2;

		if(version != 6) {
			// serialize the next sector index
			System.arraycopy(Serializer.serializeShort(m_nextSectorIndex, endianness), 0, data, offset, 2);
			offset += 2;

			// serialize the wall attributes
			System.arraycopy(Serializer.serializeShort(m_attributes.pack().shortValue(), endianness), 0, data, offset, 2);
			offset += 2;
		}

		// serialize the tile number
		System.arraycopy(Serializer.serializeShort(m_tileNumber, endianness), 0, data, offset, 2);
		offset += 2;

		// serialize the masked tile number
		System.arraycopy(Serializer.serializeShort(m_maskedTileNumber, endianness), 0, data, offset, 2);
		offset += 2;

		// serialize the shade and palette lookup table number values
		data[offset++] = m_shade;
		data[offset++] = (byte) m_paletteLookupTableNumber;

		if(version == 6) {
			// serialize the wall attributes
			System.arraycopy(Serializer.serializeShort(m_attributes.pack().shortValue(), endianness), 0, data, offset, 2);
			offset += 2;
		}

		// serialize the x/y repeat and panning values
		data[offset++] = (byte) m_xRepeat;
		data[offset++] = (byte) m_yRepeat;
		data[offset++] = (byte) m_xPanning;
		data[offset++] = (byte) m_yPanning;

		// serialize the wall tag information
		System.arraycopy(m_tagInformation.serialize(endianness), 0, data, offset, TagInformation.SIZE);
		offset += TagInformation.SIZE;

		return data;
	}

	public static Wall deserialize(byte data[], int version) throws DeserializationException {
		return deserialize(data, version, 0);
	}

	public static Wall deserialize(byte data[], int version, int offset) throws DeserializationException {
		return deserialize(data, version, offset, Endianness.LittleEndian);
	}

	public static Wall deserialize(byte data[], int version, Endianness endianness) throws DeserializationException {
		return deserialize(data, version, 0, endianness);
	}

	public static Wall deserialize(byte data[], int version, int offset, Endianness endianness) throws DeserializationException {
		if(data == null) {
			throw new DeserializationException("Invalid wall data.");
		}

		if(offset < 0 || offset >= data.length) {
			throw new DeserializationException("Invalid data offset.");
		}

		// verify that the data is long enough to contain required information
		if(data.length - offset < SIZE) {
			throw new DeserializationException("Wall data is incomplete or corrupted.");
		}

		short nextWallIndex = -1;
		short adjacentWallIndex = -1;
		short nextSectorIndex = -1;
		WallAttributes attributes = null;

		// deserialize the wall x co-ordinate
		int x = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + 4), endianness);
		offset += 4;

		// deserialize the wall y co-ordinate
		int y = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + 4), endianness);
		offset += 4;

		// deserialize the next wall index
		nextWallIndex = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
		offset += 2;

		if(version == 6) {
			// deserialize the next sector index
			nextSectorIndex = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;
		}

		// deserialize the next wall index
		adjacentWallIndex = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
		offset += 2;

		if(version != 6) {
			// deserialize the next sector index
			nextSectorIndex = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;

			// deserialize the wall attributes
			attributes = WallAttributes.unpack(Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness));
			offset += 2;
		}

		// deserialize the wall tile number
		short tileNumber = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
		offset += 2;

		// deserialize the wall masked tile number
		short maskedTileNumber = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
		offset += 2;

		// deserialize the wall shade value
		byte shade = data[offset++];

		// deserialize the wall palette lookup table number
		short paletteLookupTableNumber = (short) (data[offset++] & 0xff);

		if(version == 6) {
			// deserialize the wall attributes
			attributes = WallAttributes.unpack(Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness));
			offset += 2;
		}

		// deserialize the wall x/y repeat and panning values
		short xRepeat = (short) (data[offset++] & 0xff);
		short yRepeat = (short) (data[offset++] & 0xff);
		short xPanning = (short) (data[offset++] & 0xff);
		short yPanning = (short) (data[offset++] & 0xff);

		// deserialize the sprite tag information
		TagInformation tagInformation = TagInformation.deserialize(data, offset, endianness);
		offset += TagInformation.SIZE;

		return new Wall(x, y, nextWallIndex, adjacentWallIndex, nextSectorIndex, attributes, tileNumber, maskedTileNumber, shade, paletteLookupTableNumber, xRepeat, yRepeat, xPanning, yPanning, tagInformation);
	}

	public JSONObject toJSONObject() {
		JSONObject wall = new JSONObject();
		wall.put(X_ATTRIBUTE_NAME, m_x);
		wall.put(Y_ATTRIBUTE_NAME, m_y);
		wall.put(NEXT_WALL_INDEX_ATTRIBUTE_NAME, m_nextWallIndex);
		wall.put(ADJACENT_WALL_INDEX_ATTRIBUTE_NAME, m_adjacentWallIndex);
		wall.put(NEXT_SECTOR_INDEX_ATTRIBUTE_NAME, m_nextSectorIndex);
		wall.put(ATTRIBUTES_ATTRIBUTE_NAME, m_attributes.toJSONObject());
		wall.put(TILE_NUMBER_ATTRIBUTE_NAME, m_tileNumber);
		wall.put(MASKED_TILE_NUMBER_ATTRIBUTE_NAME, m_maskedTileNumber);
		wall.put(SHADE_ATTRIBUTE_NAME, m_shade);
		wall.put(PALETTE_LOOKUP_TABLE_NUMBER_ATTRIBUTE_NAME, m_paletteLookupTableNumber);
		wall.put(X_REPEAT_ATTRIBUTE_NAME, m_xRepeat);
		wall.put(Y_REPEAT_ATTRIBUTE_NAME, m_yRepeat);
		wall.put(X_PANNING_ATTRIBUTE_NAME, m_xPanning);
		wall.put(Y_PANNING_ATTRIBUTE_NAME, m_yPanning);
		wall.put(TAG_INFORMATION_ATTRIBUTE_NAME, m_tagInformation.toJSONObject());

		return wall;
	}

	public static Wall fromJSONObject(JSONObject wall) throws IllegalArgumentException, JSONException, MalformedItemAttributeException {
		if(wall == null) {
			throw new IllegalArgumentException("Wall JSON data cannot be null.");
		}

		return new Wall(
			wall.getInt(X_ATTRIBUTE_NAME),
			wall.getInt(Y_ATTRIBUTE_NAME),
			(short) wall.getInt(NEXT_WALL_INDEX_ATTRIBUTE_NAME),
			(short) wall.getInt(ADJACENT_WALL_INDEX_ATTRIBUTE_NAME),
			(short) wall.getInt(NEXT_SECTOR_INDEX_ATTRIBUTE_NAME),
			(WallAttributes) WallAttributes.fromJSONObject(wall.getJSONObject(ATTRIBUTES_ATTRIBUTE_NAME)),
			(short) wall.getInt(TILE_NUMBER_ATTRIBUTE_NAME),
			(short) wall.getInt(MASKED_TILE_NUMBER_ATTRIBUTE_NAME),
			(byte) wall.getInt(SHADE_ATTRIBUTE_NAME),
			(short) wall.getInt(PALETTE_LOOKUP_TABLE_NUMBER_ATTRIBUTE_NAME),
			(short) wall.getInt(X_REPEAT_ATTRIBUTE_NAME),
			(short) wall.getInt(Y_REPEAT_ATTRIBUTE_NAME),
			(short) wall.getInt(X_PANNING_ATTRIBUTE_NAME),
			(short) wall.getInt(Y_PANNING_ATTRIBUTE_NAME),
			TagInformation.fromJSONObject(wall.getJSONObject(TAG_INFORMATION_ATTRIBUTE_NAME))
		);
	}

	public Wall clone() {
		return clone(true);
	}

	public Wall clone(boolean reassignMap) {
		Wall clonedWall = new Wall(m_x, m_y, m_nextWallIndex, m_adjacentWallIndex, m_nextSectorIndex, m_attributes, m_tileNumber, m_maskedTileNumber, m_shade, m_paletteLookupTableNumber, m_xRepeat, m_yRepeat, m_xPanning, m_yPanning, m_tagInformation);

		if(reassignMap) {
			clonedWall.setMap(m_map);
		}

		return clonedWall;
	}

	public int numberOfWallChangeListeners() {
		return m_wallChangeListeners.size();
	}
	
	public WallChangeListener getWallChangeListener(int index) {
		if(index < 0 || index >= m_wallChangeListeners.size()) { return null; }

		return m_wallChangeListeners.elementAt(index);
	}
	
	public boolean hasWallChangeListener(WallChangeListener c) {
		return m_wallChangeListeners.contains(c);
	}
	
	public int indexOfWallChangeListener(WallChangeListener c) {
		return m_wallChangeListeners.indexOf(c);
	}
	
	public boolean addWallChangeListener(WallChangeListener c) {
		if(c == null || m_wallChangeListeners.contains(c)) { return false; }

		m_wallChangeListeners.add(c);

		return true;
	}
	
	public boolean removeWallChangeListener(int index) {
		if(index < 0 || index >= m_wallChangeListeners.size()) { return false; }

		m_wallChangeListeners.remove(index);

		return true;
	}
	
	public boolean removeWallChangeListener(WallChangeListener c) {
		if(c == null) { return false; }

		return m_wallChangeListeners.remove(c);
	}
	
	public void clearWallChangeListeners() {
		m_wallChangeListeners.clear();
	}
	
	protected void notifyWallChanged() {
		for(int i=0;i<m_wallChangeListeners.size();i++) {
			m_wallChangeListeners.elementAt(i).handleMapComponentChange(this);
			m_wallChangeListeners.elementAt(i).handleWallChange(this);
		}
	}

	public void handleTagInformationChange(TagInformation tagInformation) {
		notifyWallChanged();
	}

	public void handleItemAttributeChange(ItemAttributes attributes, ItemAttribute attribute, byte value) {
		notifyWallChanged();
	}

	public void paintWall(Graphics2D g) {
		paintWall(g, 1.0);
	}

	public void paintWall(Graphics2D g, double zoom) {
		if(g == null || zoom <= 0.0) {
			return;
		}

		float strokeWidth = 0.0f;

		if(getNextSectorIndex() != -1) {
			if(getBlockClipping()) {
				g.setColor(Color.MAGENTA);
			}
			else {
				g.setColor(Color.RED);
			}

			if(getBlockHitscan()) {
				strokeWidth = 3.0f;
			}
			else {
				strokeWidth = 1.0f;
			}

			g.setStroke(new BasicStroke(strokeWidth));
		}
		else {
			g.setColor(Color.WHITE);
		}

		Wall nextWall = getNextWall();

		g.drawLine((int) (m_x * zoom), (int) (m_y * zoom), (int) (nextWall.m_x * zoom), (int) (nextWall.m_y * zoom));

		g.setStroke(new BasicStroke());
	}

	public void paintVertex(Graphics2D g) {
		paintVertex(g, 1.0);
	}

	public void paintVertex(Graphics2D g, double zoom) {
		g.setColor(Color.GREEN);
		g.drawRect((int) (m_x * zoom) - (WALL_VERTEX_SIZE / 2), (int) (m_y * zoom) - (WALL_VERTEX_SIZE / 2), WALL_VERTEX_SIZE, WALL_VERTEX_SIZE);
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof Wall)) {
			return false;
		}

		Wall w = (Wall) o;

		return m_x == w.m_x &&
			   m_y == w.m_y &&
			   m_nextWallIndex == w.m_nextWallIndex &&
			   m_nextSectorIndex == w.m_nextSectorIndex &&
			   m_attributes.equals(w.m_attributes) &&
			   m_tileNumber == w.m_tileNumber &&
			   m_maskedTileNumber == w.m_maskedTileNumber &&
			   m_shade == w.m_shade &&
			   m_paletteLookupTableNumber == w.m_paletteLookupTableNumber &&
			   m_xRepeat == w.m_xRepeat &&
			   m_yRepeat == w.m_yRepeat &&
			   m_xPanning == w.m_xPanning &&
			   m_yPanning == w.m_yPanning &&
			   m_tagInformation.equals(w.m_tagInformation);
	}

}
