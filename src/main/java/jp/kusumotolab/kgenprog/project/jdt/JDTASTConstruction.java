package jp.kusumotolab.kgenprog.project.jdt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetProject;

public class JDTASTConstruction {
  public List<GeneratedAST> constructAST(TargetProject project) {
    return constructAST(project.getSourceFiles());
  }

  public List<GeneratedAST> constructAST(List<SourceFile> sourceFiles) {
    String[] filePaths =
        sourceFiles.stream().map(file -> file.path.toString()).toArray(String[]::new);

    ASTParser parser = createNewParser();

    Map<Path, SourceFile> pathToSourceFile =
        sourceFiles.stream().collect(Collectors.toMap(file -> file.path, file -> file));

    List<GeneratedAST> asts = new ArrayList<>();

    FileASTRequestor requestor = new FileASTRequestor() {
      @Override
      public void acceptAST(String sourceFilePath, CompilationUnit ast) {
        SourceFile file = pathToSourceFile.get(Paths.get(sourceFilePath));
        if (file != null) {
          asts.add(new GeneratedJDTAST(JDTASTConstruction.this, file, ast, loadAsString(sourceFilePath)));
        }
      }
    };

    parser.createASTs(filePaths, null, new String[] {}, requestor, null);

    return asts;
  }
  
  public GeneratedJDTAST constructAST(SourceFile file, String data) {
    ASTParser parser = createNewParser();
    parser.setSource(data.toCharArray());

    return new GeneratedJDTAST(this, file, (CompilationUnit) parser.createAST(null), data);
  }
  
  private ASTParser createNewParser() {
    ASTParser parser = ASTParser.newParser(AST.JLS10);

    @SuppressWarnings("unchecked")
    final Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();
    options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
    options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
    options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
    parser.setCompilerOptions(options);

    // TODO: Bindingが必要か検討
    parser.setResolveBindings(false);
    parser.setBindingsRecovery(false);
    parser.setEnvironment(null, null, null, true);

    return parser;
  }
  
  private String loadAsString(String filePath) {
    try {
      return new String(Files.readAllBytes(Paths.get(filePath)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
