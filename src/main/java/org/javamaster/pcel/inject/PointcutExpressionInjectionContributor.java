package org.javamaster.pcel.inject;

import org.javamaster.pcel.PointcutExpressionLanguage;
import com.google.common.collect.Sets;
import com.intellij.lang.injection.general.Injection;
import com.intellij.lang.injection.general.LanguageInjectionContributor;
import com.intellij.lang.injection.general.SimpleInjection;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * 将PointcutExpression语言注入到Spring AOP的相关切面注解中(例如@Around注解)
 */
public class PointcutExpressionInjectionContributor implements LanguageInjectionContributor {
    public static final Set<String> INTEREST_ANNO_SET = Sets.newHashSet(
            "org.aspectj.lang.annotation.Around",
            "org.aspectj.lang.annotation.Pointcut",
            "org.aspectj.lang.annotation.AfterReturning",
            "org.aspectj.lang.annotation.AfterThrowing"
    );
    public static final String ASPECT_CLASS_NAME = "org.aspectj.lang.annotation.Aspect";
    public static final String ASPECT_SHORT_CLASS_NAME = "Aspect";


    @Override
    public @Nullable Injection getInjection(@NotNull PsiElement context) {
        if (shouldInject(context)) {
            return new SimpleInjection(PointcutExpressionLanguage.INSTANCE, "", "", null);
        }

        return null;
    }

    private boolean shouldInject(PsiElement context) {
        if (!(context instanceof PsiLiteralExpression)) {
            return false;
        }

        PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) context;
        PsiAnnotation psiAnnotation = PsiTreeUtil.getParentOfType(psiLiteralExpression, PsiAnnotation.class);
        if (psiAnnotation == null) {
            return false;
        }

        /// 判断是否是Spring AOP的注解
        boolean contains = INTEREST_ANNO_SET.contains(psiAnnotation.getQualifiedName());
        if (!contains) {
            return false;
        }

        /// 判断注解的属性名是否是 value 或者 pointcut
        PsiNameValuePair psiNameValuePair = (PsiNameValuePair) psiLiteralExpression.getParent();
        if (!"value".equals(psiNameValuePair.getAttributeName()) && !"pointcut".equals(psiNameValuePair.getAttributeName())) {
            return false;
        }

        PsiClass psiClass = PsiUtil.getTopLevelClass(psiAnnotation);
        if (psiClass == null) {
            return false;
        }

        // 判断类是否带有 @Aspect 注解
        PsiAnnotation aspectAnnotation = psiClass.getAnnotation(ASPECT_CLASS_NAME);
        return aspectAnnotation != null;
    }

}
