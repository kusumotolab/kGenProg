package jp.kusumotolab.kgenprog.project.jdt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.LineNumberRange;
import jp.kusumotolab.kgenprog.project.SourcePath;

/**
 * JDT AST の単一ノードを示すオブジェクト Operation のターゲットに利用する
 *
 * @author r-arima
 * @see jp.kusumotolab.kgenprog.project.jdt.JDTOperation
 */
public class JDTASTLocation implements ASTLocation {

  public final ASTNode node;
  protected final SourcePath sourcePath;
  protected final GeneratedJDTAST<?> generatedAST;

  public JDTASTLocation(final SourcePath sourcePath, final ASTNode node,
      final GeneratedJDTAST<?> generatedAST) {
    this.node = node;
    this.sourcePath = sourcePath;
    this.generatedAST = generatedAST;
  }

  /**
   * 別ASTでの同形部分を取り出す．
   * 具体的には，このLocationオブジェクトを起点とした親への木構造を取り出し，別ASTから同形部分を探索する．
   *
   * @param otherASTRoot 探索対象の別ASTのルート
   * @return 探索に成功したとき，その AST ノード．探索対象のAST ノードが存在しないとき null．
   */
  public ASTNode locate(final ASTNode otherASTRoot) {
    final List<TreePathElement> treePaths = makePath(node);
    treePaths.remove(0); // remove compilationUnit

    final List<ASTNode> candidates = locateByNodeDepthAndType(treePaths, otherASTRoot);

    //解が複数存在するときは，とりあえず最初のものを返している．
    //TODO: 解が複数存在するときの適切な振る舞いを考える．
    return candidates.isEmpty() ? null : candidates.get(0);
  }

  private List<ASTNode> locateByNodeDepthAndType(final List<TreePathElement> treePaths,
      final ASTNode otherASTRoot) {
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

    return candidateNodes.stream()
        .map(TreePathElement::getNode)
        .collect(Collectors.toList());
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

  private static boolean isSameSourceCode(final ASTNode a, final ASTNode b) {
    return a.toString()
        .compareTo(b.toString()) == 0;
  }

  @Override
  public LineNumberRange inferLineNumbers() {
    final ASTNode root = this.node.getRoot();

    if (!(root instanceof CompilationUnit)) {
      return ASTLocation.NONE;
    }

    final CompilationUnit compilationUnit = (CompilationUnit) root;

    final int start = compilationUnit.getLineNumber(this.node.getStartPosition());
    final int end =
        compilationUnit.getLineNumber(this.node.getStartPosition() + this.node.getLength());

    return new LineNumberRange(start, end);
  }

  @Override
  public SourcePath getSourcePath() {
    return sourcePath;
  }

  @Override
  public GeneratedJDTAST<?> getGeneratedAST() {
    return generatedAST;
  }

  public ASTNode getNode() {
    return node;
  }

  @Override
  public int hashCode() {
    return node.hashCode();
  }

  @Override
  public boolean equals(final Object o) {
    if (null == o) {
      return false;
    }

    if (!(o instanceof JDTASTLocation)) {
      return false;
    }

    final JDTASTLocation target = (JDTASTLocation) o;
    return node.equals(target.node);
  }

  @Override
  public String toString() {
    return node.toString();
  }

  @Override
  public boolean isStatement() {
    return node instanceof Statement;
  }

  @Override
  public boolean isExpression() {
    return node instanceof Expression;
  }

  private static class TreePathElement {

    private final ASTNode node;
    StructuralPropertyDescriptor descriptor;

    public TreePathElement(final ASTNode node) {
      this(node, node.getLocationInParent());
    }

    public TreePathElement(final ASTNode node, final StructuralPropertyDescriptor descriptor) {
      this.node = node;
      this.descriptor = descriptor;
    }

    private static boolean isSameNodeType(final ASTNode a, final ASTNode b) {
      return a.getClass() == b.getClass();
    }

    private static boolean isSameDescriptor(final TreePathElement a, final TreePathElement b) {
      return a.descriptor == b.descriptor;
    }

    public boolean isSameElementType(final TreePathElement t) {
      return isSameDescriptor(this, t)
          && isSameNodeType(this.node, t.node);
    }

    public ASTNode getNode() {
      return node;
    }
  }
}
