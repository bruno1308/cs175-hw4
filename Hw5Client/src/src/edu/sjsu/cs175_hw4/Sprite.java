package edu.sjsu.cs175_hw4;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;

/**
 * @author Bruno
 * Holds and update the information about every sprite in the game
 */
public class Sprite {

    private DrawAPI gameView;
    private int MAX_X;
    private Bitmap bmp;
    private int x = 0;
    public Bitmap getBmp() {
		return bmp;
	}

	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}


	private int y = 0;
    private int xSpeed=0;
    private int ySpeed=0;
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

	public int getxSpeed() {
		return xSpeed;
	}

	public void setxSpeed(int xSpeed) {
		this.xSpeed = xSpeed;
	}

	public int getySpeed() {
		return ySpeed;
	}

	public void setySpeed(int ySpeed) {
		this.ySpeed = ySpeed;
	}


	private int width;
    private int height;

    public Sprite(DrawAPI gameView, Bitmap bmp) {
          this.width = bmp.getWidth();
          this.height = bmp.getHeight();
          this.gameView = gameView;
          this.bmp = bmp;
          MAX_X = gameView.MAX_X;
          
    }

    private synchronized void update() {
       /*   if (x >= gameView.getWidth() - width - xSpeed || x + xSpeed <= 0) {
                 xSpeed = -xSpeed;
          }*/
          x = x + xSpeed;
         /* if (y >= gameView.getHeight() - height - ySpeed || y + ySpeed <= 0) {
                 ySpeed = -ySpeed;
          }*/
          y = y + ySpeed;
    }

    public synchronized void onDraw(Canvas canvas) {
    	update();
    	
    	if(canvas!= null && x<MAX_X )
        canvas.drawBitmap(bmp, x , y, null);
    }


}
