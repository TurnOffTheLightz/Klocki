package music;

import javax.media.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
public class MusicPlay implements Runnable {
    private Time zeroTime = new Time(0);
    private Player player;
    private boolean turnOff = false;
    private boolean isloop = false;
    public MusicPlay(String filename) {
        File file = new File(filename);
        try {
            player = Manager.createRealizedPlayer(file.toURI().toURL());
            player.addControllerListener(new ControllListener());
        } catch (NoPlayerException e) {
            e.printStackTrace();
        } catch (CannotRealizeException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public MusicPlay(URL filename)
    {
        try {
            player = Manager.createRealizedPlayer(filename);
            player.addControllerListener(new ControllListener());
        } catch (NoPlayerException e) {
            e.printStackTrace();
        } catch (CannotRealizeException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean isRunning() {
        return player.getState() == Player.Started;
    }

    public void play() {
        if (!turnOff)
            player.start();
    }

    private void replay() {
        if (turnOff)
            return;

        if (player.getState() == Controller.Prefetched)
            player.setMediaTime(zeroTime);
        player.start();
    }

    public void stop() {
        player.stop();
    }

    public void close() {
        player.stop();
        player.close();
    }
    public void loop() {
        if (turnOff)
            return;

        isloop = true;
        player.prefetch();
        replay();
    }

    public void run() {
        loop();
    }
    private class ControllListener implements ControllerListener {

        public void controllerUpdate(ControllerEvent e) {
            if (e instanceof EndOfMediaEvent) {
                if (isloop) {
                    player.setMediaTime(new Time(0));
                    player.start();
                }
            }
        }
    }
}