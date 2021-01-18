package exception;

public class SoundNumberOverflowException extends Exception {

	private static final long serialVersionUID = -193847988457397135L;

	public SoundNumberOverflowException() {
		super();
	}

	public SoundNumberOverflowException(String message) {
		super(message);
	}

	public SoundNumberOverflowException(String message, Throwable cause) {
		super(message, cause);
	}

	public SoundNumberOverflowException(Throwable cause) {
		super(cause);
	}

}

