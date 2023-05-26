package map;

import java.util.*;
import exception.*;
import utilities.*;
import org.json.*;

public class Sector extends TaggedMapComponent implements PartitionChangeListener {

	protected short m_firstWallIndex;
	protected short m_numberOfWalls;
	protected Partition m_ceiling;
	protected Partition m_floor;
	protected short m_visibility;
	protected byte m_filler; // note: unused and not present in version 6
	protected byte[] m_trailingData;
	protected Vector<SectorChangeListener> m_sectorChangeListeners;

	public static final int ONE_TIME_SOUND_LOW_TAG = 10000;

	public static final String FIRST_WALL_INDEX_ATTRIBUTE_NAME = "firstWallIndex";
	public static final String NUMBER_OF_WALLS_ATTRIBUTE_NAME = "numberOfWalls";
	public static final String CEILING_ATTRIBUTE_NAME = "ceiling";
	public static final String FLOOR_ATTRIBUTE_NAME = "floor";
	public static final String VISIBILITY_ATTRIBUTE_NAME = "visibility";
	public static final String FILLER_ATTRIBUTE_NAME = "filler";
	public static final String TRAILING_DATA_ATTRIBUTE_NAME = "trailingData";

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, short ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, short floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, int lowTag, int highTag, int extra) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, PartitionAttributes.unpack(ceilingAttributes), ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, PartitionAttributes.unpack(floorAttributes), floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, (byte) 0, new TagInformation(lowTag, highTag, extra), null);
	}

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, PartitionAttributes ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, PartitionAttributes floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, int lowTag, int highTag, int extra) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, ceilingAttributes, ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, floorAttributes, floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, (byte) 0, new TagInformation(lowTag, highTag, extra), null);
	}

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, short ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, short floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, byte filler, int lowTag, int highTag, int extra) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, PartitionAttributes.unpack(ceilingAttributes), ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, PartitionAttributes.unpack(floorAttributes), floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, filler, new TagInformation(lowTag, highTag, extra), null);
	}

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, PartitionAttributes ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, PartitionAttributes floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, byte filler, int lowTag, int highTag, int extra) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, ceilingAttributes, ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, floorAttributes, floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, filler, new TagInformation(lowTag, highTag, extra), null);
	}

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, short ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, short floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, int lowTag, int highTag, int extra, byte[] trailingData) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, PartitionAttributes.unpack(ceilingAttributes), ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, PartitionAttributes.unpack(floorAttributes), floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, (byte) 0, new TagInformation(lowTag, highTag, extra), trailingData);
	}

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, PartitionAttributes ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, PartitionAttributes floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, int lowTag, int highTag, int extra, byte[] trailingData) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, ceilingAttributes, ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, floorAttributes, floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, (byte) 0, new TagInformation(lowTag, highTag, extra), trailingData);
	}

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, short ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, short floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, byte filler, int lowTag, int highTag, int extra, byte[] trailingData) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, PartitionAttributes.unpack(ceilingAttributes), ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, PartitionAttributes.unpack(floorAttributes), floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, filler, new TagInformation(lowTag, highTag, extra), trailingData);
	}

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, PartitionAttributes ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, PartitionAttributes floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, byte filler, int lowTag, int highTag, int extra, byte[] trailingData) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, ceilingAttributes, ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, floorAttributes, floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, filler, new TagInformation(lowTag, highTag, extra), trailingData);
	}

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, short ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, short floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, TagInformation tagInformation) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, PartitionAttributes.unpack(ceilingAttributes), ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, PartitionAttributes.unpack(floorAttributes), floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, (byte) 0, tagInformation, null);
	}

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, PartitionAttributes ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, PartitionAttributes floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, TagInformation tagInformation) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, ceilingAttributes, ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, floorAttributes, floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, (byte) 0, tagInformation, null);
	}

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, short ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, short floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, byte filler, TagInformation tagInformation) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, PartitionAttributes.unpack(ceilingAttributes), ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, PartitionAttributes.unpack(floorAttributes), floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, filler, tagInformation, null);
	}

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, PartitionAttributes ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, PartitionAttributes floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, byte filler, TagInformation tagInformation) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, ceilingAttributes, ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, floorAttributes, floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, filler, tagInformation, null);
	}

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, short ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, short floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, TagInformation tagInformation, byte[] trailingData) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, PartitionAttributes.unpack(ceilingAttributes), ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, PartitionAttributes.unpack(floorAttributes), floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, (byte) 0, tagInformation, trailingData);
	}

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, PartitionAttributes ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, PartitionAttributes floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, TagInformation tagInformation, byte[] trailingData) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, ceilingAttributes, ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, floorAttributes, floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, (byte) 0, tagInformation, trailingData);
	}

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, short ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, short floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, byte filler, TagInformation tagInformation, byte[] trailingData) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, PartitionAttributes.unpack(ceilingAttributes), ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, PartitionAttributes.unpack(floorAttributes), floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, filler, tagInformation, trailingData);
	}

	public Sector(short firstWallIndex, short numberOfWalls, int ceilingHeight, PartitionAttributes ceilingAttributes, short ceilingTileNumber, short ceilingSlope, byte ceilingShade, short ceilingPaletteLookupTableNumber, short ceilingXPanning, short ceilingYPanning, int floorHeight, PartitionAttributes floorAttributes, short floorTileNumber, short floorSlope, byte floorShade, short floorPaletteLookupTableNumber, short floorXPanning, short floorYPanning, short visibility, byte filler, TagInformation tagInformation, byte[] trailingData) {
		this(firstWallIndex, numberOfWalls, new Partition(ceilingHeight, ceilingAttributes, ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning), new Partition(floorHeight, floorAttributes, floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning), visibility, filler, tagInformation, trailingData);
	}

	public Sector(short firstWallIndex, short numberOfWalls, Partition ceiling, Partition floor, short visibility, int lowTag, int highTag, int extra) {
		this(firstWallIndex, numberOfWalls, ceiling, floor, visibility, (byte) 0, new TagInformation(lowTag, highTag, extra), null);
	}

	public Sector(short firstWallIndex, short numberOfWalls, Partition ceiling, Partition floor, short visibility, byte filler, int lowTag, int highTag, int extra) {
		this(firstWallIndex, numberOfWalls, ceiling, floor, visibility,filler, new TagInformation(lowTag, highTag, extra), null);
	}

	public Sector(short firstWallIndex, short numberOfWalls, Partition ceiling, Partition floor, short visibility, int lowTag, int highTag, int extra, byte[] trailingData) {
		this(firstWallIndex, numberOfWalls, ceiling, floor, visibility, (byte) 0, new TagInformation(lowTag, highTag, extra), trailingData);
	}

	public Sector(short firstWallIndex, short numberOfWalls, Partition ceiling, Partition floor, short visibility, byte filler, int lowTag, int highTag, int extra, byte[] trailingData) {
		this(firstWallIndex, numberOfWalls, ceiling, floor, visibility, filler, new TagInformation(lowTag, highTag, extra), trailingData);
	}

	public Sector(short firstWallIndex, short numberOfWalls, Partition ceiling, Partition floor, short visibility, TagInformation tagInformation) {
		this(firstWallIndex, numberOfWalls, ceiling, floor, visibility, (byte) 0, tagInformation, null);
	}

	public Sector(short firstWallIndex, short numberOfWalls, Partition ceiling, Partition floor, short visibility, byte filler, TagInformation tagInformation) {
		this(firstWallIndex, numberOfWalls, ceiling, floor, visibility, filler, tagInformation, null);
	}

	public Sector(short firstWallIndex, short numberOfWalls, Partition ceiling, Partition floor, short visibility, TagInformation tagInformation, byte[] trailingData) {
		this(firstWallIndex, numberOfWalls, ceiling, floor, visibility, (byte) 0, tagInformation, trailingData);
	}

	public Sector(short firstWallIndex, short numberOfWalls, Partition ceiling, Partition floor, short visibility, byte filler, TagInformation tagInformation, byte[] trailingData) {
		super(tagInformation);

		if(ceiling == null) { throw new IllegalArgumentException("Sector ceiling data cannot be null."); }
		if(floor == null) { throw new IllegalArgumentException("Sectot floor data cannot be null."); }
		if(trailingData != null && trailingData.length > 3) { throw new IllegalArgumentException("Invalid sector trailing data."); }

		m_firstWallIndex = firstWallIndex;
		m_numberOfWalls = numberOfWalls;
		m_visibility = visibility;
		m_filler = filler;
		m_tagInformation = tagInformation;
		m_trailingData = trailingData == null ? new byte[0] : Arrays.copyOfRange(trailingData, 0, Math.min(trailingData.length, 3));
		m_sectorChangeListeners = new Vector<SectorChangeListener>();

		setCeiling(ceiling);
		setFloor(floor);
	}

	public static int getSizeForVersion(int version) {
		return 6 + (version == 6 ? 2 : 0) + (Partition.getSizeForVersion(version) * 2) + TagInformation.SIZE;
	}

	public short getFirstWallIndex() {
		return m_firstWallIndex;
	}

	public Wall getFirstWall() {
		if(m_map == null) {
			return null;
		}

		return m_map.getWall(m_firstWallIndex);
	}

	public void setFirstWallIndex(short firstWallIndex) {
		if(m_firstWallIndex == firstWallIndex) {
			return;
		}

		m_firstWallIndex = firstWallIndex;

		notifySectorChanged();
	}

	public short getNumberOfWalls() {
		return m_numberOfWalls;
	}

	public void setNumberOfWalls(short numberOfWalls) {
		if(m_numberOfWalls == numberOfWalls) {
			return;
		}

		m_numberOfWalls = numberOfWalls;

		notifySectorChanged();
	}

	public int getCeilingHeight() {
		return m_ceiling.getHeight();
	}

	public void setCeilingHeight(int height) {
		m_ceiling.setHeight(height);
	}

	public PartitionAttributes getCeilingAttributes() {
		return m_ceiling.getAttributes();
	}

	public void setCeilingAttributes(PartitionAttributes attributes) {
		m_ceiling.setAttributes(attributes);
	}

	public short getCeilingTileNumber() {
		return m_ceiling.getTileNumber();
	}

	public void setCeilingTileNumber(short tileNumber) {
		m_ceiling.setTileNumber(tileNumber);
	}

	public short getCeilingSlope() {
		return m_ceiling.getSlope();
	}

	public void setCeilingSlope(short slope) {
		m_ceiling.setSlope(slope);
	}

	public byte getCeilingShade() {
		return m_ceiling.getShade();
	}

	public void setCeilingShade(byte shade) {
		m_ceiling.setShade(shade);
	}

	public short getCeilingPaletteLookupTableNumber() {
		return m_ceiling.getPaletteLookupTableNumber();
	}

	public void setCeilingPaletteLookupTableNumber(short paletteLookupTableNumber) {
		m_ceiling.setPaletteLookupTableNumber(paletteLookupTableNumber);
	}

	public short getCeilingXPanning() {
		return m_ceiling.getXPanning();
	}

	public void setCeilingXPanning(short xPanning) {
		m_ceiling.setXPanning(xPanning);
	}

	public short getCeilingYPanning() {
		return m_ceiling.getYPanning();
	}

	public void setCeilingYPanning(short yPanning) {
		m_ceiling.setYPanning(yPanning);
	}

	public int getFloorHeight() {
		return m_floor.getHeight();
	}

	public void setFloorHeight(int height) {
		m_floor.setHeight(height);
	}

	public PartitionAttributes getFloorAttributes() {
		return m_floor.getAttributes();
	}

	public void setFloorAttributes(PartitionAttributes attributes) {
		m_floor.setAttributes(attributes);
	}

	public short getFloorTileNumber() {
		return m_floor.getTileNumber();
	}

	public void setFloorTileNumber(short tileNumber) {
		m_floor.setTileNumber(tileNumber);
	}

	public short getFloorSlope() {
		return m_floor.getSlope();
	}

	public void setFloorSlope(short slope) {
		m_floor.setSlope(slope);
	}

	public byte getFloorShade() {
		return m_floor.getShade();
	}

	public void setFloorShade(byte shade) {
		m_floor.setShade(shade);
	}

	public short getFloorPaletteLookupTableNumber() {
		return m_floor.getPaletteLookupTableNumber();
	}

	public void setFloorPaletteLookupTableNumber(short paletteLookupTableNumber) {
		m_floor.setPaletteLookupTableNumber(paletteLookupTableNumber);
	}

	public short getFloorXPanning() {
		return m_floor.getXPanning();
	}

	public void setFloorXPanning(short xPanning) {
		m_floor.setXPanning(xPanning);
	}

	public short getFloorYPanning() {
		return m_floor.getYPanning();
	}

	public void setFloorYPanning(short yPanning) {
		m_floor.setYPanning(yPanning);
	}

	public Partition getCeiling() {
		return m_ceiling;
	}

	public void setCeiling(Partition ceiling) {
		if(ceiling == null) { throw new IllegalArgumentException("Ceiling data cannot be null."); }

		if(m_ceiling != null) {
			if(m_ceiling.equals(ceiling)) {
				return;
			}

			m_ceiling.removePartitionChangeListener(this);
		}

		m_ceiling = ceiling.clone();
		m_ceiling.addPartitionChangeListener(this);

		notifySectorChanged();
	}

	public Partition getFloor() {
		return m_floor;
	}

	public void setFloor(Partition floor) {
		if(floor == null) { throw new IllegalArgumentException("Floor data cannot be null."); }

		if(m_floor != null) {
			if(m_floor.equals(floor)) {
				return;
			}

			m_floor.removePartitionChangeListener(this);
		}

		m_floor = floor.clone();
		m_floor.addPartitionChangeListener(this);

		notifySectorChanged();
	}

	public short getVisibility() {
		return m_visibility;
	}

	public void setVisibility(short visibility) {
		if(m_visibility == visibility) {
			return;
		}

		m_visibility = visibility;

		notifySectorChanged();
	}

	public byte getFiller() {
		return m_filler;
	}

	public void setFiller(byte filler) {
		if(m_filler == filler) {
			return;
		}

		m_filler = filler;

		notifySectorChanged();
	}

	public byte[] getTrailingData() {
		return m_trailingData;
	}

	public void setTrailingData(byte[] trailingData) {
		if(trailingData != null && trailingData.length > 3) { throw new IllegalArgumentException("Invalid trailing data."); }

		m_trailingData = trailingData == null ? new byte[0] : Arrays.copyOfRange(trailingData, 0, Math.min(trailingData.length, 3));

		notifySectorChanged();
	}

	public void clearTrailingData() {
		setTrailingData(null);
	}

	public boolean hasOneTimeSound() {
		return getLowTag() > ONE_TIME_SOUND_LOW_TAG &&
			   getLowTag() < ONE_TIME_SOUND_LOW_TAG + BuildConstants.MAX_NUMBER_OF_SOUNDS;
	}

	public short getOneTimeSoundNumber() {
		if(!hasOneTimeSound()) {
			return -1;
		}

		return (short) (getLowTag() - ONE_TIME_SOUND_LOW_TAG);
	}

	public void setOneTimeSoundNumber(short soundNumber) throws IllegalArgumentException {
		if(soundNumber < 0 || soundNumber > BuildConstants.MAX_NUMBER_OF_SOUNDS) {
			throw new IllegalArgumentException("Invalid sound number, expected value inclusively between 0 and " + (BuildConstants.MAX_NUMBER_OF_SOUNDS - 1) + ".");
		}

		setLowTag((short) (ONE_TIME_SOUND_LOW_TAG + soundNumber));
	}

	public void setMap(Map map) {
		if(m_map != null) {
			removeSectorChangeListener(map);
		}

		super.setMap(map);

		if(m_map != null) {
			addSectorChangeListener(map);
		}
	}

	public byte[] serialize(int version) {
		return serialize(version, Endianness.LittleEndian);
	}

	public byte[] serialize(int version, Endianness endianness) {
		byte data[] = new byte[getSizeForVersion(version)];
		int offset = 0;
		int sectorPartitionAttributesSize = PartitionAttributes.getSizeForVersion(version);

		// serialize the first wall index
		System.arraycopy(Serializer.serializeShort(m_firstWallIndex, endianness), 0, data, offset, 2);
		offset += 2;

		// serialize the number of walls
		System.arraycopy(Serializer.serializeShort(m_numberOfWalls, endianness), 0, data, offset, 2);
		offset += 2;

		if(version == 6) {
			// serialize the sector ceiling tile number
			System.arraycopy(Serializer.serializeShort(m_ceiling.getTileNumber(), endianness), 0, data, offset, 2);
			offset += 2;

			// serialize the sector floor tile number
			System.arraycopy(Serializer.serializeShort(m_floor.getTileNumber(), endianness), 0, data, offset, 2);
			offset += 2;

			// serialize the sector ceiling slope
			System.arraycopy(Serializer.serializeShort(m_ceiling.getSlope(), endianness), 0, data, offset, 2);
			offset += 2;

			// serialize the sector floor slope
			System.arraycopy(Serializer.serializeShort(m_floor.getSlope(), endianness), 0, data, offset, 2);
			offset += 2;
		}

		// serialize the sector ceiling height
		System.arraycopy(Serializer.serializeInteger(m_ceiling.getHeight(), endianness), 0, data, offset, 4);
		offset += 4;

		// serialize the sector floor height
		System.arraycopy(Serializer.serializeInteger(m_floor.getHeight(), endianness), 0, data, offset, 4);
		offset += 4;

		if(version == 6) {
			// add the sector ceiling / floor shade values
			data[offset++] = m_ceiling.getShade();
			data[offset++] = m_floor.getShade();

			// add the sector ceiling / floor x/y panning values
			data[offset++] = (byte) m_ceiling.getXPanning();
			data[offset++] = (byte) m_floor.getXPanning();
			data[offset++] = (byte) m_ceiling.getYPanning();
			data[offset++] = (byte) m_floor.getYPanning();
		}

		// serialize the sector ceiling partition attributes
		System.arraycopy(Serializer.serializeShort(m_ceiling.getAttributes().pack().shortValue(), endianness), 0, data, offset, sectorPartitionAttributesSize);
		offset += sectorPartitionAttributesSize;

		// serialize the sector floor partition attributes
		System.arraycopy(Serializer.serializeShort(m_floor.getAttributes().pack().shortValue(), endianness), 0, data, offset, sectorPartitionAttributesSize);
		offset += sectorPartitionAttributesSize;

		if(version != 6) {
			// serialize the sector ceiling tile number
			System.arraycopy(Serializer.serializeShort(m_ceiling.getTileNumber(), endianness), 0, data, offset, 2);
			offset += 2;

			// serialize the sector ceiling slope
			System.arraycopy(Serializer.serializeShort(m_ceiling.getSlope(), endianness), 0, data, offset, 2);
			offset += 2;

			// add the sector ceiling shade value
			data[offset++] = m_ceiling.getShade();

			// add the sector ceiling palette lookup table number
			data[offset++] = (byte) m_ceiling.getPaletteLookupTableNumber();

			// add the sector ceiling x/y panning values
			data[offset++] = (byte) m_ceiling.getXPanning();
			data[offset++] = (byte) m_ceiling.getYPanning();

			// serialize the sector floor tile number
			System.arraycopy(Serializer.serializeShort(m_floor.getTileNumber(), endianness), 0, data, offset, 2);
			offset += 2;

			// serialize the sector floor slope
			System.arraycopy(Serializer.serializeShort(m_floor.getSlope(), endianness), 0, data, offset, 2);
			offset += 2;

			// add the sector floor shade value
			data[offset++] = m_floor.getShade();

			// add the sector floor palette lookup table number
			data[offset++] = (byte) m_floor.getPaletteLookupTableNumber();

			// add the sector floor x/y panning values
			data[offset++] = (byte) m_floor.getXPanning();
			data[offset++] = (byte) m_floor.getYPanning();
		}

		if(version == 6) {
			// add the sector ceiling palette lookup table number
			data[offset++] = (byte) m_ceiling.getPaletteLookupTableNumber();

			// add the sector floor palette lookup table number
			data[offset++] = (byte) m_floor.getPaletteLookupTableNumber();
		}

		// add the sector visibility value
		data[offset++] = (byte) m_visibility;

		if(version != 6) {
			// add the filler byte
			data[offset++] = (byte) m_filler;
		}

		// serialize the sector tag information
		System.arraycopy(m_tagInformation.serialize(endianness), 0, data, offset, TagInformation.SIZE);
		offset += TagInformation.SIZE;

		if(version == 6) {
			// add the trailing data
			System.arraycopy(m_trailingData, 0, data, offset, Math.min(m_trailingData.length, 3));
			offset += 3;
		}

		return data;
	}

	public static Sector deserialize(byte data[], int version) throws DeserializationException {
		return deserialize(data, version, 0);
	}

	public static Sector deserialize(byte data[], int version, int offset) throws DeserializationException {
		return deserialize(data, version, offset, Endianness.LittleEndian);
	}

	public static Sector deserialize(byte data[], int version, Endianness endianness) throws DeserializationException {
		return deserialize(data, version, 0, endianness);
	}

	public static Sector deserialize(byte data[], int version, int offset, Endianness endianness) throws DeserializationException {
		if(data == null) {
			throw new DeserializationException("Invalid sector data.");
		}

		if(offset < 0 || offset >= data.length) {
			throw new DeserializationException("Invalid data offset.");
		}

		// verify that the data is long enough to contain required information
		if(data.length - offset < getSizeForVersion(version)) {
			throw new DeserializationException("Sector data is incomplete or corrupted.");
		}

		int sectorPartitionAttributesSize = PartitionAttributes.getSizeForVersion(version);
		short ceilingTileNumber = 0;
		short ceilingSlope = 0;
		byte ceilingShade = 0;
		short ceilingPaletteLookupTableNumber = 0;
		short ceilingXPanning = 0;
		short ceilingYPanning = 0;
		short floorTileNumber = 0;
		short floorSlope = 0;
		byte floorShade = 0;
		short floorPaletteLookupTableNumber = 0;
		short floorXPanning = 0;
		short floorYPanning = 0;
		byte filler = 0;
		byte[] trailingData = null;

		// deserialize the first wall index
		short firstWallIndex = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
		offset += 2;

		// deserialize the number of walls
		short numberOfWalls = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
		offset += 2;

		if(version == 6) {
			// deserialize the sector ceiling tile number
			ceilingTileNumber = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;

			// deserialize the sector floor tile number
			floorTileNumber = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;

			// deserialize the sector ceiling slope value
			ceilingSlope = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;

			// deserialize the sector floor slope value
			floorSlope = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;
		}

		// deserialize the sector ceiling height
		int ceilingHeight = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + 4), endianness);
		offset += 4;

		// deserialize the sector floor height
		int floorHeight = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + 4), endianness);
		offset += 4;

		if(version == 6) {
			// deserialize the sector ceiling / floor shade values
			ceilingShade = data[offset++];
			floorShade = data[offset++];

			// deserialize the sector ceiling / floor panning x/y values
			ceilingXPanning = (short) (data[offset++] & 0xff);
			floorXPanning =   (short) (data[offset++] & 0xff);
			ceilingYPanning = (short) (data[offset++] & 0xff);
			floorYPanning =   (short) (data[offset++] & 0xff);
		}

		// deserialize the sector ceiling partition attributes
		PartitionAttributes ceilingAttributes = PartitionAttributes.unpack(Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + sectorPartitionAttributesSize), endianness));
		offset += sectorPartitionAttributesSize;

		// deserialize the sector floor partition attributes
		PartitionAttributes floorAttributes = PartitionAttributes.unpack(Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + sectorPartitionAttributesSize), endianness));
		offset += sectorPartitionAttributesSize;

		if(version != 6) {
			// deserialize the sector ceiling tile number
			ceilingTileNumber = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;

			// deserialize the sector ceiling slope value
			ceilingSlope = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;

			// deserialize the sector ceiling shade value
			ceilingShade = data[offset++];

			// deserialize the sector ceiling palette lookup table number
			ceilingPaletteLookupTableNumber = (short) (data[offset++] & 0xff);

			// deserialize the sector ceiling x/y panning values
			ceilingXPanning = (short) (data[offset++] & 0xff);
			ceilingYPanning = (short) (data[offset++] & 0xff);

			// deserialize the sector floor tile number
			floorTileNumber = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;

			// deserialize the sector floor slope value
			floorSlope = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;

			// deserialize the sector floor shade value
			floorShade = data[offset++];

			// deserialize the sector floor palette lookup table number
			floorPaletteLookupTableNumber = (short) (data[offset++] & 0xff);

			// deserialize the sector floor x/y panning values
			floorXPanning = (short) (data[offset++] & 0xff);
			floorYPanning = (short) (data[offset++] & 0xff);
		}

		if(version == 6) {
			// deserialize the sector ceiling palette lookup table number
			ceilingPaletteLookupTableNumber = (short) (data[offset++] & 0xff);

			// deserialize the sector floor palette lookup table number
			floorPaletteLookupTableNumber = (short) (data[offset++] & 0xff);
		}

		// deserialize the sector visibility
		short visibility = (short) (data[offset++] & 0xff);

		if(version != 6) {
			// read the filler byte
			filler = data[offset++];
		}

		// deserialize the sector tag information
		TagInformation tagInformation = TagInformation.deserialize(data, offset, endianness);
		offset += TagInformation.SIZE;

		// copy trailing data data
		if(version != 6) {
			trailingData = new byte[0];
		}
		else {
			trailingData = Arrays.copyOfRange(data, offset, offset + 3);
		}

		// initialize the ceiling and floor partitions
		Partition ceiling = new Partition(ceilingHeight, ceilingAttributes, ceilingTileNumber, ceilingSlope, ceilingShade, ceilingPaletteLookupTableNumber, ceilingXPanning, ceilingYPanning);
		Partition floor = new Partition(floorHeight, floorAttributes, floorTileNumber, floorSlope, floorShade, floorPaletteLookupTableNumber, floorXPanning, floorYPanning);

		return new Sector(firstWallIndex, numberOfWalls, ceiling, floor, visibility, filler, tagInformation, trailingData);
	}

	public JSONObject toJSONObject() {
		JSONObject sector = new JSONObject();
		sector.put(FIRST_WALL_INDEX_ATTRIBUTE_NAME, m_firstWallIndex);
		sector.put(NUMBER_OF_WALLS_ATTRIBUTE_NAME, m_numberOfWalls);
		sector.put(CEILING_ATTRIBUTE_NAME, m_ceiling.toJSONObject());
		sector.put(FLOOR_ATTRIBUTE_NAME, m_floor.toJSONObject());
		sector.put(VISIBILITY_ATTRIBUTE_NAME, m_visibility);
		sector.put(FILLER_ATTRIBUTE_NAME, m_filler);
		m_tagInformation.addToJSONObject(sector);

		if(m_trailingData.length != 0) {
			JSONArray trailingData = new JSONArray();
	
			for(int i = 0; i < m_trailingData.length; i++) {
				trailingData.put(m_trailingData[i]);
			}
	
			sector.put(TRAILING_DATA_ATTRIBUTE_NAME, trailingData);
		}

		return sector;
	}

	public static Sector fromJSONObject(JSONObject sector) throws IllegalArgumentException, JSONException, MalformedItemAttributeException {
		if(sector == null) {
			throw new IllegalArgumentException("Sector JSON data cannot be null.");
		}

		byte trailingData[] = null;

		if(sector.has(TRAILING_DATA_ATTRIBUTE_NAME)) {
			JSONArray trailingJSONData = sector.getJSONArray(TRAILING_DATA_ATTRIBUTE_NAME);
			trailingData = new byte[trailingJSONData.length()];

			for(int i = 0; i < trailingJSONData.length(); i++) {
				trailingData[i] = (byte) trailingJSONData.getInt(i);
			}
		}

		return new Sector(
			(short) sector.getInt(FIRST_WALL_INDEX_ATTRIBUTE_NAME),
			(short) sector.getInt(NUMBER_OF_WALLS_ATTRIBUTE_NAME),
			Partition.fromJSONObject(sector.getJSONObject(CEILING_ATTRIBUTE_NAME)),
			Partition.fromJSONObject(sector.getJSONObject(FLOOR_ATTRIBUTE_NAME)),
			(short) sector.getInt(VISIBILITY_ATTRIBUTE_NAME),
			(byte) sector.optInt(FILLER_ATTRIBUTE_NAME, 0),
			TagInformation.fromJSONObject(sector),
			trailingData
		);
	}

	public Sector clone() {
		return clone(true);
	}

	public Sector clone(boolean reassignMap) {
		Sector clonedSector = new Sector(m_firstWallIndex, m_numberOfWalls, m_ceiling, m_floor, m_visibility, m_filler, m_tagInformation, m_trailingData);

		if(reassignMap) {
			clonedSector.setMap(m_map);
		}

		return clonedSector;
	}

	public int numberOfSectorChangeListeners() {
		return m_sectorChangeListeners.size();
	}
	
	public SectorChangeListener getSectorChangeListener(int index) {
		if(index < 0 || index >= m_sectorChangeListeners.size()) { return null; }

		return m_sectorChangeListeners.elementAt(index);
	}
	
	public boolean hasSectorChangeListener(SectorChangeListener c) {
		return m_sectorChangeListeners.contains(c);
	}
	
	public int indexOfSectorChangeListener(SectorChangeListener c) {
		return m_sectorChangeListeners.indexOf(c);
	}
	
	public boolean addSectorChangeListener(SectorChangeListener c) {
		if(c == null || m_sectorChangeListeners.contains(c)) { return false; }

		m_sectorChangeListeners.add(c);

		return true;
	}
	
	public boolean removeSectorChangeListener(int index) {
		if(index < 0 || index >= m_sectorChangeListeners.size()) { return false; }

		m_sectorChangeListeners.remove(index);

		return true;
	}
	
	public boolean removeSectorChangeListener(SectorChangeListener c) {
		if(c == null) { return false; }

		return m_sectorChangeListeners.remove(c);
	}
	
	public void clearSectorChangeListeners() {
		m_sectorChangeListeners.clear();
	}
	
	protected void notifySectorChanged() {
		for(int i=0;i<m_sectorChangeListeners.size();i++) {
			m_sectorChangeListeners.elementAt(i).handleMapComponentChange(this);
			m_sectorChangeListeners.elementAt(i).handleSectorChange(this);
		}
	}

	public void handlePartitionChange(Partition partition) {
		notifySectorChanged();
	}

	public void handleTagInformationChange(TagInformation tagInformation) {
		notifySectorChanged();
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof Sector)) {
			return false;
		}

		Sector s = (Sector) o;

		return m_firstWallIndex == s.m_firstWallIndex &&
			   m_numberOfWalls == s.m_numberOfWalls &&
			   m_ceiling.equals(s.m_ceiling) &&
			   m_floor.equals(s.m_floor) &&
			   m_visibility == s.m_visibility &&
			   m_tagInformation.equals(m_tagInformation);
	}

}
