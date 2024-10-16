package org.javamaster.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.javamaster.i18n.UiBundle;
import org.javamaster.ui.SampleDialog;
import org.jetbrains.annotations.NotNull;

/**
 * 帮助
 *
 * @author yudong
 */
public class HelpAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        event.getPresentation().setText(UiBundle.message("HelpAction.text"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        SampleDialog sampleDialog = new SampleDialog(event);
        sampleDialog.show();
    }

}
