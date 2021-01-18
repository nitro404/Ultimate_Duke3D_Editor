package map;

import java.util.*;
import java.awt.*;
import item.*;
import exception.*;
import utilities.*;
import org.json.*;

public class Sprite extends TaggedMapComponent implements ItemAttributeChangeListener, PositionChangeListener, VelocityChangeListener {

	protected Position m_position;
	protected SpriteAttributes m_attributes;
	protected short m_tileNumber;
	protected byte m_shade;
	protected short m_paletteLookupTableNumber;
	protected short m_clippingDistance;
	protected byte m_filler; // note: unused and not prevent in version 6
	protected short m_xRepeat;
	protected short m_yRepeat;
	protected byte m_xOffset;
	protected byte m_yOffset;
	protected short m_sectorNumber;
	protected short m_statusNumber;
	protected short m_angle;
	protected short m_owner;
	protected Velocity m_velocity;
	protected Vector<SpriteChangeListener> m_spriteChangeListeners;

	public static final int SIZE = 18 + Position.SIZE + SpriteAttributes.SIZE + Velocity.SIZE + TagInformation.SIZE;

	public static final short ECHO_EFFECT_OFFSET = 1000;
	public static final short MAX_ECHO_EFFECT = 255;

	protected static final int SPRITE_SIZE = 4;
	protected static final int SPRITE_TAIL_LENGTH = 128;

	public static final String POSITION_ATTRIBUTE_NAME = "position";
	public static final String ATTRIBUTES_ATTRIBUTE_NAME = "attributes";
	public static final String TILE_NUMBER_ATTRIBUTE_NAME = "tileNumber";
	public static final String SHADE_ATTRIBUTE_NAME = "shade";
	public static final String PALETTE_LOOKUP_TABLE_NUMBER_ATTRIBUTE_NAME = "paletteLookupTableNumber";
	public static final String CLIPPING_DISTANCE_ATTRIBUTE_NAME = "clippingDistance";
	public static final String FILLER_ATTRIBUTE_NAME = "filler";
	public static final String X_REPEAT_ATTRIBUTE_NAME = "xRepeat";
	public static final String Y_REPEAT_ATTRIBUTE_NAME = "yRepeat";
	public static final String X_OFFSET_ATTRIBUTE_NAME = "xOffset";
	public static final String Y_OFFSET_ATTRIBUTE_NAME = "yOffset";
	public static final String SECTOR_NUMBER_ATTRIBUTE_NAME = "sectorNumber";
	public static final String STATUS_NUMBER_ATTRIBUTE_NAME = "statusNumber";
	public static final String ANGLE_ATTRIBUTE_NAME = "angle";
	public static final String OWNER_ATTRIBUTE_NAME = "owner";
	public static final String VELOCITY_ATTRIBUTE_NAME = "velocity";
	public static final String TAG_INFORMATION_ATTRIBUTE_NAME = "tagInformation";

	public Sprite(Position position, short attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, short velocityX, short velocityY, short velocityZ, int lowTag, int highTag, short extra) {
		this(position, SpriteAttributes.unpack(attributes), tileNumber, shade, paletteLookupTableNumber, clippingDistance, (byte) 0, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, new Velocity(velocityX, velocityY, velocityZ), new TagInformation(lowTag, highTag, extra));
	}

	public Sprite(Position position, short attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, byte filler, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, short velocityX, short velocityY, short velocityZ, int lowTag, int highTag, short extra) {
		this(position, SpriteAttributes.unpack(attributes), tileNumber, shade, paletteLookupTableNumber, clippingDistance, filler, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, new Velocity(velocityX, velocityY, velocityZ), new TagInformation(lowTag, highTag, extra));
	}

	public Sprite(Position position, SpriteAttributes attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, short velocityX, short velocityY, short velocityZ, int lowTag, int highTag, short extra) {
		this(position, attributes, tileNumber, shade, paletteLookupTableNumber, clippingDistance, (byte) 0, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, new Velocity(velocityX, velocityY, velocityZ), new TagInformation(lowTag, highTag, extra));
	}

	public Sprite(Position position, SpriteAttributes attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, byte filler, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, short velocityX, short velocityY, short velocityZ, int lowTag, int highTag, short extra) {
		this(position, attributes, tileNumber, shade, paletteLookupTableNumber, clippingDistance, filler, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, new Velocity(velocityX, velocityY, velocityZ), new TagInformation(lowTag, highTag, extra));
	}

	public Sprite(Position position, short attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, short velocityX, short velocityY, short velocityZ, TagInformation tagInformation) {
		this(position, SpriteAttributes.unpack(attributes), tileNumber, shade, paletteLookupTableNumber, clippingDistance, (byte) 0, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, new Velocity(velocityX, velocityY, velocityZ), tagInformation);
	}

