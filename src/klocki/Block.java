package klocki;

import Ids.BlockId;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import classes.Handler;
import inputs.MouseInput;

public class Block {
    private int x,y,width,height,startingX,startingY;
    private Handler handler;
    public ArrayList<Brick> brickList = new ArrayList<>();

    private boolean isOnBoard;
    private boolean isBeingDragged=false;
    private boolean isPulled=false;
    private boolean resized =   false;
    private int brickPattern[][];

    public Block(int x, int y, BlockId blockId, boolean isOnBoard, Handler handler){
        this.x=x;
        this.y=y;
        this.startingX=x;
        this.startingY=y;
        this.isOnBoard=isOnBoard;
        this.handler=handler;
        int size=5;
        brickPattern = new int[size][size];
        createBlock(blockId,isOnBoard);
        setWidthAndHeight(brickPattern,32);
    }
    public void render(Graphics g){
        for(Brick brick:brickList){
            brick.render(g);
        }
        g.setColor(Color.red);
        g.drawRect(x,y,width,height);
    }
    public void tick(){
        if(isBeingDragged){
            int scale=48;
            if(!resized){
                resized=true;
                resizeBricks(scale);
                rearrangeBricksPositionsOnDrag(brickPattern,scale);
            }
            rearrangeBricksPositionsOnDrag(brickPattern,scale);
        }
        if(isPulled){
            int scale=32;
            if(resized){
                resized=false;
                resizeBricks(scale);
                rearrangeBricksPositionsOnPull();
            }
            isBeingDragged=false;
            isPulled=false;
        }
    }

    private void rearrangeBricksPositionsOnDrag(int patternBoard[][], int scale){
        Point[]brickPositions;
        int amountOfBricksInBlock=brickList.size(),size=patternBoard.length,bricksAlreadyAdded=0;
        brickPositions = new Point[amountOfBricksInBlock];
        for(int row=0;row<size;row++){
            for(int col=0;col<size;col++){
                if(patternBoard[row][col]==1){
                    brickPositions[bricksAlreadyAdded] = new Point(scale*row,scale*col);
                    bricksAlreadyAdded++;
                    if(amountOfBricksInBlock<=bricksAlreadyAdded) break;
                }
            }
        }
            int mouseX=MouseInput.mouseDraggedX;
            int mouseY=MouseInput.mouseDraggedY;
            int centerX=width/2;
            int centerY=height/2;
        boolean isNowOnBoard = isBlockOnBoard();

            int newX,newY;
            newX    =   mouseX-centerX;
            newY    =   mouseY-centerY;
            for(int i=0;i<bricksAlreadyAdded;i++){
                this.x=newX;
                this.y=newY;
            }
            if(isNowOnBoard){
                for(int i=0;i<bricksAlreadyAdded;i++){
                    Brick b = brickList.get(i);
                    int xx= Objects.requireNonNull(stickBlockToNearestLegalPosition(new Point(x,y))).x;
                    int yy= Objects.requireNonNull(stickBlockToNearestLegalPosition(new Point(x,y))).y;
                    b.setXY(brickPositions[i].x+xx,brickPositions[i].y+yy);
                }
            }else{
                for(int i=0;i<bricksAlreadyAdded;i++){
                    Brick b = brickList.get(i);
                    b.setXY(brickPositions[i].x+newX,brickPositions[i].y+newY);
                }
            }

    }

    private boolean isBlockOnBoard(){
        Rectangle r = new Rectangle(x,y,width,height);
        return handler.board.getBounds().contains(r);
    }

