package jp.kusumotolab.kgenprog.testutil;

/**
 * JSONのキーのエイリアスを保持するクラス
 */
public class JsonKeyAlias {

  public static class TestResult {

    public final static String FQN = "fqn";
    public final static String IS_SUCCESS = "isSuccess";
  }

  public static class TestResults {

    public final static String SUCCESS_RATE = "successRate";
    public final static String EXECUTED_TESTS_COUNT = "executedTestsCount";
    public final static String TEST_RESULTS = "testResults";
  }

  public static class Variant {

    public final static String ID = "id";
    public final static String GENERATION_NUMBER = "generationNumber";
    public final static String SELECTION_COUNT = "selectionCount";
    public final static String FITNESS = "fitness";
    public final static String IS_BUILD_SUCCESS = "isBuildSuccess";
    public final static String IS_SYNTAX_VALID = "isSyntaxValid";
    public final static String PATCH = "patch";
    public final static String OPERATION = "operation";
    public final static String TEST_SUMMARY = "testSummary";
    public final static String BASES = "bases";
  }

  public static class Base {

    public final static String FILE_NAME = "fileName";
    public final static String NAME = "name";
    public final static String LINE_NUMBER_RANGE = "lineNumberRange";
    public final static String SNIPPET = "snippet";
  }

  public static class LineNumberRange {

    public final static String START = "start";
    public final static String END = "end";
  }

  public static class HistoricalElement {

    public final static String PARENT_IDS = "parentIds";
    public final static String NAME = "name";
  }

  public static class MutationHistoricalElement extends HistoricalElement {

    public final static String APPEND_BASE = "appendBase";
  }

  public static class CrossoverHistoricalElement extends HistoricalElement {

    public final static String CROSSOVER_POINT = "crossoverPoint";
  }

  public static class Patch {

    public final static String FILE_NAME = "fileName";
    public final static String DIFF = "diff";
  }

  public static class VariantStore {

    public final static String PROJECT_NAME = "projectName";
    public final static String VARIANTS = "variants";
    public final static String CONFIGURATION = "configuration";
  }
}
