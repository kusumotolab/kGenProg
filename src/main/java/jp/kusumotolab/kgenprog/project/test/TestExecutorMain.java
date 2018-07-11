package jp.kusumotolab.kgenprog.project.test;

import static java.util.stream.Collectors.toList;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public final class TestExecutorMain {

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

    final URL binUrl = Paths.get(main.binDir)
        .toUri()
        .toURL();
    final TestExecutor executor = new TestExecutor(new URL[] {binUrl});

    final TestResults testResults =
        executor.exec(createTargetFQNs(main.sourceClass), createTestFQNs(main.testClass));
    TestResults.serialize(testResults);

  }

  private static List<FullyQualifiedName> createTargetFQNs(final String names) {
    return Stream.of(names.split(SEPARATOR))
        .map(TargetFullyQualifiedName::new)
        .collect(toList());
  }

  private static List<FullyQualifiedName> createTestFQNs(final String names) {
    return Stream.of(names.split(SEPARATOR))
        .map(TestFullyQualifiedName::new)
        .collect(toList());
  }
}
