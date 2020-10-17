package gui;

import java.awt.*;
import javax.swing.*;
import settings.*;

public class PixelButton extends JButton {
	
	protected int m_x;
	protected int m_y;
	protected int m_paletteIndex;
	
	public static final int BUTTON_SIZE = SettingsManager.defaultPixelButtonSize;
	
	private static final long serialVersionUID = 3347140342174786894L;
	
	public PixelButton(Color c, int x, int y, int paletteIndex) {
		setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
		setSize(BUTTON_SIZE, BUTTON_SIZE);
		setBorder(null);
		setBorderPainted(false);
		
		m_x = x;
		m_y = y;
		m_paletteIndex = paletteIndex;
		
		if(c != null) {
			setBackground(c);
		}
	}
	
	public int getPixelX() {
		return m_x;
	}
	
	public int getPixelY() {
		return m_y;
	}
	
	public int getPaletteIndex() {
		return m_paletteIndex;
	}
	
	public boolean chooseColour() {
		Color newColour = JColorChooser.showDialog(null, "Choose Pixel Colour", getBackground());
		if(newColour == null) { return false; }
		
		setBackground(newColour);
		
		return true;
	}
	
}
