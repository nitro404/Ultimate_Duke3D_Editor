package map;

import java.util.*;
import item.*;
import exception.*;
import org.json.*;

public class SpriteAttributes extends ItemAttributes {

	protected boolean m_blockClipping;
	protected boolean m_translucent;
	protected boolean m_xFlipped;
	protected boolean m_yFlipped;
	protected SpriteDrawType m_drawType;
	protected boolean m_oneSided;
	protected boolean m_centered;
	protected boolean m_blockHitscan;
	protected boolean m_reverseTranslucent;
	protected byte m_reserved; // note: unused
	protected boolean m_invisible;

	final public static ItemAttribute BlockClipping = new ItemAttribute("Block Clipping", "blockClipping", (byte) 1, false, (byte) 0);
	final public static ItemAttribute Translucent = new ItemAttribute("Translucent", "translucent", (byte) 1, false, (byte) 1);
	final public static ItemAttribute XFlipped = new ItemAttribute("X Flipped", "xFlipped", (byte) 1, false, (byte) 2);
	final public static ItemAttribute YFlipped = new ItemAttribute("Y Flipped", "yFlipped", (byte) 1, false, (byte) 3);
	final public static ItemAttribute DrawType = new ItemAttribute("Draw Type", "drawType", (byte) 2, false, (byte) 4);
	final public static ItemAttribute OneSided = new ItemAttribute("One Sided", "oneSided", (byte) 1, false, (byte) 6);
	final public static ItemAttribute Centered = new ItemAttribute("Centered", "centered", (byte) 1, false, (byte) 7);
	final public static ItemAttribute BlockHitscan = new ItemAttribute("Block Hitscan", "blockHitscan", (byte) 1, false, (byte) 8);
	final public static ItemAttribute ReverseTranslucent = new ItemAttribute("Reverse Translucent", "reverseTranslucent", (byte) 1, false, (byte) 9);
	final public static ItemAttribute Reserved = new ItemAttribute("Reserved", "reserved", (byte) 5, false, (byte) 10);
	final public static ItemAttribute Invisible = new ItemAttribute("Invisible", "invisible", (byte) 1, false, (byte) 15);

	final public static ItemAttribute Attributes[] = {
		BlockClipping,
		Translucent,
		XFlipped,
		YFlipped,
		DrawType,
		OneSided,
		Centered,
		BlockHitscan,
		ReverseTranslucent,
		Reserved,
		Invisible
	};

	final public static int SIZE = 2;

	public SpriteAttributes() {
		this(false, false, false, false, SpriteDrawType.defaultDrawType, false, false, false, false, (byte) 0, false);
	}

	public SpriteAttributes(boolean blockClipping, boolean translucent, boolean xFlipped, boolean yFlipped, byte drawType, boolean oneSided, boolean centered, boolean blockHitscan, boolean reverseTranslucent, boolean invisible) throws IllegalArgumentException {
		this(blockClipping, translucent, xFlipped, yFlipped, drawType, oneSided, centered, blockHitscan, reverseTranslucent, (byte) 0, invisible);
	}

	public SpriteAttributes(boolean blockClipping, boolean translucent, boolean xFlipped, boolean yFlipped, SpriteDrawType drawType, boolean oneSided, boolean centered, boolean blockHitscan, boolean reverseTranslucent, boolean invisible) throws IllegalArgumentException {
		this(blockClipping, translucent, xFlipped, yFlipped, drawType, oneSided, centered, blockHitscan, reverseTranslucent, (byte) 0, invisible);
	}

	public SpriteAttributes(boolean blockClipping, boolean translucent, boolean xFlipped, boolean yFlipped, byte drawType, boolean oneSided, boolean centered, boolean blockHitscan, boolean reverseTranslucent, byte reserved, boolean invisible) throws IllegalArgumentException {
		this(blockClipping, translucent, xFlipped, yFlipped, DrawType.isValidValue(drawType) ? SpriteDrawType.values()[drawType] : SpriteDrawType.Invalid, oneSided, centered, blockHitscan, reverseTranslucent, reserved, invisible);
	}

