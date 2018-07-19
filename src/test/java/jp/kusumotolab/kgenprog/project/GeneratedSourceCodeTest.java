package jp.kusumotolab.kgenprog.project;

import static org.assertj.core.api.Assertions.assertThat;
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

    final TargetSourcePath p1 = new TargetSourcePath(Paths.get("a"));
    final TargetSourcePath p2 = new TargetSourcePath(Paths.get("b"));
    final GeneratedAST ast1 = new GeneratedASTMock(p1, Arrays.asList(l0, l1));
    final GeneratedAST ast2 = new GeneratedASTMock(p2, Arrays.asList(l2, l3, l4));

    final GeneratedSourceCode g = new GeneratedSourceCode(Arrays.asList(ast1, ast2));
    final List<Location> locations = g.getAllLocations();

    assertThat(locations).containsExactly(l0, l1, l2, l3, l4);
  }

  @Test
  public void testGetMessageDigest01() {
    final TargetSourcePath p1 = new TargetSourcePath(Paths.get("a"));
    final TargetSourcePath p2 = new TargetSourcePath(Paths.get("b"));
    final GeneratedAST ast1 = new GeneratedASTMock(p1, "aaa");
    final GeneratedAST ast2 = new GeneratedASTMock(p2, "bbb");
    final GeneratedSourceCode g = new GeneratedSourceCode(Arrays.asList(ast1, ast2));

    assertThat(g.getMessageDigest()).isEqualTo("6547436690A26A399603A7096E876A2D");
  }

  @Test
  public void testGetMessageDigest02() {
    final TargetSourcePath p1 = new TargetSourcePath(Paths.get("a"));
    final TargetSourcePath p2 = new TargetSourcePath(Paths.get("b"));
    final TargetSourcePath p3 = new TargetSourcePath(Paths.get("c"));
    final GeneratedAST ast1 = new GeneratedASTMock(p1, "aaa");
    final GeneratedAST ast2 = new GeneratedASTMock(p2, "bbb");
    final GeneratedAST ast3 = new GeneratedASTMock(p3, "ccc");

    final GeneratedSourceCode g1 = new GeneratedSourceCode(Arrays.asList(ast1, ast2, ast3));
    final GeneratedSourceCode g2 = new GeneratedSourceCode(Arrays.asList(ast2, ast3, ast1));

    assertThat(g1.getMessageDigest()).isEqualTo(g2.getMessageDigest());
  }

}
