package art;

public class TileFormat {
	
	protected String m_displayName;
	protected String m_extension;
	protected boolean m_lossless;
	
	public static final TileFormat PNG = new TileFormat("Portable Network Graphics", "PNG", true);
	public static final TileFormat GIF = new TileFormat("Graphics Interchange Format", "GIF", true);
	public static final TileFormat BMP = new TileFormat("Bitmap", "BMP", true);
	
	public static final TileFormat FORMATS[] = new TileFormat[] {
		PNG,
		GIF,
		BMP
	};
	
// TODO: add support for reading and writing PCX images (also update palette image) https://www.informit.com/articles/article.aspx?p=684049&seqNum=1
// TODO: add support for JPG once dithering is supported
	
	protected TileFormat(String displayName, String extension, boolean lossless) throws IllegalArgumentException {
		if(displayName == null) { throw new IllegalArgumentException("Missing tile format display name."); }
		if(extension == null) { throw new IllegalArgumentException("Missing tile format extension."); }
		
		m_displayName = displayName.trim();
		
		if(m_displayName.isEmpty()) { throw new IllegalArgumentException("Empty tile format display name."); }
		
		m_extension = extension.trim().toUpperCase();
		
		if(m_extension.isEmpty()) { throw new IllegalArgumentException("Empty tile format extension."); }
		
		m_lossless = lossless;
	}
	
	public String getDisplayName() {
		return m_displayName;
	}
	
	public String getExtension() {
		return m_extension;
	}
	
	public boolean isLossless() {
		return m_lossless;
	}
	
	public static TileFormat getFormat(String type) {
		if(type == null) { return null; }
		
		String formattedType = type.trim();
		
		for(int i = 0; i < FORMATS.length; i++) {
			if(formattedType.equalsIgnoreCase(FORMATS[i].m_displayName) ||
			   formattedType.equalsIgnoreCase(FORMATS[i].m_extension)) {
				return FORMATS[i];
			}
		}
		
		return null;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof TileFormat)) {
			return false;
		}
		
		TileFormat f = (TileFormat) o;
		
		return m_displayName.equalsIgnoreCase(f.m_displayName) &&
			   m_extension.equalsIgnoreCase(f.m_extension) &&
			   m_lossless == f.m_lossless;
	}
	
	public String toString() {
		return m_displayName;
	}
	
}
