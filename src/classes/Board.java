package classes;

import klocki.Brick;
import sun.applet.Main;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Board {
    private int x,y,width,height,rowToDelete,colToDelete;

    private BufferedImage boardImg;
    private boolean[][]boolBoard;
    private boolean isNewBlockOnBoard = false;
    private ArrayList<Brick> brickList   =   new ArrayList<>();
    private HUD hud;


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
    private void drawNums(Graphics g){
        g.setFont(new Font("Courier", Font.ITALIC,9));
        g.setColor(Color.black);
        for(int i=0;i<10;i++){
            for(int k=0;k<10;k++){
                g.drawString((k+","+i),i*48+10+x,k*48+10+y);
            }
        }
    }

    public void tick() {
        if(isNewBlockOnBoard){
            isNewBlockOnBoard=false;
            if(isRowToDelete()){
                deleteRow();
            }
            if(isColToDelete()){
                deleteCol();
            }
        }
    }
    private boolean isColToDelete(){
        for(int col=0;col<10;col++){
            for(int row=0;row<10;row++){
                if(!boolBoard[col][row]){
                    break;
                }
                if(row==9){
                    colToDelete=col;
                    return true;
                }
            }
        }
        return false;
    }
    private boolean isRowToDelete(){
        for(int row=0;row<10;row++){
            for(int col=0;col<10;col++){
                if(!boolBoard[col][row]){
                    break;
                }
                if(col==9){
                    rowToDelete=row;
                    return true;
                }
            }
        }
        return false;
    }

    private void deleteRow(){
        for(int i=0;i<brickList.size();i++){
            Brick b = brickList.get(i);
            if(rowToDelete==b.getCol()){
                for(int row=0;row<10;row++){
                    if(row==rowToDelete)
                        for(int col=0;col<10;col++){
                                boolBoard[col][row] =   false;
                        }
                    removeBrick(b);
                    for(int j=0;j<hud.blockList.size();j++){
                        hud.blockList.get(j).removeBricksInRow(rowToDelete);
                    }
                }
            }
        }

    }
    private void deleteCol(){
        for(int i=0;i<brickList.size();i++){
            Brick b = brickList.get(i);
            if(colToDelete==b.getRow()){
                for(int col=0;col<10;col++){
                    if(col==colToDelete)
                        for(int row=0;row<10;row++){
                            boolBoard[col][row] =   false;
                        }
                    removeBrick(b);
                    for(int j=0;j<hud.blockList.size();j++){
                        hud.blockList.get(j).removeBricksInCol(colToDelete);
                    }
                }
            }
        }
    }

    public Rectangle getBounds(){
        return new Rectangle(x,y,width,height);
    }
    public boolean isLegalMove(Point brickPositions[]){
        for(Point p:brickPositions){
            if(boolBoard[p.x][p.y]){
                return false;
            }
        }
        return true;
    }

    public void acceptMove(Point brickPositions[],ArrayList<Brick>bList){

        for(Point p:brickPositions){
            boolBoard[p.x][p.y] =   true;
        }
        for(Brick b:bList)b.setRowAndCol();
        addBricksToBoard(bList);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    private void addBricksToBoard(ArrayList<Brick> bList){
        isNewBlockOnBoard=true;
       brickList.addAll(bList);
    }
    private void removeBrick(Brick b){
        brickList.remove(b);
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

    public void printPatternBoard(){

        int size = boolBoard.length;
        System.out.println("-----------------------");
        for(int row=0;row<size;row++){
            for(int col=0;col<size;col++){
                if(boolBoard[col][row]) System.out.print("1 ");
                else System.out.print("0 ");
            }
            System.out.println();
        }
        System.out.println("-----------------------");
        System.out.println();
    }
    public void addHUD(HUD hud){
        this.hud=hud;
    }
    public synchronized void playSound(final String url) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream;
                    inputStream = AudioSystem.getAudioInputStream(
                            Main.class.getResourceAsStream("/audio/" + url));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }


}
