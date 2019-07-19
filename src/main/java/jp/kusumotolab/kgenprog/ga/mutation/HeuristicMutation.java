package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import com.google.common.collect.Lists;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.TargetFullyQualifiedName;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertBeforeOperation;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTLocation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

/**
 * ヒューリティクスを適用してビルドサクセスの数を増やす Mutation
 * 現状，修正対象の行でアクセスできる変数名に書き換えて変異処理を行う
 */
public class HeuristicMutation extends Mutation {

  protected final Type type;
  private final AccessibleVariableSearcher variableSearcher = new AccessibleVariableSearcher();
  private final List<OperationSet> operationSets = Lists.newArrayList(
      new OperationSet(e -> new DeleteOperation(), this::canAcceptDeleteOperation),
      new OperationSet(this::makeInsert, this::canAcceptInsertOperation),
      new OperationSet(this::makeReplace, this::canAcceptReplaceOperation)
  );

  /**
   * コンストラクタ
   *
   * @param mutationGeneratingCount 各世代で生成する Variant の数
   * @param random 乱数生成器
   * @param candidateSelection 再利用候補の行を選択するクラス
   * @param type 再利用するスコープのタイプ
   */
  public HeuristicMutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection, final Type type,
      final boolean needHistoricalElement) {
    super(mutationGeneratingCount, random, candidateSelection, needHistoricalElement);
    this.type = type;
  }

  @Override
  protected Operation makeOperation(final ASTLocation location) {
    if (!(location instanceof JDTASTLocation)) {
      throw new IllegalArgumentException("location must be JDTASTLocation");
    }

    final JDTASTLocation jdtastLocation = (JDTASTLocation) location;
    final List<OperationSet> operationSets = this.operationSets.stream()
        .filter(e -> e.accept(jdtastLocation))
        .collect(Collectors.toList());

    final int randomNumber = random.nextInt(operationSets.size());
    return operationSets.get(randomNumber)
        .generate(jdtastLocation);
  }

  private boolean canAcceptDeleteOperation(final JDTASTLocation location) {
    final ASTNode node = location.getNode();
    if (!(node instanceof Statement)) {
      return false;
    }
    final Statement statement = (Statement) node;
    final ASTNode parent = statement.getParent();
    return !isEndStatement(statement)
        && !(statement instanceof VariableDeclarationStatement)
        && !(statement instanceof Block)
        && !(parent instanceof TryStatement)
        && !((parent instanceof IfStatement) && ((IfStatement) parent).getThenStatement()
        .equals(statement))
        && !(parent instanceof ForStatement)
        && !(parent instanceof EnhancedForStatement)
        && !(parent instanceof WhileStatement);
  }

  private Operation makeInsert(final ASTLocation location) {
    final JDTASTLocation jdtastLocation = (JDTASTLocation) location;
    final ASTNode node = jdtastLocation.getNode();
    final Statement statement = (Statement) node;

    if (!canReachAfter(statement)
        || random.nextBoolean()) {
      final ASTNode nodeForReuse = chooseNodeForReuse(location, InsertBeforeOperation.class);
      return new InsertBeforeOperation(nodeForReuse);
    }

    final ASTNode nodeForReuse = chooseNodeForReuse(location, InsertAfterOperation.class);
    return new InsertAfterOperation(nodeForReuse);
  }

  private boolean canAcceptInsertOperation(final JDTASTLocation location) {
    final ASTNode node = location.getNode();
    return node.getParent() instanceof Block;
  }

  private Operation makeReplace(final JDTASTLocation location) {
    return new ReplaceOperation(chooseNodeForReuse(location, ReplaceOperation.class));
  }

  private boolean canAcceptReplaceOperation(final JDTASTLocation location) {
    final ASTNode node = location.getNode();
    if (!(node instanceof Statement)) {
      return false;
    }
    final Statement statement = (Statement) node;
    return !(statement instanceof VariableDeclarationStatement);
  }

  // 引数の文の下にreturnを置けるか
  @SuppressWarnings("uncked")
  private boolean canReachAfter(final Statement statement) {
    if (statement instanceof IfStatement) {
      final IfStatement ifStatement = (IfStatement) statement;
      final Statement thenStatement = ifStatement.getThenStatement();
      if (canReachAfter(thenStatement)) {
        return true;
      }
      final Statement elseStatement = ifStatement.getElseStatement();
      return canReachAfter(elseStatement);
    } else if (statement instanceof SwitchStatement) {
      final SwitchStatement switchStatement = (SwitchStatement) statement;
      final List<Object> statements = switchStatement.statements();
      final boolean noDefault = statements.stream()
          .filter(e -> e instanceof SwitchCase)
          .noneMatch(e -> ((SwitchCase) e).isDefault());
      if (noDefault) {
        return true;
      }
      for (int i = 0; i < statements.size(); i++) {
        if (statements.get(i) instanceof BreakStatement) {
          return true;
        }
      }
      final Object lastStatement = statements.get(statements.size() - 1);
      if (lastStatement instanceof Statement) {
        return canReachAfter(((Statement) lastStatement));
      }
      throw new RuntimeException(lastStatement.getClass() + " is not supported.");
    } else if (statement instanceof TryStatement) {
      final TryStatement tryStatement = (TryStatement) statement;
      final boolean tryStatementResult = canReachAfter(tryStatement);
      final boolean catchResult = tryStatement.catchClauses()
          .stream()
          .anyMatch(e -> canReachAfter(((Statement) e)));
      if (!tryStatementResult && !catchResult) {
        return false;
      }
      return canReachAfter(tryStatement.getFinally());
    } else if (statement instanceof Block) {
      final List statements = ((Block) statement).statements();
      if (statements.isEmpty()) {
        return true;
      }
      return canReachAfter(((Statement) statements.get(statements.size() - 1)));
    } else {
      return !(statement instanceof ReturnStatement) && !(statement instanceof ThrowStatement);
    }
  }

  private boolean isEndStatement(final Statement statement) {
    if (isVoidMethod(statement)) {
      return false;
    }

    ASTNode node = statement;
    while (!(node instanceof MethodDeclaration) && !(node instanceof LambdaExpression)) {
      final ASTNode parent = node.getParent();
      if (parent instanceof Block) {
        final Block block = (Block) parent;
        final List statements = block.statements();
        final Object lastObject = statements.get(statements.size() - 1);
        if (!lastObject.equals(node)) {
          return false;
        }
      }
      node = parent;
    }
    return true;
  }

  private boolean isVoidMethod(final ASTNode node) {
    final FullyQualifiedName returnType = getReturnType(node);
    if (returnType == null) {
      return false;
    }
    return returnType
        .toString()
        .toLowerCase()
        .equals("void");
  }

  private FullyQualifiedName getReturnType(final ASTNode node) {
    ASTNode n = node;
    while (!(n instanceof MethodDeclaration) && !(n instanceof LambdaExpression)) {
      n = n.getParent();
    }
    if (n instanceof LambdaExpression) {
      return null;
    }
    final MethodDeclaration methodDeclaration = (MethodDeclaration) n;
    if (methodDeclaration.isConstructor()) {
      return null;
    }
    final String type = methodDeclaration.getReturnType2()
        .toString();
    return new TargetFullyQualifiedName(type);

  }

  /**
   * 再利用候補の ASTNode を返すメソッド
   * CandidateSelection で選択したステートメントその場で利用できる変数名に書き換えて返す
   *
   * @param location 再利用先
   * @return 再利用されるステートメント
   */
  protected ASTNode chooseNodeForReuse(final ASTLocation location,
      final Class<? extends Operation> operationClass) {
    final JDTASTLocation jdtastLocation = (JDTASTLocation) location;
    final FullyQualifiedName fqn = location.getGeneratedAST()
        .getPrimaryClassName();
    final Scope scope = new Scope(type, fqn);
    final List<Variable> variables = variableSearcher.exec(location);
    final Statement statement = (Statement) jdtastLocation.getNode();

    final Query query = new Query(variables, scope,
        canNormal(jdtastLocation, operationClass), // canNormal
        canBreak(jdtastLocation), // canBreak
        canReturn(jdtastLocation, operationClass), // canReturn
        getReturnType(statement),
        canContinue(jdtastLocation)); // canContinue
    final ASTNode selectedNode = candidateSelection.exec(query);
    return rewrite(selectedNode, variables);
  }

  private boolean canNormal(final JDTASTLocation location,
      final Class<? extends Operation> operationClass) {
    final Statement statement = (Statement) location.getNode();
    if (operationClass.equals(InsertBeforeOperation.class)) {
      return true;
    } else if (operationClass.equals(InsertAfterOperation.class)) {
      return canReachAfter(statement);
    } else if (operationClass.equals(ReplaceOperation.class)) {
      return isVoidMethod(statement) || !isEndStatement(statement);
    }
    throw new RuntimeException();
  }

  private boolean canReturn(final JDTASTLocation location,
      final Class<? extends Operation> operationClass) {
    final Statement statement = (Statement) location.getNode();
    if (operationClass.equals(InsertBeforeOperation.class)) {
      return false;
    } else if (operationClass.equals(InsertAfterOperation.class)) {
      final ASTNode parent = statement.getParent();
      if (!(parent instanceof Block)) {
        return false;
      }
      final List statements = ((Block) parent).statements();
      if (statements.isEmpty()) {
        return false;
      }
      return canReachAfter(statement) && statement.equals(statements.get(statements.size() - 1));
    } else if (operationClass.equals(ReplaceOperation.class)) {
      final ASTNode parent = statement.getParent();
      if (!(parent instanceof Block)) {
        return true;
      }
      final List statements = ((Block) parent).statements();
      return statements.get(statements.size() - 1)
          .equals(statement);
    }
    throw new RuntimeException();
  }

  private boolean canBreak(final JDTASTLocation location) {
    if (!isInLoopOrSWitch(location)) {
      return false;
    }
    return isLastStatement(location);
  }

  private boolean isInLoopOrSWitch(final JDTASTLocation location) {
    final ASTNode node = location.getNode();
    ASTNode parent = node.getParent();
    while (!(parent instanceof MethodDeclaration) && !(parent instanceof LambdaExpression)) {
      if (parent instanceof SwitchStatement
          || parent instanceof ForStatement
          || parent instanceof EnhancedForStatement
          || parent instanceof WhileStatement) {
        return true;
      }
      parent = parent.getParent();
    }
    return false;
  }

  private boolean canContinue(final JDTASTLocation location) {
    if (!isInLoop(location)) {
      return false;
    }
    return isLastStatement(location);
  }

  private boolean isLastStatement(final JDTASTLocation location) {
    final ASTNode node = location.getNode();
    final ASTNode parent = node.getParent();

    if (!(parent instanceof Block)) {
      return false;
    }
    final Block block = (Block) parent;
    final List statements = block.statements();
    final Object last = statements.get(statements.size() - 1);
    return last.equals(node);
  }

  private boolean isInLoop(final JDTASTLocation location) {
    final ASTNode node = location.getNode();
    ASTNode parent = node.getParent();
    while (!(parent instanceof MethodDeclaration) && !(parent instanceof LambdaExpression)) {
      if (parent instanceof ForStatement
          || parent instanceof EnhancedForStatement
          || parent instanceof WhileStatement) {
        return true;
      }
      parent = parent.getParent();
    }
    return false;
  }

  private ASTNode rewrite(final ASTNode selectedNode, final List<Variable> queryVariables) {
    // 「再利用先で使える型」を「再利用先で使える変数名のリスト」に変換するマップ
    final Map<FullyQualifiedName, List<Variable>> fqnToNamesMap = queryVariables.stream()
        .collect(Collectors.groupingBy(Variable::getFqn));

    final List<Variable> variablesOfSelectedNode = variableSearcher.exec(selectedNode);
    // 「再利用するノード内にある変数名」を「型」に変換するマップ
    final Map<String, FullyQualifiedName> nameToFqnMap = variablesOfSelectedNode.stream()
        .collect(Collectors.toMap(Variable::getName, Variable::getFqn, (o1, o2) -> o1));

    final RewriteVisitor rewriteVisitor = new RewriteVisitor(selectedNode, nameToFqnMap,
        fqnToNamesMap, random);
    return rewriteVisitor.getRewritedNode();
  }

  // ================================ inner class ================================

  private class RewriteVisitor extends ASTVisitor {

    private final ASTNode targetNode;
    private final Map<String, FullyQualifiedName> nameToFqnMap;
    private final Map<FullyQualifiedName, List<Variable>> fqnToNamesMap;
    private final Random random;

    RewriteVisitor(final ASTNode node, final Map<String, FullyQualifiedName> nameToFqnMap,
        final Map<FullyQualifiedName, List<Variable>> fqnToNamesMap, final Random random) {
      // コピーした ASTNode を書き換えていく
      this.targetNode = ASTNode.copySubtree(node.getAST(), node);
      this.nameToFqnMap = nameToFqnMap;
      this.fqnToNamesMap = fqnToNamesMap;
      this.random = random;

      targetNode.accept(this);
    }

    @Override
    public boolean visit(final SimpleName node) {
      // 宣言部分(左辺)は無視する
      if (node.isDeclaration()) {
        return true;
      }

      // 書き換え対象の変数名を型に変換する
      final FullyQualifiedName fqn = nameToFqnMap.get(node.toString());
      if (fqn == null) {
        return true;
      }

      // 再利用先でアクセスできる変数名の一覧に変換
      List<Variable> variables = fqnToNamesMap.get(fqn);
      if (variables == null) {
        return true;
      }

      if (isLeftHandSideInAssignment(node)) {
        // 代入文の左辺なので final 修飾子がついてない変数をフィルターする
        variables = variables.stream()
            .filter(e -> !e.isFinal())
            .collect(Collectors.toList());
      }

      if (!variables.isEmpty()) {
        // ランダムに変数名を書き換える
        final Variable newName = variables.get(random.nextInt(variables.size()));
        node.setIdentifier(newName.getName());
      }

      return true;
    }

    private boolean isLeftHandSideInAssignment(final ASTNode node) {
      final ASTNode parent = node.getParent();
      return parent instanceof Assignment
          && ((Assignment) parent).getLeftHandSide()
          .equals(node);
    }

    public ASTNode getRewritedNode() {
      return targetNode;
    }
  }

  private class OperationSet {
    private final Function<JDTASTLocation, Operation> generator;
    private final Function<JDTASTLocation, Boolean> judge;

    public OperationSet(final Function<JDTASTLocation, Operation> generator,
        final Function<JDTASTLocation, Boolean> judge) {
      this.generator = generator;
      this.judge = judge;
    }

    public Operation generate(final JDTASTLocation location) {
      return generator.apply(location);
    }

    public boolean accept(final JDTASTLocation location) {
      return judge.apply(location);
    }
  }
}
