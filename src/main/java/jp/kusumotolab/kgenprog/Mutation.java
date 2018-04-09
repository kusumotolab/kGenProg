package jp.kusumotolab.kgenprog;

import java.util.List;

public interface Mutation {

    public List<Base> exec(List<Suspiciouseness> suspiciousenesses);
}
