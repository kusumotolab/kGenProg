package jp.kusumotolab.kgenprog.project.jdt;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.Location;
import jp.kusumotolab.kgenprog.project.SourceFile;

public class GeneratedJDTAST implements GeneratedAST {
  private final JDTASTConstruction construction;
  private final CompilationUnit root;
  private final SourceFile sourceFile;
  private final List<List<Statement>> lineNumberToStatements;
  private final List<Location> allLocations;
  private final String primaryClassName;
  private final String sourceCode;

  public GeneratedJDTAST(final JDTASTConstruction construction, final SourceFile sourceFile,
      final CompilationUnit root, final String source) {
    this.construction = construction;
    this.root = root;
    this.sourceFile = sourceFile;
    this.sourceCode = source;

    final StatementListVisitor visitor = new StatementListVisitor();
    visitor.analyzeStatement(root);
    this.lineNumberToStatements = visitor.getLineToStatements();
    this.allLocations = visitor.getStatements().stream().map(v -> new JDTLocation(sourceFile, v))
        .collect(Collectors.toList());
    this.primaryClassName = searchPrimaryClassName(root);
  }

  @Override
  public String getSourceCode() {
    return sourceCode;
  }

  @Override
  public SourceFile getSourceFile() {
    return sourceFile;
  }

  @Override
  public String getPrimaryClassName() {
    return primaryClassName;
  }

  @Override
  public List<Location> getAllLocations() {
    return allLocations;
  }

  public CompilationUnit getRoot() {
    return root;
  }

  public JDTASTConstruction getConstruction() {
    return construction;
  }

  @Override
  public List<Location> inferLocations(final int lineNumber) {
    if (0 <= lineNumber && lineNumber < lineNumberToStatements.size()) {
      return lineNumberToStatements.get(lineNumber).stream()
          .map(statement -> new JDTLocation(this.sourceFile, statement))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  private String searchPrimaryClassName(final CompilationUnit root) {
    @SuppressWarnings("unchecked")
    final List<AbstractTypeDeclaration> types = root.types();
    final Optional<AbstractTypeDeclaration> findAny = types.stream()
        .filter(type -> (type.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC).findAny();

    String typeName;
    if (findAny.isPresent()) {
      typeName = findAny.get().getName().getIdentifier();

    } else if (types.size() > 0) {
      typeName = types.get(0).getName().getIdentifier();

    } else {
      typeName = sourceFile.path.getFileName().toString();
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
      return packageName.getName().getFullyQualifiedName() + "." + name;
    }
  }
}
