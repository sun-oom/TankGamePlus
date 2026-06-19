package tankgame;

import java.awt.*;

public class Setting {
    public static int ENEMY_COUNT = 15;

    // 游戏状态（与数据库 game_saves.status 一致：0=进行中, 1=胜利, 2=失败）
    public static final int RUNNING = 0;
    public static final int WIN = 1;
    public static final int OVER = 2;
    // 方向常量
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    // 反向
    public static final int[] OppositeDir = {DOWN,UP,RIGHT,LEFT};
    // 坦克常量
    public static final int TANK_WIDTH = 36;
    public static final int TANK_HEIGHT = 36;
    public static final Color WHEEL_COLOR = new Color(100,100,100);
    public static final Color GUN_COLOR = new Color(150,150,150);
    // 子弹常量
    public static final int BULLET_WIDTH = 4;
    public static final int BULLET_HEIGHT = 4;
    public static final int BULLET_SPEED = 10;

    // 玩家子弹发射冷却时间（毫秒）
    public static final int PLAYER_FIRE_COOLDOWN = 300;

    public static int ENEMY_SPEED = 3;
    public static int PLAYER_SPEED = 5;
    public static final int PLAYER_BOOST_SPEED = 8;    // 玩家加速时的速度

    // 血量
    public static final int PLAYER_HP = 3;
    public static final int ENEMY_HP = 1;

    public static final int WIN_WIDTH = 800;
    public static final int WIN_HEIGHT = 600;

    // 世界地图常量
    public static final int WORLD_WIDTH = 2400;
    public static final int WORLD_HEIGHT = 1800;
    public static final int TILE_SIZE = 60;
    public static final int MAP_COLS = WORLD_WIDTH / TILE_SIZE;   // 40
    public static final int MAP_ROWS = WORLD_HEIGHT / TILE_SIZE;  // 30

    // 暂停遮罩颜色（半透明黑色）
    public static final Color PAUSE_OVERLAY = new Color(0, 0, 0, 160);

}
