package example;

import static org.assertj.core.api.Assertions.assertThat;
import org.hamcrest.Matchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

public class FooTest {

  Foo foo = new Foo();

  @Test
  public void test01() {
    assertThat(foo.foo(10)).isEqualTo(9);
  }

  @Test
  public void test02() {
    assertThat(foo.foo(100)).isEqualTo(99);
  }

  @Test
  public void test03() {
    assertThat(foo.foo(0)).isEqualTo(0); // fails
  }

  @Test
  public void test04() {
    assertThat(foo.foo(-10)).isEqualTo(-9);

    // try to use hamcrest instead of assertj
    MatcherAssert.assertThat(foo.foo(-10), Matchers.is(-9));
  }
}
