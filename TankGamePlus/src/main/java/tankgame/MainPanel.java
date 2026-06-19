package tankgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class MainPanel extends JPanel {
    GameManger gameManger;
    private Timer gameLoop;
    private JButton pauseBtn;
    private JButton quitBtn;
    private Runnable quitAction;  // 退出回调，由 MainFrame 设置

    public MainPanel(int width, int height) {
        // 设置面板的尺寸
        setPreferredSize(new Dimension(width, height));
        // 设置背景色
        setBackground(Color.BLACK);
        // 使用 null 布局以便手动定位按钮
        setLayout(null);

        gameManger = GameManger.getInstance();

        // ---- 暂停按钮 ----
        pauseBtn = new JButton("⏸ 暂停");  // ⏸ 暂停
        pauseBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        pauseBtn.setFocusable(false);  // 不抢夺键盘焦点
        pauseBtn.setContentAreaFilled(false);
        pauseBtn.setBorderPainted(false);
        pauseBtn.setForeground(Color.WHITE);
        pauseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 按钮悬停效果
        pauseBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                pauseBtn.setForeground(Color.YELLOW);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                pauseBtn.setForeground(Color.WHITE);
            }
        });

        pauseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameManger.togglePause();
                updatePauseButtonText();
                // 让游戏面板重新获得焦点，以便键盘继续生效
                MainPanel.this.requestFocusInWindow();
            }
        });
        add(pauseBtn);

        // ---- 退出按钮 ----
        quitBtn = new JButton("✕ 退出");
        quitBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        quitBtn.setFocusable(false);
        quitBtn.setContentAreaFilled(false);
        quitBtn.setBorderPainted(false);
        quitBtn.setForeground(Color.WHITE);
        quitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        quitBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                quitBtn.setForeground(Color.RED);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                quitBtn.setForeground(Color.WHITE);
            }
        });

        quitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (quitAction != null) {
                    quitAction.run();
                }
            }
        });
        add(quitBtn);

        // 根据窗口大小定位按钮（右上角）
        pauseBtn.setBounds(width - 130, 10, 120, 35);
        quitBtn.setBounds(width - 130, 50, 120, 35);

        // 游戏循环（每16毫秒刷新一次）
        gameLoop = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameManger.update();
                repaint();
            }
        });
        gameLoop.start();

        // 让面板可以获得焦点
        setFocusable(true);

        // === 鼠标右键加速 ===
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    TankPlayer p = gameManger.getPlayer();
                    if (p != null && p.alive && gameManger.getStatus() == Setting.RUNNING) {
                        p.setBoosted(true);
                    }
                }
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    TankPlayer p = gameManger.getPlayer();
                    if (p != null) {
                        p.setBoosted(false);
                    }
                }
            }
        });

        // 键盘监听
        addKeyListener(new KeyAdapter() {
            private final Set<Integer> pressedKeys = new HashSet<>();

            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                // P 键切换暂停
                if (key == KeyEvent.VK_P) {
                    gameManger.togglePause();
                    updatePauseButtonText();
                    return;
                }

                pressedKeys.add(key);

                // 每次重新获取玩家坦克引用
                TankPlayer currentTank = gameManger.getPlayer();
                if (currentTank == null) return;

                // 只有游戏运行时才响应方向键，暂停时也不响应
                if (gameManger.getStatus() != Setting.RUNNING) return;
                if (gameManger.isPaused()) return;

                if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
                    currentTank.direction = Setting.UP;
                    currentTank.setActive(true);
                }
                if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) {
                    currentTank.direction = Setting.DOWN;
                    currentTank.setActive(true);
                }
                if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
                    currentTank.direction = Setting.LEFT;
                    currentTank.setActive(true);
                }
                if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
                    currentTank.direction = Setting.RIGHT;
                    currentTank.setActive(true);
                }
                // 设置SPACE为攻击键
                if (key == KeyEvent.VK_SPACE) {
                    if (currentTank.alive) {
                        currentTank.fire();
                    }
                }
            }

            // 处理松开按键的情况
            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
                TankPlayer currentTank = gameManger.getPlayer();
                if (currentTank != null) {
                    currentTank.setActive(false);
                }
            }
        });
    }

    /** 根据暂停状态更新按钮文字 */
    private void updatePauseButtonText() {
        if (gameManger.isPaused()) {
            pauseBtn.setText("▶ 继续");  // ▶ 继续
        } else {
            pauseBtn.setText("⏸ 暂停");  // ⏸ 暂停
        }
    }

    /** 设置退出按钮的回调动作 */
    public void setQuitAction(Runnable action) {
        this.quitAction = action;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameManger.draw(g);
    }
}
