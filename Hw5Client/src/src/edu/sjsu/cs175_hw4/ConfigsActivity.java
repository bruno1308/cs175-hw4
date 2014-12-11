package edu.sjsu.cs175_hw4;

import android.support.v7.app.ActionBarActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ConfigsActivity extends ActionBarActivity {
	public static final String PREFS_NAME = "MyPrefs";
	 

	String name="";
	EditText et;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configs);
		et = (EditText)findViewById(R.id.editText1);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.configs, menu);
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
	
	public void btnOk(View v){
		name = et.getText().toString();
		//Save new name
		if(!name.equals("")){
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("name", name);
			editor.commit();
		}
		finish();
		
	}
	public void btnCancel(View v){
	finish();
	}
}
