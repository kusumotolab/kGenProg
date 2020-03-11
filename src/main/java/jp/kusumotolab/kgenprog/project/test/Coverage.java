package jp.kusumotolab.kgenprog.project.test;

import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

/**
 * カバレッジ情報を表すインタフェース．<br>
 *
 * 当該カバレッジ情報に対する以下のようなクエリを受け付ける，<br>
 * - どのソースコードの情報なのか<br>
 * - どの位置（≒行）がどのステータス（実行されたか否か等）なのか<br>
 * - ステータスの総数（≒行数）<br>
 *
 * @author shinsuke
 */
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

  /**
   * 本カバレジに対応する実行テストのFQNを取得
   *
   * @return 実行テストのFQN
   */
  FullyQualifiedName getExecutedTargetFQN();

  /**
   * 行に対するステータスを取得
   *
   * @param index 行番号
   * @return ステータス値
   */
  Status getStatus(final int index);

  /**
   * ステータスの長さ（トータル行）を取得
   *
   * @return
   */
  int getStatusesSize();

  String toString(final int indentDepth);

}
