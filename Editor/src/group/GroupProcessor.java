package group;

import java.util.*;
import java.io.*;
import java.awt.*;
import plugin.*;
import editor.*;
import exception.*;
import utilities.*;
import console.*;

public abstract class GroupProcessor {
	
	public static final boolean DEFAULT_RECURSIVE = true;
	
	protected Task m_task;
	protected Vector<GroupProcessorListener> m_listeners;
	protected boolean m_initialized;
	
	public GroupProcessor() {
		m_task = null;
		m_listeners = new Vector<GroupProcessorListener>();
		m_initialized = true;
	}
	
	public abstract boolean initialize();
	
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
		groupProcessingCompleted();
		
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
		
		FilePlugin filePlugin = EditorPluginManager.instance.getPreferredPluginPrompt(extension);
		if(filePlugin == null || !(filePlugin instanceof GroupPlugin)) { return false; }
		
		GroupPlugin groupPlugin = (GroupPlugin) filePlugin;
		
		Group group = null;
		try { group = (Group) groupPlugin.getNewItemInstance(file); }
		catch(ItemInstantiationException e) {
			SystemConsole.instance.writeLine("Failed to instantiate group for processing: " + e.getMessage());
			
			return false;
		}
		if(group == null) {
			SystemConsole.instance.writeLine("Failed to instantiate \"" + groupPlugin.getName() + " for group processing (" + groupPlugin.getSupportedFileFormatsAsString() + ")\" plugin when attempting to read group file: \"" + file.getName() + "\".");
			
			return false;
		}
		
		try {
			if(!group.load()) {
				SystemConsole.instance.writeLine("Failed to load group for processing: \"" + file.getName() + "\" using plugin: \"" + groupPlugin.getName() + " (" + groupPlugin.getSupportedFileFormatsAsString() + ")\".");
				
				return false;
			}
		}
		catch(HeadlessException e) {
			SystemConsole.instance.writeLine("Exception thrown while loading group for processing: \"" + file.getName() + "\" using plugin: \"" + groupPlugin.getName() + " (" + groupPlugin.getSupportedFileFormatsAsString() + "): " + e.getMessage());
			
			return false;
		}
		catch(ItemReadException e) {
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
		
		if(task != null) {
			task.setCompleted();
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
		
		if(task != null) {
			task.setCompleted();
		}
		
		notifyGroupProcessingCompleted();
	}
	
	public void processGroups(File files[], Task task) {
		processGroups(files, task, DEFAULT_RECURSIVE, 0);
	}
	
	public void processGroups(File files[], Task task, boolean recursive) {
		processGroups(files, task, recursive, 0);
	}
	
	public void processGroups(File files[], Task task, boolean recursive, int depth) {
		if(files == null || files.length == 0 || (task != null && task.isCancelled())) { return; }
		
		m_task = task;
		
		for(int i=0;i<files.length;i++) {
			if(files[i] == null || !files[i].exists()) { continue; }
			
			if(files[i].isDirectory()) {
				if(!recursive && depth > 0) { continue; }
				
				processGroups(files[i].listFiles(), task, recursive, depth + 1);
			}
			else if(files[i].isFile()) {
				if(processGroup(files[i])) {
					if(task != null) {
						task.addProgress(1);
					}
				}
				
				System.gc();
			}
		}
		
		if(depth == 0 && task != null) {
			task.setCompleted();
			
			notifyGroupProcessingCompleted();
		}
	}
	
	public abstract void groupProcessingCompleted();
	
	public int numberOfGroupsInFiles(File files[]) {
		return numberOfGroupsInFiles(files, DEFAULT_RECURSIVE, 0);
	}
	
	public int numberOfGroupsInFiles(File files[], boolean recursive) {
		return numberOfGroupsInFiles(files, recursive, 0);
	}
	
	protected int numberOfGroupsInFiles(File files[], boolean recursive, int depth) {
		if(files == null || files.length == 0) { return 0; }
		
		int numberOfGroups = 0;
		
		for(int i=0;i<files.length;i++) {
			if(files[i] == null || !files[i].exists()) { continue; }
			
			if(files[i].isDirectory()) {
				if(!recursive && depth > 0) { continue; }
				
				numberOfGroups += numberOfGroupsInFiles(files[i].listFiles(), recursive, depth + 1);
			}
			else if(files[i].isFile()) {
				Vector<FilePlugin> filePlugins = EditorPluginManager.instance.getPluginsForFileFormat(Utilities.getFileExtension(files[i].getName()));
				
				for(int j = 0; i < filePlugins.size(); j++) {
					if(filePlugins.elementAt(j) instanceof GroupPlugin) {
						numberOfGroups++;

						break;
					}
				}
			}
		}
		
		return numberOfGroups;
	}
	
}
