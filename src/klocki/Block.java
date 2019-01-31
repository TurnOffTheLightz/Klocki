package klocki;

import Ids.BlockId;
import classes.Board;
import classes.Handler;
import inputs.MouseInput;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Block {
    private int x,y,width,height,startingX,startingY,
            effectStartX=800,effectStartY=596,
                counter=0;
    private Handler handler;
    public ArrayList<Brick> brickList = new ArrayList<>();

    public static final int
                    BLUE=0,
                    RED=1,
                    GREEN=2,
                    YELLOW=3,
                    PURPLE=4;

    private boolean isOnHud=true;
    private boolean isOnBoard;
    private boolean isBeingDragged=false;
    private boolean isPulled=false;
    private boolean resized =   false;
    private boolean newBlockAnimation   =    true;

    private BufferedImage brickImg;

    public int brickPattern[][];
    private Point brickXYPositions [];
    private Point lastPoint;

    public Block(int x, int y, BlockId blockId, boolean isOnBoard, Handler handler,int COLOR){
        this.x=x;
        this.y=y;
        this.startingX=x;
        this.startingY=y;
        this.isOnBoard=isOnBoard;
        this.handler=handler;
        brickImg = getImageFromColor(COLOR);
        int size=5;
        brickPattern = new int[size][size];
        createBlock(blockId,isOnBoard);
        brickXYPositions = new Point[brickList.size()];
        setWidthAndHeight(brickPattern,32);
    }
    public void render(Graphics g){
        for(Brick brick:brickList){
            brick.render(g);
        }
    }
    public void tick(){
        if(newBlockAnimation){
            effectStartX-=20;
            for(Brick brick:brickList){
                brick.setX(effectStartX);
                if(effectStartX<brick.getStartingX()){
                    for(Brick b : brickList) b.setX(b.getStartingX());
                    Board.checkGameOver=true;
                    newBlockAnimation=false;
                    break;
                }
            }
        }
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
            isOnBoard   =   checkIfIsLegalMove();


            isBeingDragged=false;
            isPulled=false;

            if(isOnBoard){
                isOnHud=false;
                resized = false;
                handler.hud.insertNewRandomBlock(new Point(startingX,startingY));
                handler.board.acceptMove(brickXYPositions,brickList);
                width=0;
                height=0;
            }else{
                int scale=32;
                resized=false;
                resizeBricks(scale);
                rearrangeBricksPositionsOnPull();
            }
        }
    }
    private boolean checkIfIsLegalMove(){
        for(Rectangle r:getBoundsArray()){
            if(!handler.board.getBounds().contains(r)){
                return false;
            }
        }
        setBrickXYPositions();
        return handler.board.isLegalMove(brickXYPositions);
    }
    private void setBrickXYPositions(){
        for(int i=0;i<brickList.size();i++){
            Brick b = brickList.get(i);
            int x = (b.getX()-handler.board.getX())/48;
            int y = (b.getY()-handler.board.getY())/48;
            brickXYPositions[i] = new Point(x,y);
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

        int newX,newY;
        newX    =   mouseX-centerX;
        newY    =   mouseY-centerY;
        this.x=mouseX-width/2;
        this.y=mouseY-height/2;
        boolean isNowOnBoard = isMouseOnBoard(new Point(mouseX,mouseY));
        if(isNowOnBoard){

                for(int i=0;i<bricksAlreadyAdded;i++){
                    Brick b = brickList.get(i);
                    int xx= Objects.requireNonNull(stickBlockToNearestLegalPosition(new Point(mouseX,mouseY))).x;
                    int yy= Objects.requireNonNull(stickBlockToNearestLegalPosition(new Point(mouseX,mouseY))).y;
                    b.setXY(brickPositions[i].x+xx,brickPositions[i].y+yy);
                }

        }else{
                for(int i=0;i<bricksAlreadyAdded;i++){
                    Brick b = brickList.get(i);
                    b.setXY(brickPositions[i].x+newX,brickPositions[i].y+newY);
                }
        }
    }

    private boolean isMouseOnBoard(Point mouseXY){
        return handler.board.getBounds().contains(mouseXY);
    }

    private Point stickBlockToNearestLegalPosition(Point xy){
        Rectangle boardRect = handler.board.getBounds();


        for(int i=0;i<handler.board.getFieldsBoundsArray().length;i++){
            if(handler.board.getFieldsBoundsArray()[i].contains(xy)){
                int x = handler.board.getFieldsBoundsArray()[i].x;
                int y = handler.board.getFieldsBoundsArray()[i].y;
                if(x+width<=boardRect.x+ boardRect.width    &&
                        y+height<=boardRect.y+boardRect.height  &&
                        x>=boardRect.x  &&
                        y>=boardRect.y)
                {
                    lastPoint = new Point(x,y);
                    return new Point(x,y);
                }
            }
        }
        if(lastPoint==null)lastPoint    =   new Point(handler.board.getFieldsBoundsArray()[0].x,handler.board.getFieldsBoundsArray()[0].y);

        return lastPoint;
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

        int rowCounter=0,colCounter=0;
        for(int row=0;row<size;row++){
            for(int col=0;col<size;col++){
                if(patternBoard[row][col]==1){
                    if(row>rowCounter) rowCounter=row;
                    if(col>colCounter) colCounter=col;
                    bricksAlreadyChecked++;
                    if(bricksAlreadyChecked==amountOfBricksInBlock){
                        width   =   (rowCounter+1)*scale;
                        height  =   (colCounter+1)*scale;
                        break;
                    }
                }
            }
        }
    }
    private void removeBrick(Brick brickToRemove) {
        brickList.remove(brickToRemove);
    }
    public void removeBricksInRow(int rowToDelete){
        for(int i=0;i<brickList.size();i++){
            Brick b = brickList.get(i);
            if(rowToDelete==b.getRow()  &&  isOnBoard){
                removeBrick(b);
            }
        }
        if(brickList.isEmpty()) die();
    }
    public void removeBricksInCol(int colToDelete){
        for(int i=0;i<brickList.size();i++){
            Brick b = brickList.get(i);
            if(colToDelete==b.getCol()  &&  isOnBoard){
                removeBrick(b);
            }
        }
        if(brickList.isEmpty()) die();
    }
    private void die(){
        handler.hud.removeBlock(this);
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
    public boolean isOnBoard(){
        return isOnBoard;
    }
    public boolean isOnHud() {
        return isOnHud;
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
            brickList.add(new Brick(brickPositions[i].x+x, brickPositions[i].y+y, isOnBoard,handler,brickImg));
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
    }
    private BufferedImage getImageFromColor(int COLOR){
        String color=null;
        if(COLOR==BLUE){
            color = "blue";
        }else if(COLOR==RED){
            color = "red";
        }else if(COLOR==GREEN){
            color = "green";
        }else if(COLOR==YELLOW){
            color = "yellow";
        }else if(COLOR==PURPLE){
            color = "purple";
        }
        return loadImage("/bricks/"+color+".png");
    }
    private BufferedImage loadImage(String path){
        try {
            return ImageIO.read(getClass().getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
