package org.javamaster.aop;

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.javamaster.pcel.psi.AopContent;
import org.javamaster.pcel.psi.AopPointcut;
import org.javamaster.pcel.psi.AopValue;
import org.javamaster.utils.AopUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author yudong
 */
public interface AopMatcher {

    static @Nullable AopMatcher getMatcher(AopPointcut aopPointcut) {
        AopContent aopContent = aopPointcut.getContent();
        AopValue aopValue = aopContent.getValue();

        if (AopUtils.isInAtAnnotation(aopValue)) {
            return new AtAnnotationAopMatcher(aopValue);
        }

        return null;
    }

    /**
     * 判断类的方法是否匹配切面
     */
    boolean methodMatcher(PsiClass psiClass, PsiMethod psiMethod);

    /**
     * 收集切面匹配的所有方法
     */
    Set<PsiMethod> collectMatchMethods(Module projectModule);
}
