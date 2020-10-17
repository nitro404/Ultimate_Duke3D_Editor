package utilities;

public class ExtendedMath {

	public static final double HALF_PI = 1.57079632679489661923;
	public static final double QUARTER_PI = 0.78539816339744830962;
	public static final double TWO_PI = 6.28318530717958647693;

	public static double clamp(double value, double min, double max) {
		return value < min ? min : value > max ? max : value;
	}
	
	public static double difference(double a, double b) {
		return Math.abs(b - a);
	}
	
	public static double radiansToDegrees(double value) {
		return value * (180 / Math.PI);
	}
	
	public static double degreesToRadians(double value) {
		return value * (Math.PI / 180);
	}
	
	public static double compareAnglesDegrees(double a, double b) {
		if(a == b) {
			return 0;
		}

		double c = a % 360;
		double d = b % 360;

		if(c < 0) {
			c += 360;
		}

		if(d < 0) {
			d += 360;
		}

		if(c == d) {
			return 0;
		}

		return Math.cos(degreesToRadians(a - b) + (Math.PI / 2)) < 0 ? -1 : 1;
	}
	
	public static double compareAnglesRadians(double a, double b) {
		return compareAnglesDegrees(radiansToDegrees(a), radiansToDegrees(b));
	}
	
	public static double lerp(double a, double b, double amount) {
		if(amount == 0) {
			return a;
		}
		else if(amount == 1) {
			return b;
		}

		return a + ((b - a) * amount);
	}

	public static double normalize(double value, double min, double max) {
		return (value - min) / (max - min);
	}

}
