package jp.kusumotolab.kgenprog.project.jdt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * An abstract class for retrieving program elements with visitor pattern.
 * プログラム要素を取得するためビジターパターンのための抽象クラス．
 */
abstract public class ProgramElementVisitor extends ASTVisitor {

  protected List<ASTNode> elements;
  protected List<List<ASTNode>> lineToElements;
  private CompilationUnit unit;

  public void analyzeElements(final CompilationUnit unit) {
    elements = new ArrayList<>();
    this.unit = unit;
    int lineNumberLength = unit.getLineNumber(unit.getLength() - 1);
    this.lineToElements = IntStream.rangeClosed(0, lineNumberLength)
        .mapToObj(v -> new ArrayList<ASTNode>(0))
        .collect(Collectors.toList());

    unit.accept(this);
  }

  /**
   * return retrieved program elements.
   * 取得した要素を返す．
   *
   * @return 取得したプログラム要素
   */
  public List<ASTNode> getElements() {
    return elements;
  }

  /**
   * return program elements in each program line.
   * 各行に存在するプログラム要素を返す．
   *
   * @return 各行におけるプログラム要素
   */
  public List<List<ASTNode>> getLineToElements() {
    return lineToElements;
  }

  protected void consumeElement(ASTNode s) {

    if (s instanceof Block) {
      final Block block = (Block) s;
      final List<?> statements = block.statements();

      // 内包する文が2つ以上でないときは，BlockはFLの対象としない
      if (statements.size() < 2) {
        return;
      }

      // メソッドのBlockである場合は，FLの対象としない
      if (block.getParent() instanceof MethodDeclaration) {
        return;
      }
    }

    elements.add(s);

    int begin = unit.getLineNumber(s.getStartPosition());
    int end = unit.getLineNumber(s.getStartPosition() + s.getLength()) + 1;

    lineToElements.stream()
        .skip(begin)
        .limit(end - begin)
        .forEach(list -> list.add(s));
  }
}
