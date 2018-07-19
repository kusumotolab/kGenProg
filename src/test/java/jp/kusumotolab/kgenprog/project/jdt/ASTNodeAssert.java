package jp.kusumotolab.kgenprog.project.jdt;

import org.assertj.core.api.AbstractAssert;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class ASTNodeAssert extends AbstractAssert<ASTNodeAssert, ASTNode> {

  private static final CodeFormatter FORMATTER =
      ToolFactory.createCodeFormatter(JavaCore.getDefaultOptions());


  public ASTNodeAssert(final ASTNode actual) {
    super(actual, ASTNodeAssert.class);
  }

  public static ASTNodeAssert assertThat(final ASTNode actual) {
    return new ASTNodeAssert(actual);
  }

  /**
   * コーディングスタイルを正規化した上でASTの中身が同じソースコードかを判定する． 正規化にはある程度限界があるので注意 （空行が2つ以上連続するケースなど）
   * 
   * @param sourceCode
   * @return
   */
  public ASTNodeAssert isSameSourceCodeAs(final String sourceCode) {
    isNotNull();

    final String _actual = format(actual.toString()).trim();
    final String _expected = format(sourceCode).trim();

    if (!_actual.equals(_expected)) {
      failWithMessage("Expected souce code to be <%s> but was <%s>", _expected, _actual);
    }
    return this;
  }

  /**
   * コーディングスタイルを正規化した上でASTの中身が同じソースコードかを判定する．
   * 
   * @param sourceCode
   * @return
   */
  public ASTNodeAssert isSameSourceCodeAs(final ASTNode ast) {
    return isSameSourceCodeAs(ast.toString());
  }

  /**
   * eclipse.jdt.coreを使ったフォーマッタ．
   * 
   * @param source
   * @return
   */
  private String format(final String source) {
    final int kind = CodeFormatter.K_COMPILATION_UNIT;
    final int indentationLevel = 0;
    final String lineSeparator = "\n";
    final TextEdit textEdit =
        FORMATTER.format(kind, source, 0, source.length(), indentationLevel, lineSeparator);

    final IDocument document = new Document(source);

    try {
      textEdit.apply(document);
    } catch (MalformedTreeException | BadLocationException e) {
      failWithMessage("Source code <%s> cannot be formatted", source);
      return "";
    }
    return document.get();
  }
}
