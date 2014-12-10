package edu.sjsu.cs175_hw4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


import android.os.AsyncTask;
import android.util.Log;


//Handle the transaction between client and server
public final class Connection extends AsyncTask<String, Void, String> {
 
	public final String IP;
	public final int PORT;
	static String  response="";
	public static int sync=-1;
	String question;
	Socket socket = null;
	PrintWriter writer = null;
	//Queue of requests the client made
	final static BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    BufferedReader reader =null;
    public static Connection my_instance= null;
	Connection(String addr, int port){
		//If you will try in your own network, ignore the parameters and set the IP
		//and port manually HERE, This is my server that MIGHT be off 
		   this.IP = "10.1.10.24";
		   this.PORT = port;	
		   Connection.my_instance = this;
	         
	}
	public static Connection getInstance(){
		if(my_instance == null){
			return null;
		}
		else{
			return my_instance;
		}
	}
	@Override
	protected String doInBackground(String... arg0) {

	      try{
	    	  //Create socket and reader/writer
	    	  socket = new Socket(IP, PORT);
	    	  System.out.println("Socket created");
	 	        writer = new PrintWriter(socket.getOutputStream(),true);
	 	        reader = new BufferedReader(
    		            new InputStreamReader(socket.getInputStream()));
	 	        String fromUser="" ;
	 	       CharBuffer cb = CharBuffer.allocate(1000);
	 	        while(true){
	 	        		try {
	 	        			//sync synchronizes the network to tell when the
	 	        			//reader is ready to be read
	 	        			
	 	        			while(reader.ready()){
	 	        				/*Server sent us something!
	 	        				sync = 0 means we are busy reading,
	 	        				don't try to get data yet!*/
	 	        				sync =0;
	 	        				reader.read(cb);
	 	        				cb.flip();
	 	        				String msg = cb.toString();
								Log.i("Server", msg);
								
								response = response+msg;
								//System.out.println("Server: " + fromServer);
								//if(fromServer.equals("") || fromServer.equals("Fingercise Server")) break;			
							}
							if(!queue.isEmpty()){
								//If client wants something, send it to server
								sync =0;
								fromUser = queue.take();
				 	        	 writer.println(fromUser);
				 	        	 writer.flush();
						         System.out.println("Request sent!");	
							}	
							sync =1;
							} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	 	        	 
	 	        }
	      }
	
	      catch (UnknownHostException e) {
	    	    // TODO Auto-generated catch block
	    	    e.printStackTrace();
	    	    response = "UnknownHostException: " + e.toString();
	    	   } catch (IOException e) {
	    	    // TODO Auto-generated catch block
	    	    e.printStackTrace();
	    	    response = "IOException: " + e.toString();
	    	   }
	    	   return null;

	}
	@Override
  protected void onPostExecute(String result) {

  }
	

}
