package tankgame;

import java.awt.*;

public class Bullet extends GameObject implements InterfaceObject{
    private Tank shooter;
    /** 子弹是否存活（碰到墙体后变为 false） */
    public boolean alive = true;

    public Bullet(Tank shooter){
        super(shooter.getFirePoint().x, shooter.getFirePoint().y,Setting.BULLET_WIDTH,Setting.BULLET_HEIGHT,Setting.BULLET_SPEED);
        this.shooter = shooter;
        this.direction = shooter.direction;
    }

    public Tank getShooter(){
        return shooter;
    }

    // 判断子弹是否射出边界
    public boolean isOutOfBounds(int boundWidth, int boundHeight){
        return x < 0 || x >boundWidth || y < 0 || y > boundHeight;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.yellow);
        g.fillOval(x,y,width,height);
    }

    @Override
    public void move(int boundWidth, int boundHeight, GameMap map) {
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
        // 碰到墙体则子弹销毁
        if (map.isBlocked(getRect())) {
            alive = false;
        }
    }
}
