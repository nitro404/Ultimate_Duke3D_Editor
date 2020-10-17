package exception;

public class TileReadException extends ItemWriteException {
	
	private static final long serialVersionUID = -3464550607772103989L;

	public TileReadException() {
		super();
	}
	
	public TileReadException(String message) {
		super(message);
	}
	
	public TileReadException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TileReadException(Throwable cause) {
		super(cause);
	}
	
}