	public Sprite(Position position, short attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, byte filler, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, short velocityX, short velocityY, short velocityZ, TagInformation tagInformation) {
		this(position, SpriteAttributes.unpack(attributes), tileNumber, shade, paletteLookupTableNumber, clippingDistance, filler, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, new Velocity(velocityX, velocityY, velocityZ), tagInformation);
	}

	public Sprite(Position position, SpriteAttributes attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, short velocityX, short velocityY, short velocityZ, TagInformation tagInformation) {
		this(position, attributes, tileNumber, shade, paletteLookupTableNumber, clippingDistance, (byte) 0, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, new Velocity(velocityX, velocityY, velocityZ), tagInformation);
	}

	public Sprite(Position position, SpriteAttributes attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, byte filler, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, short velocityX, short velocityY, short velocityZ, TagInformation tagInformation) {
		this(position, attributes, tileNumber, shade, paletteLookupTableNumber, clippingDistance, filler, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, new Velocity(velocityX, velocityY, velocityZ), tagInformation);
	}

	public Sprite(Position position, short attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, Velocity velocity, int lowTag, int highTag, short extra) {
		this(position, SpriteAttributes.unpack(attributes), tileNumber, shade, paletteLookupTableNumber, clippingDistance, (byte) 0, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, velocity, new TagInformation(lowTag, highTag, extra));
	}

	public Sprite(Position position, short attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, byte filler, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, Velocity velocity, int lowTag, int highTag, short extra) {
		this(position, SpriteAttributes.unpack(attributes), tileNumber, shade, paletteLookupTableNumber, clippingDistance, filler, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, velocity, new TagInformation(lowTag, highTag, extra));
	}

	public Sprite(Position position, SpriteAttributes attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, Velocity velocity, int lowTag, int highTag, short extra) {
		this(position, attributes, tileNumber, shade, paletteLookupTableNumber, clippingDistance, (byte) 0, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, velocity, new TagInformation(lowTag, highTag, extra));
	}

	public Sprite(Position position, SpriteAttributes attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, byte filler, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, Velocity velocity, int lowTag, int highTag, short extra) {
		this(position, attributes, tileNumber, shade, paletteLookupTableNumber, clippingDistance, filler, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, velocity, new TagInformation(lowTag, highTag, extra));
	}

	public Sprite(Position position, short attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, Velocity velocity, TagInformation tagInformation) {
		this(position, SpriteAttributes.unpack(attributes), tileNumber, shade, paletteLookupTableNumber, clippingDistance, (byte) 0, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, velocity, tagInformation);
	}

	public Sprite(Position position, short attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, byte filler, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, Velocity velocity, TagInformation tagInformation) {
		this(position, SpriteAttributes.unpack(attributes), tileNumber, shade, paletteLookupTableNumber, clippingDistance, filler, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, velocity, tagInformation);
	}

	public Sprite(Position position, SpriteAttributes attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, Velocity velocity, TagInformation tagInformation) {
		this(position, attributes, tileNumber, shade, paletteLookupTableNumber, clippingDistance, (byte) 0, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, velocity, tagInformation);
	}

	public Sprite(Position position, SpriteAttributes attributes, short tileNumber, byte shade, short paletteLookupTableNumber, short clippingDistance, byte filler, short xRepeat, short yRepeat, byte xOffset, byte yOffset, short sectorNumber, short statusNumber, short angle, short owner, Velocity velocity, TagInformation tagInformation) {
		super(tagInformation);

		if(position == null) { throw new IllegalArgumentException("Position cannot be null."); }
		if(attributes == null) { throw new IllegalArgumentException("Attributes cannot be null."); }
		if(paletteLookupTableNumber < 0 || paletteLookupTableNumber > 255) { throw new IllegalArgumentException("Invalid palette lookup table number value: " + paletteLookupTableNumber + ", expected value between 0 and 255."); }
		if(clippingDistance < 0 || clippingDistance > 255) { throw new IllegalArgumentException("Invalid clipping distance value: " + clippingDistance + ", expected value between 0 and 255."); }
		if(xRepeat < 0 || xRepeat > 255) { throw new IllegalArgumentException("Invalid x repeat value: " + xRepeat + ", expected value between 0 and 255."); }
		if(yRepeat < 0 || yRepeat > 255) { throw new IllegalArgumentException("Invalid y repeat value: " + yRepeat + ", expected value between 0 and 255."); }
		if(velocity == null) { throw new IllegalArgumentException("Velocity cannot be null."); }
		if(angle < BuildConstants.MIN_ANGLE || angle > BuildConstants.MAX_ANGLE) { throw new IllegalArgumentException("Invalid sprite angle, expected value between " + BuildConstants.MIN_ANGLE + " and " + BuildConstants.MAX_ANGLE + ", inclusively."); }

		m_tileNumber = tileNumber;
		m_shade = shade;
		m_paletteLookupTableNumber = paletteLookupTableNumber;
		m_clippingDistance = clippingDistance;
		m_filler = filler;
		m_xRepeat = xRepeat;
		m_yRepeat = yRepeat;
		m_xOffset = xOffset;
		m_yOffset = yOffset;
		m_sectorNumber = sectorNumber;
		m_statusNumber = statusNumber;
		m_angle = angle;
		m_owner = owner;
		m_tagInformation = tagInformation.clone();
		m_spriteChangeListeners = new Vector<SpriteChangeListener>();

		setPosition(position);
		setAttributes(attributes);
		setVelocity(velocity);
	}

