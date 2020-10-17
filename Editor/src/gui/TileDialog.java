package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import art.*;
import utilities.*;

public class TileDialog extends JDialog implements TileChangeListener, Updatable, ActionListener {

	private Tile m_originalTile;
	private Tile m_editedTile;
	private String m_title;
	private ImageIcon m_tileImageIcon;
	private JLabel m_tileLabel;
	private boolean m_unsavedChanges;
	private boolean m_savedChanges;
	private Frame m_parent;
	private JPanel m_mainPanel;
	private TileMetadataPanel m_tileMetadataPanel;
	private JButton m_newButton;
	private JButton m_resizeButton;
	private JButton m_clearButton;
	private JButton m_replaceButton;
	private JButton m_extractButton;
	private JButton m_saveButton;
	private JButton m_closeButton;
	private Dimension m_dimensions;
	private Dimension m_previousDimensions;
	
	private JPanel m_optionsPanel;
	private int m_zoomLevel;
	private JComboBox m_zoomLevelComboBox;
	private JButton m_tileBackgroundColourButton;
	private JCheckBox m_transparentTileBackgroundCheckBox;
	protected TileResizeDialog m_tileResizeDialog;

// TODO: opening tile which uses alternate sub-palette with different lookup.dat file works correctly in main UI but uses wrong sub-palette in dialog

/* TODO: finish tile dialog implementation:
//title with tile number
//change cancel button to close
//confirm discarding changes when cancelling
//save button // this should be disabled if there are no changes
auto-crop button
options
 + checkbox for transparent background that persists as setting (add to settings menu as well)
 + add dropdown for magnification level
 + alignment crosshair with toggle button
 + resize direction buttons (ie. photoshop when cropping / expanding)
//labels for metadata
// + tile number
// + dimensions
// + number of bytes
buttons for tile actions:
// + clear tile // TODO: disable when empty
 + replace tile
 + extract tile
tile attributes:
 + XOffset,
 + YOffset,
 + NumberOfFrames,
 + AnimationType, // dropdown
 + AnimationSpeed,
 + Extra 
hook up dynamic layout calculating
allow interactive pixel painting, colour selector, palette colour chooser paint bucket and colour replacement tools
add tooltips for labels and input elements
*/

	private static final long serialVersionUID = 7954856979085049451L;
	