	public SpriteAttributes(boolean blockClipping, boolean translucent, boolean xFlipped, boolean yFlipped, SpriteDrawType drawType, boolean oneSided, boolean centered, boolean blockHitscan, boolean reverseTranslucent, byte reserved, boolean invisible) throws IllegalArgumentException {
		if(!Reserved.isValidValue(reserved)) { throw new IllegalArgumentException("Invalid reserved sprite attribute value: " + reserved + ", expected value between " + Reserved.getMinimumValue() + " and " + Reserved.getMaximumValue() + "."); }
		if(!SpriteDrawType.isValid(drawType)) { throw new IllegalArgumentException("Invalid sprite draw type value."); }

		m_blockClipping = blockClipping;
		m_translucent = translucent;
		m_xFlipped = xFlipped;
		m_yFlipped = yFlipped;
		m_drawType = drawType;
		m_oneSided = oneSided;
		m_centered = centered;
		m_blockHitscan = blockHitscan;
		m_reverseTranslucent = reverseTranslucent;
		m_reserved = reserved;
		m_invisible = invisible;
	}

	public byte numberOfBytes() {
		return 2;
	}

	public ItemAttribute[] getAttributes() {
		return Attributes;
	}

	public boolean getBlockClipping() {
		return m_blockClipping;
	}

	public boolean setBlockClipping(byte blockClipping) {
		if(!BlockClipping.isValidValue(blockClipping)) {
			return false;
		}
		
		return setBlockClipping(blockClipping != 0);
	}

	public boolean setBlockClipping(boolean blockClipping) {
		m_blockClipping = blockClipping;
		
		notifyItemAttributeChanged(BlockClipping, m_blockClipping ? (byte) 1 : (byte) 0);
		
		return true;
	}

	public boolean getTranslucent() {
		return m_translucent;
	}

	public boolean setTranslucent(byte translucent) {
		if(!Translucent.isValidValue(translucent)) {
			return false;
		}

		return setTranslucent(translucent != 0);
	}

	public boolean setTranslucent(boolean translucent) {
		m_translucent = translucent;

		notifyItemAttributeChanged(Translucent, m_translucent ? (byte) 1 : (byte) 0);

		return true;
	}

	public boolean getXFlipped() {
		return m_xFlipped;
	}

	public boolean setXFlipped(byte xFlipped) {
		if(!XFlipped.isValidValue(xFlipped)) {
			return false;
		}

		return setXFlipped(xFlipped != 0);
	}

	public boolean setXFlipped(boolean xFlipped) {
		m_xFlipped = xFlipped;

		notifyItemAttributeChanged(XFlipped, m_xFlipped ? (byte) 1 : (byte) 0);

		return true;
	}

	public boolean getYFlipped() {
		return m_yFlipped;
	}

	public boolean setYFlipped(byte yFlipped) {
		if(!YFlipped.isValidValue(yFlipped)) {
			return false;
		}

		return setYFlipped(yFlipped != 0);
	}

	public boolean setYFlipped(boolean yFlipped) {
		m_yFlipped = yFlipped;

		notifyItemAttributeChanged(YFlipped, m_yFlipped ? (byte) 1 : (byte) 0);

		return true;
	}

	public SpriteDrawType getDrawType() {
		return m_drawType;
	}

	public byte getDrawTypeValue() {
		return (byte) m_drawType.ordinal();
	}

	public boolean setDrawType(byte drawType) {
		if(drawType < 0 || drawType >= SpriteDrawType.numberOfDrawTypes()) {
			return false;
		}
		
		return setDrawType(SpriteDrawType.values()[drawType]);
	}

	public boolean setDrawType(String drawType) {
		return setDrawType(SpriteDrawType.parseFrom(drawType));
	}

	public boolean setDrawType(SpriteDrawType drawType) {
		if(!drawType.isValid()) {
			return false;
		}
		
		m_drawType = drawType;

		notifyItemAttributeChanged(DrawType, (byte) m_drawType.ordinal());
		
		return true;
	}

	public boolean getOneSided() {
		return m_oneSided;
	}

