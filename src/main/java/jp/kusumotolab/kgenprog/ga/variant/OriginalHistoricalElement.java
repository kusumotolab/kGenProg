package jp.kusumotolab.kgenprog.ga.variant;

import java.util.Collections;
import java.util.List;

/**
 * 初期 Variant のための HistoricalElement
 *
 * @see HistoricalElement
 */
public class OriginalHistoricalElement implements HistoricalElement {

  /**
   * @return 親が存在しないので要素数 0 のリストを返す
   */
  @Override
  public List<Variant> getParents() {
    return Collections.emptyList();
  }

  /**
   * @return 適用した操作がないので空文字をを返す
   */
  @Override
  public String getOperationName() {
    return "";
  }

}
