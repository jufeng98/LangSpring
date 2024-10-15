package org.javamaster.cls.step10;

import org.javamaster.cls.psi.SimpleProperty;
import org.javamaster.cls.step2.SimpleIcons;
import org.javamaster.cls.step6.SimpleUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class SimpleReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private final String key;

    SimpleReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        final List<SimpleProperty> properties = SimpleUtil.findProperties(project, key);
        List<ResolveResult> results = new ArrayList<>();
        for (SimpleProperty property : properties) {
            results.add(new PsiElementResolveResult(property));
        }
        return results.toArray(new ResolveResult[0]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @Override
    public Object @NotNull [] getVariants() {
        Project project = myElement.getProject();
        List<SimpleProperty> properties = SimpleUtil.findProperties(project);
        List<LookupElement> variants = new ArrayList<>();
        for (final SimpleProperty property : properties) {
            if (property.getKey() != null && !property.getKey().isEmpty()) {
                variants.add(LookupElementBuilder
                        .create(property).withIcon(SimpleIcons.FILE)
                        .withTypeText(property.getContainingFile().getName())
                );
            }
        }
        return variants.toArray();
    }

}
