package exception;

public class UnsupportedFileTypeException extends Exception {
	
	private static final long serialVersionUID = -8213560778571243326L;

	public UnsupportedFileTypeException() {
		super();
	}
	
	public UnsupportedFileTypeException(String message) {
		super(message);
	}
	
	public UnsupportedFileTypeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public UnsupportedFileTypeException(Throwable cause) {
		super(cause);
	}
	
}

