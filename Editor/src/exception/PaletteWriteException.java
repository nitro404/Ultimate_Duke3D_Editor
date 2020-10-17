package exception;

public class PaletteWriteException extends ItemWriteException {
	
	private static final long serialVersionUID = -8377327345630262534L;
	
	public PaletteWriteException() {
		super();
	}
	
	public PaletteWriteException(String message) {
		super(message);
	}
	
	public PaletteWriteException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PaletteWriteException(Throwable cause) {
		super(cause);
	}
	
}
