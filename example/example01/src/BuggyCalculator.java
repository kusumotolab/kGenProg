public class BuggyCalculator {

	/**
	 * 整数をゼロに一つ近づけるメソッド
	 * 
	 * bug: 0を与えたときに0であるべきが1になる
	 * 
	 * fix: たくさんの修正方法がある．詳細は本クラス内のreuse_me()メソッド参照
	 * 
	 * @param n
	 * @return
	 */
	public int close_to_zero(int n) {
		if (n > 0) {
			n--;
		} else {
			n++;
		}
		return n;
	}

	////////////////////////////////////////////////////////////////////////////////
	// 再利用されるべきメソッド1
	public void reuse_me1(int n) {
		if (n > 0) {
		} else if (n == 0) { // このif文がif-elseの間に入ればOK
		}
	}

	// 再利用されるべきメソッド2
	public int reuse_me2(int n) {
		if (n == 0) { // このifブロックが，ifの前，あるいはif(n > 0)の中に入ればOK
			return n;
		}
		return 0;
	}

	// 再利用されるべきメソッド3
	public void reuse_me3(int n) {
		if (n == 0) { // このif文がelseの中に入ればOK
			n--;
		}
	}

	// 再利用されるべきメソッド4
	public void reuse_me4(int n) {
		if (n == 0) {
			n++;
		}
	}

	// 再利用されても意味のないメソッド
	public void reuse_me_fake(int n) {
		int i = 0;
		i++;
		n += i;
	}
	
	public static void main(String args[]) {
		;
	}

}
