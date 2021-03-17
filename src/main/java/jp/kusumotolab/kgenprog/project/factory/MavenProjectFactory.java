package jp.kusumotolab.kgenprog.project.factory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.maven.cli.MavenCli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

public class MavenProjectFactory extends BuildToolProjectFactory {

  private static final Logger log = LoggerFactory.getLogger(MavenProjectFactory.class);
  private static final String CONFIG_FILE_NAME = "pom.xml";

  public static void main(String[] args) {
    MavenProjectFactory m = new MavenProjectFactory(Paths.get("example/BuildToolMaven"));
    m.create();
  }

  public MavenProjectFactory(final Path rootPath) {
    super(rootPath);
  }

  @Override
  protected String getConfigFileName() {
    return CONFIG_FILE_NAME;
  }

  @Override
  public TargetProject create() {
    final String dependencies = runMaven(
        List.of("dependency:build-classpath"),
        "Dependencies classpath:\\R(?<res>.+)\\R");
    final String srcDirs = runMaven(
        List.of("help:evaluate", "-Dexpression=project.build.sourceDirectory"),
        "No artifact parameter specified.*?\\R.*?\\R(?<res>.+)\\R");
    final String testDir = runMaven(
        List.of("help:evaluate", "-Dexpression=project.build.testSourceDirectory"),
        "No artifact parameter specified.*?\\R.*?\\R(?<res>.+)\\R");

    return createTargetProject(rootPath, srcDirs, testDir, dependencies);
  }

  private TargetProject createTargetProject(final Path rootPath,
      final String srcDir, final String testDir, final String dependencies) {

    // TODO should be handled..
    if (srcDir == null || srcDir.isEmpty() ||
        testDir == null || testDir.isEmpty() ||
        dependencies == null || dependencies.isEmpty()) {
      log.error("Maven parse failed.");
      return null;
    }

    return new TargetProject(rootPath,
        listSrcFiles(srcDir),
        listTestFiles(testDir),
        Stream.of(dependencies.split(";"))
            .map(Paths::get)
            .map(ClassPath::new)
            .collect(Collectors.toList()));
  }

  private List<ProductSourcePath> listSrcFiles(final String dir) {
    try (final Stream<Path> stream = Files.walk(Paths.get(dir))) {
      return stream.filter(p -> !Files.isDirectory(p))
          .map(p -> new ProductSourcePath(rootPath, rootPath.toAbsolutePath().relativize(p)))
          .collect(Collectors.toList());
    } catch (final IOException e) {
      log.error("Maven parse failed."); // TODO
    }
    return Collections.emptyList();
  }

  private List<TestSourcePath> listTestFiles(final String dir) {
    try (final Stream<Path> stream = Files.walk(Paths.get(dir))) {
      return stream.filter(p -> !Files.isDirectory(p))
          .map(p -> new TestSourcePath(rootPath, rootPath.toAbsolutePath().relativize(p)))
          .collect(Collectors.toList());
    } catch (final IOException e) {
      log.error("Maven parse failed."); // TODO
    }
    return Collections.emptyList();
  }

  private String runMaven(final List<String> goalParams, final String regex) {
    if (getConfigPath().isEmpty()) {
      log.error("No {} found.", CONFIG_FILE_NAME);
      return null; // TODO handle if no config here
    }

    // set a dummy value to the environment var to suppress maven error
    System.setProperty("maven.multiModuleProjectDirectory", "dummy");

    // set maven output to be written to both stdout and baos using TeeOutputStream
    // sysout is for show kgp users and baos is for parsing it
    final OutputStream outBuffer = new ByteArrayOutputStream();
    final PrintStream outDefault = System.out; //NOSONAR
    final OutputStream outTree = new TeeOutputStream(outDefault, outBuffer);
    System.setOut(new PrintStream(outTree));

    final Path pom = getConfigPath().iterator()
        .next();
    final String[] params = ArrayUtils.addAll(
        new String[] {"-f", pom.toString()},
        goalParams.toArray(String[]::new));

    // exec
    MavenCli.doMain(params, null);
    System.setOut(outDefault);

    // return the parsed stdout value based on the given regex
    return parseMavenStdout(outBuffer.toString(), regex);
  }

  private static String parseMavenStdout(final String stdout, final String regex) {
    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(stdout);
    if (!matcher.find()) {
      log.error("Maven parse error.");
      return "maven parse error";
    }
    return matcher.group("res");
  }


}
