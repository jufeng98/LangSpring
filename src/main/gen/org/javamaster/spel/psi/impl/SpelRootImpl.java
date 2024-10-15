// This is a generated file. Not intended for manual editing.
package org.javamaster.spel.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.javamaster.spel.psi.SpelTypes.*;
import org.javamaster.spel.SpelPsiElement;
import org.javamaster.spel.psi.*;
import com.intellij.psi.PsiReference;

public class SpelRootImpl extends SpelPsiElement implements SpelRoot {

  public SpelRootImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SpelVisitor visitor) {
    visitor.visitRoot(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SpelVisitor) accept((SpelVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SpelArrayWrap getArrayWrap() {
    return findChildByClass(SpelArrayWrap.class);
  }

  @Override
  @NotNull
  public List<SpelCollectionProjection> getCollectionProjectionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SpelCollectionProjection.class);
  }

  @Override
  @NotNull
  public List<SpelCollectionSelection> getCollectionSelectionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SpelCollectionSelection.class);
  }

  @Override
  @NotNull
  public List<SpelFieldOrMethod> getFieldOrMethodList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SpelFieldOrMethod.class);
  }

  @Override
  @Nullable
  public SpelFieldOrMethodName getFieldOrMethodName() {
    return findChildByClass(SpelFieldOrMethodName.class);
  }

  @Override
  @NotNull
  public List<SpelMapSelection> getMapSelectionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SpelMapSelection.class);
  }

  @Override
  @NotNull
  public List<SpelMethodCall> getMethodCallList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SpelMethodCall.class);
  }

  @Override
  @Nullable
  public SpelNumberLiteral getNumberLiteral() {
    return findChildByClass(SpelNumberLiteral.class);
  }

  @Override
  @Nullable
  public SpelStaticT getStaticT() {
    return findChildByClass(SpelStaticT.class);
  }

  @Override
  @Nullable
  public SpelStringLiteral getStringLiteral() {
    return findChildByClass(SpelStringLiteral.class);
  }

  @Override
  public PsiReference @NotNull [] getReferences() {
    return SpelPsiImplUtil.getReferences(this);
  }

}
