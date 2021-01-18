package map;

import java.util.*;
import exception.*;
import math.*;
import utilities.*;
import org.json.*;

public class Velocity extends Vector3 {

	protected Vector<VelocityChangeListener> m_velocityChangeListeners;

	final public static int SIZE = 6;

	public static final String X_ATTRIBUTE_NAME = "x";
	public static final String Y_ATTRIBUTE_NAME = "y";
	public static final String Z_ATTRIBUTE_NAME = "z";

	public Velocity() {
		this(0.0, 0.0, 0.0);
	}

	public Velocity(Vector3 v) {
		this(v.x, v.y, v.z);
	}

	public Velocity(short x, short y, short z) {
		this((double) x, (double) y, (double) z);
	}

	public Velocity(double x, double y, double z) {
		super(x, y, z);

		m_velocityChangeListeners = new Vector<VelocityChangeListener>();
	}

	public byte[] serialize() {
		return serialize(Endianness.LittleEndian);
	}

	public byte[] serialize(Endianness endianness) {
		byte data[] = new byte[SIZE];
		int offset = 0;

		// serialize velocity vector x value
		System.arraycopy(Serializer.serializeShort((short) x, endianness), 0, data, offset, 2);
		offset += 2;

		// serialize velocity vector y value
		System.arraycopy(Serializer.serializeShort((short) y, endianness), 0, data, offset, 2);
		offset += 2;

		// serialize velocity vector z value
		System.arraycopy(Serializer.serializeShort((short) z, endianness), 0, data, offset, 2);
		offset += 2;

		return data;
	}

	public static Velocity deserialize(byte data[]) throws DeserializationException {
		return deserialize(data, 0);
	}

	public static Velocity deserialize(byte data[], int offset) throws DeserializationException {
		return deserialize(data, offset, Endianness.LittleEndian);
	}

	public static Velocity deserialize(byte data[], Endianness endianness) throws DeserializationException {
		return deserialize(data, 0, endianness);
	}

	public static Velocity deserialize(byte data[], int offset, Endianness endianness) throws DeserializationException {
		if(data == null) {
			throw new DeserializationException("Invalid velocity data.");
		}

		if(offset < 0 || offset >= data.length) {
			throw new DeserializationException("Invalid data offset.");
		}

		// verify that the data is long enough to contain required information
		if(data.length - offset < SIZE) {
			throw new DeserializationException("Velocity data is incomplete or corrupted.");
		}

		// read the velocity vector x value
		short x = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
		offset += 2;

		// read the velocity vector y value
		short y = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
		offset += 2;

		// read the velocity vector z value
		short z = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
		offset += 2;

		return new Velocity(x, y, z);
	}

	public JSONObject toJSONObject() {
		JSONObject velocity = new JSONObject();
		velocity.put(X_ATTRIBUTE_NAME, (short) x);
		velocity.put(Y_ATTRIBUTE_NAME, (short) y);
		velocity.put(Z_ATTRIBUTE_NAME, (short) z);

		return velocity;
	}

	public static Velocity fromJSONObject(JSONObject velocity) throws IllegalArgumentException, JSONException {
		if(velocity == null) {
			throw new IllegalArgumentException("Velocity JSON data cannot be null.");
		}

		return new Velocity(
			(short) velocity.getInt(X_ATTRIBUTE_NAME),
			(short) velocity.getInt(Y_ATTRIBUTE_NAME),
			(short) velocity.getInt(Z_ATTRIBUTE_NAME)
		);
	}

	public int numberOfVelocityChangeListeners() {
		return m_velocityChangeListeners.size();
	}
	
	public VelocityChangeListener getVelocityChangeListener(int index) {
		if(index < 0 || index >= m_velocityChangeListeners.size()) { return null; }

		return m_velocityChangeListeners.elementAt(index);
	}
	
	public boolean hasVelocityChangeListener(VelocityChangeListener c) {
		return m_velocityChangeListeners.contains(c);
	}
	
	public int indexOfVelocityChangeListener(VelocityChangeListener c) {
		return m_velocityChangeListeners.indexOf(c);
	}
	
	public boolean addVelocityChangeListener(VelocityChangeListener c) {
		if(c == null || m_velocityChangeListeners.contains(c)) { return false; }

		m_velocityChangeListeners.add(c);

		return true;
	}
	
	public boolean removeVelocityChangeListener(int index) {
		if(index < 0 || index >= m_velocityChangeListeners.size()) { return false; }

		m_velocityChangeListeners.remove(index);

		return true;
	}
	
	public boolean removeVelocityChangeListener(VelocityChangeListener c) {
		if(c == null) { return false; }

		return m_velocityChangeListeners.remove(c);
	}
	
	public void clearVelocityChangeListeners() {
		m_velocityChangeListeners.clear();
	}
	
	protected void notifyVelocityChanged() {
		for(int i=0;i<m_velocityChangeListeners.size();i++) {
			m_velocityChangeListeners.elementAt(i).handleVelocityChange(this);
		}
	}

}
