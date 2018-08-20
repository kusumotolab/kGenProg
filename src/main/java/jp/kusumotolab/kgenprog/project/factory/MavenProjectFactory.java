package jp.kusumotolab.kgenprog.project.factory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

public class MavenProjectFactory extends BuildToolProjectFactory {

  private final static String CONFIG_FILE_NAME = "pom.xml";

  private static final Logger log = LoggerFactory.getLogger(MavenProjectFactory.class);

  private final Path rootPath;
  private final List<ProductSourcePath> sourcePaths;
  private final List<TestSourcePath> testSourcePaths;
  private final List<ClassPath> classPaths;

  public MavenProjectFactory(final Path rootPath) {
    super(rootPath);

    this.rootPath = rootPath;
    sourcePaths = new ArrayList<>();
    testSourcePaths = new ArrayList<>();
    classPaths = new ArrayList<>();
  }

  @Override
  public boolean isApplicable() {
    return getConfigPath().size() > 0;
  }

  @Override
  protected String getConfigFileName() {
    return CONFIG_FILE_NAME;
  }

  @Override
  public TargetProject create() {
    resolveSourcePath();
    resolveTestPath();
    resolveClassPath();
    return new TargetProject(rootPath, sourcePaths, testSourcePaths, classPaths);
  }

  private void resolveSourcePath() {
    final Path path = Paths.get(rootPath.toString(), "src", "main", "java");
    final List<File> files = new ArrayList<>();
    searchJavaFiles(new File(path.toString()), files);
    sourcePaths.addAll(files.stream()
        .map(e -> new ProductSourcePath(e.toPath()))
        .collect(Collectors.toList()));
  }

  private void resolveTestPath() {
    final Path path = Paths.get(rootPath.toString(), "src", "test", "java");
    final List<File> files = new ArrayList<>();
    searchJavaFiles(new File(path.toString()), files);
    testSourcePaths.addAll(files.stream()
        .map(e -> new TestSourcePath(e.toPath()))
        .collect(Collectors.toList()));
  }

  private void searchJavaFiles(File directory, List<File> results) {
    File[] files = directory.listFiles();
    if (files == null) {
      return;
    }
    for (File file : files) {
      if (file.isFile() && file.getName()
          .toLowerCase()
          .endsWith(".java")) {
        results.add(file);
      } else if (file.isDirectory()) {
        searchJavaFiles(file, results);
      }
    }
  }

  private void resolveClassPath() {
    final Path pomFilePath = Paths.get(rootPath.toString(), CONFIG_FILE_NAME);
    final File pomFile = new File(pomFilePath.toString());
    final MavenXpp3Reader reader = new MavenXpp3Reader();
    try {
      final Model model = reader.read(new FileReader(pomFile));
      final Path homePath = Paths.get(System.getProperty("user.home"));
      final Path m2Path = Paths.get(homePath.toString(), ".m2", "repository");
      for (Dependency dependency : model.getDependencies()) {
        final List<String> splits = new ArrayList<>(Arrays.asList(dependency.getGroupId()
            .split("\\.")));
        splits.add(0, m2Path.toString());
        splits.add(dependency.getArtifactId());
        splits.add(dependency.getVersion());
        final String path = String.join(File.separator, splits);
        final File file = new File(path);
        if (!file.isDirectory()) {
          continue;
        }
        for (File f : file.listFiles()) {
          if (f.getAbsolutePath()
              .endsWith(".jar")) {
            classPaths.add(new ClassPath(f.toPath()));
          }
        }
      }
    } catch (IOException | XmlPullParserException e) {
      log.debug(e.getMessage());
    }
  }
}
