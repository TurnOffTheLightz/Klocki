package inputs;

import classes.Handler;
import klocki.Block;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInput implements MouseListener,MouseMotionListener {


    public static int mouseDraggedX,mouseDraggedY;
    private Handler handler;

    private boolean mousePressed=false;
    private boolean mouseReleased=false;
    private boolean mouseDragged=false;


    public MouseInput(Handler handler){
        this.handler=handler;
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e){

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point point = new Point(e.getX(),e.getY());
        if(!handler.board.getBounds().contains(point)){
            for(int i=0;i<handler.hud.blockList.size();i++){
                Block block = handler.hud.blockList.get(i);
                if(block.isBeingDragged()){
                    block.setPulled(true);
                    break;
                }
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
    public void mouseDragged(MouseEvent e) {
        mouseDraggedX  =   e.getX();
        mouseDraggedY  =   e.getY();
        mouseDragged=true;
        Point point = new Point(e.getX(),e.getY());

        if(handler.hud.getBounds().contains(point)){
            for(int i=0;i<handler.hud.blockList.size();i++){
                Block block = handler.hud.blockList.get(i);
                for(int k=0;k<handler.hud.blockList.get(i).brickList.size();k++){
                    if(block.getBoundsArray()[k].contains(point)){
                        block.setBeingDragged(true);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
