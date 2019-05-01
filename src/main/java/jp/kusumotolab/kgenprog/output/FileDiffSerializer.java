package jp.kusumotolab.kgenprog.output;

import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * FileDiffをシリアライズするクラス<br>
 *
 * <table border="1">
 * <thead>
 * <tr>
 * <td>キー</td>
 * <td>説明</td>
 * </tr>
 * </thead>
 *
 * <tbody>
 * <tr>
 * <td>fileName</td>
 * <td>ファイル名</td>
 * </tr>
 *
 * <tr>
 * <td>diff</td>
 * <td>Unified形式の差分</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization">Gsonドキュメント</a>
 */
public class FileDiffSerializer implements JsonSerializer<FileDiff> {

  @Override
  public JsonElement serialize(final FileDiff fileDiff, final Type type,
      final JsonSerializationContext context) {

    final JsonObject serializedFileDiff = new JsonObject();
    serializedFileDiff.addProperty("fileName", fileDiff.fileName);
    serializedFileDiff.addProperty("diff", fileDiff.getDiff());

    return serializedFileDiff;
  }
}
