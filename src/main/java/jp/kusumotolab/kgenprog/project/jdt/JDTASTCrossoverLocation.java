package jp.kusumotolab.kgenprog.project.jdt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import jp.kusumotolab.kgenprog.project.SourcePath;

public class JDTASTCrossoverLocation extends JDTASTLocation {

  public JDTASTCrossoverLocation(final JDTASTLocation location) {
    super(location.getSourcePath(), location.getNode(), location.getGeneratedAST());
  }

  public JDTASTCrossoverLocation(final SourcePath sourcePath,
      final ASTNode node, final GeneratedJDTAST<?> generatedAST) {
    super(sourcePath, node, generatedAST);
  }

  @Override
  public ASTNode locate(final ASTNode otherASTRoot) {
    final List<ASTNode> treePaths = new ArrayList<>();
    ASTNode currentNode = node;
    do {
      treePaths.add(currentNode);
      currentNode = currentNode.getParent();
    } while (currentNode != null);

    treePaths.remove(treePaths.size() - 1);
    Collections.reverse(treePaths);

    List<ASTNode> possibleNode = new ArrayList<>();
    possibleNode.add(otherASTRoot);
    for (final ASTNode path : treePaths) {//これと比べる．これが正解．
      final List<ASTNode> nextPossibleNode = new ArrayList<>();
      for (final ASTNode current : possibleNode) {
        getChildren(current).stream()
            .filter(e -> isSameASTNodeType(path, e))
            .forEach(nextPossibleNode::add);
      }
      possibleNode = nextPossibleNode;
    }

    possibleNode = possibleNode.stream()
        .filter(e -> treePaths.get(treePaths.size() - 1)
            .toString()
            .equals(e.toString()))
        .collect(Collectors.toList());

    return possibleNode.isEmpty() ? null : possibleNode.get(0);
  }

  private List<ASTNode> getChildren(ASTNode node) {
    List<ASTNode> children = new ArrayList<>();
    for (final Object o : node.structuralPropertiesForType()) {
      Object child = node.getStructuralProperty((StructuralPropertyDescriptor) o);
      if (child instanceof ASTNode) {
        children.add((ASTNode) child);
      } else if (child instanceof List) {
        @SuppressWarnings("unchecked") // List の要素は必ず ASTNode
        final List<ASTNode> c = (List<ASTNode>) child;
        children.addAll(c);
      }
    }
    return children;
  }

  private boolean isSameASTNodeType(ASTNode a, ASTNode b) {
    return a.getClass() == b.getClass();
  }
}
