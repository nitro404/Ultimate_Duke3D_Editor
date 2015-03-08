package exception;

public class PluginInstantiationException extends Exception {
	
	private static final long serialVersionUID = 7675851710261555628L;
	
	public PluginInstantiationException() {
		super();
	}
	
	public PluginInstantiationException(String message) {
		super(message);
	}
	
	public PluginInstantiationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PluginInstantiationException(Throwable cause) {
		super(cause);
	}
	
}