	public boolean setOneSided(byte oneSided) {
		if(!OneSided.isValidValue(oneSided)) {
			return false;
		}

		return setOneSided(oneSided != 0);
	}

	public boolean setOneSided(boolean oneSided) {
		m_oneSided = oneSided;

		notifyItemAttributeChanged(OneSided, m_oneSided ? (byte) 1 : (byte) 0);

		return true;
	}

	public boolean getCentered() {
		return m_centered;
	}

	public boolean setCentered(byte centered) {
		if(!Centered.isValidValue(centered)) {
			return false;
		}

		return setCentered(centered != 0);
	}

	public boolean setCentered(boolean centered) {
		m_centered = centered;

		notifyItemAttributeChanged(Centered, m_centered ? (byte) 1 : (byte) 0);

		return true;
	}

	public boolean getBlockHitscan() {
		return m_blockHitscan;
	}

	public boolean setBlockHitscan(byte blockHitscan) {
		if(!BlockHitscan.isValidValue(blockHitscan)) {
			return false;
		}

		return setBlockHitscan(blockHitscan != 0);
	}

	public boolean setBlockHitscan(boolean blockHitscan) {
		m_blockHitscan = blockHitscan;

		notifyItemAttributeChanged(BlockHitscan, m_blockHitscan ? (byte) 1 : (byte) 0);

		return true;
	}

	public boolean getReverseTranslucent() {
		return m_reverseTranslucent;
	}

	public boolean setReverseTranslucent(byte reverseTranslucent) {
		if(!ReverseTranslucent.isValidValue(reverseTranslucent)) {
			return false;
		}

		return setReverseTranslucent(reverseTranslucent != 0);
	}

	public boolean setReverseTranslucent(boolean reverseTranslucent) {
		m_reverseTranslucent = reverseTranslucent;

		notifyItemAttributeChanged(ReverseTranslucent, m_reverseTranslucent ? (byte) 1 : (byte) 0);

		return true;
	}

	public byte getReserved() {
		return m_reserved;
	}

	public boolean setReserved(byte reserved) {
		if(!Reserved.isValidValue(reserved)) {
			return false;
		}
		
		m_reserved = reserved;

		notifyItemAttributeChanged(Reserved, m_reserved);
		
		return true;
	}

	public boolean getInvisible() {
		return m_invisible;
	}

	public boolean setInvisible(byte invisible) {
		if(!Invisible.isValidValue(invisible)) {
			return false;
		}

		return setInvisible(invisible != 0);
	}

	public boolean setInvisible(boolean invisible) {
		m_invisible = invisible;

		notifyItemAttributeChanged(Invisible, m_invisible ? (byte) 1 : (byte) 0);

		return true;
	}

	public byte getAttributeValue(ItemAttribute attribute) throws IllegalArgumentException {
		if(attribute == BlockClipping) {
			return m_blockClipping ? (byte) 1 : (byte) 0;
		}
		else if(attribute == Translucent) {
			return m_translucent ? (byte) 1 : (byte) 0;
		}
		else if(attribute == XFlipped) {
			return m_xFlipped ? (byte) 1 : (byte) 0;
		}
		else if(attribute == YFlipped) {
			return m_yFlipped ? (byte) 1 : (byte) 0;
		}
		else if(attribute == DrawType) {
			return (byte) m_drawType.ordinal();
		}
		else if(attribute == OneSided) {
			return m_oneSided ? (byte) 1 : (byte) 0;
		}
		else if(attribute == Centered) {
			return m_centered ? (byte) 1 : (byte) 0;
		}
		else if(attribute == BlockHitscan) {
			return m_blockHitscan ? (byte) 1 : (byte) 0;
		}
		else if(attribute == ReverseTranslucent) {
			return m_reverseTranslucent ? (byte) 1 : (byte) 0;
		}
		else if(attribute == Reserved) {
			return m_reserved;
		}
		else if(attribute == Invisible) {
			return m_invisible ? (byte) 1 : (byte) 0;
		}
		
		throw new IllegalArgumentException("Invalid item attribute type.");
	}

