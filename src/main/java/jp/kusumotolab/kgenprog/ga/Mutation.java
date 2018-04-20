package jp.kusumotolab.kgenprog.ga;

import java.util.List;

import jp.kusumotolab.kgenprog.fl.Suspiciouseness;

public interface Mutation {

    public List<Base> exec(List<Suspiciouseness> suspiciousenesses);
}
