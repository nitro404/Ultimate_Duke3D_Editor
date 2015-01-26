package group;

import java.util.*;
import utilities.*;

public abstract class GroupProcessor {
	
	protected Vector<GroupProcessorListener> m_listeners;
	
	public abstract void processGroup(Group group);
	
	public void processGroups(Vector<Group> groups, Task task) {
		if(groups == null) { return; }
		
		for(int i=0;i<groups.size();i++) {
			processGroup(groups.elementAt(i));
			
			if(task != null) {
				task.addProgress(1);
			}
		}
		
		notifyGroupProcessingCompleted();
	}
	
	public void processGroups(GroupCollection groups, Task task) {
		if(groups == null) { return; }
		
		for(int i=0;i<groups.numberOfGroups();i++) {
			processGroup(groups.getGroup(i));
			
			if(task != null) {
				task.addProgress(1);
			}
		}
		
		notifyGroupProcessingCompleted();
	}
	
	public int numberOfListeners() {
		return m_listeners.size();
	}
	
	public boolean hasListener(GroupProcessorListener listener) {
		if(listener == null) { return false; }
		
		for(int i=0;i<m_listeners.size();i++) {
			if(m_listeners.elementAt(i) == listener) {
				return true;
			}
		}
		return false;
	}
	
	public int indexOfListener(GroupProcessorListener listener) {
		if(listener == null) { return -1; }
		
		for(int i=0;i<m_listeners.size();i++) {
			if(m_listeners.elementAt(i) == listener) {
				return i;
			}
		}
		return -1;
	}
	
	public GroupProcessorListener getListener(int index) {
		if(index < 0 || index >= m_listeners.size()) { return null; }
		
		return m_listeners.elementAt(index);
	}
	
	public boolean addListener(GroupProcessorListener listener) {
		if(listener == null || hasListener(listener)) { return false; }
		
		m_listeners.add(listener);
		
		return true;
	}
	
	public boolean removeListener(int index) {
		if(index < 0 || index >= m_listeners.size()) { return false; }
		
		m_listeners.remove(index);
		
		return true;
	}
	
	public boolean removeListener(GroupProcessorListener listener) {
		if(listener == null) { return false; }
		
		for(int i=0;i<m_listeners.size();i++) {
			if(m_listeners.elementAt(i) == listener) {
				m_listeners.remove(i);
				
				return true;
			}
		}
		return false;
	}
	
	public void clearListeners() {
		m_listeners.clear();
	}
	
	public void notifyGroupProcessingCompleted() {
		for(int i=0;i<m_listeners.size();i++) {
			m_listeners.elementAt(i).groupProcessingCompleted();
		}
	}
	
}
