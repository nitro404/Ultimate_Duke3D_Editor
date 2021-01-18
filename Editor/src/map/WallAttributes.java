package map;

import java.util.*;
import item.*;
import exception.*;
import org.json.*;

public class WallAttributes extends ItemAttributes {

	protected boolean m_blockClipping;
	protected boolean m_invisibleWallBottomSwapped;
	protected boolean m_textureAlignBottom;
	protected boolean m_xFlipped;
	protected boolean m_masked;
	protected boolean m_oneWay;
	protected boolean m_blockHitscan;
	protected boolean m_translucent;
	protected boolean m_yFlipped;
	protected boolean m_reverseTranslucent;
	protected byte m_reserved; // note: unused

	final public static ItemAttribute BlockClipping = new ItemAttribute("Block Clipping", "blockClipping", (byte) 1, false, (byte) 0);
	final public static ItemAttribute InvisibleWallBottomSwapped = new ItemAttribute("Invisible Wall Bottom Swapped", "invisibleWallBottomSwapped", (byte) 1, false, (byte) 1);
	final public static ItemAttribute TextureAlignBottom = new ItemAttribute("Texture Align Bottom", "textureAlignBottom", (byte) 1, false, (byte) 2);
	final public static ItemAttribute XFlipped = new ItemAttribute("X Flipped", "xFlipped", (byte) 1, false, (byte) 3);
	final public static ItemAttribute Masked = new ItemAttribute("Masked", "masked", (byte) 1, false, (byte) 4);
	final public static ItemAttribute OneWay = new ItemAttribute("One Way", "oneWay", (byte) 1, false, (byte) 5);
	final public static ItemAttribute BlockHitscan = new ItemAttribute("Block Hitscan", "blockHitscan", (byte) 1, false, (byte) 6);
	final public static ItemAttribute Translucent = new ItemAttribute("translucent", "translucent", (byte) 1, false, (byte) 7);
	final public static ItemAttribute YFlipped = new ItemAttribute("Y Flipped", "yFlipped", (byte) 1, false, (byte) 8);
	final public static ItemAttribute ReverseTranslucent = new ItemAttribute("Reverse Translucent", "reverseTranslucent", (byte) 1, false, (byte) 9);
	final public static ItemAttribute Reserved = new ItemAttribute("Reserved", "reserved", (byte) 6, false, (byte) 10);

	final public static ItemAttribute Attributes[] = {
		BlockClipping,
		InvisibleWallBottomSwapped,
		TextureAlignBottom,
		XFlipped,
		Masked,
		OneWay,
		BlockHitscan,
		Translucent,
		YFlipped,
		ReverseTranslucent,
		Reserved
	};
	
	public WallAttributes() {
		this(false, false, false, false, false, false, false, false, false, false, (byte) 0);
	}

	public WallAttributes(boolean blockClipping, boolean invisibleWallBottomSwapped, boolean textureAlignBottom, boolean xFlipped, boolean masked, boolean oneWay, boolean blockHitscan, boolean translucent, boolean yFlipped, boolean reverseTranslucent) throws IllegalArgumentException {
		this(blockClipping, invisibleWallBottomSwapped, textureAlignBottom, xFlipped, masked, oneWay, blockHitscan, translucent, yFlipped, reverseTranslucent, (byte) 0);
	}

