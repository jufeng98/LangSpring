// This is a generated file. Not intended for manual editing.
package org.javamaster.pcel.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.javamaster.pcel.psi.PointcutExpressionTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.javamaster.pcel.psi.*;
import com.intellij.psi.PsiReference;

public class AopExprImpl extends ASTWrapperPsiElement implements AopExpr {

  public AopExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull AopVisitor visitor) {
    visitor.visitExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof AopVisitor) accept((AopVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getExprPattern() {
    return findNotNullChildByType(EXPR_PATTERN);
  }

  @Override
  public PsiReference @NotNull [] getReferences() {
    return PointcutExpressionPsiImplUtil.getReferences(this);
  }

}
