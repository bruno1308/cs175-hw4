package edu.sjsu.cs175_hw4;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import edu.sjsu.cs175_hw4.R;

/**
 * @author Bruno
 * Main screen
 */
public class MainActivity extends ActionBarActivity {
	public static final String PREFS_NAME = "MyPrefs";
	
	TextView high_score;
	  DBConnection my_connection;
	  int opt=0;
	  String name;

	/* 
	 * Loads high score or create a new database
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		my_connection = new DBConnection(this, "Game", 3);
	    high_score = (TextView)findViewById(R.id.txtHighScore);
	    SQLiteDatabase db = my_connection.getReadableDatabase();
	    Cursor c = my_connection.select(db);
	    if(c.getCount()== 0){
	       db = my_connection.getWritableDatabase();
	       my_connection.insert(db,0);
	       high_score.setText(Integer.toString(0));
	       db.close();	
	    }
	    else{
	    	c.moveToLast();
	    	int score = c.getInt(1);
	    	high_score.setText(Integer.toString(score));
	    	db.close();
	    }
	      Dialog d = onCreateDialog( savedInstanceState) ;
	      d.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void btnStart(View v){
		Intent game = new Intent(this, GameActivity.class);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		name = settings.getString("name", "Unknown");
		game.putExtra("mode", opt);
		game.putExtra("name", name);
		startActivity(game);
 
	}
	public void btnResults(View v){
		Intent results = new Intent(this, ResultsActivity.class);
		startActivity(results);
 
	}
	public void btnConfigs(View v){
		Intent configs = new Intent(this, ConfigsActivity.class);
		startActivity(configs);
 
	}
	@Override
	public void onResume(){
	    super.onResume();
	    
	    SQLiteDatabase db = my_connection.getReadableDatabase();
        Cursor c = my_connection.select(db);
        if(c.getCount()== 0){
        	  db = my_connection.getWritableDatabase();
        	 my_connection.insert(db,0);
        	 high_score.setText(0);
        	 db.close();
    		
    	}
    	else{
    		c.moveToLast();
    		int score = c.getInt(1);
    		high_score.setText(Integer.toString(score));
    		 db.close();
    	}

	}
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    // Set the dialog title
	    CharSequence[] array = {"Dr. Pollett controls (90 degrees arrows)","Intuitive controls (left/right arrows)"};
	    builder.setTitle(R.string.pick_toppings)
	    // Specify the list array, the items to be selected by default (null for none),
	    // and the listener through which to receive callbacks when items are selected
	    
	           .setSingleChoiceItems(array, 0,
	                      new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								opt = which;
								
							}

							
	               
	           })
	    // Set the action buttons
	           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   
	                   
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   
	                       dialog.dismiss();

	               }
	           });

	    return builder.create();
	}
}
