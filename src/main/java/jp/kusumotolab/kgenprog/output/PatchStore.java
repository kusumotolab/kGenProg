package jp.kusumotolab.kgenprog.output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 全てのテストを通過したバリアントの変更内容．
 * @author k-naitou
 *
 */
public class PatchStore {

  private static final Logger log = LoggerFactory.getLogger(PatchStore.class);

  private final List<Patch> patchList = new ArrayList<>();

  public void add(final Patch patch) {
    patchList.add(patch);
  }

  public void writeToFile(final Path outDir) {

    for (final Patch patch : patchList) {
      final String variantId = makeVariantId(patch);
      final Path variantDir = outDir.resolve(variantId);
      patch.writeToFile(variantDir);

      // 各ファイルの差分のリストを取得する
      final List<FileDiff> fileDiffs = patch.getAll();
      final List<String> diffs = fileDiffs.stream()
          .map(FileDiff::getDiff)
          .collect(Collectors.toList());

      // variantX.patchを出力
      try {
        Files.write(outDir.resolve(variantId + ".patch"), diffs);
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  public void writeToLogger() {
    for (final Patch patch : patchList) {
      patch.writeToLogger();
    }
  }

  private String makeVariantId(final Patch patch) {
    return "variant" + (patchList.indexOf(patch) + 1);
  }
}
