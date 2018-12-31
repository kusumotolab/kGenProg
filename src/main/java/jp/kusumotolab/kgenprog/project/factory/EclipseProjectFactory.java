package jp.kusumotolab.kgenprog.project.factory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

public class EclipseProjectFactory extends BuildToolProjectFactory {

  private final static Logger log = LoggerFactory.getLogger(EclipseProjectFactory.class);
  private final static String CONFIG_FILE_NAME = ".classpath";

  public EclipseProjectFactory(final Path rootPath) {
    super(rootPath);
  }

  @Override
  public TargetProject create() {
    try {
      final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
      final SAXParser saxParser = saxParserFactory.newSAXParser();
      final ClassPathHandler classPathHandler = new ClassPathHandler();
      final Path configFilePath = rootPath.resolve(Paths.get(CONFIG_FILE_NAME));
      saxParser.parse(configFilePath.toString(), classPathHandler);

      return new TargetProject(rootPath, classPathHandler.getProductSourcePaths(),
          classPathHandler.getTestSourcePaths(), classPathHandler.getClassPaths());

    } catch (final SAXException | ParserConfigurationException | IOException e) {
      log.error(e.getMessage(), e);
    }
    return new TargetProject(rootPath, Collections.emptyList(), Collections.emptyList(),
        Collections.emptyList());
  }

  @Override
  public boolean isApplicable() {
    return getConfigPath().size() > 0;
  }

  @Override
  protected String getConfigFileName() {
    return CONFIG_FILE_NAME;
  }

  private class ClassPathHandler extends DefaultHandler {

    private final List<ProductSourcePath> productSourcePaths = new ArrayList<>();
    private final List<TestSourcePath> testSourcePaths = new ArrayList<>();
    private final List<ClassPath> classPaths = new ArrayList<>();

    @Override
    public void startElement(final String uri, final String localName, final String qName,
        final Attributes attributes) {

      if (!qName.equals("classpathentry")) {
        return;
      }

      switch (attributes.getValue("kind")) {
        case "src":
          final Path sourceRootPath = rootPath.resolve(Paths.get(attributes.getValue("path")));
          for (final Path javaSourcePath : collectJavaSourcePath(sourceRootPath)) {
            if (javaSourcePath.toString()
                .endsWith("Test.java")) {
              testSourcePaths.add(TestSourcePath.relativizeAndCreate(rootPath, javaSourcePath));
            } else {
              productSourcePaths.add(ProductSourcePath.relativizeAndCreate(rootPath, javaSourcePath));
            }
          }
          break;
        case "lib":
          final Path classpath = Paths.get(attributes.getValue("path"));
          // 絶対パスか調べる
          if (classpath.isAbsolute()) {
            classPaths.add(new ClassPath(classpath));
          } else {
            classPaths.add(new ClassPath(rootPath.resolve(classpath)));
          }
          break;
      }
    }

    private List<Path> collectJavaSourcePath(final Path sourceRootPath) {
      try {
        return Files.walk(sourceRootPath)
            .filter(e -> e.toString()
                .endsWith(".java"))
            .collect(Collectors.toList());
      } catch (final IOException e) {
        log.error(e.getMessage(), e);
      }
      return Collections.emptyList();
    }

    List<ClassPath> getClassPaths() {
      return classPaths;
    }

    List<ProductSourcePath> getProductSourcePaths() {
      return productSourcePaths;
    }

    List<TestSourcePath> getTestSourcePaths() {
      return testSourcePaths;
    }
  }
}
