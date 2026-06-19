package tankgame;

import java.awt.*;

public abstract class Tank extends GameObject implements InterfaceObject{
    protected Color color;
    public boolean alive = true;
    public int hp;          // 当前血量
    public int maxHp;       // 最大血量

    public Tank(int x, int y, int width, int height, int speed, Color color, int maxHp){
        super(x, y , width, height, speed);
        this.color = color;
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    public Point getFirePoint(){
        Point point = new Point();
        int gunLength = Setting.TANK_HEIGHT/2;
        switch (direction){
            case Setting.LEFT:
                point.x = this.x - gunLength;
                point.y = this.y + (Setting.TANK_HEIGHT - Setting.BULLET_HEIGHT)/2;
                break;
            case Setting.RIGHT:
                point.x = this.x + Setting.TANK_WIDTH + gunLength;
                point.y = this.y + (Setting.TANK_HEIGHT - Setting.BULLET_HEIGHT)/2;
                break;
            case Setting.UP:
                point.x = this.x + (Setting.TANK_WIDTH - Setting.BULLET_WIDTH)/2;
                point.y = this.y + gunLength;
                break;
            case Setting.DOWN:
                point.x = this.x + (Setting.TANK_WIDTH - Setting.BULLET_WIDTH)/2;
                point.y = this.y + Setting.TANK_HEIGHT + gunLength;
                break;
        }
        return point;
    }

    // 绘制坦克
    @Override
    public void draw(Graphics g){
        if(!alive){
            return;
        }

        // 履带
        g.setColor(Setting.WHEEL_COLOR);
        int wheel = width/5;
        int bodyW,bodyH;
        if(direction == Setting.UP || direction == Setting.DOWN){
            bodyW = width - wheel*2;
            bodyH = height*4/5;
            g.fill3DRect(x,y,wheel,height,false);
            g.fill3DRect(x + width - wheel,y,wheel,height,false);
        }else{
            bodyW = width*4/5;
            bodyH = height - wheel*2;
            g.fill3DRect(x,y,width,wheel,false);
            g.fill3DRect(x,y + height - wheel,width,wheel,false);
        }

        // 车身
        g.setColor(color);
        g.fill3DRect(x+(width-bodyW)/2,y+(height-bodyH)/2,bodyW,bodyH,false);

        // 炮台
        g.setColor(color.darker());
        int deck = Math.min(width,height)*3/5;
        g.fillOval(x + (width - deck)/2, y + (height - deck)/2,deck,deck);

        // 炮管
        g.setColor(Setting.GUN_COLOR);
        int gun = Setting.TANK_HEIGHT;
        int gunW = width/10;
        int gunX = x + width/2 - gunW/2;
        int gunY = y + height/2 - gunW/2;

        switch (direction){
            case Setting.UP:
                g.fillRect(gunX,gunY-gun,gunW,gun);
                break;
            case Setting.DOWN:
                g.fillRect(gunX,gunY,gunW,gun);
                break;
            case Setting.LEFT:
                g.fillRect(gunX-gun,gunY,gun,gunW);
                break;
            case Setting.RIGHT:
                g.fillRect(gunX, gunY, gun, gunW);
        }

        // === 血条（仅玩家坦克显示）===
        if (hp < maxHp && this instanceof TankPlayer) {  // 满血时不显示血条，保持画面干净
            int barWidth = width + 8;
            int barHeight = 5;
            int barX = x - 4;
            int barY = y - 8;

            // 血条背景
            g.setColor(Color.DARK_GRAY);
            g.fillRect(barX, barY, barWidth, barHeight);

            // 血条血量（颜色根据血量比例变化）
            double ratio = (double) hp / maxHp;
            if (ratio > 0.5) {
                g.setColor(Color.GREEN);
            } else if (ratio > 0.25) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.RED);
            }
            g.fillRect(barX, barY, (int) (barWidth * ratio), barHeight);

            // 血条边框
            g.setColor(Color.BLACK);
            g.drawRect(barX, barY, barWidth, barHeight);
        }
    }
}
