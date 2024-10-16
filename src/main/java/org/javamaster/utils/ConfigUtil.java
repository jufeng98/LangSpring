package org.javamaster.utils;

import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.json.psi.JsonValue;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import kotlin.Pair;

import java.util.Collection;
import java.util.prefs.Preferences;

public class ConfigUtil {
    private static final Preferences PREFERENCES = Preferences.userRoot().node("org.javamaster.lang.spring");

    public static Pair<String, String> getBaiduConfig() {
        return new Pair<>(PREFERENCES.get("baiduAppId", ""), PREFERENCES.get("baiduAppKey", ""));
    }

    public static void saveBaiduConfig(String appId, String appKey) {
        PREFERENCES.put("baiduAppId", appId);
        PREFERENCES.put("baiduAppKey", appKey);
    }

    public static String getApolloUrl(Project project) {
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
        Collection<VirtualFile> files = FilenameIndex.getVirtualFilesByName("git-flow-k8s.json", scope);
        if (files.isEmpty()) {
            return null;
        }

        VirtualFile virtualFile = files.iterator().next();
        PsiFile psiFile = PsiUtil.getPsiFile(project, virtualFile);
        JsonFile jsonFile = (JsonFile) psiFile;

        JsonObject topLevelValue = (JsonObject) jsonFile.getTopLevelValue();
        if (topLevelValue == null) {
            return null;
        }

        JsonProperty jsonProperty = topLevelValue.findProperty("apolloUrl");
        if (jsonProperty == null) {
            return null;
        }

        JsonValue value = jsonProperty.getValue();
        if (value == null) {
            return null;
        }

        JsonStringLiteral jsonStringLiteral = (JsonStringLiteral) value;

        return jsonStringLiteral.getValue();
    }
}
