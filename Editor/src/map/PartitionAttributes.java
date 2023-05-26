package map;

import java.util.*;
import item.*;
import exception.*;
import org.json.*;

public class PartitionAttributes extends ItemAttributes {

	protected boolean m_parallaxed;
	protected boolean m_sloped;
	protected boolean m_swapTextureXY;
	protected boolean m_doubleSmooshiness;
	protected boolean m_xFlipped;
	protected boolean m_yFlipped;
	protected boolean m_textureAlign;
	protected byte m_reserved; // note: unused

	final public static ItemAttribute Parallaxed = new ItemAttribute("Parallaxed", "parallaxed", (byte) 1, false, (byte) 0);
	final public static ItemAttribute Sloped = new ItemAttribute("Sloped", "sloped", (byte) 1, false, (byte) 1);
	final public static ItemAttribute SwapTextureXY = new ItemAttribute("Swap Texture XY", "swapTextureXY", (byte) 1, false, (byte) 2);
	final public static ItemAttribute DoubleSmooshiness = new ItemAttribute("Double Smooshiness", "doubleSmooshiness", (byte) 1, false, (byte) 3);
	final public static ItemAttribute XFlipped = new ItemAttribute("X Flipped", "xFlipped", (byte) 1, false, (byte) 4);
	final public static ItemAttribute YFlipped = new ItemAttribute("Y Flipped", "yFlipped", (byte) 1, false, (byte) 5);
	final public static ItemAttribute TextureAlign = new ItemAttribute("Texture Align", "textureAlign", (byte) 1, false, (byte) 6);
	final public static ItemAttribute Reserved = new ItemAttribute("Reserved", "reserved", (byte) 9, false, (byte) 7);

	final public static ItemAttribute Attributes[] = {
		Parallaxed,
		Sloped,
		SwapTextureXY,
		DoubleSmooshiness,
		XFlipped,
		YFlipped,
		TextureAlign,
		Reserved
	};
	
	public PartitionAttributes() {
		this(false, false, false, false, false, false, false, (byte) 0);
	}

	public PartitionAttributes(boolean parallaxed, boolean sloped, boolean swapTextureXY, boolean doubleSmooshiness, boolean xFlipped, boolean yFlipped, boolean textureAlign) throws IllegalArgumentException {
		this(parallaxed, sloped, swapTextureXY, doubleSmooshiness, xFlipped, yFlipped, textureAlign, (byte) 0);
	}

	public PartitionAttributes(boolean parallaxed, boolean sloped, boolean swapTextureXY, boolean doubleSmooshiness, boolean xFlipped, boolean yFlipped, boolean textureAlign, byte reserved) throws IllegalArgumentException {
		if(!Reserved.isValidValue(reserved)) { throw new IllegalArgumentException("Invalid reserved partition attribute value: " + reserved + ", expected value between " + Reserved.getMinimumValue() + " and " + Reserved.getMaximumValue() + "."); }

		m_parallaxed = parallaxed;
		m_sloped = sloped;
		m_swapTextureXY = swapTextureXY;
		m_doubleSmooshiness = doubleSmooshiness;
		m_xFlipped = xFlipped;
		m_yFlipped = yFlipped;
		m_textureAlign = textureAlign;
		m_reserved = reserved;
	}

	public byte numberOfBytes() {
		return 2;
	}

	public static int getSizeForVersion(int version) {
		return version == 6 ? 1 : 2;
	}

	public ItemAttribute[] getAttributes() {
		return Attributes;
	}

	public boolean getParallaxed() {
		return m_parallaxed;
	}

	public boolean setParallaxed(byte parallaxed) {
		if(!Parallaxed.isValidValue(parallaxed)) {
			return false;
		}

		return setParallaxed(parallaxed != 0);
	}

	public boolean setParallaxed(boolean parallaxed) {
		m_parallaxed = parallaxed;

		notifyItemAttributeChanged(Parallaxed, m_parallaxed ? (byte) 1 : (byte) 0);

		return true;
	}

	public boolean getSloped() {
		return m_sloped;
	}

	public boolean setSloped(byte sloped) {
		if(!Sloped.isValidValue(sloped)) {
			return false;
		}

		return setSloped(sloped != 0);
	}

