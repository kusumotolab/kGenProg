package jp.kusumotolab.kgenprog.project.jdt;

import static org.junit.Assert.*;
import java.nio.file.Paths;
import static org.hamcrest.Matchers.*;
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
    Location location = ast.inferASTNode(3);

    assertThat(location, instanceOf(JDTLocation.class));
    JDTLocation jdtLocation = (JDTLocation) location;

    assertThat(jdtLocation.node.toString(), is("int a=0;\n"));
  }

  @Test
  public void testInferASTNode02() {
    Location location = ast.inferASTNode(5);

    assertThat(location, instanceOf(JDTLocation.class));
    JDTLocation jdtLocation = (JDTLocation) location;

    assertThat(jdtLocation.node.toString(), is("System.out.println(a);\n"));
  }

  @Test
  public void testInferASTNode03() {
    Location location = ast.inferASTNode(1);

    assertThat(location, nullValue());
  }

}
