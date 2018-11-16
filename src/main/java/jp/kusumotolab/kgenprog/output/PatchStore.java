package jp.kusumotolab.kgenprog.output;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 * 全てのテストを通過したバリアントの変更内容．
 * @author k-naitou
 *
 */
public class PatchStore {

  private final List<Patch> patchList = new ArrayList<>();

  public void add(final Patch patch) {
    patchList.add(patch);
  }

  public void writeToFile(final Path outDir) {
    final String timeStamp = getTimeStamp();
    final Path outDirInthisExecution = outDir.resolve(timeStamp);

    for (final Patch patch : patchList) {
      final String variantId = makeVariantId(patch);
      final Path variantDir = outDirInthisExecution.resolve(variantId);
      patch.writeToFile(variantDir);
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

  private String getTimeStamp() {
    final Date date = new Date();
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    return sdf.format(date);
  }
}
