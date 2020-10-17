package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import art.*;
import utilities.*;

public class TileMetadataPanel extends JPanel implements Updatable, TileChangeListener, ActionListener, DocumentListener {
	
	protected Tile m_tile;
	protected boolean m_updating;
	protected boolean m_updatingUsingDocument;
	protected Dimension m_dimensions;

	protected JLabel m_tileNumberLabel;
	protected JLabel m_tileNumberValueLabel;
	protected JLabel m_tileNameLabel;
	protected JLabel m_tileNameValueLabel;
	protected JLabel m_numberOfBytesLabel;
	protected JLabel m_numberOfBytesValueLabel;
	protected JLabel m_dimensionsLabel;
	protected JLabel m_dimensionsValueLabel;
	protected JLabel m_offsetLabel;
	protected JLabel m_offsetSeparatorLabel;
	protected JTextField m_xOffsetTextField;
	protected JTextField m_yOffsetTextField;
	protected JLabel m_numberOfFramesLabel;
	protected JTextField m_numberOfFramesTextField;
	protected JLabel m_animationTypeLabel;
	protected JComboBox<String> m_animationTypeComboBox;
	protected JLabel m_animationSpeedLabel;
	protected JTextField m_animationSpeedTextField;
	protected JLabel m_extraLabel;
	protected JTextField m_extraTextField;
	protected Border m_defaultTextFieldBorder;
	protected static Border invalidTextFieldBorder = BorderFactory.createLineBorder(Color.RED);

	protected JLabel m_mainLabels[];
	protected JLabel m_allLabels[];
	protected JLabel m_separatorLabels[];
	protected Component m_mainComponents[];
	protected Component m_allComponents[];
	protected JTextField m_byteAttributeTextFields[];
	
	private static final long serialVersionUID = 3942229742673801875L;
	
	public TileMetadataPanel() {
		m_updating = false;
		m_updatingUsingDocument = false;
		m_dimensions = new Dimension(0, 0);
		
		setLayout(null);
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		initComponents();
		
		update();
	}
	
