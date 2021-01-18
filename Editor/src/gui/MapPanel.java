package gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import action.*;
import item.*;
import map.Map;
import exception.*;

public class MapPanel extends ScrollableItemPanel implements ActionListener, MouseListener {

	protected Map m_map;
	protected boolean m_initialized;
	protected Vector<MapActionListener> m_mapActionListeners;

	protected MapMetadataPanel m_mapMetadataPanel;
	protected Map2DViewPanel m_map2DViewPanel;

	protected JPopupMenu m_mapPanelPopupMenu;
	protected JMenuItem m_savePopupMenuItem;
	protected JMenuItem m_saveAsPopupMenuItem;
	protected JMenuItem m_closePopupMenuItem;
	protected JMenuItem m_canelPopupMenuItem;

	private static final long serialVersionUID = 2423398480120528793L;

	public MapPanel() {
		this(null);
	}

	public MapPanel(Item item) {
		super(item);

		if(!(item instanceof Map)) {
			throw new IllegalArgumentException("Invalid item, expected map instance!");
		}

		m_initialized = false;

		m_mapActionListeners = new Vector<MapActionListener>();

		setLayout(new BorderLayout());

		m_mapMetadataPanel = new MapMetadataPanel(m_map);
		add(m_mapMetadataPanel, BorderLayout.PAGE_START);

		m_map2DViewPanel = new Map2DViewPanel(m_map, this);
		add(m_map2DViewPanel, BorderLayout.CENTER);

		initPopupMenu();

		addMouseListener(this);
	}

	public boolean init() {
		if(m_initialized) { return true; }

		m_map2DViewPanel.init();

		m_initialized = true;

		update();

		return true;
	}

	private void initPopupMenu() {
		m_mapPanelPopupMenu = new JPopupMenu();
		
		m_savePopupMenuItem = new JMenuItem("Save");
		m_saveAsPopupMenuItem = new JMenuItem("Save As");
		m_closePopupMenuItem = new JMenuItem("Close");
		m_canelPopupMenuItem = new JMenuItem("Cancel");

		m_savePopupMenuItem.addActionListener(this);
		m_saveAsPopupMenuItem.addActionListener(this);
		m_closePopupMenuItem.addActionListener(this);
		m_canelPopupMenuItem.addActionListener(this);

		m_mapPanelPopupMenu.add(m_savePopupMenuItem);
		m_mapPanelPopupMenu.add(m_saveAsPopupMenuItem);
		m_mapPanelPopupMenu.add(m_closePopupMenuItem);
		m_mapPanelPopupMenu.addSeparator();
		m_mapPanelPopupMenu.add(m_canelPopupMenuItem);
	}

	public String getTabName() {
		String fileName = m_map.getFile() == null ? null : m_map.getFile().getName();
		return fileName == null ? "NEW " + m_map.getFileExtension() + " *" : fileName + (m_map == null || !m_map.isChanged() ? "" : " *");
	}

	public String getTabDescription() {
		String fileName = m_map.getFile() == null ? null : m_map.getFile().getName();
		return "Palette " + m_itemNumber + (fileName == null ? "" : " (" + fileName + ")");
	}

	public Map getMap() {
		return m_map;
	}

	public boolean setMap(Map map) {
		return this.setItem(map);
	}
	
	public boolean setItem(Item item) {
		if(item != null && !(item instanceof Map)) {
			return false;
		}
		
		if(!super.setItem(item)) {
			return false;
		}
		
		m_map = (Map) item;
		
		update();
		updateLayout();
		
		return true;
	}

	public String getFileExtension() {
		return m_map.getFileExtension();
	}

	public void cleanup() {
		m_map2DViewPanel.cleanup();
	}

	public boolean save() throws ItemWriteException {
		if(!m_initialized) { return false; }

		boolean saved = m_map.save();

		if(saved) {
			setChanged(false);
		}

		return saved;
	}
	
	public void update() { }
	public void updateLayout() { }

	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseMoved(MouseEvent e) { }
	public void mouseDragged(MouseEvent e) { }
	
	public void mouseReleased(MouseEvent e) {
		if(!m_initialized || m_map == null) { return; }
		
		if(e.getButton() == MouseEvent.BUTTON3) {
			showPopupMenu(MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y);
		}
	}

	public boolean isPopupMenuVisible() {
		if(!m_initialized) {
			return false;
		}

		return m_mapPanelPopupMenu.isVisible();
	}

	public void showPopupMenu(int x, int y) {
		if(!m_initialized || m_map == null) { return; }
		
		update();
		
		m_mapPanelPopupMenu.show(this, x, y);
	}

	public void actionPerformed(ActionEvent e) {
		if(m_map == null || e == null || e.getSource() == null || m_updating) { return; }

		if(e.getSource() == m_savePopupMenuItem) {
			dispatchMapAction(new MapAction(this, MapActionType.Save));
		}
		else if(e.getSource() == m_saveAsPopupMenuItem) {
			dispatchMapAction(new MapAction(this, MapActionType.SaveAs));
		}
		else if(e.getSource() == m_closePopupMenuItem) {
			dispatchMapAction(new MapAction(this, MapActionType.Close));
		}
	}

	public Dimension getPreferredSize() {
		return getParent().getSize();
	}

	public int numberOfMapActionListeners() {
		return m_mapActionListeners.size();
	}
	
	public MapActionListener getMapActionListener(int index) {
		if(index < 0 || index >= m_mapActionListeners.size()) { return null; }
		
		return m_mapActionListeners.elementAt(index);
	}
	
	public boolean hasMapActionListener(MapActionListener a) {
		return m_mapActionListeners.contains(a);
	}
	
	public int indexOfMapActionListener(MapActionListener a) {
		return m_mapActionListeners.indexOf(a);
	}
	
	public boolean addMapActionListener(MapActionListener a) {
		if(a == null || m_mapActionListeners.contains(a)) { return false; }
		
		m_mapActionListeners.add(a);
		
		return true;
	}
	
	public boolean removeMapActionListener(int index) {
		if(index < 0 || index >= m_mapActionListeners.size()) { return false; }
		
		m_mapActionListeners.remove(index);
		
		return true;
	}
	
	public boolean removeMapActionListener(MapActionListener a) {
		if(a == null) { return false; }
		
		return m_mapActionListeners.remove(a);
	}
	
	public void clearMapActionListeners() {
		m_mapActionListeners.clear();
	}
	
	public void dispatchMapAction(MapAction action) {
		if(!MapAction.isvalid(action)) { return; }
		
		for(int i=0;i<m_mapActionListeners.size();i++) {
			m_mapActionListeners.elementAt(i).handleMapAction(action);
		}
	}

}
