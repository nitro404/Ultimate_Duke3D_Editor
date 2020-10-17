package utilities;

import java.util.*;
import java.io.*;

public class Serializer {
	
	private Serializer() {
		
	}
	
	// serialize a boolean to a byte array
	public static byte[] serializeBoolean(boolean b) {
		byte[] data = new byte[1];
		data[0] = (byte) (b ? 1 : 0);
		return data;
	}
	
	// deserialize a byte array back into a boolean
	public static boolean deserializeBoolean(byte[] data) {
		if(data == null || data.length != 1) { return false; }
		return (boolean) (data[0] != 0);
	}
	
	// serialize a short to a byte array using default endianness
	public static byte[] serializeShort(short s) {
		return serializeShort(s, Endianness.defaultEndianness);
	}
	
	// serialize a short to a byte array
	public static byte[] serializeShort(short s, Endianness e) {
		if(!e.isValid()) { return null; }
		byte[] data = new byte[2];
		data[e == Endianness.BigEndian ? 0 : 1] = (byte) (s >> 8);
		data[e == Endianness.BigEndian ? 1 : 0] = (byte) (s);
		return data;
	}
	
	// deserialize a byte array back into a short using default endinanness
	public static short deserializeShort(byte[] data) {
		return deserializeShort(data, Endianness.defaultEndianness);
	}
	
	// deserialize a byte array back into a short
	public static short deserializeShort(byte[] data, Endianness e) {
		if(!e.isValid()) { return -1; }
		if(data == null || data.length != 2) { return -1; }
		return (short) (data[e == Endianness.BigEndian ? 0 : 1] << 8
					 | (data[e == Endianness.BigEndian ? 1 : 0] & 0xff));
	}
	
	// serialize an integer to a byte array using default endinanness
	public static byte[] serializeInteger(int i) {
		return serializeInteger(i, Endianness.defaultEndianness);
	}
	
	// serialize an integer to a byte array
	public static byte[] serializeInteger(int i, Endianness e) {
		if(!e.isValid()) { return null; }
		byte[] data = new byte[4];
		data[e == Endianness.BigEndian ? 0 : 3] = (byte) (i >> 24);
		data[e == Endianness.BigEndian ? 1 : 2] = (byte) (i >> 16);
		data[e == Endianness.BigEndian ? 2 : 1] = (byte) (i >> 8);
		data[e == Endianness.BigEndian ? 3 : 0] = (byte) (i);
		return data;
	}
	
	// deserialize a byte array back into an integer using default endinanness
	public static int deserializeInteger(byte[] data) {
		return deserializeInteger(data, Endianness.defaultEndianness);
	}
	
	// deserialize a byte array back into an integer
	public static int deserializeInteger(byte[] data, Endianness e) {
		if(!e.isValid()) { return -1; }
		if(data == null || data.length != 4) { return -1; }
		return (int) (data[e == Endianness.BigEndian ? 0 : 3] << 24
				   | (data[e == Endianness.BigEndian ? 1 : 2] & 0xff) << 16
				   | (data[e == Endianness.BigEndian ? 2 : 1] & 0xff) << 8
				   | (data[e == Endianness.BigEndian ? 3 : 0] & 0xff));
	}
	
	// serialize a long to a byte array using default endinanness
	public static byte[] serializeLong(long l) {
		return serializeLong(l, Endianness.defaultEndianness);
	}
	
	// serialize a long to a byte array
	public static byte[] serializeLong(long l, Endianness e) {
		if(!e.isValid()) { return null; }
		byte[] data = new byte[8];
		data[e == Endianness.BigEndian ? 0 : 7] = (byte) (l >> 56);
		data[e == Endianness.BigEndian ? 1 : 6] = (byte) (l >> 48);
		data[e == Endianness.BigEndian ? 2 : 5] = (byte) (l >> 40);
		data[e == Endianness.BigEndian ? 3 : 4] = (byte) (l >> 32);
		data[e == Endianness.BigEndian ? 4 : 3] = (byte) (l >> 24);
		data[e == Endianness.BigEndian ? 5 : 2] = (byte) (l >> 16);
		data[e == Endianness.BigEndian ? 6 : 1] = (byte) (l >> 8);
		data[e == Endianness.BigEndian ? 7 : 0] = (byte) (l);
		return data;
	}
	
