package exception;

public class InvalidMetadataException extends Exception {
	
	private static final long serialVersionUID = 9208776452402647038L;

	public InvalidMetadataException() {
		super();
	}
	
	public InvalidMetadataException(String message) {
		super(message);
	}
	
	public InvalidMetadataException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidMetadataException(Throwable cause) {
		super(cause);
	}
	
}

