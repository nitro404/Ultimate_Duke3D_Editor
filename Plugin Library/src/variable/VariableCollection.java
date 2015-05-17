package variable;

import java.util.*;
import java.io.*;
import utilities.*;

public class VariableCollection {
	
	/** The collection of Variable objects. */
	private Vector<Variable> m_variables;
	
	/** The collection of category names. */
	private Vector<String> m_categories;
	
	
	/** Constructs an empty VariableSystem object with a default size of 10. */
	public VariableCollection() {
		m_variables = new Vector<Variable>();
		m_categories = new Vector<String>();
	}
	
	/**
	 * Returns the number of categories.
	 * 
	 * @return number of categories.
	 */
	public int numberOfCategories() {
		return m_categories.size();
	}
	
	public boolean hasCategory(String category) {
		if(category == null || category.length() == 0 || m_categories.size() == 0) { return false; }
		
		String formattedCategory = category.trim();
		
		if(formattedCategory.length() == 0) { return false; }
		
		for(int i=0;i<m_categories.size();i++) {
			if(formattedCategory.equalsIgnoreCase(m_categories.elementAt(i))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the index of the category, if it was found.
	 * 
	 * @param category the name of the category to be located.
	 * @return the index of the category, if it was found.
	 */
	public int indexOfCategory(String category) {
		if(category == null || category.length() == 0 || m_categories.size() == 0) { return -1; }
		
		String formattedCategory = category.trim();
		
		if(formattedCategory.length() == 0) { return -1; }
		
		for(int i=0;i<m_categories.size();i++) {
			if(formattedCategory.equalsIgnoreCase(m_categories.elementAt(i))) {
				return i;
			}
		}

		return -1;
	}
	
	/**
	 * Returns the name of a category at the specified index.
	 * 
	 * @param index the index of the category to be retrieved.
	 * @return the name of a category at the specified index.
	 */
	public String getCategory(int index) {
		if(index < 0 || index >= m_categories.size()) { return null; }
		
		return m_categories.elementAt(index);
	}
	
	/**
	 * Adds a category and returns the index of it after it is added (or the index of it if it already exists).
	 * 
	 * @param category the name of the category to be added.
	 * @return the index of the category, after it was added.
	 */
	public int addCategory(String category) {
		if(category == null || category.length() == 0) { return Variable.NO_CATEGORY; }
		
		String formattedCategory = category.trim();
		
		if(formattedCategory.length() == 0) { return Variable.NO_CATEGORY; }
		
		for(int i=0;i<m_categories.size();i++) {
			if(formattedCategory.equalsIgnoreCase(m_categories.elementAt(i))) {
				return i;
			}
		}
		
		m_categories.add(formattedCategory);
		
		return m_categories.size() - 1;
	}

	/**
	 * Removes the specified category and any variables associated with it.
	 * 
	 * @param data the name of the category to be removed.
	 */
	public void removeCategory(String category) {
		if(category == null || category.length() == 0) { return; }
		
		String formattedCategory = category.trim();
		
		if(formattedCategory.length() == 0) { return; }

		int categoryIndex = indexOfCategory(formattedCategory);

		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getCategory() == categoryIndex) {
				m_variables.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * Returns the number of Variable objects stored within the collection of Variables.
	 * 
	 * @return the number of Variable objects stored within the collection of Variables.
	 */
	public int numberOfVariables() {
		return m_variables.size();
	}

	/**
	 * Checks to see if a Variable with an id matching the specified parameter exists. 
	 * 
	 * @param id the id to be matched.
	 * @return true if a Variable with an id matching the specified parameter is found.
	 */
	public boolean hasVariable(String id) {
		if(id == null || id.length() == 0) { return false; }
		
		String formattedID = id.trim();
		
		if(formattedID.length() == 0) { return false; }
		
		// loop through and check to see if any variables contain a matching id
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getID().equalsIgnoreCase(formattedID)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks to see if a Variable with an id matching the specified parameters exists.
	 * 
	 * @param the id to be matched.
	 * @param category the name of the category the Variable belongs to.
	 * @return true if a Variable with an id matching the specified parameters is found.
	 */
	public boolean hasVariable(String id, String category) {
		if(id == null || id.length() == 0) { return false; }
		
		String formattedID = id.trim();
		
		if(formattedID.length() == 0) { return false; }
		
		int categoryIndex = indexOfCategory(category);
		
		// loop through and check to see if any variables contain a matching id
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getCategory() == categoryIndex &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(formattedID)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks to see if a matching Variable exists within the collection of Variables.
	 * 
	 * @param v the Variable to be matched.
	 * @return true if a matching Variable is found within the collection of Variables.
	 */
	public boolean hasVariable(Variable v) {
		if(v == null) { return false; }
		
		// loop through and search for a Variable matching the corresponding parameter
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getCategory() == v.getCategory() &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(v.getID())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the index of the Variable matching the specified id if it exists, otherwise returns -1.
	 * 
	 * @param id the id to be matched.
	 * @return the index of the Variable matching the specified id if it exists, otherwise returns -1.
	 */
	public int indexOfVariable(String id) {
		if(id == null || id.length() == 0) { return -1; }
		
		String formattedID = id.trim();
		
		if(formattedID.length() == 0) { return -1; }
		
		// loop through and check to see if any variables contain a matching id
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getID().equalsIgnoreCase(formattedID)) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Returns the index of the Variable matching the specified id and category if it exists, otherwise returns -1.
	 * 
	 * @param id the id to be matched.
	 * @param category the name of the category the Variable belongs to.
	 * @return the index of the Variable matching the specified id and category if it exists, otherwise returns -1.
	 */
	public int indexOfVariable(String id, String category) {
		if(id == null || id.length() == 0) { return -1; }
		
		String formattedID = id.trim();
		
		if(formattedID.length() == 0) { return -1; }
		
		int categoryIndex = indexOfCategory(category);
		
		// loop through and check to see if any variables contain a matching id
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getCategory() == categoryIndex &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(formattedID)) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Returns the index of the Variable matching the specified Variable if it exists, otherwise returns -1.
	 * 
	 * @param v the Variable to be matched.
	 * @return the index of the Variable matching the specified Variable if it exists, otherwise returns -1.
	 */
	public int indexOfVariable(Variable v) {
		if(v == null) { return -1; }
		
		// loop through and search for a Variable matching the corresponding parameter
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getCategory() == v.getCategory() &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(v.getID())) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Returns the Variable located at the specified index, otherwise returns null if the index is out of range or there are no elements.
	 * 
	 * @param index the index of the Variable to be returned.
	 * @return the Variable located at the specified index, otherwise returns null if the index is out of range or there are no elements.
	 */
	public Variable getVariable(int index) {
		if(index < 0 || index >= m_variables.size()) { return null; }
		
		// return the Variable at the specified index if the index is within the boundaries of the collection of Variables
		return m_variables.elementAt(index);
	}
	
	/**
	 * Returns the Variable matching the corresponding id if it exists in the collection of Variables, otherwise returns null.
	 * 
	 * @param id the id to be matched.
	 * @return the Variable matching the corresponding id if it exists in the collection of Variables, otherwise returns null.
	 */
	public Variable getVariable(String id) {
		if(id == null || id.length() == 0) { return null; }
		
		String formattedID = id.trim();
		
		if(formattedID.length() == 0) { return null; }
		
		// loop through and check to see if any variables contain a matching id, if one exists then return the value of the corresponding Variable
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getID().equalsIgnoreCase(formattedID)) {
				return m_variables.elementAt(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the Variable matching the corresponding id and category if it exists in the collection of Variables, otherwise returns null.
	 * 
	 * @param id the id to be matched.
	 * @param category the name of the category associated with the variable.
	 * @return the Variable matching the corresponding id and category if it exists in the collection of Variables, otherwise returns null.
	 */
	public Variable getVariable(String id, String category) {
		if(id == null || id.length() == 0) { return null; }
		
		String formattedID = id.trim();
		
		if(formattedID.length() == 0) { return null; }
		
		int categoryIndex = indexOfCategory(category);
		
		// loop through and check to see if any variables contain a matching id, if one exists then return the value of the corresponding Variable
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getCategory() == categoryIndex &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(formattedID)) {
				return m_variables.elementAt(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the value of a Variable matching the corresponding id if it exists in the collection of Variables, otherwise returns null.
	 * 
	 * @param id the id to be matched.
	 * @return the value of a Variable matching the corresponding id if it exists in the collection of Variables, otherwise returns null.
	 */
	public String getValue(String id) {
		if(id == null || id.length() == 0) { return null; }
		
		String formattedID = id.trim();
		
		if(formattedID.length() == 0) { return null; }
		
		// loop through and check to see if any variables contain a matching id, if one exists then return the value of the corresponding Variable
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getID().equalsIgnoreCase(formattedID)) {
				return m_variables.elementAt(i).getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the value of a Variable matching the corresponding id and category if it exists in the collection of Variables, otherwise returns null.
	 * 
	 * @param id the id to be matched.
	 * @param category the name of the category associated with the variable.
	 * @return the value of a Variable matching the corresponding id and category if it exists in the collection of Variables, otherwise returns null.
	 */
	public String getValue(String id, String category) {
		if(id == null || id.length() == 0) { return null; }
		
		String formattedID = id.trim();
		
		if(formattedID.length() == 0) { return null; }
		
		int categoryIndex = indexOfCategory(category);
		
		// loop through and check to see if any variables contain a matching id, if one exists then return the value of the corresponding Variable
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getCategory() == categoryIndex &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(formattedID)) {
				return m_variables.elementAt(i).getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * Returns a collection of Variable objects associated with the specified category.
	 * 
	 * @param category the category associated with the Variables to be collected.
	 * @return a collection of Variable objects associated with the specified category.
	 */
	public Vector<Variable> getVariablesInCategory(String category) {
		int categoryIndex = indexOfCategory(category);

		Vector<Variable> variableCollection = new Vector<Variable>();

		// collect all variables in the specified category
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getCategory() == categoryIndex) {
				variableCollection.add(m_variables.elementAt(i));
			}
		}
		
		return variableCollection;
	}
	
	/**
	 * Updates the string value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, String value) {
		if(id == null || id.length() == 0) { return; }
		
		String formattedID = id.trim();
		
		if(formattedID.length() == 0) { return; }
		
		boolean valueUpdated = false;
		
		for(int i=0;i<m_variables.size();i++) {
			if(formattedID.equalsIgnoreCase(m_variables.elementAt(i).getID())) {
				m_variables.elementAt(i).setValue(value == null ? "" : value);
				valueUpdated = true;
			}
		}

		// if the variable doesn't exist, add it
		if(!valueUpdated) {
			addVariable(id, value);
		}
	}
	
	/**
	 * Updates the boolean value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, boolean value) {
		setValue(id, value ? "true" : "false");
	}
	
	/**
	 * Updates the char value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, char value) {
		setValue(id, Character.toString(value));
	}
	
	/**
	 * Updates the byte value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, byte value) {
		setValue(id, Byte.toString(value));
	}
	
	/**
	 * Updates the short value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, short value) {
		setValue(id, Short.toString(value));
	}
	
	/**
	 * Updates the integer value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, int value) {
		setValue(id, Integer.toString(value));
	}
	
	/**
	 * Updates the long value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, long value) {
		setValue(id, Long.toString(value));
	}
	
	/**
	 * Updates the float value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, float value) {
		setValue(id, Float.toString(value));
	}
	
	/**
	 * Updates the double value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, double value) {
		setValue(id, Double.toString(value));
	}

	/**
	 * Updates the object value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, Object value) {
		setValue(id, value == null ? "null" : value.toString());
	}
	
	/**
	 * Updates the string value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, String value, String category) {
		if(id == null || id.length() == 0) { return; }
		
		String formattedID = id.trim();
		
		if(formattedID.length() == 0) { return; }

		int categoryIndex = indexOfCategory(category);
		
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getCategory() == categoryIndex &&
				formattedID.equalsIgnoreCase(m_variables.elementAt(i).getID())) {
				m_variables.elementAt(i).setValue(value == null ? "" : value);
				return;
			}
		}

		// if the variable doesn't exist, add it
		addVariable(id, value, category);
	}

	/**
	 * Updates the boolean value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, boolean value, String category) {
		setValue(id, value ? "true" : "false", category);
	}

	/**
	 * Updates the char value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, char value, String category) {
		setValue(id, Character.toString(value), category);
	}
	
	/**
	 * Updates the short value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, short value, String category) {
		setValue(id, Short.toString(value), category);
	}
	
	/**
	 * Updates the integer value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, int value, String category) {
		setValue(id, Integer.toString(value), category);
	}

	/**
	 * Updates the long value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, long value, String category) {
		setValue(id, Long.toString(value), category);
	}

	/**
	 * Updates the long value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, float value, String category) {
		setValue(id, Float.toString(value), category);
	}
	
	/**
	 * Updates the double value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, double value, String category) {
		setValue(id, Double.toString(value), category);
	}

	/**
	 * Updates the double value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, Object value, String category) {
		setValue(id, value == null ? "null" : value.toString(), category);
	}
	
	/**
	 * Creates and adds a Variable object to the collection of Variables.
	 * 
	 * @param id the id of the Variable to be created.
	 * @param value the value of the Variable to be created.
	 * @return true if the Variable is valid and was added or updated.
	 */
	public boolean addVariable(String id, String value) {
		if(id == null || id.length() == 0) { return false; }
		
		return addVariable(id, value, true);
	}
	
	/**
	 * Creates and adds a Variable object to the collection of Variables.
	 * 
	 * @param id the id of the Variable to be created.
	 * @param value the value of the Variable to be created.
	 * @param update determines if the variable should be updated, if it already exists.
	 * @return true if the Variable is valid and was added or updated.
	 */
	public boolean addVariable(String id, String value, boolean update) {
		if(id == null || id.length() == 0) { return false; }
		
		String formattedID = id.trim();
		
		if(formattedID.length() == 0) { return false; }
		
		Variable v = getVariable(formattedID);
		
		if(v == null) {
			m_variables.add(new Variable(formattedID, value, Variable.NO_CATEGORY));
			return true;
		}
		else {
			if(update) {
				v.setValue(value);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Creates and adds a Variable object to the collection of Variables.
	 * 
	 * @param id the id of the Variable to be created.
	 * @param value the value of the Variable to be created.
	 * @param category the name of the category associated with the Variable.
	 * @return true if the Variable is valid and was added or updated.
	 */
	public boolean addVariable(String id, String value, String category) {
		if(id == null || id.length() == 0) { return false; }
		
		return addVariable(id, value, category, true);
	}
	
	/**
	 * Creates and adds a Variable object to the collection of Variables.
	 * 
	 * @param id the id of the Variable to be created.
	 * @param value the value of the Variable to be created.
	 * @param category the name of the category associated with the Variable.
	 * @param update determines if the variable should be updated, if it already exists.
	 * @return true if the Variable is valid and was added or updated.
	 */
	public boolean addVariable(String id, String value, String category, boolean update) {
		if(id == null || id.length() == 0) { return false; }
		
		String formattedID = id.trim();
		
		if(formattedID.length() == 0) { return false; }
		
		Variable v = getVariable(formattedID, category);
		
		if(v == null) {
			int categoryIndex = addCategory(category);
			m_variables.add(new Variable(formattedID, value, categoryIndex));
			return true;
		}
		else {
			if(update) {
				v.setValue(value);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds a Variable object to the collection of Variables.
	 * 
	 * @param v the Variable to be added to the collection of Variables.
	 * @return true if the Variable is valid and not already contained within the collection of Variables.
	 */
	public boolean addVariable(Variable v) {
		if(v == null || v.getID().length() == 0) { return false; }
		
		return addVariable(v, true);
	}
	
	/**
	 * Adds a Variable object to the collection of Variables.
	 * 
	 * @param v the Variable to be added to the collection of Variables.
	 * @param update determines if the variable should be updated, if it already exists.
	 * @return true if the Variable is valid and was added or updated.
	 */
	public boolean addVariable(Variable v, boolean update) {
		if(v == null || v.getID().length() == 0 || v.getCategory() >= m_categories.size()) { return false; }
		
		int variableIndex = indexOfVariable(v);
		
		if(variableIndex < 0) {
			m_variables.add(v);
			return true;
		}
		else {
			if(update) {
				m_variables.elementAt(variableIndex).setValue(v.getValue());
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds (merges) a Vector of Variable objects into the current collection.
	 * 
	 * @param v a Vector of Variable objects to add (merge) into the current Variables collection.
	 */
	public void addVariables(Variable[] v) {
		if(v == null) { return; }
		
		// loop through all of the variables in the specified Vector of Variable objects and add them to the current collection
		for(int i=0;i<v.length;i++) {
			addVariable(v[i], true);
		}
	}
	
	/**
	 * Adds (merges) a Vector of Variable objects into the current collection.
	 * 
	 * @param v a Vector of Variable objects to add (merge) into the current Variables collection.
	 * @param update determines if variables should be updated, if they already exist.
	 */
	public void addVariables(Variable[] v, boolean update) {
		if(v == null) { return; }
		
		// loop through all of the variables in the specified Vector of Variable objects and add them to the current collection
		for(int i=0;i<v.length;i++) {
			addVariable(v[i], update);
		}
	}
	
	/**
	 * Adds (merges) a Vector of Variable objects into the current collection.
	 * 
	 * @param v a Vector of Variable objects to add (merge) into the current Variables collection.
	 */
	public void addVariables(Vector<Variable> v) {
		if(v == null) { return; }
		
		// loop through all of the variables in the specified Vector of Variable objects and add them to the current collection
		for(int i=0;i<v.size();i++) {
			addVariable(v.elementAt(i), true);
		}
	}
	
	/**
	 * Adds (merges) a Vector of Variable objects into the current collection.
	 * 
	 * @param v a Vector of Variable objects to add (merge) into the current Variables collection.
	 * @param update determines if variables should be updated, if they already exist.
	 */
	public void addVariables(Vector<Variable> v, boolean update) {
		if(v == null) { return; }
		
		// loop through all of the variables in the specified Vector of Variable objects and add them to the current collection
		for(int i=0;i<v.size();i++) {
			addVariable(v.elementAt(i), update);
		}
	}

	/**
	 * Adds (merges) another VariableSystem into the current collection.
	 * 
	 * @param v the collection of Variables to add (merge) into the current VariableSystem. 
	 */
	public void addVariables(VariableCollection v) {
		if(v == null) { return; }
		
		// loop through all of the variables in the specified Variables collection and add them to the current collection
		for(int i=0;i<v.m_variables.size();i++) {
			addVariable(v.m_variables.elementAt(i), true);
		}
	}
	
	/**
	 * Adds (merges) another VariableSystem into the current collection.
	 * 
	 * @param v the collection of Variables to add (merge) into the current VariableSystem.
	 * @param update determines if variables should be updated, if they already exist.
	 */
	public void addVariables(VariableCollection v, boolean update) {
		if(v == null) { return; }
		
		// loop through all of the variables in the specified Variables collection and add them to the current collection
		for(int i=0;i<v.m_variables.size();i++) {
			addVariable(v.m_variables.elementAt(i), update);
		}
	}
	
	/**
	 * Removes a Variable located at a specified index.
	 * 
	 * @param index the index from which to remove a Variable.
	 * @return true if the Variable was successfully removed from the Variables collection.
	 */
	public boolean removeVariable(int index) {
		if(index < 0 || index >= m_variables.size()) { return false; }
		
		m_variables.remove(index);
		
		return true;
	}
	
	/**
	 * Removes a Variable based on its id.
	 * 
	 * @param id the id of the variable to remove.
	 * @return true if the Variable was located and removed from the collection of Variables.
	 */
	public boolean removeVariable(String id) {
		if(id == null || id.length() == 0) { return false; }
		
		String formattedID = id.trim();
		
		if(formattedID.length() == 0) { return false; }
		
		// loop through and check to see if any variables contain a matching id, and remove the corresponding Variable if one is found
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getID().equalsIgnoreCase(formattedID)) {
				m_variables.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes a Variable based on its id.
	 * 
	 * @param id the id of the Variable to remove.
	 * @param category the name of the category associated with the Variable.
	 * @return true if the Variable was located and removed from the collection of Variables.
	 */
	public boolean removeVariable(String id, String category) {
		if(id == null || id.length() == 0) { return false; }
		
		String formattedID = id.trim();
		
		if(formattedID.length() == 0) { return false; }
		
		int categoryIndex = indexOfCategory(category);
		
		// loop through and check to see if any variables contain a matching id, and remove the corresponding Variable if one is found
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getCategory() == categoryIndex &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(formattedID)) {
				m_variables.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes a Variable from the current collection of Variables.
	 * 
	 * @param v the Variable to be removed.
	 * @return true if the Variable was located and removed from the collection of Variables.
	 */
	public boolean removeVariable(Variable v) {
		if(v == null || v.getID().length() == 0) { return false; }
		
		// loop through and check to see if any variables contain a matching id, and remove the corresponding Variable if one is found
		for(int i=0;i<m_variables.size();i++) {
			if(v.getCategory() == m_variables.elementAt(i).getCategory() &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(v.getID())) {
				m_variables.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Clears the current collection of Variables.
	 */
	public void clearVariables() {
		m_variables.clear();
		m_categories.clear();
	}
	
	/**
	 * Groups all Variables together based on their categories.
	 */
	public void sort() {
		m_variables = mergeSortVariables(m_variables);
	}
	
	/**
	 * Merge sorts all Variables based on categories.
	 * 
	 * @param variables a collection of variables to be sorted.
	 * @return a sorted copy of the variable collection.
	 */
	private static Vector<Variable> mergeSortVariables(Vector<Variable> variables) {
		if(variables.size() <= 1) {
			Vector<Variable> newVariables = new Vector<Variable>();
			if(variables.size() > 0) {
				newVariables.add(variables.elementAt(0));
			}
			
			return newVariables;
		}

		Vector<Variable> left = new Vector<Variable>();
		Vector<Variable> right = new Vector<Variable>();

		int mid = variables.size() / 2;

		for(int i=0;i<mid;i++) {
			left.add(variables.elementAt(i));
		}

		for(int i=mid;i<variables.size();i++) {
			right.add(variables.elementAt(i));
		}

		left = mergeSortVariables(left);
		right = mergeSortVariables(right);

		return mergeVariables(left, right);
	}
	
	/**
	 * Merges two collections together.
	 * 
	 * @param left the left half of the collection to be merged.
	 * @param left the right half of the collection to be merged.
	 * @return a merged copy of the two collections.
	 */
	private static Vector<Variable> mergeVariables(Vector<Variable> left, Vector<Variable> right) {
		Vector<Variable> result = new Vector<Variable>();

		while(left.size() > 0 && right.size() > 0) {
			if(left.elementAt(0).getCategory() <= right.elementAt(0).getCategory()) {
				result.add(left.elementAt(0));
				left.remove(0);
			}
			else {
				result.add(right.elementAt(0));
				right.remove(0);
			}
		}

		for(int i=0;i<left.size();i++) {
			result.add(left.elementAt(i));
		}

		for(int i=0;i<right.size();i++) {
			result.add(right.elementAt(i));
		}

		return result;
	}
	
	/**
	 * Reads a collection of Variables from the specified file name.
	 * 
	 * @param fileName the name of the file to be parsed into a collection of variables.
	 * @return a collection of Variables read from the specified file.
	 */
	public static VariableCollection readFrom(String fileName) {
		if(fileName == null) { return null; }
		return readFrom(new File(fileName));
	}
	
	/**
	 * Reads a collection of Variables from the specified file and adds them to the current collection of Variables (if appropriate).
	 * 
	 * @param file the file to be parsed into a collection of Variables.
	 * @return a collection of Variables read from the specified file.
	 */
	public static VariableCollection readFrom(File file) {
		if(file == null || !file.exists() || !file.isFile()) { return null; }
		
		VariableCollection variables;
		
		BufferedReader in;
		String input, data;
		
		try {
			// open the file
			in = new BufferedReader(new FileReader(file));
			
			variables = new VariableCollection();
			String category = null;
			int categoryIndex = Variable.NO_CATEGORY;
			
			// read until the end of the file
			while((input = in.readLine()) != null) {
				data = input.trim();
				
				if(data.length() == 0) {
					category = null;
					categoryIndex = Variable.NO_CATEGORY;
					continue;
				}

				if(Utilities.isComment(data)) { continue; }
				
				// parse a category
				if(data.length() >= 2 && data.charAt(0) == '[' && data.charAt(data.length() - 1) == ']') {
					category = data.substring(1, data.length() - 1).trim();
					categoryIndex = variables.addCategory(category);
				}
				// parse a variable
				else {
					Variable v = Variable.parseFrom(data);
					if(v != null) {
						v.setCategory(categoryIndex);
						variables.addVariable(v);
					}
				}
			}
			
			in.close();
		}
		catch(IOException e) {
			return null;
		}
		
		return variables;
	}
	
	/**
	 * Outputs a collection of Variables to a specified file.
	 * 
	 * @param fileName the name of the file to write the collection of Variables to.
	 * @return true if writing to the file was successful.
	 */
	public boolean writeTo(String fileName) {
		return writeTo(new File(fileName));
	}
	
	/**
	 * Outputs a collection of Variables to a specified file.
	 * 
	 * @param file the file to write the collection of Variables to.
	 * @return true if writing to the file was successful.
	 */
	public boolean writeTo(File file) {
		if(file == null) { return false; }
		
		PrintWriter out;
		try {
			// open the file for writing, write to it and then close the file
			out = new PrintWriter(new FileWriter(file));
			writeTo(out);
			out.close();
		}
		catch(IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Writes a collection of Variables to a specified PrintWriter.
	 * 
	 * @param out the PrintWriter to write the collection of Variables to.
	 * @throws IOException if there was an error writing to the output stream.
	 */
	public void writeTo(PrintWriter out) throws IOException {
		int lastCategory = Variable.NO_CATEGORY;
		
		boolean firstLine = true;
		
		// output all of the variables to the file, grouped under corresponding categories
		for(int i=0;i<m_variables.size();i++) {
			if(lastCategory == Variable.NO_CATEGORY || lastCategory != m_variables.elementAt(i).getCategory()) {
				if(m_variables.elementAt(i).getCategory() != Variable.NO_CATEGORY) {
					if(!firstLine) { out.println(); }
					out.println("[" + m_categories.elementAt(m_variables.elementAt(i).getCategory()) + "]");
					firstLine = false;
				}
				lastCategory = m_variables.elementAt(i).getCategory();
			}
			m_variables.elementAt(i).writeTo(out);
			firstLine = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if(o == null || !(o instanceof VariableCollection)) { return false; }
		
		VariableCollection v = (VariableCollection) o;
		
		// check the size of each collection of Variables
		if(m_variables.size() != m_variables.size()) { return false; }
		
		// verify that each Variable in the current collection is also in the other collection of Variables
		for(int i=0;i<m_variables.size();i++) {
			if(!v.hasVariable(m_variables.elementAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = new String();
		
		// return a String representation of the collection of Variables using comma to separate the data
		for(int i=0;i<m_variables.size();i++) {
			s += m_variables.elementAt(i);
			if(i < m_variables.size() - 1) {
				s += ", ";
			}
		}
		
		return s;
	}
	
}
