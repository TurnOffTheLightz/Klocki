package graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpriteSheet {
    private BufferedImage map;
    public SpriteSheet(String path){
        try {
            map = ImageIO.read(getClass().getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getImage(int x, int y){
        return map.getSubimage(x*64-64,y*64,64,64);
    }
}
