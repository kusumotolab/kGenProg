package jp.kusumotolab.kgenprog.project.jdt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

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

  public List<ASTNode> getElements() {
    return elements;
  }

  public List<List<ASTNode>> getLineToElements() {
    return lineToElements;
  }

  protected void consumeElement(ASTNode s) {
    elements.add(s);

    int begin = unit.getLineNumber(s.getStartPosition());
    int end = unit.getLineNumber(s.getStartPosition() + s.getLength()) + 1;

    lineToElements.stream()
        .skip(begin)
        .limit(end - begin)
        .forEach(list -> list.add(s));
  }
}
