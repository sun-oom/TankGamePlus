package tankgame;

import tankgame.db.GameSave;
import tankgame.db.GameSaveDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;
import java.io.IOException;

public class MenuPanel extends JPanel{
    // 管理多个面板之间的切换布局
    private CardLayout cardLayout;
    // 卡片布局的容器面板
    private JPanel container;
    private int width;
    private int height;
    // 原始游戏窗口尺寸（不受背景图片影响）
    private final int gameWidth;
    private final int gameHeight;
    // 按钮矩形区域
    private Rectangle startBtn;
    private Rectangle aboutBtn;
    private Rectangle exitBtn;
    private Rectangle continueBtn;
    private Rectangle settingBtn;
    private boolean continueHover = false;

    // 鼠标悬停状态
    private boolean startHover = false;
    private boolean aboutHover = false;
    private boolean settingHover = false;
    private boolean exitHover = false;

    // 背景图片
    private Image backgroundImage;
    // 图片原始尺寸
    private int imageWidth;
    private int imageHeight;

    public MenuPanel(CardLayout cardLayout, JPanel container, int width, int height){
        this.cardLayout = cardLayout;
        this.container = container;
        this.width = width;
        this.height = height;
        // 保存原始游戏窗口尺寸，不受背景图片尺寸影响
        this.gameWidth = width;
        this.gameHeight = height;

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);

        // 加载背景图片并获取尺寸
        loadBackgroundImage();

        // 如果图片加载成功，使用图片尺寸作为面板尺寸
        if (backgroundImage != null && imageWidth > 0 && imageHeight > 0) {
            this.width = imageWidth;
            this.height = imageHeight;
            setPreferredSize(new Dimension(imageWidth, imageHeight));
        }

        // 重新计算按钮位置（基于新的尺寸）
        initButtons();

        // 处理所有鼠标事件
        MouseAdapter mouseAdapter = new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                int mouseX = e.getX();
                int mouseY = e.getY();

