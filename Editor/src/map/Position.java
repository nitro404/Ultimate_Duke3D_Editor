package map;

import java.util.*;
import exception.*;
import utilities.*;
import org.json.*;

public class Position {

	protected int m_x;
	protected int m_y;
	protected int m_z;
	protected Vector<PositionChangeListener> m_positionChangeListeners;

	public static int SIZE = 12;
	
	public static final String X_ATTRIBUTE_NAME = "x";
	public static final String Y_ATTRIBUTE_NAME = "y";
	public static final String Z_ATTRIBUTE_NAME = "z";

	public Position() {
		this(0, 0, 0);
	}

	public Position(int x, int y, int z) {
		m_x = x;
		m_y = y;
		m_z = z;
		m_positionChangeListeners = new Vector<PositionChangeListener>();
	}

	public int getX() {
		return m_x;
	}

	public void setX(int x) {
		if(m_x == x) {
			return;
		}

		m_x = x;

		notifyPositionChanged();
	}

	public int getY() {
		return m_y;
	}

	public void setY(int y) {
		if(m_y == y) {
			return;
		}

		m_y = y;

		notifyPositionChanged();
	}

	public int getZ() {
		return m_z;
	}

	public void setZ(int z) {
		if(m_z == z) {
			return;
		}

		m_z = z;

		notifyPositionChanged();
	}

	public byte[] serialize() {
		return serialize(Endianness.LittleEndian);
	}

	public byte[] serialize(Endianness endianness) {
		byte data[] = new byte[SIZE];
		int offset = 0;

		System.arraycopy(Serializer.serializeInteger(m_x, endianness), 0, data, offset, 4);
		offset += 4;

		System.arraycopy(Serializer.serializeInteger(m_y, endianness), 0, data, offset, 4);
		offset += 4;

		System.arraycopy(Serializer.serializeInteger(m_z, endianness), 0, data, offset, 4);
		offset += 4;

		return data;
	}

	public static Position deserialize(byte data[]) throws DeserializationException {
		return deserialize(data, 0);
	}

	public static Position deserialize(byte data[], int offset) throws DeserializationException {
		return deserialize(data, offset, Endianness.LittleEndian);
	}

	public static Position deserialize(byte data[], Endianness endianness) throws DeserializationException {
		return deserialize(data, 0, endianness);
	}

	public static Position deserialize(byte data[], int offset, Endianness endianness) throws DeserializationException {
		if(data == null) {
			throw new MapDeserializationException("Invalid position data.");
		}

		if(offset < 0 || offset >= data.length) {
			throw new MapDeserializationException("Invalid data offset.");
		}

		// verify that the data is long enough to contain required information
		if(data.length - offset < SIZE) {
			throw new MapDeserializationException("Position data is incomplete or corrupted.");
		}

		// read the x co-ordinate
		int x = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + 4), endianness);
		offset += 4;

		// read the y co-ordinate
		int y = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + 4), endianness);
		offset += 4;

		// read the z co-ordinate
		int z = Serializer.deserializeInteger(Arrays.copyOfRange(data, offset, offset + 4), endianness);
		offset += 4;

		return new Position(x, y, z);
	}

	public JSONObject toJSONObject() {
		JSONObject position = new JSONObject();
		position.put(X_ATTRIBUTE_NAME, m_x);
		position.put(Y_ATTRIBUTE_NAME, m_y);
		position.put(Z_ATTRIBUTE_NAME, m_z);

		return position;
	}

	public static Position fromJSONObject(JSONObject position) throws IllegalArgumentException, JSONException {
		if(position == null) {
			throw new IllegalArgumentException("Position JSON data cannot be null.");
		}

		return new Position(
			(short) position.getInt(X_ATTRIBUTE_NAME),
			(short) position.getInt(Y_ATTRIBUTE_NAME),
			(short) position.getInt(Z_ATTRIBUTE_NAME)
		);
	}

	public Position clone() {
		return new Position(m_x, m_y, m_z);
	}

	public int numberOfPositionChangeListeners() {
		return m_positionChangeListeners.size();
	}
	
	public PositionChangeListener getPositionChangeListener(int index) {
		if(index < 0 || index >= m_positionChangeListeners.size()) { return null; }

		return m_positionChangeListeners.elementAt(index);
	}
	
	public boolean hasPositionChangeListener(PositionChangeListener c) {
		return m_positionChangeListeners.contains(c);
	}
	
	public int indexOfPositionChangeListener(PositionChangeListener c) {
		return m_positionChangeListeners.indexOf(c);
	}
	
	public boolean addPositionChangeListener(PositionChangeListener c) {
		if(c == null || m_positionChangeListeners.contains(c)) { return false; }

		m_positionChangeListeners.add(c);

		return true;
	}
	
	public boolean removePositionChangeListener(int index) {
		if(index < 0 || index >= m_positionChangeListeners.size()) { return false; }

		m_positionChangeListeners.remove(index);

		return true;
	}
	
	public boolean removePositionChangeListener(PositionChangeListener c) {
		if(c == null) { return false; }

		return m_positionChangeListeners.remove(c);
	}
	
	public void clearPositionChangeListeners() {
		m_positionChangeListeners.clear();
	}
	
	public void notifyPositionChanged() {
		for(int i=0;i<m_positionChangeListeners.size();i++) {
			m_positionChangeListeners.elementAt(i).handlePositionChange(this);
		}
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof Position)) {
			return false;
		}
		
		Position p = (Position) o;
		
		return m_x == p.m_x &&
			   m_y == p.m_y &&
			   m_z == p.m_z;
	}
	
	public String toString() {
		return m_x + ", " + m_y + ", " + m_z;
	}

}
