package exception;

public class PaletteReadException extends ItemReadException {
	
	private static final long serialVersionUID = 4246184421197833843L;
	
	public PaletteReadException() {
		super();
	}
	
	public PaletteReadException(String message) {
		super(message);
	}
	
	public PaletteReadException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PaletteReadException(Throwable cause) {
		super(cause);
	}
	
}
