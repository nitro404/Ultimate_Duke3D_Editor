package gui;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import utilities.*;
import art.*;
import console.*;

public class TileButton extends JButton implements TileChangeListener, Updatable {
	
	protected int m_imageScale;
	protected Tile m_tile;
	protected Color m_pressedColour;
	
	private static final long serialVersionUID = -5095632619676123826L;
	
	public TileButton(Tile tile) throws IllegalArgumentException {
		setBorder(BorderFactory.createEmptyBorder());
		setBackground(Color.MAGENTA);
		setContentAreaFilled(false);
		
		m_imageScale = 1;
		
		setTile(tile);
	}
	
	public Tile getTile() {
		return m_tile;
	}
	
	public void setTile(Tile tile) throws IllegalArgumentException {
		if(tile == null) { throw new IllegalArgumentException("Invalid tile argument."); }
		
		if(m_tile != null) {
			m_tile.removeTileChangeListener(this);
		}
		
		m_tile = tile;
		
		m_tile.addTileChangeListener(this);
		
		update();
	}

	public Color getPressedColour() {
		return m_pressedColour;
	}
	
	public void setPressedColour(Color c) {
		m_pressedColour = c;
	}

	public void clearPressedColour() {
		setPressedColour(null);
	}
	
// TODO: add customization for auto image scaling and flag to turn it off?
	public void updateImageScale() {
		int minSize = Math.min(m_tile.getWidth(), m_tile.getHeight());
		int maxSize = Math.max(m_tile.getWidth(), m_tile.getHeight());

		if(maxSize <= 8) {
			m_imageScale = 10;
		}
		else if(maxSize <= 16) {
			m_imageScale = 8;
		}
		else if(maxSize <= 24) {
			m_imageScale = 6;
		}
		else if(maxSize <= 32) {
			m_imageScale = 4;
		}
		else if(maxSize <= 64) {
			m_imageScale = 3;
		}
		else if(maxSize <= 128) {
			m_imageScale = 2;
		}
		
		if(m_imageScale == 1) {
			if(minSize <= 32) {
				m_imageScale = 2;
			}
		}
	}
	
	public void update() {
		String tooltipText = "TILE " + Utilities.addLeadingZeroes(m_tile.getNumber(), 4);
		
		String tileName = TileNames.DEFAULT_TILE_NAMES.getTileName(m_tile.getNumber());
		
		if(Utilities.isNonEmptyString(tileName, false)) {
			tooltipText += " '" +  tileName + "'";
		}
		
		if(m_tile.isEmpty()) {
			setPreferredSize(new Dimension(Tile.EMPTY_IMAGE_SIZE, Tile.EMPTY_IMAGE_SIZE));
			setSize(Tile.EMPTY_IMAGE_SIZE, Tile.EMPTY_IMAGE_SIZE);
			setIcon(new ImageIcon(Tile.getEmptyImage()));
			
			tooltipText += " (Empty)";
		}
		else {
// TODO: maybe render tile # on UI in addition to tooltip?
			BufferedImage image = m_tile.getImage(true);
			
			if(image != null) {
				updateImageScale();
				
				int width = m_tile.getWidth() * m_imageScale;
				int height = m_tile.getHeight() * m_imageScale;
				
				setPreferredSize(new Dimension(width, height));
				setSize(getPreferredSize());
				setIcon(new ImageIcon(m_imageScale == 1 ? image : image.getScaledInstance(width, height, java.awt.Image.SCALE_REPLICATE)));

				tooltipText += " (" + m_tile.getWidth() + " x " + m_tile.getHeight() + " @ " + m_imageScale + "x)";
			}
			else {
				SystemConsole.instance.writeLine("Failed to obtain tile image!");
				
				tooltipText += " (INVALID)";
			}
		}

		setToolTipText(tooltipText);
	}
	
	protected void paintComponent(Graphics g) {
		if(m_pressedColour != null & getModel().isPressed()) {
			g.setColor(m_pressedColour);
		}
		else {
			g.setColor(getBackground());
		}
		
		g.fillRect(0, 0, getWidth(), getHeight());
		
		super.paintComponent(g);
	}

	public void handleTileChange(Tile tile) {
		update();
	}

	public void handleTileAttributeChange(TileAttributes attributes, TileAttribute attribute, byte value) { }

}
