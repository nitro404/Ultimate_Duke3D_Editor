package art;

import java.util.*;
import exception.*;
import org.json.*;

public class TileAttributes {

// TODO: extend ItemAttributes when its added
// TODO: extra is missing from TileAttributes.setAttributes

	protected byte m_xOffset;
	protected byte m_yOffset;
	protected byte m_numberOfFrames;
	protected TileAnimationType m_animationType;
	protected byte m_animationSpeed;
	protected byte m_extra; // note: extra value is not used
	protected Vector<TileAttributeChangeListener> m_tileAttributeChangeListeners;

	final public static TileAttribute XOffset = new TileAttribute("X Offset", "xOffset", (byte) 8, true, (byte) 8);
	final public static TileAttribute YOffset = new TileAttribute("Y Offset", "yOffset", (byte) 8, true, (byte) 16);
	final public static TileAttribute NumberOfFrames = new TileAttribute("Number of Frames", "numberOfFrames", (byte) 6, false, (byte) 0);
	final public static TileAttribute AnimationType = new TileAttribute("AnimationType", "animationType", (byte) 2, false, (byte) 6);
	final public static TileAttribute AnimationSpeed = new TileAttribute("Animation Speed", "animationSpeed", (byte) 4, false, (byte) 24);
	final public static TileAttribute Extra = new TileAttribute("Extra", "extra", (byte) 4, false, (byte) 28);
	
	final public static TileAttribute Attributes[] = {
		XOffset,
		YOffset,
		NumberOfFrames,
		AnimationType,
		AnimationSpeed,
		Extra
	};
	
	public TileAttributes() {
		this((byte) 0, (byte) 0, (byte) 0, TileAnimationType.defaultAnimationType, (byte) 0, (byte) 0);
	}

	public TileAttributes(byte xOffset, byte yOffset, byte numberOfFrames, byte animationType, byte animationSpeed) throws IllegalArgumentException {
		this(xOffset, yOffset, numberOfFrames, animationType, animationSpeed, (byte) 0);
	}

	public TileAttributes(byte xOffset, byte yOffset, byte numberOfFrames, TileAnimationType animationType, byte animationSpeed) throws IllegalArgumentException {
		this(xOffset, yOffset, numberOfFrames, animationType, animationSpeed, (byte) 0);
	}
	
	public TileAttributes(byte xOffset, byte yOffset, byte numberOfFrames, byte animationType, byte animationSpeed, byte extra) throws IllegalArgumentException {
		this(xOffset, yOffset, numberOfFrames, animationType < 0 || animationType >= TileAnimationType.numberOfAnimationTypes() ? TileAnimationType.Invalid : TileAnimationType.values()[animationType], animationSpeed, extra);
	}
	
	public TileAttributes(byte xOffset, byte yOffset, byte numberOfFrames, TileAnimationType animationType, byte animationSpeed, byte extra) throws IllegalArgumentException {
		if(!XOffset.isValidValue(xOffset)) { throw new IllegalArgumentException("Invalid x offset tile attribute value: " + xOffset + ", expected value between " + XOffset.getMinimumValue() + " and " + XOffset.getMaximumValue() + "."); }
		if(!YOffset.isValidValue(yOffset)) { throw new IllegalArgumentException("Invalid y offset tile attribute value: " + yOffset + ", expected value between " + YOffset.getMinimumValue() + " and " + YOffset.getMaximumValue() + "."); }
		if(!NumberOfFrames.isValidValue(numberOfFrames)) { throw new IllegalArgumentException("Invalid number of frames tile attribute value: " + numberOfFrames + ", expected value between " + NumberOfFrames.getMinimumValue() + " and " + NumberOfFrames.getMaximumValue() + "."); }
		if(!animationType.isValid()) { throw new IllegalArgumentException("Invalid animation type tile attribute value: \"" + animationType.getDisplayName() + "\"."); }
		if(!AnimationSpeed.isValidValue(animationSpeed)) { throw new IllegalArgumentException("Invalid animation speed tile attribute value: " + animationSpeed + ", expected value between " + AnimationSpeed.getMinimumValue() + " and " + AnimationSpeed.getMaximumValue() + "."); }
		if(!Extra.isValidValue(extra)) { throw new IllegalArgumentException("Invalid extra tile attribute value: " + extra + ", expected value between " + Extra.getMinimumValue() + " and " + Extra.getMaximumValue() + "."); }
		
		m_xOffset = xOffset;
		m_yOffset = yOffset;
		m_numberOfFrames = numberOfFrames;
		m_animationType = animationType;
		m_animationSpeed = animationSpeed;
		m_extra = extra;
		m_tileAttributeChangeListeners = new Vector<TileAttributeChangeListener>();
	}

