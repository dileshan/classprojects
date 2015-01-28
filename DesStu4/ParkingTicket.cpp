#include <iostream>
#include <fstream>
#include <string>
#include <vector>
 
#include "Extra.h" //including the header file
 
using namespace std;
 
int ticNum, fineCode;
char tN[7];
char fC[3];
char pltNum[7];
PayTix* ticketPtr;
vector<PayTix*> tixV;
string response, tNumber, pNumber;
 
void input()
{
	PayTix *checkVector = new PayTix(tixV);
	cout << "Would you like to enter a ticket number(Y/N)? " << endl;
	cin>> response;
	if(response == "Y" || response == "y")
	{
		cout << "Please enter your ticket number. " << endl;
		cin >> tNumber;
		checkVector->payTicketN(tNumber);
	}
	else if(response == "N" || response == "n")
	{
		cout << "Would you like to enter a license plate number(Y/N)? " << endl;
		cin >> response;
		if(response == "Y" || response == "y")
		{
			cout << "Please enter your license plate number. " << endl;
			cin >> pNumber;
			checkVector->payTicketP(pNumber);
			
			cout << endl;
			
		}
		else if(response == "N" || response == "n")
		{
			input();
		}
	}
}//end input
int main()
{
	ifstream inputFILE("TicketInfo.txt", ios::in);
	
	if(!inputFILE)
	{
		cerr << "There was a problem reading the file. " << endl;
		exit(-1);
	}
	
	while(!inputFILE.eof())
	{
		inputFILE.getline(tN, 7, ',');
		inputFILE.getline(fC, 3, ',');
		inputFILE.getline(pltNum, 7);
		ticNum = atoi(tN);
		fineCode = atoi(fC);
		ticketPtr = new PayTix(ticNum, fineCode, pltNum);
		tixV.push_back(ticketPtr);
	}
	input();
 
	delete ticketPtr;
	exit(-1);
}//end main