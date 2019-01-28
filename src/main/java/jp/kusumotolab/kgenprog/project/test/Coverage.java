package jp.kusumotolab.kgenprog.project.test;

import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

public interface Coverage {

  enum Status {
    /**
     * 実行不可能な行 (value is 0x00).
     */
    EMPTY,
    /**
     * 実行可能だが実行されなかった行 (value is 0x01).
     */
    NOT_COVERED,
    /**
     * 実行可能で実行された行 (value is 0x02).
     */
    COVERED,
    /**
     * 実行可能で一部だけ実行された行(value is 0x03). TODO 現在このステータスは一切利用していない．
     * jacocoはif分岐等にこの値をセットするが，本Statusではif分岐はCOVEREDに持ち上げ．
     */
    PARTLY_COVERED
  }

  FullyQualifiedName getExecutedTargetFQN();

  Status getStatus(final int index);

  int getStatusesSize();

  String toString(final int indentDepth);
}
