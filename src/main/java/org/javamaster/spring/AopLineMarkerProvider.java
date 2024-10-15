package org.javamaster.spring;

import org.javamaster.aop.AopMatcher;
import org.javamaster.pcel.psi.AopPointcut;
import org.javamaster.utils.AopUtils;
import com.intellij.codeInsight.daemon.GutterName;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UMethod;
import org.jetbrains.uast.UastUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.javamaster.pcel.inject.PointcutExpressionInjectionContributor.ASPECT_SHORT_CLASS_NAME;

/**
 * @author yudong
 */
public final class AopLineMarkerProvider extends LineMarkerProviderDescriptor {
    private static final Logger LOG = LoggerFactory.getLogger(AopLineMarkerProvider.class);

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements,
                                       @NotNull Collection<? super LineMarkerInfo<?>> result) {
        if (elements.isEmpty()) {
            return;
        }

        PsiElement first = elements.iterator().next();
        Module module = ModuleUtilCore.findModuleForPsiElement(first);
        if (module == null) {
            return;
        }

        PsiClass currentPsiClass = PsiUtil.getTopLevelClass(first);
        if (currentPsiClass == null) {
            return;
        }

        if (AopUtils.isAspectClass(currentPsiClass)) {
            handleAdvisedClasses(currentPsiClass, result, module);
        } else {
            Set<PsiClass> aspectPsiClasses = collectAdvisedClassesAndCache(module);
            if (aspectPsiClasses.isEmpty()) {
                return;
            }

            for (PsiElement element : elements) {
                ProgressManager.checkCanceled();

                annotateMethod(element, result, aspectPsiClasses);
            }
        }
    }

    /**
     * 收集切面类并缓存起来
     */
    private Set<PsiClass> collectAdvisedClassesAndCache(Module module) {
        return CachedValuesManager.getManager(module.getProject())
                .getCachedValue(module, () -> {
                    long l = System.currentTimeMillis();
                    Set<PsiClass> set = collectAdvisedClasses(module);
                    LOG.info("收集切面类耗时:{}ms,{}", System.currentTimeMillis() - l, set);
                    return CachedValueProvider.Result.create(set, PsiModificationTracker.MODIFICATION_COUNT);
                });
    }

    /**
     * 收集切面类
     */
    private Set<PsiClass> collectAdvisedClasses(Module module) {
        GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
        JavaAnnotationIndex javaAnnotationIndex = JavaAnnotationIndex.getInstance();
        Collection<PsiAnnotation> psiAnnotations = javaAnnotationIndex.get(ASPECT_SHORT_CLASS_NAME, module.getProject(), scope);

        return psiAnnotations.stream()
                .map(it -> PsiTreeUtil.getParentOfType(it, PsiClass.class))
                .collect(Collectors.toSet());
    }

    /**
     * 为切面类方法添加行标记,点击后能显示所有匹配的方法并跳转
     */
    private void handleAdvisedClasses(PsiClass aspectPsiClass, Collection<? super LineMarkerInfo<?>> result, Module module) {
        Set<Pair<PsiMethod, AopPointcut>> pairSet = AopUtils.collectAspectMethods(aspectPsiClass);
        if (pairSet.isEmpty()) {
            return;
        }

        for (Pair<PsiMethod, AopPointcut> pair : pairSet) {
            PsiMethod psiMethod = pair.first;
            AopPointcut aopPointcut = pair.second;

            @SuppressWarnings({"DialogTitleCapitalization", "DataFlowIssue"})
            RelatedItemLineMarkerInfo<PsiElement> lineMarkerInfo = NavigationGutterIconBuilder
                    .create(IconLoader.getIcon("/icons/pointcut.svg", getClass()))
                    .setTargets(NotNullLazyValue.lazy(() -> findAdvisedMatchedMethods(module, aopPointcut, aspectPsiClass)))
                    .setTooltipText("导航到切面匹配的方法")
                    .setEmptyPopupText("切面没有匹配的方法")
                    .createLineMarkerInfo(psiMethod.getNameIdentifier());

            result.add(lineMarkerInfo);
            LOG.info("为切面类:{}的{}方法添加行标记", aspectPsiClass.getName(), psiMethod.getName());
        }
    }

    private Set<PsiMethod> findAdvisedMatchedMethods(Module module, AopPointcut aopPointcut, PsiClass aspectPsiClass) {
        AopMatcher matcher = AopMatcher.getMatcher(aopPointcut);
        if (matcher == null) {
            return Collections.emptySet();
        }

        VirtualFile virtualFile = aspectPsiClass.getContainingFile().getVirtualFile();
        Collection<Module> projectModules = ModuleUtil.getModulesOfType(module.getProject(), JavaModuleType.getModuleType());

        // 找到依赖了该切面的项目模块
        List<Module> modulesDependAspectCls = projectModules.stream()
                .filter(projectModule -> projectModule.getModuleContentWithDependenciesScope().accept(virtualFile))
                .collect(Collectors.toList());

        // 收集模块里所有匹配切面的方法
        return modulesDependAspectCls.stream()
                .map(matcher::collectMatchMethods)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * 为匹配类方法添加行标记,点击后能跳转到对应的切面
     */
    private void annotateMethod(PsiElement psiElement, Collection<? super LineMarkerInfo<?>> result, Set<PsiClass> aspectPsiClasses) {
        UElement uParent = UastUtils.getUParentForIdentifier(psiElement);
        if (uParent == null) {
            return;
        }

        PsiElement psiParent = uParent.getJavaPsi();
        if (psiParent == null) {
            return;
        }

        if (!(uParent instanceof UMethod)) {
            return;
        }

        UMethod uMethod = (UMethod) uParent;
        if (uMethod.isConstructor()) {
            return;
        }

        UClass containingClass = UastUtils.getContainingUClass(uParent);
        if (containingClass == null) {
            return;
        }

        PsiClass psiClass = containingClass.getJavaPsi();
        PsiMethod psiMethod = uMethod.getJavaPsi();

        PsiIdentifier nameIdentifier = psiMethod.getNameIdentifier();
        if (nameIdentifier == null) {
            return;
        }

        LOG.info("开始收集匹配类:{}的{}方法所对应的切面", psiClass.getName(), psiMethod.getName());
        Set<PsiMethod> psiMethods = AopUtils.collectMethodMatchedAspects(psiClass, psiMethod, aspectPsiClasses);
        LOG.info("完成收集匹配类:{}的{}方法所对应的切面,数量:{}", psiClass.getName(), psiMethod.getName(), psiMethods.size());
        if (psiMethods.isEmpty()) {
            return;
        }

        @SuppressWarnings("DialogTitleCapitalization")
        RelatedItemLineMarkerInfo<PsiElement> lineMarkerInfo = NavigationGutterIconBuilder
                .create(IconLoader.getIcon("/icons/method.svg", getClass()))
                .setTargets(psiMethods)
                .setTooltipText("导航到切面")
                .createLineMarkerInfo(psiElement);
        result.add(lineMarkerInfo);
        LOG.info("为匹配类:{}的{}方法添加行标记", psiClass.getName(), psiMethod.getName());
    }

    @Override
    public @GutterName String getName() {
        return "AOP (Java/Kotlin)";
    }
}
