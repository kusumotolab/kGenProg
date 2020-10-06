package jp.kusumotolab.kgenprog.project.jdt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

/**
 * JDT AST の単一ノードを示すオブジェクト 交叉で個体を生成するときの Operation のターゲットに利用する．
 */
public class JDTASTCrossoverLocation extends JDTASTLocation {

  public JDTASTCrossoverLocation(final JDTASTLocation location) {
    super(location.getSourcePath(), location.getNode(), location.getGeneratedAST());
  }

  /**
   * @param otherASTRoot 探索対象の別 AST のルート
   * @return 探索に成功したとき，その AST ノード．探索対象のAST ノードが存在しないとき null．
   */
  @Override
  public ASTNode locate(final ASTNode otherASTRoot) {

    final List<TreePathElement> treePaths = makePath(node);
    treePaths.remove(0); // remove compilationUnit

    List<TreePathElement> candidateNodes = new ArrayList<>();
    candidateNodes.add(new TreePathElement(otherASTRoot));

    for (final TreePathElement path : treePaths) {//これと比べる．これが正解．
      final List<TreePathElement> nextCandidateNodes = new ArrayList<>();
      for (final TreePathElement current : candidateNodes) {
        getChildren(current.getNode()).stream()
            .filter(path::isSameElementType)
            .forEach(nextCandidateNodes::add);
      }
      candidateNodes = nextCandidateNodes;
    }

    candidateNodes.removeIf(e -> !isSameSourceCode(node, e.getNode()));

    //解が複数存在するときは，とりあえず最初のものを返している．
    //TODO: 解が複数存在するときの適切な振る舞いを考える．
    return candidateNodes.isEmpty() ? null : candidateNodes.get(0)
        .getNode();
  }

  private List<TreePathElement> makePath(final ASTNode dest) {
    final List<TreePathElement> treePaths = new ArrayList<>();
    ASTNode currentNode = dest;
    while (currentNode != null) {
      treePaths.add(new TreePathElement(currentNode));
      currentNode = currentNode.getParent();
    }

    Collections.reverse(treePaths);
    return treePaths;
  }

  private List<TreePathElement> getChildren(final ASTNode node) {
    final List<TreePathElement> children = new ArrayList<>();
    for (final Object o : node.structuralPropertiesForType()) {
      final Object childOrChildren = node.getStructuralProperty((StructuralPropertyDescriptor) o);
      if (childOrChildren instanceof ASTNode) {
        children.add(new TreePathElement((ASTNode) childOrChildren));
      } else if (childOrChildren instanceof List) {
        @SuppressWarnings("unchecked") // このとき，List の要素は必ず ASTNode
        final List<ASTNode> c = (List<ASTNode>) childOrChildren;
        c.stream()
            .map(TreePathElement::new)
            .forEach(children::add);
      }
    }
    return children;
  }


  private boolean isSameSourceCode(final ASTNode a, final ASTNode b) {
    return a.toString()
        .compareTo(b.toString()) == 0;
  }

  private static class TreePathElement {

    private final ASTNode node;
    private final StructuralPropertyDescriptor descriptor;

    public TreePathElement(final ASTNode node) {
      this(node, node.getLocationInParent());
    }

    public TreePathElement(final ASTNode node, final StructuralPropertyDescriptor descriptor) {
      this.node = node;
      this.descriptor = descriptor;
    }

    public boolean isSameElementType(final TreePathElement t) {
      return isSameDescriptor(this, t)
          && isSameNodeType(this.getNode(), t.getNode());
    }

    private boolean isSameNodeType(final ASTNode a, final ASTNode b) {
      return a.getClass() == b.getClass();
    }

    private boolean isSameDescriptor(final TreePathElement a, final TreePathElement b) {
      return a.descriptor == b.descriptor;
    }

    public ASTNode getNode() {
      return node;
    }
  }
}