	// deserialize a byte array back into a long using default endinanness
	public static long deserializeLong(byte[] data) {
		return deserializeLong(data, Endianness.defaultEndianness);
	}
	
	// deserialize a byte array back into a long
	public static long deserializeLong(byte[] data, Endianness e) {
		if(!e.isValid()) { return -1L; }
		if(data == null || data.length != 8) { return -1; }
		long l = 0;
		int x;
		for(int i=0;i<8;++i) {
			x = e == Endianness.BigEndian ? i : 7 - i;
			l |= ((long) data[x] & 0xff) << ((8-x-1) << 3);
		}
		return l;
	}
	
	// serialize a float into a byte array
	public static byte[] serializeFloat(float f) {
		return serializeInteger(Float.floatToIntBits(f));
	}
	
	// deserialize a byte array back into a float
	public static float deserializeFloat(byte[] data) {
		return Float.intBitsToFloat(deserializeInteger(data));
	}
	
	// serialize a double into a byte array
	public static byte[] serializeDouble(double d) {
		return serializeLong(Double.doubleToLongBits(d));
	}
	
	// deserialize a byte array back into a double
	public static double deserializeDouble(byte[] data) {
		return Double.longBitsToDouble(deserializeLong(data));
	}
	
	// serialize a single byte character to a byte
	public static byte serializeByteCharacter(char c) {
		return (byte) c;
	}
	
	// deserialize a byte into a single byte character
	public static char deserializeByteCharacter(byte data) {
		return (char) (data & 0xff);
	}
	
	// serialize a character to a byte array using default endinanness
	public static byte[] serializeCharacter(char c) {
		return serializeCharacter(c, Endianness.defaultEndianness);
	}
	
	// serialize a character to a byte array
	public static byte[] serializeCharacter(char c, Endianness e) {
		if(!e.isValid()) { return null; }
		byte[] data = new byte[2];
		data[e == Endianness.BigEndian ? 0 : 1] = (byte) (c >> 8);
		data[e == Endianness.BigEndian ? 1 : 0] = (byte) (c);
		return data;
	}
	
	// deserialize a byte array back into a character using default endinanness
	public static char deserializeCharacter(byte[] data) {
		return deserializeCharacter(data, Endianness.defaultEndianness);
	}
	
	// deserialize a byte array back into a character
	public static char deserializeCharacter(byte[] data, Endianness e) {
		if(!e.isValid()) { return '\0'; }
		if(data == null || data.length != 2) { return '\0'; }
		return (char) (data[e == Endianness.BigEndian ? 0 : 1] << 8
					| (data[e == Endianness.BigEndian ? 1 : 0] & 0xff));
	}
	
	// serialize the specified byte string
	public static byte[] serializeByteString(String s) {
		if(s == null) { return null; }
		if(s.isEmpty()) { return null; }
		
		byte[] data = new byte[s.length()];
		
		// serialize and store the bytes for each character in the string
		int j = 0;
		for(int i=0;i<s.length();i++) {
			data[j++] = (byte) s.charAt(i);
		}
		
		return data;
	}
	
	// de-serialize the specified byte string
	public static String deserializeByteString(byte[] data) {
		if(data == null) { return null; }
		if(data.length == 0) { return null; }
		
		String s = "";
		char c = '\0';
		
		for(int i=0;i<data.length;i++) {
			c = (char) (data[i] & 0xff);
			if(c == '\0') { break; }
			s += c;
		}
		
		return s;
	}
	
	// serialize the specified string using default endianness
	public static byte[] serializeString(String s) {
		return serializeString(s, Endianness.defaultEndianness);
	}
	
	// serialize the specified string
	public static byte[] serializeString(String s, Endianness e) {
		if(s == null || s.isEmpty() || !e.isValid()) { return null; }
		
		byte[] data = new byte[s.length() * 2];
		
		// serialize and store the bytes for each character in the string
		int j = 0;
		for(int i=0;i<s.length();i++) {
			data[e == Endianness.BigEndian ? j : j + 1] = (byte) (s.charAt(i) >> 8);
			data[e == Endianness.BigEndian ? j + 1 : j] = (byte) (s.charAt(i));
			j += 2;
		}
		
		return data;
	}
	
