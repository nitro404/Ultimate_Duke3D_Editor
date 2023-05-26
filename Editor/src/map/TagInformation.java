package map;

import java.util.*;
import exception.*;
import utilities.*;
import org.json.*;

public class TagInformation {

	protected int m_lowTag;
	protected int m_highTag;
	protected int m_extra;
	protected Vector<TagInformationChangeListener> m_tagInformationChangeListeners;

	final public static int SIZE = 6;

	public static final String LOW_TAG_ATTRIBUTE_NAME = "lowTag";
	public static final String HIGH_TAG_ATTRIBUTE_NAME = "highTag";
	public static final String EXTRA_ATTRIBUTE_NAME = "extra";

	public TagInformation(int lowTag, int highTag, int extra)  throws IllegalArgumentException {
		m_tagInformationChangeListeners = new Vector<TagInformationChangeListener>();

		setLowTag(lowTag);
		setHighTag(highTag);
		setExtra(extra);
	}

	public boolean hasLowTag() {
		return m_lowTag != 0;
	}

	public int getLowTag() {
		return m_lowTag;
	}

	public void setLowTag(int lowTag) throws IllegalArgumentException {
		if(lowTag < 0 || lowTag > 65535) {
			throw new IllegalArgumentException("Invalid low tag value: " + lowTag + ", expected value between 0 and 65535.");
		}

		if(m_lowTag == lowTag) {
			return;
		}

		m_lowTag = lowTag;

		notifyTagInformationChanged();
	}

	public boolean hasHighTag() {
		return m_highTag != 0;
	}

	public int getHighTag() {
		return m_highTag;
	}

	public void setHighTag(int highTag) throws IllegalArgumentException {
		if(highTag < 0 || highTag > 65535) {
			throw new IllegalArgumentException("Invalid high tag value: " + highTag + ", expected value between 0 and 65535.");
		}

		if(m_highTag == highTag) {
			return;
		}

		m_highTag = highTag;

		notifyTagInformationChanged();
	}

	public int getExtra() {
		return m_extra;
	}

	public void setExtra(int extra) {
		if(extra < 0 || extra > 65535) {
			throw new IllegalArgumentException("Invalid extra value: " + extra + ", expected value between 0 and 65535.");
		}

		if(m_extra == extra) {
			return;
		}

		m_extra = extra;

		notifyTagInformationChanged();
	}

	public byte[] serialize() {
		return serialize(Endianness.LittleEndian);
	}

	public byte[] serialize(Endianness endianness) {
		byte data[] = new byte[SIZE];
		int offset = 0;

		System.arraycopy(Serializer.serializeShort((short) m_lowTag, endianness), 0, data, offset, 2);
		offset += 2;

		System.arraycopy(Serializer.serializeShort((short) m_highTag, endianness), 0, data, offset, 2);
		offset += 2;

		System.arraycopy(Serializer.serializeShort((short) m_extra, endianness), 0, data, offset, 2);
		offset += 2;

		return data;
	}

	public static TagInformation deserialize(byte data[]) throws DeserializationException {
		return deserialize(data, 0);
	}

	public static TagInformation deserialize(byte data[], int offset) throws DeserializationException {
		return deserialize(data, offset, Endianness.LittleEndian);
	}

	public static TagInformation deserialize(byte data[], Endianness endianness) throws DeserializationException {
		return deserialize(data, 0, endianness);
	}

	public static TagInformation deserialize(byte data[], int offset, Endianness endianness) throws DeserializationException {
		if(data == null) {
			throw new MapDeserializationException("Invalid tag information data.");
		}

		if(offset < 0 || offset >= data.length) {
			throw new MapDeserializationException("Invalid data offset.");
		}

		// verify that the data is long enough to contain required information
		if(data.length - offset < SIZE) {
			throw new MapDeserializationException("Tag information data is incomplete or corrupted.");
		}

		// read the low tag
		int lowTag = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness) & 0xffff;
		offset += 2;

