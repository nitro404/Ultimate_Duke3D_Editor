package group;

import java.util.*;
import java.io.*;

public class GroupCollection {
	
	protected Vector<Group> m_groups;
	
	public GroupCollection() {
		m_groups = new Vector<Group>();
	}
	
	public GroupCollection(Vector<Group> groups) {
		m_groups = new Vector<Group>();
		
		if(groups != null) {
			for(int i=0;i<groups.size();i++) {
				m_groups.add(groups.elementAt(i));
			}
		}
	}
	
	public int numberOfGroups() {
		return m_groups.size();
	}
	
	public boolean hasGroup(String fileName) {
		if(fileName == null || fileName.length() == 0) { return false; }
		
		String formattedName = fileName.trim();
		if(formattedName.length() == 0) { return false; }
		
		File f = null;
		for(int i=0;i<m_groups.size();i++) {
			f = m_groups.elementAt(i).getFile();
			
			if(f != null && formattedName.equalsIgnoreCase(f.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasGroup(File file) {
		if(file == null) { return false; }
		
		for(int i=0;i<m_groups.size();i++) {
			if(file.equals(m_groups.elementAt(i).getFile())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasGroup(Group group) {
		if(group == null) { return false; }
		
		for(int i=0;i<m_groups.size();i++) {
			if(m_groups.elementAt(i).equals(group)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasGroupWithFile(String fileName) {
		if(fileName == null || fileName.length() == 0) { return false; }
		
		for(int i=0;i<m_groups.size();i++) {
			if(m_groups.elementAt(i).hasFile(fileName)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasGroupWithFile(File file) {
		if(file == null) { return false; }
		
		return hasGroupWithFile(file.getName());
	}
	
	public boolean hasGroupWithFile(GroupFile file) {
		if(file == null) { return false; }
		
		return hasGroupWithFile(file.getFileName());
	}
	
	public int indexOfGroup(String fileName) {
		if(fileName == null || fileName.length() == 0) { return -1; }
		
		String formattedName = fileName.trim();
		if(formattedName.length() == 0) { return -1; }
		
		File f = null;
		for(int i=0;i<m_groups.size();i++) {
			f = m_groups.elementAt(i).getFile();
			
			if(f != null && formattedName.equalsIgnoreCase(f.getName())) {
				return i;
			}
		}
		return -1;
	}
	
	public int indexOfGroup(File file) {
		if(file == null) { return -1; }
		
		for(int i=0;i<m_groups.size();i++) {
			if(file.equals(m_groups.elementAt(i).getFile())) {
				return i;
			}
		}
		return -1;
	}
	
	public int indexOfGroup(Group group) {
		if(group == null) { return -1; }
		
		for(int i=0;i<m_groups.size();i++) {
			if(m_groups.elementAt(i).equals(group)) {
				return i;
			}
		}
		return -1;
	}
	
	public int indexOfFirstGroupWithFile(String fileName) {
		if(fileName == null || fileName.length() == 0) { return -1; }
		
		for(int i=0;i<m_groups.size();i++) {
			if(m_groups.elementAt(i).hasFile(fileName)) {
				return i;
			}
		}
		return -1;
	}
	
	public int indexOfFirstGroupWithFile(File file) {
		if(file == null) { return -1; }
		
		return indexOfFirstGroupWithFile(file.getName());
	}
	
	public int indexOfFirstGroupWithFile(GroupFile file) {
		if(file == null) { return -1; }
		
		return indexOfFirstGroupWithFile(file.getFileName());
	}

	public Group getGroup(int index) {
		if(index < 0 || index >= m_groups.size()) { return null; }
		
		return m_groups.elementAt(index);
	}
	
	public Group getGroup(String fileName) {
		if(fileName == null || fileName.length() == 0) { return null; }
		
		String formattedName = fileName.trim();
		if(formattedName.length() == 0) { return null; }
		
		File f = null;
		for(int i=0;i<m_groups.size();i++) {
			f = m_groups.elementAt(i).getFile();
			
			if(f != null && formattedName.equalsIgnoreCase(f.getName())) {
				return m_groups.elementAt(i);
			}
		}
		return null;
	}
	
	public Group getGroup(File file) {
		if(file == null) { return null; }
		
		for(int i=0;i<m_groups.size();i++) {
			if(file.equals(m_groups.elementAt(i).getFile())) {
				return m_groups.elementAt(i);
			}
		}
		return null;
	}
	
	public Group getFirstGroupWithFile(String fileName) {
		if(fileName == null || fileName.length() == 0) { return null; }
		
		for(int i=0;i<m_groups.size();i++) {
			if(m_groups.elementAt(i).hasFile(fileName)) {
				return m_groups.elementAt(i);
			}
		}
		return null;
	}
	
	public Group getFirstGroupWithFile(File file) {
		if(file == null) { return null; }
		
		return getFirstGroupWithFile(file.getName());
	}
	
	public Vector<Group> getGroupsWithFile(String fileName) {
		if(fileName == null || fileName.length() == 0) { return null; }
		
		Vector<Group> groupsWithFile = new Vector<Group>();
		
		for(int i=0;i<m_groups.size();i++) {
			if(m_groups.elementAt(i).hasFile(fileName)) {
				groupsWithFile.add(m_groups.elementAt(i));
			}
		}
		return groupsWithFile;
	}
	
	public Vector<Group> getGroupsWithFile(File file) {
		if(file == null) { return null; }
		
		return getGroupsWithFile(file.getName());
	}
	
	public Vector<Group> getGroupsWithFile(GroupFile file) {
		if(file == null) { return null; }
		
		return getGroupsWithFile(file.getFileName());
	}
	
	public Vector<Group> getGroups() {
		return m_groups;
	}
	
	public boolean addGroup(Group group) {
		if(group == null || hasGroup(group)) { return false; }
		
		m_groups.add(group);
		
		return true;
	}
	
	public boolean removeGroup(int index) {
		if(index < 0 || index >= m_groups.size()) { return false; }
		
		m_groups.remove(index);
		
		return true;
	}
	
	public boolean removeGroup(String fileName) {
		if(fileName == null || fileName.length() == 0) { return false; }
		
		String formattedName = fileName.trim();
		if(formattedName.length() == 0) { return false; }
		
		File f = null;
		for(int i=0;i<m_groups.size();i++) {
			f = m_groups.elementAt(i).getFile();
			
			if(f != null && formattedName.equalsIgnoreCase(f.getName())) {
				m_groups.remove(i);
				
				return true;
			}
		}
		return false;
	}
	
	public boolean removeGroup(File file) {
		if(file == null) { return false; }
		
		for(int i=0;i<m_groups.size();i++) {
			if(file.equals(m_groups.elementAt(i).getFile())) {
				m_groups.remove(i);
				
				return true;
			}
		}
		return false;
	}
	
	public boolean removeGroup(Group group) {
		if(group == null) { return false; }
		
		for(int i=0;i<m_groups.size();i++) {
			if(m_groups.elementAt(i).equals(group)) {
				m_groups.remove(i);
				
				return true;
			}
		}
		return false;
	}
	
	public void clearGroups() {
		m_groups.clear();
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof GroupCollection)) {
			return false;
		}
		
		GroupCollection g = (GroupCollection) o;
		
		if(m_groups.size() != g.m_groups.size()) { return false; }
		
		for(int i=0;i<m_groups.size();i++) {
			if(!hasGroup(g.m_groups.elementAt(i))) {
				return false;
			}
		}
		return true;
	}
	
}
