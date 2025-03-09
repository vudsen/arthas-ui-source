// This is a generated file. Not intended for manual editing.
package io.github.vudsen.arthasui.language.arthas.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ArthasWatchStatement extends ArthasCommand {

  @NotNull
  List<ArthasArgument> getArgumentList();

  @Nullable
  ArthasClazz getClazz();

  @Nullable
  ArthasMethod getMethod();

  @Nullable
  ArthasOgnl getOgnl();

}
