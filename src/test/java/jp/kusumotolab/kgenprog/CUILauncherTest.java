package jp.kusumotolab.kgenprog;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ch.qos.logback.classic.Level;

public class CUILauncherTest {

  private CUILauncher launcher;

  @Before
  public void setUp() {
    launcher = new CUILauncher();
  }

  @After
  public void tearDown() {
    launcher = null;
  }

  @Test
  public void getLogLevel() {
    assertThat(launcher.getLogLevel()).isEqualTo(Level.INFO);
  }

  @Test
  public void setLogLevelDebug() {
    launcher.setLogLevelDebug(true);
    assertThat(launcher.getLogLevel()).isEqualTo(Level.DEBUG);
  }

  @Test
  public void setLogLevelError() {
    launcher.setLogLevelError(true);
    assertThat(launcher.getLogLevel()).isEqualTo(Level.ERROR);
  }
}
