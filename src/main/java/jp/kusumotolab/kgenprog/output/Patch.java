package jp.kusumotolab.kgenprog.output;

import java.util.List;

/***
 * 1バリアントの変更内容．
 * FileDiffの集合．
 *
 * @author k-naitou
 *
 */
public class Patch {

  private final List<FileDiff> fileDiffs;
  private final long variantId;

  public Patch(List<FileDiff> fileDiffs, final long variantId) {
    this.fileDiffs = fileDiffs;
    this.variantId = variantId;
  }

  public List<FileDiff> getFileDiffs() {
    return fileDiffs;
  }

  public long getVariantId() {
    return variantId;
  }
}
