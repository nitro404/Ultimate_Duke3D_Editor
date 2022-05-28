package gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import exception.*;
import action.*;
import item.*;
import art.*;
import editor.*;

public class ArtPanel extends ScrollableItemPanel implements ArtChangeListener, ActionListener, MouseListener {
	
	protected Art m_art;
	protected ArtMetadataPanel m_artMetadataPanel;
	protected TileButton m_activeTileButton;
	protected TileButton m_buttons[];
	protected Dimension m_dimensions;
	protected boolean m_changed;
	protected Vector<ArtActionListener> m_artActionListeners;
	protected boolean m_initialized;
	protected TileDialog m_tileDialog;
	
	protected JPopupMenu m_artPanelPopupMenu;
	protected JMenuItem m_savePopupMenuItem;
	protected JMenuItem m_saveAsPopupMenuItem;
	protected JMenuItem m_clearTilePopupMenuItem;
	protected JMenuItem m_swapTilesPopupMenuItem;
	protected JMenuItem m_replaceTilePopupMenuItem;
	protected JMenuItem m_importTilesPopupMenuItem;
	protected JMenuItem m_extractTilePopupMenuItem;
	protected JMenuItem m_extractTilesPopupMenuItem;
	protected JMenuItem m_extractAllTilesPopupMenuItem;
	protected JMenuItem m_setNumberPopupMenuItem;
	protected JMenuItem m_setNumberOfTilesPopupMenuItem;
	protected JMenuItem m_setLegacyTileCountPopupMenuItem;
	protected JMenuItem m_closePopupMenuItem;
	protected JMenuItem m_canelPopupMenuItem;

	private static final long serialVersionUID = -4733283252942137007L;

	public ArtPanel() {
		this(null);
	}
	
	public ArtPanel(Item item) {
		super(item);

		if(!(item instanceof Art)) {
			throw new IllegalArgumentException("Invalid item, expected art instance!");
		}

		m_initialized = false;
		
		setLayout(null);
		
		m_dimensions = new Dimension(0, 0);
		m_changed = false;
		m_artActionListeners = new Vector<ArtActionListener>();
		m_tileDialog = new TileDialog((JFrame) SwingUtilities.getWindowAncestor(this));
		
		initPopupMenu();
		
		addMouseListener(this);
	}
	
	public boolean init() {
		if(m_initialized) { return true; }
		
		initComponents();
		
		m_initialized = true;
		
		update();
		
// TODO: temporary debugging:
//m_tileDialog.display(m_art.getTileByNumber(2524));
		
		return true;
	}
	
	private void initComponents() {
		m_artMetadataPanel = new ArtMetadataPanel(m_art);
		
		add(m_artMetadataPanel);
	}
	
