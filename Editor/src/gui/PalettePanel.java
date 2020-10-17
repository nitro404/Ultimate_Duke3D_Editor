package gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import exception.*;
import settings.*;
import action.*;
import item.*;
import palette.*;
import editor.*;

public class PalettePanel extends ScrollableItemPanel implements ActionListener, MouseListener {
	
	protected Palette m_palette;
	protected PaletteMetadataPanel m_paletteMetadataPanel;
	protected PixelButton m_buttons[];
	protected Dimension m_dimensions;
	protected boolean m_changed;
	protected PixelButton m_activePixelButton;
	protected JPanel m_controlsPanel;
	protected JButton m_saveButton;
	protected JButton m_saveAsButton;
	protected JButton m_importButton;
	protected JButton m_exportButton;
	protected JButton m_closeButton;
	protected Vector<PaletteActionListener> m_paletteActionListeners;
	protected boolean m_initialized;
	
	public static final int PALETTE_SPACING = SettingsManager.defaultPaletteSpacing;
	
	protected JPopupMenu m_palettePanelPopupMenu;
	protected JMenuItem m_savePopupMenuItem;
	protected JMenuItem m_saveAsPopupMenuItem;
	protected JMenuItem m_importPopupMenuItem;
	protected JMenuItem m_exportPopupMenuItem;
	protected JMenuItem m_closePopupMenuItem;
	protected JMenuItem m_canelPopupMenuItem;
	
	private static final long serialVersionUID = -7026896833349413736L;

	public PalettePanel() {
		this(null);
	}
	
	public PalettePanel(Item item) {
		super(item);

		if(!(item instanceof Palette)) {
			throw new IllegalArgumentException("Invalid item, expected palette instance!");
		}

		m_initialized = false;
		
		setLayout(null);
		setBackground(SettingsManager.defaultBackgroundColour);

		m_paletteMetadataPanel = new PaletteMetadataPanel((Palette) item);
		add(m_paletteMetadataPanel);

		m_controlsPanel = new JPanel();
		m_controlsPanel.setLayout(new WrapLayout());
		add(m_controlsPanel);

		m_saveButton = new JButton("Save");
		m_saveButton.addActionListener(this);
		m_controlsPanel.add(m_saveButton);

		m_saveAsButton = new JButton("Save As");
		m_saveAsButton.addActionListener(this);
		m_controlsPanel.add(m_saveAsButton);

		m_importButton = new JButton("Import");
		m_importButton.addActionListener(this);
		m_controlsPanel.add(m_importButton);

		m_exportButton = new JButton("Export");
		m_exportButton.addActionListener(this);
		m_controlsPanel.add(m_exportButton);

		m_closeButton = new JButton("Close");
		m_closeButton.addActionListener(this);
		m_controlsPanel.add(m_closeButton);

		m_dimensions = new Dimension(Palette.PALETTE_WIDTH * PixelButton.BUTTON_SIZE, Palette.PALETTE_HEIGHT * PixelButton.BUTTON_SIZE);
		m_changed = false;
		m_paletteActionListeners = new Vector<PaletteActionListener>();
		
		initPopupMenu();
		
		addMouseListener(this);
	}
	
	public boolean init() {
		if(m_initialized) { return true; }
		
		m_initialized = true;

		update();
		
		return true;
	}
	
