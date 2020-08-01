package jp.kusumotolab.kgenprog;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class CUILauncherTest {

  @Rule
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();

  @Test
  public void testNullArgs() {
    exit.expectSystemExitWithStatus(1);
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final PrintStream printStream = System.out;
    System.setOut(new PrintStream(out));

    final CUILauncher launcher = new CUILauncher();
    CUILauncher.main(null);
    assertThat(out.toString()).contains("NullPointerException");

    System.setOut(printStream);
  }
}
