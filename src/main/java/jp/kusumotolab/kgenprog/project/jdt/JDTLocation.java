package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.ASTNode;

import jp.kusumotolab.kgenprog.project.Location;

/**
 * JDT AST の単一ノードを示すオブジェクト
 * Operation のターゲットに利用する
 * @see jp.kusumotolab.kgenprog.JDTOperaion
 * @author k-naitou
 *
 */
final public class JDTLocation implements Location {
	final public ASTNode node;

	public JDTLocation(ASTNode node) {
		this.node = node;
	}
}
