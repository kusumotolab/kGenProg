package jp.kusumotolab.kgenprog.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

/**
 * 解のパッチをログに出力する．
 */
class PatchLogExporter implements Exporter {

  private static final Logger log = LoggerFactory.getLogger(PatchLogExporter.class);

  /**
   * パッチをログに出力する．
   *
   * @param variantStore バリアントを保持するクラス
   */
  @Override
  public void export(final VariantStore variantStore) {
    final PatchGenerator generator = new PatchGenerator();
    variantStore.getFoundSolutions()
        .stream()
        .map(generator::exec)
        .forEach(this::writeToLog);
  }

  private void writeToLog(final Patch patch) {
    patch.getFileDiffs()
        .forEach(fd -> log.info(String.format("patch (v%d)%s%s",
            patch.getVariantId(), System.lineSeparator(), fd.toStringWithDefaultEncoding()))
        );
  }

}
