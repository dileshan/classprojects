//Adam Schaffner & Matt DeSilvey
//eID shifty10
//12-4-07
//Extra Credit Parking Ticket

#include <string>
#include <vector>
#include <iostream>

using namespace std;

class PayTix
{
public:
	//constructor
	PayTix(int, int, string);
	PayTix(vector<PayTix*> &);
	//getters
	int getTicketNum();
	int getInfractionNum();
	string getPlateNum();
	//setters
	void setVectorPayTix(vector<PayTix*> &);
	void setTicketNum(int);
	void setInfractionNum(int);
	void setPlateNum(string);
	void payTicketN(string);
	void payTicketP(string);
	void printTicket();
	int ticketNum;
	int infractionNum;
	string plateNum;
private:
	//any methods that manipulate the tix and deal with paying them
	//any variables that go just with the above methods
	int userTicNum;
	vector<PayTix*> vectorPtrs;
};
