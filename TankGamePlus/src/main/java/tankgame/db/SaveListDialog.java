package tankgame.db;

import tankgame.GameManger;
import tankgame.Setting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SaveListDialog extends JDialog {
    private DefaultListModel<String> listModel;
    private JList<String> saveList;
    private List<GameSave> saves;
    /** 最后一次成功加载的存档 ID */
    private String loadedSaveId = null;

    public SaveListDialog(Frame owner) {
        super(owner, "选择存档", true);
        setSize(450, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // 标题
        JLabel titleLabel = new JLabel("请选择要加载的存档", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 列表
        listModel = new DefaultListModel<>();
        saveList = new JList<>(listModel);
        saveList.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        saveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 双击加载存档
        saveList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    loadSelectedSave();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(saveList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // 底部按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton loadBtn = new JButton("加载存档");
        loadBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        loadBtn.addActionListener(e -> loadSelectedSave());

        JButton deleteBtn = new JButton("删除存档");
        deleteBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        deleteBtn.addActionListener(e -> deleteSelectedSave());

        JButton cancelBtn = new JButton("取消");
        cancelBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(loadBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // 加载存档列表
        refreshSaveList();
    }

    private void refreshSaveList() {
        listModel.clear();
        try {
            GameSaveDAO dao = new GameSaveDAO();
            saves = dao.getActiveSaves(); // 只显示进行中的存档

            if (saves.isEmpty()) {
                listModel.addElement("  （暂无存档）");
                saveList.setEnabled(false);
            } else {
                for (GameSave save : saves) {
                    String display = save.getSaveName() + " - " + save.getSaveTime();
                    listModel.addElement(display);
                }
                saveList.setEnabled(true);
                if (listModel.size() > 0) {
                    saveList.setSelectedIndex(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            listModel.addElement("  （加载存档失败: " + e.getMessage() + "）");
            saveList.setEnabled(false);
        }
    }

    private void loadSelectedSave() {
        int index = saveList.getSelectedIndex();
        if (index < 0 || index >= saves.size()) {
            JOptionPane.showMessageDialog(this, "请先选择一个存档");
            return;
        }

        GameSave selected = saves.get(index);

        // 确认加载
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要加载存档 \"" + selected.getSaveName() + "\" 吗？\n当前游戏进度将会丢失。",
                "确认加载",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // 加载完整数据
                GameSaveDAO dao = new GameSaveDAO();
                GameSave fullSave = dao.loadSave(selected.getSaveId());
                if (fullSave != null) {
                    // 恢复到游戏
                    GameManger gm = GameManger.getInstance();
                    gm.loadFromSave(fullSave);

                    // 记录加载的存档 ID
                    loadedSaveId = fullSave.getSaveId();

                    // 关闭对话框
                    dispose();

                    // 切换到游戏面板（通过父窗口处理）
                    JOptionPane.showMessageDialog(
                            this,
                            "存档加载成功！",
                            "成功",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "加载存档失败: " + e.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void deleteSelectedSave() {
        int index = saveList.getSelectedIndex();
        if (index < 0 || index >= saves.size()) {
            JOptionPane.showMessageDialog(this, "请先选择一个存档");
            return;
        }

        GameSave selected = saves.get(index);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除存档 \"" + selected.getSaveName() + "\" 吗？\n此操作不可恢复！",
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                GameSaveDAO dao = new GameSaveDAO();
                dao.deleteSave(selected.getSaveId());
                refreshSaveList();
                JOptionPane.showMessageDialog(
                        this,
                        "存档已删除",
                        "成功",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "删除存档失败: " + e.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /** 获取最后一次成功加载的存档 ID */
    public String getLoadedSaveId() {
        return loadedSaveId;
    }
}