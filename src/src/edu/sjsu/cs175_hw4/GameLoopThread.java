package edu.sjsu.cs175_hw4;

import android.annotation.SuppressLint;
import android.graphics.Canvas;

/**
 * @author Bruno
 * Thread in which the game is run
 */
public class GameLoopThread extends Thread {
	
    static long FPS = 10;
    public static long getFPS() {
		return FPS;
	}

	public static void setFPS(long fPS) {
		FPS = fPS;
	}

	private DrawAPI view;
    private boolean running = false;
   
    public GameLoopThread(DrawAPI view) {
          this.view = view;
    }

    public void setRunning(boolean run) {
          running = run;
    }

    @SuppressLint("WrongCall")
	@Override
    public void run() {
          long ticksPS = 1000 / FPS;
          long startTime;
          long sleepTime;
          while (running) {
                 Canvas c = null;
                 startTime = System.currentTimeMillis();
                 try {
                        c = view.getHolder().lockCanvas();
                        synchronized (view.getHolder()) {
                               view.onDraw(c);
                        }
                 } finally {
                        if (c != null) {
                               view.getHolder().unlockCanvasAndPost(c);
                        }
                 }
                 ticksPS = 1000 / FPS;
                 sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
                 try {
                        if (sleepTime > 0)
                               sleep(sleepTime);
                        else
                               sleep(10);
                 } catch (Exception e) {}
          }
    }
}