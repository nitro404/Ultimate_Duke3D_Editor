package gui;

import java.awt.*;
import javax.swing.*;
import item.*;
import palette.*;
import utilities.*;

public class PaletteMetadataPanel extends JPanel implements ItemChangeListener, Updatable {

	protected Palette m_palette;
	protected JLabel m_fileTypeLabel;
	protected JLabel m_numberOfPalettesLabel;
	protected JLabel m_paletteFileSizeLabel;
	protected JLabel m_bytesPerPixelLabel;

	protected JLabel m_labels[];

	private static final long serialVersionUID = 453508496483743514L;

	public PaletteMetadataPanel() {
		this(null);
	}
	
	public PaletteMetadataPanel(Palette palette) {
		setLayout(new WrapLayout(WrapLayout.CENTER, 15, 10));
		setOpaque(false);

		initComponents();

		setPalette(palette);
	}
	
	private void initComponents() {
		m_fileTypeLabel = new JLabel();
		m_numberOfPalettesLabel = new JLabel();
		m_paletteFileSizeLabel = new JLabel();
		m_bytesPerPixelLabel = new JLabel();
		
		m_labels = new JLabel[] {
			m_fileTypeLabel,
			m_numberOfPalettesLabel,
			m_paletteFileSizeLabel,
			m_bytesPerPixelLabel
		};

		add(m_fileTypeLabel);
		add(m_numberOfPalettesLabel);
		add(m_paletteFileSizeLabel);
		add(m_bytesPerPixelLabel);
	}
	
	public void setPalette(Palette palette) {
		if(m_palette == palette) {
			return;
		}
		
		if(m_palette != null) {
			m_palette.removeItemChangeListener(this);
		}
		
		m_palette = palette;
		
		if(m_palette != null) {
			m_palette.addItemChangeListener(this);
		}
		
		update();
	}
	
	public void update() {
		if(m_palette == null) {
			m_fileTypeLabel.setText("File Type: Unknown");
			m_numberOfPalettesLabel.setText("Number of Palettes: N/A");
			m_paletteFileSizeLabel.setText("Palette File Size: N/A");
			m_bytesPerPixelLabel.setText("Bytes Per Pixel: N/A");
		}
		else {
			m_fileTypeLabel.setText("File Type: " + m_palette.getFileType());
			m_numberOfPalettesLabel.setText("Number of Palettes: " + m_palette.numberOfPalettes());
			m_paletteFileSizeLabel.setText("Palette File Size: " + m_palette.getPaletteFileSize());
			m_bytesPerPixelLabel.setText("Bytes Per Pixel: " + m_palette.numberOfBytesPerPixel());
		}

		Component parent = getParent();

		if(parent != null) {
			Color textColour = Utilities.getHighContrastYIQColor(parent.getBackground());

			for(int i = 0; i < m_labels.length; i++) {
				m_labels[i].setForeground(textColour);
			}
		}
	}

	public void handleItemChange(Item item) {
		if(m_palette != item) {
			return;
		}
		
		update();
	}
}
