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
import java.util.stream.Stream;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.GenerationFailedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;

public class JDTASTConstruction {

  public GeneratedSourceCode constructAST(final TargetProject project) {
    return constructAST(project.getProductSourcePaths(), project.getTestSourcePaths());
  }

  public GeneratedSourceCode constructAST(final List<ProductSourcePath> productSourcePaths,
      final List<TestSourcePath> testSourcePaths) {
    final String[] paths = Stream.concat(productSourcePaths.stream(), testSourcePaths.stream())
        .map(path -> path.path.toString())
        .toArray(String[]::new);

    final ASTParser parser = createNewParser();

    final Map<Path, ProductSourcePath> pathToProductSourcePath = productSourcePaths.stream()
        .collect(Collectors.toMap(path -> path.path, path -> path));
    final Map<Path, TestSourcePath> pathToTestSourcePath = testSourcePaths.stream()
        .collect(Collectors.toMap(path -> path.path, path -> path));

    final List<GeneratedAST<ProductSourcePath>> productAsts = new ArrayList<>();
    final List<GeneratedAST<TestSourcePath>> testAsts = new ArrayList<>();
    final List<IProblem> problems = new ArrayList<>();

    final FileASTRequestor requestor = new FileASTRequestor() {

      @Override
      public void acceptAST(final String sourcePath, final CompilationUnit ast) {
        final ProductSourcePath productPath = pathToProductSourcePath.get(Paths.get(sourcePath));
        if (productPath != null) {
          productAsts.add(new GeneratedJDTAST<>(JDTASTConstruction.this, productPath, ast,
              loadAsString(sourcePath)));
        }

        final TestSourcePath testPath = pathToTestSourcePath.get(Paths.get(sourcePath));
        if (testPath != null) {
          testAsts.add(new GeneratedJDTAST<>(JDTASTConstruction.this, testPath, ast,
              loadAsString(sourcePath)));
        }

        problems.addAll(Arrays.asList(ast.getProblems()));
      }
    };

    parser.createASTs(paths, null, new String[] {}, requestor, null);

    if (isConstructionSuccess(problems)) {
      return new GeneratedSourceCode(productAsts, testAsts);
    } else {
      final String messages = concatProblemMessages(problems);
      return new GenerationFailedSourceCode(messages);
    }
  }

  public <T extends SourcePath> GeneratedJDTAST<T> constructAST(final T sourcePath,
      final String data) {
    final ASTParser parser = createNewParser();
    parser.setSource(data.toCharArray());

    return new GeneratedJDTAST<>(this, sourcePath, (CompilationUnit) parser.createAST(null), data);
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
