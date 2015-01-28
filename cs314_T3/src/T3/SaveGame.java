package a2;


import java.util.*;
import java.io.*;


public class SaveGame implements Serializable
{
	private Room p_loc;
	private Weapons p_weapon;
	private int p_health;
	private ArrayList p_items;
	private Room[] c_rooms;
	public File f_name;
	private Vector rooms_Info;
	
	public SaveGame(Room r, Item a, int ph, ArrayList pi, Room[] ar, long name, String character, String diff, Vector temp)
	{
		p_loc = r;
		p_weapon = (Weapons)a;
		p_health = ph;
		p_items = pi;
		c_rooms = ar;

		try {
	        BufferedWriter out = new BufferedWriter(new FileWriter("SaveGame"+ name+ ".txt"));
	        String savedGame = "SaveGame"+name+".txt";
	        SaveNames(savedGame);
	        String info = character +"|" + p_loc.getNum() + "|" + p_weapon.getDesc() + "|" + p_weapon.getStrength() + "|" + ph + "|" + diff + "|";
	        for(int i = 0; i < pi.size(); i++)
	        {
	        	info = info + pi.get(i) + ",";
	        }
	        info = info + "|\n";
	        out.write(info);
	        for(int i = 0; i < temp.size(); i++)
	        {
	        	out.write((String)temp.get(i));
	        }
	        out.close();
	    } catch (IOException e) {
	    }
	}
	
	public void SaveNames(String s) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter("SavedGames.txt", true));
		out.write(s+"\n");
		out.close();
	}
	
}