	public byte getXOffset() {
		return m_xOffset;
	}

	public boolean setXOffset(byte xOffset) {
		if(!XOffset.isValidValue(xOffset)) {
			return false;
		}
		
		m_xOffset = xOffset;
		
		notifyTileAttributeChanged(XOffset, m_xOffset);
		
		return true;
	}
	
	public byte getYOffset() {
		return m_yOffset;
	}
	
	public boolean setYOffset(byte yOffset) {
		if(!YOffset.isValidValue(yOffset)) {
			return false;
		}
		
		m_yOffset = yOffset;

		notifyTileAttributeChanged(YOffset, m_yOffset);
		
		return true;
	}
	
	public byte getNumberOfFrames() {
		return m_numberOfFrames;
	}

	public boolean setNumberOfFrames(byte numberOfFrames) {
		if(!NumberOfFrames.isValidValue(numberOfFrames)) {
			return false;
		}
		
		m_numberOfFrames = numberOfFrames;

		notifyTileAttributeChanged(NumberOfFrames, m_numberOfFrames);
		
		return true;
	}

	public TileAnimationType getAnimationType() {
		return m_animationType;
	}

	public byte getAnimationTypeValue() {
		return (byte) m_animationType.ordinal();
	}

	public boolean setAnimationType(byte animationType) {
		if(animationType < 0 || animationType >= TileAnimationType.numberOfAnimationTypes()) {
			return false;
		}
		
		return setAnimationType(TileAnimationType.values()[animationType]);
	}

	public boolean setAnimationType(TileAnimationType animationType) {
		if(!animationType.isValid()) {
			return false;
		}
		
		m_animationType = animationType;

		notifyTileAttributeChanged(AnimationType, (byte) m_animationType.ordinal());
		
		return true;
	}
	
	public byte getAnimationSpeed() {
		return m_animationSpeed;
	}

	public boolean setAnimationSpeed(byte animationSpeed) {
		if(!AnimationSpeed.isValidValue(animationSpeed)) {
			return false;
		}
		
		m_animationSpeed = animationSpeed;

		notifyTileAttributeChanged(AnimationSpeed, m_animationSpeed);
		
		return true;
	}

	public byte getExtra() {
		return m_extra;
	}

	public boolean setExtra(byte extra) {
		if(!Extra.isValidValue(extra)) {
			return false;
		}
		
		m_extra = extra;

		notifyTileAttributeChanged(Extra, m_extra);
		
		return true;
	}
	
	public byte getAttributeValue(String name) throws IllegalArgumentException {
		return getAttributeValue(getTileAttribute(name));
	}
	
	public byte getAttributeValue(TileAttribute attribute) throws IllegalArgumentException {
		if(attribute == XOffset) {
			return m_xOffset;
		}
		else if(attribute == YOffset) {
			return m_yOffset;
		}
		else if(attribute == NumberOfFrames) {
			return m_numberOfFrames;
		}
		else if(attribute == AnimationType) {
			return (byte) m_animationType.ordinal();
		}
		else if(attribute == AnimationSpeed) {
			return m_animationSpeed;
		}
		else if(attribute == Extra) {
			return m_extra;
		}
		
		throw new IllegalArgumentException("Invalid tile attribute type.");
	}

	public boolean setAttributeValue(String name, byte value) throws IllegalArgumentException {
		return setAttributeValue(getTileAttribute(name), value);
	}
	
