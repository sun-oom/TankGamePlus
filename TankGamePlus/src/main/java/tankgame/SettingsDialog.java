package tankgame;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * 游戏设置对话框，使用滑动条调整敌人数量、敌人速度、玩家速度。
 */
public class SettingsDialog extends JDialog {

    private final JSlider enemyCountSlider;
    private final JSlider enemySpeedSlider;
    private final JSlider playerSpeedSlider;

    private final JLabel enemyCountValue;
    private final JLabel enemySpeedValue;
    private final JLabel playerSpeedValue;

    public SettingsDialog(Frame owner) {
        super(owner, "游戏设置", true);
        setSize(420, 320);
        setLocationRelativeTo(owner);
        setResizable(false);

        // 主面板：垂直排列的三行滑动条 + 底部按钮
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // ---- 滑动条区域 ----
        JPanel slidersPanel = new JPanel(new GridLayout(3, 1, 0, 15));

        // 敌人数量滑动条
        enemyCountSlider = new JSlider(1, 20, Setting.ENEMY_COUNT);
        enemyCountSlider.setMajorTickSpacing(5);
        enemyCountSlider.setMinorTickSpacing(1);
        enemyCountSlider.setPaintTicks(true);
        enemyCountSlider.setPaintLabels(true);
        enemyCountValue = new JLabel(String.valueOf(Setting.ENEMY_COUNT));

        // 敌人速度滑动条
        enemySpeedSlider = new JSlider(1, 30, Setting.ENEMY_SPEED);
        enemySpeedSlider.setMajorTickSpacing(5);
        enemySpeedSlider.setMinorTickSpacing(1);
        enemySpeedSlider.setPaintTicks(true);
        enemySpeedSlider.setPaintLabels(true);
        enemySpeedValue = new JLabel(String.valueOf(Setting.ENEMY_SPEED));

        // 玩家速度滑动条
        playerSpeedSlider = new JSlider(1, 20, Setting.PLAYER_SPEED);
        playerSpeedSlider.setMajorTickSpacing(5);
        playerSpeedSlider.setMinorTickSpacing(1);
        playerSpeedSlider.setPaintTicks(true);
        playerSpeedSlider.setPaintLabels(true);
        playerSpeedValue = new JLabel(String.valueOf(Setting.PLAYER_SPEED));

        // 将每个滑动条和值标签组装成一行
        slidersPanel.add(createSliderRow("敌人数量：", enemyCountSlider, enemyCountValue));
        slidersPanel.add(createSliderRow("敌人速度：", enemySpeedSlider, enemySpeedValue));
        slidersPanel.add(createSliderRow("玩家速度：", playerSpeedSlider, playerSpeedValue));

        mainPanel.add(slidersPanel, BorderLayout.CENTER);

        // ---- 底部按钮 ----
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 5));

        JButton okBtn = new JButton("确定");
        okBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        okBtn.addActionListener(e -> {
            // 保存设置
            Setting.ENEMY_COUNT = enemyCountSlider.getValue();
            Setting.ENEMY_SPEED = enemySpeedSlider.getValue();
            Setting.PLAYER_SPEED = playerSpeedSlider.getValue();
            dispose();
        });

        JButton cancelBtn = new JButton("取消");
        cancelBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 监听滑动条变化，实时更新数值标签
        ChangeListener valueUpdater = e -> {
            enemyCountValue.setText(String.valueOf(enemyCountSlider.getValue()));
            enemySpeedValue.setText(String.valueOf(enemySpeedSlider.getValue()));
            playerSpeedValue.setText(String.valueOf(playerSpeedSlider.getValue()));
        };
        enemyCountSlider.addChangeListener(valueUpdater);
        enemySpeedSlider.addChangeListener(valueUpdater);
        playerSpeedSlider.addChangeListener(valueUpdater);
    }

    /**
     * 创建一行：标签 + 滑动条 + 当前值
     */
    private JPanel createSliderRow(String labelText, JSlider slider, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout(5, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        label.setPreferredSize(new Dimension(80, 25));

        valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        valueLabel.setPreferredSize(new Dimension(35, 25));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        row.add(label, BorderLayout.WEST);
        row.add(slider, BorderLayout.CENTER);
        row.add(valueLabel, BorderLayout.EAST);

        return row;
    }
}
