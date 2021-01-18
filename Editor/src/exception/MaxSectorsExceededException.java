package exception;

public class MaxSectorsExceededException extends Exception {

	private static final long serialVersionUID = -3615889572197257076L;

	public MaxSectorsExceededException() {
		super();
	}
	
	public MaxSectorsExceededException(String message) {
		super(message);
	}
	
	public MaxSectorsExceededException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public MaxSectorsExceededException(Throwable cause) {
		super(cause);
	}
	
}
