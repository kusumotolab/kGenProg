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
  private CompilationUnit root;
  private SourceFile sourceFile;
  private List<List<Statement>> lineNumberToStatements;
  private List<Location> allLocations;
  private String primaryClassName;
  private String sourceCode;

  @Override
  public String getSourceCode() {
    return sourceCode;
  }

  @Deprecated
  public GeneratedJDTAST(SourceFile sourceFile, CompilationUnit root) {
    this(sourceFile, root, root.toString());
  }
  
  public GeneratedJDTAST(SourceFile sourceFile, CompilationUnit root, String source) {
    this.root = root;
    this.sourceFile = sourceFile;
    this.sourceCode = source;

    StatementListVisitor visitor = new StatementListVisitor();
    visitor.analyzeStatement(root);
    this.lineNumberToStatements = visitor.getLineToStatements();
    this.allLocations = visitor.getStatements().stream().map(v -> new JDTLocation(sourceFile, v)).collect(Collectors.toList());
    this.primaryClassName = searchPrimaryClassName(root);
  }
  
  public static GeneratedJDTAST generateAST(SourceFile sourceFile, String source) {
    // TODO Refactoring
    JDTASTConstruction construction = new JDTASTConstruction();
    return construction.constructAST(sourceFile, source);
  }

  public CompilationUnit getRoot() {
    return root;
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
  public List<Location> inferLocations(int lineNumber) {
    if (0 <= lineNumber && lineNumber < lineNumberToStatements.size()) {
      return lineNumberToStatements.get(lineNumber).stream()
          .map(statement -> new JDTLocation(this.sourceFile, statement))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }
  
  @Override
  public List<Location> getAllLocations() {
    return allLocations;
  }
  
  private String searchPrimaryClassName(CompilationUnit root) {
    List<AbstractTypeDeclaration> types = root.types();
    Optional<AbstractTypeDeclaration> findAny = types.stream()
        .filter(type -> (type.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC).findAny();

    String typeName;
    if (findAny.isPresent()) {
      typeName = findAny.get().getName().getIdentifier();

    } else if (types.size() > 0) {
      typeName = types.get(0).getName().getIdentifier();

    } else {
      typeName = sourceFile.path.getFileName().toString();
      int idx = typeName.indexOf(".");
      if (idx > 0) {
        typeName = typeName.substring(0, idx);
      }
    }
    return constructFQN(root.getPackage(), typeName);
  }

  private String constructFQN(PackageDeclaration packageName, String name) {
    if (packageName == null) {
      return name;
    } else {
      return packageName.getName().getFullyQualifiedName() + "." + name;
    }
  }
}
