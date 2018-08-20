package jp.kusumotolab.kgenprog.project.test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import jp.kusumotolab.kgenprog.project.ClassPath;

public final class TestExecutorMain {

  private final static long timeoutSeconds = 60;

  @Option(name = "-b", aliases = "--bindir", required = true, usage = "Specify a bin directory")
  private String binDir;

  @Option(name = "-s", aliases = "--src", required = true, usage = "Specify source classes")
  private String sourceClass;

  @Option(name = "-t", aliases = "--test", required = true, usage = "Specify executed test classes")
  private String testClass;

  public static final String SEPARATOR = File.pathSeparator;

  /**
   * Application entry point <br>
   * usage: $ java Main -s jp.kusu.TargetClass jp.kusu.TargetClassTest
   * 
   * @param args
   * @throws Exception
   */
  public static void main(final String[] args) throws Exception {
    final TestExecutorMain main = new TestExecutorMain();
    final CmdLineParser parser = new CmdLineParser(main);
    parser.parseArgument(args);

    final TestExecutor executor = new TestExecutor(timeoutSeconds);
    final List<ClassPath> cps = Arrays.asList(new ClassPath(Paths.get(main.binDir)));
    final List<FullyQualifiedName> targets = createTargetFQNs(main.sourceClass);
    final List<FullyQualifiedName> tests = createTestFQNs(main.testClass);
    final TestResults testResults = executor.exec(cps, targets, tests);

    TestResults.serialize(testResults);

  }

  private static List<FullyQualifiedName> createTargetFQNs(final String names) {
    return Stream.of(names.split(SEPARATOR))
        .map(TargetFullyQualifiedName::new)
        .collect(Collectors.toList());
  }

  private static List<FullyQualifiedName> createTestFQNs(final String names) {
    return Stream.of(names.split(SEPARATOR))
        .map(TestFullyQualifiedName::new)
        .collect(Collectors.toList());
  }
}
