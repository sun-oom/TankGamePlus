package tankgame;

import java.awt.*;

public class GameObject {
    public int x;
    public int y;
    protected int width;
    protected int height;
    protected int speed;
    public int direction;

    public GameObject(){}
    public GameObject(int x, int y, int width, int height, int speed){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }

    public Rectangle getRect(){
        return new Rectangle(x,y,width,height);
    }
}
