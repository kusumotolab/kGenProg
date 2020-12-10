package jp.kusumotolab.kgenprog.project.factory;

import org.apache.maven.cli.MavenCli;

public class MavenCliTrial {

  public static void main(String[] args) throws Exception {
    String pom = "example/BuildSuccess05/pom.xml";
    System.setProperty("maven.multiModuleProjectDirectory", "dummy");
    MavenCli.doMain(new String[] {"-f", pom, "dependency:build-classpath"}, null);
    MavenCli.doMain(new String[] {"-f", pom, "help:evaluate", "-Dexpression=project.build"}, null);
  }

}