	public WallAttributes(boolean blockClipping, boolean invisibleWallBottomSwapped, boolean textureAlignBottom, boolean xFlipped, boolean masked, boolean oneWay, boolean blockHitscan, boolean translucent, boolean yFlipped, boolean reverseTranslucent, byte reserved) throws IllegalArgumentException {
		if(!Reserved.isValidValue(reserved)) { throw new IllegalArgumentException("Invalid reserved wall attribute value: " + reserved + ", expected value between " + Reserved.getMinimumValue() + " and " + Reserved.getMaximumValue() + "."); }

		m_blockClipping = blockClipping;
		m_invisibleWallBottomSwapped = invisibleWallBottomSwapped;
		m_textureAlignBottom = textureAlignBottom;
		m_xFlipped = xFlipped;
		m_masked = masked;
		m_oneWay = oneWay;
		m_blockHitscan = blockHitscan;
		m_translucent = translucent;
		m_yFlipped = yFlipped;
		m_reverseTranslucent = reverseTranslucent;;
		m_reserved = reserved;
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

	public boolean getInvisibleWallBottomSwapped() {
		return m_invisibleWallBottomSwapped;
	}

	public boolean setInvisibleWallBottomSwapped(byte invisibleWallBottomSwapped) {
		if(!InvisibleWallBottomSwapped.isValidValue(invisibleWallBottomSwapped)) {
			return false;
		}
		
		return setInvisibleWallBottomSwapped(invisibleWallBottomSwapped != 0);
	}

	public boolean setInvisibleWallBottomSwapped(boolean invisibleWallBottomSwapped) {
		m_invisibleWallBottomSwapped = invisibleWallBottomSwapped;
		
		notifyItemAttributeChanged(InvisibleWallBottomSwapped, m_invisibleWallBottomSwapped ? (byte) 1 : (byte) 0);
		
		return true;
	}

	public boolean getTextureAlignBottom() {
		return m_textureAlignBottom;
	}

	public boolean setTextureAlignBottom(byte textureAlignBottom) {
		if(!TextureAlignBottom.isValidValue(textureAlignBottom)) {
			return false;
		}
		
		return setTextureAlignBottom(textureAlignBottom != 0);
	}

	public boolean setTextureAlignBottom(boolean textureAlignBottom) {
		m_textureAlignBottom = textureAlignBottom;
		
		notifyItemAttributeChanged(TextureAlignBottom, m_textureAlignBottom ? (byte) 1 : (byte) 0);
		
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

	public boolean getMasked() {
		return m_masked;
	}

	public boolean setMasked(byte masked) {
		if(!Masked.isValidValue(masked)) {
			return false;
		}

		return setMasked(masked != 0);
	}

	public boolean setMasked(boolean masked) {
		m_masked = masked;

		notifyItemAttributeChanged(Masked, m_masked ? (byte) 1 : (byte) 0);

		return true;
	}

	public boolean getOneWay() {
		return m_oneWay;
	}

	public boolean setOneWay(byte oneWay) {
		if(!OneWay.isValidValue(oneWay)) {
			return false;
		}

		return setOneWay(oneWay != 0);
	}

	public boolean setOneWay(boolean oneWay) {
		m_oneWay = oneWay;

		notifyItemAttributeChanged(OneWay, m_oneWay ? (byte) 1 : (byte) 0);

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

	public byte getAttributeValue(ItemAttribute attribute) throws IllegalArgumentException {
		if(attribute == BlockClipping) {
			return m_blockClipping ? (byte) 1 : (byte) 0;
		}
		else if(attribute == InvisibleWallBottomSwapped) {
			return m_invisibleWallBottomSwapped ? (byte) 1 : (byte) 0;
		}
		else if(attribute == TextureAlignBottom) {
			return m_textureAlignBottom ? (byte) 1 : (byte) 0;
		}
		else if(attribute == XFlipped) {
			return m_xFlipped ? (byte) 1 : (byte) 0;
		}
		else if(attribute == Masked) {
			return m_masked ? (byte) 1 : (byte) 0;
		}
		else if(attribute == OneWay) {
			return m_oneWay ? (byte) 1 : (byte) 0;
		}
		else if(attribute == BlockHitscan) {
			return m_blockHitscan ? (byte) 1 : (byte) 0;
		}
		else if(attribute == Translucent) {
			return m_translucent ? (byte) 1 : (byte) 0;
		}
		else if(attribute == YFlipped) {
			return m_yFlipped ? (byte) 1 : (byte) 0;
		}
		else if(attribute == ReverseTranslucent) {
			return m_reverseTranslucent ? (byte) 1 : (byte) 0;
		}
		else if(attribute == Reserved) {
			return m_reserved;
		}
		
		throw new IllegalArgumentException("Invalid item attribute type.");
	}

	public boolean setAttributeValue(ItemAttribute attribute, byte value) throws IllegalArgumentException {
		if(attribute == BlockClipping) {
			return setBlockClipping(value);
		}
		else if(attribute == InvisibleWallBottomSwapped) {
			return setInvisibleWallBottomSwapped(value);
		}
		else if(attribute == TextureAlignBottom) {
			return setTextureAlignBottom(value);
		}
		else if(attribute == XFlipped) {
			return setXFlipped(value);
		}
		else if(attribute == Masked) {
			return setMasked(value);
		}
		else if(attribute == OneWay) {
			return setOneWay(value);
		}
		else if(attribute == BlockHitscan) {
			return setBlockHitscan(value);
		}
		else if(attribute == Translucent) {
			return setTranslucent(value);
		}
		else if(attribute == YFlipped) {
			return setYFlipped(value);
		}
		else if(attribute == ReverseTranslucent) {
			return setReverseTranslucent(value);
		}
		else if(attribute == Reserved) {
			return setReserved(value);
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

	public void setAttributes(boolean blockClipping, boolean invisibleWallBottomSwapped, boolean textureAlignBottom, boolean xFlipped, boolean masked, boolean oneWay, boolean blockHitscan, boolean translucent, boolean yFlipped, boolean reverseTranslucent) throws IllegalArgumentException {
		setAttributes(new WallAttributes(blockClipping, invisibleWallBottomSwapped, textureAlignBottom, xFlipped, masked, oneWay, blockHitscan, translucent, yFlipped, reverseTranslucent));
	}

	public void setAttributes(boolean blockClipping, boolean invisibleWallBottomSwapped, boolean textureAlignBottom, boolean xFlipped, boolean masked, boolean oneWay, boolean blockHitscan, boolean translucent, boolean yFlipped, boolean reverseTranslucent, byte reserved) throws IllegalArgumentException {
		setAttributes(new WallAttributes(blockClipping, invisibleWallBottomSwapped, textureAlignBottom, xFlipped, masked, oneWay, blockHitscan, translucent, yFlipped, reverseTranslucent, reserved));
	}

	public void setAttributes(ItemAttributes attributes, boolean reassignItemChangeListeners) throws IllegalArgumentException {
		if(attributes == null) {
			throw new IllegalArgumentException("Wall attributes cannot be null!");
		}

		if(!(attributes instanceof WallAttributes)) {
			throw new IllegalArgumentException("Invalid wall attributes instance!");
		}

		WallAttributes a = (WallAttributes) attributes;
		
		setBlockClipping(a.m_blockClipping);
		setInvisibleWallBottomSwapped(a.m_invisibleWallBottomSwapped);
		setTextureAlignBottom(a.m_textureAlignBottom);
		setXFlipped(a.m_xFlipped);
		setMasked(a.m_masked);
		setOneWay(a.m_oneWay);
		setBlockHitscan(a.m_blockHitscan);
		setTranslucent(a.m_translucent);
		setYFlipped(a.m_yFlipped);
		setReverseTranslucent(a.m_reverseTranslucent);
		setReserved(a.m_reserved);
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
	
	public static WallAttributes unpack(short packedValue) {
		ItemAttribute attribute = null;
		WallAttributes attributes = new WallAttributes();
		
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

	public static WallAttributes fromJSONObject(JSONObject wallAttributes) throws IllegalArgumentException, JSONException, MalformedItemAttributeException {
		if(wallAttributes == null) {
			throw new IllegalArgumentException("Wall attributes JSON data cannot be null.");
		}

		WallAttributes newAttributes = new WallAttributes();
		ItemAttribute attribute = null;

		for(int i = 0; i < Attributes.length; i++) {
			attribute = Attributes[i];

			if(attribute == Reserved) {
				if(!wallAttributes.has(Reserved.getAttributeName())) {
					continue;
				}

				if(!newAttributes.setReserved((byte) wallAttributes.getInt(Reserved.getAttributeName()))) {
					throw new MalformedItemAttributeException("Invalid " + Reserved.getDisplayName() + " attribute value.");
				}
			}
			else {
				if(!newAttributes.setAttributeValue(attribute, wallAttributes.getBoolean(attribute.getAttributeName()) ? (byte) 1 : (byte) 0)) {
					throw new MalformedItemAttributeException("Invalid " + attribute.getDisplayName() + " attribute value.");
				}
			}
		}

		return newAttributes;
	}

	public ItemAttributes clone(boolean reassignItemChangeListeners) {
		WallAttributes newWallAttributes = new WallAttributes(m_blockClipping, m_invisibleWallBottomSwapped, m_textureAlignBottom, m_xFlipped, m_masked, m_oneWay, m_blockHitscan, m_translucent, m_yFlipped, m_reverseTranslucent, m_reserved);

		if(reassignItemChangeListeners) {
			newWallAttributes.m_itemAttributeChangeListeners = new Vector<ItemAttributeChangeListener>(m_itemAttributeChangeListeners.size());
			
			for(int i = 0; i < m_itemAttributeChangeListeners.size(); i++) {
				newWallAttributes.m_itemAttributeChangeListeners.add(m_itemAttributeChangeListeners.elementAt(i));
			}
		}

		return newWallAttributes;
	}
	
	public void reset() {
		m_blockClipping = false;
		m_invisibleWallBottomSwapped = false;
		m_textureAlignBottom = false;
		m_xFlipped = false;
		m_masked = false;
		m_oneWay = false;
		m_blockHitscan = false;
		m_translucent = false;
		m_yFlipped = false;
		m_reverseTranslucent = false;
		m_reserved = (byte) 0;
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof WallAttributes)) {
			return false;
		}
		
		WallAttributes a = (WallAttributes) o;

		return m_blockClipping == a.m_blockClipping &&
			   m_translucent == a.m_translucent &&
			   m_invisibleWallBottomSwapped == a.m_invisibleWallBottomSwapped &&
			   m_textureAlignBottom == a.m_textureAlignBottom &&
			   m_xFlipped == a.m_xFlipped &&
			   m_masked == a.m_masked &&
			   m_oneWay == a.m_oneWay &&
			   m_blockHitscan == a.m_blockHitscan &&
			   m_translucent == a.m_translucent &&
			   m_yFlipped == a.m_yFlipped &&
			   m_reverseTranslucent == a.m_reverseTranslucent &&
			   m_reserved == a.m_reserved;
	}
	
}