	private void initPopupMenu() {
		m_artPanelPopupMenu = new JPopupMenu();
		
		m_savePopupMenuItem = new JMenuItem("Save");
		m_saveAsPopupMenuItem = new JMenuItem("Save As");
		m_clearTilePopupMenuItem = new JMenuItem("Clear Tile");
		m_swapTilesPopupMenuItem = new JMenuItem("Swap Tiles");
		m_replaceTilePopupMenuItem = new JMenuItem("Replace Tile");
		m_importTilesPopupMenuItem = new JMenuItem("Import Tiles");
		m_extractTilePopupMenuItem = new JMenuItem("Extract Tile");
		m_extractTilesPopupMenuItem = new JMenuItem("Extract Tiles");
		m_extractAllTilesPopupMenuItem = new JMenuItem("Extract All Tiles");
		m_setNumberPopupMenuItem = new JMenuItem("Set Art Number");
		m_setNumberOfTilesPopupMenuItem = new JMenuItem("Set Number of Tiles");
		m_setLegacyTileCountPopupMenuItem = new JMenuItem("Set Legacy Tile Count");
		m_closePopupMenuItem = new JMenuItem("Close");
		m_canelPopupMenuItem = new JMenuItem("Cancel");
		
		m_savePopupMenuItem.addActionListener(this);
		m_saveAsPopupMenuItem.addActionListener(this);
		m_clearTilePopupMenuItem.addActionListener(this);
		m_swapTilesPopupMenuItem.addActionListener(this);
		m_replaceTilePopupMenuItem.addActionListener(this);
		m_importTilesPopupMenuItem.addActionListener(this);
		m_extractTilePopupMenuItem.addActionListener(this);
		m_extractTilesPopupMenuItem.addActionListener(this);
		m_extractAllTilesPopupMenuItem.addActionListener(this);
		m_setNumberPopupMenuItem.addActionListener(this);
		m_setNumberOfTilesPopupMenuItem.addActionListener(this);
		m_setLegacyTileCountPopupMenuItem.addActionListener(this);
		m_closePopupMenuItem.addActionListener(this);
		m_canelPopupMenuItem.addActionListener(this);
		
		m_artPanelPopupMenu.add(m_savePopupMenuItem);
		m_artPanelPopupMenu.add(m_saveAsPopupMenuItem);
		m_artPanelPopupMenu.add(m_clearTilePopupMenuItem);
		m_artPanelPopupMenu.add(m_swapTilesPopupMenuItem);
		m_artPanelPopupMenu.add(m_replaceTilePopupMenuItem);
		m_artPanelPopupMenu.add(m_importTilesPopupMenuItem);
		m_artPanelPopupMenu.add(m_extractTilePopupMenuItem);
		m_artPanelPopupMenu.add(m_extractTilesPopupMenuItem);
		m_artPanelPopupMenu.add(m_extractAllTilesPopupMenuItem);
		m_artPanelPopupMenu.add(m_setNumberPopupMenuItem);
		m_artPanelPopupMenu.add(m_setNumberOfTilesPopupMenuItem);
		m_artPanelPopupMenu.add(m_setLegacyTileCountPopupMenuItem);
		m_artPanelPopupMenu.add(m_closePopupMenuItem);
		m_artPanelPopupMenu.addSeparator();
		m_artPanelPopupMenu.add(m_canelPopupMenuItem);
	}

	public String getTabName() {
		String fileName = m_art.getFile() == null ? null : m_art.getFile().getName();
		return fileName == null ? "NEW " + m_art.getFileExtension() + " *" : fileName + (m_changed ? " *" : "");
	}
	
	public String getTabDescription() {
		String fileName = m_art.getFile() == null ? null : m_art.getFile().getName();
		return "Art " + m_itemNumber + (fileName == null ? "" : " (" + fileName + ")");
	}
	
	public Art getArt() {
		return m_art;
	}
	
	public String getFileExtension() {
		return m_art.getFileExtension();
	}
	
	public void setTileButtonPressedColour(Color c) {
		for(int i = 0; i < m_buttons.length; i++) {
			m_buttons[i].setPressedColour(c);
		}
	}

	public void setTileButtonBackgroundColour(Color c) {
		for(int i = 0; i < m_buttons.length; i++) {
			m_buttons[i].setBackground(c);
		}
	}
	
	public int numberOfArtActionListeners() {
		return m_artActionListeners.size();
	}
	
	public ArtActionListener getArtActionListener(int index) {
		if(index < 0 || index >= m_artActionListeners.size()) { return null; }
		
		return m_artActionListeners.elementAt(index);
	}
	
	public boolean hasArtActionListener(ArtActionListener a) {
		return m_artActionListeners.contains(a);
	}
	
	public int indexOfArtActionListener(ArtActionListener a) {
		return m_artActionListeners.indexOf(a);
	}
	
	public boolean addArtActionListener(ArtActionListener a) {
		if(a == null || m_artActionListeners.contains(a)) { return false; }
		
		m_artActionListeners.add(a);
		
		return true;
	}
	
