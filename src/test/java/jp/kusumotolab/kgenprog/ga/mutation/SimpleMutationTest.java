package jp.kusumotolab.kgenprog.ga.mutation;

import static jp.kusumotolab.kgenprog.project.jdt.ASTNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;
import jp.kusumotolab.kgenprog.OrdinalNumber;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.ga.mutation.selection.RouletteStatementAndConditionSelection;
import jp.kusumotolab.kgenprog.ga.validation.SimpleFitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.factory.TargetProject;
import jp.kusumotolab.kgenprog.project.factory.TargetProjectFactory;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.testutil.TestUtil;

public class SimpleMutationTest {

  @SuppressWarnings("serial")
  private class MockRandom extends Random {

    MockRandom(final long seed) {
      super();
      setSeed(seed);
    }

    @Override
    public int nextInt() {
      return 0;
    }

    @Override
    public int nextInt(final int divisor) {
      return 1;
    }

    @Override
    public double nextDouble() {
      return 1.0d;
    }

    @Override
    public boolean nextBoolean() {
      return true;
    }
  }

  @Test
  public void testGeneratedVariantsSize() {
    final GeneratedSourceCode generatedSourceCode = createGeneratedSourceCode();

    final Variant initialVariant = createInitialVariant(generatedSourceCode);
    final VariantStore variantStore = createVariantStore(initialVariant);

    final SimpleMutation simpleMutation = createSimpleMutation(generatedSourceCode);
    final List<Variant> variantList = simpleMutation.exec(variantStore);

    assertThat(variantList).hasSize(15);
  }

  @Test
  public void testStopFirst01() {
    final GeneratedSourceCode generatedSourceCode = createGeneratedSourceCode();

    final Variant initialVariant = createInitialVariant(generatedSourceCode);
    final VariantStore variantStore = createVariantStore(initialVariant);

    // 修正プログラムがすでに1つ存在している状態にする
    when(variantStore.getFoundSolutionsNumber()).thenReturn(new OrdinalNumber(1));

    final SimpleMutation simpleMutation = createSimpleMutation(generatedSourceCode);
    final List<Variant> variantList = simpleMutation.exec(variantStore);

    // 変異プログラムを全く生成しないはず
    assertThat(variantList).hasSize(0);
  }

  @Test
  public void testStopFirst02() {
    final GeneratedSourceCode generatedSourceCode = createGeneratedSourceCode();

    final Variant initialVariant = createInitialVariant(generatedSourceCode);
    final VariantStore variantStore = createVariantStore(initialVariant);

    // 修正プログラムが必ず生成されるようにモックを設定する
    when(variantStore.createVariant(any(), any())).then(ans -> {
      return new Variant(0, 0, ans.getArgument(0), null, null, new SimpleFitness(1.0), null,
          ans.getArgument(1));
    });

    final SimpleMutation simpleMutation = createSimpleMutation(generatedSourceCode);
    final List<Variant> variantList = simpleMutation.exec(variantStore);

    // 最初の変異プログラムが修正プログラムなので，それ以降は変異プログラムを生成しないはず
    assertThat(variantList).hasSize(1);
  }

  @Test
  public void testBias() {
    final GeneratedSourceCode generatedSourceCode = createGeneratedSourceCode();

    final Variant initialVariant = createInitialVariant(generatedSourceCode);
    final List<Suspiciousness> suspiciousnesses = initialVariant.getSuspiciousnesses();

    final VariantStore variantStore = createVariantStore(initialVariant);
    final SimpleMutation simpleMutation = createSimpleMutation(generatedSourceCode, new Random(0));
    final List<Variant> variantList = simpleMutation.exec(variantStore);

    final Map<String, List<Base>> map = variantList.stream()
        .map(this::getLastBase)
        .collect(
            Collectors.groupingBy(e -> ((JDTASTLocation) e.getTargetLocation()).node.toString()));

    final String weakSuspiciousness = ((JDTASTLocation) suspiciousnesses.get(0)
        .getLocation()).node.toString();
    final String strongSuspiciousness = ((JDTASTLocation) suspiciousnesses.get(1)
        .getLocation()).node.toString();

    final List<Base> weakBases = map.get(weakSuspiciousness);
    final List<Base> strongBases = map.get(strongSuspiciousness);

    assertThat(weakBases.size())
        .isLessThan(strongBases.size());
  }

