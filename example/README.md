﻿# BuildFailure
ビルドに失敗する題材．  
内部処理（ビルドやFL）の振る舞いを確かめる目的であり，APR用の題材ではないことに注意．  
処理の中身には意味がない．

# BuildSuccess
ビルドに成功する題材．  
内部処理（ビルドやFL）の振る舞いを確かめる目的であり，APR用の題材ではないことに注意．  
処理の中身には意味がない．

### BuildSuccess01
1実装クラスと1テストクラス．  
4テストのうち，1テストでfailする．  
JDT-ASTによる狂ったコードフォーマットを適用済み．  

### BuildSuccess02
2実装クラスと2テストクラス．  
JDT-ASTによる狂ったコードフォーマットを適用済み．  
複数クラスに対するビルドの振る舞いを試す題材．  

### BuildSuccess03
3実装クラスと3テストクラス．  
JDT-ASTによる狂ったコードフォーマットを適用済み．
Bazのみまともなフォーマットを適用．
内部クラスや匿名クラスに対するビルドの振る舞いを試す題材．  

### BuildSuccess04
1実装クラスと1テストクラス．  
初期状態で無限ループするテストケースを含む題材．  

### BuildSuccess05
1実装クラスと1テストクラス．  
Maven（pom.xml）プロジェクト．  

### BuildSuccess08
1実装クラスと1テストクラス．  
JUnit以外の外部ライブラリを用いる題材．  

### BuildSuccess09
1実装クラスと1テストクラス．  
末尾にブランク改行を含むファイルに対するログの振る舞いを試す題材．

# CloseToZero
APR用の題材．  
整数をゼロに一つ近づけるメソッド`close_to_zero(n)`の修正を試みる．  
単一バグを含み単一操作によって修正可能な最も単純なAPR題材．  
処理の本質が「if文+加減算」だけなので，様々な修正方法が存在する．  

### CloseToZero01
bug：不要な行が挿入されている．  

### CloseToZero02
bug：不要な行が挿入されている．  

### CloseToZero03
bug：条件式が不足している．  

### CloseToZero04
bug：条件式が逆になっている．  

