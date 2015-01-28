//Adam Schaffner & Matt DeSilvey
//eID shifty10
//12-4-07
//Extra Credit Parking Ticket

#include <iostream>
#include <sstream>
#include "Extra.h" //including the header file
#include <stdio.h>
#include <stdlib.h>

using namespace std;

PayTix::PayTix(int t, int i, string p)
{
	setTicketNum(t);
	setInfractionNum(i);
	setPlateNum(p);
}

PayTix::PayTix(vector<PayTix*> &vect)
{
	setVectorPayTix(vect);
}

void PayTix::setVectorPayTix(vector<PayTix*> &vect)
{
	for(int i = 0; i < vect.size(); i++)
	{
		PayTix *temp = vect[i];
		vectorPtrs.push_back(temp);
	}
}

void PayTix::setTicketNum(int t)
{
	ticketNum = t;
}

int PayTix::getTicketNum()
{
	return ticketNum;
}
void PayTix::setInfractionNum(int i)
{
	infractionNum = i;
}
int PayTix::getInfractionNum()
{
	return infractionNum;
}
void PayTix::setPlateNum(string p)
{
	plateNum = p;
}
string PayTix::getPlateNum()
{
	return plateNum;
}
void PayTix::payTicketN(string n)
{
	//istringstream i(n);
	//n is number entered from user
	int check;
	string str = "";
	for(int i = 0; i < vectorPtrs.size(); i++)
	{	
		stringstream out;
		PayTix *temp = vectorPtrs[i];
		check = temp->getTicketNum();
		out << check;
		str = out.str();
		
		if(n.compare(str) == 0)
		{			
			temp->printTicket();
		}
	}
	
	
}
void PayTix::payTicketP(string n)
{
	
}

void PayTix::printTicket()
{
	int tempInf;
	tempInf = getInfractionNum();
	if(tempInf == 1)
	{
		//Print out the junk
		cout << "Ticket Number: " << getTicketNum() << endl;
		cout << "Infraction type: " << "Expired meter" << endl;
		cout << "Plate Number: " << getPlateNum() << endl;
		cout << "Fine: " << "$15.00" << endl;
	}
	else if(tempInf == 2)
	{
		cout << "Ticket Number: " << getTicketNum() << endl;
		cout << "Infraction type: " << "No parking permit" << endl;
		cout << "Plate Number: " << getPlateNum() << endl;
		cout << "Fine: " << "$35.00" << endl;
	}
	else if(tempInf == 3)
	{
		cout << "Ticket Number: " << getTicketNum() << endl;
		cout << "Infraction type: " << "Parking in handicap space" << endl;
		cout << "Plate Number: " << getPlateNum() << endl;
		cout << "Fine: " << "$1,000,000.00" << endl;
	}
	else if(tempInf == 4)
	{
		cout << "Ticket Number: " << getTicketNum() << endl;
		cout << "Infraction type: " << "Parking in fire lane" << endl;
		cout << "Plate Number: " << getPlateNum() << endl;
		cout << "Fine: " << "$50.00" << endl;
	}
	else
	{
		cout << "Invailid infraction type" << endl;
	}
}
