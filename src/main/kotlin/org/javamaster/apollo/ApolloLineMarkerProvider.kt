package org.javamaster.apollo

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.lang.properties.IProperty
import com.intellij.navigation.GotoRelatedItem
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.util.ConstantFunction
import org.javamaster.spring.ValueAnnotationLineMarkerProvider.ValueGutterIconNavigationHandler

/**
 * @author yudong
 */
class ApolloLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>,
    ) {
        val containingFile = element.containingFile ?: return

        val name = containingFile.name
        if (name != "app.properties") {
            return
        }

        if (element !is IProperty) {
            return
        }

        val property = element as IProperty
        if (property.name != "app.id") {
            return
        }

        val appId = property.value

        val lineMarkerInfo = RelatedItemLineMarkerInfo(
            element,
            element.textRange,
            IconLoader.getIcon("/icons/apollo.svg", javaClass),
            ConstantFunction("浏览器打开Apollo管理页面"),
            ValueGutterIconNavigationHandler(listOf(appId)),
            GutterIconRenderer.Alignment.CENTER
        ) { GotoRelatedItem.createItems(emptyList()) }

        result.add(lineMarkerInfo)
    }
}
