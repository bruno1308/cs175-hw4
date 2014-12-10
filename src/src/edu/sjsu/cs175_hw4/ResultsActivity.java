package edu.sjsu.cs175_hw4;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ResultsActivity extends ActionBarActivity {
	TextView firstname,secondname,thirdname,fourthname,fifthname;
	TextView firstscore,secondscore,thirdscore,fourthscore,fifthscore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		firstname = (TextView) findViewById(R.id.txtFirstName);
		secondname = (TextView) findViewById(R.id.txtSecondName);
		thirdname = (TextView) findViewById(R.id.txtThirdName);
		fourthname = (TextView) findViewById(R.id.txtFourthName);
		fifthname = (TextView) findViewById(R.id.txtFifthName);
		firstscore = (TextView) findViewById(R.id.txtFirstScore);
		secondscore = (TextView) findViewById(R.id.txtSecondScore);
		thirdscore = (TextView) findViewById(R.id.txtThirdScore);
		fourthscore = (TextView) findViewById(R.id.txtFourthScore);
		fifthscore = (TextView) findViewById(R.id.txtFifthScore);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
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
	
	public void btnBack(View v){
		finish();
	}
	public boolean btnResults(View v){
		try{
			if(Connection.my_instance == null){
				Connection c = new Connection("54.173.198.121", 7890);
     			c.execute();
     			System.out.println("Connection Estabileshed and task running");
    			Thread.sleep(500);
    			
			}
			Connection.response ="";
			String msg = "results";
			Connection.queue.add(msg);
			String response;
			System.out.println("Waiting for results reply");
			int time_out=0;
			while (Connection.sync == 0 || Connection.response.equals("") || !Connection.response.contains(".")) {
				Thread.sleep(100);
				time_out++;
				if(time_out == 20) {
					return false;
				}
			}
			response = Connection.response;
			String lines[] = response.split("\\r?\\n");
			// Now split between values
			String[] first = lines[0].split("\\t");
			String[] second = lines[1].split("\\t");
			String[] third = lines[2].split("\\t");
			String[] fourth = lines[3].split("\\t");
			String[] fifth = lines[4].split("\\t");
			firstname.setText(first[0]+" ");
			firstscore.setText(first[1]);
			secondname.setText(second[0]+" ");
			secondscore.setText(second[1]);
			thirdname.setText(third[0]+" ");
			thirdscore.setText(third[1]);
			fourthname.setText(fourth[0]+" ");
			fourthscore.setText(fourth[1]);
			fifthname.setText(fifth[0]+" ");
			fifthscore.setText(fifth[1]);
			return true;
		}catch(Exception e){
			
		}
		return true;
	}
	
}