    private Point stickBlockToNearestLegalPosition(Point xy){

        for(int i=0;i<handler.board.getFieldsBoundsArray().length;i++){
            if(handler.board.getFieldsBoundsArray()[i].contains(xy)){
                int x = handler.board.getFieldsBoundsArray()[i].x;
                int y = handler.board.getFieldsBoundsArray()[i].y;
                return new Point(x,y);
            }
        }

        int x = handler.board.getFieldsBoundsArray()[0].x;
        int y = handler.board.getFieldsBoundsArray()[0].y;
        return new Point(x,y);
    }
    private void rearrangeBricksPositionsOnPull(){
        for(Brick b:brickList){
            b.rearrangeToStartingPositions();
        }
        rearrangeToStartingPositions();
    }
    private void resizeBricks(int scale){
        for(Brick b:brickList){
            b.setWidth(scale);
            b.setHeight(scale);
        }
        setWidthAndHeight(brickPattern,scale);
    }
    private void rearrangeToStartingPositions(){
        this.x=startingX;
        this.y=startingY;
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
    private void setWidthAndHeight(int patternBoard[][],int scale){
        int size = patternBoard.length;
        int amountOfBricksInBlock = brickList.size(), bricksAlreadyChecked=0;

        for(int row=0;row<size;row++){
            for(int col=0;col<size;col++){
                if(patternBoard[row][col]==1){
                    bricksAlreadyChecked++;
                    if(bricksAlreadyChecked==amountOfBricksInBlock){
                        width   =   (row+1)*scale;
                        height  =   (col+1)*scale;
                        break;
                    }
                }
            }
        }
    }
    public void setBeingDragged(boolean beingDragged) {
        isBeingDragged = beingDragged;
    }
    public boolean isBeingDragged() {
        return isBeingDragged;
    }
    public void setPulled(boolean pulled) {
        isPulled = pulled;
    }
    public boolean isPulled() {
        return isPulled;
    }
    public void setResized(boolean r){
        resized = r;
    }
    public boolean isResized(){
        return resized;
    }

    private void setBrickPositions(int patternBoard[][],int amountOfBricksInBlock,boolean isOnBoard){
        Point[]brickPositions;
        brickPositions = new Point[amountOfBricksInBlock];
        int size=patternBoard.length,bricksAlreadyAdded=0,scale;
        scale = isOnBoard ? 48 : 32;
        for(int row=0;row<size;row++){
            for(int col=0;col<size;col++){
                if(patternBoard[row][col]==1){
                    brickPositions[bricksAlreadyAdded] = new Point(scale*row,scale*col);
                    bricksAlreadyAdded++;
                    if(amountOfBricksInBlock<=bricksAlreadyAdded) break;
                }
            }
        }
        for(int i=0;i<amountOfBricksInBlock;i++){
            brickList.add(new Brick(brickPositions[i].x+x, brickPositions[i].y+y, isOnBoard));
        }
    }
    public Rectangle[]getBoundsArray(){
        int amountOfBricksInBlock = brickList.size();
        Rectangle[] brickBounds;
        brickBounds = new Rectangle[amountOfBricksInBlock];
        for(int i=0;i<amountOfBricksInBlock;i++){
            brickBounds[i] = brickList.get(i).getBounds();
        }
        return brickBounds;
    }

    private void printPatternBoard(int patternBoard[][],BlockId id){
        int size = patternBoard.length;
        System.out.println("-----------\t"+id+"\t------------");
        for(int row=0;row<size;row++){
            for(int col=0;col<size;col++){
                System.out.print(patternBoard[row][col]+" ");
            }
            System.out.println();
        }
        System.out.println("-----------------------");
        System.out.println();
    }
    private void createBlock(BlockId id,boolean isOnBoard){
        int size = 5;
        int amountOfBricksInBlock = 0;
        for(int col=0;col<size;col++){
            for(int row=0;row<size;row++){
                brickPattern[row][col]=0;
            }
        }
        switch (id){
            case a:
                amountOfBricksInBlock=1;
                brickPattern[0][0]=1;
                break;
            case b1:
                amountOfBricksInBlock=2;
                brickPattern[0][0]=1;brickPattern[0][1]=1;
                break;
            case b2:
                amountOfBricksInBlock=2;
                brickPattern[0][0]=1;
                brickPattern[1][0]=1;
                break;
            case c1:
                amountOfBricksInBlock=3;
                brickPattern[0][0]=1;brickPattern[0][1]=1;brickPattern[0][2]=1;
                break;
            case c2:
                amountOfBricksInBlock=3;
                brickPattern[0][0]=1;
                brickPattern[1][0]=1;
                brickPattern[2][0]=1;
                break;
            case c3:
                amountOfBricksInBlock=3;
                brickPattern[0][0]=1;
                brickPattern[1][0]=1;brickPattern[1][1]=1;
                break;
            case c4:
                amountOfBricksInBlock=3;
                brickPattern[0][0]=1;brickPattern[0][1]=1;
                brickPattern[1][0]=1;
                break;
            case c5:
                amountOfBricksInBlock=3;
                brickPattern[0][0]=1;brickPattern[0][1]=1;
                brickPattern[1][1]=1;
                break;
            case c6:
                amountOfBricksInBlock=3;
                brickPattern[0][1]=1;
                brickPattern[1][0]=1;brickPattern[1][1]=1;
                break;
            case d1:
                amountOfBricksInBlock=4;
                brickPattern[0][0]=1;brickPattern[0][1]=1;brickPattern[0][2]=1;brickPattern[0][3]=1;
                break;
            case d2:
                amountOfBricksInBlock=4;
                brickPattern[0][0]=1;
                brickPattern[1][0]=1;
                brickPattern[2][0]=1;
                brickPattern[3][0]=1;
                break;
            case d3:
                amountOfBricksInBlock=4;
                brickPattern[0][0]=1;
                brickPattern[1][0]=1;brickPattern[1][1]=1;brickPattern[1][2]=1;
                break;
            case d4:
                amountOfBricksInBlock=4;
                brickPattern[0][0]=1;brickPattern[0][1]=1;brickPattern[0][2]=1;
                brickPattern[1][0]=1;
                break;
            case d5:
                amountOfBricksInBlock=4;
                brickPattern[0][0]=1;brickPattern[0][1]=1;brickPattern[0][2]=1;
                brickPattern[1][2]=1;
                break;
            case d6:
                amountOfBricksInBlock=4;
                brickPattern[0][1]=1;
                brickPattern[1][1]=1;
                brickPattern[2][0]=1;brickPattern[2][1]=1;
                break;
            case d7:
                amountOfBricksInBlock=4;
                brickPattern[0][0]=1;brickPattern[0][1]=1;
                brickPattern[1][0]=1;brickPattern[1][1]=1;
                break;
            case d8:
                amountOfBricksInBlock=4;
                brickPattern[0][0]=1;brickPattern[0][1]=1;brickPattern[0][2]=1;
                brickPattern[1][1]=1;
                break;
            case d9:
                amountOfBricksInBlock=4;
                brickPattern[0][0]=1;
                brickPattern[1][0]=1;brickPattern[1][1]=1;
                brickPattern[2][0]=1;
                break;
            case d10:
                amountOfBricksInBlock=4;
                brickPattern[0][1]=1;
                brickPattern[1][0]=1;brickPattern[1][1]=1;brickPattern[1][2]=1;
                break;
            case d11:
                amountOfBricksInBlock=4;
                brickPattern[0][1]=1;
                brickPattern[1][0]=1;brickPattern[1][1]=1;
                brickPattern[2][1]=1;
                break;
            case e1:
                amountOfBricksInBlock=5;
                brickPattern[0][0]=1;brickPattern[0][1]=1;brickPattern[0][2]=1;brickPattern[0][3]=1;brickPattern[0][4]=1;
                break;
            case e2:
                amountOfBricksInBlock=5;
                brickPattern[0][0]=1;
                brickPattern[1][0]=1;
                brickPattern[2][0]=1;
                brickPattern[3][0]=1;
                brickPattern[4][0]=1;
                break;
            case e3:
                amountOfBricksInBlock=5;
                brickPattern[0][0]=1;
                brickPattern[1][0]=1;
                brickPattern[2][0]=1;brickPattern[2][1]=1;brickPattern[2][2]=1;
                break;
            case e4:
                amountOfBricksInBlock=5;
                brickPattern[0][0]=1;brickPattern[0][1]=1;brickPattern[0][2]=1;
                brickPattern[1][0]=1;
                brickPattern[2][0]=1;
                break;
            case e5:
                amountOfBricksInBlock=5;
                brickPattern[0][0]=1;brickPattern[0][1]=1;brickPattern[0][2]=1;
                brickPattern[1][2]=1;
                brickPattern[2][2]=1;
                break;
            case e6:
                amountOfBricksInBlock=5;
                brickPattern[0][2]=1;
                brickPattern[1][2]=1;
                brickPattern[2][0]=1;brickPattern[2][1]=1;brickPattern[2][2]=1;
                break;
            case f:
                amountOfBricksInBlock=9;
                brickPattern[0][0]=1;brickPattern[0][1]=1;brickPattern[0][2]=1;
                brickPattern[1][0]=1;brickPattern[1][1]=1;brickPattern[1][2]=1;
                brickPattern[2][0]=1;brickPattern[2][1]=1;brickPattern[2][2]=1;
                break;
            default:
                System.out.println("error creating block");
                break;
        }
        setBrickPositions(brickPattern,amountOfBricksInBlock,isOnBoard);
//        printPatternBoard(brickPattern,id);
    }
}