                if (continueBtn.contains(mouseX, mouseY)) {
                    loadLatestSave();
                    return;
                }
                if (startBtn.contains(mouseX, mouseY)){
                    GameManger gameManger = GameManger.getInstance();
                    gameManger.InitGame(gameWidth, gameHeight, Setting.TANK_WIDTH, Setting.TANK_HEIGHT);
                    // 切换前将窗口调整为游戏尺寸
                    resizeFrameToGameSize();
                    cardLayout.show(container, "game");
                    MainPanel gamePanel = (MainPanel) container.getComponent(1);
                    gamePanel.requestFocusInWindow();
                    gamePanel.setFocusable(true);
                } else if (aboutBtn.contains(mouseX, mouseY)){
                    JOptionPane.showMessageDialog(
                            MenuPanel.this,
                            "坦克大战游戏\n版本 2.0\n\n使用 WASD 或方向键控制坦克移动，空格键开火\n\n初始敌人数量：15",
                            "关于",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else if (settingBtn.contains(mouseX, mouseY)) {
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(MenuPanel.this);
                    new SettingsDialog(frame).setVisible(true);
                } else if (exitBtn.contains(mouseX, mouseY)){
                    MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(MenuPanel.this);
                    if (mainFrame != null) {
                        mainFrame.saveAndExit();
                    } else {
                        System.exit(0);
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e){
                int mouseX = e.getX();
                int mouseY = e.getY();

                continueHover = continueBtn.contains(mouseX, mouseY);
                startHover = startBtn.contains(mouseX, mouseY);
                aboutHover = aboutBtn.contains(mouseX, mouseY);
                settingHover = settingBtn.contains(mouseX, mouseY);
                exitHover = exitBtn.contains(mouseX, mouseY);

                repaint();
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        setFocusable(true);
    }

    /**
     * 初始化按钮位置（根据面板尺寸计算）
     */
    private void initButtons() {
        int btnWidth = 200;
        int btnHeight = 50;
        int btnX = (width - btnWidth) / 2;
        int continueY = height/2 - 100;
        int startY = height/2 - 30;
        int aboutY = height/2 + 40;
        int settingY = height/2 + 110;
        int exitY = height/2 + 180;

        continueBtn = new Rectangle(btnX, continueY, btnWidth, btnHeight);
        startBtn = new Rectangle(btnX, startY, btnWidth, btnHeight);
        aboutBtn = new Rectangle(btnX, aboutY, btnWidth, btnHeight);
        settingBtn = new Rectangle(btnX, settingY, btnWidth, btnHeight);
        exitBtn = new Rectangle(btnX, exitY, btnWidth, btnHeight);
    }

    /**
     * 加载背景图片并获取尺寸
     */
    private void loadBackgroundImage() {
        try {
            // 从资源目录加载图片
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/tankgame/img/background.png"));

            if (backgroundImage != null) {
                // 获取图片尺寸
                imageWidth = backgroundImage.getWidth(this);
                imageHeight = backgroundImage.getHeight(this);
                System.out.println("图片加载成功，尺寸: " + imageWidth + " x " + imageHeight);
            } else {
                System.err.println("背景图片文件不存在，使用默认尺寸");
                // 使用默认尺寸
                imageWidth = width;
                imageHeight = height;
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("无法加载背景图片，使用默认尺寸");
            imageWidth = width;
            imageHeight = height;
            backgroundImage = null;
        }
    }

    /**
     * 获取图片宽度
     */
    public int getImageWidth() {
        return imageWidth;
    }

    /**
     * 获取图片高度
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * 将父窗口调整为游戏面板的原始尺寸
     */
    private void resizeFrameToGameSize() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            // 考虑窗口边框，设置内容面板的 preferred size，然后 pack
            container.setPreferredSize(new Dimension(gameWidth, gameHeight));
            frame.pack();
            frame.setLocationRelativeTo(null);
        }
    }

    /** 直接加载最新的存档并进入游戏 */
    private void loadLatestSave() {
        try {
            GameSaveDAO dao = new GameSaveDAO();
            GameSave latestSave = dao.getLatestActiveSave();

            if (latestSave == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "没有可继续的游戏进度。",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            GameManger gm = GameManger.getInstance();
            gm.loadFromSave(latestSave);

            MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame != null) {
                mainFrame.setCurrentSaveId(latestSave.getSaveId());
            }

            // 切换前将窗口调整为游戏尺寸
            resizeFrameToGameSize();
            cardLayout.show(container, "game");
            MainPanel gamePanel = (MainPanel) container.getComponent(1);
            gamePanel.requestFocusInWindow();
            gamePanel.setFocusable(true);

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 绘制背景图片（自适应填满）
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, width, height, this);
        } else {
            // 如果没有图片，使用纯色背景
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
        }

        // 绘制半透明遮罩层（让按钮更清晰）
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, width, height);

        // 绘制标题
        Font titleFont = new Font("微软雅黑", Font.BOLD, 48);
        g.setFont(titleFont);
        g.setColor(Color.YELLOW);
        FontMetrics fm = g.getFontMetrics();
        String title = "坦克大战";
        int titleX = (width - fm.stringWidth(title)) / 2;
        int titleY = height / 4;
        g.drawString(title, titleX, titleY);

        // 绘制按钮
        g.setColor(Color.ORANGE);
        drawButton(g, continueBtn, "继续游戏", continueHover);
        g.setColor(Color.cyan);
        drawButton(g, startBtn, "开始", startHover);
        g.setColor(Color.blue);
        drawButton(g, aboutBtn, "关于", aboutHover);
        g.setColor(Color.GREEN);
        drawButton(g, settingBtn, "设置", settingHover);
        g.setColor(Color.red);
        drawButton(g, exitBtn, "退出", exitHover);
    }

    private void drawButton(Graphics g, Rectangle btn, String text, boolean hover) {
        if (hover) {
            g.setColor(new Color(0, 0, 139));
            g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 30, 30);
            g.setColor(Color.WHITE);
            g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 30, 30);
        } else {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 30, 30);
            g.setColor(Color.WHITE);
            g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 30, 30);
        }

        Font btnFont = new Font("微软雅黑", Font.BOLD, 24);
        g.setFont(btnFont);
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width - fm.stringWidth(text)) / 2;
        int textY = btn.y + (btn.height - fm.getHeight()) / 2 + fm.getAscent();
        g.setColor(Color.WHITE);
        g.drawString(text, textX, textY);
    }
}