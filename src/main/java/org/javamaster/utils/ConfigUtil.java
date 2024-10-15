package org.javamaster.utils;

import com.intellij.openapi.project.Project;
import kotlin.Pair;

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
        return "";
    }
}