	private void initComponents() {
		m_tileNumberLabel = new JLabel("Tile Number:");
		add(m_tileNumberLabel);
		
		m_tileNumberValueLabel = new JLabel();
		add(m_tileNumberValueLabel);

		m_tileNameLabel = new JLabel("Tile Name:");
		add(m_tileNameLabel);
		
		m_tileNameValueLabel = new JLabel();
		add(m_tileNameValueLabel);

		m_numberOfBytesLabel = new JLabel("Total Bytes:");
		add(m_numberOfBytesLabel);
		
		m_numberOfBytesValueLabel = new JLabel();
		add(m_numberOfBytesValueLabel);

		m_dimensionsLabel = new JLabel("Dimensions:");
		add(m_dimensionsLabel);

		m_dimensionsValueLabel = new JLabel();
		add(m_dimensionsValueLabel);

		m_offsetLabel = new JLabel("X / Y Offset:");
		add(m_offsetLabel);
		
		m_offsetSeparatorLabel = new JLabel(",");
		add(m_offsetSeparatorLabel);

		m_xOffsetTextField = new JTextField();
		m_xOffsetTextField.getDocument().addDocumentListener(this);
		add(m_xOffsetTextField);

		m_yOffsetTextField = new JTextField();
		m_yOffsetTextField.getDocument().addDocumentListener(this);
		add(m_yOffsetTextField);

		m_numberOfFramesLabel = new JLabel("Number of Frames:");
		add(m_numberOfFramesLabel);
		
		m_numberOfFramesTextField = new JTextField();
		m_numberOfFramesTextField.getDocument().addDocumentListener(this);
		add(m_numberOfFramesTextField);

		m_animationTypeLabel = new JLabel("Animation Type:");
		add(m_animationTypeLabel);
		
		m_animationTypeComboBox = new JComboBox<String>();
		m_animationTypeComboBox = new JComboBox<String>(TileAnimationType.getValidDisplayNames());
		m_animationTypeComboBox.setSelectedIndex(TileAnimationType.defaultAnimationType.ordinal());
		m_animationTypeComboBox.addActionListener(this);
		add(m_animationTypeComboBox);

		m_animationSpeedLabel = new JLabel("Animation Speed:");
		add(m_animationSpeedLabel);

		m_animationSpeedTextField = new JTextField();
		m_animationSpeedTextField.getDocument().addDocumentListener(this);
		add(m_animationSpeedTextField);

		m_extraLabel = new JLabel("Extra:");
		add(m_extraLabel);
		
		m_extraTextField = new JTextField();
		m_extraTextField.getDocument().addDocumentListener(this);
		add(m_extraTextField);
		
		m_mainLabels = new JLabel[] {
			m_tileNumberLabel,
			m_tileNameLabel,
			m_numberOfBytesLabel,
			m_dimensionsLabel,
			m_offsetLabel,
			m_numberOfFramesLabel,
			m_animationTypeLabel,
			m_animationSpeedLabel,
			m_extraLabel
		};
		
		m_allLabels = new JLabel[] {
			m_tileNumberLabel,
			m_tileNameLabel,
			m_numberOfBytesLabel,
			m_dimensionsLabel,
			m_offsetLabel,
			m_offsetSeparatorLabel,
			m_numberOfFramesLabel,
			m_animationTypeLabel,
			m_animationSpeedLabel,
			m_extraLabel,
			m_tileNumberValueLabel,
			m_tileNameValueLabel,
			m_numberOfBytesValueLabel
		};
		
		m_separatorLabels = new JLabel[] {
			m_offsetSeparatorLabel
		};

		m_mainComponents = new Component[] {
			m_tileNumberValueLabel,
			m_tileNameValueLabel,
			m_numberOfBytesValueLabel,
			m_dimensionsValueLabel,
			m_xOffsetTextField,
			m_numberOfFramesTextField,
			m_animationTypeComboBox,
			m_animationSpeedTextField,
			m_extraTextField
		};
		
		m_allComponents = new Component[] {
			m_tileNumberValueLabel,
			m_tileNameValueLabel,
			m_numberOfBytesValueLabel,
			m_dimensionsValueLabel,
			m_xOffsetTextField,
			m_yOffsetTextField,
			m_numberOfFramesTextField,
			m_animationTypeComboBox,
			m_animationSpeedTextField,
			m_extraTextField
		};

		m_byteAttributeTextFields = new JTextField[] {
			m_xOffsetTextField,
			m_yOffsetTextField,
			m_numberOfFramesTextField,
			m_animationSpeedTextField,
			m_extraTextField
		};

		for(int i = 0; i < m_mainLabels.length; i++) {
			m_mainLabels[i].setHorizontalAlignment(SwingConstants.RIGHT);
		}

		for(int i = 0; i < m_separatorLabels.length; i++) {
			m_separatorLabels[i].setVerticalAlignment(SwingConstants.BOTTOM);
		}

		update();
	}
	
	public void setTile(Tile tile) {
		if(m_tile == tile) {
			return;
		}
		
		if(m_tile != null) {
			m_tile.removeTileChangeListener(this);
		}
		
		m_tile = tile;
		
		if(m_tile != null) {
			m_tile.addTileChangeListener(this);
		}
		
		update();
	}

