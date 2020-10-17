package gui;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import utilities.*;

public class ArrowIcon implements Icon {

	protected ArrowDirection m_direction;
	protected int m_size;
	protected Color m_colour;
	protected BufferedImage m_arrowImage;
	
	public static final ArrowDirection DEFAULT_DIRECTION = ArrowDirection.defaultDirection;
	public static final int DEFAULT_SIZE = 24;
	public static final Color DEFAULT_COLOUR = Color.BLACK;
	protected static final float DEFAULT_ARROW_RATIO = 0.5f;

	public ArrowIcon() {
		this(DEFAULT_DIRECTION);
	}

	public ArrowIcon(ArrowDirection direction) {
		this(direction, DEFAULT_SIZE);
	}

	public ArrowIcon(ArrowDirection direction, int size) {
		this(direction, size, DEFAULT_COLOUR);
	}

	public ArrowIcon(ArrowDirection direction, int size, Color colour) {
		m_direction = direction;
		m_size = size;
		m_colour = colour;
		
		createArrowImage();
	}

	public int getIconWidth() {
		return m_size;
	}

	public int getIconHeight() {
		return m_size;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.drawImage(m_arrowImage, x, y, c);
	}
	
	protected void createArrowImage() {
		if (m_arrowImage != null) {
			return;
		}

		m_arrowImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(m_size, m_size, Transparency.TRANSLUCENT);
		
		if(m_direction == ArrowDirection.Center ||
		   m_direction == ArrowDirection.None) {
			return;
		}
		
		int halfSize = m_size / 2;
		AffineTransform transform = new AffineTransform();

		switch(m_direction) {
			case North:
				transform.setToRotation(ExtendedMath.degreesToRadians(0), halfSize, halfSize);
				break;

			case NorthEast:
				transform.setToRotation(ExtendedMath.degreesToRadians(45), halfSize, halfSize);
				break;

			case East:
				transform.setToRotation(ExtendedMath.degreesToRadians(90), halfSize, halfSize);
				break;

			case SouthEast:
				transform.setToRotation(ExtendedMath.degreesToRadians(135), halfSize, halfSize);
				break;

			case South:
				transform.setToRotation(ExtendedMath.degreesToRadians(180), halfSize, halfSize);
				break;

			case SouthWest:
				transform.setToRotation(ExtendedMath.degreesToRadians(225), halfSize, halfSize);
				break;

			case West:
				transform.setToRotation(ExtendedMath.degreesToRadians(270), halfSize, halfSize);
				break;

			case NorthWest:
				transform.setToRotation(ExtendedMath.degreesToRadians(315), halfSize, halfSize);
				break;

			default:
				break;
		}

		// obtain and configure 2d graphics
		Graphics2D g = (Graphics2D) m_arrowImage.getGraphics();

		g.setTransform(transform);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setStroke(new BasicStroke(m_size * 0.075f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		g.translate(0, 10);

		g.translate(halfSize, m_size / 8);

		float arrowLength = halfSize;

		// draw arrow head
		Path2D.Float path = new Path2D.Float();
		path.moveTo(-DEFAULT_ARROW_RATIO * arrowLength, 0);
		path.lineTo(0.0f, -arrowLength);
		path.lineTo(DEFAULT_ARROW_RATIO * arrowLength, 0);
		path.lineTo(-DEFAULT_ARROW_RATIO * arrowLength, 0);

		g.setColor ( m_colour );
		g.fill(path);

		// draw arrow tail
		g.draw(new Line2D.Float(0.0f, -(arrowLength / 2), 0.0f, m_size / 2));
	}

}
