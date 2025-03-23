// This is a generated file. Not intended for manual editing.
package io.github.vudsen.arthasui.language.arthas.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ArthasGetstaticCommand extends ArthasCommand {

  @NotNull
  List<ArthasArgument> getArgumentList();

  @Nullable
  PsiElement getOgnl();

  @NotNull
  PsiElement getClazz();

  @NotNull
  PsiElement getMethod();

}
