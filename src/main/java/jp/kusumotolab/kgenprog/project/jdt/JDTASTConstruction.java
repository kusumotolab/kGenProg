package jp.kusumotolab.kgenprog.project.jdt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public class JDTASTConstruction {

  private static final Logger log = LoggerFactory.getLogger(JDTASTConstruction.class);

  public GeneratedSourceCode constructAST(final TargetProject project) {
    return constructAST(project.getProductSourcePaths());
  }

  public GeneratedSourceCode constructAST(final List<ProductSourcePath> productSourcePaths) {
    final String[] paths = productSourcePaths.stream()
        .map(path -> path.path.toString())
        .toArray(String[]::new);

    final ASTParser parser = createNewParser();

    final Map<Path, ProductSourcePath> pathToSourcePath = productSourcePaths.stream()
        .collect(Collectors.toMap(path -> path.path, path -> path));

    final List<GeneratedAST> asts = new ArrayList<>();
    final List<IProblem> problems = new ArrayList<>();

    final FileASTRequestor requestor = new FileASTRequestor() {

      @Override
      public void acceptAST(final String sourcePath, final CompilationUnit ast) {
        final ProductSourcePath path = pathToSourcePath.get(Paths.get(sourcePath));
        if (path != null) {
          asts.add(
              new GeneratedJDTAST(JDTASTConstruction.this, path, ast, loadAsString(sourcePath)));
        }
        problems.addAll(Arrays.asList(ast.getProblems()));
      }
    };

    parser.createASTs(paths, null, new String[] {}, requestor, null);

    if (isConstructionSuccess(problems)) {
      return new GeneratedSourceCode(asts);
    } else {
      final String messages = concatProblemMessages(problems);
      log.debug("AST construction failed: " + messages);
      return new GenerationFailedSourceCode(messages);
    }
  }

  public GeneratedJDTAST constructAST(final ProductSourcePath productSourcePath,
      final String data) {
    final ASTParser parser = createNewParser();
    parser.setSource(data.toCharArray());

    return new GeneratedJDTAST(this, productSourcePath, (CompilationUnit) parser.createAST(null),
        data);
  }

  private ASTParser createNewParser() {
    final ASTParser parser = ASTParser.newParser(AST.JLS10);

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

  private String loadAsString(final String path) {
    try {
      return new String(Files.readAllBytes(Paths.get(path)));
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isConstructionSuccess(final List<IProblem> problems) {
    return problems.stream()
        .noneMatch(IProblem::isError);
  }

  private String concatProblemMessages(final List<IProblem> problems) {
    return problems.stream()
        .map(IProblem::getMessage)
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
