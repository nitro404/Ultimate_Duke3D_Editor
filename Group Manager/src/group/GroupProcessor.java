package group;

import java.util.*;
import java.io.*;
import java.awt.*;
import exception.*;
import utilities.*;
import console.*;

public abstract class GroupProcessor {
	
	public static final boolean DEFAULT_RECURSIVE = true;
	
	protected Task m_task;
	protected Vector<GroupProcessorListener> m_listeners;
	
	public GroupProcessor() {
		m_task = null;
		m_listeners = new Vector<GroupProcessorListener>();
	}
	
	public Task getTask() {
		return m_task;
	}
	
	public void setTask(Task task) {
		m_task = task;
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

	public abstract boolean processGroup(Group group);
	
	public boolean processGroup(File file) {
		if(file == null || !file.exists()) {
			SystemConsole.instance.writeLine("Cannot process group file: \"" + file.getName() + "\", file does not exist.");
			return false;
		}
		
		String extension = Utilities.getFileExtension(file.getName());
		
		GroupPlugin plugin = GroupPluginManager.instance.getPluginForFileType(extension);
		if(plugin == null) { return false; }
		
		Group group = null;
		try { group = plugin.getGroupInstance(file); }
		catch(GroupInstantiationException e) {
			SystemConsole.instance.writeLine("Failed to instantiate group for processing: " + e.getMessage());
			
			return false;
		}
		if(group == null) {
			SystemConsole.instance.writeLine("Failed to instantiate \"" + plugin.getName() + " for group processing (" + plugin.getSupportedGroupFileTypesAsString() + ")\" plugin when attempting to read group file: \"" + file.getName() + "\".");
			
			return false;
		}
		
		try {
			if(!group.load()) {
				SystemConsole.instance.writeLine("Failed to load group for processing: \"" + file.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedGroupFileTypesAsString() + ")\".");
				
				return false;
			}
		}
		catch(HeadlessException e) {
			SystemConsole.instance.writeLine("Exception thrown while loading group for processing: \"" + file.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedGroupFileTypesAsString() + "): " + e.getMessage());
			
			return false;
		}
		catch(GroupReadException e) {
			SystemConsole.instance.writeLine("Failed to load group for processing: " + e.getMessage());
			
			return false;
		}
		
		SystemConsole.instance.writeLine("Group file \"" + file.getName() +  "\" loaded successfully for processing!");
		
		processGroup(group);
		
		return true;
	}
	
	public void processGroups(Vector<Group> groups, Task task) {
		if(groups == null || task != null && task.isCancelled()) { return; }
		
		m_task = task;
		
		for(int i=0;i<groups.size();i++) {
			if(processGroup(groups.elementAt(i))) {
				if(task != null) {
					task.addProgress(1);
				}
			}
		}
		
		notifyGroupProcessingCompleted();
	}
	
	public void processGroups(GroupCollection groups, Task task) {
		if(groups == null || (task != null && task.isCancelled())) { return; }
		
		m_task = task;
		
		for(int i=0;i<groups.numberOfGroups();i++) {
			if(processGroup(groups.getGroup(i))) {
				if(task != null) {
					task.addProgress(1);
				}
			}
		}
		
		notifyGroupProcessingCompleted();
	}
	
	public void processGroups(File directory, Task task) {
		processGroups(directory, task, DEFAULT_RECURSIVE);
	}
	
	public void processGroups(File directory, Task task, boolean recursive) {
		if(directory == null || !directory.exists() || !directory.isDirectory() || (task != null && task.isCancelled())) { return; }
		
		m_task = task;
		
		File contents[] = directory.listFiles();
		
		for(int i=0;i<contents.length;i++) {
			if(contents[i].isDirectory()) {
				if(recursive) {
					processGroups(contents[i], task, recursive);
				}
			}
			else if(contents[i].isFile()) {
				if(processGroup(contents[i])) {
					if(task != null) {
						task.addProgress(1);
					}
				}
			}
		}
		
		notifyGroupProcessingCompleted();
	}
	
	public int numberOfGroupsInDirectory(File directory) {
		return numberOfGroupsInDirectory(directory, DEFAULT_RECURSIVE);
	}
	
	public int numberOfGroupsInDirectory(File directory, boolean recursive) {
		if(directory == null || !directory.exists() || !directory.isDirectory()) { return -1; }
		
		int numberOfGroups = 0;
		File contents[] = directory.listFiles();
		
		for(int i=0;i<contents.length;i++) {
			if(contents[i].isDirectory()) {
				if(recursive) {
					numberOfGroups += numberOfGroupsInDirectory(contents[i], recursive);
				}
			}
			else if(contents[i].isFile()) {
				numberOfGroups++;
			}
		}
		return numberOfGroups;
	}
	
}
