package edu.sjsu.cs175_hw4;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Bruno
 * Class that
 */
public class DBConnection extends SQLiteOpenHelper {

	/**
	 * Class that handles SQLite connections
	 */
	public DBConnection(Context context, String name, int version) {
		super(context, name, null, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try
 	   {
 		  db.execSQL("CREATE TABLE IF NOT EXISTS utils (id INTEGER PRIMARY_KEY, high_score INTEGER)"); 
 	   }
 	   catch(SQLException e)
 	   {
 		  Log.e("SqliteAndroid", "DBOpenHelper", e);
 	   }

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS utils");
		this.onCreate(db);
	}
	public Cursor select(SQLiteDatabase db){
		Cursor c = db.rawQuery("SELECT * FROM utils where id =0", null);
		return c;
		
	}
	public void insert(SQLiteDatabase db, int score){
		db.execSQL("INSERT OR REPLACE INTO utils (id, high_score) VALUES (0, "+score+")");
	}

}