	protected void updateTileMetadataUsingDocument(Document document) {
		if(m_updatingUsingDocument || document == null) {
			return;
		}
		
		m_updatingUsingDocument = true;

		TileAttribute byteAttribute = null;

		TileAttribute byteAttributes[] = new TileAttribute[] {
			TileAttributes.XOffset,
			TileAttributes.YOffset,
			TileAttributes.NumberOfFrames,
			TileAttributes.AnimationSpeed,
			TileAttributes.Extra
		};
		
		JTextField byteAttributeTextField = null;

		for(int i = 0; i < m_byteAttributeTextFields.length; i++) {
			byteAttributeTextField = m_byteAttributeTextFields[i];
			
			if(document == byteAttributeTextField.getDocument()) {
				byteAttribute = byteAttributes[i];
				
				boolean validAttributeValue = false;
	
				try {
					validAttributeValue = m_tile.getAttributes().setAttributeValue(byteAttribute, Byte.parseByte(document.getText(0, document.getLength())));
				}
				catch(NumberFormatException e) { }
				catch(BadLocationException e) { }
				
				if(m_defaultTextFieldBorder == null) {
					m_defaultTextFieldBorder = byteAttributeTextField.getBorder();
				}
				
				if(validAttributeValue) {
					byteAttributeTextField.setBorder(m_defaultTextFieldBorder);
				}
				else {
					byteAttributeTextField.setBorder(invalidTextFieldBorder);
				}
				
				break;
			}
		}

		m_updatingUsingDocument = false;
	}

	public void update() {
		if(m_updating || m_updatingUsingDocument) {
			return;
		}
		
		m_updating = true;
		
		Component parent = getParent();

		if(parent != null) {
			Color textColour = Utilities.getHighContrastYIQColor(parent.getBackground());

			for(int i = 0; i < m_allLabels.length; i++) {
				m_allLabels[i].setForeground(textColour);
			}
		}
		
		if(m_tile == null) {
			m_tileNumberValueLabel.setText("N/A");
			m_tileNameValueLabel.setText("N/A");
			m_numberOfBytesValueLabel.setText("N/A");
			m_dimensionsValueLabel.setText("0 x 0");
			m_xOffsetTextField.setText("0");
			m_yOffsetTextField.setText("0");
			m_numberOfFramesTextField.setText("0");
			m_animationTypeComboBox.setSelectedIndex(TileAnimationType.defaultAnimationType.ordinal());
			m_animationSpeedTextField.setText("0");
			m_extraTextField.setText("0");

			if(m_defaultTextFieldBorder != null) {
				for(int i = 0; i < m_byteAttributeTextFields.length; i++) {
					m_byteAttributeTextFields[i].setBorder(m_defaultTextFieldBorder);
				}
			}
		}
		else {
			m_tileNumberValueLabel.setText(Integer.toString(m_tile.getNumber()));
			m_tileNameValueLabel.setText(m_tile.hasName() ? m_tile.getName() : "N/A");
			m_numberOfBytesValueLabel.setText(Integer.toString(m_tile.getSize()));
			m_dimensionsValueLabel.setText(Integer.toString(m_tile.getWidth()) + " x " + Integer.toString(m_tile.getHeight()));
			m_xOffsetTextField.setText(Integer.toString(m_tile.getAttributes().getXOffset()));
			m_yOffsetTextField.setText(Integer.toString(m_tile.getAttributes().getYOffset()));
			m_numberOfFramesTextField.setText(Integer.toString(m_tile.getAttributes().getNumberOfFrames()));
			m_animationTypeComboBox.setSelectedIndex(m_tile.getAttributes().getAnimationType().ordinal());
			m_animationSpeedTextField.setText(Integer.toString(m_tile.getAttributes().getAnimationSpeed()));
			m_extraTextField.setText(Integer.toString(m_tile.getAttributes().getExtra()));
		}
		
		updateLayout();
		
		m_updating = false;
	}
	
