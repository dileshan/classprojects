package a2;

public class Warrior 
{
	private int health;
	private Room wLoc;
	private String name;
	private Item[] wThings = new Item[1];
	
	public Warrior(int i, Room r, String s)
	{
		health = i;
		wLoc = r;
		name = s;
	}
	
	public void setHealth(int h)
	{
		health = h;
	}
	
	public int getHealth()
	{
		return health;
	}
	
	public void setWLoc(Room r)
	{
		wLoc = r;
	}
	
	public Room getWLoc()
	{
		return wLoc;
	}
	
	public void setName(String n)
	{
		name = n;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getPic()
	{
		int picNum = 0;
		
		if(name.compareTo("angrymonkey") == 0)
		{
			if(this.getHealth() == (health))
				picNum = 1;
			else if(this.getHealth() < (health))
				picNum = 2;
			else if(this.getHealth() == 0)
				picNum = 3;
		}
		else if(name.compareTo("clone") == 0)
		{
			if(this.getHealth() == (health))
				picNum = 4;
			else if(this.getHealth() < (health))
				picNum = 5;
			else if(this.getHealth() == 0)
				picNum = 6;
		}
		return picNum;
	}
}