	public int getX() {
		return m_position.getX();
	}

	public void setX(int x) {
		m_position.setX(x);
	}

	public int getY() {
		return m_position.getY();
	}

	public void setY(int y) {
		m_position.setY(y);
	}

	public int getZ() {
		return m_position.getZ();
	}

	public void setZ(int z) {
		m_position.setZ(z);
	}

	public Position getPosition() {
		return m_position;
	}

	public void setPosition(Position position) throws IllegalArgumentException {
		if(position == null) { throw new IllegalArgumentException("Position cannot be null."); }

		if(m_position != null) {
			if(m_position.equals(position)) {
				return;
			}

			m_position.removePositionChangeListener(this);
		}

		m_position = position.clone();
		m_position.addPositionChangeListener(this);

		notifySpriteChanged();
	}

	public boolean getBlockClipping() {
		return m_attributes.getBlockClipping();
	}

	public boolean setBlockClipping(boolean blockClipping) {
		return m_attributes.setBlockClipping(blockClipping);
	}

	public boolean getTranslucent() {
		return m_attributes.getTranslucent();
	}

	public boolean setTranslucent(boolean translucent) {
		return m_attributes.setTranslucent(translucent);
	}

	public boolean getXFlipped() {
		return m_attributes.getXFlipped();
	}

	public boolean setXFlipped(boolean xFlipped) {
		return m_attributes.setXFlipped(xFlipped);
	}

	public boolean getYFlipped() {
		return m_attributes.getYFlipped();
	}

	public boolean setYFlipped(boolean yFlipped) {
		return m_attributes.setYFlipped(yFlipped);
	}

	public SpriteDrawType getDrawType() {
		return m_attributes.getDrawType();
	}

	public byte getDrawTypeValue() {
		return m_attributes.getDrawTypeValue();
	}

	public boolean setDrawType(SpriteDrawType drawType) {
		return m_attributes.setDrawType(drawType);
	}

	public boolean getOneSided() {
		return m_attributes.getOneSided();
	}

	public boolean setOneSided(boolean oneSided) {
		return m_attributes.setOneSided(oneSided);
	}

	public boolean getCentered() {
		return m_attributes.getCentered();
	}

	public boolean setCentered(boolean centered) {
		return m_attributes.setCentered(centered);
	}

	public boolean getBlockHitscan() {
		return m_attributes.getBlockHitscan();
	}

	public boolean setBlockHitscan(boolean blockHitscan) {
		return m_attributes.setBlockHitscan(blockHitscan);
	}

	public boolean getReverseTranslucent() {
		return m_attributes.getReverseTranslucent();
	}

	public boolean setReverseTranslucent(boolean reverseTranslucent) {
		return m_attributes.setReverseTranslucent(reverseTranslucent);
	}

	public boolean getInvisible() {
		return m_attributes.getInvisible();
	}

	public boolean setInvisible(boolean invisible) {
		return m_attributes.setInvisible(invisible);
	}

	public SpriteAttributes getAttributes() {
		return m_attributes;
	}

	public void setAttributes(short attributes) throws IllegalArgumentException {
		setAttributes(SpriteAttributes.unpack(attributes));
	}

	public void setAttributes(SpriteAttributes attributes) throws IllegalArgumentException {
		if(attributes == null) { throw new IllegalArgumentException("Attributes cannot be null."); }

		if(m_attributes != null) {
			if(m_attributes.equals(attributes)) {
				return;
			}

			m_attributes.removeItemAttributeChangeListener(this);
		}

		m_attributes = (SpriteAttributes) attributes.clone();
		m_attributes.addItemAttributeChangeListener(this);

		notifySpriteChanged();
	}

	public short getTileNumber() {
		return m_tileNumber;
	}

	public void setTileNumber(short tileNumber) {
		if(m_tileNumber == tileNumber) {
			return;
		}

		m_tileNumber = tileNumber;

		notifySpriteChanged();
	}

	public byte getShade() {
		return m_shade;
	}

	public void setShade(byte shade) {
		if(m_shade == shade) {
			return;
		}

		m_shade = shade;

		notifySpriteChanged();
	}

	public short getPaletteLookupTableNumber() {
		return m_paletteLookupTableNumber;
	}

	public void setPaletteLookupTableNumber(short paletteLookupTableNumber) throws IllegalArgumentException {
		if(paletteLookupTableNumber < 0 || paletteLookupTableNumber > 255) { throw new IllegalArgumentException("Invalid palette lookup table number value: " + paletteLookupTableNumber + ", expected value between 0 and 255."); }

		if(m_paletteLookupTableNumber == paletteLookupTableNumber) {
			return;
		}

		m_paletteLookupTableNumber = paletteLookupTableNumber;

		notifySpriteChanged();
	}

