package jp.kusumotolab.kgenprog.project.build;

import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAR;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.BAZ;
import static jp.kusumotolab.kgenprog.testutil.ExampleAlias.Fqn.FOO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.test.FullyQualifiedName;

public class BinaryStoreTest {

  static BinaryStore binStore = BinaryStore.instance;
  static FullyQualifiedName fqn1;
  static FullyQualifiedName fqn2;
  static FullyQualifiedName fqn3;
  static JavaMemoryObject object1;
  static JavaMemoryObject object2;
  static JavaMemoryObject object3;
  static GeneratedAST ast1;
  static GeneratedAST ast2;
  static GeneratedAST ast3;

  @BeforeClass()
  public static void beforeAll() {
    fqn1 = FOO;
    fqn2 = BAR;
    fqn3 = BAZ;

    object1 = new JavaMemoryObject(fqn1.toString(), Kind.CLASS);
    object2 = new JavaMemoryObject(fqn2.toString(), Kind.CLASS);
    object3 = new JavaMemoryObject(fqn3.toString(), Kind.CLASS);

    ast1 = mock(GeneratedAST.class);
    ast2 = mock(GeneratedAST.class);
    ast3 = mock(GeneratedAST.class);
    when(ast1.getMessageDigest()).thenReturn("aaa");
    when(ast2.getMessageDigest()).thenReturn("bbb");
    when(ast3.getMessageDigest()).thenReturn("ccc");
  }

  @Before
  public void before() {
    binStore.removeAll(); // Must be cleared before tests
  }

  @Test
  // 基本操作の確認．putしてgetできるか
  public void testStoreAndGetByPath() {
    binStore.put(fqn1, object1);
    binStore.put(fqn2, object2);

    assertThat(binStore.get(fqn1)).isSameAs(object1);
    assertThat(binStore.get(fqn2)).isSameAs(object2);
    assertThat(binStore.get(fqn3)).isNull();
  }

  @Test
  // キャッシュを上書きできるか
  public void testOverrideByPath() {
    binStore.put(fqn1, object1);
    binStore.put(fqn1, object2); // force override

    assertThat(binStore.get(fqn1)).isSameAs(object2);
  }

  @Test
  // 空の状態からnullが返ってくるか
  public void testJustGet() {
    assertThat(binStore.get(fqn1)).isNull();
    assertThat(binStore.get(fqn2)).isNull();
    assertThat(binStore.get(fqn3)).isNull();
  }

  @Test
  // listの確認．パッケージ名を指定して期待のバイナリが返ってくるか
  public void testList() {
    binStore.put(fqn1, object1);
    binStore.put(fqn2, object2);

    // "example" とは異なる名前のJMOバイナリを追加
    final String dummyPackName = "xxx.BarTest";
    final JavaFileObject dummy = new JavaMemoryObject(dummyPackName, Kind.CLASS);
    binStore.put(fqn3, dummy);

    // o1とo2だけのはず（dummyは含まれない）
    assertThat(binStore.list("example")).containsExactlyInAnyOrder(object1, object2);
  }



}
