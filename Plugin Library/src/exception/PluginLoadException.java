package exception;

public class PluginLoadException extends Exception {
	
	private static final long serialVersionUID = 944885739911279027L;
	
	public PluginLoadException() {
		super();
	}
	
	public PluginLoadException(String message) {
		super(message);
	}
	
	public PluginLoadException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PluginLoadException(Throwable cause) {
		super(cause);
	}
	
}
