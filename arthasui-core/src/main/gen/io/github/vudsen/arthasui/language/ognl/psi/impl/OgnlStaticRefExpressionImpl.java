// This is a generated file. Not intended for manual editing.
package io.github.vudsen.arthasui.language.ognl.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.github.vudsen.arthasui.language.ognl.psi.OgnlTypes.*;
import io.github.vudsen.arthasui.language.ognl.psi.*;

public class OgnlStaticRefExpressionImpl extends OgnlExpressionImpl implements OgnlStaticRefExpression {

  public OgnlStaticRefExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull OgnlVisitor visitor) {
    visitor.visitStaticRefExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof OgnlVisitor) accept((OgnlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public OgnlClazz getClazz() {
    return findChildByClass(OgnlClazz.class);
  }

  @Override
  @Nullable
  public OgnlIdentifierExpression getIdentifierExpression() {
    return findChildByClass(OgnlIdentifierExpression.class);
  }

}
