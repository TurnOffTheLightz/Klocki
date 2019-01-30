package classes;


import music.MusicPlay;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Handler {

    public Board board;
    public HUD hud;
    private BufferedImage highScoreButtonImg;
    private BufferedImage musicOnButtonImg;
    private BufferedImage musicOffButtonImg;
    private BufferedImage newGameButtonImg;

    private BufferedImage playAgainButtonImg;
    private BufferedImage exitButtonImg;
    //highlited:
    private BufferedImage newGameButtonHighlitedImg;
    private BufferedImage playAgainButtonHighlitedImg;
    private BufferedImage exitButtonHighlitedImg;
    //other
    private BufferedImage[] coinImg;
    private BufferedImage gameOverInfoImg;
    private final Point //render points
            highScoreP = new Point(-25,-25),
            musicP = new Point(0,190),
            newGameP = new Point(-50,380),

            playAgainP = new Point(205,255),
            exitP = new Point(260,450);
    private boolean newGameHighlited = false;
    private boolean exitHighlited=false;
    private boolean playAgainHighlited=false;
    private boolean music = true,startMusic=true;

    private MusicPlay backgroundMusic;

    Handler(Board b) {
        this.board = b;
        loadAllImages();
        backgroundMusic = new MusicPlay(MusicPlay.class.getResource("/sounds/soundtrack.wav"));
    }

    public void render(Graphics g) {
        if(!Board.gameOverScreen){
            board.render(g);
            hud.render(g);
        }
        renderVisual(g);
    }
    private void renderVisual(Graphics g){
        if(!Board.gameOverScreen){
            g.drawImage(highScoreButtonImg,highScoreP.x,highScoreP.y,highScoreButtonImg.getWidth(),highScoreButtonImg.getHeight(),null);
            if(!newGameHighlited){
                g.drawImage(newGameButtonImg,newGameP.x,newGameP.y,newGameButtonImg.getWidth(),newGameButtonImg.getHeight(),null);
            }else{
                g.drawImage(newGameButtonHighlitedImg,newGameP.x,newGameP.y,newGameButtonHighlitedImg.getWidth(),newGameButtonHighlitedImg.getHeight(),null);
            }
            if(music){
                g.drawImage(musicOnButtonImg,musicP.x,musicP.y,musicOnButtonImg.getWidth(),musicOnButtonImg.getHeight(),null);
            }else{
                g.drawImage(musicOffButtonImg,musicP.x,musicP.y,musicOffButtonImg.getWidth(),musicOffButtonImg.getHeight(),null);
            }
            g.setColor(Color.white);
            g.setFont(new Font("Courier",Font.BOLD,26));
            g.drawString(Integer.toString(board.getScore()),120,107);
            if(board.isAddingScore()){
                    g.setColor(Color.RED);
                    g.setFont(new Font("Courier",Font.BOLD,80));
                    Point p = board.getScoreAnimationXY();
                    int offset=0, currScore = board.getScoreForCurrentBlock();
                    if(currScore>=100){
                        offset=60;
                    }
                    g.drawString("+"+Integer.toString(currScore),p.x,p.y);
                    BufferedImage coin = coinImg[board.getCoinFrames()];
                    g.drawImage(coin,p.x+140+offset,p.y-58,coin.getWidth()*2,coin.getHeight()*2,null);
            }

            if(board.getGameOverInfo()){
                Rectangle r = board.getGameOverInfoRect();
                g.drawImage(gameOverInfoImg,r.x,r.y,r.width,r.height,null);
            }
        }else{
            g.drawImage(highScoreButtonImg,250,100,highScoreButtonImg.getWidth(),highScoreButtonImg.getHeight(),null);
            g.setColor(Color.white);
            g.setFont(new Font("Courier",Font.BOLD,26));
            g.drawString(Integer.toString(board.getScore()),380,232);
            if(!playAgainHighlited){
                g.drawImage(playAgainButtonImg,playAgainP.x,playAgainP.y,playAgainButtonImg.getWidth(),playAgainButtonImg.getHeight(),null);
            }else{
                g.drawImage(playAgainButtonHighlitedImg,playAgainP.x,playAgainP.y,playAgainButtonHighlitedImg.getWidth(),playAgainButtonHighlitedImg.getHeight(),null);
            }
            if(!exitHighlited){
                g.drawImage(exitButtonImg,exitP.x,exitP.y,exitButtonImg.getWidth(),exitButtonImg.getHeight(),null);
            }else{
                g.drawImage(exitButtonHighlitedImg,exitP.x,exitP.y,exitButtonHighlitedImg.getWidth(),exitButtonHighlitedImg.getHeight(),null);
            }
        }
    }

    public void tick() {
        if(!Board.gameOverScreen){
            board.tick();
            hud.tick();
        }else{
            if(Board.newGame){
                Board.newGame=false;
                Board.gameOverScreen=false;
                board.startNewBoard();
                hud.startNewHud();
            }
        }
        if(Board.newGame){
            Board.newGame=false;
            board.startNewBoard();
            hud.startNewHud();
        }
        if(music&&startMusic){
            startMusic=false;
            backgroundMusic.loop();
        }else if(!startMusic&&!music){
            startMusic = true;
            backgroundMusic.stop();
        }
    }
    public void addHud(HUD hud){
        this.hud=hud;
    }

    private void loadAllImages(){
        highScoreButtonImg = loadImage("/buttons/highScoreButton.png");
        musicOnButtonImg = loadImage("/buttons/musicOnButton.png");
        musicOffButtonImg = loadImage("/buttons/musicOffButton.png");
        newGameButtonImg = loadImage("/buttons/newGameButton.png");
        playAgainButtonImg = loadImage("/buttons/playAgainButton.png");
        exitButtonImg = loadImage("/buttons/exitButton.png");
        //highlited:
        newGameButtonHighlitedImg = loadImage("/buttons/newGameButtonHighlited.png");
        playAgainButtonHighlitedImg = loadImage("/buttons/playAgainButtonHighlited.png");
        exitButtonHighlitedImg = loadImage("/buttons/exitButtonHighlited.png");
        //other
        coinImg = new BufferedImage[7];
        for(int i=0;i<7;i++) {
            coinImg[i] = loadImage("/coin/coin-"+(i+1)+".png");
        }
        gameOverInfoImg = loadImage("/gameOverInfo.png");
    }

    private BufferedImage loadImage(String path){
        try {
            return ImageIO.read(getClass().getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void setMusic(boolean m){
        this.music = m;
    }
    public boolean getMusic(){
        return music;
    }

    public boolean isNewGameHighlited() {
        return newGameHighlited;
    }

    public void setNewGameHighlited(boolean newGameHighlited) {
        this.newGameHighlited = newGameHighlited;
    }

    public boolean isExitHighlited() {
        return exitHighlited;
    }

    public void setExitHighlited(boolean exitHighlited) {
        this.exitHighlited = exitHighlited;
    }

    public boolean isPlayAgainHighlited() {
        return playAgainHighlited;
    }

    public void setPlayAgainHighlited(boolean playAgainHighlited) {
        this.playAgainHighlited = playAgainHighlited;
    }

}
