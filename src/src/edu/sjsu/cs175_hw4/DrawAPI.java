package edu.sjsu.cs175_hw4;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class DrawAPI extends SurfaceView {
	//Surface where we will draw on
	private Bitmap headl,headu,headd,headr;
	 final DBConnection my_connection;
	private volatile int flag_btn=0;
	private Bitmap bush;
	private int lives = 3;
	private int score =0;
	private int reload_flag =0;
	private int difficulty =0;
	private Bitmap grassbmp;
	private Bitmap bodyrl,bodyud;
	private Bitmap life;
	private Bitmap left,right, turndl, turndr, turnur, turnul;
	public int height,width,hpixel,wpixel;
    private SurfaceHolder holder;
    private Sprite head, left_button,right_button,livespr;
    private Vector<Sprite> mapvec;
    private char[] mapmask;
    private Vector<Sprite> trail;

    private char dir ='r';
    private GameLoopThread gameLoopThread;
    private static final int PIXELS=32;
    private int SPEED=32;
    public int MAX_X;
    private static int MAX_y;
    private int opt;

    public DrawAPI(Context context,int w,int h, int mode) {
          super(context);
          final Activity activity = (Activity) context;
          my_connection= new DBConnection(context, "Game", 3);
          height = h;
          width = w;
          opt = mode;
          MAX_X = width-32;
          wpixel = width/PIXELS;
          hpixel = height/PIXELS;
          holder = getHolder();
          gameLoopThread = new GameLoopThread(this);
          
          holder.addCallback(new SurfaceHolder.Callback() {

        	  public void surfaceDestroyed(SurfaceHolder holder) {
                     boolean retry = true;
                     gameLoopThread.setRunning(false);
                     while (retry) {
                            try {
                                  gameLoopThread.join();
                                  retry = false;
                            } catch (InterruptedException e) {
                            }
                     }
              }

                 @SuppressLint("WrongCall")
				public void surfaceCreated(SurfaceHolder holder) {
                	 	initSprites();
                	 	
                	 	gameLoopThread.setRunning(true);
                        gameLoopThread.start();
                       
                 }

                 public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
                 }
          });
          
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	//Draw the whole map (grass and trees)
          for(int i=0; i<wpixel;++i){
           	  for(int j=0;j<hpixel;++j){
           		mapvec.elementAt(j+ i*hpixel).onDraw(canvas);
           		
           	  }
          }
          drawTexts(canvas);
          //Draw lives available
          for(int i=0;i<lives;++i){
        	  livespr.setX((wpixel+(i*80)));
        	  livespr.setY(hpixel*24*1/20);
        	  livespr.onDraw(canvas);
        	  
          }
         //Draw snake's head
          head.onDraw(canvas);
          
          checkCollision();
          //Draw Buttons
          left_button.onDraw(canvas);
          right_button.onDraw(canvas);
        //Flag for synchronization
          if(flag_btn ==0){        	  
        	  //Add a new tail piece
        	  Sprite t =showTrail(head.getX(),head.getY());
        	  if(t!=null)trail.add(t);
        	 /* if(difficulty>6){
        		  Sprite t2 = null;
        		  if(dir == 'l'){ t2=showTrail(head.getX()-32,head.getY());}
        		  if(dir == 'r'){ t2 =showTrail(head.getX()+32,head.getY());}
        		  if(dir == 'u'){ t2 =showTrail(head.getX(),head.getY()-32);}
        		  if(dir == 'd'){ t2 =showTrail(head.getX(),head.getY()+32);}
            	  if(t2!=null)trail.add(t2);
        	  }*/
          }
          List <Sprite> s = new ArrayList <Sprite> ();
          s = (List<Sprite>) trail.clone();
          ListIterator<Sprite> iter = s.listIterator();
         //Draw all the trail
          synchronized (iter) {
        	  while(iter.hasNext()){
            	  Sprite t = iter.next();
            	  t.onDraw(canvas);
              }
		}
          
          flag_btn=0;
        
     
    }
          
    
    /**
     * Initializes all sprites altogether with map, maze, snake, buttons and life
     */
    public void initSprites(){
    	final BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    	options.inScaled = false;
    	
    	headl = BitmapFactory.decodeResource(getResources(), R.drawable.headl,options);
    	headr = BitmapFactory.decodeResource(getResources(), R.drawable.headr,options);
    	headu = BitmapFactory.decodeResource(getResources(), R.drawable.headu,options);
    	headd = BitmapFactory.decodeResource(getResources(), R.drawable.headd,options);
    	bodyrl = BitmapFactory.decodeResource(getResources(), R.drawable.body1,options);
    	bodyud = BitmapFactory.decodeResource(getResources(), R.drawable.bodyud,options);
        grassbmp = BitmapFactory.decodeResource(getResources(), R.drawable.grass,options);
        bush = BitmapFactory.decodeResource(getResources(), R.drawable.rightbush,options);
        left = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_back);
        right = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_forward);
        turndr = BitmapFactory.decodeResource(getResources(), R.drawable.turndr);
        turndl = BitmapFactory.decodeResource(getResources(), R.drawable.turndl);
        turnur = BitmapFactory.decodeResource(getResources(), R.drawable.turnur);
        turnul = BitmapFactory.decodeResource(getResources(), R.drawable.turnul);
        life = BitmapFactory.decodeResource(getResources(), R.drawable.life,options);
         
           
        initMap();
        initMaze(difficulty);
        initSnake(); 
        initButtonsLife();
         
         
        
       
    	
    }
    
    /**
     * Add a new piece of the tail of the snake
     */
    public synchronized Sprite showTrail(int headx, int heady){
    	int velx = head.getxSpeed();
    	Sprite trail;
    	if(flag_btn ==1) return null;
    	if(velx !=0) {
    		trail = new Sprite(this, bodyrl);
    		trail.setX(headx-head.getxSpeed());
    		trail.setY(heady);
    		
    	} else {
    		 trail = new Sprite(this, bodyud);
    		trail.setX(headx);
    		trail.setY(heady-head.getySpeed());
    	}
    	trail.setxSpeed(0);
		trail.setySpeed(0);
		if(flag_btn ==1) return null;
		mapmask[heady/32 + hpixel*headx/32] = 't';
    	return trail;
    	
    }
    /**
     * Initializes map with some barriers
     */
    public void initMap(){
    	mapmask = new char[hpixel*wpixel];
    	mapvec = new Vector<Sprite>(wpixel*hpixel);
        for(int i=0; i<wpixel;++i){
      	  for(int j=0;j<hpixel;++j){
      		  mapvec.add(new Sprite(this,grassbmp));
      		  mapvec.elementAt(hpixel*i + j).setX(PIXELS*i);
      		  mapvec.elementAt(hpixel*i + j).setY(PIXELS*j);
      		  mapmask[hpixel*i +j] = 'g';
      		  if(j>hpixel-12 || j<5) continue;
      		  if(i==wpixel-1 || i==0 || j==hpixel-12 || j==5  ){
      			  if(j!= hpixel/2){
      			  mapvec.elementAt(hpixel*i +j).setBmp(bush);
      			  mapmask[hpixel*i +j] = 'b';
      			  }
      		  }
      		
      	  }
        }
    }
    /**
     * Initializes a maze given a difficulty dif
     */
    public void initMaze(int dif){
    	MazeGenerator mg = new MazeGenerator();
    	
        Vector<Integer> obst = mg.generate(dif, hpixel, wpixel);
        for(Integer i: obst){
       	 mapvec.elementAt(i).setBmp(bush);
       	 mapmask[i] = 'b';
        }
    }
    /**
     * Initializes snake position and speed
     */
    public void initSnake(){
    	head = new Sprite(this,headr);
        head.setX(0);
        head.setY((height-48)/2);
        
        head.setxSpeed(32);
        dir = 'r';
        trail = new Vector<Sprite>();
    }
    /**
     * Initializes buttons image and lives image
     */
    public void initButtonsLife(){
    	 livespr= new Sprite(this,life);
    	
    	left_button = new Sprite(this, left);
        right_button = new Sprite(this, right);
        left_button.setX((width+360)/5);
        left_button.setY((height+98)*8/10);
        right_button.setX((width+64)*3/5);
        right_button.setY((height+98)*8/10);
    }
    /* 
     * Holds the touch event on the screen
     */
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	
	    int x = (int)event.getX();
	    int y = (int)event.getY();
	    int x_right = right_button.getX();
	    int y_right = right_button.getY();
	    int x_left = left_button.getX();
	    int y_left = left_button.getY();
	    int density = getResources().getDisplayMetrics().densityDpi;
	    switch (density) {
	    case DisplayMetrics.DENSITY_MEDIUM:
	    	if(buttonTouch(x,x_right,y,y_right,32)){
	    		rightButton();
	    	} else if(buttonTouch(x,x_left,y,y_left,32)){
	    		leftButton();
	    	}
	        break;
	    case DisplayMetrics.DENSITY_HIGH:
	    	if(buttonTouch(x,x_right,y,y_right,48)){
	    		rightButton();
	    	}else if(buttonTouch(x,x_left,y,y_left,48)){
	    		leftButton();
	    	}
	        break;
	    case DisplayMetrics.DENSITY_XHIGH:
	    	if(buttonTouch(x,x_right,y,y_right,64)){
	    		rightButton();
	    	}else if(buttonTouch(x,x_left,y,y_left,64)){
	    		leftButton();
	    	}
	    case DisplayMetrics.DENSITY_XXHIGH:
	    	if(buttonTouch(x,x_right,y,y_right,96)){
	    		rightButton();
	    	}else if(buttonTouch(x,x_left,y,y_left,96)){
	    		leftButton();
	    	}
	        break;
	    }
	    
	return false;
	}
	/**
	 * Check whether the touch was on the buttons, with an error margin
	 */
	public boolean buttonTouch(int x, int x_btn, int y, int y_btn, int density){
		int margin = density/2;
		if(x > x_btn-margin && x< x_btn+density+margin &&y > y_btn-margin && y< y_btn+density+margin ){
			return true;
		}
		return false;
	}
	/**
	 * Handles the right button click, turning the snake and leaving a trail
	 */
	public void rightButton(){
		if(reload_flag==1) return;
		flag_btn =1;
		Sprite turning;
		mapmask[head.getY()/PIXELS + hpixel*head.getX()/PIXELS] = 't';
		switch(dir){
		case 'r':
			turning = new Sprite(this,turndr);
			turning.setX(head.getX());
			turning.setY(head.getY());
			trail.add(turning);
			dir = 'd';
			head.setBmp(headd);
			head.setxSpeed(0);
			head.setySpeed(SPEED);
			 
			break;
		case 'l':
			turning = new Sprite(this,turnul);
			turning.setX(head.getX());
			turning.setY(head.getY());
			trail.add(turning);
			dir = 'u';
			head.setBmp(headu);
			head.setxSpeed(0);
			head.setySpeed(-SPEED);
			break;
		case 'u':
			turning = new Sprite(this,turndl);
			turning.setX(head.getX());
			turning.setY(head.getY());
			trail.add(turning);
			dir ='r';
			head.setBmp(headr);
			head.setxSpeed(SPEED);
			head.setySpeed(0);
			break;
		case 'd':
			if(opt ==1){
			turning = new Sprite(this,turnul);
			turning.setX(head.getX());
			turning.setY(head.getY());
			trail.add(turning);
			dir = 'r';
			head.setBmp(headr);
			head.setxSpeed(SPEED);
			head.setySpeed(0);
			break;
			}else{
				turning = new Sprite(this,turnur);
				turning.setX(head.getX());
				turning.setY(head.getY());
				trail.add(turning);
				dir = 'l';
				head.setBmp(headl);
				head.setxSpeed(-SPEED);
				head.setySpeed(0);
				break;
			}
		}

	}
	/**
	 * Handles the left button click, turning the snake and leaving a trail
	 */
	public void leftButton(){
		if(reload_flag==1) return;
		flag_btn =1;
		mapmask[head.getY()/PIXELS+ hpixel*head.getX()/PIXELS] = 't';
		Sprite turning;
		switch(dir){
		case 'r':
			turning = new Sprite(this,turnur);
			turning.setX(head.getX());
			turning.setY(head.getY());
			trail.add(turning);
			dir = 'u';
			head.setBmp(headu);
			head.setxSpeed(0);
			head.setySpeed(-SPEED);
			break;
		case 'l':
			turning = new Sprite(this,turndl);
			turning.setX(head.getX());
			turning.setY(head.getY());
			trail.add(turning);
			dir = 'd';
			head.setBmp(headd);
			head.setxSpeed(0);
			head.setySpeed(SPEED);
			break;
		case 'u':
			turning = new Sprite(this,turndr);
			turning.setX(head.getX());
			turning.setY(head.getY());
			trail.add(turning);
			dir ='l';
			head.setBmp(headl);
			head.setxSpeed(-SPEED);
			head.setySpeed(0);
			break;
		case 'd':
			if(opt==1){
			turning = new Sprite(this,turnur);
			turning.setX(head.getX());
			turning.setY(head.getY());
			trail.add(turning);
			dir = 'l';
			head.setBmp(headl);
			head.setxSpeed(-SPEED);
			head.setySpeed(0);
			break;
			}else{
				turning = new Sprite(this,turnul);
				turning.setX(head.getX());
				turning.setY(head.getY());
				trail.add(turning);
				dir = 'r';
				head.setBmp(headr);
				head.setxSpeed(SPEED);
				head.setySpeed(0);
				break;
			}
		}
		
		
	}
	/**
	 * Check collision against anything that is not grass (body, barriers)
	 */
	public synchronized void checkCollision(){
		int xhead, yhead;
		xhead = head.getX()/PIXELS;
		yhead = head.getY()/PIXELS;
		if(xhead == wpixel && yhead == (hpixel-1)/2) {levelCleared(); return;}
		if(xhead >= wpixel || yhead >= hpixel || xhead <0 || yhead <0) collisionHappened();
		if(mapmask[yhead + hpixel*xhead] !='g'){
			collisionHappened();
		}
		
	}
	/**
	 * Collision handler
	 */
	public void collisionHappened(){
		lives--;
		if(lives ==0) finishGame();
		reloadGame();
	}
	/**
	 * Save score if it's the highest and show Message
	 */
	public void finishGame(){
		final Activity a = (Activity)this.getContext();
		SQLiteDatabase db = my_connection.getReadableDatabase();
        Cursor c = my_connection.select(db);
        if(c.getCount()!=0){
        	c.moveToLast();
        	int high_score = c.getInt(1);
        	if(score > high_score){
        		db = my_connection.getWritableDatabase();
        		my_connection.insert(db, score);
        		
        	}
        }
        db.close();
        score=0;
        a.runOnUiThread(new Runnable() {
      	  public void run() {
      	    Toast.makeText(a, "Game Over", Toast.LENGTH_LONG ).show();
      	  }
      	});
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        GameLoopThread.setFPS(10);
		a.finish();
		
	}
	/**
	 * Lost one life, reload all the map and start over
	 */
	public void reloadGame(){
		reload_flag =1;
		initMap();
        initMaze(difficulty);
        initSnake();
        
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        reload_flag=0;
	}
	/**
	 * Add score and increase difficulty
	 */
	public void levelCleared(){
		try {
			reload_flag =1;
			score += (100*(difficulty+1))-trail.size();
			initMap();
	        initMaze(++difficulty);
	        initSnake();
	        GameLoopThread.setFPS(10+(difficulty*2));
			Thread.sleep(2000);
			//finish();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reload_flag= 0;
		
	}
	/**
	 * Draw level and score on the screen
	 */
	public void drawTexts(Canvas canvas){
		if(canvas ==null)return;
		Paint paint = new Paint(); 

		paint.setColor(Color.YELLOW); 
		paint.setTextSize(50); 
		canvas.drawText("Level: "+Integer.toString(difficulty), wpixel*28/2, hpixel*21/10, paint);
		canvas.drawText("Score: "+Integer.toString(score), wpixel*28*3/4, hpixel*21/10, paint);
	}
}