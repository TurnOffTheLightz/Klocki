package classes;

import Ids.BlockId;
import klocki.Block;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class HUD {

    private int x,y,width,height;
    private BufferedImage hudImg;
    public ArrayList<Block> blockList = new ArrayList<>();
    private ArrayList<BlockId>blockIds = new ArrayList<>(Arrays.asList(BlockId.values()));  //copying all BlockId enum values into ArrayList
    private Handler handler;

    public boolean newRand = false;

    HUD(int x, int y, int width, int height,Handler handler){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.handler=handler;
        init();
    }

    public void render(Graphics g) {
        g.drawImage(hudImg,x,y,width,height,null);
        g.setColor(Color.black);
        g.drawRect(x-1,y-1,width+2,height+2);

        for(Block block:blockList){
            block.render(g);
        }
    }
    public void tick(){
        for(int i=0;i<blockList.size();i++){
            blockList.get(i).tick();
        }
    }

    public void addBlock(Block block){
        blockList.add(block);
    }
    public void removeBlock(Block block){
        blockList.remove(block);
    }
    private void init(){
        Random rand = new Random();
        try {
            hudImg = ImageIO.read(getClass().getResource("/hud.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<3;i++){
            int index = rand.nextInt(blockIds.size());
            BlockId id = blockIds.get(index);
            Point point = nextHudPositionPoint(i);
            addBlock(new Block(point.x,point.y,id,false,handler));
        }
    }
    private Point nextHudPositionPoint(int whichOne){
        return new Point(x+whichOne*200+40,y+25);
    }
    public Rectangle getBounds(){
        return new Rectangle(x,y,width,height);
    }
    public void insertNewRandomBlock(Point pointOfLastRemovedBlock){
        Random rand = new Random();
        int index = rand.nextInt(blockIds.size());
        BlockId id = blockIds.get(index);
        addBlock(new Block(pointOfLastRemovedBlock.x,pointOfLastRemovedBlock.y,id,false,handler));
    }
}
