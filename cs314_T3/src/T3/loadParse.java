package a2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class loadParse {

	Vector games = new Vector();
	public loadParse() {
		
	}
	public void parseGames()
	{
		
		try {		      //use buffering, reading one line at a time
		      //FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader("SavedGames.txt"));
		      try {
		        String line = null; //not declared within while loop
		        while (( line = input.readLine()) != null){
		          
		          //System.out.println(line);
		          
		        	games.add(line);
		        	//System.out.println(games.size());
		        }
		      }
		      finally {
		        input.close();
		      }
		    }
		    catch (IOException ex){
		      ex.printStackTrace();
		    }
		
		
	}
	public Vector getGames()
	{
		return games;
	}
	
	public void loadSavedGame(String a)
	{
		try {		      //use buffering, reading one line at a time
		      //FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader(a));
		      try {
		        String line = null; //not declared within while loop
		        while (( line = input.readLine()) != null){
		          
		          //System.out.println(line);
		          
		        	games.add(line);
		        	//System.out.println(games.size());
		        }
		      }
		      finally {
		        input.close();
		      }
		    }
		    catch (IOException ex){
		      ex.printStackTrace();
		    }
		    
	}
	
	public Vector getLoadInfo()
	{
		Vector load = new Vector();
		int count = 0;
		int count2 = 0;
		String line = (String)games.get(0);
		while(count != line.length()){
			String temp = "";
			
			
			while(line.charAt(count) != '|'){
				//System.out.print(a.charAt(count));
				char c = line.charAt(count);
				//System.out.println(c);
				temp = temp + c;
				//System.out.println(temp);
				count++;
				
			}
			//System.out.println(temp);
			load.add(temp);
			
			
			//System.out.print("\n");
			count++;
			count2++;
			
		}
		int count3 = 0;
		String temp = (String)load.lastElement();
		load.remove(load.lastElement());
		while(count3 != temp.length())
		{
			String a = "";
			while(temp.charAt(count3) != ',')
			{
				char c = temp.charAt(count3);
				//System.out.println(c);
				a = a + c;
				//System.out.println(temp);
				count3++;
			}
			load.add(a);
			//System.out.println(a);
			count3++;
		}
		
		
		return load;
	}
	
	public Vector getLoadRoom()
	{
		Vector roomsInfo = new Vector();
		
		
		for(int i = 1; i < games.size(); i++)
		{
			Vector rooms = new Vector();
			Vector tempList = new Vector();
			String tempLine = (String)games.get(i);
			//System.out.println(tempLine);
			int count = 0;
			while(count != tempLine.length())
			{
				
				String temp = "";
				while(tempLine.charAt(count) != '|')
				{
					char c = tempLine.charAt(count);
					//System.out.println(c);
					temp = temp + c;
					//System.out.println(temp);
					count++;
					
				}
				//System.out.println(temp);
				tempList.add(temp);
				
				
				//System.out.print("\n");
				count++;
			}
			int count2 = 0;
			
			String tempItems = (String)tempList.get(0);
			while(count2 != tempItems.length())
			{
				String temp2 = "";
				while(tempItems.charAt(count2) != ',')
				{
					char b = tempItems.charAt(count2);
					temp2 = temp2 + b;
					
					count2++;
				}
				rooms.add(temp2);
				//System.out.println(temp2);
				count2++;
			}
			rooms.add(tempList.get(1));	
			roomsInfo.add(rooms);
			
		
		}
		
		return roomsInfo;
	}
}