	public boolean setAttributeValue(ItemAttribute attribute, byte value) throws IllegalArgumentException {
		if(attribute == BlockClipping) {
			return setBlockClipping(value);
		}
		else if(attribute == Translucent) {
			return setTranslucent(value);
		}
		else if(attribute == XFlipped) {
			return setXFlipped(value);
		}
		else if(attribute == YFlipped) {
			return setYFlipped(value);
		}
		else if(attribute == DrawType) {
			return setDrawType(value);
		}
		else if(attribute == OneSided) {
			return setOneSided(value);
		}
		else if(attribute == Centered) {
			return setCentered(value);
		}
		else if(attribute == BlockHitscan) {
			return setBlockHitscan(value);
		}
		else if(attribute == ReverseTranslucent) {
			return setReverseTranslucent(value);
		}
		else if(attribute == Reserved) {
			return setReserved(value);
		}
		else if(attribute == Invisible) {
			return setInvisible(value);
		}

		throw new IllegalArgumentException("Invalid item attribute type.");
	}

	public void setAttributes(Number packedValue) throws IllegalArgumentException {
		if(packedValue == null) {
			throw new IllegalArgumentException("Packed attributes value cannot be null!");
		}

		setAttributes(packedValue.shortValue());
	}

	public void setAttributes(short packedValue) throws IllegalArgumentException {
		setAttributes(unpack(packedValue));
	}

	public void setAttributes(boolean blockClipping, boolean translucent, boolean xFlipped, boolean yFlipped, byte drawType, boolean oneSided, boolean centered, boolean blockHitscan, boolean reverseTranslucent, boolean invisible) throws IllegalArgumentException {
		setAttributes(new SpriteAttributes(blockClipping, translucent, xFlipped, yFlipped, drawType, oneSided, centered, blockHitscan, reverseTranslucent, invisible));
	}

	public void setAttributes(boolean blockClipping, boolean translucent, boolean xFlipped, boolean yFlipped, byte drawType, boolean oneSided, boolean centered, boolean blockHitscan, boolean reverseTranslucent, byte reserved, boolean invisible) throws IllegalArgumentException {
		setAttributes(new SpriteAttributes(blockClipping, translucent, xFlipped, yFlipped, drawType, oneSided, centered, blockHitscan, reverseTranslucent, reserved, invisible));
	}

	public void setAttributes(boolean blockClipping, boolean translucent, boolean xFlipped, boolean yFlipped, SpriteDrawType drawType, boolean oneSided, boolean centered, boolean blockHitscan, boolean reverseTranslucent, boolean invisible) throws IllegalArgumentException {
		setAttributes(new SpriteAttributes(blockClipping, translucent, xFlipped, yFlipped, drawType, oneSided, centered, blockHitscan, reverseTranslucent, invisible));
	}

	public void setAttributes(boolean blockClipping, boolean translucent, boolean xFlipped, boolean yFlipped, SpriteDrawType drawType, boolean oneSided, boolean centered, boolean blockHitscan, boolean reverseTranslucent, byte reserved, boolean invisible) throws IllegalArgumentException {
		setAttributes(new SpriteAttributes(blockClipping, translucent, xFlipped, yFlipped, drawType, oneSided, centered, blockHitscan, reverseTranslucent, reserved, invisible));
	}

	public void setAttributes(ItemAttributes attributes, boolean reassignItemChangeListeners) throws IllegalArgumentException {
		if(attributes == null) {
			throw new IllegalArgumentException("Sprite attributes cannot be null!");
		}

		if(!(attributes instanceof SpriteAttributes)) {
			throw new IllegalArgumentException("Invalid sprite attributes instance!");
		}

		SpriteAttributes a = (SpriteAttributes) attributes;

		setBlockClipping(a.m_blockClipping);
		setTranslucent(a.m_translucent);
		setXFlipped(a.m_xFlipped);
		setYFlipped(a.m_yFlipped);
		setDrawType(a.m_drawType);
		setOneSided(a.m_oneSided);
		setCentered(a.m_centered);
		setBlockHitscan(a.m_blockHitscan);
		setReverseTranslucent(a.m_reverseTranslucent);
		setReserved(a.m_reserved);
		setInvisible(a.m_invisible);
	}

