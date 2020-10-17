package exception;

public class InvalidFileTypeException extends Exception {
	
	private static final long serialVersionUID = -1808664367136463568L;

	public InvalidFileTypeException() {
		super();
	}
	
	public InvalidFileTypeException(String message) {
		super(message);
	}
	
	public InvalidFileTypeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidFileTypeException(Throwable cause) {
		super(cause);
	}
	
}

