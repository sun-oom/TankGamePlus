package tankgame;

/**
 * 摄像机，跟随目标在世界坐标中移动，自动钳制到世界边界内。
 */
public class Camera {
    /** 视口左上角在世界坐标中的 X 位置 */
    private int x;
    /** 视口左上角在世界坐标中的 Y 位置 */
    private int y;

    public Camera() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * 将摄像机中心对准目标坐标，并钳制到世界边界内。
     *
     * @param targetWorldX 目标世界 X 坐标（中心点）
     * @param targetWorldY 目标世界 Y 坐标（中心点）
     * @param screenWidth  屏幕宽度
     * @param screenHeight 屏幕高度
     * @param worldWidth   世界宽度
     * @param worldHeight  世界高度
     */
    public void update(int targetWorldX, int targetWorldY,
                       int screenWidth, int screenHeight,
                       int worldWidth, int worldHeight) {
        x = targetWorldX - screenWidth / 2;
        y = targetWorldY - screenHeight / 2;

        // 钳制到世界边界内（摄像机不能超出世界范围）
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > worldWidth - screenWidth) x = worldWidth - screenWidth;
        if (y > worldHeight - screenHeight) y = worldHeight - screenHeight;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
