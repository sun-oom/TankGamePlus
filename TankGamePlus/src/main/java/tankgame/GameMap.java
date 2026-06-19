package tankgame;

import java.awt.*;

/**
 * 游戏地图管理：2D 瓦片网格，墙体碰撞检测，可见区域绘制。
 * 地图尺寸 2400×1800，瓦片 60×60，共 40 列 × 30 行。
 */
public class GameMap {

    /** 瓦片尺寸（像素） */
    private final int tileSize;
    /** 世界尺寸（像素） */
    private final int worldWidth;
    private final int worldHeight;
    /** 列数、行数 */
    private final int cols;
    private final int rows;
    /** 网格数据：[row][col]，0=空地，1=墙体 */
    private final int[][] grid;

    /** 墙体颜色 */
    private static final Color WALL_COLOR = new Color(120, 80, 50);
    private static final Color WALL_BORDER = new Color(80, 50, 30);

    /**
     * 地图蓝图：'#' = 墙体，'.' = 空地
     * 40 列 × 30 行
     */
    private static final String[] BLUEPRINT = {
        // 行 0-1：顶部实心边框
        "########################################",
        "########################################",
        // 行 2-3：顶部走廊，两侧墙体
        "##....##......##..........##......##....##",
        "##....##......##..........##......##....##",
        // 行 4-5：中部墙体集群
        "##..........####....####..........####..##",
        "##..........####....####..........####..##",
        // 行 6-7：左侧掩体
        "##....##....................##..........##",
        "##....##....................##..........##",
        // 行 8-9：右侧墙体
        "##........####......####..........####..##",
        "##........####......####..........####..##",
        // 行 10-11：中央走廊
        "##......................##..............##",
        "##......................##..............##",
        // 行 12-13：顶部掩体群
        "##....####..........####......####......##",
        "##....####..........####......####......##",
        // 行 14-15：中部走廊
        "##..........##..............##..........##",
        "##..........##..............##..........##",
        // 行 16-17：交错掩体
        "##....##........####..........####......##",
        "##....##........####..........####......##",
        // 行 18-19：中部墙体
        "##........####..........##..............##",
        "##........####..........##..............##",
        // 行 20-21：下方掩体
        "##....##......................####..##..##",
        "##....##......................####..##..##",
        // 行 22-23：下方走廊
        "##..........####......####..............##",
        "##..........####......####..............##",
        // 行 24-25：底部掩体群
        "##....##..........##..........####......##",
        "##....##..........##..........####......##",
        // 行 26-27：玩家出生区域（保持空旷）
        "##........................................##",
        "##........................................##",
        // 行 28-29：底部实心边框
        "########################################",
        "########################################",
    };

    public GameMap(int tileSize, int worldWidth, int worldHeight) {
        this.tileSize = tileSize;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.cols = worldWidth / tileSize;
        this.rows = worldHeight / tileSize;
        this.grid = new int[rows][cols];

        // 从蓝图初始化网格
        for (int r = 0; r < rows; r++) {
            String line = BLUEPRINT[r];
            for (int c = 0; c < cols; c++) {
                grid[r][c] = (line.charAt(c) == '#') ? 1 : 0;
            }
        }
    }

    /**
     * 检查矩形区域是否与任何墙体瓦片重叠。
     *
     * @param rect 世界坐标中的矩形
     * @return 如果矩形与墙体有任何重叠则返回 true
     */
    public boolean isBlocked(Rectangle rect) {
        int startCol = Math.max(0, rect.x / tileSize);
        int endCol = Math.min(cols - 1, (rect.x + rect.width - 1) / tileSize);
        int startRow = Math.max(0, rect.y / tileSize);
        int endRow = Math.min(rows - 1, (rect.y + rect.height - 1) / tileSize);

        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                if (grid[r][c] == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查单个世界坐标点是否在墙体上。
     */
    public boolean isBlocked(int worldX, int worldY) {
        int col = worldX / tileSize;
        int row = worldY / tileSize;
        if (col < 0 || col >= cols || row < 0 || row >= rows) {
            return true; // 世界外的坐标视为墙体
        }
        return grid[row][col] == 1;
    }

    /**
     * 绘制地图中在摄像机视野内的墙体瓦片。
     */
    public void draw(Graphics g, int camX, int camY, int screenWidth, int screenHeight) {
        // 计算可见瓦片范围
        int startCol = Math.max(0, camX / tileSize);
        int endCol = Math.min(cols - 1, (camX + screenWidth) / tileSize);
        int startRow = Math.max(0, camY / tileSize);
        int endRow = Math.min(rows - 1, (camY + screenHeight) / tileSize);

        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                if (grid[r][c] == 1) {
                    int wx = c * tileSize;
                    int wy = r * tileSize;
                    // 填充
                    g.setColor(WALL_COLOR);
                    g.fillRect(wx, wy, tileSize, tileSize);
                    // 边框（使瓦片分界清晰）
                    g.setColor(WALL_BORDER);
                    g.drawRect(wx, wy, tileSize - 1, tileSize - 1);
                }
            }
        }
    }

    // ---- 简单的 getter ----

    public int getWorldWidth()  { return worldWidth; }
    public int getWorldHeight() { return worldHeight; }
    public int getTileSize()    { return tileSize; }
    public int getCols()        { return cols; }
    public int getRows()        { return rows; }
}