	public void updateLayout() {
		JLabel label = null;
		Component component = null;
		int spacing = 4;
		int padding = 8;
		int x = padding;
		int y = padding;
		Dimension preferredLabelSize = null;
		Dimension preferredComponentSize = null;
		int maxLabelWidth = 0;
		int maxLabelHeight = 0;
		int maxHeight = 0;
		int maxComponentHeight = 0;
		int currentWidth = 0;
		int maxInputWidth = 0;
		int totalMaxWidth = 0;
		int separatorWidth = 12;
		int componentWidth = 80;

		for(int i = 0; i < m_mainLabels.length; i++) {
			label = m_mainLabels[i];
			component = m_mainComponents[i];
			
			preferredLabelSize = label.getPreferredSize();
			preferredComponentSize = component.getPreferredSize();

			if(preferredLabelSize.width > maxLabelWidth) {
				maxLabelWidth = preferredLabelSize.width;
			}

			if(preferredLabelSize.height > maxLabelHeight) {
				maxLabelHeight = preferredLabelSize.height;
			}

			if(preferredComponentSize.height > maxComponentHeight) {
				maxComponentHeight = preferredComponentSize.height;
			}
		}
		
		maxHeight = Math.max(maxLabelHeight, maxComponentHeight);
		
		for(int i = 0; i < m_mainLabels.length; i++) {
			label = m_mainLabels[i];
			component = m_mainComponents[i];
			
			label.setLocation(x, y);
			label.setSize(new Dimension(maxLabelWidth, maxHeight));
			
			component.setLocation(x + maxLabelWidth + spacing, y);
// TODO: temp width:

			y += maxHeight + spacing;
		}
		
		for(int i = 0; i < m_allComponents.length; i++) {
			m_allComponents[i].setSize(new Dimension(componentWidth, maxHeight));
		}
		
		for(int i = 0; i < m_separatorLabels.length; i++) {
			label = m_separatorLabels[i];
			
			label.setPreferredSize(new Dimension(separatorWidth, maxHeight));
			label.setSize(label.getPreferredSize());
		}

		m_offsetSeparatorLabel.setSize(m_offsetSeparatorLabel.getPreferredSize());
		m_offsetSeparatorLabel.setLocation(m_xOffsetTextField.getX() + m_xOffsetTextField.getWidth(), m_xOffsetTextField.getY());
		m_yOffsetTextField.setLocation(m_offsetSeparatorLabel.getX() + m_offsetSeparatorLabel.getWidth(), m_offsetSeparatorLabel.getY());

		for(int i = 0; i < m_allComponents.length; i++) {
			component = m_allComponents[i];
			currentWidth = component.getX() + component.getWidth() + padding;
			
			if(currentWidth > totalMaxWidth) {
				totalMaxWidth = currentWidth;
			}
		}
		
		maxInputWidth = totalMaxWidth - m_mainLabels[0].getWidth() - spacing - (padding * 2);
		
		m_animationTypeComboBox.setSize(new Dimension(maxInputWidth, maxHeight));
		m_tileNameValueLabel.setSize(new Dimension(maxInputWidth, maxHeight));

		m_dimensions = new Dimension(totalMaxWidth, y + padding);
		setSize(m_dimensions); 
		
		revalidate();
	}

	public void handleTileChange(Tile tile) {
		if(m_tile != tile || m_updating || m_updatingUsingDocument) {
			return;
		}
		
		update();
	}

	public void handleTileAttributeChange(TileAttributes attributes, TileAttribute attribute, byte value) { }

	public Dimension getPreferredSize() {
		return m_dimensions;
	}

	public void actionPerformed(ActionEvent e) {
		if(m_updating || e == null || e.getSource() == null) { return; }
		
		if(e.getSource() == m_animationTypeComboBox) {
			if(m_animationTypeComboBox.getSelectedItem() == null) { return; }
			
			TileAnimationType animationType = TileAnimationType.parseFrom(m_animationTypeComboBox.getSelectedItem().toString());
			
			if(animationType.isValid()) {
				m_tile.getAttributes().setAnimationType(animationType);
			}
		}
	}

	public void insertUpdate(final DocumentEvent e) {
		if(m_updating || e == null || e.getDocument() == null) { return; }
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateTileMetadataUsingDocument(e.getDocument());
			}
		});
	}
	
	public void removeUpdate(final DocumentEvent e) {
		if(m_updating || e == null || e.getDocument() == null) { return; }
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateTileMetadataUsingDocument(e.getDocument());
			}
		});
	}
	
	public void changedUpdate(final DocumentEvent e) {
		if(m_updating || e == null || e.getDocument() == null) { return; }
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateTileMetadataUsingDocument(e.getDocument());
			}
		});
	}

}
