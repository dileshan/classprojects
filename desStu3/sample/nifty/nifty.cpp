/*************
 * Nifty 
 * November 30, 2007
 * Matt Floyd
 * David Becker
 * Matt DeSilvey
 *************/

#include <iostream>
#include <iomanip>
#include <cstring>

using namespace std;
#include "nifty.h"

nifty::nifty()
{
	personality = "";
	talents = 0;
}

nifty::nifty(char *str)
{
	personality = str;
	talents = 0;
}

nifty::nifty(const nifty &n)
{
	personality = n.personality;
	talents = n.talents;
}

char* nifty::getPersonality()
{
	return personality;
}

int nifty::getTalents()
{
	return talents;
}

nifty nifty::operator =(nifty &g)
{
	personality = g.getPersonality();	
}

nifty::operator int()
{
	return talents;
}

ostream &operator <<(ostream &out, nifty &n)
{
	out << n.getPersonality() << " " << n.getTalents() << endl;
	return out;
}