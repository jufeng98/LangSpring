package org.javamaster.spel.psi.impl;

import org.javamaster.spel.psi.SpelFieldName;
import org.javamaster.spel.psi.SpelFieldOrMethodName;
import org.javamaster.spel.psi.SpelMethodParam;
import org.javamaster.spel.psi.SpelRoot;
import org.javamaster.spel.psi.SpelSpel;
import org.javamaster.spel.psi.SpelStaticT;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;

public class SpelPsiImplUtil {
    public static PsiReference @NotNull [] getReferences(SpelFieldName param) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(param);
    }

    public static PsiReference @NotNull [] getReferences(SpelMethodParam param) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(param);
    }
    public static PsiReference @NotNull [] getReferences(SpelStaticT param) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(param);
    }

    public static PsiReference @NotNull [] getReferences(SpelFieldOrMethodName param) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(param);
    }

    public static PsiReference @NotNull [] getReferences(SpelSpel param) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(param);
    }

    public static PsiReference @NotNull [] getReferences(SpelRoot param) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(param);
    }
}
