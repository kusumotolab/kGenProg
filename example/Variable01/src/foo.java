import java.util.ArrayList;
import java.util.List;

class Foo {

  final double double_1 = 0.0d;
  double double_2_1, double_2_2 = 0.0d;

  public void bar(final int int_1) {
    final String str_1 = "";
    if (true) {
      String str_2_1, str2_2 = "";
      if (true) {
        String str_3 = "";
      } else {
        String str_4 = "";
      }
      String str_5 = "";
      System.out.println(str_1);
    }

    if (true) {
      String str_6 = "";
    }
    String str_7 = "";
  }

  private void piyo() {
    for (int i = 0; i < 10; i++) {
      System.out.println(i);
    }
    final List<String> list = new ArrayList<>();
    for (final String string : list) {
      System.out.println(string);
    }
  }
}