	// de-serialize the specified string using default endianness
	public static String deserializeString(byte[] data) {
		return deserializeString(data, Endianness.defaultEndianness);
	}
	
	// de-serialize the specified string
	public static String deserializeString(byte[] data, Endianness e) {
		if(data == null || data.length == 0 || data.length % 2 != 0 || !e.isValid()) { return null; }
		
		String s = "";
		
		for(int i=0;i<data.length;i+=2) {
			s += (char) (data[e == Endianness.BigEndian ? i : i + 1] << 8
					  | (data[e == Endianness.BigEndian ? i + 1 : i] & 0xff));
		}
		
		return s;
	}
	
	// return a byte array representing the serialized version of an object
	public static byte[] serializeObject(Object o) throws IOException {
		if(o == null) { return null; }
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objectStream = null;
		
	    objectStream = new ObjectOutputStream(byteStream);
	    objectStream.writeObject(o);
	    objectStream.close();
		
	    return byteStream.toByteArray();
	}
	
	// return an object which has been deserialized from a byte array
	public static Object deserializeObject(byte[] data) throws IOException {
		if(data == null || data.length < 1) { return null; }
		
		Object object;
		ObjectInputStream objectStream = null;
		try {
			objectStream = new ObjectInputStream(new ByteArrayInputStream(data));
			object = objectStream.readObject();
			objectStream.close();
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		return object;
	}
	
	// serializes the specified array of objects
	public static byte[] serializeArray(Object[] objects) throws IOException {
		if(objects == null || objects.length == 0) { return null; }
		
		Vector<byte[]> serializedObjects = new Vector<byte[]>(objects.length);
		
		int length = 4;
		
		// serialize and store the bytes for each object
		byte[] serializedObject = null;
		for(int i=0;i<objects.length;i++) {
			serializedObject = serializeObject(objects[i]);
			if(serializedObject == null) {
				throw new IOException("error serializing object");
			}
			length += 4 + serializedObject.length;
			serializedObjects.add(serializedObject);
		}
		
		// initialize the byte stream
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(length);
		
		// write the serialized bytes for the number of objects
		byteStream.write(serializeInteger(serializedObjects.size()));
		
		// write the serialized bytes for the size of the serialized object, followed by the serialized bytes of each object
		for(int i=0;i<serializedObjects.size();i++) {
			byteStream.write(serializeInteger(serializedObjects.elementAt(i).length));
			byteStream.write(serializedObjects.elementAt(i));
		}
		
		return byteStream.toByteArray();
	}
	
	// de-serializes the specified array of objects
	public static Object[] deserializeArray(byte[] data) throws IOException {
		if(data == null || data.length == 0) { return null; }
		
		int length, numberOfObjects = -1;
		byte[] temp;
		Object[] objects;
		ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
		
		// read and de-serialize the bytes for the number of objects
		temp = new byte[4];
		byteStream.read(temp);
		numberOfObjects = deserializeInteger(temp);
		if(numberOfObjects < 1) { return null; }
		objects = new Object[numberOfObjects];
		
		// read all of the objects
		for(int i=0;i<numberOfObjects;i++) {
			// read and de-serialize the bytes for the size of the object
			temp = new byte[4];
			byteStream.read(temp);
			length = deserializeInteger(temp);
			if(length < 1) { return null; }
			
			// read and de-serialize the bytes for each object
			temp = new byte[length];
			byteStream.read(temp);
			objects[i] = deserializeObject(temp);
			if(objects[i] == null) { return null; }
		}
		
		return objects;
	}
	
	// read a serialized boolean off of a specified input stream
	public static boolean readBoolean(InputStream in) throws IOException {
		if(in == null) { return false; }
		
		byte[] data = new byte[1];
		
		in.read(data);
		
		return deserializeBoolean(data);
	}
	
	// serialize and write a boolean to a specified output stream
	public static boolean writeBoolean(boolean b, OutputStream out) throws IOException {
		if(out == null) { return false; }
		
		byte[] data = serializeBoolean(b);
		
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized short off of a specified input stream using default endianness
	public static short readShort(InputStream in) throws IOException {
		return readShort(in, Endianness.defaultEndianness);
	}
	
	// read a serialized short off of a specified input stream
	public static short readShort(InputStream in, Endianness e) throws IOException {
		if(in == null || !e.isValid()) { return -1; }
		
		byte[] data = new byte[2];
		
		in.read(data);
		
		return deserializeShort(data, e);
	}
	
	// serialize and write a short to a specified output stream using default endianness
	public static boolean writeShort(short s, OutputStream out) throws IOException {
		return writeShort(s, out, Endianness.defaultEndianness);
	}
	
	// serialize and write a short to a specified output stream
	public static boolean writeShort(short s, OutputStream out, Endianness e) throws IOException {
		if(out == null || !e.isValid()) { return false; }
		
		byte[] data = serializeShort(s, e);
		
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized integer off of a specified input stream using default endianness
	public static int readInteger(InputStream in) throws IOException {
		return readInteger(in, Endianness.defaultEndianness);
	}
	
	// read a serialized integer off of a specified input stream
	public static int readInteger(InputStream in, Endianness e) throws IOException {
		if(in == null || !e.isValid()) { return -1; }
		
		byte[] data = new byte[4];
		
		in.read(data);
		
		return deserializeInteger(data, e);
	}
	
	// serialize and write an integer to a specified output stream using default endianness
	public static boolean writeInteger(int i, OutputStream out) throws IOException {
		return writeInteger(i, out, Endianness.defaultEndianness);
	}
	
	// serialize and write an integer to a specified output stream
	public static boolean writeInteger(int i, OutputStream out, Endianness e) throws IOException {
		if(out == null) { return false; }
		
		byte[] data = serializeInteger(i, e);
		
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized long off of a specified input stream using default endianness
	public static long readLong(InputStream in) throws IOException {
		return readLong(in, Endianness.defaultEndianness);
	}
	
	// read a serialized long off of a specified input stream
	public static long readLong(InputStream in, Endianness e) throws IOException {
		if(in == null || !e.isValid()) { return -1; }
		
		byte[] data = new byte[8];
		
		in.read(data);
		
		return deserializeLong(data, e);
	}
	
	// serialize and write a long to a specified output stream using default endianness
	public static boolean writeLong(long l, OutputStream out) throws IOException {
		return writeLong(l, out, Endianness.defaultEndianness);
	}
	
	// serialize and write a long to a specified output stream
	public static boolean writeLong(long l, OutputStream out, Endianness e) throws IOException {
		if(out == null || !e.isValid()) { return false; }
		
		byte[] data = serializeLong(l, e);
		
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized float off of a specified input stream
	public static float readFloat(InputStream in) throws IOException {
		if(in == null) { return -1; }
		
		byte[] data = new byte[4];
		
		in.read(data);
		
		return deserializeFloat(data);
	}
	
	// serialize and write a float to a specified output stream
	public static boolean writeFloat(float f, OutputStream out) throws IOException {
		if(out == null) { return false; }
		
		byte[] data = serializeFloat(f);
		
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized double off of a specified input stream
	public static double readDouble(InputStream in) throws IOException {
		if(in == null) { return -1; }
		
		byte[] data = new byte[8];
		
		in.read(data);
		
		return deserializeDouble(data);
	}
	
	// serialize and write a double to a specified output stream
	public static boolean writeDouble(double d, OutputStream out) throws IOException {
		if(out == null) { return false; }
		
		byte[] data = serializeDouble(d);
		
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized single byte character off of a specified input stream
	public static char readByteCharacter(InputStream in) throws IOException {
		if(in == null) { return '\0'; }
		
		byte[] data = new byte[1];
		
		in.read(data);
		
		return (char) (data[0] & 0xff);
	}
	
	// serialize and write a single byte character to a specified output stream
	public static boolean writeByteCharacter(char c, OutputStream out) throws IOException {
		if(out == null) { return false; }
		
		byte[] data = { (byte) c };
		
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized character off of a specified input stream using default endianness
	public static char readCharacter(InputStream in) throws IOException {
		return readCharacter(in, Endianness.defaultEndianness);
	}
	
	// read a serialized character off of a specified input stream
	public static char readCharacter(InputStream in, Endianness e) throws IOException {
		if(in == null || !e.isValid()) { return '\0'; }
		
		byte[] data = new byte[2];
		
		in.read(data);
		
		return deserializeCharacter(data, e);
	}
	
	// serialize and write a character to a specified output stream using default endianness
	public static boolean writeCharacter(char c, OutputStream out) throws IOException {
		return writeCharacter(c, out, Endianness.defaultEndianness);
	}
	
	// serialize and write a character to a specified output stream
	public static boolean writeCharacter(char c, OutputStream out, Endianness e) throws IOException {
		if(out == null || !e.isValid()) { return false; }
		
		byte[] data = serializeCharacter(c, e);
		
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read characters off of the specified input stream until a newline or cr character is encountered using default endianness
	public static String readLine(InputStream in) throws IOException {
		return readLine(in, Endianness.defaultEndianness);
	}

	// read characters off of the specified input stream until a newline or cr character is encountered
	public static String readLine(InputStream in, Endianness e) throws IOException {
		if(in == null || !e.isValid()) { return null; }
		
		char c = '\0';
		String s = new String("");
		
		// accumulate the string until a newline character is encountered
		byte[] data = new byte[2];
		while(true) {
			in.read(data);
			c = deserializeCharacter(data, e);
			if(c == '\n' || c == '\r') { break; }
			
			s += c;
		}
		return s;
	}
	
	// read characters off of the specified input stream until a newline, cr, space or tab character is encountered using default endianness
	public static String readToken(InputStream in) throws IOException {
		return readToken(in, Endianness.defaultEndianness);
	}
	
	// read characters off of the specified input stream until a newline, cr, space or tab character is encountered
	public static String readToken(InputStream in, Endianness e) throws IOException {
		if(in == null || !e.isValid()) { return null; }
		
		char c = '\0';
		String s = new String("");
		
		// accumulate the string until a newline or spacing character is encountered
		byte[] data = new byte[2];
		while(true) {
			in.read(data);
			c = deserializeCharacter(data, e);
			if(c == '\n' || c == '\r' || c == '\t' || c == ' ') { break; }
			
			s += c;
		}
		return s;
	}
	
	// write a single byte string to the specified output stream
	public static boolean writeByteString(String s, OutputStream out) throws IOException {
		if(s == null || out == null) { return false; }
		
		// serialize and write each single byte character in the string
		for(int i=0;i<s.length();i++) {
			byte[] data = { (byte) s.charAt(i) };
			out.write(data, 0, data.length);
		}
		
		return true;
	}
	
	// write a string to the specified output stream
	public static boolean writeString(String s, OutputStream out) throws IOException {
		if(s == null || out == null) { return false; }
		
		// serialize and write each character in the string
		for(int i=0;i<s.length();i++) {
			byte[] data = serializeCharacter(s.charAt(i));
			out.write(data, 0, data.length);
		}
		
		return true;
	}
	
	// read a serialized object from a specified input stream
	public static Object readObject(InputStream in) throws IOException {
		if(in == null) { return null; }
		
		byte[] lengthData = new byte[4];
		in.read(lengthData);
		int length = deserializeInteger(lengthData);
		if(length < 1) { return null; }
		
		byte[] data = new byte[length];
		in.read(data);
		
		return deserializeObject(data);
	}
	
	// serialize and write an object to a specified output stream 
	public static boolean writeObject(Object o, OutputStream out) throws IOException {
		if(o == null || out == null) { return false; }
		
		byte[] data = serializeObject(o);
		if(data == null) { return false; }
		
		byte[] lengthData = serializeInteger(data.length);
		if(lengthData == null) { return false; }
		
		out.write(lengthData, 0, data.length);
		out.write(data, 0, data.length);
		
		return true;
	}
	
	// read a serialized array from the specified input stream
	public static Object[] readArray(InputStream in) throws IOException {
		if(in == null) { return null; }
		
		byte[] lengthData = new byte[4];
		in.read(lengthData);
		int length = deserializeInteger(lengthData);
		if(length < 1) { return null; }
		
		byte[] data = new byte[length];
		in.read(data);
		
		return deserializeArray(data);
	}
	
	// serialize and write an array to the specified output stream
	public static boolean writeArray(Object[] array, OutputStream out) throws IOException {
		if(array == null || out == null) { return false; }
		
		byte[] data = serializeArray(array);
		if(data == null) { return false; }
		
		byte[] lengthData = serializeInteger(data.length);
		if(lengthData == null) { return false; }
		
		out.write(lengthData, 0, data.length);
		out.write(data, 0, data.length);
		
		return true;
	}
	
}