	public boolean removeArtActionListener(int index) {
		if(index < 0 || index >= m_artActionListeners.size()) { return false; }
		
		m_artActionListeners.remove(index);
		
		return true;
	}
	
	public boolean removeArtActionListener(ArtActionListener a) {
		if(a == null) { return false; }
		
		return m_artActionListeners.remove(a);
	}
	
	public void clearArtActionListeners() {
		m_artActionListeners.clear();
	}
	
	public void dispatchArtAction(ArtAction action) {
		if(!ArtAction.isvalid(action)) { return; }
		
		for(int i=0;i<m_artActionListeners.size();i++) {
			m_artActionListeners.elementAt(i).handleArtAction(action);
		}
	}

	public boolean setArt(Art art) {
		return setItem(art);
	}
	
	public boolean setItem(Item item) {
		if(item != null && !(item instanceof Art)) {
			return false;
		}
		
		if(!super.setItem(item)) {
			return false;
		}
		
		if(m_art != null) {
			m_art.removeArtChangeListener(this);
		}
		
		m_art = (Art) item;

		if(m_art != null) {
			m_art.addArtChangeListener(this);
		}
		
		if(m_artMetadataPanel != null) {
			m_artMetadataPanel.setArt(m_art);
		}
		
		update();
		updateTileButtons();
		
		return true;
	}
	
	public void updateTileButtons() {
		if(m_buttons != null) {
			for(int i=0;i<m_buttons.length;i++) {
				m_buttons[i].removeActionListener(this);
				remove(m_buttons[i]);
			}

			m_buttons = null;
		}
		
		if(m_art != null) {
			m_buttons = new TileButton[m_art.numberOfTiles()];
			
			for(int i = 0; i < m_art.numberOfTiles(); i++) {
				m_buttons[i] = new TileButton(m_art.getTileByIndex(i));
				m_buttons[i].addActionListener(this);
				m_buttons[i].addMouseListener(this);
				add(m_buttons[i]);
			}
		}
		
		updateLayout();
	}

	public void cleanup() { }

	public boolean save() throws ItemWriteException {
		if(!m_initialized) { return false; }

		boolean saved = m_art.save();
		if(saved) { setChanged(false); }
		return saved;
	}
	
	public void update() {
		if(!m_initialized) { return; }
		
		setBackground(Editor.settings.backgroundColour);
		
		m_artMetadataPanel.update();
		
		repaint();
		revalidate();
	}
	
	public void updateLayout() {
		if(!m_initialized) { return; }
		
		setBackground(Editor.settings.backgroundColour);
		
		Component parent = getParent();
		TileButton button = null;
		int parentWidth = parent == null ? 0 : parent.getWidth();
		int parentHeight = parent == null ? 0 : parent.getHeight();
		int buttonSpacing = 2;
		int x = 0;
		int y = 0;
		int maxHeight = 0;

		m_artMetadataPanel.updateLayout(parentWidth);
		m_artMetadataPanel.setLocation(x, y);
		
		y += m_artMetadataPanel.getHeight();
		
		for(int i = 0; i < m_buttons.length; i++) {
			button = m_buttons[i];

			if(x + buttonSpacing + button.getWidth() > parentWidth) {
				x = 0;
				y += buttonSpacing + maxHeight;
				maxHeight = 0;
			}
			
			button.setLocation(x, y);
			
			if(button.getHeight() > maxHeight) {
				maxHeight = button.getHeight();
			}
			
			x += buttonSpacing + button.getWidth();
		}
		
		m_dimensions = new Dimension(parentWidth, Math.max(parentHeight, y + maxHeight));
		setSize(m_dimensions);

		revalidate();
	}
	
	public void handleNumberOfTilesChanged(Art art) {
		if(m_art != art) {
			return;
		}
		
		updateTileButtons();
	}

