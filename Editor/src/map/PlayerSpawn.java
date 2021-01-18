package map;

import java.util.*;
import java.awt.*;
import exception.*;
import utilities.*;
import org.json.*;

public class PlayerSpawn extends MapComponent implements PositionChangeListener {

	protected Position m_position;
	protected short m_angle;
	protected short m_sectorNumber;
	protected Vector<PlayerSpawnChangeListener> m_playerSpawnChangeListeners;

	public static final int SIZE = 16;

	public static final String POSITION_ATTRIBUTE_NAME = "position";
	public static final String ANGLE_ATTRIBUTE_NAME = "angle";
	public static final String SECTOR_NUMBER_ATTRIBUTE_NAME = "sector";

	protected static final double ARROW_SIZE = 128;

	public PlayerSpawn() {
		this(new Position(32768, 32768, 0), (short) 1536, (short) 0);
	}

	public PlayerSpawn(int x, int y, int z, short angle, short sectorNumber) {
		this(new Position(x, y, z), angle, sectorNumber);
	}

	public PlayerSpawn(Position position, short angle, short sectorNumber) {
		m_playerSpawnChangeListeners = new Vector<PlayerSpawnChangeListener>();

		setPosition(position);
		setAngle(angle);
		setSectorNumber(sectorNumber);
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

		notifyPlayerSpawnChanged();
	}

	public short getAngle() {
		return m_angle;
	}

	public double getAngleDegrees() {
		return (m_angle / (double) BuildConstants.MAX_ANGLE) * 360.0;
	}

	public double getAngleRadians() {
		return (m_angle / (double) BuildConstants.MAX_ANGLE) * Math.PI * 2;
	}

	public void setAngle(short angle) throws IllegalArgumentException {
		if(angle < BuildConstants.MIN_ANGLE || angle > BuildConstants.MAX_ANGLE) {
			throw new IllegalArgumentException("Invalid player spawn angle, expected value between " + BuildConstants.MIN_ANGLE + " and " + BuildConstants.MAX_ANGLE + ", inclusively.");
		}

		if(m_angle == angle) {
			return;
		}

		m_angle = angle;

		notifyPlayerSpawnChanged();
	}

	public short getSectorNumber() {
		return m_sectorNumber;
	}

	public void setSectorNumber(short sectorNumber) {
		if(m_sectorNumber == sectorNumber) {
			return;
		}

		m_sectorNumber = sectorNumber;

		notifyPlayerSpawnChanged();
	}

	public void setMap(Map map) {
		if(m_map != null) {
			removePlayerSpawnChangeListener(map);
		}

		super.setMap(map);

		if(m_map != null) {
			addPlayerSpawnChangeListener(map);
		}
	}

	public byte[] serialize() {
		return serialize(Endianness.LittleEndian);
	}

	public byte[] serialize(Endianness endianness) {
		byte data[] = new byte[SIZE];
		int offset = 0;

		System.arraycopy(m_position.serialize(endianness), 0, data, offset, Position.SIZE);
		offset += Position.SIZE;

		System.arraycopy(Serializer.serializeShort(m_angle, endianness), 0, data, offset, 2);
		offset += 2;

		System.arraycopy(Serializer.serializeShort(m_sectorNumber, endianness), 0, data, offset, 2);
		offset += 2;

		return data;
	}

	public static PlayerSpawn deserialize(byte data[]) throws DeserializationException {
		return deserialize(data, 0);
	}

	public static PlayerSpawn deserialize(byte data[], int offset) throws DeserializationException {
		return deserialize(data, offset, Endianness.LittleEndian);
	}

	public static PlayerSpawn deserialize(byte data[], Endianness endianness) throws DeserializationException {
		return deserialize(data, 0, endianness);
	}

	public static PlayerSpawn deserialize(byte data[], int offset, Endianness endianness) throws DeserializationException {
		if(data == null) {
			throw new MapDeserializationException("Invalid player spawn data.");
		}

		if(offset < 0 || offset >= data.length) {
			throw new MapDeserializationException("Invalid data offset.");
		}

		// verify that the data is long enough to contain required information
		if(data.length - offset < SIZE) {
			throw new MapDeserializationException("Player spawn data is incomplete or corrupted.");
		}

		// read the player spawn position
		Position position = Position.deserialize(data, offset, endianness);
		offset += Position.SIZE;

		// read the player spawn angle
		short angle = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
		offset += 2;

		// read the sector number
		short sectorNumber = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness);
		offset += 2;

