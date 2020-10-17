package variable;

import java.io.*;

public class Variable {
	
	/**
	 * The id associated with the current Variable.
	 */
	private int m_category = NO_CATEGORY;
	
	/**
	 * The id associated with the current Variable.
	 */
	private String m_id;
	
	/**
	 * The value assigned to the current Variable.
	 */
	private String m_value;
	
	/**
	 * The character used to separate the id from the value.
	 */
	final public static String SEPARATORS = ":=";
	
	/**
	 * Value assigned to the category of a variable if it doesn't belong to a category.
	 */
	final public static int NO_CATEGORY = -1;
	
	/**
	 * Constructs an empty Variable object. 
	 */
	public Variable() {
		this(null, null, NO_CATEGORY);
	}
	
	/**
	 * Constructs a Variable object instantiated with the specified id and value. 
	 * 
	 * @param id the id to associate with the current Variable.
	 * @param value the value to assign to the current Variable.
	 */
	public Variable(String id, String value) {
		this(id, value, NO_CATEGORY);
	}
	
	/**
	 * Constructs a Variable object instantiated with the specified id and value. 
	 * 
	 * @param id the id to associate with the current Variable.
	 * @param value the value to assign to the current Variable.
	 * @param category the category id to assign to the current Variable.
	 */
	public Variable(String id, String value, int category) {
		m_id = (id == null) ? "" : id.trim();
		m_value = (value == null) ? "" : value.trim();
		m_category = (category < 0) ? NO_CATEGORY : category;
	}
	
	/**
	 * Returns the category id assigned to the current Variable.
	 * 
	 * @return the category id assigned to the current Variable.
	 */
	public int getCategory() {
		return m_category;
	}
	
	/**
	 * Returns id associated with the current Variable.
	 * 
	 * @return id associated with the current Variable.
	 */
	public String getID() {
		return m_id;
	}
	
	/**
	 * Returns the value assigned to the current Variable.
	 * 
	 * @return the value assigned to the current Variable.
	 */
	public String getValue() {
		return m_value;
	}
	
	/**
	 * Changes the category associated with the current variable.
	 */
	public void setCategory(int category) {
		m_category = (category < 0) ? NO_CATEGORY : category;
	}
	
	/**
	 * Sets the id of the current Variable to the new, specified id.
	 * 
	 * @param id the new id to associate with the current Variable. 
	 */
	public void setID(String id) {
		m_id = (id == null) ? "" : id.trim();
	}
	
	/**
	 * Sets the value of the current Variable to the new, specified value.
	 * 
	 * @param value the new value to assign to the current Variable.
	 */
	public void setValue(String value) {
		m_value = (value == null) ? "" : value.trim();
	}
	
	/**
	 * Updates the boolean value of the Variable.
	 * 
	 * @param value the value to update the Variable with.
	 */
	public void setValue(boolean value) {
		setValue(value ? "true" : "false");
	}
	
	/**
	 * Updates the char value of the Variable.
	 * 
	 * @param value the value to update the Variable with.
	 */
	public void setValue(char value) {
		setValue(Character.toString(value));
	}
	
	/**
	 * Updates the byte value of the Variable.
	 * 
	 * @param value the value to update the Variable with.
	 */
	public void setValue(byte value) {
		setValue(Byte.toString(value));
	}
	
	/**
	 * Updates the short value of the Variable.
	 * 
	 * @param value the value to update the Variable with.
	 */
	public void setValue(short value) {
		setValue(Short.toString(value));
	}
	
	/**
	 * Updates the integer value of the Variable.
	 * 
	 * @param value the value to update the Variable with.
	 */
	public void setValue(int value) {
		setValue(Integer.toString(value));
	}
	
	/**
	 * Updates the long value of the Variable.
	 * 
	 * @param value the value to update the Variable with.
	 */
	public void setValue(long value) {
		setValue(Long.toString(value));
	}
	
	/**
	 * Updates the float value of the Variable.
	 * 
	 * @param value the value to update the Variable with.
	 */
	public void setValue(float value) {
		setValue(Float.toString(value));
	}
	
	/**
	 * Updates the double value of a Variable.
	 * 
	 * @param value the value to update the Variable with.
	 */
	public void setValue(double value) {
		setValue(Double.toString(value));
	}
	
	/**
	 * Removes any category associated with the current variable.
	 */
	public void removeCategory() {
		m_category = NO_CATEGORY;
	}
	
	/**
	 * Creates a Variable from a specified String and returns it.
	 * 
	 * Parses the Variable from a String in the form:
	 * "ID: Value" where
	 * ID is the id to be associated with the Variable,
	 * Value is the data to be assigned to the value of the Variable and
	 * : is the separator character.
	 * 
	 * @param data the data to be parsed into a Variable.
	 * @return the Variable parsed from the data String.
	 */
	public static Variable parseFrom(String data) {
		if(data == null) { return null; }
		
		String[] parts = data.replaceAll("([\n\r]+|^[\t ]+|[\t ]+$)", "").split("[\t ]*[" + SEPARATORS + "][\t ]*", 2);
		
		if(parts.length != 2) { return null; }
		
		return new Variable(parts[0], parts[1], NO_CATEGORY);
	}
	
	/**
	 * Writes a Variable to the specified PrintWriter.
	 * 
	 * Outputs the Variable to the form:
	 * "ID: Value" where
	 * ID is the id associated with the current Variable,
	 * Value is the data assigned to the current Variable and
	 * : is the default separator character.
	 * 
	 * @param out the output stream to write the Variable to.
	 * @throws IOException if there was an error writing to the output stream.
	 */
	public void writeTo(PrintWriter out) throws IOException {
		writeTo(out, SEPARATORS.charAt(0));
	}
	
	/**
	 * Writes a Variable to the specified PrintWriter.
	 * 
	 * Outputs the Variable to the form:
	 * "ID: Value" where
	 * ID is the id associated with the current Variable,
	 * Value is the data assigned to the current Variable and
	 * : is the separator character.
	 * 
	 * @param out the output stream to write the Variable to.
	 * @param separator the character used to separate the id and value
	 * @throws IOException if there was an error writing to the output stream.
	 */
	public void writeTo(PrintWriter out, char separator) throws IOException {
		if(out == null || separator == '\0') { return; }
		
		out.println(m_id + separator + (separator == ':' ? " " : "") + m_value);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Variable)) {
			return false;
		}
		
		Variable v = (Variable) o;
		
		// return true if the id of each Variable matches
		return m_id.equalsIgnoreCase(v.m_id);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		// return a String representation of the Variable with the id and value separated by the separator character (default is colon)
		return m_id + SEPARATORS.charAt(0) + " " + m_value;
	}
	
	
}
