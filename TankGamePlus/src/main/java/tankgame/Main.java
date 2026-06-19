package tankgame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame(Setting.WIN_WIDTH,Setting.WIN_HEIGHT);
                frame.setVisible(true);  // 显示窗口
            }
        });
    }
}