		return new PlayerSpawn(position, angle, sectorNumber);
	}

	public JSONObject toJSONObject() {
		JSONObject playerSpawn = new JSONObject();
		playerSpawn.put(POSITION_ATTRIBUTE_NAME, m_position.toJSONObject());
		playerSpawn.put(ANGLE_ATTRIBUTE_NAME, m_angle);
		playerSpawn.put(SECTOR_NUMBER_ATTRIBUTE_NAME, m_sectorNumber);

		return playerSpawn;
	}

	public static PlayerSpawn fromJSONObject(JSONObject playerSpawn) throws IllegalArgumentException, JSONException {
		if(playerSpawn == null) {
			throw new IllegalArgumentException("Player spawn JSON data cannot be null.");
		}

		return new PlayerSpawn(
			Position.fromJSONObject(playerSpawn.getJSONObject(POSITION_ATTRIBUTE_NAME)),
			(short) playerSpawn.getInt(ANGLE_ATTRIBUTE_NAME),
			(short) playerSpawn.getInt(SECTOR_NUMBER_ATTRIBUTE_NAME)
		);
	}

	public PlayerSpawn clone() {
		return new PlayerSpawn(m_position, m_angle, m_sectorNumber);
	}

	public int numberOfPlayerSpawnChangeListeners() {
		return m_playerSpawnChangeListeners.size();
	}
	
	public PlayerSpawnChangeListener getPlayerSpawnChangeListener(int index) {
		if(index < 0 || index >= m_playerSpawnChangeListeners.size()) { return null; }

		return m_playerSpawnChangeListeners.elementAt(index);
	}
	
	public boolean hasPlayerSpawnChangeListener(PlayerSpawnChangeListener c) {
		return m_playerSpawnChangeListeners.contains(c);
	}
	
	public int indexOfPlayerSpawnChangeListener(PlayerSpawnChangeListener c) {
		return m_playerSpawnChangeListeners.indexOf(c);
	}
	
	public boolean addPlayerSpawnChangeListener(PlayerSpawnChangeListener c) {
		if(c == null || m_playerSpawnChangeListeners.contains(c)) { return false; }

		m_playerSpawnChangeListeners.add(c);

		return true;
	}
	
	public boolean removePlayerSpawnChangeListener(int index) {
		if(index < 0 || index >= m_playerSpawnChangeListeners.size()) { return false; }

		m_playerSpawnChangeListeners.remove(index);

		return true;
	}
	
	public boolean removePlayerSpawnChangeListener(PlayerSpawnChangeListener c) {
		if(c == null) { return false; }

		return m_playerSpawnChangeListeners.remove(c);
	}
	
	public void clearPlayerSpawnChangeListeners() {
		m_playerSpawnChangeListeners.clear();
	}
	
	protected void notifyPlayerSpawnChanged() {
		for(int i=0;i<m_playerSpawnChangeListeners.size();i++) {
			m_playerSpawnChangeListeners.elementAt(i).handleMapComponentChange(this);
			m_playerSpawnChangeListeners.elementAt(i).handlePlayerSpawnChange(this);
		}
	}

	public void handlePositionChange(Position position) {
		notifyPlayerSpawnChanged();
	}

	public void paintPlayerSpawn(Graphics2D g) {
		paintPlayerSpawn(g, 1.0);
	}

	public void paintPlayerSpawn(Graphics2D g, double zoom) {
		if(g == null || zoom <= 0.0) {
			return;
		}

		int x = (int) (m_position.getX() * zoom);
		int y = (int) (m_position.getY() * zoom);

		double angle = getAngleRadians();
		double arrowSize = ARROW_SIZE * zoom;

		int arrowTipX = (int) (x + (arrowSize * Math.cos(angle)));
		int arrowTipY = (int) (y + (arrowSize * Math.sin(angle)));

		int arrowTailX = (int) (x + (arrowSize * Math.cos(angle + Math.PI)));
		int arrowTailY = (int) (y + (arrowSize * Math.sin(angle + Math.PI)));

		int arrowLeftArmX = (int) (x + (arrowSize * Math.cos(angle - (Math.PI / 2.0))));
		int arrowLeftArmY = (int) (y + (arrowSize * Math.sin(angle - (Math.PI / 2.0))));

		int arrowRightArmX = (int) (x + (arrowSize * Math.cos(angle + (Math.PI / 2.0))));
		int arrowRightArmY = (int) (y + (arrowSize * Math.sin(angle + (Math.PI / 2.0))));

		g.setColor(Color.ORANGE);
		g.drawLine(arrowTipX, arrowTipY, arrowTailX, arrowTailY);
		g.drawLine(arrowTipX, arrowTipY, arrowLeftArmX, arrowLeftArmY);
		g.drawLine(arrowTipX, arrowTipY, arrowRightArmX, arrowRightArmY);
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof PlayerSpawn)) {
			return false;
		}
		
		PlayerSpawn s = (PlayerSpawn) o;
		
		return m_position.equals(s.m_position) &&
			   m_angle == s.m_angle &&
			   m_sectorNumber == s.m_sectorNumber;
	}
	
	public String toString() {
		return "Spawn Position: " + m_position + ", Angle: " + m_angle + ", Sector Number: " + m_sectorNumber;
	}

}
