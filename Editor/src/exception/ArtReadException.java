package exception;

public class ArtReadException extends ItemReadException {
	
	private static final long serialVersionUID = -826769139537101025L;

	public ArtReadException() {
		super();
	}
	
	public ArtReadException(String message) {
		super(message);
	}
	
	public ArtReadException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ArtReadException(Throwable cause) {
		super(cause);
	}
	
}
