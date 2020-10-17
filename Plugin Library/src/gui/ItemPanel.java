package gui;

import java.util.*;
import java.io.*;
import javax.swing.*;
import utilities.*;
import item.*;
import exception.*;

public abstract class ItemPanel extends JPanel implements ItemChangeListener, Updatable {
	
	private static final long serialVersionUID = -2881744445153282080L;

	protected int m_itemNumber;
	protected Item m_item;
	protected Vector<UpdateListener> m_updateListeners;

	public ItemPanel() {
		this(null);
	}
	
	public ItemPanel(Item item) {
		m_itemNumber = 0;
		m_updateListeners = new Vector<UpdateListener>();
		
		setItem(item);
	}
	
	public abstract boolean init();

	public int getItemNumber() {
		return m_itemNumber;
	}
	
	public void setItemNumber(int itemNumber) {
		m_itemNumber = itemNumber;
	}

	abstract public String getTabName();
	
	abstract public String getTabDescription();
	
	public Item getItem() {
		return m_item;
	}

	public boolean setItem(Item item) {
		if(m_item != null) {
			m_item.removeItemChangeListener(this);
		}
		
		m_item = item;

		if(m_item != null) {
			m_item.addItemChangeListener(this);
		}
		
		updateLayout();

		return true;
	}
	
	abstract public String getFileExtension();
	
	public boolean isChanged() {
		return m_item == null ? false : m_item.isChanged();		
	}

	public void setChanged(boolean changed) {
		m_item.setChanged(changed);
	}

	public boolean isSameFile(File file) {
		if(file == null || m_item.getFile() == null) { return false; }
		
		File localCanonicalFile = null;
		File externalCanonicalFile = null;
		try {
			localCanonicalFile = m_item.getFile().getCanonicalFile();
			externalCanonicalFile = file.getCanonicalFile();
			
			return localCanonicalFile.equals(externalCanonicalFile);
		}
		catch(IOException e) {
			return m_item.getFile().equals(file);
		}
	}
	
	public abstract boolean save() throws ItemWriteException;

	public void handleItemChange(Item item) {
		if(item == null || m_item != item) { return; }
		
		update();
		updateWindow();
		
		notifyUpdateWindow();
	}

	public abstract void updateWindow();
	
	public abstract void updateLayout();

	public int numberOfUpdateListeners() {
		return m_updateListeners.size();
	}
	
	public UpdateListener getUpdateListener(int index) {
		if(index < 0 || index >= m_updateListeners.size()) { return null; }
		
		return m_updateListeners.elementAt(index);
	}
	
	public boolean hasUpdateListener(UpdateListener u) {
		return m_updateListeners.contains(u);
	}
	
	public int indexOfUpdateListener(UpdateListener u) {
		return m_updateListeners.indexOf(u);
	}
	
	public boolean addUpdateListener(UpdateListener u) {
		if(u == null || m_updateListeners.contains(u)) { return false; }
		
		m_updateListeners.add(u);
		
		return true;
	}
	
	public boolean removeUpdateListener(int index) {
		if(index < 0 || index >= m_updateListeners.size()) { return false; }
		
		m_updateListeners.remove(index);
		
		return true;
	}
	
	public boolean removeUpdateListener(UpdateListener u) {
		if(u == null) { return false; }
		
		return m_updateListeners.remove(u);
	}
	
	public void clearUpdateListeners() {
		m_updateListeners.clear();
	}
	
	public void notifyUpdateWindow() {
		for(int i=0;i<m_updateListeners.size();i++) {
			m_updateListeners.elementAt(i).updateWindow();
		}
	}
	
	public void notifyUpdateAll() {
		for(int i=0;i<m_updateListeners.size();i++) {
			m_updateListeners.elementAt(i).updateAll();
		}
	}
	
}
