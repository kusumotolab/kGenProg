package jp.kusumotolab.kgenprog.project.jdt;

import org.eclipse.jdt.core.dom.CompilationUnit;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.SourceFile;

public class GeneratedJDTAST implements GeneratedAST {
  private CompilationUnit root;
  private SourceFile sourceFile;

  @Override
  public String getSourceCode() {
    return root.toString();
  }

  public GeneratedJDTAST(SourceFile sourceFile, CompilationUnit root) {
    this.root = root;
    this.sourceFile = sourceFile;
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
    // TODO Auto-generated method stub
    return null;
  }
}
