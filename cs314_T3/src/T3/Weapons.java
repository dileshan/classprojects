package a2;

//Weapons Class

public class Weapons extends Item
{
	private int strength;
	
	public void Weapons(int _strength)
	{
		strength = _strength;
	}
	
	public void setStrength(int _strength)
	{
		strength = _strength;
	}
	
	public int getStrength()
	{
		return strength;
	}
}
