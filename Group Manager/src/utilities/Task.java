package utilities;

import java.util.*;

public class Task {
	
	private int m_progress;
	private int m_length;
	private boolean m_cancelled;
	private Vector<TaskListener> m_listeners;
	
	public Task(int length) {
		this(length, null);
	}
	
	public Task(int length, TaskListener listener) {
		if(length <= 0) { throw new IllegalArgumentException("Task length must be greater than zero"); }
		
		m_progress = 0;
		m_length = length;
		m_cancelled = false;
		m_listeners = new Vector<TaskListener>();
		
		addTaskListener(listener);
	}
	
	public int getProgress() {
		return m_progress;
	}
	
	public double getPercentCompleted() {
		return ((double) m_progress / (double) m_length) * 100.0;
	}
	
	public int getTaskLength() {
		return m_length;
	}
	
	public boolean isCompleted() {
		return m_progress >= m_length;
	}
	
	public boolean isCancelled() {
		return m_cancelled;
	}
	
	public boolean addProgress(int progress) {
		if(progress <= 0) { return false; }
		
		m_progress += progress;
		
		notifyTaskListeners();
		
		return true;
	}
	
	public boolean setProgress(int progress) {
		if(progress < 0) { return false; }
		
		m_progress = progress;
		
		notifyTaskListeners();
		
		return true;
	}
	
	public void setCompleted() {
		m_progress = m_length;
		
		notifyTaskListeners();
	}
	
	public void cancel() {
		m_cancelled = true;
		
		for(int i=0;i<m_listeners.size();i++) {
			m_listeners.elementAt(i).taskCancelled(this);
		}
	}
	
	public boolean addTaskListener(TaskListener listener) {
		if(listener == null) { return false; }
		
		m_listeners.add(listener);
		
		return true;
	}
	
	public boolean removeTaskListener(TaskListener listener) {
		if(listener == null) { return false; }
		
		return m_listeners.remove(listener);
	}
	
	public void clearTaskListeners() {
		m_listeners.clear();
	}
	
	public void notifyTaskListeners() {
		for(int i=0;i<m_listeners.size();i++) {
			m_listeners.elementAt(i).taskProgressChanged(this);
		}
	}
	
}
