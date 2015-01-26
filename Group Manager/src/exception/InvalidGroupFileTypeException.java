package exception;

public class InvalidGroupFileTypeException extends Exception {
	
	private static final long serialVersionUID = -5180054487343624623L;
	
	public InvalidGroupFileTypeException() {
		super();
	}
	
	public InvalidGroupFileTypeException(String message) {
		super(message);
	}
	
	public InvalidGroupFileTypeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidGroupFileTypeException(Throwable cause) {
		super(cause);
	}
	
}

