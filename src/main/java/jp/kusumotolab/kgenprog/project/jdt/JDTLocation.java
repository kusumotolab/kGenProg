package jp.kusumotolab.kgenprog.project.jdt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import jp.kusumotolab.kgenprog.project.Location;
import jp.kusumotolab.kgenprog.project.Range;
import jp.kusumotolab.kgenprog.project.SourcePath;

/**
 * JDT AST の単一ノードを示すオブジェクト Operation のターゲットに利用する
 * 
 * @see jp.kusumotolab.kgenprog.JDTOperaion
 * @author r-arima
 *
 */
final public class JDTLocation implements Location {

  final public ASTNode node;

  private SourcePath sourcePath;

  public JDTLocation(SourcePath sourcePath, ASTNode node) {
    this.node = node;
    this.sourcePath = sourcePath;
  }

  public ASTNode locate(ASTNode otherASTRoot) {
    List<TreePathElement> treePaths = new ArrayList<TreePathElement>();
    ASTNode currentNode = node;
    while (true) {
      StructuralPropertyDescriptor locationInParent = currentNode.getLocationInParent();
      if (locationInParent == null) {
        break;
      }

      ASTNode parent = currentNode.getParent();
      int idx = TreePathElement.NOT_LIST;

      if (locationInParent.isChildListProperty()) {
        // Listの場合、indexも覚えておく
        List<?> children = (List<?>) parent.getStructuralProperty(locationInParent);
        idx = children.indexOf(currentNode);
      }

      treePaths.add(new TreePathElement(locationInParent, idx));

      currentNode = parent;
    }

    Collections.reverse(treePaths);

    currentNode = otherASTRoot;
    for (TreePathElement path : treePaths) {
      currentNode = path.moveToChild(currentNode);
    }

    return currentNode;
  }

  private static class TreePathElement {

    public static final int NOT_LIST = -1;

    StructuralPropertyDescriptor descriptor;
    int idx;

    public TreePathElement(StructuralPropertyDescriptor descriptor, int idx) {
      this.descriptor = descriptor;
      this.idx = idx;
    }

    public ASTNode moveToChild(ASTNode current) {
      Object child = current.getStructuralProperty(descriptor);
      if (idx == NOT_LIST) {
        return (ASTNode) child;
      } else {
        return (ASTNode) ((List<?>) child).get(idx);
      }
    }
  }

  @Override
  public SourcePath getSourcePath() {
    return sourcePath;
  }

  @Override
  public Range inferLineNumbers() {
    ASTNode root = this.node.getRoot();

    if (!(root instanceof CompilationUnit)) {
      return Location.NONE;
    }

    CompilationUnit compilationUnit = (CompilationUnit) root;

    int start = compilationUnit.getLineNumber(this.node.getStartPosition());
    int end = compilationUnit.getLineNumber(this.node.getStartPosition() + this.node.getLength());

    return new Range(start, end);
  }
}
