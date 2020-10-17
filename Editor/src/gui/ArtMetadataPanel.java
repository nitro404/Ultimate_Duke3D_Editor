package gui;

import java.awt.*;
import javax.swing.*;
import item.*;
import art.*;
import utilities.*;

public class ArtMetadataPanel extends JPanel implements ItemChangeListener, Updatable {
	
	protected Art m_art;
	protected Dimension m_dimensions;
	protected JLabel m_artNumberLabel;
	protected JLabel m_numberOfTilesLabel;
	protected JLabel m_numberOfNonEmptyTilesLabel;
	protected JLabel m_numberOfEmptyTilesLabel;
	protected JLabel m_localTileStartLabel;
	protected JLabel m_localTileEndLabel;
	protected JLabel m_legacyTileCountLabel;

	protected JLabel m_labels[];
	
	private static final long serialVersionUID = 3942229742673801875L;
	
	public ArtMetadataPanel() {
		this(null);
	}
	
	public ArtMetadataPanel(Art art) {
		m_dimensions = new Dimension(0, 0);
		
		setLayout(null);
		setOpaque(false);

		initComponents();

		setArt(art);
	}
	
	private void initComponents() {
		m_artNumberLabel = new JLabel();
		m_numberOfTilesLabel = new JLabel();
		m_numberOfNonEmptyTilesLabel = new JLabel();
		m_numberOfEmptyTilesLabel = new JLabel();
		m_localTileStartLabel = new JLabel();
		m_localTileEndLabel = new JLabel();
		m_legacyTileCountLabel = new JLabel();
		
		m_labels = new JLabel[] {
			m_artNumberLabel,
			m_numberOfTilesLabel,
			m_numberOfNonEmptyTilesLabel,
			m_numberOfEmptyTilesLabel,
			m_localTileStartLabel,
			m_localTileEndLabel,
			m_legacyTileCountLabel
		};

		add(m_artNumberLabel);
		add(m_numberOfTilesLabel);
		add(m_numberOfNonEmptyTilesLabel);
		add(m_numberOfEmptyTilesLabel);
		add(m_localTileStartLabel);
		add(m_localTileEndLabel);
		add(m_legacyTileCountLabel);
		
		update();
	}
	
	public void setArt(Art art) {
		if(m_art == art) {
			return;
		}
		
		if(m_art != null) {
			m_art.removeItemChangeListener(this);
		}
		
		m_art = art;
		
		if(m_art != null) {
			m_art.addItemChangeListener(this);
		}
		
		update();
	}
	
	public void update() {
		m_artNumberLabel.setText("Art Number: " + (m_art == null ? "N/A" : m_art.getNumber()));
		m_numberOfTilesLabel.setText("Number of Tiles: " + (m_art == null ? "N/A" : m_art.numberOfTiles()));
		m_numberOfNonEmptyTilesLabel.setText("Number of Non-Empty Tiles: " + (m_art == null ? "N/A" : m_art.numberOfNonEmptyTiles()));
		m_numberOfEmptyTilesLabel.setText("Number of Empty Tiles: " + (m_art == null ? "N/A" : m_art.numberOfEmptyTiles()));
		m_localTileStartLabel.setText("Local Tile Start: " + (m_art == null ? "N/A" : m_art.getLocalTileStart()));
		m_localTileEndLabel.setText("Local Tile End: " + (m_art == null ? "N/A" : m_art.getLocalTileEnd()));
		m_legacyTileCountLabel.setText("Legacy Tile Count: " + (m_art == null ? "N/A" : m_art.getLegacyTileCount()));
		
		Component parent = getParent();

		if(parent != null) {
			Color textColour = Utilities.getHighContrastYIQColor(parent.getBackground());

			for(int i = 0; i < m_labels.length; i++) {
				m_labels[i].setForeground(textColour);
			}
		}
		
		updateLayout();
	}
	
	public void updateLayout() {
		updateLayout(0);
	}
	
	public void updateLayout(int width) {
		Component parent = getParent();
		JLabel label = null;
		int parentWidth = parent == null ? 0 : parent.getWidth();
		int labelSpacing = 8;
		int padding = 10;
		int x = padding;
		int y = padding;
		int maxHeight = 0;

		if(parentWidth == 0) {
			parentWidth = width;
		}

		for(int i = 0; i < m_labels.length; i++) {
			label = m_labels[i];
			
			label.setSize(label.getPreferredSize());

			if(x + labelSpacing + label.getWidth() > parentWidth) {
				x = padding;
				y += labelSpacing + maxHeight;
				maxHeight = 0;
			}
			
			label.setLocation(x, y);
			
			if(label.getHeight() > maxHeight) {
				maxHeight = label.getHeight();
			}
			
			x += labelSpacing + label.getWidth();
		}
		
		m_dimensions = new Dimension(parentWidth + padding, y + maxHeight + padding);
		setSize(m_dimensions); 
		
		revalidate();
	}

	public void handleItemChange(Item item) {
		if(m_art != item) {
			return;
		}
		
		update();
	}

	public Dimension getPreferredSize() {
		return m_dimensions;
	}
	
}
