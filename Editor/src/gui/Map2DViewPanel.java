package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import item.*;
import map.Map;

public class Map2DViewPanel extends JPanel implements ItemChangeListener, Map2DCameraChangeListener, MouseListener, MouseWheelListener, MouseMotionListener {

	protected Map m_map;
	protected Map2DCamera m_2dCamera;
	protected MapPanel m_parent;
	protected Point m_previousMouseDragLocation;
	protected boolean m_dirty;
	protected Thread m_drawThread;
	protected boolean m_drawThreadRunning;
	protected boolean m_initialized;
	protected boolean m_readOnlyWarningShown;

	protected static final long FRAME_RATE = 60L;

	private static final long serialVersionUID = 5905860946222642464L;

	public Map2DViewPanel() {
		this(null);
	}

	public Map2DViewPanel(Map map) {
		this(map, null);
	}

	public Map2DViewPanel(Map map, MapPanel parent) {
		m_parent = parent;
		m_previousMouseDragLocation = null;
		m_initialized = false;
		m_readOnlyWarningShown = false;

		m_2dCamera = new Map2DCamera();
		m_2dCamera.addMap2DCameraChangeListener(this);

		setLayout(null);
		addMouseListener(this);
		addMouseWheelListener(this);
		addMouseMotionListener(this);

		setMap(map);
	}

	public void init() {
		if(m_initialized) {
			return;
		}

		m_drawThreadRunning = false;

		m_drawThread = new Thread(new Runnable() {
			public void run() {
				m_drawThreadRunning = true;
				
				while(m_drawThreadRunning) {
					if(m_dirty) {
						m_dirty = false;

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								repaint();
							}
						});
					}

					try { Thread.sleep(1000L / FRAME_RATE); }
					catch(InterruptedException e) { return; }
				}
			}
		});

		m_drawThread.start();

		m_initialized = true;
	}

	public void cleanup() {
		m_initialized = false;
		m_drawThreadRunning = false;

		try { m_drawThread.interrupt(); } catch(Exception e) { }
	}

	public Map getMap() {
		return m_map;
	}

	public boolean setMap(Map map) {
		if(m_map != null) {
			m_map.removeItemChangeListener(this);
		}

		m_map = (Map) map;
		m_map.addItemChangeListener(this);

		m_2dCamera.setMap(m_map);

		m_dirty = true;
		
		return true;
	}

	public MapPanel getParent() {
		return m_parent;
	}

	public void setParent(MapPanel parent) {
		m_parent = parent;
	}

	public String getFileExtension() {
		return m_map.getFileExtension();
	}

	protected void paintComponent(Graphics g) {
		if(!(g instanceof Graphics2D)) {
			return;
		}

		paintComponent((Graphics2D) g);
	}

	protected void paintComponent(Graphics2D g) {
		if(g == null) {
			return;
		}

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());

		m_2dCamera.updateGraphics(g, this);

		if(m_map != null) {
			m_map.paintMap(g, this, m_2dCamera.getZoom());
		}
	}

	public void mouseClicked(MouseEvent e) { }

	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON2) {
			m_previousMouseDragLocation = e.getPoint();
		}
	}

	public void mouseReleased(MouseEvent e) {
		if(m_map == null) { return; }

		if(e.getButton() == MouseEvent.BUTTON1) {
			if(!m_readOnlyWarningShown) {
				m_readOnlyWarningShown = true;

				JOptionPane.showMessageDialog(SwingUtilities.getRoot(this), "Map interface is read only, editing not currently supported!", "Read Only", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if(e.getButton() == MouseEvent.BUTTON2) {
			m_previousMouseDragLocation = null;
		}
		else if(e.getButton() == MouseEvent.BUTTON3) {
			if(m_parent != null) {
				m_parent.showPopupMenu(getLocation().x + MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x, getLocation().y + MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y);
			}
		}
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
			if(e.getWheelRotation() > 0) {
				m_2dCamera.zoomOut();
			}
			else {
				m_2dCamera.zoomIn();
			}
		}
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseMoved(MouseEvent e) { }

	public void mouseDragged(MouseEvent e) {
		if(m_previousMouseDragLocation != null) {
			m_2dCamera.translate(e.getX() - m_previousMouseDragLocation.x, e.getY() - m_previousMouseDragLocation.y);

			m_previousMouseDragLocation = e.getPoint();
		}
	}
	
	public void handleItemChange(Item item) {
		m_dirty = true;
	}

	public void handleMap2DCameraChange(Map2DCamera camera) {
		m_dirty = true;
	}

}
