package jp.kusumotolab.kgenprog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ファイルの文字コードを調べるクラス
 */
public class CharsetDetector {

  private final static Logger log = LoggerFactory.getLogger(CharsetDetector.class);

  /**
   * 与えられたファイルの文字コードを調べる．
   * 文字コードがわからなかった場合はデフォルトの文字コードを返す
   *
   * @param path 文字コードを調べたいファイル
   *
   * @return ファイルの文字コード
   */
  public Charset detect(final Path path) {
    try {
      final String charsetName = UniversalDetector.detectCharset(path);
      return charsetName != null ? Charset.forName(charsetName) : Charset.defaultCharset();
    } catch (final IOException e) {
      log.error(e.getMessage(), e);
      return Charset.defaultCharset();
    }
  }
}
