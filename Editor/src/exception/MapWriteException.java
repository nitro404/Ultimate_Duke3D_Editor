package exception;

public class MapWriteException extends ItemWriteException {

	private static final long serialVersionUID = -2228890025480163143L;

	public MapWriteException() {
		super();
	}

	public MapWriteException(String message) {
		super(message);
	}

	public MapWriteException(String message, Throwable cause) {
		super(message, cause);
	}

	public MapWriteException(Throwable cause) {
		super(cause);
	}

}
