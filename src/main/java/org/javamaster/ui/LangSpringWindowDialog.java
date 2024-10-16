package org.javamaster.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import kotlin.Pair;
import org.javamaster.utils.ConfigUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LangSpringWindowDialog {
    private JTextField appIdField;
    private JPasswordField appKeyField;
    private JPanel mainPanel;
    private JButton hideBtn;
    private JButton removeBtn;
    private JButton saveBtn;

    @SuppressWarnings("DialogTitleCapitalization")
    public LangSpringWindowDialog(@NotNull Project project, ToolWindow toolWindow) {
        Pair<String, String> pair = ConfigUtil.getBaiduConfig();
        appIdField.setText(pair.getFirst());
        appKeyField.setText(pair.getSecond());

        if (toolWindow == null) {
            return;
        }

        hideBtn.addActionListener(e -> toolWindow.hide());
        removeBtn.addActionListener(e -> toolWindow.setAvailable(false));
        saveBtn.addActionListener(e -> {
            ConfigUtil.saveBaiduConfig(appIdField.getText(), new String(appKeyField.getPassword()));
            ToolWindowManager.getInstance(project).notifyByBalloon("LangSpring.toolWindow",
                    MessageType.INFO, "<h3>配置保存成功!</h3>");
        });
    }

    public JComponent createCenterPanel() {
        return mainPanel;
    }
}
