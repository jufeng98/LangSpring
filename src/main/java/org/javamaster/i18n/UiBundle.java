package org.javamaster.i18n;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class UiBundle extends DynamicBundle {
    public static final String BUNDLE = "messages.ui";
    private static final UiBundle INSTANCE = new UiBundle(BUNDLE);

    public UiBundle(@NotNull String pathToBundle) {
        super(pathToBundle);
    }

    @Override
    public @NotNull @Nls String getMessage(@NotNull @NonNls String key, Object @NotNull ... params) {
        return message(key, params);
    }

    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }

}
