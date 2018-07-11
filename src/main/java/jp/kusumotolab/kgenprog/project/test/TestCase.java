package jp.kusumotolab.kgenprog.project.test;

import java.io.Serializable;
import jp.kusumotolab.kgenprog.project.SourceFile;

@Deprecated
public class TestCase implements Serializable {

  private SourceFile testFile;
  private String name;
  private int startLine;

  public TestCase(SourceFile testFile, String name, int startLine) {
    this.testFile = testFile;
    this.name = name;
    this.startLine = startLine;
  }
}
