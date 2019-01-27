package classes;

import java.awt.*;

public class Handler {

    public Board board;
    public HUD hud;

    Handler(Board b){
        this.board = b;
    }

    public void render(Graphics g) {
        board.render(g);
        hud.render(g);
    }

    public void tick() {
        board.tick();
        hud.tick();
    }
    public void addHud(HUD hud){
        this.hud=hud;
    }
}
