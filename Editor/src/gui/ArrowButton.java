package gui;

import java.awt.*;
import javax.swing.*;
import utilities.*;

public class ArrowButton extends JButton implements Updatable, Resettable {

	protected ArrowDirection m_currentDirection;
	protected ArrowDirection m_originalDirection;
	
	protected static ArrowIcon ARROW_ICONS[];

	private static final long serialVersionUID = -104162147360836258L;

	public ArrowButton() throws IllegalArgumentException {
		this(ArrowIcon.DEFAULT_DIRECTION);
	}
	
	public ArrowButton(ArrowDirection direction) throws IllegalArgumentException {
		initializeArrowIcons();
		
		m_originalDirection = direction;
		
		setDirection(direction);
	}
	
	protected static void initializeArrowIcons() {
		if(ARROW_ICONS != null) {
			return;
		}
		
		ARROW_ICONS = new ArrowIcon[ArrowDirection.numberOfArrowDirections()];
		
		for(int i = 0; i < ARROW_ICONS.length; i++) {
			ARROW_ICONS[i] = new ArrowIcon(ArrowDirection.values()[i]);
		}
	}
	
	public ArrowDirection getCurrentDirection() {
		return m_currentDirection;
	}
	
	public ArrowDirection getOriginalDirection() {
		return m_originalDirection;
	}
	
	public void setDirection(ArrowDirection direction) throws IllegalArgumentException {
		if(direction == ArrowDirection.Invalid) {
			throw new IllegalArgumentException("Invalid arrow direction!");
		}
		
		m_currentDirection = direction;
		
		update();
	}
	
	public void reset() {
		setDirection(m_originalDirection);
	}
	
	public void update() {
		int padding = 2;
		
		ArrowIcon arrowIcon = ARROW_ICONS[m_currentDirection.ordinal()];

		setEnabled(m_currentDirection != ArrowDirection.Center);
		setPreferredSize(new Dimension(arrowIcon.getIconWidth() + (padding * 2), arrowIcon.getIconHeight() + (padding * 2)));
		setSize(getPreferredSize());
		setIcon(arrowIcon);
	}

}
