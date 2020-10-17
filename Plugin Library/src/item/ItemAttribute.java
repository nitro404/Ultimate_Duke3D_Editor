package item;

public class ItemAttribute {

	private String m_displayName;
	private String m_attributeName;
	private byte m_bitSize;
	private boolean m_signed;
	private byte m_bitOffset;

	public ItemAttribute(String displayName, String attributeName, byte bitSize, boolean signed, byte bitOffset) throws IllegalArgumentException {
		if(displayName.trim().isEmpty()) { throw new IllegalArgumentException("Item attribute display name cannot be empty."); }
		if(attributeName.trim().isEmpty()) { throw new IllegalArgumentException("Item attribute attribute name cannot be empty."); }
		if(bitSize < 1 || bitSize > 32) { throw new IllegalArgumentException("Item attribute bit size must be between 1 and 32."); }
		if(bitOffset < 0 || bitOffset > 31) { throw new IllegalArgumentException("Item attribute bit offset must be between 0 and 31."); }

		m_displayName = displayName.trim();
		m_attributeName = attributeName.trim();
		m_bitSize = bitSize;
		m_signed = signed;
		m_bitOffset = bitOffset;
	}

	public String getDisplayName() {
		return m_displayName;
	}

	public String getAttributeName() {
		return m_attributeName;
	}

	public byte getBitSize() {
		return m_bitSize;
	}

	public boolean isSigned() {
		return m_signed;
	}

	public byte getBitOffset() {
		return m_bitOffset;
	}

	public int getMinimumValue() {
		return m_signed ? -((m_bitSize - 1) << 1) : 0;
	}

	public int getMaximumValue() {
		return m_signed ? ((m_bitSize - 1) << 1) - 1 : (m_bitSize << 1) - 1;
	}

	public int getBitMask() {
		return (m_bitSize << 1) - 1;
	}

	public int getOffsetBitMask() {
		return ((m_bitSize << 1) - 1) << m_bitOffset; 
	}

	public int getSignBitMask() {
		return (m_bitSize - 1) << 1;
	}

	public boolean isValidValue(byte value) {
		return value >= getMinimumValue() && value <= getMaximumValue();
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof ItemAttribute)) {
			return false;
		}

		ItemAttribute a = (ItemAttribute) o;

		return m_displayName.equalsIgnoreCase(a.m_displayName) &&
			   m_attributeName.equalsIgnoreCase(a.m_attributeName) &&
			   m_bitSize == a.m_bitSize &&
			   m_signed == a.m_signed &&
			   m_bitOffset == a.m_bitOffset;
	}

	public String toString() {
		return m_displayName + " Item Attribute (Size: " + m_bitSize + ", Signed: " + m_signed + ", Offset: " + m_bitOffset + ")";
	}

}