	public Number pack() {
		short packedValue = 0;
		
		ItemAttribute attribute = null;
		
		for(int i = 0; i < Attributes.length; i++) {
			attribute = Attributes[i];
			
			packedValue += (getAttributeValue(attribute) & attribute.getBitMask()) << attribute.getBitOffset();
		}

		return packedValue;
	}
	
	public static SpriteAttributes unpack(short packedValue) {
		ItemAttribute attribute = null;
		SpriteAttributes attributes = new SpriteAttributes();
		
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

	public JSONObject toJSONObject() {
		JSONObject spriteAttributes = super.toJSONObject();
		spriteAttributes.put(DrawType.getAttributeName(), m_drawType.getDisplayName());

		return spriteAttributes;
	}

	public static SpriteAttributes fromJSONObject(JSONObject spriteAttributes) throws IllegalArgumentException, JSONException, MalformedItemAttributeException {
		if(spriteAttributes == null) {
			throw new IllegalArgumentException("Sprite attributes JSON data cannot be null.");
		}

		SpriteAttributes newAttributes = new SpriteAttributes();
		ItemAttribute attribute = null;

		for(int i = 0; i < Attributes.length; i++) {
			attribute = Attributes[i];

			if(attribute == DrawType) {
				if(!newAttributes.setDrawType(spriteAttributes.getString(DrawType.getAttributeName()))) {
					throw new MalformedItemAttributeException("Invalid " + DrawType.getDisplayName() + " attribute value.");
				}
			}
			else if(attribute == Reserved) {
				if(!spriteAttributes.has(Reserved.getAttributeName())) {
					continue;
				}

				if(!newAttributes.setReserved((byte) spriteAttributes.getInt(Reserved.getAttributeName()))) {
					throw new MalformedItemAttributeException("Invalid " + Reserved.getDisplayName() + " attribute value.");
				}
			}
			else {
				if(!newAttributes.setAttributeValue(attribute, spriteAttributes.getBoolean(attribute.getAttributeName()) ? (byte) 1 : (byte) 0)) {
					throw new MalformedItemAttributeException("Invalid " + attribute.getDisplayName() + " attribute value.");
				}
			}
		}

		return newAttributes;
	}

	public ItemAttributes clone(boolean reassignItemChangeListeners) {
		SpriteAttributes newSpriteAttributes = new SpriteAttributes(m_blockClipping, m_translucent, m_xFlipped, m_yFlipped, m_drawType, m_oneSided, m_centered, m_blockHitscan, m_reverseTranslucent, m_reserved, m_invisible);

		if(reassignItemChangeListeners) {
			newSpriteAttributes.m_itemAttributeChangeListeners = new Vector<ItemAttributeChangeListener>(m_itemAttributeChangeListeners.size());
			
			for(int i = 0; i < m_itemAttributeChangeListeners.size(); i++) {
				newSpriteAttributes.m_itemAttributeChangeListeners.add(m_itemAttributeChangeListeners.elementAt(i));
			}
		}

		return newSpriteAttributes;
	}
	
	public void reset() {
		m_blockClipping = false;
		m_translucent = false;;
		m_xFlipped = false;
		m_yFlipped = false;
		m_drawType = SpriteDrawType.defaultDrawType;
		m_oneSided = false;
		m_centered = false;
		m_blockHitscan = false;
		m_reverseTranslucent = false;
		m_reserved = (byte) 0;
		m_invisible = false;
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof SpriteAttributes)) {
			return false;
		}
		
		SpriteAttributes a = (SpriteAttributes) o;
		
		return m_blockClipping == a.m_blockClipping &&
			   m_translucent == a.m_translucent &&
			   m_xFlipped == a.m_xFlipped &&
			   m_yFlipped == a.m_yFlipped &&
			   m_drawType == a.m_drawType &&
			   m_oneSided == a.m_oneSided &&
			   m_centered == a.m_centered &&
			   m_blockHitscan == a.m_blockHitscan &&
			   m_reverseTranslucent == a.m_reverseTranslucent &&
			   m_reserved == a.m_reserved &&
			   m_invisible == a.m_invisible;
	}
	
}