	private void initPopupMenu() {
		m_palettePanelPopupMenu = new JPopupMenu();
		
		m_savePopupMenuItem = new JMenuItem("Save");
		m_saveAsPopupMenuItem = new JMenuItem("Save As");
		m_importPopupMenuItem = new JMenuItem("Import");
		m_exportPopupMenuItem = new JMenuItem("Export");
		m_closePopupMenuItem = new JMenuItem("Close");
		m_canelPopupMenuItem = new JMenuItem("Cancel");
		
		m_savePopupMenuItem.addActionListener(this);
		m_saveAsPopupMenuItem.addActionListener(this);
		m_importPopupMenuItem.addActionListener(this);
		m_exportPopupMenuItem.addActionListener(this);
		m_closePopupMenuItem.addActionListener(this);
		m_canelPopupMenuItem.addActionListener(this);
		
		m_palettePanelPopupMenu.add(m_savePopupMenuItem);
		m_palettePanelPopupMenu.add(m_saveAsPopupMenuItem);
		m_palettePanelPopupMenu.add(m_importPopupMenuItem);
		m_palettePanelPopupMenu.add(m_exportPopupMenuItem);
		m_palettePanelPopupMenu.add(m_closePopupMenuItem);
		m_palettePanelPopupMenu.addSeparator();
		m_palettePanelPopupMenu.add(m_canelPopupMenuItem);
	}

	public String getTabName() {
		String fileName = m_palette.getFile() == null ? null : m_palette.getFile().getName();
		return fileName == null ? "NEW " + m_palette.getFileExtension() + " *" : fileName + (m_changed ? " *" : "");
	}
	
	public String getTabDescription() {
		String fileName = m_palette.getFile() == null ? null : m_palette.getFile().getName();
		return "Palette " + m_itemNumber + (fileName == null ? "" : " (" + fileName + ")");
	}
	
	public Palette getPalette() {
		return m_palette;
	}
	
	public String getFileExtension() {
		return m_palette.getFileExtension();
	}
	
	public int numberOfPaletteActionListeners() {
		return m_paletteActionListeners.size();
	}
	
	public PaletteActionListener getPaletteActionListener(int index) {
		if(index < 0 || index >= m_paletteActionListeners.size()) { return null; }
		
		return m_paletteActionListeners.elementAt(index);
	}
	
	public boolean hasPaletteActionListener(PaletteActionListener a) {
		return m_paletteActionListeners.contains(a);
	}
	
	public int indexOfPaletteActionListener(PaletteActionListener a) {
		return m_paletteActionListeners.indexOf(a);
	}
	
	public boolean addPaletteActionListener(PaletteActionListener a) {
		if(a == null || m_paletteActionListeners.contains(a)) { return false; }
		
		m_paletteActionListeners.add(a);
		
		return true;
	}
	
	public boolean removePaletteActionListener(int index) {
		if(index < 0 || index >= m_paletteActionListeners.size()) { return false; }
		
		m_paletteActionListeners.remove(index);
		
		return true;
	}
	
	public boolean removePaletteActionListener(PaletteActionListener a) {
		if(a == null) { return false; }
		
		return m_paletteActionListeners.remove(a);
	}
	
	public void clearPaletteActionListeners() {
		m_paletteActionListeners.clear();
	}
	
	public void dispatchPaletteAction(PaletteAction action) {
		if(!PaletteAction.isvalid(action)) { return; }
		
		for(int i=0;i<m_paletteActionListeners.size();i++) {
			m_paletteActionListeners.elementAt(i).handlePaletteAction(action);
		}
	}

	public boolean setPalette(Palette palette) {
		return this.setItem(palette);
	}
	
	public boolean setItem(Item item) {
		if(item != null && !(item instanceof Palette)) {
			return false;
		}
		
		if(!super.setItem(item)) {
			return false;
		}
		
		m_palette = (Palette) item;
		
		if(m_buttons != null) {
			for(int i=0;i<m_buttons.length;i++) {
				m_buttons[i].removeActionListener(this);
				m_buttons[i].removeMouseListener(this);
				remove(m_buttons[i]);
			}
			m_buttons = null;
		}
		
		if(m_palette != null) {
			int numberOfPixels = Palette.NUMBER_OF_COLOURS * m_palette.numberOfPalettes();
			int pixelIndex = 0;
			m_buttons = new PixelButton[numberOfPixels];
			for(int p=0;p<m_palette.numberOfPalettes();p++) {
				for(int j=0;j<Palette.PALETTE_HEIGHT;j++) {
					for(int i=0;i<Palette.PALETTE_WIDTH;i++) {
						pixelIndex = (p * Palette.NUMBER_OF_COLOURS) + (j * Palette.PALETTE_WIDTH) + i;
						m_buttons[pixelIndex] = new PixelButton(m_palette.getPixel(i, j, p), i, j, p);
						m_buttons[pixelIndex].addActionListener(this);
						m_buttons[pixelIndex].addMouseListener(this);
						add(m_buttons[pixelIndex]);
					}
				}
			}
		}
		
		update();
		updateLayout();
		
		return true;
	}
	
