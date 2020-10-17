package exception;

public class MetadataWriteException extends ItemReadException {
	
	private static final long serialVersionUID = 6429082728016141292L;

	public MetadataWriteException() {
		super();
	}
	
	public MetadataWriteException(String message) {
		super(message);
	}
	
	public MetadataWriteException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public MetadataWriteException(Throwable cause) {
		super(cause);
	}
	
}
