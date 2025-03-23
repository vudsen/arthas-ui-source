// This is a generated file. Not intended for manual editing.
package io.github.vudsen.arthasui.language.arthas.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.github.vudsen.arthasui.language.arthas.psi.ArthasTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.github.vudsen.arthasui.language.arthas.psi.*;

public class ArthasArgumentImpl extends ASTWrapperPsiElement implements ArthasArgument {

  public ArthasArgumentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ArthasVisitor visitor) {
    visitor.visitArgument(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ArthasVisitor) accept((ArthasVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getArgumentHead() {
    return findNotNullChildByType(ARGUMENT_HEAD);
  }

  @Override
  @Nullable
  public PsiElement getNonWhitespaceSequence() {
    return findChildByType(NON_WHITESPACE_SEQUENCE);
  }

}
