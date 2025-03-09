// This is a generated file. Not intended for manual editing.
package io.github.vudsen.arthasui.language.ognl.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class OgnlVisitor extends PsiElementVisitor {

  public void visitClassName(@NotNull OgnlClassName o) {
    visitPsiElement(o);
  }

  public void visitClazz(@NotNull OgnlClazz o) {
    visitPsiElement(o);
  }

  public void visitClazzPackage(@NotNull OgnlClazzPackage o) {
    visitPsiElement(o);
  }

  public void visitDynamicExpressionList(@NotNull OgnlDynamicExpressionList o) {
    visitPsiElement(o);
  }

  public void visitExpression(@NotNull OgnlExpression o) {
    visitPsiElement(o);
  }

  public void visitExpressionList(@NotNull OgnlExpressionList o) {
    visitPsiElement(o);
  }

  public void visitIdentifierExpression(@NotNull OgnlIdentifierExpression o) {
    visitExpression(o);
  }

  public void visitLiteralExpression(@NotNull OgnlLiteralExpression o) {
    visitExpression(o);
  }

  public void visitMethodCallExpression(@NotNull OgnlMethodCallExpression o) {
    visitExpression(o);
  }

  public void visitNewExpression(@NotNull OgnlNewExpression o) {
    visitExpression(o);
  }

  public void visitReferenceExpression(@NotNull OgnlReferenceExpression o) {
    visitExpression(o);
  }

  public void visitStaticRefExpression(@NotNull OgnlStaticRefExpression o) {
    visitExpression(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
