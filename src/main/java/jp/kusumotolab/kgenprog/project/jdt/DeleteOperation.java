package jp.kusumotolab.kgenprog.project.jdt;

import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Location;

public class DeleteOperation implements JDTOperation {

  @Override
  public GeneratedSourceCode apply(GeneratedSourceCode generatedSourceCode, Location location) {
    JDTLocation jdtLocation = (JDTLocation) location;

    List<GeneratedAST> newASTs = generatedSourceCode.getFiles().stream().map(ast -> {
      if (ast.getSourceFile().equals(location.getSourceFile())) {
        CompilationUnit unit = ((GeneratedJDTAST) ast).getRoot();
        CompilationUnit newAST = (CompilationUnit) ASTNode.copySubtree(unit.getAST(), unit);
        ASTNode target = jdtLocation.locate(newAST);
        target.delete();
        return new GeneratedJDTAST(ast.getSourceFile(), newAST);
      } else {
        return ast;
      }
    }).collect(Collectors.toList());

    return new GeneratedSourceCode(newASTs);
  }

  @Override
  public GeneratedSourceCode applyDirectly(GeneratedSourceCode generatedSourceCode,
      Location location) {
    JDTLocation jdtLocation = (JDTLocation) location;

    generatedSourceCode.getFiles().stream()
        .filter(ast -> ast.getSourceFile().equals(location.getSourceFile())).forEach(ast -> {
          if (ast.getSourceFile().equals(location.getSourceFile())) {
            CompilationUnit unit = ((GeneratedJDTAST) ast).getRoot();
            ASTNode target = jdtLocation.locate(unit);
            target.delete();
          }
        });

    return generatedSourceCode;
  }
}
