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

public class SpelArrayWrapImpl extends SpelPsiElement implements SpelArrayWrap {

  public SpelArrayWrapImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SpelVisitor visitor) {
    visitor.visitArrayWrap(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SpelVisitor) accept((SpelVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SpelRoot> getRootList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SpelRoot.class);
  }

}
