package item;

import java.util.*;
import org.json.*;

public abstract class ItemAttributes {

	protected Vector<ItemAttributeChangeListener> m_itemAttributeChangeListeners;

	public ItemAttributes() {
		m_itemAttributeChangeListeners = new Vector<ItemAttributeChangeListener>();
	}

	public abstract byte numberOfBytes();

	public abstract ItemAttribute[] getAttributes();

	public byte getAttributeValue(String name) throws IllegalArgumentException {
		return getAttributeValue(getItemAttribute(name));
	}

	public abstract byte getAttributeValue(ItemAttribute attribute) throws IllegalArgumentException;

	public boolean setAttributeValue(String name, byte value) throws IllegalArgumentException {
		return setAttributeValue(getItemAttribute(name), value);
	}

	public abstract boolean setAttributeValue(ItemAttribute attribute, byte value) throws IllegalArgumentException;

	public abstract void setAttributes(Number packedValue) throws IllegalArgumentException;

	public void setAttributes(ItemAttributes attributes) throws IllegalArgumentException {
		setAttributes(attributes, false);
	}

	public abstract void setAttributes(ItemAttributes attributes, boolean reassignItemChangeListeners) throws IllegalArgumentException;

	public abstract Number pack();

	public boolean isValidItemAttribute(ItemAttribute a) {
		if(a == null) {
			return false;
		}

		ItemAttribute[] attributes = getAttributes();

		for(int i = 0; i < attributes.length; i++) {
			if(a == attributes[i]) {
				return true;
			}
		}

		return false;
	}

	public ItemAttribute getItemAttribute(String name) {
		String formattedName = name.trim();

		if(formattedName.isEmpty()) {
			return null;
		}

		ItemAttribute[] attributes = getAttributes();

		for(int i = 0; i < attributes.length; i++) {
			if(formattedName.equalsIgnoreCase(attributes[i].getDisplayName()) ||
			   formattedName.equalsIgnoreCase(attributes[i].getAttributeName())) {
				return attributes[i];
			}
		}

		return null;
	}

	public JSONObject toJSONObject() {
		JSONObject itemAttributes = new JSONObject();
		ItemAttribute attribute = null;
		ItemAttribute[] attributes = getAttributes();

		for(int i = 0; i < attributes.length; i++) {
			attribute = attributes[i];

			itemAttributes.put(attribute.getAttributeName(), getAttributeValue(attribute));
		}

		return itemAttributes;
	}

	public ItemAttributes clone() {
		return clone(false);
	}

	public abstract ItemAttributes clone(boolean reassignItemChangeListeners);

	public abstract void reset();

	public boolean equals(Object o) {
		if(o == null || !(o instanceof ItemAttributes)) {
			return false;
		}

		ItemAttributes a = (ItemAttributes) o;

		return pack().equals(a.pack());
	}

	public String toString() {
		ItemAttribute attribute = null;
		String itemAttributeString = "Item Attributes (";

		ItemAttribute[] attributes = getAttributes();

		for(int i = 0; i < attributes.length; i++) {
			attribute = attributes[i];

			if(i != 0) {
				itemAttributeString += ", ";
			}

			itemAttributeString += attribute.getDisplayName() + ": ";
			itemAttributeString += getAttributeValue(attribute);
		}

		itemAttributeString += ")";

		return itemAttributeString;
	}

	public int numberOfItemAttributeChangeListeners() {
		return m_itemAttributeChangeListeners.size();
	}

	public ItemAttributeChangeListener getItemAttributeChangeListener(int index) {
		if(index < 0 || index >= m_itemAttributeChangeListeners.size()) { return null; }
		
		return m_itemAttributeChangeListeners.elementAt(index);
	}

	public boolean hasItemAttributeChangeListener(ItemAttributeChangeListener c) {
		return m_itemAttributeChangeListeners.contains(c);
	}

	public int indexOfItemAttributeChangeListener(ItemAttributeChangeListener c) {
		return m_itemAttributeChangeListeners.indexOf(c);
	}

	public boolean addItemAttributeChangeListener(ItemAttributeChangeListener c) {
		if(c == null || m_itemAttributeChangeListeners.contains(c)) { return false; }
		
		m_itemAttributeChangeListeners.add(c);
		
		return true;
	}

	public boolean removeItemAttributeChangeListener(int index) {
		if(index < 0 || index >= m_itemAttributeChangeListeners.size()) { return false; }
		
		m_itemAttributeChangeListeners.remove(index);
		
		return true;
	}

	public boolean removeItemAttributeChangeListener(ItemAttributeChangeListener c) {
		if(c == null) { return false; }
		
		return m_itemAttributeChangeListeners.remove(c);
	}

	public void clearItemAttributeChangeListeners() {
		m_itemAttributeChangeListeners.clear();
	}

	public void notifyItemAttributeChanged(ItemAttribute attribute, byte value) {
		for(int i=0;i<m_itemAttributeChangeListeners.size();i++) {
			m_itemAttributeChangeListeners.elementAt(i).handleItemAttributeChange(this, attribute, value);
		}
	}

}