	public TileDialog(Frame parent) {
		super(parent, true);

		setResizable(false);
// TODO: disable default layout manager
//		setLayout(null);
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});

		getRootPane().registerKeyboardAction(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					close();
				}
			},
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
			JComponent.WHEN_IN_FOCUSED_WINDOW
		);

		m_tileResizeDialog = new TileResizeDialog(parent);
		
		m_parent = parent;
		m_unsavedChanges = false;
		m_savedChanges = false;
		m_dimensions = new Dimension(0, 0);
		
		initComponents();
		updateLayout();
	}
	
	private void initComponents() {
		m_mainPanel = new JPanel();
		
		m_tileImageIcon = new ImageIcon(Tile.getEmptyImage());
		m_tileLabel = new JLabel(m_tileImageIcon);
		
		m_tileMetadataPanel = new TileMetadataPanel();

		m_newButton = new JButton("New");
		m_newButton.addActionListener(this);

		m_resizeButton = new JButton("Resize");
		m_resizeButton.addActionListener(this);

		m_clearButton = new JButton("Clear");
		m_clearButton.addActionListener(this);

		m_replaceButton = new JButton("Replace");
		m_replaceButton.addActionListener(this);

		m_extractButton = new JButton("Extract");
		m_extractButton.addActionListener(this);
		
		m_saveButton = new JButton("Save");
		m_saveButton.addActionListener(this);
		
		m_closeButton = new JButton("Close");
		m_closeButton.addActionListener(this);
		
		m_mainPanel.add(m_tileLabel);
		m_mainPanel.add(m_tileMetadataPanel);
		m_mainPanel.add(m_newButton);
		m_mainPanel.add(m_resizeButton);
		m_mainPanel.add(m_clearButton);
		m_mainPanel.add(m_replaceButton);
		m_mainPanel.add(m_extractButton);
		m_mainPanel.add(m_saveButton);
		m_mainPanel.add(m_closeButton);
		
		setContentPane(m_mainPanel);
	}
	
	private void setTile(Tile tile) {
		if(m_originalTile == tile) {
			return;
		}
		
		m_unsavedChanges = false;

		if(m_editedTile != null) {
			m_editedTile.removeTileChangeListener(this);
		}

		m_originalTile = tile;
		m_editedTile = tile == null ? null : tile.clone(false, false);

		if(m_editedTile != null) {
			m_editedTile.addTileChangeListener(this);
		}

		m_tileMetadataPanel.setTile(m_editedTile);
		
		update();
	}
	
	public void setTitle(String title) {
		m_title = title;

		updateTitle();
	}
	
	private void updateTitle() {
		super.setTitle(m_title + (m_unsavedChanges ? " *" : ""));
	}
	
	public void update() {
		setTitle(m_editedTile == null ? "" : m_editedTile.getDisplayName() + (m_editedTile.hasName() ? " '" + m_editedTile.getName() + "'" : ""));

		m_tileMetadataPanel.update();
		
		boolean isEmpty = m_editedTile == null || m_editedTile.isEmpty();
		
		m_tileImageIcon.setImage(m_editedTile == null || m_editedTile.getImage() ==  null ? Tile.getEmptyImage() : m_editedTile.getImage());
		m_resizeButton.setEnabled(!isEmpty);
		m_clearButton.setEnabled(!isEmpty);
		m_extractButton.setEnabled(!isEmpty);
		m_saveButton.setEnabled(m_unsavedChanges);
		
		updateLayout();
	}
	
	public boolean userSavedChanges() {
		return m_savedChanges;
	}
	
	private void setUnsavedChanges() {
		m_unsavedChanges = true;
		
		update();
	}

	public void newTilePrompt() {
// TODO: prompt for dimensions, and then initialize image 
	}

	public void resizeTilePrompt() {
		m_tileResizeDialog.display();
	}

	public void clearTile() {
		m_editedTile.clear();
	}

	private void promptReplaceTile() {
// TODO: add prompt to replace tile in tile dialog
	}
	
	private void promptExtractTile() {
// TODO: add prompt to extract tile in tile dialog
	}
	
	private void saveTile() {
		if(!m_unsavedChanges) {
			return;
		}
		
		m_originalTile.apply(m_editedTile);
		
		m_unsavedChanges = false;
		m_savedChanges = true;
		
		update();
	}
	
	public void close() {
		if(m_unsavedChanges) {
			int choice = JOptionPane.showConfirmDialog(m_parent, "Would you like to save your changes?", "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(choice == JOptionPane.CANCEL_OPTION) { return; }
			else if(choice == JOptionPane.YES_OPTION) {
				saveTile();
			}
		}
		
		clear();

		setVisible(false);
	}
	
	public void clear() {
		setTile(null);
	}
	
	public void display(Tile tile) {
		if(tile == null) { return; }
		
		m_savedChanges = false;
		
		setTile(tile);
		setVisible(true);
	}
	
	public void updateLayout() {
// TODO: update tile dialog layout
		
		m_previousDimensions = m_dimensions;
		
		m_dimensions = new Dimension(1024, 768);

		setSize(m_dimensions);
		
		if(!m_dimensions.equals(m_previousDimensions)) {
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			
			setLocation((d.width / 2) - (m_dimensions.width / 2), (d.height / 2) - (m_dimensions.height / 2));
		}
	}

	public void handleTileChange(Tile tile) {
		setUnsavedChanges();
	}

	public void handleTileAttributeChange(TileAttributes attributes, TileAttribute attribute, byte value) { }

	public Dimension getPreferredSize() {
		return m_dimensions;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == m_newButton) {
			newTilePrompt();
		}
		else if(e.getSource() == m_resizeButton) {
			resizeTilePrompt();
		}
		else if(e.getSource() == m_clearButton) {
			clearTile();
		}
		else if(e.getSource() == m_replaceButton) {
			promptReplaceTile();
		}
		else if(e.getSource() == m_extractButton) {
			promptExtractTile();
		}
		else if(e.getSource() == m_saveButton) {
			saveTile();
		}
		else if(e.getSource() == m_closeButton) {
			close();
		}
	}
	
}
