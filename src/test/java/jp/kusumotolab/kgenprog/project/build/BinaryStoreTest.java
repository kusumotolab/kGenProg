package jp.kusumotolab.kgenprog.project.build;

import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAR;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAZ;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.nio.file.Paths;
import javax.tools.JavaFileObject.Kind;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

public class BinaryStoreTest {

  static BinaryStore binStore = new BinaryStore();
  static String digest1;
  static String digest2;
  static String digest3;
  static BinaryStoreKey key1;
  static BinaryStoreKey key2;
  static BinaryStoreKey key3;
  static JavaBinaryObject object1;
  static JavaBinaryObject object2;
  static JavaBinaryObject object3;
  static GeneratedAST<ProductSourcePath> ast1;
  static GeneratedAST<ProductSourcePath> ast2;
  static GeneratedAST<ProductSourcePath> ast3;

  @BeforeClass()
  @SuppressWarnings("unchecked")
  public static void beforeAll() {
    digest1 = "1111";
    digest2 = "2222";
    digest3 = "3333";

    key1 = new BinaryStoreKey(FOO.value, digest1);
    key2 = new BinaryStoreKey(BAR.value, digest2);
    key3 = new BinaryStoreKey(BAZ.value, digest3);

    object1 = new JavaBinaryObject(key1.toString(), FOO.value, Kind.CLASS, digest1,
        new ProductSourcePath(Paths.get("")));
    object2 = new JavaBinaryObject(key2.toString(), BAR.value, Kind.CLASS, digest2,
        new ProductSourcePath(Paths.get("")));
    object3 = new JavaBinaryObject(key3.toString(), BAZ.value, Kind.CLASS, digest3,
        new ProductSourcePath(Paths.get("")));

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
  public void testStoreAndGetByPath() {
    binStore.add(object1);
    binStore.add(object2);

    assertThat(binStore.get(key1)).containsExactly(object1);
    assertThat(binStore.get(key2)).containsExactly(object2);
    assertThat(binStore.get(key3)).isEmpty();
  }

  @Test
  // 空の状態からnullが返ってくるか
  public void testJustGet() {
    assertThat(binStore.get(key1)).isEmpty();
    assertThat(binStore.get(key2)).isEmpty();
    assertThat(binStore.get(key3)).isEmpty();
  }

  @Test
  // listの確認．パッケージ名を指定して期待のバイナリが返ってくるか
  public void testList() {
    binStore.add(object1);
    binStore.add(object2);

    // "example" とは異なる名前のJMOバイナリを追加
    final String dummyPackName = "xxx.BarTest";
    final JavaBinaryObject dummy =
        new JavaBinaryObject(dummyPackName, dummyPackName, Kind.CLASS, "4444", null);
    binStore.add(dummy);

    // o1とo2だけのはず（dummyは含まれない）
    assertThat(binStore.list("example")).containsExactlyInAnyOrder(object1, object2);
  }



}