	public boolean updatePaletteData() {
		if(!m_initialized) { return false; }
		
		int numberOfPalettes = m_palette.numberOfPalettes();
		Color colourData[] = new Color[Palette.NUMBER_OF_COLOURS * numberOfPalettes];
		int pixelIndex = 0;
		for(int p=0;p<numberOfPalettes;p++) {
			for(int j=0;j<Palette.PALETTE_HEIGHT;j++) {
				for(int i=0;i<Palette.PALETTE_WIDTH;i++) {
					pixelIndex = (p * Palette.NUMBER_OF_COLOURS) + (j * Palette.PALETTE_WIDTH) + i;
					colourData[pixelIndex] = m_buttons[pixelIndex].getBackground();
				}
			}
		}
		
		return m_palette.updateAllColourData(colourData);
	}
	
	public boolean updatePixelButtons() {
		if(!m_initialized) { return false; }
		
		for(int p=0;p<m_palette.numberOfPalettes();p++) {
			for(int j=0;j<Palette.PALETTE_HEIGHT;j++) {
				for(int i=0;i<Palette.PALETTE_WIDTH;i++) {
					m_buttons[(p * Palette.NUMBER_OF_COLOURS) + (j * Palette.PALETTE_WIDTH) + i].setBackground(m_palette.getPixel(i, j, p));
				}
			}
		}
		
		return true;
	}

	public void cleanup() { }

	public boolean save() throws ItemWriteException {
		return save(true);
	}
	
	public boolean save(boolean update) throws ItemWriteException {
		if(!m_initialized) { return false; }
		if(update && !updatePaletteData()) { return false; }
		boolean saved = m_palette.save();
		if(saved) { setChanged(false); }
		return saved;
	}
	
	public void update() {
		if(!m_initialized || m_updating) { return; }
		
		m_updating = true;
		
		setBackground(Editor.settings.backgroundColour);
		
		m_paletteMetadataPanel.update();
		
		repaint();
		revalidate();
		
		m_updating = false;
	}
	
