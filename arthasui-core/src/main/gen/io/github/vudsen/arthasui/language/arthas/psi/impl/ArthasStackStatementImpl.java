// This is a generated file. Not intended for manual editing.
package io.github.vudsen.arthasui.language.arthas.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.github.vudsen.arthasui.language.arthas.psi.ArthasTypes.*;
import io.github.vudsen.arthasui.language.arthas.psi.*;

public class ArthasStackStatementImpl extends ArthasCommandImpl implements ArthasStackStatement {

  public ArthasStackStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull ArthasVisitor visitor) {
    visitor.visitStackStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ArthasVisitor) accept((ArthasVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ArthasArgument> getArgumentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ArthasArgument.class);
  }

  @Override
  @NotNull
  public ArthasClazz getClazz() {
    return findNotNullChildByClass(ArthasClazz.class);
  }

  @Override
  @NotNull
  public ArthasMethod getMethod() {
    return findNotNullChildByClass(ArthasMethod.class);
  }

  @Override
  @NotNull
  public ArthasOgnl getOgnl() {
    return findNotNullChildByClass(ArthasOgnl.class);
  }

}
