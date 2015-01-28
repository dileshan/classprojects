
#include <iostream>
#include <string>
using namespace std;

class nifty{
private:
	string *personality;
	int talents;

public:
	nifty();
	nifty(char *);
	nifty(const nifty &);
	int getTalents();
	char* getPersonality();
	nifty operator =(nifty &);
	operator int();
};
ostream &operator<<(ostream &out, nifty &a);

