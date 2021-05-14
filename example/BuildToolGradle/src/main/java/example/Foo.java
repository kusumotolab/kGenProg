package example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Foo {

  // this requires BOTH slf4j and sl4j-api implementations
  private final static Logger logger = LoggerFactory.getLogger(Foo.class);

  public int foo(int n) {
    if (n > 0) {
      n--;
    } else {
      n++;
    }
    logger.info("n = {}", n);
    return n;
  }
}
