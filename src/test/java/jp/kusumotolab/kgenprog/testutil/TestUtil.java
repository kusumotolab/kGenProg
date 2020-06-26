package jp.kusumotolab.kgenprog.testutil;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.stream.Stream;
import com.google.common.jimfs.Jimfs;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.Strategies;
import jp.kusumotolab.kgenprog.fl.Ochiai;
import jp.kusumotolab.kgenprog.ga.codegeneration.DefaultSourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.selection.DefaultVariantSelection;
import jp.kusumotolab.kgenprog.ga.validation.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.test.LocalTestExecutor;

public class TestUtil {

  public static Path createVirtualDir(final String dirname) {
    final FileSystem fs = Jimfs.newFileSystem();
    return fs.getPath(dirname);
  }

  public static Path createVirtualDir() {
    return createVirtualDir("tmp");
  }

  public static void printPath(final Path path) {
    try (Stream<Path> walk = Files.walk(path)) {
      walk.forEach(System.out::println);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Variant createVariant(final Configuration config) {
    return createVariantStoreWithDefaultStrategies(config).getInitialVariant();
  }

  public static VariantStore createVariantStoreWithDefaultStrategies(final Configuration config) {
    final Strategies strategies = createDefaultStrategies(config);
    return new VariantStore(config, strategies);
  }

  public static GeneratedSourceCode createGeneratedSourceCode(final TargetProject project) {
    return new JDTASTConstruction().constructAST(project);
  }

  private static Strategies createDefaultStrategies(final Configuration config) {
    return new Strategies(new Ochiai(), new JDTASTConstruction(), new DefaultSourceCodeGeneration(),
        new DefaultCodeValidation(), new LocalTestExecutor(config),
        new DefaultVariantSelection(0, new Random(0)));
  }

}
