package inputs;

import classes.Board;
import classes.Game;
import classes.Handler;
import klocki.Block;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInput implements MouseListener,MouseMotionListener {


    public static int mouseDraggedX,mouseDraggedY;
    public static int mouseMovedX,mouseMovedY;
    private Handler handler;
    private boolean draggingBlock;
    private final Rectangle //action button bounds
            //game buttons
            musicButtonRect = new Rectangle(40,215,210,85),//250,300
            newGameRect = new Rectangle(0,420,250,105),//240,525
            //gameOverScreen buttons
            playAgainRect = new Rectangle(245,290,205,105),//550,395
            exitRect = new Rectangle(300,435,190,115);//490,540

    public MouseInput(Handler handler){
        this.handler=handler;
        draggingBlock   =   false;
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        mouseDraggedX  =   e.getX();
        mouseDraggedY  =   e.getY();
        Point point = new Point(e.getX(),e.getY());

//        System.out.println("mouse released\t::\t"+draggingBlock);
        if(handler.hud.getBounds().contains(point)&&!draggingBlock){
            for(int i=0;i<handler.hud.blockList.size();i++){
                Block block = handler.hud.blockList.get(i);
                for(int k=0;k<handler.hud.blockList.get(i).brickList.size();k++){
                    if((!block.isOnBoard()||block.isOnHud())&&block.getBoundsArray()[k].contains(point)){
                        block.setBeingDragged(true);
                        draggingBlock=true;
                        break;
                    }
                }
            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for(int i=0;i<handler.hud.blockList.size();i++){
            Block block = handler.hud.blockList.get(i);
            if(block.isBeingDragged()){
                block.setPulled(true);
                break;
            }
        }
    }
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e){
        int x = e.getX();
        int y = e.getY();
        if(!Board.gameOverScreen){
            if(musicButtonRect.contains(x,y)){
                Board.playSound("buttonPressed.wav");
                if(handler.getMusic()){
                    handler.setMusic(false);
                }else{
                    handler.setMusic(true);
                }
            }
            if(newGameRect.contains(x,y)){
                Board.playSound("buttonPressed.wav");
                Board.newGame = true;
            }
        }else{
            if(exitRect.contains(x,y)){
                Board.playSound("buttonPressed.wav");
                Game.exit=true;
            }
            if(playAgainRect.contains(x,y)){
                Board.playSound("buttonPressed.wav");
                Board.newGame = true;
            }
        }
    }
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


    @Override
    public void mouseMoved(MouseEvent e) {
        if(draggingBlock)draggingBlock=false;
        mouseMovedX = e.getX();
        mouseMovedY = e.getY();

        int x = e.getX();
        int y = e.getY();
        if(!Board.gameOverScreen){
            if(newGameRect.contains(x,y)){
                Board.playSound("buttonMoved.wav");
                handler.setNewGameHighlited(true);
            }else{
                handler.setNewGameHighlited(false);
            }
        }else{
            if(playAgainRect.contains(x,y)){
                Board.playSound("buttonMoved.wav");
                handler.setPlayAgainHighlited(true);
            }else{
                handler.setPlayAgainHighlited(false);
            }
            if(exitRect.contains(x,y)){
                Board.playSound("buttonMoved.wav");
                handler.setExitHighlited(true);
            }else{
                handler.setExitHighlited(false);
            }
        }
    }
}
