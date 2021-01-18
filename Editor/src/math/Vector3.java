package math;

public class Vector3 {

	public double x;
	public double y;
	public double z;

	public static Vector3 ZERO = new Vector3(0.0, 0.0, 0.0);
	public static Vector3 ONE = new Vector3(1.0, 1.0, 1.0);

	public Vector3() {
		this(0.0, 0.0, 0.0);
	}

	public Vector3(Vector3 v) {
		this(v.x, v.y, v.z);
	}

	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void multiply(double c) {
		x *= c;
		y *= c;
		z *= c;
	}

	public void multiply(Vector3 v) {
		x *= v.x;
		y *= v.y;
		z *= v.z;
	}

	public Vector3 multiplied(double c) {
		return new Vector3(x * c, y * c, z * c);
	}

	public Vector3 multiplied(Vector3 v) {
		return new Vector3(x * v.x, y * v.y, z * v.z);
	}

	public void divide(double c) {
		x /= c;
		y /= c;
		z /= c;
	}

	public void divide(Vector3 v) {
		x /= v.x;
		y /= v.y;
		z /= v.z;
	}

	public Vector3 divided(double c) {
		return new Vector3(x / c, y / c, z / c);
	}

	public Vector3 divided(Vector3 v) {
		return new Vector3(x / v.x, y / v.y, z / v.z);
	}

	public void add(double c) {
		x += c;
		y += c;
		z += c;
	}

	public void add(Vector3 v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}

	public Vector3 added(double c) {
		return new Vector3(x + c, y + c, z + c);
	}

	public Vector3 added(Vector3 v) {
		return new Vector3(x + v.x, y + v.y, z + v.z);
	}

	public void subtract(double c) {
		x -= c;
		y -= c;
		z -= c;
	}

	public void subtract(Vector3 v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}

	public Vector3 subtracted(double c) {
		return new Vector3(x - c, y - c, z - c);
	}

	public Vector3 subtracted(Vector3 v) {
		return new Vector3(x - v.x, y - v.y, z - v.z);
	}

	public void negate() {
		x = -x;
		y = -y;
		z = -z;
	}

	public Vector3 negated() {
		return new Vector3(-x, -y, -z);
	}

	public double length() {
		return Math.sqrt((x * x) + (y * y) + (z * z));
	}

	public double dot(Vector3 v) {
		return (x * v.x) + (y * v.y) + (z * v.z);
	}

	public Vector3 cross(Vector3 v) {
		return new Vector3((y * v.z) - (z * v.y), (z * v.x) - (x * v.z), (x * v.y) - (y * v.x));
	}

	public void normalize() {
		double l = length();

		if(l == 0) {
			x = 0;
			y = 0;
			z = 0;
		}
		else {
			x /= l;
			y /= l;
			z /= l;
		}
	}

	public Vector3 normalized() {
		double l = length();

		if(l == 0) {
			return new Vector3(0.0, 0.0, 0.0);
		}

		return new Vector3(x / l, y / l, z / l);
	}

	public Vector3 clone() {
		return new Vector3(x, y, z);
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof Vector3)) {
			return false;
		}

		Vector3 v = (Vector3) o;

		return x == v.x &&
			   y == v.y &&
			   z == v.z;
	}

	public String toString() {
		return x + ", " + y + ", " + z;
	}

}
