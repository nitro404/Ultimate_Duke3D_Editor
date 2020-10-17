package exception;

public class ArtWriteException extends ItemWriteException {
	
	private static final long serialVersionUID = 3057285456329071898L;

	public ArtWriteException() {
		super();
	}
	
	public ArtWriteException(String message) {
		super(message);
	}
	
	public ArtWriteException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ArtWriteException(Throwable cause) {
		super(cause);
	}
	
}
