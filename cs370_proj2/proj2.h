#ifndef PROJ2_H_
#define PROJ2_H_
#include <string>
#include <cstring>
using namespace std;

class Client
{
public:
	Client();
	void startClient(Client &);
		
	
private:
	vector<string> dvdList;
	vector<string> success;
	vector<string> transNum;
	vector<string> message;
	vector<string> request;
	int i;
	
};

class Dvd
{
public:
	Dvd();
	Dvd(string &, int);
	
	void setCop(int);
	int getCop();
	
	string getTitle();
	
private:
	string title;
	int copies;
};
#endif /*PROJ2_H_*/
