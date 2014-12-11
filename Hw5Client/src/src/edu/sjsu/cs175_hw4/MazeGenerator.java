package edu.sjsu.cs175_hw4;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class MazeGenerator {
	Vector<Integer> result = new Vector<Integer>();
	Set<Integer> openings = new HashSet<Integer>();
	static Set<Integer> blockings = new HashSet<Integer>();
	static Set<Integer> blockingsaux = new HashSet<Integer>();
	 
	static int diff=0;
	static int h,w;
	/**
	 * Generates new mazes randomly given a certain difficulty
	 */
	public Vector<Integer> generate(int dif,int h,int w){
		MazeGenerator.diff = dif;
		MazeGenerator.h = h;
		MazeGenerator.w= w;
		int myvec[] = {1,2,2,2,3,3,4,4,5,5};
		int blocking_number;
		//Never has more than 5 blocking spots
		if(dif>=10) blocking_number = myvec[9];
		else blocking_number = myvec[dif];
		
		for(int k=blocking_number;k>0;--k) blockings.add(randBlocking(5, w-5));
		
		int usable_rows = h-12-5;
		int openings_number = (usable_rows/10)*((6-dif));
		if(openings_number <=0) openings_number=1;
		
			
			for(int i=0; i<w;++i){
				openings.clear();
				for(int k=openings_number;k>0;--k)openings.add(randInt(6,h-11));
		       	  for(int j=0;j<h;++j){
		       		if(j>h-12 || j<5) continue;
		       		  if(blockings.contains(i) ){
		       				  if(!openings.contains(j))		       				  
		       					  result.add(h*i +j);		       			  
		       		  }
		       	  }
		    }
			
			blockings.clear();
			blockingsaux.clear();
			openings.clear();
		return result;
		
		
	}
	public static int randBlocking(int min, int max) {
	   // nextInt is normally exclusive of the top value,
	   // so add 1 to make it inclusive
	   int randomNum = randInt(min,max);
	   // Now, let's assure minimum distance of blockings,
	   // otherwise it might generate impossible levels
	   while(blockingsaux.contains(randomNum)){
	   		//System.out.print("Already has blocking here, ");
	   		randomNum = randInt(min,max);
	   		//System.out.println("generating new one: "+randomNum);
	   	}
	   	//No blockings allowed in the neighboorhood
	   	blockingsaux.add(randomNum);
	   	blockingsaux.add(randomNum+1);
	   	blockingsaux.add(randomNum+2);
	   	blockingsaux.add(randomNum-1);
	    blockingsaux.add(randomNum-2);
	    return randomNum;
	}
	public static int randInt(int min, int max){
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;		
	}
	


}
