
#include <iostream>
#include <string>
#include <cstring>
using namespace std;

#include "nifty.h"
//nifty class

//default constructor
nifty::nifty()
{
	personality = "";
	talents = 5;
}

nifty::nifty(string *str)
{
	personality = str;
	talents = 10;
}

//copy constructor
nifty::nifty(nifty &x)
{
	string temp =(nifty) x;
	int length = strlen(temp)+1;
	personality = new char[length];
	strcpy(personality, str);
	talents = x.talents;
}

char* nifty::getPersonality()
{
	return personality;
}

int nifty::getTalents()
{
	return talents;
}

nifty nifty::operator =(nifty &a)
{
	string temp = (nifty)a;
	int length = strlen(temp)+1;
	personality = new char[length];
	strcpy(personality, a);
	talents = a.getTalents();
}

nifty::operator int()
{
	return talents;
}

ostream &operator<<(ostream &out, nifty &a)
{
	out << a.getPersonality() << " " << a.getTalents() << endl;
	return out;
}
