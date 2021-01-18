package map;

import java.util.Vector;
import item.*;
import exception.*;
import org.json.*;

public class Partition implements ItemAttributeChangeListener {

	private int m_height;
	private PartitionAttributes m_attributes;
	private short m_tileNumber;
	private short m_slope;
	private byte m_shade;
	private short m_paletteLookupTableNumber;
	private short m_xPanning;
	private short m_yPanning;
	protected Vector<PartitionChangeListener> m_partitionChangeListeners;

	public static final String HEIGHT_ATTRIBUTE_NAME = "height";
	public static final String ATTRIBUTES_ATTRIBUTE_NAME = "attributes";
	public static final String TILE_NUMBER_ATTRIBUTE_NAME = "tileNumber";
	public static final String SLOPE_ATTRIBUTE_NAME = "slope";
	public static final String SHADE_ATTRIBUTE_NAME = "shade";
	public static final String PALETTE_LOOKUP_TABLE_NUMBER_ATTRIBUTE_NAME = "paletteLookupTableNumber";
	public static final String X_PANNING_ATTRIBUTE_NAME = "xPanning";
	public static final String Y_PANNING_ATTRIBUTE_NAME = "yPanning";

	public Partition(int height, short attributes, short tileNumber, short slope, byte shade, short paletteLookupTableNumber, short xPanning, short yPanning) {
		this(height, PartitionAttributes.unpack(attributes), tileNumber, slope, shade, paletteLookupTableNumber, xPanning, yPanning);
	}

	public Partition(int height, PartitionAttributes attributes, short tileNumber, short slope, byte shade, short paletteLookupTableNumber, short xPanning, short yPanning) {
		if(attributes == null) { throw new IllegalArgumentException("Invalid partition attributes."); }
		if(paletteLookupTableNumber < 0 || paletteLookupTableNumber > 255) { throw new IllegalArgumentException("Invalid palette lookup table value: " + paletteLookupTableNumber + ", expected value between 0 and 255."); }
		if(xPanning < 0 || xPanning > 255) { throw new IllegalArgumentException("Invalid x pannning value: " + xPanning + ", expected value between 0 and 255."); }
		if(yPanning < 0 || yPanning > 255) { throw new IllegalArgumentException("Invalid y pannning value: " + yPanning + ", expected value between 0 and 255."); }

		m_height = height;
		m_tileNumber = tileNumber;
		m_slope = slope;
		m_shade = shade;
		m_paletteLookupTableNumber = paletteLookupTableNumber;
		m_xPanning = xPanning;
		m_yPanning = yPanning;
		m_partitionChangeListeners = new Vector<PartitionChangeListener>();

		setAttributes(attributes);
	}

	public static int getSizeForVersion(int version) {
		return 12 + PartitionAttributes.getSizeForVersion(version);
	}

	public int getHeight() {
		return m_height;
	}

	public void setHeight(int height) {
		if(m_height == height) {
			return;
		}

		m_height = height;

		notifyPartitionChanged();
	}

	public PartitionAttributes getAttributes() {
		return m_attributes;
	}

	public void setAttributes(PartitionAttributes attributes) {
		if(attributes == null) {
			throw new IllegalArgumentException("Partition attributes cannot be null.");
		}

		if(m_attributes != null) {
			if(m_attributes.equals(attributes)) {
				return;
			}

			m_attributes.removeItemAttributeChangeListener(this);
		}

		m_attributes = (PartitionAttributes) attributes.clone();
		m_attributes.addItemAttributeChangeListener(this);

		notifyPartitionChanged();
	}

	public short getTileNumber() {
		return m_tileNumber;
	}

	public void setTileNumber(short tileNumber) {
		if(m_tileNumber == tileNumber) {
			return;
		}

		m_tileNumber = tileNumber;

		notifyPartitionChanged();
	}

	public short getSlope() {
		return m_slope;
	}

	public void setSlope(short slope) {
		if(m_slope == slope) {
			return;
		}

		m_slope = slope;

		notifyPartitionChanged();
	}

	public byte getShade() {
		return m_shade;
	}

	public void setShade(byte shade) {
		if(m_shade == shade) {
			return;
		}

		m_shade = shade;

		notifyPartitionChanged();
	}

	public short getPaletteLookupTableNumber() {
		return m_paletteLookupTableNumber;
	}

	public void setPaletteLookupTableNumber(short paletteLookupTableNumber) {
		if(paletteLookupTableNumber < 0 || paletteLookupTableNumber > 255) { throw new IllegalArgumentException("Invalid palette lookup table value: " + paletteLookupTableNumber + ", expected value between 0 and 255."); }

		if(m_paletteLookupTableNumber == paletteLookupTableNumber) {
			return;
		}

		m_paletteLookupTableNumber = paletteLookupTableNumber;

		notifyPartitionChanged();
	}

	public short getXPanning() {
		return m_xPanning;
	}

