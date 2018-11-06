package jp.kusumotolab.kgenprog.project.build;

import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAR;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAZ;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.QUX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.SourcePath;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.testutil.ExampleAlias.Src;

public class BinaryStoreTest {

  static BinaryStore binStore = new BinaryStore();
  static String digest1;
  static String digest2;
  static String digest3;
  static SourcePath path1;
  static SourcePath path2;
  static SourcePath path3;
  static JavaBinaryObject object1;
  static JavaBinaryObject object2;
  static JavaBinaryObject object3;
  static JavaBinaryObject object4;
  static GeneratedAST<ProductSourcePath> ast1;
  static GeneratedAST<ProductSourcePath> ast2;
  static GeneratedAST<ProductSourcePath> ast3;

  @BeforeClass()
  @SuppressWarnings("unchecked")
  public static void beforeAll() {
    digest1 = "1111";
    digest2 = "2222";
    digest3 = "3333";

    path1 = new ProductSourcePath(Src.FOO);
    path2 = new ProductSourcePath(Src.BAR);
    path3 = new ProductSourcePath(Src.BAZ);

    object1 = new JavaBinaryObject(FOO, FOO, digest1, path1, false);
    object2 = new JavaBinaryObject(BAR, BAR, digest2, path2, false);
    object3 = new JavaBinaryObject(BAZ, BAZ, digest3, path3, false);
    object4 = new JavaBinaryObject(QUX, BAZ, digest3, path3, false); // Barの内部クラスを想定

    ast1 = mock(GeneratedAST.class);
    ast2 = mock(GeneratedAST.class);
    ast3 = mock(GeneratedAST.class);
    when(ast1.getMessageDigest()).thenReturn(digest1);
    when(ast2.getMessageDigest()).thenReturn(digest2);
    when(ast3.getMessageDigest()).thenReturn(digest3);
  }

  @Before
  public void before() {
    binStore.removeAll(); // Must be cleared before tests
  }

  @Test
  // 基本操作の確認．putしてgetできるか
  public void testStoreAndGet() {
    binStore.add(object1);
    binStore.add(object2);

    assertThat(binStore.getAll()).containsExactlyInAnyOrder(object1, object2);

    assertThat(binStore.get(FOO, digest1)).containsExactly(object1);
    assertThat(binStore.get(BAR, digest2)).containsExactly(object2);
    assertThat(binStore.get(BAZ, digest3)).isEmpty();

    assertThat(binStore.get(FOO)).isSameAs(object1);
    assertThat(binStore.get(BAR)).isSameAs(object2);
    // assertThat(binStore.get(BAZ)).isNull(); // TODO NPE発生するので修正すべき
  }

  @Test
  // 空の状態からnullが返ってくるか
  public void testJustGet() {
    assertThat(binStore.get(FOO, digest1)).isEmpty();
    assertThat(binStore.get(BAR, digest2)).isEmpty();
    assertThat(binStore.get(BAZ, digest3)).isEmpty();
  }

  @Test
  // getの確認．パッケージ名を指定して期待のバイナリが返ってくるか
  public void testList() {
    binStore.add(object1);
    binStore.add(object2);

    // "example" とは異なる名前のJMOバイナリを追加
    final FullyQualifiedName fqn = new TargetFullyQualifiedName("xxx.BarTest");
    final JavaBinaryObject dummy = new JavaBinaryObject(fqn, fqn, "4444", null, false);
    binStore.add(dummy);

    // o1とo2だけのはず（dummyは含まれない）
    assertThat(binStore.get("example")).containsExactlyInAnyOrder(object1, object2);
  }

  @Test
  // 基本操作の確認．内部クラスの操作
  public void testStoreAndGetForInnerClass() {
    binStore.add(object1);
    binStore.add(object2);
    binStore.add(object3);
    binStore.add(object4);

    assertThat(binStore.getAll()).containsExactlyInAnyOrder(object1, object2, object3, object4);

    assertThat(binStore.get(FOO, digest1)).containsExactly(object1);
    assertThat(binStore.get(BAR, digest2)).containsExactly(object2);
    assertThat(binStore.get(BAZ, digest3)).containsExactlyInAnyOrder(object3, object4);

    assertThat(binStore.get(FOO)).isSameAs(object1);
    assertThat(binStore.get(BAR)).isSameAs(object2);
    assertThat(binStore.get(BAZ)).isSameAs(object3);
    assertThat(binStore.get(QUX)).isSameAs(object4);

    assertThat(binStore.get(path1)).containsExactly(object1);
    assertThat(binStore.get(path2)).containsExactly(object2);
    assertThat(binStore.get(path3)).containsExactlyInAnyOrder(object3, object4);
  }

}