	public boolean setAttributeValue(TileAttribute attribute, byte value) throws IllegalArgumentException {
		if(attribute == XOffset) {
			return setXOffset(value);
		}
		else if(attribute == YOffset) {
			return setYOffset(value);
		}
		else if(attribute == NumberOfFrames) {
			return setNumberOfFrames(value);
		}
		else if(attribute == AnimationType) {
			return setAnimationType(value);
		}
		else if(attribute == AnimationSpeed) {
			return setAnimationSpeed(value);
		}
		else if(attribute == Extra) {
			return setExtra(value);
		}
		
		throw new IllegalArgumentException("Invalid tile attribute type.");
	}
	
	public void setAttributes(int packedValue) throws IllegalArgumentException {
		setAttributes(unpack(packedValue));
	}

	public void setAttributes(byte xOffset, byte yOffset, byte numberOfFrames, byte animationType, byte animationSpeed) throws IllegalArgumentException {
		setAttributes(new TileAttributes(xOffset, yOffset, numberOfFrames, animationType, animationSpeed));
	}

	public void setAttributes(byte xOffset, byte yOffset, byte numberOfFrames, TileAnimationType animationType, byte animationSpeed) throws IllegalArgumentException {
		setAttributes(new TileAttributes(xOffset, yOffset, numberOfFrames, animationType, animationSpeed));
	}

	public void setAttributes(byte xOffset, byte yOffset, byte numberOfFrames, byte animationType, byte animationSpeed, byte extra) throws IllegalArgumentException {
		setAttributes(new TileAttributes(xOffset, yOffset, numberOfFrames, animationType, animationSpeed));
	}

	public void setAttributes(byte xOffset, byte yOffset, byte numberOfFrames, TileAnimationType animationType, byte animationSpeed, byte extra) throws IllegalArgumentException {
		setAttributes(new TileAttributes(xOffset, yOffset, numberOfFrames, animationType, animationSpeed));
	}

	public void setAttributes(TileAttributes attributes) throws IllegalArgumentException {
		setAttributes(attributes, false);
	}
	
	public void setAttributes(TileAttributes attributes, boolean reassignTileChangeListeners) throws IllegalArgumentException {
		if(attributes == null) {
			throw new IllegalArgumentException("Tile attributes cannot be null!");
		}
		
		setXOffset(attributes.m_xOffset);
		setYOffset(attributes.m_yOffset);
		setNumberOfFrames(attributes.m_numberOfFrames);
		setAnimationType(attributes.m_animationType);
		setAnimationSpeed(attributes.m_animationSpeed);
		setExtra(attributes.m_extra);
	}

	public JSONObject getMetadata() {
		JSONObject metadata = new JSONObject();
		metadata.put(XOffset.getAttributeName(), m_xOffset);
		metadata.put(YOffset.getAttributeName(), m_yOffset);
		metadata.put(NumberOfFrames.getAttributeName(), m_numberOfFrames);
		metadata.put(AnimationType.getAttributeName(), m_animationType.getDisplayName());
		metadata.put(AnimationSpeed.getAttributeName(), m_animationSpeed);
		metadata.put(Extra.getAttributeName(), m_extra);
		return metadata;
	}
	
	public static TileAttributes parseFromMetadata(JSONObject metadata) throws IllegalArgumentException, JSONException {
		return new TileAttributes(
			(byte) metadata.getInt(XOffset.getAttributeName()),
			(byte) metadata.getInt(YOffset.getAttributeName()),
			(byte) metadata.getInt(NumberOfFrames.getAttributeName()),
			TileAnimationType.parseFrom(metadata.getString(AnimationType.getAttributeName())),
			(byte) metadata.getInt(AnimationSpeed.getAttributeName()),
			(byte) metadata.getInt(Extra.getAttributeName())
		);
	}

