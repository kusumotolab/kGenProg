package jp.kusumotolab.kgenprog.project;

public class LineNumberRange {

  /** 開始行番号 (この行を含む) */
  public final int start;

  /** 終了行番号 (この行を含む) */
  public final int end;

  public LineNumberRange(final int start, final int end) {
    this.start = start;
    this.end = end;
  }

  public int getLength() {
    return this.end - start + 1;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + end;
    result = prime * result + start;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    LineNumberRange other = (LineNumberRange) obj;
    if (end != other.end) {
      return false;
    }
    if (start != other.start) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Range [start=" + start + ", end=" + end + "]";
  }
}