	public short getClippingDistance() {
		return m_clippingDistance;
	}

	public void setClippingDistance(short clippingDistance) throws IllegalArgumentException {
		if(clippingDistance < 0 || clippingDistance > 255) { throw new IllegalArgumentException("Invalid clipping distance value: " + clippingDistance + ", expected value between 0 and 255."); }

		if(m_clippingDistance == clippingDistance) {
			return;
		}

		m_clippingDistance = clippingDistance;

		notifySpriteChanged();
	}

	public byte getFiller() {
		return m_filler;
	}

	public void setFiller(byte filler) {
		if(m_filler == filler) {
			return;
		}

		m_filler = filler;

		notifySpriteChanged();
	}

	public short getXRepeat() {
		return m_xRepeat;
	}

	public void setXRepeat(short xRepeat) throws IllegalArgumentException {
		if(xRepeat < 0 || xRepeat > 255) { throw new IllegalArgumentException("Invalid x repeat value: " + xRepeat + ", expected value between 0 and 255."); }

		if(m_xRepeat == xRepeat) {
			return;
		}

		m_xRepeat = xRepeat;

		notifySpriteChanged();
	}

	public short getYRepeat() {
		return m_yRepeat;
	}

	public void setYRepeat(short yRepeat) throws IllegalArgumentException {
		if(yRepeat < 0 || yRepeat > 255) { throw new IllegalArgumentException("Invalid y repeat value: " + yRepeat + ", expected value between 0 and 255."); }

		if(m_yRepeat == yRepeat) {
			return;
		}

		m_yRepeat = yRepeat;

		notifySpriteChanged();
	}

	public byte getXOffset() {
		return m_xOffset;
	}

	public void setXOffset(byte xOffset) {
		if(m_xOffset == xOffset) {
			return;
		}

		m_xOffset = xOffset;

		notifySpriteChanged();
	}

	public byte getYOffset() {
		return m_yOffset;
	}

	public void setYOffset(byte yOffset) {
		if(m_yOffset == yOffset) {
			return;
		}

		m_yOffset = yOffset;

		notifySpriteChanged();
	}

	public short getSectorNumber() {
		return m_sectorNumber;
	}

	public Sector getSector() {
		if(m_map == null) {
			return null;
		}

		return m_map.getSector(m_sectorNumber);
	}

	public void setSectorNumber(short sectorNumber) {
		if(m_sectorNumber == sectorNumber) {
			return;
		}

		m_sectorNumber = sectorNumber;

		notifySpriteChanged();
	}

	public short getStatusNumber() {
		return m_statusNumber;
	}

	public void setStatusNumber(short statusNumber) {
		if(m_statusNumber == statusNumber) {
			return;
		}

		m_statusNumber = statusNumber;

		notifySpriteChanged();
	}

	public short getAngle() {
		return m_angle;
	}

	public double getAngleDegrees() {
		return (m_angle / (double) BuildConstants.MAX_ANGLE) * 360.0;
	}

	public double getAngleRadians() {
		return (m_angle / (double) BuildConstants.MAX_ANGLE) * Math.PI * 2.0;
	}

	public void setAngle(short angle) throws IllegalArgumentException {
		if(angle < BuildConstants.MIN_ANGLE || angle > BuildConstants.MAX_ANGLE) { throw new IllegalArgumentException("Invalid sprite angle, expected value between " + BuildConstants.MIN_ANGLE + " and " + BuildConstants.MAX_ANGLE + ", inclusively."); }

		if(m_angle == angle) {
			return;
		}

		m_angle = angle;

		notifySpriteChanged();
	}

	public short getOwner() {
		return m_angle;
	}

	public void setOwner(short owner) {
		if(m_owner == owner) {
			return;
		}

		m_owner = owner;

		notifySpriteChanged();
	}

	public Velocity getVelocity() {
		return m_velocity;
	}

	public void setVelocity(Velocity velocity) throws IllegalArgumentException {
		if(velocity == null) {
			throw new IllegalArgumentException("Velocity cannot be null.");
		}

		if(m_velocity != null) {
			if(m_velocity.equals(velocity)) {
				return;
			}

			m_velocity.removeVelocityChangeListener(this);
		}

		m_velocity = velocity;
		m_velocity.addVelocityChangeListener(this);

		notifySpriteChanged();
	}

	public boolean isSpecialSprite() {
		return SpecialSprite.getSpecialSpriteByTileNumber(m_tileNumber).isValid();
	}

	public boolean hasPrimarySound() {
		if(getSpecialSprite() == SpecialSprite.MusicAndSFX) {
			return getLowTag() > 0 && getLowTag() < ECHO_EFFECT_OFFSET;
		}
		else if(isSwitchWithSoundOverride()) {
			return getHighTag() > 0 && getHighTag() < BuildConstants.MAX_NUMBER_OF_SOUNDS;
		}

		return false;
	}

