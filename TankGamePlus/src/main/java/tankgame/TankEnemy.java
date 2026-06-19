package tankgame;

import java.awt.*;
import java.util.List;

public class TankEnemy extends Tank implements InterfaceBullet{

    public TankEnemy(int x, int y, int tankWidth, int tankHeight){
        super(x,y,tankWidth,tankHeight,Setting.ENEMY_SPEED,Color.red, Setting.ENEMY_HP);
        direction = (int)(Math.random()*4);// Math.random()返回[0.0, 1.0)的double值
    }

    private int moveCounter = 0;
    private int oldX,oldY;

    /*
    moveCounter 从 0 开始递增（每次调用 move() 就 +1）
    当 moveCounter 大于 70 时，就随机改变一次方向
    改变方向后，moveCounter 重置为 0，重新计数
    */
    @Override
    public void move(int boundWidth, int boundHeight, GameMap map) {
        oldX = x;
        oldY = y;

        if(moveCounter++ > 70){
            direction = (int) (Math.random()*4);
            moveCounter = 0;
        }

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
                x +=speed;
                break;
        }

        // 限制坦克不跑出世界边界
        int maxX = boundWidth - getRect().width;
        int maxY = boundHeight - getRect().height;
        if(x < 0) x = 0;
        if(x > maxX) x = maxX;
        if(y < 0) y = 0;
        if(y > maxY) y = maxY;

        // 碰到墙体：回退位置并随机换方向
        if (map.isBlocked(getRect())) {
            x = oldX;
            y = oldY;
            int oldDirection = direction;
            while (oldDirection == direction) {
                direction = (int) (Math.random() * 4);
            }
        }

        // 碰到世界边界：随机换方向
        boolean toughBound = (x == 0||x == maxX||y == 0||y == maxY);

        if(toughBound){
            int oldDirection = direction;
            while(oldDirection == direction){// 随机方向，与原来不一致
                direction = (int) (Math.random()*4);
            }
        }
    }

    @Override
    public void fire() {
        if(Math.random()<0.02){// 只有%2的概率敌方坦克发射子弹
            Bullet bullet = new Bullet(this);
            GameManger.getInstance().addBullet(bullet);
        }
    }
    // 避免与其他坦克的重叠
    public void collideEvade(List<Tank> allTanks){
        for(Tank otherTank:allTanks){
            if(otherTank == this){
                continue;
            }
            if(this.getRect().intersects(otherTank.getRect())){
                x = oldX;
                y = oldY;
                direction = Setting.OppositeDir[direction];
                return;
            }
        }
    }
}
