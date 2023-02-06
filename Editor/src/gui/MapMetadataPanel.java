package gui;

import java.awt.*;
import javax.swing.*;
import item.*;
import map.*;
import utilities.*;

public class MapMetadataPanel extends JPanel implements ItemChangeListener, Updatable {
	
	protected Map m_map;
	protected JLabel m_fileTypeLabel;
	protected JLabel m_versionLabel;
	protected JLabel m_typeLabel;
	protected JLabel m_playerSpawnLabel;
	protected JLabel m_numberOfSectorsLabel;
	protected JLabel m_numberOfWallsLabel;
	protected JLabel m_numberOfSpritesLabel;
	protected JLabel m_mapFileSizeLabel;

	protected JLabel m_labels[];
	
	private static final long serialVersionUID = 3942229742673801875L;
	
	public MapMetadataPanel() {
		this(null);
	}
	
	public MapMetadataPanel(Map map) {
		setLayout(new WrapLayout(WrapLayout.CENTER, 15, 10));
		setBackground(Color.BLACK);

		initComponents();

		setMap(map);
	}
	
	private void initComponents() {
		m_fileTypeLabel = new JLabel();
		m_versionLabel = new JLabel();
		m_typeLabel = new JLabel();
		m_playerSpawnLabel = new JLabel();
		m_numberOfSectorsLabel = new JLabel();
		m_numberOfWallsLabel = new JLabel();
		m_numberOfSpritesLabel = new JLabel();
		m_mapFileSizeLabel = new JLabel();
		
		m_labels = new JLabel[] {
			m_fileTypeLabel,
			m_versionLabel,
			m_typeLabel,
			m_playerSpawnLabel,
			m_numberOfSectorsLabel,
			m_numberOfWallsLabel,
			m_numberOfSpritesLabel,
			m_mapFileSizeLabel
		};

		add(m_fileTypeLabel);
		add(m_versionLabel);
		add(m_typeLabel);
		add(m_playerSpawnLabel);
		add(m_numberOfSectorsLabel);
		add(m_numberOfWallsLabel);
		add(m_numberOfSpritesLabel);
		add(m_mapFileSizeLabel);
	}
	
	public void setMap(Map map) {
		if(m_map == map) {
			return;
		}
		
		if(m_map != null) {
			m_map.removeItemChangeListener(this);
		}
		
		m_map = map;
		
		if(m_map != null) {
			m_map.addItemChangeListener(this);
		}
		
		update();
	}
	
	public void update() {
		if(m_map == null) {
			m_fileTypeLabel.setText("File Type: Unknown");
			m_versionLabel.setText("Version: N/A");
			m_typeLabel.setText("Type: N/A");
			m_playerSpawnLabel.setText("Player Spawn: N/A");
			m_numberOfSectorsLabel.setText("Number of Sectors: N/A");
			m_numberOfWallsLabel.setText("Number of Walls: N/A");
			m_numberOfSpritesLabel.setText("Number of Sprites: N/A");
			m_mapFileSizeLabel.setText("Group File Size: N/A");
		}
		else {
			m_fileTypeLabel.setText("File Type: " + m_map.getFileType());
			m_versionLabel.setText("Version: " + m_map.getVersion());
			m_typeLabel.setText("Type: " + m_map.getMapType().getDisplayName());
			m_playerSpawnLabel.setText("Player Spawn: " + m_map.getPlayerSpawnPosition());
			m_numberOfSectorsLabel.setText("Number of Sectors: " + m_map.numberOfSectors() + " / " + BuildConstants.MAX_NUMBER_OF_SECTORS);
			m_numberOfWallsLabel.setText("Number of Walls: " + m_map.numberOfWalls() + " / " + BuildConstants.MAX_NUMBER_OF_WALLS);
			m_numberOfSpritesLabel.setText("Number of Sprites: " + m_map.numberOfSprites() + " / " + BuildConstants.MAX_NUMBER_OF_SPRITES);
			m_mapFileSizeLabel.setText("Map File Size: " + m_map.getMapFileSizeString());
		}

		Color textColour = Utilities.getHighContrastYIQColor(getBackground());

		for(int i = 0; i < m_labels.length; i++) {
			m_labels[i].setForeground(textColour);
		}
	}

	public void handleItemChange(Item item) {
		if(m_map != item) {
			return;
		}
		
		update();
	}
}
