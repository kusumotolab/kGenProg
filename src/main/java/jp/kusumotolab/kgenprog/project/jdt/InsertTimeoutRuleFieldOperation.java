package jp.kusumotolab.kgenprog.project.jdt;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.TestSourcePath;


public class InsertTimeoutRuleFieldOperation implements Operation {

  private static final Logger log = LoggerFactory.getLogger(InsertTimeoutRuleFieldOperation.class);

  private static final String INSERT_FIELD_NAME = "globalTimeout";
  private final long timeoutSeconds;

  public InsertTimeoutRuleFieldOperation(final long timeoutSeconds) {
    this.timeoutSeconds = timeoutSeconds;
  }

  @Override
  public GeneratedSourceCode apply(final GeneratedSourceCode generatedSourceCode,
      final ASTLocation location) {

    try {
      final List<GeneratedAST<TestSourcePath>> newTestAsts = generatedSourceCode.getTestAsts()
          .stream()
          .map(ast -> applyEachAST(ast))
          .collect(Collectors.toList());
      return new GeneratedSourceCode(generatedSourceCode.getProductAsts(), newTestAsts);
    } catch (final Exception e) {
      log.warn("Inserting timeout rule is failed: {}", e.getMessage());
      log.trace("Trace:", e);
      return generatedSourceCode;
    }
  }

  private GeneratedAST<TestSourcePath> applyEachAST(final GeneratedAST<TestSourcePath> ast) {
    final GeneratedJDTAST<TestSourcePath> jdtast = (GeneratedJDTAST<TestSourcePath>) ast;
    final ASTRewrite astRewrite = ASTRewrite.create(jdtast.getRoot()
        .getAST());

    ASTStream.stream(jdtast.getRoot())
        .filter(TypeDeclaration.class::isInstance)
        .map(TypeDeclaration.class::cast)
        .forEach(d -> insertField(astRewrite, d));

    final Document document = new Document(jdtast.getSourceCode());
    try {
      astRewrite.rewriteAST(document, null)
          .apply(document);
    } catch (MalformedTreeException | BadLocationException e) {
      throw new RuntimeException(e);
    }

    return jdtast.getConstruction()
        .constructAST(ast.getSourcePath(), document.get());
  }

  /**
   * クラスにフィールドを追加する
   *
   * @param astRewrite ASTRewrite
   * @param target 追加対象
   */
  private void insertField(final ASTRewrite astRewrite, final TypeDeclaration target) {
    final String fieldName = createFieldName(target);

    final ListRewrite listRewrite =
        astRewrite.getListRewrite(target, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);

    final ASTNode astNode = ASTNode.copySubtree(astRewrite.getAST(), createInsertField(fieldName));
    listRewrite.insertFirst(astNode, null);
  }


  /**
   * 重複しない変数名を生成する
   *
   * @param typeDeclaration 重複チェックをするクラス
   * @return 生成された変数名
   */
  @SuppressWarnings("unchecked")
  private String createFieldName(final TypeDeclaration typeDeclaration) {
    final FieldDeclaration[] fields = typeDeclaration.getFields();
    final Set<String> fieldNames = Arrays.stream(fields)
        .flatMap(f -> ((List<VariableDeclarationFragment>) (f.fragments())).stream())
        .map(VariableDeclarationFragment::getName)
        .map(SimpleName::getIdentifier)
        .collect(Collectors.toSet());

    String insertFieldName = INSERT_FIELD_NAME;
    while (fieldNames.contains(insertFieldName)) {
      insertFieldName = "_" + insertFieldName;
    }

    return insertFieldName;
  }

  /**
   * 挿入対象のFieldDeclarationを生成する
   *
   * @param fieldName 生成するフィールドの変数名
   * @return 生成されたFieldDeclaration
   */
  private FieldDeclaration createInsertField(final String fieldName) {
    final char[] insertSourceCode = new StringBuilder().append("")
        .append("@org.junit.Rule\n")
        .append("public final org.junit.rules.Timeout ")
        .append(fieldName)
        .append(" = org.junit.rules.Timeout.seconds(")
        .append(timeoutSeconds)
        .append(");")
        .toString()
        .toCharArray();

    final ASTParser parser = JDTASTConstruction.createNewParser();
    parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
    parser.setSource(insertSourceCode);
    final TypeDeclaration typeDeclaration = (TypeDeclaration) parser.createAST(null);
    return typeDeclaration.getFields()[0];
  }
}
