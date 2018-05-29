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
			System.out.println("InnerClass method eecuted.");
		}
	}
	
	// a static inner class
	static class StaticInnerClass {
		static void exec() {
			System.out.println("StaticInnerClass method executed.");
		}
	}
}

class OuterClass {
	void exec() {
		System.out.println("OuterClass method executed.");
	}
	
}