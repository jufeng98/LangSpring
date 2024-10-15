// This is a generated file. Not intended for manual editing.
package org.javamaster.spel.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public interface SpelRoot extends PsiElement {

  @Nullable
  SpelArrayWrap getArrayWrap();

  @NotNull
  List<SpelCollectionProjection> getCollectionProjectionList();

  @NotNull
  List<SpelCollectionSelection> getCollectionSelectionList();

  @NotNull
  List<SpelFieldOrMethod> getFieldOrMethodList();

  @Nullable
  SpelFieldOrMethodName getFieldOrMethodName();

  @NotNull
  List<SpelMapSelection> getMapSelectionList();

  @NotNull
  List<SpelMethodCall> getMethodCallList();

  @Nullable
  SpelNumberLiteral getNumberLiteral();

  @Nullable
  SpelStaticT getStaticT();

  @Nullable
  SpelStringLiteral getStringLiteral();

  PsiReference @NotNull [] getReferences();

}