	public boolean hasSecondarySound() {
		if(getSpecialSprite() != SpecialSprite.MusicAndSFX) {
			return false;
		}

		Sector sector = getSector();
		
		if(sector == null || !sector.hasLowTag()) {
			return false;
		}

		SpecialSectorTag specialSectorTag = SpecialSectorTag.getSpecialSectorTagByTagNumber(sector.getLowTag());

		if(!specialSectorTag.isValid() || !specialSectorTag.hasMultipleSounds()) {
			return false;
		}

		return true;
	}

	public boolean isActivationSoundSprite() {
		if(getSpecialSprite() != SpecialSprite.MusicAndSFX) {
			return false;
		}

		Sector sector = getSector();
		
		if(sector == null || !sector.hasLowTag()) {
			return false;
		}

		return true;
	}

	public boolean isAmbientSoundSprite() {
		if(getSpecialSprite() != SpecialSprite.MusicAndSFX) {
			return false;
		}

		Sector sector = getSector();
		
		if(sector == null || sector.hasLowTag()) {
			return false;
		}

		return getLowTag() < ECHO_EFFECT_OFFSET;
	}

	public boolean isEchoEffectSoundSprite() {
		if(getSpecialSprite() != SpecialSprite.MusicAndSFX) {
			return false;
		}

		Sector sector = getSector();
		
		if(sector == null || sector.hasLowTag()) {
			return false;
		}

		return getLowTag() >= ECHO_EFFECT_OFFSET &&
			   getLowTag() <= ECHO_EFFECT_OFFSET + MAX_ECHO_EFFECT;
	}

	public boolean isSwitch() {
		return SwitchSprite.getSwitchSpriteByTileNumber(m_tileNumber).isValid();
	}

	public boolean isSwitchWithSoundOverride() {
		return SwitchSprite.getSwitchSpriteByTileNumber(m_tileNumber).hasSoundOverride();
	}

	public boolean isSwitchWithCustomSound() {
		return SwitchSprite.getSwitchSpriteByTileNumber(m_tileNumber).hasSoundOverride() &&
			   getHighTag() > 0 && getHighTag() <= BuildConstants.MAX_NUMBER_OF_SOUNDS;
	}

	public SpecialSprite getSpecialSprite() {
		return SpecialSprite.getSpecialSpriteByTileNumber(m_tileNumber);
	}

	public short getPrimarySoundNumber() {
		if(getSpecialSprite() == SpecialSprite.MusicAndSFX) {
			Sector sector = getSector();
			
			if(sector == null || getLowTag() >= ECHO_EFFECT_OFFSET) {
				return -1;
			}
	
			return (short) getLowTag();
		}
		else if(isSwitchWithSoundOverride()) {
			return (short) getHighTag();
		}

		return -1;
	}

	public boolean setPrimarySoundNumber(short soundNumber) {
		if(soundNumber < 0 || soundNumber >= BuildConstants.MAX_NUMBER_OF_SOUNDS) {
			throw new IllegalArgumentException("Sound number value of " + soundNumber + " exceeds maximum number of sounds.");
		}

		if(getSpecialSprite() == SpecialSprite.MusicAndSFX) {
			setLowTag(soundNumber);

			return true;
		}
		else if(isSwitchWithSoundOverride()) {
			setHighTag(soundNumber);

			return true;
		}

		return false;
	}

	public short getSecondarySoundNumber() {
		if(getSpecialSprite() != SpecialSprite.MusicAndSFX) {
			return -1;
		}

		Sector sector = getSector();
		
		if(sector == null || !sector.hasLowTag()) {
			return -1;
		}

		SpecialSectorTag specialSectorTag = SpecialSectorTag.getSpecialSectorTagByTagNumber(sector.getLowTag());

		if(!specialSectorTag.isValid() || !specialSectorTag.hasMultipleSounds()) {
			return -1;
		}

		return (short) getHighTag();
	}

	public boolean setSecondarySoundNumber(short soundNumber) {
		if(soundNumber < 0 || soundNumber >= BuildConstants.MAX_NUMBER_OF_SOUNDS) {
			throw new IllegalArgumentException("Sound number value of " + soundNumber + " exceeds maximum number of sounds.");
		}

		if(getSpecialSprite() != SpecialSprite.MusicAndSFX) {
			return false;
		}

		Sector sector = getSector();

		if(sector == null || !sector.hasLowTag()) {
			return false;
		}

		SpecialSectorTag specialSectorTag = SpecialSectorTag.getSpecialSectorTagByTagNumber(sector.getLowTag());

		if(!specialSectorTag.isValid() || !specialSectorTag.hasMultipleSounds()) {
			return false;
		}

		setHighTag(soundNumber);

		return true;
	}

	public void setMap(Map map) {
		if(m_map != null) {
			removeSpriteChangeListener(map);
		}

		super.setMap(map);

		if(m_map != null) {
			addSpriteChangeListener(map);
		}
	}

