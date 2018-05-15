package jp.kusumotolab.kgenprog.project.test;

import java.io.Serializable;
import java.util.List;

public class TestResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private FullyQualifiedName methodName;
	private boolean failed;
	private List<Coverage> coverages;

	/**
	 * constructor
	 * 
	 * @param methodName 実行したテストメソッドの名前
	 * @param failed テストの結果
	 * @param coverages テスト対象それぞれの行ごとのCoverage計測結果
	 */
	public TestResult(FullyQualifiedName methodName, boolean failed, List<Coverage> coverages) {
		this.methodName = methodName;
		this.failed = failed;
		this.coverages = coverages;
	}

	public boolean wasFailed() {
		return failed;
	}

	public FullyQualifiedName getMethodName() {
		return methodName;
	}

	public List<Coverage> getCoverages() {
		return coverages;
	}

}
