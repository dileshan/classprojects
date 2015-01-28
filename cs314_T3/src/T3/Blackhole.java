package a2;

/**  Cave Game  Program Code
     Copyright (c) 1999 James M. Bieman

     To compile: javac CaveGame.java
     To run:     java CaveGame

     The main routine is CaveGame.main
				    
**/

// class Blackhole

public class Blackhole implements CaveSite
{

  /** The blackhole's location. */
  private CaveSite outSite;
  private CaveSite inSite;

  /** We can construct a blackhole at the site. */
  Blackhole(CaveSite out, CaveSite in)
  {
    outSite = out;
    inSite = in;
  }

 public int enter(Player p)
 {
    if (p.getLoc() == outSite)
    {
    	inSite.enter(p);
    }
    else if (p.getLoc() == inSite) 
    {
    	outSite.enter(p); 
    }
    return 4;
  }

}

