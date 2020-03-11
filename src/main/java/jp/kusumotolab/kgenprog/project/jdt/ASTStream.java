package jp.kusumotolab.kgenprog.project.jdt;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExportsDirective;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.IntersectionType;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.ModuleDeclaration;
import org.eclipse.jdt.core.dom.ModuleModifier;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.OpensDirective;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ProvidesDirective;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.RequiresDirective;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeMethodReference;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.UsesDirective;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;

public class ASTStream extends ASTVisitor implements Iterator<ASTNode> {

  public static Iterator<ASTNode> iterator(final ASTNode node) {
    return new ASTStream(node);
  }

  public static Stream<ASTNode> stream(final ASTNode node) {
    final Spliterator<ASTNode> spliterator =
        Spliterators.spliteratorUnknownSize(iterator(node), Spliterator.NONNULL);
    return StreamSupport.stream(spliterator, false);
  }

  private final Deque<ASTNode> globalStack;
  private final Deque<ASTNode> stepStack;
  private boolean alreadyConsumed;

  public ASTStream(final ASTNode node) {
    globalStack = new ArrayDeque<>();
    stepStack = new ArrayDeque<>();

    globalStack.push(node);
  }

  @Override
  public boolean hasNext() {
    return !globalStack.isEmpty();
  }

  @Override
  public ASTNode next() {
    final ASTNode node = globalStack.pop();

    alreadyConsumed = false;
    node.accept(this);

    while (!stepStack.isEmpty()) {
      globalStack.push(stepStack.pop());
    }

    return node;
  }

  private boolean consume(final ASTNode node) {
    if (alreadyConsumed) {
      stepStack.push(node);
      return false;
    }
    alreadyConsumed = true;
    return true;
  }

  @Override
  public boolean visit(final AnnotationTypeDeclaration node) {
    return consume(node);
  }

  @Override
  public boolean visit(final AnnotationTypeMemberDeclaration node) {
    return consume(node);
  }

  @Override
  public boolean visit(final AnonymousClassDeclaration node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ArrayAccess node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ArrayCreation node) {

    return consume(node);
  }

  @Override
  public boolean visit(final ArrayInitializer node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ArrayType node) {
    return consume(node);
  }

  @Override
  public boolean visit(final AssertStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final Assignment node) {
    return consume(node);
  }

  @Override
  public boolean visit(final Block node) {
    return consume(node);
  }

  @Override
  public boolean visit(final BlockComment node) {
    return consume(node);
  }

  @Override
  public boolean visit(final BooleanLiteral node) {
    return consume(node);
  }

  @Override
  public boolean visit(final BreakStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final CastExpression node) {
    return consume(node);
  }

  @Override
  public boolean visit(final CatchClause node) {
    return consume(node);
  }

  @Override
  public boolean visit(final CharacterLiteral node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ClassInstanceCreation node) {
    return consume(node);
  }

  @Override
  public boolean visit(final CompilationUnit node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ConditionalExpression node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ConstructorInvocation node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ContinueStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final CreationReference node) {
    return consume(node);
  }

  @Override
  public boolean visit(final Dimension node) {
    return consume(node);
  }

  @Override
  public boolean visit(final DoStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final EmptyStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final EnhancedForStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final EnumConstantDeclaration node) {
    return consume(node);
  }

  @Override
  public boolean visit(final EnumDeclaration node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ExportsDirective node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ExpressionMethodReference node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ExpressionStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final FieldAccess node) {
    return consume(node);
  }

  @Override
  public boolean visit(final FieldDeclaration node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ForStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final IfStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ImportDeclaration node) {
    return consume(node);
  }

  @Override
  public boolean visit(final InfixExpression node) {
    return consume(node);
  }

  @Override
  public boolean visit(final Initializer node) {
    return consume(node);
  }

  @Override
  public boolean visit(final InstanceofExpression node) {
    return consume(node);
  }

  @Override
  public boolean visit(final IntersectionType node) {
    return consume(node);
  }

  @Override
  public boolean visit(final Javadoc node) {
    return consume(node);
  }

  @Override
  public boolean visit(final LabeledStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final LambdaExpression node) {
    return consume(node);
  }

