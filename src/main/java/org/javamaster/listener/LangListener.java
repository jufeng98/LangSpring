package org.javamaster.listener;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

public class LangListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        DumbService.getInstance(project)
                .smartInvokeLater(() -> {

                });

        ReadAction.nonBlocking(() -> "").inSmartMode(project);
    }

}
