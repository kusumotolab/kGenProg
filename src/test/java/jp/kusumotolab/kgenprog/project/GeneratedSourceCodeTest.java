package jp.kusumotolab.kgenprog.project;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.jdt.JDTLocation;

public class GeneratedSourceCodeTest {

  private static class GeneratedASTMock implements GeneratedAST {

    private final SourcePath path;
    private final List<Location> locations;
    private final String messageDigest;

    public GeneratedASTMock(final SourcePath path, final List<Location> locations) {
      this.path = path;
      this.locations = locations;
      this.messageDigest = "";
    }

    public GeneratedASTMock(final SourcePath path, final String messageDigest) {
      this.path = path;
      this.locations = null;
      this.messageDigest = messageDigest;
    }

    @Override
    public String getSourceCode() {
      return null;
    }

    @Override
    public String getPrimaryClassName() {
      return null;
    }

    @Override
    public SourcePath getSourcePath() {
      return path;
    }

    @Override
    public List<Location> inferLocations(int lineNumber) {
      return null;
    }

    @Override
    public List<Location> getAllLocations() {
      return locations;
    }

    @Override
    public String getMessageDigest() {
      return messageDigest;
    }

  }

  @Test
  public void testGetAllLocations() {
    final Location l0 = new JDTLocation(null, null);
    final Location l1 = new JDTLocation(null, null);
    final Location l2 = new JDTLocation(null, null);
    final Location l3 = new JDTLocation(null, null);
    final Location l4 = new JDTLocation(null, null);

    final GeneratedAST ast1 =
        new GeneratedASTMock(new TargetSourcePath(Paths.get("a")), Arrays.asList(l0, l1));
    final GeneratedAST ast2 =
        new GeneratedASTMock(new TargetSourcePath(Paths.get("b")), Arrays.asList(l2, l3, l4));
    final GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Arrays.asList(ast1, ast2));

    final List<Location> locations = generatedSourceCode.getAllLocations();

    assertThat(locations, hasSize(5));
    assertThat(locations.get(0), is(l0));
    assertThat(locations.get(1), is(l1));
    assertThat(locations.get(2), is(l2));
    assertThat(locations.get(3), is(l3));
    assertThat(locations.get(4), is(l4));

  }

  @Test
  public void testGetMessageDigest01() {
    final GeneratedAST ast1 = new GeneratedASTMock(new TargetSourcePath(Paths.get("a")), "aaa");
    final GeneratedAST ast2 = new GeneratedASTMock(new TargetSourcePath(Paths.get("b")), "bbb");
    final GeneratedSourceCode generatedSourceCode =
        new GeneratedSourceCode(Arrays.asList(ast1, ast2));
    assertThat(generatedSourceCode.getMessageDigest(), is("6547436690A26A399603A7096E876A2D"));
  }

  @Test
  public void testGetMessageDigest02() {
    final GeneratedAST ast1 = new GeneratedASTMock(new TargetSourcePath(Paths.get("a")), "aaa");
    final GeneratedAST ast2 = new GeneratedASTMock(new TargetSourcePath(Paths.get("b")), "bbb");
    final GeneratedAST ast3 = new GeneratedASTMock(new TargetSourcePath(Paths.get("c")), "ccc");

    final GeneratedSourceCode generatedSourceCode1 =
        new GeneratedSourceCode(Arrays.asList(ast1, ast2, ast3));
    final GeneratedSourceCode generatedSourceCode2 =
        new GeneratedSourceCode(Arrays.asList(ast2, ast3, ast1));

    assertThat(generatedSourceCode1.getMessageDigest()
        .equals(generatedSourceCode2.getMessageDigest()), is(true));
  }

}
