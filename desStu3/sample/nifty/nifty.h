/*************
 * Nifty 
 * November 30, 2007
 * Matt Floyd
 * David Becker
 * Matt DeSilvey
 *************/

#ifndef	_NIFTY_H_
#define	_NIFTY_H_

#include <iostream>
#include <iomanip>
#include <cstring>

using namespace std;

class nifty
{
	public:
		nifty();					//default constructor
		nifty(char*);				//constructor
		nifty(const nifty&);		//copy constructor
		nifty operator =(nifty&);	//operator =
		operator int();			//operator int
		char* getPersonality();		//returns personality
		int getTalents();			//returns talents

	private:
		char *personality;
		int talents;

};

ostream &operator <<(ostream &out, nifty &n);

#endif