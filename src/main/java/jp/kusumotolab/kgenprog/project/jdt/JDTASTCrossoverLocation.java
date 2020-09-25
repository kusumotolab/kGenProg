package jp.kusumotolab.kgenprog.project.jdt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    final List<ASTNode> treePaths = new ArrayList<>();
    ASTNode currentNode = node;
    while (currentNode != null) {
      treePaths.add(currentNode);
      currentNode = currentNode.getParent();
    }

    treePaths.remove(treePaths.size() - 1); // remove compilationunit
    Collections.reverse(treePaths);

    List<ASTNode> possibleNodes = new ArrayList<>();
    possibleNodes.add(otherASTRoot);
    for (final ASTNode path : treePaths) {//これと比べる．これが正解．
      final List<ASTNode> nextPossibleNodes = new ArrayList<>();
      for (final ASTNode current : possibleNodes) {
        getChildren(current).stream()
            .filter(e -> isSameASTNodeType(path, e))
            .forEach(nextPossibleNodes::add);
      }
      possibleNodes = nextPossibleNodes;
    }

    possibleNodes = possibleNodes.stream()
        .filter(e -> isSameSourceCode(node, e))
        .collect(Collectors.toList());

    //解が複数存在するときは，とりあえず最初のものを返している．
    //TODO: 解が複数存在するときの適切な振る舞いを考える．
    return possibleNodes.isEmpty() ? null : possibleNodes.get(0);
  }

  private List<ASTNode> getChildren(final ASTNode node) {
    final List<ASTNode> children = new ArrayList<>();
    for (final Object o : node.structuralPropertiesForType()) {
      final Object childOrChildren = node.getStructuralProperty((StructuralPropertyDescriptor) o);
      if (childOrChildren instanceof ASTNode) {
        children.add((ASTNode) childOrChildren);
      } else if (childOrChildren instanceof List) {
        @SuppressWarnings("unchecked") // このとき，List の要素は必ず ASTNode
        final List<ASTNode> c = (List<ASTNode>) childOrChildren;
        children.addAll(c);
      }
    }
    return children;
  }

  private boolean isSameASTNodeType(final ASTNode a, final ASTNode b) {
    return a.getClass() == b.getClass();
  }

  private boolean isSameSourceCode(final ASTNode a, final ASTNode b) {
    return a.toString()
        .compareTo(b.toString()) == 0;
  }
}
