package jp.kusumotolab.kgenprog.project.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;

public class Coverage implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Status {
		/**
		 * Status flag for no items (value is 0x00).
		 */
		EMPTY,
		/**
		 * Status flag when all items are not covered (value is 0x01).
		 */
		NOT_COVERED,
		/**
		 * Status flag when all items are covered (value is 0x02).
		 */
		COVERED,
		/**
		 * Status flag when items are partly covered (value is 0x03). どういう時に起きるか不明．
		 */
		PARTLY_COVERED
	}

	final private FullyQualifiedName targetClassFQN;
	final private List<Status> statuses;

	/**
	 * 
	 * @param className
	 *            Coverage計測対象のクラス名
	 * @param statuses
	 *            Coverage計測の結果
	 */
	public Coverage(IClassCoverage classCoverage) {
		this.targetClassFQN = new FullyQualifiedName(classCoverage.getName().replaceAll("/", "."));
		this.statuses = convertClassCoverage(classCoverage);
	}

	/**
	 * ClassCoverageに格納されたCoverageをList<Status>に変換する． 実質enumの型変換やってるだけ．
	 * 
	 * @param classCoverage
	 * @return
	 */
	private List<Status> convertClassCoverage(IClassCoverage classCoverage) {
		final List<Coverage.Status> statuses = new ArrayList<>();
		for (int i = 1; i <= classCoverage.getLastLine(); i++) {
			final Coverage.Status status;
			final int s = classCoverage.getLine(i).getStatus();

			if (s == ICounter.EMPTY) {
				status = Coverage.Status.EMPTY;
			} else if (s == ICounter.FULLY_COVERED || s == ICounter.PARTLY_COVERED) {
				status = Coverage.Status.COVERED;
			} else if (s == ICounter.NOT_COVERED) {
				status = Coverage.Status.NOT_COVERED;
			} else {
				status = Coverage.Status.EMPTY;
			}
			statuses.add(status);
		}
		return statuses;
	}

	public String toString() {
		final StringBuffer sb = new StringBuffer();
		final String separator = " ";
		sb.append(targetClassFQN + "\n");
		for (int i = 0; i < statuses.size(); i++) {
			sb.append(String.format("%2d", i + 1));
			sb.append(separator);
		}
		sb.append("\n");
		for (Status status : statuses) {
			sb.append(String.format("%2d", status.ordinal()));
			sb.append(separator);
		}
		return sb.toString();
	}
}
