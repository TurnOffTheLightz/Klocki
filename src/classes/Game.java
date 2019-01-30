package classes;

import inputs.MouseInput;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Game extends JPanel implements Runnable{

    private final int  WIDTH = 200;
    private final int HEIGHT = 200;
    private final int SCALE = 4;
    private int frames=0;
    private Canvas canvas;

    private Thread thread;
    private Handler handler;
    private boolean running;
    public static boolean exit=false;

    private BufferedImage backgroundImg;

    private Game(){
        Dimension dim = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
        this.canvas = new Canvas();
        canvas.setPreferredSize(dim);
        canvas.setMaximumSize(dim);
        canvas.setMinimumSize(dim);
        setPreferredSize(dim);
        setMinimumSize(dim);
        setMaximumSize(dim);

        JFrame frame = new JFrame("(:   KLOCKI  :)");
        frame.setResizable(false);
        frame.setSize(dim);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setFocusable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        this.add(this.canvas);
        frame.setContentPane(this);
        frame.pack();
    }
    private void init(){
        Board board = new Board(275,80,481,481);
        handler = new Handler(board);
        HUD hud = new HUD(140,571,641,216,handler);
        handler.addHud(hud);
        board.addHUD(hud);
        canvas.addMouseListener(new MouseInput(handler));
        canvas.addMouseMotionListener(new MouseInput(handler));
        backgroundImg   =   loadImage("/background.png");
    }
    private void render(){
        BufferStrategy bs = this.canvas.getBufferStrategy();

        if(bs==null){
            this.canvas.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.drawImage(backgroundImg,0,0,WIDTH*SCALE,HEIGHT*SCALE,null);

        g.setColor(Color.white);
        g.drawString("("+Integer.toString(MouseInput.mouseMovedX)+","+Integer.toString(MouseInput.mouseMovedY)+")",730,40);

        handler.render(g);

        g.dispose();
        bs.show();
//        this.paintComponent(g);
    }
    private void tick(){
        handler.tick();
        if(exit){
            running=false;
            stop();
            System.exit(0);
        }
    }
    private synchronized void start(){
        if(running) return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }
    private synchronized void stop(){
        if(!running) return;
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        init();
        requestFocus();
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0.0;
        double ns = 1000000000.0/60.0;
        int updates = 0;
        while(running){
            long now = System.nanoTime();
            delta+=(now-lastTime)/ns;
            lastTime = now;

            while(delta>=1){
                tick();
                updates++;
                delta--;
            }
            render();
            frames++;
            if(System.currentTimeMillis()-timer>1000){
                timer+=1000;
                System.out.println("FPS: "+frames + "\tUPDATES: "+updates);
                frames=0;
                updates=0;
            }
        }
    }

    public static void main(String[] args) {
        Game g = new Game();
        g.start();
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

