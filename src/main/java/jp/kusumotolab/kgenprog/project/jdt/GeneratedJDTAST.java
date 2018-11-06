package jp.kusumotolab.kgenprog.project.jdt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
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
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.test.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.test.TargetFullyQualifiedName;

public class GeneratedJDTAST<T extends SourcePath> implements GeneratedAST<T> {

  private static final String DIGEST_ALGORITHM = "MD5";

  private final JDTASTConstruction construction;
  private final CompilationUnit root;
  private final T sourcePath;
  private final List<List<Statement>> lineNumberToStatements;
  private final List<ASTLocation> allLocations;
  private final FullyQualifiedName primaryClassName;
  private final String sourceCode;
  private final String messageDigest;

  public GeneratedJDTAST(final JDTASTConstruction construction, final T sourcePath,
      final CompilationUnit root, final String source) {
    this.construction = construction;
    this.root = root;
    this.sourcePath = sourcePath;
    this.sourceCode = source;

    final StatementListVisitor visitor = new StatementListVisitor();
    visitor.analyzeStatement(root);
    this.lineNumberToStatements = visitor.getLineToStatements();
    this.allLocations = visitor.getStatements()
        .stream()
        .map(v -> new JDTASTLocation(sourcePath, v))
        .collect(Collectors.toList());
    this.primaryClassName = searchPrimaryClassName(root);
    this.messageDigest = createMessageDigest();
  }

  @Override
  public String getSourceCode() {
    return sourceCode;
  }

  @Override
  public T getSourcePath() {
    return sourcePath;
  }

  @Override
  public FullyQualifiedName getPrimaryClassName() {
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
          .map(statement -> new JDTASTLocation(this.sourcePath, statement))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  private FullyQualifiedName searchPrimaryClassName(final CompilationUnit root) {
    @SuppressWarnings("unchecked")
    final List<AbstractTypeDeclaration> types = root.types();
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
      typeName = sourcePath.path.getFileName()
          .toString();
      final int idx = typeName.indexOf(".");
      if (idx > 0) {
        typeName = typeName.substring(0, idx);
      }
    }
    return constructFQN(root.getPackage(), typeName);
  }

  private FullyQualifiedName constructFQN(final PackageDeclaration packageName, final String name) {
    final String fqnString;
    if (packageName == null) {
      fqnString = name;
    } else {
      fqnString = packageName.getName()
          .getFullyQualifiedName() + "." + name;
    }
    return new TargetFullyQualifiedName(fqnString);
  }

  private String createMessageDigest() {
    try {
      final MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);

      return Hex.encodeHexString(digest.digest(root.toString()
          .getBytes()));
    } catch (final NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
