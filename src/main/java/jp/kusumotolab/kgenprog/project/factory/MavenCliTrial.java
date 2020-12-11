package jp.kusumotolab.kgenprog.project.factory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.apache.maven.cli.MavenCli;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

public class MavenCliTrial {

  public static void main(String[] args) {
    runMaven();
    runGradle();
  }

  public static void runMaven() {
    String pom = "example/BuildToolMaven/pom.xml";
    System.setProperty("maven.multiModuleProjectDirectory", "dummy");
    MavenCli.doMain(new String[] {"-f", pom, "dependency:build-classpath"}, null);
    MavenCli.doMain(new String[] {"-f", pom, "help:evaluate",
        "-Dexpression=project.build.sourceDirectory"}, null);
    MavenCli.doMain(new String[] {"-f", pom,
        "help:evaluate", "-Dexpression=project.build.testSourceDirectory"}, null);
  }

  public static void runGradle() {
    final Path initGradle = createInitGradle();
    final Path root = Paths.get("example/BuildToolGradle/");
    ProjectConnection connection = GradleConnector.newConnector()
        .forProjectDirectory(root.toFile())
        .connect();

    final BuildLauncher build = connection.newBuild();
    final OutputStream outputStream = new ByteArrayOutputStream();
    build.addArguments("--init-script", initGradle.toString());
    build.forTasks("printDependencies");
    build.setStandardOutput(System.out);

    build.run();
    connection.close();
  }

  public static Path createInitGradle() {

    String contents = String.join("\n"
        , "allprojects {"
        , "    task printDependencies {"
        , "        doLast {"
        , "            def printEach = {arr -> arr.each { println it }}"
        , "            printEach(project.configurations.compileClasspath)"
        , "            printEach(project.configurations.runtimeClasspath)"
        , "            printEach(project.configurations.testCompileClasspath)"
        , "            printEach(project.configurations.testRuntimeClasspath)"
        , "        }"
        , "    }"
        , "}"
    );
    try {
      Path initGradle = Files.createTempFile("kgp", "-init.gradle");
      Files.write(initGradle, Collections.singleton(contents));
      return initGradle.toAbsolutePath();
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