	public void applyMetadata(JSONObject tileAttributeMetadata) throws InvalidMetadataException {
		validateMetadata(tileAttributeMetadata);
		
		setXOffset((byte) tileAttributeMetadata.getInt(XOffset.getAttributeName()));
		setYOffset((byte) tileAttributeMetadata.getInt(YOffset.getAttributeName()));
		setNumberOfFrames((byte) tileAttributeMetadata.getInt(NumberOfFrames.getAttributeName()));
		setAnimationType(TileAnimationType.parseFrom(tileAttributeMetadata.getString(AnimationType.getAttributeName())));
		setAnimationSpeed((byte) tileAttributeMetadata.getInt(AnimationSpeed.getAttributeName()));
		setExtra((byte) tileAttributeMetadata.getInt(Extra.getAttributeName()));
	}
	
	public static boolean isValidMetadata(JSONObject tileAttributesMetadata) {
		try {
			return validateMetadata(tileAttributesMetadata, false);
		}
		catch(InvalidMetadataException exception) {
			return false;
		}
	}

	public static boolean validateMetadata(JSONObject tileAttributesMetadata) throws InvalidMetadataException {
		return validateMetadata(tileAttributesMetadata, true);
	}

	public static boolean validateMetadata(JSONObject tileAttributesMetadata, boolean throwErrors) throws InvalidMetadataException {
		try {
			if(tileAttributesMetadata == null) {
				throw new InvalidMetadataException("Missing tile attributes metadata.");
			}
			
			TileAttribute attribute = null;
			
			for(int i = 0; i < Attributes.length; i++) {
				attribute = Attributes[i];
				
				if(!tileAttributesMetadata.has(attribute.getAttributeName())) {
					throw new InvalidMetadataException("Missing tile attributes '" + attribute.getAttributeName() + "' metadata value.");
				}
				
				if(attribute == AnimationType) {
					try {
						String animationType = tileAttributesMetadata.getString(attribute.getAttributeName());
						
						if(!TileAnimationType.parseFrom(animationType).isValid()) {
							throw new InvalidMetadataException("Invalid tile attributes '" + attribute.getAttributeName() + "' metadata value: '" + animationType + "'.");
						}
					}
					catch(JSONException exception) {
						throw new InvalidMetadataException("Invalid tile attributes '" + attribute.getAttributeName() + "' metadata value type, expected string.");
					}
				}
				else {
					try {
						int value = tileAttributesMetadata.getInt(attribute.getAttributeName());
						
						if(value > Byte.MAX_VALUE || !attribute.isValidValue((byte) value)) {
							throw new InvalidMetadataException("Invalid tile attributes '" + attribute.getAttributeName() + "' metadata value: '" + value + "'.");
						}
					}
					catch(JSONException exception) {
						throw new InvalidMetadataException("Invalid tile attributes '" + attribute.getAttributeName() + "' metadata value type, expected integer.");
					}
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
	
	public int pack() {
		int packedValue = 0;
		
		TileAttribute attribute = null;
		
		for(int i = 0; i < Attributes.length; i++) {
			attribute = Attributes[i];
			
			packedValue += (getAttributeValue(attribute) & attribute.getBitMask()) << attribute.getBitOffset();
		}

		return packedValue;
	}
	
	public static TileAttributes unpack(int packedValue) {
		TileAttribute attribute = null;
		TileAttributes attributes = new TileAttributes();
		
		for(int i = 0; i < Attributes.length; i++) {
			attribute = Attributes[i];
			
			byte value = (byte) (((packedValue & attribute.getOffsetBitMask()) >> attribute.getBitOffset()) & attribute.getBitMask());
			
			if(attribute.isSigned()) {
				value = (byte) (((value & attribute.getSignBitMask()) == attribute.getSignBitMask()) ? -(attribute.getBitMask() - value + 1) : value);
			}
			
			if(!attributes.setAttributeValue(attribute, value)) {
				return null;
			}
		}

		return attributes;
	}

	public static boolean isValidTileAttribute(TileAttribute a) {
		if(a == null) {
			return false;
		}
		
		for(int i = 0; i < Attributes.length; i++) {
			if(a == Attributes[i]) {
				return true;
			}
		}
		
		return false;
	}
	
	public static TileAttribute getTileAttribute(String name) {
		String formattedName = name.trim();
		
		if(formattedName.isEmpty()) {
			return null;
		}
		
		for(int i = 0; i < Attributes.length; i++) {
			if(formattedName.equalsIgnoreCase(Attributes[i].getDisplayName()) ||
			   formattedName.equalsIgnoreCase(Attributes[i].getAttributeName())) {
				return Attributes[i];
			}
		}
		
		return null;
	}

	public TileAttributes clone() {
		return clone(false);
	}
	
	public TileAttributes clone(boolean reassignTileChangeListeners) {
		TileAttributes newTileAttributes = new TileAttributes(m_xOffset, m_yOffset, m_numberOfFrames, m_animationType, m_animationSpeed, m_extra);

		if(reassignTileChangeListeners) {
			newTileAttributes.m_tileAttributeChangeListeners = new Vector<TileAttributeChangeListener>(m_tileAttributeChangeListeners.size());
			
			for(int i = 0; i < m_tileAttributeChangeListeners.size(); i++) {
				newTileAttributes.m_tileAttributeChangeListeners.add(m_tileAttributeChangeListeners.elementAt(i));
			}
		}

		return newTileAttributes;
	}
	
	public void reset() {
		m_xOffset = 0;
		m_yOffset = 0;
		m_numberOfFrames = 0;
		m_animationType = TileAnimationType.defaultAnimationType;
		m_animationSpeed = 0;
		m_extra = 0;
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof TileAttributes)) {
			return false;
		}
		
		TileAttributes a = (TileAttributes) o;
		
		return m_xOffset == a.m_xOffset &&
			   m_yOffset == a.m_yOffset &&
			   m_numberOfFrames == a.m_numberOfFrames &&
			   m_animationType == a.m_animationType &&
			   m_animationSpeed == a.m_animationSpeed;
	}
	
	public String toString() {
		TileAttribute attribute = null;
		String tileAttributeString = "Tile Attributes (";

		for(int i = 0; i < Attributes.length; i++) {
			attribute = Attributes[i];

			if(i != 0) {
				tileAttributeString += ", ";
			}

			tileAttributeString += attribute.getDisplayName() + ": ";

			if(attribute == TileAttributes.AnimationType) {
				tileAttributeString += m_animationType.getDisplayName();
			}
			else {
				tileAttributeString += getAttributeValue(attribute);
			}
		}

		tileAttributeString += ")";

		return tileAttributeString;
	}

	public int numberOfTileAttributeChangeListeners() {
		return m_tileAttributeChangeListeners.size();
	}
	
	public TileAttributeChangeListener getTileAttributeChangeListener(int index) {
		if(index < 0 || index >= m_tileAttributeChangeListeners.size()) { return null; }
		
		return m_tileAttributeChangeListeners.elementAt(index);
	}
	
	public boolean hasTileAttributeChangeListener(TileAttributeChangeListener c) {
		return m_tileAttributeChangeListeners.contains(c);
	}
	
	public int indexOfTileAttributeChangeListener(TileAttributeChangeListener c) {
		return m_tileAttributeChangeListeners.indexOf(c);
	}
	
	public boolean addTileAttributeChangeListener(TileAttributeChangeListener c) {
		if(c == null || m_tileAttributeChangeListeners.contains(c)) { return false; }
		
		m_tileAttributeChangeListeners.add(c);
		
		return true;
	}
	
	public boolean removeTileAttributeChangeListener(int index) {
		if(index < 0 || index >= m_tileAttributeChangeListeners.size()) { return false; }
		
		m_tileAttributeChangeListeners.remove(index);
		
		return true;
	}
	
	public boolean removeTileAttributeChangeListener(TileAttributeChangeListener c) {
		if(c == null) { return false; }
		
		return m_tileAttributeChangeListeners.remove(c);
	}
	
	public void clearTileAttributeChangeListeners() {
		m_tileAttributeChangeListeners.clear();
	}
	
	public void notifyTileAttributeChanged(TileAttribute attribute, byte value) {
		for(int i=0;i<m_tileAttributeChangeListeners.size();i++) {
			m_tileAttributeChangeListeners.elementAt(i).handleTileAttributeChange(this, attribute, value);
		}
	}

}