	public byte[] serialize(int version) {
		return serialize(version, Endianness.LittleEndian);
	}

	public byte[] serialize(int version, Endianness endianness) {
		byte data[] = new byte[SIZE];
		int offset = 0;

		// serialize the sprite position
		System.arraycopy(m_position.serialize(endianness), 0, data, offset, Position.SIZE);
		offset += Position.SIZE;

		// serialize the sprite attributes
		System.arraycopy(Serializer.serializeShort(m_attributes.pack().shortValue(), endianness), 0, data, offset, 2);
		offset += 2;

		if(version != 6) {
			// serialize the sprite tile number
			System.arraycopy(Serializer.serializeShort(m_tileNumber, endianness), 0, data, offset, 2);
			offset += 2;
		}

		// serialize the shade, palette lookup table number and clipping distance values
		data[offset++] = m_shade;
		data[offset++] = (byte) m_paletteLookupTableNumber;
		data[offset++] = (byte) m_clippingDistance;
		
		if(version != 6) {
			// add the filler byte
			data[offset++] = m_filler;
		}

		// serialize the x/y repeat and offset values
		data[offset++] = (byte) m_xRepeat;
		data[offset++] = (byte) m_yRepeat;
		data[offset++] = m_xOffset;
		data[offset++] = m_yOffset;

		if(version != 6) {
			// serialize the sprite sector number
			System.arraycopy(Serializer.serializeShort(m_sectorNumber, endianness), 0, data, offset, 2);
			offset += 2;

			// serialize the sprite status number
			System.arraycopy(Serializer.serializeShort(m_statusNumber, endianness), 0, data, offset, 2);
			offset += 2;
		}
		else {
			// serialize the sprite tile number
			System.arraycopy(Serializer.serializeShort(m_tileNumber, endianness), 0, data, offset, 2);
			offset += 2;
		}

		// serialize the sprite angle
		System.arraycopy(Serializer.serializeShort(m_angle, endianness), 0, data, offset, 2);
		offset += 2;

		if(version != 6) {
			// serialize the sprite owner value
			System.arraycopy(Serializer.serializeShort(m_owner, endianness), 0, data, offset, 2);
			offset += 2;
		}

		// serialize the sprite velocity vector
		System.arraycopy(m_velocity.serialize(endianness), 0, data, offset, Velocity.SIZE);
		offset += Velocity.SIZE;

		if(version == 6) {
			// serialize the sprite owner value
			System.arraycopy(Serializer.serializeShort(m_owner, endianness), 0, data, offset, 2);
			offset += 2;

			// serialize the sprite sector number
			System.arraycopy(Serializer.serializeShort(m_sectorNumber, endianness), 0, data, offset, 2);
			offset += 2;

			// serialize the sprite status number
			System.arraycopy(Serializer.serializeShort(m_statusNumber, endianness), 0, data, offset, 2);
			offset += 2;
		}

		// serialize the sprite tag information
		System.arraycopy(m_tagInformation.serialize(endianness), 0, data, offset, TagInformation.SIZE);
		offset += TagInformation.SIZE;

		if(version == 6) {
			// add the filler byte
			data[offset++] = m_filler;
		}

		return data;
	}

	public static Sprite deserialize(byte data[], int version) throws DeserializationException {
		return deserialize(data, version, 0);
	}

	public static Sprite deserialize(byte data[], int version, int offset) throws DeserializationException {
		return deserialize(data, version, offset, Endianness.LittleEndian);
	}

	public static Sprite deserialize(byte data[], int version, Endianness endianness) throws DeserializationException {
		return deserialize(data, version, 0, endianness);
	}

	public static Sprite deserialize(byte data[], int version, int offset, Endianness endianness) throws DeserializationException {
		if(data == null) {
			throw new DeserializationException("Invalid sprite data.");
		}

		if(offset < 0 || offset >= data.length) {
			throw new DeserializationException("Invalid data offset.");
		}

		// verify that the data is long enough to contain required information
		if(data.length - offset < SIZE) {
			throw new DeserializationException("Sprite data is incomplete or corrupted.");
		}

		short tileNumber = -1;
		byte filler = 0;
		short sectorNumber = -1;
		short statusNumber = -1;
		short owner = -1;

		// deserialize the sprite position
		Position position = Position.deserialize(data, offset, endianness);
		offset += Position.SIZE;

		// deserialize the sprite attributes
		SpriteAttributes attributes = SpriteAttributes.unpack(Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness));
		offset += 2;

		if(version != 6) {
			// deserialize the sprite tile number
			tileNumber = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;
		}

		// read the sprite shade value
		byte shade = data[offset++];

		// deserialize the sprite palette lookup table number
		short paletteLookupTableNumber = (short) (data[offset++] & 0xff);

		// deserialize the sprite clipping distance
		short clippingDistance = (short) (data[offset++] & 0xff);

