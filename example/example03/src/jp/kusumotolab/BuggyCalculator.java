package jp.kusumotolab;

public class BuggyCalculator {
	public int close_to_zero(int n) {
		if (n > 0) {
			n = Util.minus(n);
		} else {
			n = Util.plus(n);
		}
		InnerClass innerClass = new InnerClass();
		innerClass.exec();

		StaticInnerClass.exec();

		OuterClass outerClass = new OuterClass();
		outerClass.exec();

		return n;
	}

	// a simple inner class                            
	class InnerClass {
		void exec() {
			new String(); // do nothing but be measured by jacoco
		}
	}

	// a static inner class                            
	static class StaticInnerClass {
		static void exec() {
			new String(); // do nothing but be measured by jacoco
		}
	}
}

class OuterClass {
	void exec() {
		new String(); // do nothing but be measured by jacoco
	}

}