	public void updateLayout() {
		if(!m_initialized) { return; }
		
		Component parent = getParent();
		int parentWidth = parent == null ? 0 : parent.getWidth();
		int parentHeight = parent == null ? 0 : parent.getHeight();
		int buttonSize = Editor.settings.pixelButtonSize;
		int paletteSpacing = Editor.settings.paletteSpacing;
		int paletteWidth = Palette.PALETTE_WIDTH * buttonSize;
		int paletteHeight = Palette.PALETTE_HEIGHT * buttonSize;
		int numberOfHorizontalPalettes = 1 + (parent == null ? 1 : (int) (Math.floor((float) (parentWidth - paletteWidth) / (float) (paletteWidth + paletteSpacing))));
		int numberOfVerticalPalettes = (int) Math.ceil((float) m_palette.numberOfPalettes() / (float) numberOfHorizontalPalettes);
		int newWidth = paletteWidth + (m_palette.numberOfPalettes() > 1 ? paletteWidth + paletteSpacing : 0);
		int newHeight = (numberOfVerticalPalettes * paletteHeight) + ((numberOfVerticalPalettes - 1) * paletteSpacing);
		m_dimensions = new Dimension(parentWidth > newWidth ? parentWidth : newWidth, parentHeight > newHeight ? parentHeight : newHeight);
		int numberOfPalettes = m_palette.numberOfPalettes();
		int paletteSize = Palette.PALETTE_WIDTH * Palette.PALETTE_HEIGHT;
		int horizontalSpacing = (Palette.PALETTE_WIDTH * buttonSize) + paletteSpacing;
		int verticalSpacing = (Palette.PALETTE_HEIGHT * buttonSize) + paletteSpacing;
		int paletteColumn = 0;
		int paletteRow = 0;
		int x = 0;
		int y = 0;
		int pixelIndex = 0;

		int paletteMetadataPanelHeight = m_paletteMetadataPanel.getPreferredSize().height;
		
		m_paletteMetadataPanel.setLocation(x, y);
		m_paletteMetadataPanel.setSize(parentWidth, paletteMetadataPanelHeight);
		
		y += paletteMetadataPanelHeight;
		
		for(int p=0;p<numberOfPalettes;p++) {
			for(int j=0;j<Palette.PALETTE_HEIGHT;j++) {
				for(int i=0;i<Palette.PALETTE_WIDTH;i++) {
					pixelIndex = (p * paletteSize) + (j * Palette.PALETTE_WIDTH) + i;
					m_buttons[pixelIndex].setLocation(x + (i * buttonSize) + (paletteColumn * horizontalSpacing), y + (j * buttonSize) + (paletteRow * verticalSpacing));
					m_buttons[pixelIndex].setPreferredSize(new Dimension(buttonSize, buttonSize));
					m_buttons[pixelIndex].setSize(buttonSize, buttonSize);
					m_buttons[pixelIndex].setBackground(m_palette.getPixel(i, j, p));
				}
			}
			
			if(paletteColumn >= numberOfHorizontalPalettes - 1) {
				paletteRow++;
				paletteColumn = 0;
			}
			else {
				paletteColumn++;
			}
		}

		int controlsPanelHeight = m_controlsPanel.getPreferredSize().height;
		
		m_controlsPanel.setSize(parentWidth, controlsPanelHeight);
		m_controlsPanel.setLocation(0, parentHeight - controlsPanelHeight);
		
		revalidate();
	}

	public void actionPerformed(ActionEvent e) {
		if(!m_initialized || m_updating || e == null || e.getSource() == null) { return; }
		
		if(e.getSource() instanceof PixelButton) {
			PixelButton pixelButton = (PixelButton) e.getSource();
			if(pixelButton.chooseColour()) {
				m_palette.updatePixel(pixelButton.getPixelX(), pixelButton.getPixelY(), pixelButton.getBackground(), pixelButton.getPaletteIndex());
				
				setChanged(true);
			}
		}
		else if(e.getSource() == m_saveButton || e.getSource() == m_savePopupMenuItem) {
			dispatchPaletteAction(new PaletteAction(this, PaletteActionType.Save));
		}
		else if(e.getSource() == m_saveAsButton || e.getSource() == m_saveAsPopupMenuItem) {
			dispatchPaletteAction(new PaletteAction(this, PaletteActionType.SaveAs));
		}
		else if(e.getSource() == m_importButton || e.getSource() == m_importPopupMenuItem) {
			dispatchPaletteAction(new PaletteAction(this, PaletteActionType.Import));
		}
		else if(e.getSource() == m_exportButton || e.getSource() == m_exportPopupMenuItem) {
			dispatchPaletteAction(new PaletteAction(this, PaletteActionType.Export));
		}
		else if(e.getSource() == m_closeButton || e.getSource() == m_closePopupMenuItem) {
			dispatchPaletteAction(new PaletteAction(this, PaletteActionType.Close));
		}
		
		m_activePixelButton = null;
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
			PixelButton pixelButton = (e.getSource() instanceof PixelButton) ? (PixelButton) e.getSource() : null;

			m_activePixelButton = pixelButton;

			m_palettePanelPopupMenu.show(m_activePixelButton == null ? this : m_activePixelButton, e.getX(), e.getY());
		}
	}
	
	public Dimension getPreferredSize() {
		return m_dimensions;
	}
	
}