  @Override
  public boolean visit(final LineComment node) {
    return consume(node);
  }

  @Override
  public boolean visit(final MarkerAnnotation node) {
    return consume(node);
  }

  @Override
  public boolean visit(final MemberRef node) {
    return consume(node);
  }

  @Override
  public boolean visit(final MemberValuePair node) {
    return consume(node);
  }

  @Override
  public boolean visit(final MethodRef node) {
    return consume(node);
  }

  @Override
  public boolean visit(final MethodRefParameter node) {
    return consume(node);
  }

  @Override
  public boolean visit(final MethodDeclaration node) {
    return consume(node);
  }

  @Override
  public boolean visit(final MethodInvocation node) {
    return consume(node);
  }

  @Override
  public boolean visit(final Modifier node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ModuleDeclaration node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ModuleModifier node) {
    return consume(node);
  }

  @Override
  public boolean visit(final NameQualifiedType node) {
    return consume(node);
  }

  @Override
  public boolean visit(final NormalAnnotation node) {
    return consume(node);
  }

  @Override
  public boolean visit(final NullLiteral node) {
    return consume(node);
  }

  @Override
  public boolean visit(final NumberLiteral node) {
    return consume(node);
  }

  @Override
  public boolean visit(final OpensDirective node) {
    return consume(node);
  }

  @Override
  public boolean visit(final PackageDeclaration node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ParameterizedType node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ParenthesizedExpression node) {
    return consume(node);
  }

  @Override
  public boolean visit(final PostfixExpression node) {
    return consume(node);
  }

  @Override
  public boolean visit(final PrefixExpression node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ProvidesDirective node) {
    return consume(node);
  }

  @Override
  public boolean visit(final PrimitiveType node) {
    return consume(node);
  }

  @Override
  public boolean visit(final QualifiedName node) {
    return consume(node);
  }

  @Override
  public boolean visit(final QualifiedType node) {
    return consume(node);
  }

  @Override
  public boolean visit(final RequiresDirective node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ReturnStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final SimpleName node) {
    return consume(node);
  }

  @Override
  public boolean visit(final SimpleType node) {
    return consume(node);
  }

  @Override
  public boolean visit(final SingleMemberAnnotation node) {
    return consume(node);
  }

  @Override
  public boolean visit(final SingleVariableDeclaration node) {
    return consume(node);
  }

  @Override
  public boolean visit(final StringLiteral node) {
    return consume(node);
  }

  @Override
  public boolean visit(final SuperConstructorInvocation node) {
    return consume(node);
  }

  @Override
  public boolean visit(final SuperFieldAccess node) {
    return consume(node);
  }

  @Override
  public boolean visit(final SuperMethodInvocation node) {
    return consume(node);
  }

  @Override
  public boolean visit(final SuperMethodReference node) {
    return consume(node);
  }

  @Override
  public boolean visit(final SwitchCase node) {
    return consume(node);
  }

  @Override
  public boolean visit(final SwitchStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final SynchronizedStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final TagElement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final TextElement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ThisExpression node) {
    return consume(node);
  }

  @Override
  public boolean visit(final ThrowStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final TryStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final TypeDeclaration node) {
    return consume(node);
  }

  @Override
  public boolean visit(final TypeDeclarationStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final TypeLiteral node) {
    return consume(node);
  }

  @Override
  public boolean visit(final TypeMethodReference node) {
    return consume(node);
  }

  @Override
  public boolean visit(final TypeParameter node) {
    return consume(node);
  }

  @Override
  public boolean visit(final UnionType node) {
    return consume(node);
  }

  @Override
  public boolean visit(final UsesDirective node) {
    return consume(node);
  }

  @Override
  public boolean visit(final VariableDeclarationExpression node) {
    return consume(node);
  }

  @Override
  public boolean visit(final VariableDeclarationStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final VariableDeclarationFragment node) {
    return consume(node);
  }

  @Override
  public boolean visit(final WhileStatement node) {
    return consume(node);
  }

  @Override
  public boolean visit(final WildcardType node) {
    return consume(node);
  }

}
