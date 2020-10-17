package utilities;

public interface TaskListener {
	
	public void taskProgressChanged(Task t);
	public void taskCancelled(Task t);
	
}
