package exception;

public class SoundNumberUnderflowException extends Exception {

	private static final long serialVersionUID = 2671395486737865261L;

	public SoundNumberUnderflowException() {
		super();
	}

	public SoundNumberUnderflowException(String message) {
		super(message);
	}

	public SoundNumberUnderflowException(String message, Throwable cause) {
		super(message, cause);
	}

	public SoundNumberUnderflowException(Throwable cause) {
		super(cause);
	}

}