# GCD
APR用の題材．  
[ICSE2009のAPR論文](https://dl.acm.org/citation.cfm?id=1555051)の例題プログラムの亜種．  

# QuickSort01
APR用の題材．  
無限ループを含む題材．  

# Math
実アプリの題材．
Apache Commons Mathの開発過程で生じたバグ．
[Defects4J](https://github.com/rjust/defects4j)から取得．

### Math02
Developer's patch:
```diff
diff --git a/org/apache/commons/math3/distribution/HypergeometricDistribution.java b/org/apache/commons/math3/distribution/HypergeometricDistribution.java
index 81e180c..2769127 100644
--- a/org/apache/commons/math3/distribution/HypergeometricDistribution.java
+++ b/org/apache/commons/math3/distribution/HypergeometricDistribution.java
@@ -265,7 +265,7 @@ public class HypergeometricDistribution extends AbstractIntegerDistribution {
      * size {@code n}, the mean is {@code n * m / N}.
      */
     public double getNumericalMean() {
+        return getSampleSize() * (getNumberOfSuccesses() / (double) getPopulationSize());
-        return (double) (getSampleSize() * getNumberOfSuccesses()) / (double) getPopulationSize();
     }
 
     /**
```
https://github.com/Spirals-Team/defects4j-repair/tree/master/results/2015-august#math-2

### Math05
Developer's patch:
```diff
diff --git a/org/apache/commons/math3/complex/Complex.java b/org/apache/commons/math3/complex/Complex.java
index ac8185b..22b23f2 100644
--- a/org/apache/commons/math3/complex/Complex.java
+++ b/org/apache/commons/math3/complex/Complex.java
@@ -302,7 +302,7 @@ public class Complex implements FieldElement<Complex>, Serializable  {
         }
 
         if (real == 0.0 && imaginary == 0.0) {
+            return INF;
-            return NaN;
         }
 
         if (isInfinite) {
```
https://github.com/Spirals-Team/defects4j-repair/tree/master/results/2015-august#math-5

### Math70
Developer's patch:
```diff
diff --git a/org/apache/commons/math/analysis/solvers/BisectionSolver.java b/org/apache/commons/math/analysis/solvers/BisectionSolver.java
index 180caef..3f66927 100644
--- a/org/apache/commons/math/analysis/solvers/BisectionSolver.java
+++ b/org/apache/commons/math/analysis/solvers/BisectionSolver.java
@@ -69,7 +69,7 @@ public class BisectionSolver extends UnivariateRealSolverImpl {
     /** {@inheritDoc} */
     public double solve(final UnivariateRealFunction f, double min, double max, double initial)
         throws MaxIterationsExceededException, FunctionEvaluationException {
+        return solve(f, min, max);
-        return solve(min, max);
     }
 
     /** {@inheritDoc} */
```
https://github.com/Spirals-Team/defects4j-repair/tree/master/results/2015-august#math-70

### Math73
Developer's patch:
```diff
diff --git a/org/apache/commons/math/analysis/solvers/BrentSolver.java b/org/apache/commons/math/analysis/solvers/BrentSolver.java
index e0cb427..4e95ed5 100644
--- a/org/apache/commons/math/analysis/solvers/BrentSolver.java
+++ b/org/apache/commons/math/analysis/solvers/BrentSolver.java
@@ -32,11 +32,6 @@ import org.apache.commons.math.analysis.UnivariateRealFunction;
  */
 public class BrentSolver extends UnivariateRealSolverImpl {
 
+    /** Error message for non-bracketing interval. */
+    private static final String NON_BRACKETING_MESSAGE =
+        "function values at endpoints do not have different signs.  " +
+        "Endpoints: [{0}, {1}], Values: [{2}, {3}]";
-
     /** Serializable version identifier */
     private static final long serialVersionUID = 7694577816772532779L;
 
@@ -133,11 +128,6 @@ public class BrentSolver extends UnivariateRealSolverImpl {
             return solve(f, initial, yInitial, max, yMax, initial, yInitial);
         }
 
+        if (yMin * yMax > 0) {
+            throw MathRuntimeException.createIllegalArgumentException(
+                  NON_BRACKETING_MESSAGE, min, max, yMin, yMax);
+        }
-
         // full Brent algorithm starting with provided initial guess
         return solve(f, min, yMin, max, yMax, initial, yInitial);
 
@@ -186,7 +176,9 @@ public class BrentSolver extends UnivariateRealSolverImpl {
             } else {
                 // neither value is close to zero and min and max do not bracket root.
                 throw MathRuntimeException.createIllegalArgumentException(
+                        NON_BRACKETING_MESSAGE, min, max, yMin, yMax);
-                        "function values at endpoints do not have different signs.  " +
-                        "Endpoints: [{0}, {1}], Values: [{2}, {3}]",
-                        min, max, yMin, yMax);
             }
         } else if (sign < 0){
             // solve using only the first endpoint as initial guess
```
https://github.com/Spirals-Team/defects4j-repair/tree/master/results/2015-august#math-73

### Math85
Developer's patch:
```diff
diff --git a/org/apache/commons/math/analysis/solvers/UnivariateRealSolverUtils.java b/org/apache/commons/math/analysis/solvers/UnivariateRealSolverUtils.java
index e6398f6..5b76415 100644
--- a/org/apache/commons/math/analysis/solvers/UnivariateRealSolverUtils.java
+++ b/org/apache/commons/math/analysis/solvers/UnivariateRealSolverUtils.java
@@ -195,7 +195,7 @@ public class UnivariateRealSolverUtils {
         } while ((fa * fb > 0.0) && (numIterations < maximumIterations) && 
                 ((a > lowerBound) || (b < upperBound)));
    
+        if (fa * fb > 0.0 ) {
-        if (fa * fb >= 0.0 ) {
             throw new ConvergenceException(
                       "number of iterations={0}, maximum iterations={1}, " +
                       "initial={2}, lower bound={3}, upper bound={4}, final a value={5}, " +
```
https://github.com/Spirals-Team/defects4j-repair/tree/master/results/2015-august#math-85