  @Test
  public void testGeneratedOperation() throws NoSuchFieldException, IllegalAccessException {
    final GeneratedSourceCode generatedSourceCode = createGeneratedSourceCode();

    final Variant initialVariant = createInitialVariant(generatedSourceCode);
    final VariantStore variantStore = createVariantStore(initialVariant);

    final SimpleMutation simpleMutation = createSimpleMutation(generatedSourceCode);
    final List<Variant> variantList = simpleMutation.exec(variantStore);

    final Variant variant = variantList.get(0);
    final Gene gene = variant.getGene();
    final List<Base> bases = gene.getBases();
    final Base base = bases.get(0);

    final JDTASTLocation targetLocation = (JDTASTLocation) base.getTargetLocation();
    assertThat(targetLocation.node).isSameSourceCodeAs("return n;");

    final Operation operation = base.getOperation();
    assertThat(operation).isInstanceOf(InsertAfterOperation.class);

    final InsertAfterOperation insertOperation = (InsertAfterOperation) operation;
    final Field field = insertOperation.getClass()
        .getDeclaredField("astNode");
    field.setAccessible(true);
    final ASTNode node = (ASTNode) field.get(insertOperation);
    assertThat(node).isSameSourceCodeAs("return n;");

  }

  @Test
  public void testHistoricalElement() {
    final GeneratedSourceCode generatedSourceCode = createGeneratedSourceCode();

    final Variant initialVariant = createInitialVariant(generatedSourceCode);
    final VariantStore variantStore = createVariantStore(initialVariant);

    final SimpleMutation simpleMutation = createSimpleMutation(generatedSourceCode);
    final List<Variant> variantList = simpleMutation.exec(variantStore);

    final Variant variant = variantList.get(0);
    final Base base = getLastBase(variant);

    final HistoricalElement element = variant.getHistoricalElement();
    final List<Variant> parents = element.getParents();
    assertThat(element).isInstanceOf(MutationHistoricalElement.class);

    final MutationHistoricalElement mElement = (MutationHistoricalElement) element;
    final Base appendedBase = mElement.getAppendedBase();
    assertThat(parents).hasSize(1)
        .containsExactly(initialVariant);

    assertThat(appendedBase).isEqualTo(base);
  }

  private GeneratedSourceCode createGeneratedSourceCode() {
    final Path basePath = Paths.get("example/BuildSuccess01");
    final TargetProject targetProject = TargetProjectFactory.create(basePath);
    return TestUtil.createGeneratedSourceCode(targetProject);
  }

  private SimpleMutation createSimpleMutation(final GeneratedSourceCode sourceCode) {
    final Random random = new MockRandom(0);
    return createSimpleMutation(sourceCode, random);
  }

  private SimpleMutation createSimpleMutation(final GeneratedSourceCode sourceCode,
      final Random random) {
    final CandidateSelection statementSelection = new RouletteStatementAndConditionSelection(
        random);
    final SimpleMutation simpleMutation = new SimpleMutation(15, random, statementSelection, 1,
        Type.PROJECT);
    simpleMutation.setCandidates(sourceCode.getProductAsts());
    return simpleMutation;
  }

  private GeneratedJDTAST<ProductSourcePath> createGeneratedAST(
      final GeneratedSourceCode sourceCode) {
    final List<GeneratedAST<ProductSourcePath>> asts = sourceCode.getProductAsts();
    return (GeneratedJDTAST<ProductSourcePath>) asts.get(0);
  }