	public void setXPanning(short xPanning) {
		if(xPanning < 0 || xPanning > 255) { throw new IllegalArgumentException("Invalid x panning value: " + xPanning + ", expected value between 0 and 255."); }

		if(m_xPanning == xPanning) {
			return;
		}

		m_xPanning = xPanning;

		notifyPartitionChanged();
	}

	public short getYPanning() {
		return m_yPanning;
	}

	public void setYPanning(short yPanning) {
		if(yPanning < 0 || yPanning > 255) { throw new IllegalArgumentException("Invalid y panning value: " + yPanning + ", expected value between 0 and 255."); }

		if(m_yPanning == yPanning) {
			return;
		}

		m_yPanning = yPanning;

		notifyPartitionChanged();
	}

	public JSONObject toJSONObject() {
		JSONObject partition = new JSONObject();
		partition.put(HEIGHT_ATTRIBUTE_NAME, m_height);
		partition.put(ATTRIBUTES_ATTRIBUTE_NAME, m_attributes.toJSONObject());
		partition.put(TILE_NUMBER_ATTRIBUTE_NAME, m_tileNumber);
		partition.put(SLOPE_ATTRIBUTE_NAME, m_slope);
		partition.put(SHADE_ATTRIBUTE_NAME, m_shade);
		partition.put(PALETTE_LOOKUP_TABLE_NUMBER_ATTRIBUTE_NAME, m_paletteLookupTableNumber);
		partition.put(X_PANNING_ATTRIBUTE_NAME, m_xPanning);
		partition.put(Y_PANNING_ATTRIBUTE_NAME, m_yPanning);

		return partition;
	}

	public static Partition fromJSONObject(JSONObject partition) throws IllegalArgumentException, JSONException, MalformedItemAttributeException {
		if(partition == null) {
			throw new IllegalArgumentException("Partition JSON data cannot be null.");
		}

		return new Partition(
			partition.getInt(HEIGHT_ATTRIBUTE_NAME),
			(PartitionAttributes) PartitionAttributes.fromJSONObject(partition.getJSONObject(ATTRIBUTES_ATTRIBUTE_NAME)),
			(short) partition.getInt(TILE_NUMBER_ATTRIBUTE_NAME),
			(short) partition.getInt(SLOPE_ATTRIBUTE_NAME),
			(byte) partition.getInt(SHADE_ATTRIBUTE_NAME),
			(short) partition.getInt(PALETTE_LOOKUP_TABLE_NUMBER_ATTRIBUTE_NAME),
			(short) partition.getInt(X_PANNING_ATTRIBUTE_NAME),
			(short) partition.getInt(Y_PANNING_ATTRIBUTE_NAME)
		);
	}

	public Partition clone() {
		return new Partition(m_height, m_attributes, m_tileNumber, m_slope, m_shade, m_paletteLookupTableNumber, m_xPanning, m_yPanning);
	}

	public int numberOfPartitionChangeListeners() {
		return m_partitionChangeListeners.size();
	}
	
	public PartitionChangeListener getPartitionChangeListener(int index) {
		if(index < 0 || index >= m_partitionChangeListeners.size()) { return null; }

		return m_partitionChangeListeners.elementAt(index);
	}
	
	public boolean hasPartitionChangeListener(PartitionChangeListener c) {
		return m_partitionChangeListeners.contains(c);
	}
	
	public int indexOfPartitionChangeListener(PartitionChangeListener c) {
		return m_partitionChangeListeners.indexOf(c);
	}
	
	public boolean addPartitionChangeListener(PartitionChangeListener c) {
		if(c == null || m_partitionChangeListeners.contains(c)) { return false; }

		m_partitionChangeListeners.add(c);

		return true;
	}
	
	public boolean removePartitionChangeListener(int index) {
		if(index < 0 || index >= m_partitionChangeListeners.size()) { return false; }

		m_partitionChangeListeners.remove(index);

		return true;
	}
	
	public boolean removePartitionChangeListener(PartitionChangeListener c) {
		if(c == null) { return false; }

		return m_partitionChangeListeners.remove(c);
	}
	
	public void clearPartitionChangeListeners() {
		m_partitionChangeListeners.clear();
	}
	
	protected void notifyPartitionChanged() {
		for(int i=0;i<m_partitionChangeListeners.size();i++) {
			m_partitionChangeListeners.elementAt(i).handlePartitionChange(this);
		}
	}

	public void handleItemAttributeChange(ItemAttributes attributes, ItemAttribute attribute, byte value) {
		notifyPartitionChanged();
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof Partition)) {
			return false;
		}
		
		Partition p = (Partition) o;
		
		return m_height == p.m_height &&
			   m_attributes.equals(p.m_attributes) &&
			   m_tileNumber == p.m_tileNumber &&
			   m_slope == p.m_slope &&
			   m_shade == p.m_shade &&
			   m_paletteLookupTableNumber == p.m_paletteLookupTableNumber &&
			   m_xPanning == p.m_xPanning &&
			   m_yPanning == p.m_yPanning;
	}

}
