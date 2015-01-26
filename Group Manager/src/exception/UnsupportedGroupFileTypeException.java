package exception;

public class UnsupportedGroupFileTypeException extends Exception {
	
	private static final long serialVersionUID = 5502020318659047935L;
	
	public UnsupportedGroupFileTypeException() {
		super();
	}
	
	public UnsupportedGroupFileTypeException(String message) {
		super(message);
	}
	
	public UnsupportedGroupFileTypeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public UnsupportedGroupFileTypeException(Throwable cause) {
		super(cause);
	}
	
}

