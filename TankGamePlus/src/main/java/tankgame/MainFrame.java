package tankgame;

import tankgame.db.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MainFrame extends JFrame{
    private final GameManger gameManger;
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private MenuPanel menuPanel;
    private MainPanel gamePanel;
    private int width;
    private int height;

    /** 当前游戏对应的存档 ID，null 表示尚未保存过 */
    private String currentSaveId = null;

    public MainFrame(int width, int height){
        this.width = width;
        this.height = height;
        // 设置窗口标题
        setTitle("Tank Game");
        // 设置关闭按钮的行为（点X时触发保存后再退出）
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 窗口大小不可改变
        setResizable(false);

        gameManger = GameManger.getInstance();
        gameManger.InitGame(width,height,Setting.TANK_WIDTH,Setting.TANK_HEIGHT);

        // 使用 CardLayout 管理面板
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // 创建菜单面板
        menuPanel = new MenuPanel(cardLayout, mainContainer, width, height);
        mainContainer.add(menuPanel, "menu");

        // 创建游戏面板
        gamePanel = new MainPanel(width, height);
        gamePanel.setQuitAction(() -> {
            autoSaveGame();
            currentSaveId = null;
            // 切回菜单时把窗口调整为菜单尺寸
            int imgW = menuPanel.getImageWidth();
            int imgH = menuPanel.getImageHeight();
            if (imgW > 0 && imgH > 0) {
                mainContainer.setPreferredSize(new Dimension(imgW, imgH));
                pack();
                setLocationRelativeTo(null);
            }
            cardLayout.show(mainContainer, "menu");
            menuPanel.requestFocusInWindow();
        });
        mainContainer.add(gamePanel, "game");

        add(mainContainer);

        // 根据菜单面板的图片尺寸调整窗口的大小
        int imageWidth = menuPanel.getImageWidth();
        int imageHeight = menuPanel.getImageHeight();

        // 如果图片加载成功，使用图片尺寸；否则使用默认尺寸
        if (imageWidth > 0 && imageHeight > 0) {
            // 重新设置菜单面板的尺寸
            menuPanel.setPreferredSize(new Dimension(imageWidth, imageHeight));
            // 设置窗口大小
            setSize(imageWidth, imageHeight);
        } else {
            setSize(width, height);
        }

        // 全局键盘监听
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyEventDispatcher() {
                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        if (e.getID() == KeyEvent.KEY_PRESSED) {
                            int key = e.getKeyCode();
                            // 游戏结束或胜利时回到菜单
                            if (key == KeyEvent.VK_ENTER) {
                                int status = gameManger.getStatus();
                                if (status == Setting.OVER || status == Setting.WIN) {
                                    // 更新存档状态
                                    updateSaveStatus(status);
                                    currentSaveId = null;  // 游戏结束，重置存档 ID
                                    // 切回菜单时把窗口调整为菜单尺寸（图片尺寸）
                                    int imgW = menuPanel.getImageWidth();
                                    int imgH = menuPanel.getImageHeight();
                                    if (imgW > 0 && imgH > 0) {
                                        mainContainer.setPreferredSize(new Dimension(imgW, imgH));
                                        pack();
                                        setLocationRelativeTo(null);
                                    }
                                    cardLayout.show(mainContainer, "menu");
                                    menuPanel.requestFocusInWindow();
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                });

        // 添加窗口关闭监听器（自动保存）
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                autoSaveGame();
            }
        });

        // pack();  // 自动调整窗口大小以适应画板
        setLocationRelativeTo(null);
    }

    /**
     * 窗口关闭时自动保存游戏进度
     */
    private void autoSaveGame() {
        try {
            GameManger gm = GameManger.getInstance();
            int status = gm.getStatus();

            // 只有在游戏进行中才自动保存
            if (status == Setting.RUNNING) {
                GameSave save = gm.getCurrentGameSave();
                GameSaveDAO dao = new GameSaveDAO();
                String savedId = dao.saveGame(save);
                if (savedId != null) {
                    currentSaveId = savedId;
                }
                System.out.println("游戏已自动保存");
            }
        } catch (Exception e) {
            System.err.println("自动保存失败: " + e.getMessage());
            e.printStackTrace();
            // 弹窗提示用户保存失败
            JOptionPane.showMessageDialog(
                    this,
                    "游戏进度保存失败！\n请检查数据库连接是否正常。\n错误信息: " + e.getMessage(),
                    "保存失败",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * 游戏结束时更新存档状态
     */
    private void updateSaveStatus(int gameStatus) {
        if (currentSaveId == null) return;

        // 将游戏状态映射到数据库 status 值
        // Setting.RUNNING=0, Setting.WIN=1, Setting.OVER=2 — 已与 DB 对齐
        int dbStatus = gameStatus;

        try {
            GameSaveDAO dao = new GameSaveDAO();
            dao.updateStatus(currentSaveId, dbStatus);
            System.out.println("存档状态已更新: " + currentSaveId + " -> " + dbStatus);
        } catch (Exception e) {
            System.err.println("更新存档状态失败: " + e.getMessage());
        }
    }

    /**
     * 供 MenuPanel 在点击"退出"时调用，确保游戏进度被保存
     */
    public void saveAndExit() {
        autoSaveGame();
        System.exit(0);
    }

    /** 获取当前存档 ID（供外部使用） */
    public String getCurrentSaveId() {
        return currentSaveId;
    }

    /** 设置当前存档 ID（加载存档时由外部设置） */
    public void setCurrentSaveId(String saveId) {
        this.currentSaveId = saveId;
    }
}
