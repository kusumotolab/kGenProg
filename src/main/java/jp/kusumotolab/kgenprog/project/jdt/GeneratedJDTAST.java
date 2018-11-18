package jp.kusumotolab.kgenprog.project.jdt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import org.apache.commons.codec.binary.Hex;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import jp.kusumotolab.kgenprog.project.ASTLocations;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.SourcePath;

public class GeneratedJDTAST<T extends SourcePath> implements GeneratedAST<T> {

  private static final String DIGEST_ALGORITHM = "MD5";

  private final JDTASTConstruction construction;
  private final CompilationUnit root;
  private final T sourcePath;
  private final FullyQualifiedName primaryClassName;
  private final String sourceCode;
  private final String messageDigest;
  private final int numberOfLines;

  public GeneratedJDTAST(final JDTASTConstruction construction, final T sourcePath,
      final CompilationUnit root, final String source) {
    this.construction = construction;
    this.root = root;
    this.sourcePath = sourcePath;
    this.sourceCode = source;

    this.primaryClassName = searchPrimaryClassName(root);
    this.messageDigest = createMessageDigest();
    this.numberOfLines = calculateNumberOfLines();
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
  public ASTLocations createLocations() {
    return new JDTASTLocations<>(this, root, sourcePath);
  }

  @Override
  public String getMessageDigest() {
    return messageDigest;
  }

  @Override
  public int getNumberOfLines() {
    return numberOfLines;
  }

  public CompilationUnit getRoot() {
    return root;
  }

  public JDTASTConstruction getConstruction() {
    return construction;
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
    return sourcePath.createFullyQualifiedName(fqnString);
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

  private int calculateNumberOfLines() {
    final int pos = root.getExtendedStartPosition(root) + root.getExtendedLength(root) - 1;
    return root.getLineNumber(pos);
  }
}
