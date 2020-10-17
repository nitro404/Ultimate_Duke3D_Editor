package exception;

public class MalformedItemAttributeException extends Exception {

	private static final long serialVersionUID = -7178673340876531924L;

	public MalformedItemAttributeException() {
		super();
	}

	public MalformedItemAttributeException(String message) {
		super(message);
	}

	public MalformedItemAttributeException(String message, Throwable cause) {
		super(message, cause);
	}

	public MalformedItemAttributeException(Throwable cause) {
		super(cause);
	}

}
