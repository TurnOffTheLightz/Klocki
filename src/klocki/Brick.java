package klocki;

import classes.Handler;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Brick {
    private int x;
    private int y;
    private int width;
    private int height;
    private int startingX;
    private int startingY;
    private int row;
    private int col;
    private boolean isOnBoard;
    private Handler handler;
    private Color color;
    private BufferedImage brickImg;

    Brick(int x, int y, boolean isOnBoard, Handler handler, BufferedImage brickImg){
        this.x=x;
        this.y=y;
        this.startingX=x;
        this.startingY=y;
        this.isOnBoard=isOnBoard;
        this.handler=handler;
        this.brickImg=brickImg;
        if(isOnBoard){
            this.width=48;
            this.height=48;
        }else{
            this.width=32;
            this.height=32;
        }
    }

    public void render(Graphics g) {
        g.drawImage(brickImg,x,y,width,height,null);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    public int getStartingX() {
        return startingX;
    }

    public int getStartingY() {
        return startingY;
    }


    public void setXY(int x,int y){
        this.x=x;
        this.y=y;
    }
    public void rearrangeToStartingPositions() {
        this.x=startingX;
        this.y=startingY;
    }
    public void moveWithMouse(Point newPos){
        this.x=newPos.x;
        this.y=newPos.y;
    }
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRowAndCol() {
        this.col = (x-handler.board.getX())/48;     //?
        this.row = (y-handler.board.getY())/48;     //?
    }

    public boolean isOnBoard() {
        return isOnBoard;
    }

    public void setOnBoard(boolean onBoard) {
        isOnBoard = onBoard;
    }
    public Rectangle getBounds(){
        return new Rectangle(x,y,width,height);
    }

}
