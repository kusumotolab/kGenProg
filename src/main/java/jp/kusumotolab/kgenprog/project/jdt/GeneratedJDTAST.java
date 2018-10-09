package jp.kusumotolab.kgenprog.project.jdt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.codec.binary.Hex;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

public class GeneratedJDTAST implements GeneratedAST {

  private static final String DIGEST_ALGORITHM = "MD5";

  private final JDTASTConstruction construction;
  private final CompilationUnit root;
  private final ProductSourcePath productSourcePath;
  private final List<List<Statement>> lineNumberToStatements;
  private final List<ASTLocation> allLocations;
  private final String primaryClassName;
  private final String sourceCode;
  private final String messageDigest;

  public GeneratedJDTAST(final JDTASTConstruction construction,
      final ProductSourcePath productSourcePath, final CompilationUnit root, final String source) {
    this.construction = construction;
    this.root = root;
    this.productSourcePath = productSourcePath;
    this.sourceCode = source;

    final StatementListVisitor visitor = new StatementListVisitor();
    visitor.analyzeStatement(root);
    this.lineNumberToStatements = visitor.getLineToStatements();
    this.allLocations = visitor.getStatements()
        .stream()
        .map(v -> new JDTASTLocation(productSourcePath, v))
        .collect(Collectors.toList());
    this.primaryClassName = searchPrimaryClassName(root);
    this.messageDigest = createMessageDigest();
  }

  @Override
  public String getSourceCode() {
    return sourceCode;
  }

  @Override
  public ProductSourcePath getProductSourcePath() {
    return productSourcePath;
  }

  @Override
  public String getPrimaryClassName() {
    return primaryClassName;
  }

  @Override
  public List<ASTLocation> getAllLocations() {
    return allLocations;
  }

  @Override
  public String getMessageDigest() {
    return messageDigest;
  }

  public CompilationUnit getRoot() {
    return root;
  }

  public JDTASTConstruction getConstruction() {
    return construction;
  }

  @Override
  public List<ASTLocation> inferLocations(final int lineNumber) {
    if (0 <= lineNumber && lineNumber < lineNumberToStatements.size()) {
      return lineNumberToStatements.get(lineNumber)
          .stream()
          .map(statement -> new JDTASTLocation(this.productSourcePath, statement))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  private String searchPrimaryClassName(final CompilationUnit root) {
    @SuppressWarnings("unchecked") final List<AbstractTypeDeclaration> types = root.types();
    final Optional<AbstractTypeDeclaration> findAny = types.stream()
        .filter(type -> (type.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC)
        .findAny();

    String typeName;
    if (findAny.isPresent()) {
      typeName = findAny.get()
          .getName()
          .getIdentifier();

    } else if (types.size() > 0) {
      typeName = types.get(0)
          .getName()
          .getIdentifier();

    } else {
      typeName = productSourcePath.path.getFileName()
          .toString();
      final int idx = typeName.indexOf(".");
      if (idx > 0) {
        typeName = typeName.substring(0, idx);
      }
    }
    return constructFQN(root.getPackage(), typeName);
  }

  private String constructFQN(final PackageDeclaration packageName, final String name) {
    if (packageName == null) {
      return name;
    } else {
      return packageName.getName()
          .getFullyQualifiedName() + "." + name;
    }
  }

  private String createMessageDigest() {
    try {
      final MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);

      return Hex.encodeHexString(digest.digest(root.toString()
          .getBytes()));
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean equals(final Object o) {

    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final GeneratedJDTAST generatedJDTAST = (GeneratedJDTAST) o;
    return root == generatedJDTAST.root &&
        Objects.equals(productSourcePath, generatedJDTAST.productSourcePath) &&
        Objects.equals(allLocations, generatedJDTAST.allLocations) &&
        Objects.equals(primaryClassName, generatedJDTAST.primaryClassName) &&
        Objects.equals(sourceCode, generatedJDTAST.sourceCode) &&
        Objects.equals(messageDigest, generatedJDTAST.messageDigest);
  }

  @Override
  public int hashCode() {
    final int prime = 31;

    int result = 1;
    // JDT関連のクラスのhashは計算しない
    result = result * prime + productSourcePath.hashCode();
    result = result * prime + allLocations.hashCode();
    result = result * prime + primaryClassName.hashCode();
    result = result * prime + sourceCode.hashCode();
    result = result * prime + messageDigest.hashCode();

    return result;
  }
}
