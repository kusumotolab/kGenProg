package jp.kusumotolab.kgenprog.fl;

import java.util.List;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public interface FaultLocalization {

  public List<Suspiciousness> exec(GeneratedSourceCode generatedSourceCode, TestResults testResults);

  public enum Technique {
    Ample {
      @Override
      public FaultLocalization initialize() {
        return new Ample();
      }
    },

    Jaccard {
      @Override
      public FaultLocalization initialize() {
        return new Jaccard();
      }
    },

    Ochiai {
      @Override
      public FaultLocalization initialize() {
        return new Ochiai();
      }
    },

    Tarantula {
      @Override
      public FaultLocalization initialize() {
        return new Tarantula();
      }
    },

    Zoltar {
      @Override
      public FaultLocalization initialize() {
        return new Zoltar();
      }
    };

    public abstract FaultLocalization initialize();
  }
}
