package jp.kusumotolab.kgenprog.testutil;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.stream.Stream;
import org.eclipse.jdt.core.dom.ASTNode;
import com.google.common.jimfs.Jimfs;
import com.google.gson.Gson;
import io.gsonfire.GsonFireBuilder;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.Strategies;
import jp.kusumotolab.kgenprog.fl.Ochiai;
import jp.kusumotolab.kgenprog.ga.codegeneration.DefaultSourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.selection.DefaultVariantSelection;
import jp.kusumotolab.kgenprog.ga.validation.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.CrossoverHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.output.ASTNodeSerializer;
import jp.kusumotolab.kgenprog.output.BaseSerializer;
import jp.kusumotolab.kgenprog.output.CrossoverHistoricalElementSerializer;
import jp.kusumotolab.kgenprog.output.FileDiff;
import jp.kusumotolab.kgenprog.output.FileDiffSerializer;
import jp.kusumotolab.kgenprog.output.FitnessSerializer;
import jp.kusumotolab.kgenprog.output.FullyQualifiedNameSerializer;
import jp.kusumotolab.kgenprog.output.GeneSerializer;
import jp.kusumotolab.kgenprog.output.GeneratedJDTASTSerializer;
import jp.kusumotolab.kgenprog.output.GeneratedSourceCodeSerializer;
import jp.kusumotolab.kgenprog.output.HistoricalElementSerializer;
import jp.kusumotolab.kgenprog.output.MutationHistoricalElementSerializer;
import jp.kusumotolab.kgenprog.output.Patch;
import jp.kusumotolab.kgenprog.output.PatchSerializer;
import jp.kusumotolab.kgenprog.output.PathSerializer;
import jp.kusumotolab.kgenprog.output.SourcePathSerializer;
import jp.kusumotolab.kgenprog.output.TestResultSerializer;
import jp.kusumotolab.kgenprog.output.TestResultsSerializer;
import jp.kusumotolab.kgenprog.output.VariantSerializer;
import jp.kusumotolab.kgenprog.output.VariantStoreSerializer;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.test.LocalTestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestResult;
import jp.kusumotolab.kgenprog.project.test.TestResults;

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

  public static Gson createGson() {
    return new GsonFireBuilder().enableExposeMethodResult()
        .createGsonBuilder()
        .registerTypeHierarchyAdapter(ASTNode.class, new ASTNodeSerializer())
        .registerTypeHierarchyAdapter(Base.class, new BaseSerializer())
        .registerTypeHierarchyAdapter(FileDiff.class, new FileDiffSerializer())
        .registerTypeHierarchyAdapter(Fitness.class, new FitnessSerializer())
        .registerTypeHierarchyAdapter(FullyQualifiedName.class, new FullyQualifiedNameSerializer())
        .registerTypeHierarchyAdapter(Gene.class, new GeneSerializer())
        .registerTypeHierarchyAdapter(GeneratedJDTAST.class, new GeneratedJDTASTSerializer())
        .registerTypeHierarchyAdapter(GeneratedSourceCode.class,
            new GeneratedSourceCodeSerializer())
        .registerTypeHierarchyAdapter(Patch.class, new PatchSerializer())
        .registerTypeHierarchyAdapter(Path.class, new PathSerializer())
        .registerTypeHierarchyAdapter(SourcePath.class, new SourcePathSerializer())
        .registerTypeHierarchyAdapter(TestResult.class, new TestResultSerializer())
        .registerTypeHierarchyAdapter(TestResults.class, new TestResultsSerializer())
        .registerTypeHierarchyAdapter(Variant.class, new VariantSerializer())
        .registerTypeHierarchyAdapter(VariantStore.class, new VariantStoreSerializer())
        .registerTypeHierarchyAdapter(HistoricalElement.class, new HistoricalElementSerializer())
        .registerTypeHierarchyAdapter(CrossoverHistoricalElement.class,
            new CrossoverHistoricalElementSerializer())
        .registerTypeHierarchyAdapter(MutationHistoricalElement.class,
            new MutationHistoricalElementSerializer())
        .create();
  }

  private static Strategies createDefaultStrategies(final Configuration config) {
    return new Strategies(new Ochiai(), new JDTASTConstruction(), new DefaultSourceCodeGeneration(),
        new DefaultCodeValidation(), new LocalTestExecutor(config),
        new DefaultVariantSelection(0, new Random(0)));
  }

}