  @SuppressWarnings("unchecked")
  private List<Statement> createStatement(final GeneratedAST<ProductSourcePath> generatedAST) {
    final CompilationUnit root = ((GeneratedJDTAST<ProductSourcePath>) generatedAST).getRoot();
    final List<TypeDeclaration> types = root.types();
    final TypeDeclaration typeRoot = types.get(0);

    return (List<Statement>) typeRoot.getMethods()[0].getBody()
        .statements();
  }

  private Variant createInitialVariant(final GeneratedSourceCode sourceCode) {
    final GeneratedJDTAST<ProductSourcePath> generatedAST = createGeneratedAST(sourceCode);

    final List<Statement> statements = createStatement(generatedAST);
    final SourcePath sourcePath = generatedAST.getSourcePath();

    final List<Suspiciousness> suspiciousnesses = new ArrayList<>();
    double susValue = 0.0;
    for (final Statement statement : statements) {
      susValue += 1.0 / statements.size();
      final JDTASTLocation location = new JDTASTLocation(sourcePath, statement, generatedAST);
      final Suspiciousness suspiciousness = new Suspiciousness(location, susValue);
      suspiciousnesses.add(suspiciousness);
    }

    final Gene initialGene = new Gene(Collections.emptyList());
    return new Variant(0, 0, initialGene, sourceCode, null, new SimpleFitness(0.0),
        suspiciousnesses, null);
  }

  private VariantStore createVariantStore(final Variant initialVariant) {
    final VariantStore variantStore = mock(VariantStore.class);
    when(variantStore.getCurrentVariants()).thenReturn(Collections.singletonList(initialVariant));
    when(variantStore.createVariant(any(), any())).then(ans -> {
      return new Variant(0, 0, ans.getArgument(0), null, null, new SimpleFitness(0.5), null,
          ans.getArgument(1));
    });
    when(variantStore.getFoundSolutionsNumber()).thenReturn(new OrdinalNumber(0));
    return variantStore;
  }

  private Base getLastBase(final Variant variant) {
    final List<Base> bases = variant.getGene()
        .getBases();

    if (bases.isEmpty()) {
      return null;
    }
    return bases.get(bases.size() - 1);
  }

  @Test
  public void testReusedNode01() {
    final GeneratedSourceCode generatedSourceCode = createGeneratedSourceCode();
    final SimpleMutation simpleMutation = createSimpleMutation(generatedSourceCode);

    // Statementを表すlocationを生成
    final JDTASTLocation location = mock(JDTASTLocation.class);
    when(location.isStatement()).thenReturn(true);
    when(location.isExpression()).thenReturn(false);

    // generatedASTのハリボテを生成
    final GeneratedJDTAST generatedAST = mock(GeneratedJDTAST.class);
    when(generatedAST.getPrimaryClassName()).thenReturn(new TargetFullyQualifiedName("test"));
    when(location.getGeneratedAST()).thenReturn(generatedAST);

    // locationと置換可能なプログラム要素を取得
    // 取得したプログラム要素はStatementのはず．
    final ASTNode chosenNode = simpleMutation.chooseNodeForReuse(location);
    assertThat(chosenNode).isInstanceOf(Statement.class);
  }

  @Test
  public void testReusedNode02() {
    final GeneratedSourceCode generatedSourceCode = createGeneratedSourceCode();
    final SimpleMutation simpleMutation = createSimpleMutation(generatedSourceCode);

    // Expressionを表すlocationを生成
    final JDTASTLocation location = mock(JDTASTLocation.class);
    when(location.isStatement()).thenReturn(false);
    when(location.isExpression()).thenReturn(true);

    // generatedASTのハリボテを生成
    final GeneratedJDTAST generatedAST = mock(GeneratedJDTAST.class);
    when(generatedAST.getPrimaryClassName()).thenReturn(new TargetFullyQualifiedName("example"));
    when(location.getGeneratedAST()).thenReturn(generatedAST);

    // locationと置換可能なプログラム要素を取得
    // 取得したプログラム要素はNullのはず．
    final ASTNode chosenNode = simpleMutation.chooseNodeForReuse(location);
    assertThat(chosenNode).isNull();
  }
}