		// read the high tag
		int highTag = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness) & 0xffff;
		offset += 2;

		// read extra data
		int extra = Serializer.deserializeShort(Arrays.copyOfRange(data, offset, offset + 2), endianness) & 0xffff;
		offset += 2;

		return new TagInformation(lowTag, highTag, extra);
	}

	public JSONObject toJSONObject() {
		JSONObject tagInformation = new JSONObject();
		tagInformation.put(LOW_TAG_ATTRIBUTE_NAME, m_lowTag);
		tagInformation.put(HIGH_TAG_ATTRIBUTE_NAME, m_highTag);
		tagInformation.put(EXTRA_ATTRIBUTE_NAME, m_extra);

		return tagInformation;
	}

	public boolean addToJSONObject(JSONObject jsonObject) {
		if(jsonObject == null) {
			return false;
		}

		jsonObject.put(LOW_TAG_ATTRIBUTE_NAME, m_lowTag);
		jsonObject.put(HIGH_TAG_ATTRIBUTE_NAME, m_highTag);
		jsonObject.put(EXTRA_ATTRIBUTE_NAME, m_extra);

		return true;
	}

	public static TagInformation fromJSONObject(JSONObject tagInformation) throws IllegalArgumentException, JSONException {
		if(tagInformation == null) {
			throw new IllegalArgumentException("Tag information JSON data cannot be null.");
		}

		return new TagInformation(
			tagInformation.getInt(LOW_TAG_ATTRIBUTE_NAME),
			tagInformation.getInt(HIGH_TAG_ATTRIBUTE_NAME),
			tagInformation.getInt(EXTRA_ATTRIBUTE_NAME)
		);
	}

	public TagInformation clone() {
		return new TagInformation(m_lowTag, m_highTag, m_extra);
	}

	public int numberOfTagInformationChangeListeners() {
		return m_tagInformationChangeListeners.size();
	}
	
	public TagInformationChangeListener getTagInformationChangeListener(int index) {
		if(index < 0 || index >= m_tagInformationChangeListeners.size()) { return null; }

		return m_tagInformationChangeListeners.elementAt(index);
	}
	
	public boolean hasTagInformationChangeListener(TagInformationChangeListener c) {
		return m_tagInformationChangeListeners.contains(c);
	}
	
	public int indexOfTagInformationChangeListener(TagInformationChangeListener c) {
		return m_tagInformationChangeListeners.indexOf(c);
	}
	
	public boolean addTagInformationChangeListener(TagInformationChangeListener c) {
		if(c == null || m_tagInformationChangeListeners.contains(c)) { return false; }

		m_tagInformationChangeListeners.add(c);

		return true;
	}
	
	public boolean removeTagInformationChangeListener(int index) {
		if(index < 0 || index >= m_tagInformationChangeListeners.size()) { return false; }

		m_tagInformationChangeListeners.remove(index);

		return true;
	}
	
	public boolean removeTagInformationChangeListener(TagInformationChangeListener c) {
		if(c == null) { return false; }

		return m_tagInformationChangeListeners.remove(c);
	}
	
	public void clearTagInformationChangeListeners() {
		m_tagInformationChangeListeners.clear();
	}
	
	protected void notifyTagInformationChanged() {
		for(int i=0;i<m_tagInformationChangeListeners.size();i++) {
			m_tagInformationChangeListeners.elementAt(i).handleTagInformationChange(this);
		}
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof TagInformation)) {
			return false;
		}
		
		TagInformation t = (TagInformation) o;
		
		return m_lowTag == t.m_lowTag &&
			   m_highTag == t.m_highTag &&
			   m_extra == t.m_extra;
	}
	
	public String toString() {
		return "Low Tag: " + m_lowTag + ", High Tag: " + m_highTag + ", Extra: " + m_extra;
	}

}
