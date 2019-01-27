package klocki;

import java.awt.*;

public class Brick {
    private int x,y,width,height;
    private int startingX;
    private int startingY;
    private boolean isOnBoard;

    Brick(int x, int y, boolean isOnBoard){
        this.x=x;
        this.y=y;
        this.startingX=x;
        this.startingY=y;
        this.isOnBoard=isOnBoard;
        if(isOnBoard){
            this.width=48;
            this.height=48;
        }else{
            this.width=32;
            this.height=32;
        }
    }

    public void render(Graphics g) {
        g.setColor(Color.blue);
        g.fillRect(x,y,width,height);
        g.setColor(Color.black);
        g.drawRect(x,y,width,height);
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

    public void setXY(int x,int y){
        this.x=x;
        this.y=y;
    }
    public int getStartingX() {
        return startingX;
    }

    public int getStartingY() {
        return startingY;
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
