package gui;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import item.*;
import map.*;
import map.Map;

public class Map2DCamera implements ItemChangeListener {

	protected int m_x;
	protected int m_y;
	protected int m_zoomLevel;
	protected Map m_map;
	protected Vector<Map2DCameraChangeListener> m_map2DCameraChangeListeners;

	public static final int DEFAULT_ZOOM_LEVEL = 6;
	public static final int MIN_ZOOM_LEVEL = 1;
	public static final int MAX_ZOOM_LEVEL = 128;

	public Map2DCamera() {
		this(null);
	}

	public Map2DCamera(Map map) {
		m_x = 0;
		m_y = 0;
		m_zoomLevel = DEFAULT_ZOOM_LEVEL;
		m_map2DCameraChangeListeners = new Vector<Map2DCameraChangeListener>();

		setMap(map);
	}

	public int getX() {
		return m_x;
	}

	public void setX(int x) {
		if(m_x == x) {
			return;
		}

		m_x = x;

		notifyMap2DCameraChanged();
	}

	public void translateX(int x) {
		setX(m_x + x);
	}

	public int getY() {
		return m_y;
	}

	public void setY(int y) {
		if(m_y == y) {
			return;
		}

		m_y = y;

		notifyMap2DCameraChanged();
	}

	public void translateY(int y) {
		setY(m_y + y);
	}

	public void setPosition(Position p) {
		if(p == null) {
			return;
		}

		setPosition(p.getX(), p.getY());
	}

	public void setPosition(Point p) {
		if(p == null) {
			return;
		}

		setPosition(p.x, p.y);
	}

	public void setPosition(int x, int y) {
		setX(x);
		setY(y);
	}

	public void translate(Point p) {
		if(p == null) {
			return;
		}

		translate(p.x, p.y);
	}

	public void translate(int x, int y) {
		translateX(x);
		translateY(y);
	}

	public int getZoomLevel() {
		return m_zoomLevel;
	}

	public double getZoom() {
		return 1.0 * (m_zoomLevel / (double) (MAX_ZOOM_LEVEL));
	}

	public void setZoomLevel(int zoomLevel) throws IllegalArgumentException {
		if(m_zoomLevel < MIN_ZOOM_LEVEL || m_zoomLevel > MAX_ZOOM_LEVEL) {
			throw new IllegalArgumentException("Invalid zoom level, expected integer between " + MIN_ZOOM_LEVEL + " and " + MAX_ZOOM_LEVEL + ", inclusively.");
		}

		if(m_zoomLevel == zoomLevel) {
			return;
		}

		double previousZoom = getZoom();
		double x = m_x / previousZoom;
		double y = m_y / previousZoom;

		m_zoomLevel = zoomLevel;

		double newZoom = getZoom();
		setPosition((int) (x * newZoom), (int) (y * newZoom));

		notifyMap2DCameraChanged();
	}

	public boolean zoomIn() {
		if(m_zoomLevel == MAX_ZOOM_LEVEL) {
			return false;
		}

		setZoomLevel(m_zoomLevel + 1);

		return true;
	}

	public boolean zoomOut() {
		if(m_zoomLevel == MIN_ZOOM_LEVEL) {
			return false;
		}

		setZoomLevel(m_zoomLevel - 1);

		return true;
	}

	public Map getMap() {
		return m_map;
	}

	public void setMap(Map map) {
		if(m_map != null) {
			m_map.removeItemChangeListener(this);
		}

		m_map = map;

		if(m_map != null) {
			double zoom = getZoom();

			m_x = (int) (-m_map.getPlayerSpawnX() * zoom);
			m_y = (int) (-m_map.getPlayerSpawnY() * zoom);
		}

		if(m_map != null) {
			m_map.addItemChangeListener(this);
		}
	}

	public int numberOfMap2DCameraChangeListeners() {
		return m_map2DCameraChangeListeners.size();
	}
	
	public Map2DCameraChangeListener getMap2DCameraChangeListener(int index) {
		if(index < 0 || index >= m_map2DCameraChangeListeners.size()) { return null; }

		return m_map2DCameraChangeListeners.elementAt(index);
	}
	
	public boolean hasMap2DCameraChangeListener(Map2DCameraChangeListener c) {
		return m_map2DCameraChangeListeners.contains(c);
	}
	
	public int indexOfMap2DCameraChangeListener(Map2DCameraChangeListener c) {
		return m_map2DCameraChangeListeners.indexOf(c);
	}
	
	public boolean addMap2DCameraChangeListener(Map2DCameraChangeListener c) {
		if(c == null || m_map2DCameraChangeListeners.contains(c)) { return false; }

		m_map2DCameraChangeListeners.add(c);

		return true;
	}
	
	public boolean removeMap2DCameraChangeListener(int index) {
		if(index < 0 || index >= m_map2DCameraChangeListeners.size()) { return false; }

		m_map2DCameraChangeListeners.remove(index);

		return true;
	}
	
	public boolean removeMap2DCameraChangeListener(Map2DCameraChangeListener c) {
		if(c == null) { return false; }

		return m_map2DCameraChangeListeners.remove(c);
	}
	
	public void clearMap2DCameraChangeListeners() {
		m_map2DCameraChangeListeners.clear();
	}
	
	protected void notifyMap2DCameraChanged() {
		for(int i=0;i<m_map2DCameraChangeListeners.size();i++) {
			m_map2DCameraChangeListeners.elementAt(i).handleMap2DCameraChange(this);
		}
	}

	public void handleItemChange(Item item) {
		
	}

	public void updateGraphics(Graphics2D g, JComponent target) {
		if(g == null) {
			return;
		}

		g.translate(m_x, m_y);
		g.translate((target.getWidth() / 2), (target.getHeight() / 2));
	}

}
