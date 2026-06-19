package tankgame;

import java.awt.*;

public interface InterfaceObject {
    void draw(Graphics g);
    void move(int boundWidth, int boundHeight, GameMap map);
}
