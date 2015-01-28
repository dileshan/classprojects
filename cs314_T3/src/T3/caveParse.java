package a2;

/**
 * 
 */
import java.io.*;
import java.util.*;
/**
 * @author Matt
 *
 */
public class caveParse {

	/**
	 * @param args
	 */
	Room roomsTot[] = new Room[100];
	Vector roomsDis = new Vector();
	int count = 0;
	int roomNo = 0;
	public void caveParse() {
		
	}
	public Room readFile(){
		
	// TODO Auto-generated method stub
		StringBuffer contents = new StringBuffer();
		int count = 0;
		
		try {		      //use buffering, reading one line at a time
		      //FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader("caveGame.txt"));
		      try {
		        String line = null; //not declared within while loop
		        while (( line = input.readLine()) != null){
		          
		          //System.out.println(line);
		          //System.out.println(count);
		          count++;
		          createCaves(line);
		        }
		      }
		      finally {
		        input.close();
		      }
		    }
		    catch (IOException ex){
		      ex.printStackTrace();
		    }
		    roomsSetWalls();
		return roomsTot[0];
	}
	public void createCaves(String a){
		int count = 0;
		Vector roomCreate = new Vector();
		while(count != a.length()){
			String temp = "";
			char bob[] = new char[100];
			int count2 = 0;
			while(a.charAt(count) != '|'){
				//System.out.print(a.charAt(count));
				char c = a.charAt(count);
				//System.out.println(c);
				temp = temp + c;
				//System.out.println(temp);
				count++;
				count2++;
			}
			roomCreate.add(temp);
			//System.out.println(temp);
			//System.out.print("\n");
			count++;
			
		}
		createRooms(roomCreate);
		
		
	}
	public void roomsSetWalls() 
	{
		for(int i = 0; i < roomsDis.size(); i++)
		{
			//System.out.println(roomsDis.size());
			//System.out.println(roomsDis.size());
			Vector des = (Vector)roomsDis.get(i);
			int count = 0;
			for(int a = 0; a < des.size()-1; a++)
			{
				String one = (String)des.get(a);
				a++;
				String two = (String)des.get(a);
				int way = count;
				
				switch(way){
				case 1:
					way = 2;
					break;
				case 2:
					way = 1;
					break;
				case 4:
					way = 5;
					break;
				case 5:
					way = 4;
					break;
				}
				
				
				//System.out.println("Side "+side[0] + " " + side[1]);
				if(one.compareTo("w") != 0)
				{
					if(one.compareTo("x") == 0)
					{
						String roomNum = two;
						int b = Integer.parseInt(roomNum);
						
						roomsTot[i].setSide(way, roomsTot[b]);
					}
					if(one.compareTo("b") == 0)
					{
						String roomNum = two;
						int b = Integer.parseInt(roomNum);
						
						roomsTot[i].setSide(way, roomsTot[b]);
						Blackhole hole = new Blackhole(roomsTot[i],roomsTot[b]);
						roomsTot[i].setSide(way, hole);
						
					}
					if(one.compareTo("dt") == 0)
					{
						String roomNum = two;
						int b = Integer.parseInt(roomNum);
						
						roomsTot[i].setSide(way, roomsTot[b]);
						Door dr = new Door(roomsTot[i],roomsTot[b],"toyKey");
						roomsTot[i].setSide(way, dr);
					}
					if(one.compareTo("dg") == 0)
					{
						String roomNum = two;
						int b = Integer.parseInt(roomNum);
						
						roomsTot[i].setSide(way, roomsTot[b]);
						Door dr = new Door(roomsTot[i],roomsTot[b],"goldKey");
						roomsTot[i].setSide(way, dr);
					}
					if(one.compareTo("ds") == 0)
					{
						String roomNum = two;
						int b = Integer.parseInt(roomNum);
						
						roomsTot[i].setSide(way, roomsTot[b]);
						Door dr = new Door(roomsTot[i],roomsTot[b],"silverKey");
						roomsTot[i].setSide(way, dr);
					}
				}
				count++;
			}
			
		}
		
	}
	public void createRooms(Vector a)
	{
		String roomNum = (String)a.get(0);
		//System.out.println(roomNum);
		String directions = (String)a.get(1);
		//System.out.println(directions);
		Vector moves = new Vector();
		moves = parseMoves(directions);
		
		//System.out.println(directions.length());
		String desc = (String)a.get(2);
		//System.out.println(desc);
		String roomItems = (String)a.get(3);
		Vector items = new Vector();
		items = parseItems(roomItems);
		//create room
		Room r1 = new Room();
		r1.setDesc(desc);
		r1.setRoomNum(roomNo);
		roomNo++;
		//System.out.println(items);
		if(items.size() != 0)
		{	
			for(int i = 0; i < items.size(); i++)
			{
				String item1 = (String)items.get(i);
				
				if(item1.compareTo("stick") == 0)
				{
					//System.out.println(count + " " + item1);
					Weapons weap = new Weapons();
					weap.setDesc(item1);
					weap.setStrength(20);
					r1.addItem(weap);
				}
				else if(item1.compareTo("whip") == 0)
				{
					//System.out.println(count + " " + item1);
					Weapons weap = new Weapons();
					weap.setDesc(item1);
					weap.setStrength(50);
					r1.addItem(weap);
				}
				else if(item1.compareTo("gun") == 0)
				{
					//System.out.println(count + " " + item1);
					Weapons weap = new Weapons();
					weap.setDesc(item1);
					weap.setStrength(70);
					r1.addItem(weap);
				}
				else if(item1.compareTo("sword") == 0)
				{
					//System.out.println(count + " " + item1);
					Weapons weap = new Weapons();
					weap.setDesc(item1);
					weap.setStrength(60);
					r1.addItem(weap);
				}
				else if(item1.compareTo("lightsaber") == 0)
				{
					//System.out.println(count + " " + item1);
					Weapons weap = new Weapons();
					weap.setDesc(item1);
					weap.setStrength(75);
					r1.addItem(weap);
				}
				else if(item1.compareTo("bomb") == 0)
				{
					//System.out.println(count + " " + item1);
					Weapons weap = new Weapons();
					weap.setDesc(item1);
					weap.setStrength(1000);
					r1.addItem(weap);
				}
				else if(item1.compareTo("chopsticks") == 0)
				{
					//System.out.println(count + " " + item1);
					Weapons weap = new Weapons();
					weap.setDesc(item1);
					weap.setStrength(100);
					r1.addItem(weap);
				}
				else if(item1.compareTo("silverKey") == 0)
				{
					//System.out.println(count + " " + item1);
					Key theKey = new Key();
					theKey.setDesc(item1);
					r1.addItem(theKey);
				}
				else if(item1.compareTo("goldKey") == 0)
				{
					//System.out.println(count + " " + item1);
					Key theKey = new Key();
					theKey.setDesc(item1);
					r1.addItem(theKey);
				}
				else if(item1.compareTo("toyKey") == 0)
				{
					//System.out.println(count + " " + item1);
					Key theKey = new Key();
					theKey.setDesc(item1);
					r1.addItem(theKey);
				}
				else if(item1.compareTo("necklace") == 0)
				{
					//System.out.println(count + " " + item1);
					Treasure theTreasure = new Treasure();
					theTreasure.setDesc(item1);
					r1.addItem(theTreasure);
				}
				else if(item1.compareTo("treasure") == 0)
				{
					//System.out.println(count + " " + item1);
					//System.out.println(count + " " + item1);
					Treasure theTreasure = new Treasure();
					theTreasure.setDesc(item1);
					r1.addItem(theTreasure);
				}
				else if(item1.compareTo("food")== 0)
				{
					//System.out.println(count + " " + item1);
					Food weap = new Food();
					weap.setDesc(item1);
					r1.addItem(weap);
				}
				else if(item1.compareTo("angrymonkey") == 0 )
				{
					//System.out.println(count + " " + item1);
					int hp = 125;
					if(CaveGameView.diff_level.compareTo("hard") == 0){hp = 175;}
					Warrior weap = new Warrior(hp, r1, item1);
					weap.setName(item1);
					r1.addWarrior(weap);
				}
				else if(item1.compareTo("clone") == 0)
				{
					//System.out.println(count + " " + item1);
					int hp = 75;
					if(CaveGameView.diff_level.compareTo("hard") == 0){hp = 125;}
					Warrior weap = new Warrior(hp, r1, item1);
					weap.setName(item1);
					r1.addWarrior(weap);
				}
				
			}
			
		}
		
		roomsTot[count] = r1;
		count++;
		roomsDis.add(moves);
		//System.out.println(roomsDis.size());
	}
	public Vector parseItems(String a){
		Vector items = new Vector();
		int count = 1;
		char num = a.charAt(0);
		if(num != '0')
		{
			while(count <= a.length()-1)
			{
				//System.out.println("Chat at 1 "+ a.charAt(2));
			    String it = "";
			    while(count <= a.length()-1 && a.charAt(count) != ',')
				{
					it = (it + a.charAt(count));
					
						count++;
					
				}
			    //System.out.println(it);
				items.add(it);
				
				count++;	
			}
		}
		
		return items;
	}
	public Vector parseMoves(String a){
		//System.out.println(a);
		Vector moves = new Vector();
		int count = 0;
		
		while(count < a.length())
		{
			String temp = "";
			while(count < a.length() && a.charAt(count) != ',')
			{
				temp = temp + a.charAt(count);
				count++;
			}	
			moves.add(temp);
			count++;
			
		}
		
		return moves;
	}
	public void setLoadItems()
	{
		
	
	}
	public Room[] getVectorRooms()
	{
		return roomsTot;
	}
	public int getRoomTotal()
	{
		return roomsDis.size();
	}
	public Room getRoomLoad(int a)
	{
		
		return roomsTot[a];
	}
	public Vector getSaveRooms()
	{
		Vector roomInfo = new Vector();
		for(int i = 0; i < roomsDis.size(); i++)
		{
			String str = "";
			ArrayList tempItem = roomsTot[i].getRoomItemDesc();
			if(tempItem.size() != 0 && tempItem != null)
			{
				for(int u = 0; u < tempItem.size(); u++)
				{
					str = str + tempItem.get(u) + ",";
				}
			}
			else
			{
				str = str + "none,";
				
			}
			str = str + "|";
			if(roomsTot[i].isWarrior())
			{
				if(roomsTot[i].getWarrior().getHealth() > 0)
				{
					str = str + "1|\n";
				}
				else
				{
					str = str + "0|\n";
				}
			}
			else{
				str = str + "0|\n";
			}
			roomInfo.add(str);
			
		}
		
		return roomInfo;
	}
	
	public void loadRoomsSaved(Vector a)
	{
		for(int i = 0; i < a.size(); i++)
		{
			Vector items = (Vector)a.get(i);
			roomsTot[i].removeAllItem();
			for(int b = 0; b < items.size()-1; b++)
			{
				if(((String)items.get(b)).compareTo("none") != 0)
				{
					Item tempItem = new Item();
					tempItem.setDesc((String)items.get(b));
					roomsTot[i].addItem(tempItem);
				}
			}
			if(roomsTot[i].isWarrior())
			{
				String tempStr = (String)items.get(items.size()-1);
				if(tempStr.compareTo("0") == 0)
				{
					roomsTot[i].getWarrior().setHealth(0);
				}
			}
		}
		
	}

}