	public void actionPerformed(ActionEvent e) {
		if(!m_initialized || m_updating || e == null || e.getSource() == null) { return; }
		
// TODO: add copy & paste options (and dynamically expand / shrink canvas if applicable)
		if(e.getSource() instanceof TileButton) {
			m_tileDialog.display(((TileButton) e.getSource()).getTile());
		}
		else if(e.getSource() == m_savePopupMenuItem) {
			dispatchArtAction(new ArtAction(this, m_activeTileButton, ArtActionType.Save));
		}
		else if(e.getSource() == m_saveAsPopupMenuItem) {
			dispatchArtAction(new ArtAction(this, m_activeTileButton, ArtActionType.SaveAs));
		}
		else if(e.getSource() == m_clearTilePopupMenuItem) {
			dispatchArtAction(new ArtAction(this, m_activeTileButton, ArtActionType.ClearTile));
		}
		else if(e.getSource() == m_swapTilesPopupMenuItem) {
			dispatchArtAction(new ArtAction(this, m_activeTileButton, ArtActionType.SwapTiles));
		}
		else if(e.getSource() == m_replaceTilePopupMenuItem) {
			dispatchArtAction(new ArtAction(this, m_activeTileButton, ArtActionType.ReplaceTile));
		}
		else if(e.getSource() == m_importTilesPopupMenuItem) {
			dispatchArtAction(new ArtAction(this, m_activeTileButton, ArtActionType.ImportTiles));
		}
		else if(e.getSource() == m_extractTilePopupMenuItem) {
			dispatchArtAction(new ArtAction(this, m_activeTileButton, ArtActionType.ExtractTile));
		}
		else if(e.getSource() == m_extractTilesPopupMenuItem) {
			dispatchArtAction(new ArtAction(this, m_activeTileButton, ArtActionType.ExtractTiles));
		}
		else if(e.getSource() == m_extractAllTilesPopupMenuItem) {
			dispatchArtAction(new ArtAction(this, m_activeTileButton, ArtActionType.ExtractAllTiles));
		}
		else if(e.getSource() == m_setNumberPopupMenuItem) {
			dispatchArtAction(new ArtAction(this, m_activeTileButton, ArtActionType.SetNumber));
		}
		else if(e.getSource() == m_setNumberOfTilesPopupMenuItem) {
			dispatchArtAction(new ArtAction(this, m_activeTileButton, ArtActionType.SetNumberOfTiles));
		}
		else if(e.getSource() == m_setLegacyTileCountPopupMenuItem) {
			dispatchArtAction(new ArtAction(this, m_activeTileButton, ArtActionType.SetLegacyTileCount));
		}
		else if(e.getSource() == m_closePopupMenuItem) {
			dispatchArtAction(new ArtAction(this, m_activeTileButton, ArtActionType.Close));
		}
		
		m_activeTileButton =  null;
	}
	
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseMoved(MouseEvent e) { }
	public void mouseDragged(MouseEvent e) { }
	
	public void mouseReleased(MouseEvent e) {
		if(!m_initialized) { return; }
		
		if(e.getButton() == MouseEvent.BUTTON3) {
			TileButton tileButton = (e.getSource() instanceof TileButton) ? (TileButton) e.getSource() : null;
			
			m_activeTileButton = tileButton;
			
			m_clearTilePopupMenuItem.setEnabled(m_activeTileButton != null && !m_activeTileButton.getTile().isEmpty());
			m_replaceTilePopupMenuItem.setEnabled(m_activeTileButton != null);
			m_extractTilePopupMenuItem.setEnabled(m_activeTileButton != null && !m_activeTileButton.getTile().isEmpty());
			m_extractAllTilesPopupMenuItem.setEnabled(m_art.hasNonEmptyTile());
			
			m_artPanelPopupMenu.show(m_activeTileButton == null ? this : m_activeTileButton, e.getX(), e.getY());
		}
	}
	
	public Dimension getPreferredSize() {
		return m_dimensions;
	}
	
}
