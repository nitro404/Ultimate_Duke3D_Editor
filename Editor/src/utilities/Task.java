package utilities;

import java.util.*;

public class Task {
	
	protected int m_progress;
	protected int m_length;
	protected boolean m_completed;
	protected boolean m_cancelled;
	protected Vector<TaskListener> m_listeners;
	
	public Task(int length) {
		this(length, null);
	}
	
	public Task(int length, TaskListener listener) {
		if(length <= 0) { throw new IllegalArgumentException("Task length must be greater than zero"); }
		
		m_progress = 0;
		m_length = length;
		m_completed = false;
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
		return m_completed;
	}
	
	public boolean isCancelled() {
		return m_cancelled;
	}
	
	public boolean addProgress(int progress) {
		if(progress <= 0) { return false; }
		
		m_progress += progress;
		
		checkCompleted();
		
		notifyTaskListeners();
		
		return true;
	}
	
	public boolean setProgress(int progress) {
		if(progress < 0) { return false; }
		
		m_progress = progress;
		
		checkCompleted();
		
		notifyTaskListeners();
		
		return true;
	}
	
	protected boolean checkCompleted() {
		if(m_progress >= m_length) {
			m_completed = true;
		}
		
		return m_completed;
	}
	
	public void setCompleted() {
		m_completed = true;
		
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