		if(version != 6) {
			// read the filler byte
			filler = data[offset++];
		}

		// read the sprite x repeat value
		short xRepeat = (short) (data[offset++] & 0xff);

		// read the sprite y repeat value
		short yRepeat = (short) (data[offset++] & 0xff);

		// read the sprite x offset value
		byte xOffset = data[offset++];

		// read the sprite y offset value
		byte yOffset = data[offset++];

		if(version != 6) {
			// deserialize the sector number
			sectorNumber = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;

			// deserialize the status number
			statusNumber = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;
		}
		else {
			// deserialize the tile number
			tileNumber = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;
		}

		// deserialize the angle
		short angle = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
		offset += 2;

		if(version != 6) {
			// deserialize the owner value
			owner = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;
		}

		// deserialize the sprite velocity vector
		Velocity velocity = Velocity.deserialize(data, offset, endianness);
		offset += Velocity.SIZE;

		if(version == 6) {
			// deserialize the owner number
			owner = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;

			// deserialize the sector number
			sectorNumber = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;

			// deserialize the status number
			statusNumber = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
			offset += 2;
		}

		// deserialize the sprite tag information
		TagInformation tagInformation = TagInformation.deserialize(data, offset, endianness);
		offset += TagInformation.SIZE;

		if(version == 6) {
			// read the filler byte
			filler = data[offset++];
		}

