package jp.kusumotolab.kgenprog.ga;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jp.kusumotolab.kgenprog.fl.Suspiciouseness;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;

public class RandomMutation implements Mutation {

    @Override
    public List<Base> exec(List<Suspiciouseness> suspiciousenesses) {
        List<Base> bases = suspiciousenesses.stream()
                .sorted(Comparator.comparingDouble(Suspiciouseness::getValue).reversed())
                .map(this::makeBase).collect(Collectors.toList());
        return bases;
    }

    private Base makeBase(Suspiciouseness suspiciouseness) {
        return new Base(suspiciouseness.getLocation(), makeOperationAtRandom());
    }

    private Operation makeOperationAtRandom() {
        return new NoneOperation();
    }
}
