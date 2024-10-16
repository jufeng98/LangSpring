package org.javamaster.spring;

import com.google.common.collect.Lists;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.javamaster.spel.psi.SpelArrayWrap;
import org.javamaster.spel.psi.SpelCollectionProjection;
import org.javamaster.spel.psi.SpelFieldName;
import org.javamaster.spel.psi.SpelFieldOrMethod;
import org.javamaster.spel.psi.SpelFieldOrMethodName;
import org.javamaster.spel.psi.SpelFieldRecursiveCall;
import org.javamaster.spel.psi.SpelMethodCall;
import org.javamaster.spel.psi.SpelMethodParam;
import org.javamaster.spel.psi.SpelMethodParams;
import org.javamaster.spel.psi.SpelRoot;
import org.javamaster.spel.psi.SpelRootCombination;
import org.javamaster.spel.psi.SpelSpel;
import org.javamaster.spel.psi.SpelStaticT;
import org.javamaster.spel.psi.SpelStringLiteral;
import org.javamaster.utils.SpelUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yudong
 */
public class SpelReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(SpelSpel.class), new SpelPsiReferenceProvider());
    }

    public static class SpelPsiReferenceProvider extends PsiReferenceProvider {

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                               @NotNull ProcessingContext context) {
            SpelSpel spelSpel = (SpelSpel) element;
            PsiLanguageInjectionHost injectionHost = InjectedLanguageManager.getInstance(spelSpel.getProject()).getInjectionHost(spelSpel);
            PsiLiteralExpression psiLiteralExpression = (PsiLiteralExpression) injectionHost;

            PsiMethod psiMethod = PsiTreeUtil.getParentOfType(psiLiteralExpression, PsiMethod.class);
            if (psiMethod == null) {
                return PsiReference.EMPTY_ARRAY;
            }

            SpelRoot spelRoot = spelSpel.getRoot();
            PsiElement[] children = spelRoot.getChildren();
            List<SpelReference> references = Lists.newArrayList();

            handleRootChildren(children, spelSpel, psiMethod, references);

            List<SpelRootCombination> rootCombinationList = spelSpel.getRootCombinationList();
            for (SpelRootCombination spelRootCombination : rootCombinationList) {
                SpelRoot root = spelRootCombination.getRoot();
                if (root == null) {
                    continue;
                }

                PsiElement[] list = root.getChildren();
                handleRootChildren(list, spelSpel, psiMethod, references);
            }

            return references.toArray(new PsiReference[0]);
        }

        private PsiClass handleRootChildren(PsiElement[] children, SpelSpel spelSpel,
                                            PsiMethod psiMethod, List<SpelReference> references) {
            PsiClass prevResolve = null;

            for (PsiElement child : children) {
                if (child instanceof SpelFieldOrMethodName) {
                    SpelFieldOrMethodName fieldOrMethodName = (SpelFieldOrMethodName) child;

                    prevResolve = handleSpelFieldOrMethodName(fieldOrMethodName, spelSpel, psiMethod, references);
                } else if (child instanceof SpelFieldOrMethod) {
                    SpelFieldOrMethod fieldOrMethod = (SpelFieldOrMethod) child;

                    prevResolve = handleSpelFieldOrMethod(fieldOrMethod, prevResolve, spelSpel, psiMethod, references);
                } else if (child instanceof SpelStaticT) {
                    SpelStaticT staticT = (SpelStaticT) child;

                    prevResolve = handleSpelStaticT(staticT, spelSpel, references);
                } else if (child instanceof SpelMethodCall) {
                    SpelMethodCall methodCall = (SpelMethodCall) child;

                    handleMethodCall(methodCall, spelSpel, psiMethod, references);
                } else if (child instanceof SpelStringLiteral) {
                    Project project = psiMethod.getProject();
                    GlobalSearchScope scope = GlobalSearchScope.allScope(project);
                    prevResolve = JavaPsiFacade.getInstance(project).findClass(String.class.getName(), scope);
                } else if (child instanceof SpelCollectionProjection) {
                    SpelCollectionProjection collectionProjection = (SpelCollectionProjection) child;

                    prevResolve = handleSpelCollectionProjection(collectionProjection, prevResolve, spelSpel, references);
                } else if (child instanceof SpelArrayWrap) {
                    SpelArrayWrap spelArrayWrap = (SpelArrayWrap) child;
                    for (SpelRoot root : spelArrayWrap.getRootList()) {
                        handleRootChildren(root.getChildren(), spelSpel, psiMethod, references);
                    }
                }
            }

            return prevResolve;
        }

        private PsiClass handleSpelCollectionProjection(SpelCollectionProjection collectionProjection, PsiClass prevResolve,
                                                        SpelSpel spelSpel, List<SpelReference> references) {
            SpelFieldRecursiveCall fieldRecursiveCall = collectionProjection.getFieldRecursiveCall();
            if (fieldRecursiveCall == null) {
                return null;
            }

            SpelFieldName fieldName = fieldRecursiveCall.getFieldName();
            PsiField psiField = prevResolve.findFieldByName(fieldName.getText(), true);
            if (psiField == null) {
                return null;
            }

            TextRange textRange = fieldName.getTextRange();
            SpelReference reference = new SpelReference(spelSpel, textRange, psiField);
            references.add(reference);

            return SpelUtils.resolvePsiType(psiField.getType());
        }

        private PsiClass handleSpelFieldOrMethodName(SpelFieldOrMethodName fieldOrMethodName, SpelSpel spelSpel,
                                                     PsiMethod psiMethod, List<SpelReference> references) {
            if (SpelUtils.isSharpField(fieldOrMethodName)) {
                String paramName = fieldOrMethodName.getText();
                if (paramName.equals("result")) {
                    PsiTypeElement returnTypeElement = psiMethod.getReturnTypeElement();
                    if (returnTypeElement == null) {
                        return null;
                    }

                    TextRange textRange = fieldOrMethodName.getTextRange();
                    SpelReference reference = new SpelReference(spelSpel, textRange, returnTypeElement);
                    references.add(reference);

                    return SpelUtils.resolvePsiType(returnTypeElement.getType());
                }

                PsiParameter psiParameter = SpelUtils.findMethodParamRelateToField(paramName, psiMethod);
                if (psiParameter == null) {
                    return null;
                }

                TextRange textRange = fieldOrMethodName.getTextRange();
                SpelReference reference = new SpelReference(spelSpel, textRange, psiParameter);
                references.add(reference);

                PsiClass psiClass = SpelUtils.resolvePsiType(psiParameter.getType());
                if (psiClass == null) {
                    return null;
                }

                Project project = psiMethod.getProject();
                GlobalSearchScope scope = GlobalSearchScope.allScope(project);
                PsiClass psiClassCollection = JavaPsiFacade.getInstance(project).findClass(Collection.class.getName(), scope);

                //noinspection ConstantConditions
                if (!psiClass.isInheritor(psiClassCollection, true)) {
                    return SpelUtils.resolvePsiType(psiParameter.getType());
                }

                PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
                for (PsiParameter parameter : parameters) {
                    if (!parameter.getName().equals(paramName)) {
                        continue;
                    }

                    PsiClassReferenceType referenceType = (PsiClassReferenceType) parameter.getType();
                    @SuppressWarnings("UnstableApiUsage")
                    Iterator<JvmType> iterator = referenceType.typeArguments().iterator();
                    if (iterator.hasNext()) {
                        PsiClassReferenceType jvmType = (PsiClassReferenceType) iterator.next();
                        return jvmType.resolve();
                    }
                }

                return SpelUtils.resolvePsiType(psiParameter.getType());
            }

            if (SpelUtils.isSharpMethod(fieldOrMethodName)) {
                PsiClass contextPsiClass = SpelUtils.getMethodContextClz(spelSpel);
                if (contextPsiClass == null) {
                    return null;
                }

                PsiMethod[] psiMethods = contextPsiClass.findMethodsByName(fieldOrMethodName.getText(), false);
                if (psiMethods.length == 0) {
                    return null;
                }

                TextRange textRange = fieldOrMethodName.getTextRange();
                SpelReference reference = new SpelReference(spelSpel, textRange, psiMethods[0]);
                references.add(reference);
                return null;
            }

            return null;
        }

        /**
         * @return 如果是字段，返回字段类型，否则返回方法返回值的类型
         */
        @Nullable
        private PsiClass handleSpelFieldOrMethod(SpelFieldOrMethod fieldOrMethod, PsiClass prevResolve,
                                                 SpelSpel spelSpel, PsiMethod psiMethod, List<SpelReference> references) {
            if (prevResolve == null) {
                return null;
            }

            SpelFieldOrMethodName fieldOrMethodName = fieldOrMethod.getFieldOrMethodName();
            if (fieldOrMethodName == null) {
                return null;
            }

            SpelMethodCall methodCall = fieldOrMethod.getMethodCall();
            if (methodCall == null) {
                String fieldName = fieldOrMethodName.getText();
                PsiField psiField = prevResolve.findFieldByName(fieldName, true);
                if (psiField == null) {
                    return null;
                }

                TextRange textRangeTmp = fieldOrMethod.getTextRange();
                TextRange textRange = new TextRange(textRangeTmp.getStartOffset() + 1, textRangeTmp.getEndOffset());
                SpelReference reference = new SpelReference(spelSpel, textRange, psiField);
                references.add(reference);

                return SpelUtils.resolvePsiType(psiField.getType());
            }

            String methodName = fieldOrMethodName.getText();
            PsiMethod[] fieldPsiMethods = prevResolve.findMethodsByName(methodName, true);
            if (fieldPsiMethods.length == 0) {
                return null;
            }

            TextRange textRange = fieldOrMethodName.getTextRange();

            SpelMethodParams methodParams = methodCall.getMethodParams();
            if (methodParams == null) {
                PsiMethod fieldPsiMethod = fieldPsiMethods[0];
                SpelReference reference = new SpelReference(spelSpel, textRange, fieldPsiMethod);
                references.add(reference);

                return SpelUtils.resolvePsiType(fieldPsiMethod.getReturnType());
            }

            List<SpelMethodParam> methodParamList = methodParams.getMethodParamList();
            List<PsiClass> paramPsiClasses = handleMethodParams(methodParamList, spelSpel, psiMethod, references);

            outer:
            for (PsiMethod fieldPsiMethod : fieldPsiMethods) {
                PsiParameterList parameterList = fieldPsiMethod.getParameterList();
                PsiParameter[] parameters = parameterList.getParameters();

                if (parameters.length != paramPsiClasses.size()) {
                    continue;
                }

                List<PsiClass> list = Arrays.stream(parameters)
                        .map(parameter -> SpelUtils.resolvePsiType(parameter.getType()))
                        .collect(Collectors.toList());

                for (int i = 0; i < list.size(); i++) {
                    PsiClass psiClass = list.get(i);
                    PsiClass paramPsiClass = paramPsiClasses.get(i);

                    // 暂时不考虑参数转型情况
                    if (psiClass != paramPsiClass) {
                        continue outer;
                    }
                }

                SpelReference reference = new SpelReference(spelSpel, textRange, fieldPsiMethod);
                references.add(reference);

                return SpelUtils.resolvePsiType(fieldPsiMethod.getReturnType());
            }

            return null;
        }

        private PsiClass handleSpelStaticT(SpelStaticT staticT, SpelSpel spelSpel, List<SpelReference> references) {
            PsiElement staticReference = staticT.getStaticReference();
            String text = staticReference.getText();
            String fullClassName = text.substring(2, text.length() - 1);

            Module module = ModuleUtil.findModuleForPsiElement(staticReference);
            if (module == null) {
                return null;
            }

            Project project = staticT.getProject();
            GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
            PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(fullClassName, scope);

            TextRange textRangeTmp = staticReference.getTextRange();
            TextRange textRange = new TextRange(textRangeTmp.getStartOffset() + 2, textRangeTmp.getEndOffset() - 1);
            SpelReference reference = new SpelReference(spelSpel, textRange, psiClass);
            references.add(reference);

            return psiClass;
        }

        private void handleMethodCall(SpelMethodCall methodCall, SpelSpel spelSpel,
                                      PsiMethod psiMethod, List<SpelReference> references) {
            SpelMethodParams methodParams = methodCall.getMethodParams();
            if (methodParams == null) {
                return;
            }

            List<SpelMethodParam> methodParamList = methodParams.getMethodParamList();
            handleMethodParams(methodParamList, spelSpel, psiMethod, references);
        }


        private List<PsiClass> handleMethodParams(List<SpelMethodParam> methodParamList, SpelSpel spelSpel,
                                                  PsiMethod psiMethod, List<SpelReference> references) {
            List<PsiClass> psiClasses = Lists.newArrayList();
            for (SpelMethodParam spelMethodParam : methodParamList) {
                SpelRoot root = spelMethodParam.getRoot();
                PsiElement[] children = root.getChildren();

                PsiClass paramPsiClass = handleRootChildren(children, spelSpel, psiMethod, references);
                psiClasses.add(paramPsiClass);
            }
            return psiClasses;
        }
    }

    public static final class SpelReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
        private final PsiElement targetPsiElement;

        public SpelReference(@NotNull PsiElement element, TextRange textRange, PsiElement targetPsiElement) {
            super(element, textRange);
            this.targetPsiElement = targetPsiElement;
        }

        @Override
        public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
            PsiElementResolveResult resolveResult = new PsiElementResolveResult(targetPsiElement);
            return new ResolveResult[]{resolveResult};
        }

        @Nullable
        @Override
        public PsiElement resolve() {
            ResolveResult[] resolveResults = multiResolve(false);
            return resolveResults[0].getElement();
        }

    }

}