package jp.kusumotolab.kgenprog.project;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class GeneratedSourceCodeTest {

  private static class GeneratedASTMock implements GeneratedAST<ProductSourcePath> {

    private final ProductSourcePath path;
    private final String messageDigest;

    public GeneratedASTMock(final ProductSourcePath path, final String messageDigest) {
      this.path = path;
      this.messageDigest = messageDigest;
    }

    @Override
    public String getSourceCode() {
      return null;
    }

    @Override
    public FullyQualifiedName getPrimaryClassName() {
      return null;
    }

    @Override
    public ProductSourcePath getSourcePath() {
      return path;
    }

    @Override
    public String getMessageDigest() {
      return messageDigest;
    }

    @Override
    public ASTLocations createLocations() {
      return null;
    }

  }

  @Test
  public void testGetMessageDigest01() {
    final ProductSourcePath p1 = new ProductSourcePath(Paths.get("a"));
    final ProductSourcePath p2 = new ProductSourcePath(Paths.get("b"));
    final GeneratedAST<ProductSourcePath> ast1 = new GeneratedASTMock(p1, "aaa");
    final GeneratedAST<ProductSourcePath> ast2 = new GeneratedASTMock(p2, "bbb");
    final GeneratedSourceCode g =
        new GeneratedSourceCode(Arrays.asList(ast1, ast2), Collections.emptyList());

    assertThat(g.getMessageDigest()).isEqualTo("6547436690a26a399603a7096e876a2d");
  }

  @Test
  public void testGetMessageDigest02() {
    final ProductSourcePath p1 = new ProductSourcePath(Paths.get("a"));
    final ProductSourcePath p2 = new ProductSourcePath(Paths.get("b"));
    final ProductSourcePath p3 = new ProductSourcePath(Paths.get("c"));
    final GeneratedAST<ProductSourcePath> ast1 = new GeneratedASTMock(p1, "aaa");
    final GeneratedAST<ProductSourcePath> ast2 = new GeneratedASTMock(p2, "bbb");
    final GeneratedAST<ProductSourcePath> ast3 = new GeneratedASTMock(p3, "ccc");

    final GeneratedSourceCode g1 =
        new GeneratedSourceCode(Arrays.asList(ast1, ast2, ast3), Collections.emptyList());
    final GeneratedSourceCode g2 =
        new GeneratedSourceCode(Arrays.asList(ast2, ast3, ast1), Collections.emptyList());

    assertThat(g1.getMessageDigest()).isEqualTo(g2.getMessageDigest());
  }

}
