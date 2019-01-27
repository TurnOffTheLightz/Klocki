package classes;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Board {
    private int x,y,width,height;
    private BufferedImage boardImg;
    private boolean[][]boolBoard;

    Board(int x, int y, int w, int h){
        this.x=x;
        this.y=y;
        this.width=w;
        this.height=h;
        createBoard();
    }
    private void createBoard(){
        try {
            boardImg = ImageIO.read(getClass().getResource("/board.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int SIZE = 10;
        boolBoard = new boolean[SIZE][SIZE];
        for(int col = 0; col< SIZE; col++){
            for(int row = 0; row< SIZE; row++){
                boolBoard[row][col] =   false;
            }
        }
    }
    public void render(Graphics g){
        g.drawImage(boardImg,x,y,width,height,null);
    }

    public void tick() {

    }
    public Rectangle getBounds(){
        return new Rectangle(x,y,width,height);
    }
    public boolean isLegalMove(Point brickPositions[]){
        for(Point p:brickPositions){
            if(!boolBoard[p.x][p.y]){
                return false;
            }
        }
        return true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    public Rectangle[]getFieldsBoundsArray(){
        Rectangle[]rectangles;
        int counter=0;
        rectangles=new Rectangle[100];
        for(int col=0;col<10;col++){
            for(int row=0;row<10;row++){
                rectangles[counter]   =   new Rectangle(x+row*48,y+col*48,48,48);
                counter++;
            }
        }
        return rectangles;
    }

}
