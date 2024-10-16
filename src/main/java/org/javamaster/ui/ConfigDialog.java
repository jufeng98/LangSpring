package org.javamaster.ui;

import com.intellij.ui.components.JBPasswordField;
import kotlin.Pair;
import org.javamaster.utils.ConfigUtil;

import javax.swing.*;

/**
 * @author yudong
 */
public class ConfigDialog {
    private JPanel mainPanel;
    private JTextField appIdField;
    private JBPasswordField appKeyField;

    public ConfigDialog() {
        resetConfig();
    }

    /**
     * 重置配置
     */
    public void resetConfig() {
        Pair<String, String> pair = ConfigUtil.getBaiduConfig();
        appIdField.setText(pair.getFirst());
        appKeyField.setText(pair.getSecond());
    }

    /**
     * 获取页面当前的配置
     */
    public Pair<String, String> getCurrentConfig() {
        return new Pair<>(appIdField.getText(), new String(appKeyField.getPassword()));
    }

    /**
     * 保存当前配置
     */
    public void saveCurrentConfig() {
        Pair<String, String> curPair = getCurrentConfig();
        ConfigUtil.saveBaiduConfig(curPair.getFirst(), curPair.getSecond());
    }

    public JComponent createCenterPanel() {
        return mainPanel;
    }
}
