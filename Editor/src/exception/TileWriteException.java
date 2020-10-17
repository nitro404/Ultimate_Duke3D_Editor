package exception;

public class TileWriteException extends ItemWriteException {
	
	private static final long serialVersionUID = 7996270568564511451L;

	public TileWriteException() {
		super();
	}
	
	public TileWriteException(String message) {
		super(message);
	}
	
	public TileWriteException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TileWriteException(Throwable cause) {
		super(cause);
	}
	
}