		return new Sprite(position, attributes, tileNumber, shade, paletteLookupTableNumber, clippingDistance, filler, xRepeat, yRepeat, xOffset, yOffset, sectorNumber, statusNumber, angle, owner, velocity, tagInformation);
	}

	public JSONObject toJSONObject() {
		JSONObject sprite = new JSONObject();
		sprite.put(POSITION_ATTRIBUTE_NAME, m_position.toJSONObject());
		sprite.put(ATTRIBUTES_ATTRIBUTE_NAME, m_attributes.toJSONObject());
		sprite.put(TILE_NUMBER_ATTRIBUTE_NAME, m_tileNumber);
		sprite.put(SHADE_ATTRIBUTE_NAME, m_shade);
		sprite.put(PALETTE_LOOKUP_TABLE_NUMBER_ATTRIBUTE_NAME, m_paletteLookupTableNumber);
		sprite.put(CLIPPING_DISTANCE_ATTRIBUTE_NAME, m_clippingDistance);
		sprite.put(FILLER_ATTRIBUTE_NAME, m_filler);
		sprite.put(X_REPEAT_ATTRIBUTE_NAME, m_xRepeat);
		sprite.put(Y_REPEAT_ATTRIBUTE_NAME, m_yRepeat);
		sprite.put(X_OFFSET_ATTRIBUTE_NAME, m_xOffset);
		sprite.put(Y_OFFSET_ATTRIBUTE_NAME, m_yOffset);
		sprite.put(SECTOR_NUMBER_ATTRIBUTE_NAME, m_sectorNumber);
		sprite.put(STATUS_NUMBER_ATTRIBUTE_NAME, m_statusNumber);
		sprite.put(ANGLE_ATTRIBUTE_NAME, m_angle);
		sprite.put(OWNER_ATTRIBUTE_NAME, m_owner);
		sprite.put(VELOCITY_ATTRIBUTE_NAME, m_velocity.toJSONObject());
		sprite.put(TAG_INFORMATION_ATTRIBUTE_NAME, m_tagInformation.toJSONObject());

		return sprite;
	}

	public static Sprite fromJSONObject(JSONObject sprite) throws IllegalArgumentException, JSONException, MalformedItemAttributeException {
		if(sprite == null) {
			throw new IllegalArgumentException("Sprite JSON data cannot be null.");
		}

		return new Sprite(
			Position.fromJSONObject(sprite.getJSONObject(POSITION_ATTRIBUTE_NAME)),
			(SpriteAttributes) SpriteAttributes.fromJSONObject(sprite.getJSONObject(ATTRIBUTES_ATTRIBUTE_NAME)),
			(short) sprite.getInt(TILE_NUMBER_ATTRIBUTE_NAME),
			(byte) sprite.getInt(SHADE_ATTRIBUTE_NAME),
			(short) sprite.getInt(PALETTE_LOOKUP_TABLE_NUMBER_ATTRIBUTE_NAME),
			(short) sprite.getInt(CLIPPING_DISTANCE_ATTRIBUTE_NAME),
			(byte) sprite.optInt(FILLER_ATTRIBUTE_NAME, 0),
			(short) sprite.getInt(X_REPEAT_ATTRIBUTE_NAME),
			(short) sprite.getInt(Y_REPEAT_ATTRIBUTE_NAME),
			(byte) sprite.getInt(X_OFFSET_ATTRIBUTE_NAME),
			(byte) sprite.getInt(Y_OFFSET_ATTRIBUTE_NAME),
			(short) sprite.getInt(SECTOR_NUMBER_ATTRIBUTE_NAME),
			(short) sprite.getInt(STATUS_NUMBER_ATTRIBUTE_NAME),
			(short) sprite.getInt(ANGLE_ATTRIBUTE_NAME),
			(short) sprite.getInt(OWNER_ATTRIBUTE_NAME),
			Velocity.fromJSONObject(sprite.getJSONObject(VELOCITY_ATTRIBUTE_NAME)),
			TagInformation.fromJSONObject(sprite.getJSONObject(TAG_INFORMATION_ATTRIBUTE_NAME))
		);
	}

	public Sprite clone() {
		return clone(true);
	}

	public Sprite clone(boolean reassignMap) {
		Sprite clonedSprite = new Sprite(m_position, m_attributes, m_tileNumber, m_shade, m_paletteLookupTableNumber, m_clippingDistance, m_filler, m_xRepeat, m_yRepeat, m_xOffset, m_yOffset, m_sectorNumber, m_statusNumber, m_angle, m_owner, m_velocity, m_tagInformation);

		if(reassignMap) {
			clonedSprite.setMap(m_map);
		}

		return clonedSprite;
	}

	public int numberOfSpriteChangeListeners() {
		return m_spriteChangeListeners.size();
	}
	
	public SpriteChangeListener getSpriteChangeListener(int index) {
		if(index < 0 || index >= m_spriteChangeListeners.size()) { return null; }

		return m_spriteChangeListeners.elementAt(index);
	}
	
	public boolean hasSpriteChangeListener(SpriteChangeListener c) {
		return m_spriteChangeListeners.contains(c);
	}
	
	public int indexOfSpriteChangeListener(SpriteChangeListener c) {
		return m_spriteChangeListeners.indexOf(c);
	}
	
	public boolean addSpriteChangeListener(SpriteChangeListener c) {
		if(c == null || m_spriteChangeListeners.contains(c)) { return false; }

		m_spriteChangeListeners.add(c);

		return true;
	}
	
	public boolean removeSpriteChangeListener(int index) {
		if(index < 0 || index >= m_spriteChangeListeners.size()) { return false; }

		m_spriteChangeListeners.remove(index);

		return true;
	}
	
	public boolean removeSpriteChangeListener(SpriteChangeListener c) {
		if(c == null) { return false; }

		return m_spriteChangeListeners.remove(c);
	}
	
	public void clearSpriteChangeListeners() {
		m_spriteChangeListeners.clear();
	}
	
	protected void notifySpriteChanged() {
		for(int i=0;i<m_spriteChangeListeners.size();i++) {
			m_spriteChangeListeners.elementAt(i).handleMapComponentChange(this);
			m_spriteChangeListeners.elementAt(i).handleSpriteChange(this);
		}
	}

	public void handlePositionChange(Position position) {
		notifySpriteChanged();
	}

	public void handleTagInformationChange(TagInformation tagInformation) {
		notifySpriteChanged();
	}

	public void handleItemAttributeChange(ItemAttributes attributes, ItemAttribute attribute, byte value) {
		notifySpriteChanged();
	}

	public void handleVelocityChange(Velocity velocity) {
		notifySpriteChanged();
	}

	public void paintSprite(Graphics2D g) {
		paintSprite(g, 1.0);
	}

	public void paintSprite(Graphics2D g, double zoom) {
		if(g == null || zoom <= 0.0) {
			return;
		}

		double angle = this.getAngleRadians();
		double spriteTailLength = SPRITE_TAIL_LENGTH * zoom;

		int x = (int) (m_position.getX() * zoom);
		int y = (int) (m_position.getY() * zoom);

		int spriteTailX = (int) (x + (spriteTailLength * Math.cos(angle)));
		int spriteTailY = (int) (y + (spriteTailLength * Math.sin(angle)));

		g.setColor(Color.MAGENTA);
		g.drawOval(x - (SPRITE_SIZE / 2), y - (SPRITE_SIZE / 2), SPRITE_SIZE, SPRITE_SIZE);
		g.drawLine(x, y, spriteTailX, spriteTailY);
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof Sprite)) {
			return false;
		}

		Sprite s = (Sprite) o;

		return m_position.equals(s.m_position) &&
			   m_attributes.equals(s.m_attributes) &&
			   m_tileNumber == s.m_tileNumber &&
			   m_shade == s.m_shade &&
			   m_paletteLookupTableNumber == s.m_paletteLookupTableNumber &&
			   m_clippingDistance == s.m_clippingDistance &&
			   m_xRepeat == s.m_xRepeat &&
			   m_yRepeat == s.m_yRepeat &&
			   m_xOffset == s.m_xOffset &&
			   m_yOffset == s.m_yOffset &&
			   m_sectorNumber == s.m_sectorNumber &&
			   m_statusNumber == s.m_statusNumber &&
			   m_angle == s.m_angle &&
			   m_owner == s.m_owner &&
			   m_velocity.equals(s.m_velocity) &&
			   m_tagInformation.equals(s.m_tagInformation);
	}

}
