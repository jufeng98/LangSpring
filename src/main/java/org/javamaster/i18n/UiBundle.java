package org.javamaster.i18n;

import com.intellij.BundleBase;
import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author yudong
 */
public class UiBundle extends DynamicBundle {
    public static final String BUNDLE = "messages.ui";
    private static final UiBundle INSTANCE = new UiBundle(BUNDLE);

    public UiBundle(@NotNull String pathToBundle) {
        super(pathToBundle);
    }

    @Override
    public @NotNull @Nls String getMessage(@NotNull @NonNls String key, Object @NotNull ... params) {
        return BundleBase.messageOrDefault(ResourceBundle.getBundle(BUNDLE, Locale.CHINESE), key, null, params);
    }

    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }

}