	public boolean setSloped(boolean sloped) {
		m_sloped = sloped;

		notifyItemAttributeChanged(Sloped, m_sloped ? (byte) 1 : (byte) 0);

		return true;
	}

	public boolean getSwapTextureXY() {
		return m_swapTextureXY;
	}

	public boolean setSwapTextureXY(byte swapTextureXY) {
		if(!SwapTextureXY.isValidValue(swapTextureXY)) {
			return false;
		}

		return setSwapTextureXY(swapTextureXY != 0);
	}

	public boolean setSwapTextureXY(boolean swapTextureXY) {
		m_swapTextureXY = swapTextureXY;

		notifyItemAttributeChanged(SwapTextureXY, m_swapTextureXY ? (byte) 1 : (byte) 0);

		return true;
	}

	public boolean getDoubleSmooshiness() {
		return m_doubleSmooshiness;
	}

	public boolean setDoubleSmooshiness(byte doubleSmooshiness) {
		if(!DoubleSmooshiness.isValidValue(doubleSmooshiness)) {
			return false;
		}

		return setDoubleSmooshiness(doubleSmooshiness != 0);
	}

	public boolean setDoubleSmooshiness(boolean doubleSmooshiness) {
		m_doubleSmooshiness = doubleSmooshiness;

		notifyItemAttributeChanged(DoubleSmooshiness, m_doubleSmooshiness ? (byte) 1 : (byte) 0);

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

	public boolean getTextureAlign() {
		return m_textureAlign;
	}

	public boolean setTextureAlign(byte textureAlign) {
		if(!TextureAlign.isValidValue(textureAlign)) {
			return false;
		}

		return setTextureAlign(textureAlign != 0);
	}

	public boolean setTextureAlign(boolean textureAlign) {
		m_textureAlign = textureAlign;

		notifyItemAttributeChanged(TextureAlign, m_textureAlign ? (byte) 1 : (byte) 0);

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
		if(attribute == Parallaxed) {
			return m_parallaxed ? (byte) 1 : (byte) 0;
		}
		else if(attribute == Sloped) {
			return m_sloped ? (byte) 1 : (byte) 0;
		}
		else if(attribute == SwapTextureXY) {
			return m_swapTextureXY ? (byte) 1 : (byte) 0;
		}
		else if(attribute == DoubleSmooshiness) {
			return m_doubleSmooshiness ? (byte) 1 : (byte) 0;
		}
		else if(attribute == XFlipped) {
			return m_xFlipped ? (byte) 1 : (byte) 0;
		}
		else if(attribute == YFlipped) {
			return m_yFlipped ? (byte) 1 : (byte) 0;
		}
		else if(attribute == TextureAlign) {
			return m_textureAlign ? (byte) 1 : (byte) 0;
		}
		else if(attribute == Reserved) {
			return m_reserved;
		}
		
		throw new IllegalArgumentException("Invalid item attribute type.");
	}

	public boolean setAttributeValue(ItemAttribute attribute, byte value) throws IllegalArgumentException {
		if(attribute == Parallaxed) {
			return setParallaxed(value);
		}
		else if(attribute == Sloped) {
			return setSloped(value);
		}
		else if(attribute == SwapTextureXY) {
			return setSwapTextureXY(value);
		}
		else if(attribute == DoubleSmooshiness) {
			return setDoubleSmooshiness(value);
		}
		else if(attribute == XFlipped) {
			return setXFlipped(value);
		}
		else if(attribute == YFlipped) {
			return setYFlipped(value);
		}
		else if(attribute == TextureAlign) {
			return setTextureAlign(value);
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

	public void setAttributes(boolean parallaxed, boolean sloped, boolean swapTextureXY, boolean doubleSmooshiness, boolean xFlip, boolean yFlip, boolean textureAlign) throws IllegalArgumentException {
		setAttributes(new PartitionAttributes(parallaxed, sloped, swapTextureXY, doubleSmooshiness, xFlip, yFlip, textureAlign));
	}

	public void setAttributes(boolean parallaxed, boolean sloped, boolean swapTextureXY, boolean doubleSmooshiness, boolean xFlip, boolean yFlip, boolean textureAlign, byte reserved) throws IllegalArgumentException {
		setAttributes(new PartitionAttributes(parallaxed, sloped, swapTextureXY, doubleSmooshiness, xFlip, yFlip, textureAlign, reserved));
	}

	public void setAttributes(ItemAttributes attributes, boolean reassignItemChangeListeners) throws IllegalArgumentException {
		if(attributes == null) {
			throw new IllegalArgumentException("Partition attributes cannot be null!");
		}

		if(!(attributes instanceof PartitionAttributes)) {
			throw new IllegalArgumentException("Invalid partition attributes instance!");
		}

		PartitionAttributes a = (PartitionAttributes) attributes;

		setParallaxed(a.m_parallaxed);
		setSloped(a.m_sloped);
		setSwapTextureXY(a.m_swapTextureXY);
		setDoubleSmooshiness(a.m_doubleSmooshiness);
		setXFlipped(a.m_xFlipped);
		setYFlipped(a.m_yFlipped);
		setTextureAlign(a.m_textureAlign);
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
	
	public static PartitionAttributes unpack(short packedValue) {
		ItemAttribute attribute = null;
		PartitionAttributes attributes = new PartitionAttributes();
		
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

	public static PartitionAttributes fromJSONObject(JSONObject partitionAttributes) throws IllegalArgumentException, JSONException, MalformedItemAttributeException {
		if(partitionAttributes == null) {
			throw new IllegalArgumentException("Partition attributes JSON data cannot be null.");
		}

		PartitionAttributes newAttributes = new PartitionAttributes();
		ItemAttribute attribute = null;

		for(int i = 0; i < Attributes.length; i++) {
			attribute = Attributes[i];

			if(attribute == Reserved) {
				if(!partitionAttributes.has(Reserved.getAttributeName())) {
					continue;
				}

				if(!newAttributes.setReserved((byte) partitionAttributes.getInt(Reserved.getAttributeName()))) {
					throw new MalformedItemAttributeException("Invalid " + Reserved.getDisplayName() + " attribute value.");
				}
			}
			else {
				if(!newAttributes.setAttributeValue(attribute, partitionAttributes.getBoolean(attribute.getAttributeName()) ? (byte) 1 : (byte) 0)) {
					throw new MalformedItemAttributeException("Invalid " + attribute.getDisplayName() + " attribute value.");
				}
			}
		}

		return newAttributes;
	}

	public ItemAttributes clone(boolean reassignItemChangeListeners) {
		PartitionAttributes newPartitionAttributes = new PartitionAttributes(m_parallaxed, m_sloped, m_swapTextureXY, m_doubleSmooshiness, m_xFlipped, m_yFlipped, m_textureAlign, m_reserved);

		if(reassignItemChangeListeners) {
			newPartitionAttributes.m_itemAttributeChangeListeners = new Vector<ItemAttributeChangeListener>(m_itemAttributeChangeListeners.size());
			
			for(int i = 0; i < m_itemAttributeChangeListeners.size(); i++) {
				newPartitionAttributes.m_itemAttributeChangeListeners.add(m_itemAttributeChangeListeners.elementAt(i));
			}
		}

		return newPartitionAttributes;
	}
	
	public void reset() {
		m_parallaxed = false;
		m_sloped = false;
		m_swapTextureXY = false;
		m_doubleSmooshiness = false;
		m_xFlipped = false;
		m_yFlipped = false;
		m_textureAlign = false;
		m_reserved = (byte) 0;
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof PartitionAttributes)) {
			return false;
		}
		
		PartitionAttributes a = (PartitionAttributes) o;
		
		return m_parallaxed == a.m_parallaxed &&
			   m_sloped == a.m_sloped &&
			   m_swapTextureXY == a.m_swapTextureXY &&
			   m_doubleSmooshiness == a.m_doubleSmooshiness &&
			   m_xFlipped == a.m_xFlipped &&
			   m_yFlipped == a.m_yFlipped &&
			   m_textureAlign == a.m_textureAlign &&
			   m_reserved == a.m_reserved;
	}
	
}
