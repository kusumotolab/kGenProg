package jp.kusumotolab.kgenprog.project.jdt;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.Location;
import jp.kusumotolab.kgenprog.project.SourceFile;
import jp.kusumotolab.kgenprog.project.TargetSourceFile;

public class GeneratedJDTASTTest {

  private static final String TEST_SOURCE_FILE_NAME = "A.java";
  private static final String TEST_SOURCE = 
      "class A {\n" +
      "   public void a() {\n" +
      "       int a = 0;\n" +
      "       if (a == 1) {\n" +
      "           System.out.println(a);\n" +
      "       }\n" +
      "   }\n" +
      "   public int b(int a) {\n" +
      "       if (a < 0) { return -a; }\n" +
      "       return a;\n" +
      "   }\n" +
      "}\n" +
      ""
      ;

  private GeneratedJDTAST ast;

  @Before
  public void setup() {
    SourceFile testSourceFile = new TargetSourceFile(Paths.get(TEST_SOURCE_FILE_NAME));
    JDTASTConstruction constructor = new JDTASTConstruction();
    this.ast =
        (GeneratedJDTAST) constructor.constructAST(testSourceFile, TEST_SOURCE.toCharArray());
  }

  @Test
  public void testInferASTNode01() {
    List<Location> locations = ast.inferLocations(3);

    assertThat(locations, hasSize(2));
    testLocation(locations.get(0),
        "{\n  int a=0;\n  if (a == 1) {\n    System.out.println(a);\n  }\n}\n");
    testLocation(locations.get(1), "int a=0;\n");
  }

  @Test
  public void testInferASTNode02() {
    List<Location> locations = ast.inferLocations(5);

    assertThat(locations, hasSize(4));
    testLocation(locations.get(0),
        "{\n  int a=0;\n  if (a == 1) {\n    System.out.println(a);\n  }\n}\n");
    testLocation(locations.get(1), "if (a == 1) {\n  System.out.println(a);\n}\n");
    testLocation(locations.get(2), "{\n  System.out.println(a);\n}\n");
    testLocation(locations.get(3), "System.out.println(a);\n");
  }

  @Test
  public void testInferASTNode03() {
    List<Location> locations = ast.inferLocations(1);

    assertThat(locations.size(), is(0));
  }

  @Test
  public void testInferASTNode04() {
    List<Location> locations = ast.inferLocations(9);

    testLocation(locations.get(0), "{\n  if (a < 0) {\n    return -a;\n  }\n  return a;\n}\n");
    testLocation(locations.get(1), "if (a < 0) {\n  return -a;\n}\n");
    testLocation(locations.get(2), "{\n  return -a;\n}\n");
    testLocation(locations.get(3), "return -a;\n");
  }

  private void testLocation(Location target, String expected) {
    assertThat(target, instanceOf(JDTLocation.class));
    JDTLocation jdtLocation = (JDTLocation) target;
    assertThat(jdtLocation.node.toString(), is(expected));
  }

  @Test
  public void testgetPrimaryClassName01() {
    String testSource = "package a.b.c; class T1{} public class T2{}";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("a", "b", "c", "T2.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast =
        (GeneratedJDTAST) constructor.constructAST(testSourceFile, testSource.toCharArray());

    assertThat(ast.getPrimaryClassName(), is("a.b.c.T2"));
  }

  @Test
  public void testgetPrimaryClassName02() {
    String testSource = "class T1{} public class T2{}";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("T2.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast =
        (GeneratedJDTAST) constructor.constructAST(testSourceFile, testSource.toCharArray());

    assertThat(ast.getPrimaryClassName(), is("T2"));
  }

  @Test
  public void testgetPrimaryClassName03() {
    String testSource = "package a.b.c; class T1{} class T2{} class T3{}";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("a", "b", "c", "T2.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast =
        (GeneratedJDTAST) constructor.constructAST(testSourceFile, testSource.toCharArray());

    assertThat(ast.getPrimaryClassName(), is("a.b.c.T1"));
  }

  @Test
  public void testgetPrimaryClassName04() {
    String testSource = "package a.b.c;";
    SourceFile testSourceFile = new TargetSourceFile(Paths.get("a", "b", "c", "package-info.java"));

    JDTASTConstruction constructor = new JDTASTConstruction();
    GeneratedJDTAST ast =
        (GeneratedJDTAST) constructor.constructAST(testSourceFile, testSource.toCharArray());

    assertThat(ast.getPrimaryClassName(), is("a.b.c.package-info"));
  }

}
