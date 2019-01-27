package graphics;

import java.awt.image.BufferedImage;

public class Sprite {
    private BufferedImage image;
    private int width,height;
    public Sprite(int x, int y,int width, int height, SpriteSheet sheet){
        this.image=sheet.getImage(x, y);
        this.width=width;
        this.height=height;
    }
}
