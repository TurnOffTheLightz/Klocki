package classes;

import klocki.Block;
import klocki.Brick;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Board {
    private int x,y,width,height;
    private int[]rowToDelete,colToDelete;
    private int score=0,scoreCounter=0,coinFrames=0,scoreForCurrentBlock;

    private BufferedImage boardImg;
    private boolean[][]boolBoard;
    private boolean isNewBlockOnBoard = false;
    private boolean deleteAnimation;
    private boolean isRowBeingDeleted,isColBeingDeleted;
    private boolean addScore = false;
    public static boolean checkGameOver=false;
    public static boolean gameOverScreen = false;
    public static boolean newGame = false;
    private boolean gameOverInfo = false;
    private ArrayList<Brick> brickList   =   new ArrayList<>();
    private HUD hud;
    private Point []rowEffectPoint,colEffectPoint;

    private BufferedImage[]rowAnimation = new BufferedImage[7];
    private BufferedImage[]colAnimation = new BufferedImage[7];
    private int totalCounter=0,animationCounter=0,gameOverInfoCounter=0;
    private Point scoreAnimationXY;
    private Rectangle gameOverInfoRect= new Rectangle(-300,-100,1100,1100);


    Board(int x, int y, int w, int h){
        this.x=x;
        this.y=y;
        this.width=w;
        this.height=h;
        initBoard();
    }

    void render(Graphics g){
        renderBoardImg(g);
        renderAnimations(g);
    }

    private void initBoard(){
        int tabSize=5;
        rowToDelete = new int[tabSize];
        colToDelete = new int[tabSize];
        rowEffectPoint = new Point[tabSize];
        colEffectPoint = new Point[tabSize];
        for(int i=0;i<tabSize;i++){
            rowToDelete[i]=-1;
            colToDelete[i]=-1;
            rowEffectPoint[i] = new Point(-1,-1);
            colEffectPoint[i] = new Point(-1,-1);
        }
        try {
            boardImg = ImageIO.read(getClass().getResource("/board.png"));
            for(int i=0;i<7;i++){
                rowAnimation[i] = ImageIO.read(getClass().getResource("/deleteAnimation/rowAnimation-"+(i+1)+".png"));
                colAnimation[i] = ImageIO.read(getClass().getResource("/deleteAnimation/colAnimation-"+(i+1)+".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        playSound("newGame.wav");
        initBoolBoard();
    }
    private void renderBoardImg(Graphics g){
        g.drawImage(boardImg,x,y,width,height,null);
    }
    private void renderAnimations(Graphics g){
        if(deleteAnimation){
            if(isRowBeingDeleted){
                for (Point p : rowEffectPoint) {
                    if (p.x == -1 && p.y == -1) continue;
                    g.drawImage(rowAnimation[animationCounter], p.x, p.y, rowAnimation[animationCounter].getWidth(), rowAnimation[animationCounter].getHeight(), null);
                }
            }
            if(isColBeingDeleted){
                for(Point p : colEffectPoint){
                    if(p.x == -1 && p.y == -1) continue;
                    g.drawImage(colAnimation[animationCounter],p.x,p.y,colAnimation[animationCounter].getWidth(),colAnimation[animationCounter].getHeight(),null);
                }
            }
        }
    }
    void tick() {
        if(isNewBlockOnBoard){
            isNewBlockOnBoard=false;
            addScore=true;
            boolean rowDelete = isRowToDelete(),colDelete = isColToDelete();
            if(rowDelete||colDelete){
                playSound("rowOrColDeleted.wav");
                deleteAnimation=true;
                if(rowDelete) deleteRow();
                if(colDelete) deleteCol();
            }
        }
        if(addScore){
            scoreCounter++;
            Point p = scoreAnimationXY;
            int yBlocker=100,xBlocker=80,speed=3;
            if(p.x<=xBlocker){
                p.x=xBlocker;
            }else{
                p.x -= scoreCounter/speed;
            }
            if(p.x==xBlocker){
                p.y -= scoreCounter/speed;
            }
            if(p.y<yBlocker) p.y=yBlocker;
            if(scoreCounter%3==0){
                coinFrames++;
                if(coinFrames>=7) coinFrames=0;
            }
            if(p.x==xBlocker  &&  p.y==yBlocker){
                addScore=false;
                scoreCounter=0;
                scoreAnimationXY = new Point(-1,-1);
            }
        }
        if(deleteAnimation){
            totalCounter++;
            if(totalCounter%10==0){
                animationCounter++;
                if(animationCounter>6){
                    animationCounter=0;
                    totalCounter=0;
                    deleteAnimation=false;
                    if(isColBeingDeleted) isColBeingDeleted=false;
                    if(isRowBeingDeleted) isRowBeingDeleted=false;
                    for(int i=0;i<rowToDelete.length;i++){
                        rowToDelete[i]=-1;
                        colToDelete[i]=-1;
                        rowEffectPoint[i] = new Point(-1,-1);
                        colEffectPoint[i] = new Point(-1,-1);
                    }
                }
            }
        }
        if(checkGameOver&&(hud.blockList.size()!=3)){
            checkGameOver = false;
            if(isGameOver()){
                gameOverInfo=true;
            }
        }
        if(gameOverInfo){
            playSound("gameOver.wav");
            gameOverInfoCounter++;
            if(gameOverInfoRect.width>=481){
                gameOverInfoRect.x+=3.5;
                gameOverInfoRect.y+=1.05;
                gameOverInfoRect.width-=4;
                gameOverInfoRect.height-=4;
            }
            else if(gameOverInfoCounter>130){
                gameOverInfoRect.x+=10;
                gameOverInfoRect.y+=10;
            }

            if(gameOverInfoCounter>180){
                gameOverInfo=false;
                gameOverScreen=true;
            }
        }
    }
    private boolean isColToDelete(){
        int colCounter=0;
        for(int col=0;col<10;col++){
            for(int row=0;row<10;row++){
                if(!boolBoard[col][row]){
                    break;
                }
                if(row==9){
                    colToDelete[colCounter]=col;
                    colCounter++;
                }
            }
        }
        for (int col : colToDelete) {
            if (col != -1) {
                return true;
            }
        }
        return false;
    }
    private boolean isRowToDelete(){
        int rowCounter=0;
        for(int row=0;row<10;row++){
            for(int col=0;col<10;col++){
                if(!boolBoard[col][row]){
                    break;
                }
                if(col==9){
                    rowToDelete[rowCounter]=row;
                    rowCounter++;
                }
            }
        }
        for(int row:rowToDelete){
            if(row != -1){
                return true;
            }
        }
        return false;
    }

    private void deleteRow(){
        isRowBeingDeleted=true;
        for(int i=0;i<brickList.size();i++) {
            Brick b = brickList.get(i);
            for (int j=0;j<rowToDelete.length;j++) {
                int rToDelete = rowToDelete[j];
                if(rToDelete==-1)continue;
                if(rToDelete == b.getRow()) {
                    rowEffectPoint[j]  =   new Point(x,y+rowToDelete[j]*48);
                    for (int row = 0; row < 10; row++) {
                        if (row == rToDelete)
                            for (int col = 0; col < 10; col++) {
                                boolBoard[col][row] = false;
                            }
                        removeBrick(b);
                        for (int k = 0; k < hud.blockList.size(); k++) {
                            hud.blockList.get(k).removeBricksInRow(rToDelete);
                        }
                    }
                }
            }
        }
        int delta=0;
        for (int r : rowToDelete) {
            if (r != -1) {
                score += 100;
                delta += 100;
            }
        }
        scoreForCurrentBlock += delta;
    }
    private void deleteCol(){
        isColBeingDeleted=true;
        for(int i=0;i<brickList.size();i++){
            Brick b = brickList.get(i);
            for(int j=0;j<colToDelete.length;j++) {
                int cToDelete = colToDelete[j];
                if(cToDelete==-1) continue;
                if (cToDelete == b.getCol()) {
                    colEffectPoint[j]  =   new Point(x+colToDelete[j]*48,y);
                    for (int col = 0; col < 10; col++) {
                        if (col == cToDelete)
                            for (int row = 0; row < 10; row++) {
                                boolBoard[col][row] = false;
                            }
                        removeBrick(b);
                        for (int k = 0; k < hud.blockList.size(); k++) {
                            hud.blockList.get(k).removeBricksInCol(cToDelete);
                        }
                    }
                }
            }
        }
        int delta=0;
        for (int c : colToDelete) {
            if(c != -1){
                score+=100;
                delta += 100;
            }
        }
        scoreForCurrentBlock += delta;
    }

    public Rectangle getBounds(){
        return new Rectangle(x,y,width,height);
    }
    public Rectangle getGameOverInfoRect(){
        return this.gameOverInfoRect;
    }
    public int getScore(){
        return this.score;
    }
    public int getCoinFrames(){
        return this.coinFrames;
    }
    public boolean isAddingScore(){
        return this.addScore;
    }
    public int getScoreForCurrentBlock(){
        return this.scoreForCurrentBlock;
    }
    public Point getScoreAnimationXY(){
        return this.scoreAnimationXY;
    }
    public boolean getGameOverInfo(){
        return this.gameOverInfo;
    }

    public void acceptMove(Point brickPositions[],ArrayList<Brick>bList){
        playSound("brickPut.wav");
        for(Point p:brickPositions){
            boolBoard[p.x][p.y] =   true;
        }
        for(Brick b:bList){
            b.setRowAndCol();
        }
        this.score+= bList.size()*10;
        this.scoreForCurrentBlock = bList.size()*10;
        this.scoreAnimationXY = new Point(bList.get(0).getX(),bList.get(0).getY());
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
    public boolean isLegalMove(Point brickPositions[]){
        for(Point p:brickPositions){
            if(p.x==-1  &&  p.y==-1) continue;
            if(boolBoard[p.x][p.y]){
                return false;
            }
        }
        return true;
    }
    private boolean isGameOver(){
        ArrayList<Block> blocks = new ArrayList<>();  //3 that are always on hud

        for(int i=0;i<hud.blockList.size();i++){
            Block block = hud.blockList.get(i);
                if(block.isOnHud()){
                    blocks.add(block);
                }
        }

        Point[][] brickPosition = new Point[blocks.size()][];
        for(int i=0;i<blocks.size();i++){
            brickPosition[i] = new Point[blocks.get(i).brickList.size()];
            int bricksAlreadyAdded = 0;
            int size = blocks.get(i).brickPattern.length;
            for(int col=0;col<size;col++){
                for(int row=0;row<size;row++){
                    if(blocks.get(i).brickPattern[row][col]==1){
                        brickPosition[i][bricksAlreadyAdded] = new Point(col,row);
                        bricksAlreadyAdded++;
                        if(bricksAlreadyAdded==blocks.get(i).brickList.size()) break;
                    }
                }
            }
        }
        for(int i=0;i<blocks.size();i++){
            if(isAnyLegalMove(brickPosition[i])) return false;
        }
        return true;
    }
    private boolean isAnyLegalMove(Point brickPositions[]){
        //(0,0),(1,0),(2,0)->
        // przesuwam kształt o odpowiedni wektor i sprawdzam czy pola są puste,
        // jesli funkcja zwroci false to znaczy ze nie ma mozliwych ruchow dla danego kształtu
        /*
            1. przesuń o 1 w prawo->
                (0,1),(1,1),(2,1)   ...   (0,9),(1,9),(2,9)
                    :: wykonaj 10 razy, po czym->
            2. przesuń o 1 w dół->
                (1,0),(2,0).(3,0)
                    :: wykonaj 1 raz, po czym->
                        przesuń o 1 w prawo...
         */
        for(int row=0;row<10;row++){
            for(int col=0;col<10;col++){
                Point vector = new Point(row,col);
                for(int whichBrick=0;whichBrick<brickPositions.length;whichBrick++){
                    Point pTransform = brickPositions[whichBrick];

                    if(pTransform.x+vector.x<=9 && pTransform.y+vector.y<=9){
                        if(whichBrick==brickPositions.length-1){
                            Point checkPoint[] = new Point[brickPositions.length];
                            for(int i=0;i<brickPositions.length;i++){
                                Point p = brickPositions[i];
                                checkPoint[i]   =   new Point(p.y+vector.y,p.x+vector.x);
                            }
                            if(isLegalMove(checkPoint)){
//                                System.out.println(Arrays.toString(checkPoint) +"\t::\t"+isLegalMove(checkPoint)+"\n");
                                return true;
                            }
                        }
                    }else break;
                }
            }
        }
        return false;
    }

    public void startNewBoard(){
        initBoolBoard();
        brickList.clear();
        this.score=0;
        playSound("newGame.wav");
    }

    private void initBoolBoard(){
        int SIZE = 10;
        boolBoard = new boolean[SIZE][SIZE];
        for(int col = 0; col< SIZE; col++){
            for(int row = 0; row< SIZE; row++){
                boolBoard[row][col] =   false;
            }
        }
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
    public static synchronized void playSound(final String url) {
        // The wrapper thread is unnecessary, unless it blocks on the
// Clip finishing; see comments.
        new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream;
                inputStream = AudioSystem.getAudioInputStream(Main.class.getResourceAsStream("/sounds/" + url));

                clip.open(inputStream);

                if(url.equals("buttonMoved.wav")||url.equals("buttonPressed.wav")){
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    double gain = 0.2;
                    float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
                    gainControl.setValue(dB);
                }

                clip.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }).start();
    }
}
