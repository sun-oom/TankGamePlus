package tankgame;

import java.awt.*;
import java.util.List;

public class TankPlayer extends Tank implements InterfaceBullet{
    private boolean active = false;
    private boolean boosted = false;   // 是否处于加速状态
    private long lastFireTime = 0;     // 上次发射时间（毫秒）
    int oldX,oldY;
    public TankPlayer(int x, int y, int tankWidth, int tankHeight){
        super(x,y,tankWidth,tankHeight,Setting.PLAYER_SPEED, Color.green, Setting.PLAYER_HP);
        direction = Setting.UP;
    }

    /** 开启/关闭加速 */
    public void setBoosted(boolean boosted){
        this.boosted = boosted;
        this.speed = boosted ? Setting.PLAYER_BOOST_SPEED : Setting.PLAYER_SPEED;
    }

    public boolean isBoosted(){
        return boosted;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    @Override
    public void move(int boundWidth, int boundHeight, GameMap map) {
        if(active){
            oldX = x;
            oldY = y;

            switch (direction){
                case Setting.UP:
                    y -= speed;
                    break;
                case Setting.DOWN:
                    y += speed;
                    break;
                case Setting.LEFT:
                    x -= speed;
                    break;
                case Setting.RIGHT:
                    x += speed;
                    break;
            }

            // 限制坦克不跑出世界边界
            int maxX = boundWidth - getRect().width;
            int maxY = boundHeight - getRect().height;
            if(x < 0) x = 0;
            if(x > maxX) x = maxX;
            if(y < 0) y = 0;
            if(y > maxY) y = maxY;

            // 墙体碰撞：回退到移动前位置
            if (map.isBlocked(getRect())) {
                x = oldX;
                y = oldY;
            }
        }
    }

    @Override
    public void fire() {
        // 冷却时间检查：防止按住空格键连射
        long now = System.currentTimeMillis();
        if (now - lastFireTime < Setting.PLAYER_FIRE_COOLDOWN) {
            return;
        }
        lastFireTime = now;
        Bullet bullet = new Bullet(this);
        GameManger.getInstance().addBullet(bullet);
    }

    // 玩家与敌方坦克放重叠(碰撞后改变方向)
    public void checkEnemyCollision(List<TankEnemy> enemies){
        for(TankEnemy enemy:enemies){
            if(this.getRect().intersects(enemy.getRect())){
                // 回退到移动前位置
                x = oldX;
                y = oldY;
                // 方向变为反向
                direction = Setting.OppositeDir[direction];
                break;
            }
        }
    }

    /** 重写绘制：加速时在坦克周围绘制光环提示 */
    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (boosted && alive) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(0, 255, 255, 150));
            g2d.setStroke(new BasicStroke(2.5f));
            int margin = 4;
            g2d.drawOval(x - margin, y - margin, width + margin * 2, height + margin * 2);
            g2d.setStroke(new BasicStroke(1f));
            // 上方小字
            g.setColor(Color.CYAN);
            g.setFont(new Font("微软雅黑", Font.BOLD, 11));
            g.drawString("加速", x + 2, y - 12);
        }
    }
}
