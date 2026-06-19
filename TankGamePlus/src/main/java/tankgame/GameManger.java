package tankgame;

import tankgame.db.GameSave;
import tankgame.db.GameStateConverter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameManger {
    int boundWidth;     // 屏幕宽度
    int boundHeight;    // 屏幕高度

    private static final GameManger instance = new GameManger();
    private final List<GameObject>gameObjects = new ArrayList<>();

    private TankPlayer tankPlayer;

    // 地图系统
    private GameMap gameMap;
    private Camera camera;
    private int worldWidth;
    private int worldHeight;

    private int status = Setting.RUNNING;
    private boolean paused = false;

    // 击杀计数
    private int killedEnemyCount = 0;
    private int totalEnemyCount = 0;

    public int getStatus(){
        return status;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void togglePause() {
        this.paused = !this.paused;
    }

    private GameManger(){}

    public static GameManger getInstance(){
        return instance;
    }
    public TankPlayer getPlayer(){
        return tankPlayer;
    }

    // 初始化游戏
    public void InitGame(int boundWidth, int boundHeight, int tankWidth, int tankHeight){
        this.boundWidth = boundWidth;
        this.boundHeight = boundHeight;
        this.worldWidth = Setting.WORLD_WIDTH;
        this.worldHeight = Setting.WORLD_HEIGHT;

        // 创建地图和摄像机
        this.gameMap = new GameMap(Setting.TILE_SIZE, worldWidth, worldHeight);
        this.camera = new Camera();

        gameObjects.clear();
        // 初始化玩家坦克位置（世界坐标：水平居中，底部偏上）
        int playerX = worldWidth / 2;
        int playerY = worldHeight - tankHeight - 120;
        tankPlayer = new TankPlayer(playerX, playerY, tankWidth, tankHeight);
        gameObjects.add(tankPlayer);

        // 初始化敌方坦克（世界坐标上半区域，避开墙体）
        int maxAttempts = 200;
        List<TankEnemy>enemies = new ArrayList<>();

        for(int i = 0;i < Setting.ENEMY_COUNT;i++){
            boolean placed = false;

            for(int attempt = 0;attempt < maxAttempts;attempt++){
                // 敌人生成在世界坐标上半区域，避开边框
                int temptX = (int)(Math.random()*(worldWidth - tankWidth - 120)) + 60;
                int temptY = (int)(Math.random()*(worldHeight/2 - 120)) + 60;

                TankEnemy tmpTank = new TankEnemy(temptX,temptY,tankWidth,tankHeight);
                // 是否与玩家坦克重叠
                if(tmpTank.getRect().intersects(tankPlayer.getRect())){
                    continue;
                }
                // 是否与已放置的敌方坦克重叠
                boolean overlapping = false;
                for(TankEnemy allEnemy:enemies){
                    if(tmpTank.getRect().intersects(allEnemy.getRect())){
                        overlapping = true;
                        break;
                    }
                }
                if(overlapping) continue;
                // 是否在墙体内部
                if(gameMap.isBlocked(tmpTank.getRect())) continue;
                // 没有重叠且不在墙内，放置成功
                enemies.add(tmpTank);
                placed = true;
                break;
            }
            if(!placed){
                System.out.println("没有空间放置第" + i + "个敌方坦克");
            }
        }
        gameObjects.addAll(enemies);
        totalEnemyCount = enemies.size();
        killedEnemyCount = 0;
        status = Setting.RUNNING;
        paused = false;
    }

    public void addBullet(Bullet bullet){
        gameObjects.add(bullet);
    }

    public void draw(Graphics g){
        if(status == Setting.RUNNING){
            Graphics2D g2d = (Graphics2D) g;
            // 保存原始变换
            java.awt.geom.AffineTransform originalTransform = g2d.getTransform();

            // 应用摄像机偏移（世界坐标 → 屏幕坐标）
            g2d.translate(-camera.getX(), -camera.getY());

            // 绘制地图（仅可见瓦片）
            gameMap.draw(g, camera.getX(), camera.getY(), boundWidth, boundHeight);

            // 绘制所有游戏对象（在世界坐标中）
            for(GameObject obj:gameObjects){
                ((InterfaceObject)obj).draw(g);
            }

            // 恢复原始变换（用于 UI 叠加层）
            g2d.setTransform(originalTransform);

            // === 左上角击杀计数 ===
            drawKillCount(g);
        }

        // 暂停遮罩（仅游戏进行中时显示）
        if (paused && status == Setting.RUNNING) {
            g.setColor(Setting.PAUSE_OVERLAY);
            g.fillRect(0, 0, boundWidth, boundHeight);
            drawCenteredText(g, "游戏暂停", 40, Font.BOLD, Color.WHITE, -80);
            drawCenteredText(g, "按 P 键或点击按钮继续", 20, Font.PLAIN, Color.LIGHT_GRAY, -30);
        }

        // 游戏胜利时
        if(status == Setting.WIN){
            drawWinScreen(g);
        }

        // 游戏失败时
        if(status == Setting.OVER){
            drawEndScreen(g, "GAME OVER", Color.RED);
        }
    }

    /** 左上角显示击杀进度 */
    private void drawKillCount(Graphics g) {
        String text = "击杀: " + killedEnemyCount + " / " + totalEnemyCount;
        Font font = new Font("微软雅黑", Font.BOLD, 18);
        g.setFont(font);
        // 描边（黑色）
        g.setColor(Color.BLACK);
        g.drawString(text, 11, 71);
        // 主体（白色）
        g.setColor(Color.WHITE);
        g.drawString(text, 10, 70);
    }

    /** 增强版胜利画面 */
    private void drawWinScreen(Graphics g) {
        // 半透明黑色遮罩
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, boundWidth, boundHeight);

        // 金色光芒背景框
        int boxW = 500;
        int boxH = 200;
        int boxX = (boundWidth - boxW) / 2;
        int boxY = (boundHeight - boxH) / 2;
        g.setColor(new Color(40, 30, 0, 200));
        g.fillRoundRect(boxX, boxY, boxW, boxH, 30, 30);
        g.setColor(new Color(255, 215, 0));
        g.drawRoundRect(boxX, boxY, boxW, boxH, 30, 30);
        g.drawRoundRect(boxX - 5, boxY - 5, boxW + 10, boxH + 10, 35, 35);

        // 大号 WIN 文字（带阴影）
        Font bigFont = new Font("Arial", Font.BOLD, 80);
        g.setFont(bigFont);
        FontMetrics fm = g.getFontMetrics();
        int textX = (boundWidth - fm.stringWidth("YOU WIN")) / 2;
        int textY = boxY + 90;
        // 阴影
        g.setColor(new Color(80, 50, 0));
        g.drawString("YOU WIN", textX + 3, textY + 3);
        // 主体金色
        g.setColor(new Color(255, 215, 0));
        g.drawString("YOU WIN", textX, textY);
        // 高光
        g.setColor(new Color(255, 255, 200));
        g.drawString("YOU WIN", textX, textY - 1);

        // 副标题：击杀统计
        String killInfo = "击毁敌方坦克: " + killedEnemyCount + " / " + totalEnemyCount;
        Font subFont = new Font("微软雅黑", Font.BOLD, 22);
        g.setFont(subFont);
        fm = g.getFontMetrics();
        int subX = (boundWidth - fm.stringWidth(killInfo)) / 2;
        g.setColor(Color.BLACK);
        g.drawString(killInfo, subX + 1, boxY + 140 + 1);
        g.setColor(Color.ORANGE);
        g.drawString(killInfo, subX, boxY + 140);

        // 重启提示
        Font hintFont = new Font("微软雅黑", Font.PLAIN, 20);
        g.setFont(hintFont);
        fm = g.getFontMetrics();
        String hint = "按 回车 键返回菜单";
        int hintX = (boundWidth - fm.stringWidth(hint)) / 2;
        g.setColor(Color.LIGHT_GRAY);
        g.drawString(hint, hintX, boxY + boxH + 40);
    }

    // 统一处理结束画面
    public void drawEndScreen(Graphics g, String title, Color titleColor) {
        // 半透明黑色遮罩
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, boundWidth, boundHeight);
        // 绘制标题
        Font titleFont = new Font("Arial", Font.BOLD, 60);
        g.setFont(titleFont);
        g.setColor(titleColor);
        FontMetrics fm = g.getFontMetrics();
        int x = (boundWidth - fm.stringWidth(title)) / 2;
        int y = (boundHeight - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(title, x, y);
        // 绘制重启提示
        Font hintFont = new Font("微软雅黑", Font.PLAIN, 24);
        g.setFont(hintFont);
        g.setColor(Color.LIGHT_GRAY);
        fm = g.getFontMetrics();
        int hx = (boundWidth - fm.stringWidth("请按回车回到菜单")) / 2;
        g.drawString("请按回车回到菜单", hx, y + 60);
    }

    private void drawCenteredText(Graphics g, String text, int fontSize, int fontStyle, Color color, int yOffset) {
        Font font = new Font("微软雅黑", fontStyle, fontSize);
        g.setFont(font);
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        int x = (boundWidth - fm.stringWidth(text)) / 2;
        int y = (boundHeight - fm.getHeight()) / 2 + fm.getAscent() + yOffset;
        g.drawString(text, x, y);
    }


    // 获取所有存活的坦克
    public List<Tank> getAllTanks(){
        List<Tank> tanks = new ArrayList<>();
        for(GameObject obj:gameObjects){
            if(obj instanceof Tank && ((Tank)obj).alive){
                tanks.add((Tank) obj);
            }
        }
        return tanks;
    }
    // 获取所有敌方坦克
    public List<TankEnemy> getAllEnemyTanks(){
        List<TankEnemy>enemies = new ArrayList<>();
        for(GameObject obj:gameObjects){
            if(obj instanceof TankEnemy && ((TankEnemy)obj).alive){
                enemies.add((TankEnemy) obj);
            }
        }
        return enemies;
    }

    public int getKilledEnemyCount() { return killedEnemyCount; }
    public int getTotalEnemyCount()  { return totalEnemyCount; }
    // 子弹击中坦克（改用血量系统）
    private void checkBulletTankCollision(Bullet bullet){
        List<GameObject> toRemove = new ArrayList<>();
        for(GameObject obj:new ArrayList<>(gameObjects)){
            if (!(obj instanceof Tank)) continue;
            if (obj == bullet.getShooter()) continue;

            Tank tank = (Tank) obj;

            // 敌方坦克的子弹不会伤害其他敌方坦克（禁止友军伤害）
            if (bullet.getShooter() instanceof TankEnemy && tank instanceof TankEnemy) {
                continue;
            }

            if (bullet.getRect().intersects(tank.getRect())) {
                tank.hp--;                    // 扣血
                toRemove.add(bullet);         // 子弹消失

                if (tank.hp <= 0) {
                    tank.alive = false;
                    toRemove.add(tank);

                    // 移除被消灭坦克的所有子弹
                    removeBulletsByShooter(tank);

                    // 玩家被击杀 → 游戏结束
                    if (tank instanceof TankPlayer) {
                        status = Setting.OVER;
                    }
                    // 玩家击杀敌人 → 计数+1
                    if (bullet.getShooter() instanceof TankPlayer) {
                        killedEnemyCount++;
                    }
                }
                break;  // 一枚子弹只打中一个目标
            }
        }
        gameObjects.removeAll(toRemove);
    }

    /** 移除某个坦克发射的所有子弹 */
    private void removeBulletsByShooter(Tank shooter) {
        List<GameObject> toRemove = new ArrayList<>();
        for (GameObject obj : gameObjects) {
            if (obj instanceof Bullet) {
                Bullet b = (Bullet) obj;
                if (b.getShooter() == shooter) {
                    toRemove.add(b);
                }
            }
        }
        gameObjects.removeAll(toRemove);
    }

    public void update(){
        if (paused) return;
        if(status == Setting.RUNNING){

            // 更新摄像机：跟随玩家坦克中心
            if (tankPlayer != null && tankPlayer.alive) {
                camera.update(
                    tankPlayer.x + Setting.TANK_WIDTH / 2,
                    tankPlayer.y + Setting.TANK_HEIGHT / 2,
                    boundWidth, boundHeight,
                    worldWidth, worldHeight
                );
            }

            for(GameObject obj: new ArrayList<>(gameObjects)){
                if(obj instanceof Tank){
                    // 让所有坦克动起来（传入世界尺寸和地图）
                    ((Tank)obj).move(worldWidth, worldHeight, gameMap);
                    // 处理玩家坦克碰撞
                    if(obj instanceof TankPlayer){
                        ((TankPlayer)obj).checkEnemyCollision(getAllEnemyTanks());
                    }
                    // 处理敌方坦克的碰撞与开火
                    if(obj instanceof TankEnemy){
                        ((TankEnemy)obj).collideEvade(getAllTanks());
                        ((InterfaceBullet)obj).fire();
                    }

                }
                // 处理子弹与坦克之间的碰撞
                if(obj instanceof Bullet){
                    Bullet bullet = (Bullet) obj;
                    bullet.move(worldWidth, worldHeight, gameMap);
                    // 子弹碰到墙体
                    if (!bullet.alive) {
                        gameObjects.remove(obj);
                        continue;
                    }
                    checkBulletTankCollision(bullet);
                    // 检验是否离开世界边界
                    if(bullet.isOutOfBounds(worldWidth, worldHeight)){
                        gameObjects.remove(obj);
                    }
                }
            }
        }
        if (getAllEnemyTanks().isEmpty() && tankPlayer.alive) {
            status = Setting.WIN;
        }
    }

    // 获取当前游戏状态（用于保存）
    public GameSave getCurrentGameSave() {
        GameSave save = new GameSave();
        save.setSaveName("自动存档 " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        save.setPlayerX(tankPlayer.x);
        save.setPlayerY(tankPlayer.y);
        save.setPlayerDirection(tankPlayer.direction);
        save.setScore(0);  // 如果有分数系统，这里设置
        save.setLevel(1);  // 如果有关卡系统，这里设置
        save.setStatus(Setting.RUNNING);

        // 使用 Gson 序列化敌方坦克状态为 JSON
        List<TankEnemy> enemies = getAllEnemyTanks();
        save.setEnemyData(GameStateConverter.enemiesToJson(enemies));
        save.setPlayTime(0); // 如果有计时器，这里设置

        return save;
    }

    // 从存档加载游戏
    public void loadFromSave(GameSave save) {
        // 初始化世界和地图
        this.worldWidth = Setting.WORLD_WIDTH;
        this.worldHeight = Setting.WORLD_HEIGHT;
        this.gameMap = new GameMap(Setting.TILE_SIZE, worldWidth, worldHeight);
        this.camera = new Camera();

        // 清空所有游戏对象
        gameObjects.clear();

        // 恢复玩家坦克
        tankPlayer = new TankPlayer(
                save.getPlayerX(),
                save.getPlayerY(),
                Setting.TANK_WIDTH,
                Setting.TANK_HEIGHT
        );
        tankPlayer.direction = save.getPlayerDirection();
        gameObjects.add(tankPlayer);

        // 恢复敌方坦克（使用 Gson 解析 JSON）
        List<TankEnemy> enemies = GameStateConverter.jsonToEnemies(save.getEnemyData());
        gameObjects.addAll(enemies);
        totalEnemyCount = enemies.size();
        killedEnemyCount = 0;

        status = Setting.RUNNING;
        paused = false;
    }
}
