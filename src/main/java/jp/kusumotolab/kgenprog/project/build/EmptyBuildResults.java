package jp.kusumotolab.kgenprog.project.build;

/**
 * ビルド失敗時を表すBuildResultsオブジェクト．<br>
 * いわゆるNullオブジェクト．
 * 
 * @author shinsuke
 *
 */
public class EmptyBuildResults extends BuildResults {

  /**
   * シングルトン
   */
  public static final EmptyBuildResults instance = new EmptyBuildResults();

  private EmptyBuildResults() {
    super(null, null, null, true);
  }

}
