package jp.kusumotolab.kgenprog.ga;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jp.kusumotolab.kgenprog.fl.Suspiciouseness;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;
import jp.kusumotolab.kgenprog.project.jdt.InsertOperation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;
import org.eclipse.jdt.core.dom.*;

public class RandomMutation implements Mutation {

    private List<GeneratedAST> candidates = new ArrayList<>();
    private final RandomNumberGeneration randomNumberGeneration;

    public RandomMutation() {
        this.randomNumberGeneration = new RandomNumberGeneration();
    }

    public RandomMutation(RandomNumberGeneration randomNumberGeneration) {
        this.randomNumberGeneration = randomNumberGeneration;
    }

    @Override
    public void setCandidates(List<GeneratedAST> candidates) {
        this.candidates = candidates;
    }

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
        final int randomNumber = randomNumberGeneration.getRandomNumber(3);
        switch (randomNumber) {
            case 0:
                return new DeleteOperation();
            case 1:
                return new InsertOperation(chooseNodeAtRandom());
            case 2:
                return new ReplaceOperation(chooseNodeAtRandom());
        }
        return new NoneOperation();
    }

    private Statement chooseNodeAtRandom() {
        final int randomNumber = randomNumberGeneration.getRandomNumber(candidates.size());
        final GeneratedAST generatedAST = candidates.get(randomNumber);
        final CompilationUnit root = ((GeneratedJDTAST) generatedAST).getRoot();
        final List types = root.types();
        final Object type = types.get(randomNumberGeneration.getRandomNumber(types.size()));
        if(type instanceof TypeDeclaration) {
            final TypeDeclaration typeDeclaration = ((TypeDeclaration) type);
            final MethodDeclaration[] methods = typeDeclaration.getMethods();
            if (methods.length == 0) {
                return chooseNodeAtRandom();
            }
            final MethodDeclaration method = methods[randomNumberGeneration.getRandomNumber(methods.length)];
            final Statement statement = excludeStatementFromBlock(method.getBody());
            if (statement == null) {
                return chooseNodeAtRandom();
            }
            return statement;
        }
        return chooseNodeAtRandom();
    }

    private Statement excludeStatement(Statement statement) {
        if (statement instanceof Block) {
            return excludeStatementFromBlock(((Block) statement));
        } else if (statement instanceof IfStatement) {
            return excludeStatementFromIfStatement(((IfStatement) statement));
        } else if (statement instanceof WhileStatement) {
            return excludeStatementFromWhile(((WhileStatement) statement));
        } else if (statement instanceof SwitchStatement) {
            return excludeStatementFromSwitch(((SwitchStatement) statement));
        } else if (statement instanceof DoStatement) {
            return excludeStatementFromDo(((DoStatement) statement));
        } else if (statement instanceof TryStatement) {
            return excludeStatementFromTry(((TryStatement) statement));
        }
        return statement;
    }

    private Statement excludeStatementFromStatements(List<Statement> statements) {
        if (statements.isEmpty()) {
            return null;
        }
        final Statement statement = statements.get(randomNumberGeneration.getRandomNumber(statements.size()));
        return excludeStatement(statement);
    }

    private Statement excludeStatementFromBlock(Block block) {
        return excludeStatementFromStatements(block.statements());
    }

    private Statement excludeStatementFromIfStatement(IfStatement ifStatement) {
        if (randomNumberGeneration.getRandomBoolean()) {
            return excludeStatement(ifStatement.getThenStatement());
        } else if (randomNumberGeneration.getRandomBoolean()) {
            return excludeStatement(ifStatement.getElseStatement());
        } else if (randomNumberGeneration.getRandomBoolean()) {
            return ifStatement.getElseStatement();
        } else if (randomNumberGeneration.getRandomBoolean()) {
            return ifStatement.getThenStatement();
        }
        return ifStatement;
    }

    private Statement excludeStatementFromWhile(WhileStatement whileStatement) {
        if (randomNumberGeneration.getRandomBoolean()) {
            return excludeStatement(whileStatement.getBody());
        }
        return whileStatement;
    }

    private Statement excludeStatementFromSwitch(SwitchStatement switchStatement) {
        if (randomNumberGeneration.getRandomBoolean()) {
            return excludeStatementFromStatements(switchStatement.statements());
        }
        return switchStatement;
    }

    private Statement excludeStatementFromDo(DoStatement doStatement) {
        return excludeStatement(doStatement.getBody());
    }

    private Statement excludeStatementFromTry(TryStatement tryStatement) {
        if (randomNumberGeneration.getRandomBoolean()) {
            return excludeStatementFromBlock(tryStatement.getBody());
        } else if (randomNumberGeneration.getRandomBoolean()) {
            return excludeStatementFromBlock(tryStatement.getFinally());
        }
        return tryStatement;
    }
}
