/*
Matt Floyd 
Matt Desilvey
cs370
Project 3
*/

#include <iostream>
#include <iomanip>
#include <fstream> // file stream
#include <string>
#include <cstdlib>
#include <vector>

using namespace std;
#include "proj3.h"


// Main Function
int main(int argc, char *argv[])
{
	//declaring variables
	int MAX_BLOCKS = 80000000;
	int track = 1000;
	int tracksPerCyl = 4;
	int cylinders = 20000;
	vector<int> numCyl;
	double avg = 0;
	vector<int> cacheLookUp;
	
	//cout << argc << endl;
	//cout << argv << endl;
	string schedPolicy;
	string one;
	string two;
	string three;
	string four;
	int cacheSize = 0;
	int readAhead = 0;
	int sysLoad = 0;
	int iterations = 100000;
	//check to see if there is enough arguements, if not then print out and exit
	if(argc == 6)
	{
		//read in from commandline
		schedPolicy = argv[1];
		if(schedPolicy != "FCFS" && schedPolicy != "SSTF" && schedPolicy != "CLOOK")
		{
			cout << "Invalid scheduling policy must be(FCFS, SSTF, or CLOOK)" << endl;
			exit(0); 
		}
		one = argv[2];
		cacheSize = atoi(one.c_str());
		if(cacheSize != 2 && cacheSize != 8 && cacheSize != 16 && cacheSize != 32)
		{
			cout << "Invalid cache size must be(2, 8, 16, or 32)" << endl;
			exit(0);
		}
		two = argv[3];
		readAhead = atoi(two.c_str());
		if(readAhead > ((cacheSize*1000000)/512) || readAhead < 0)
		{
			cout << "Invalid read ahead number" << endl;
			exit(0);
		}
		three = argv[4];
		sysLoad = atoi(three.c_str());
		
		four = argv[5];
		iterations = atoi(four.c_str());
		cout << schedPolicy << " " << cacheSize << " " << readAhead << " " << sysLoad << " " << iterations << endl; 
		vector<int> requests;
		int counter = sysLoad;
		fcfs f;
		f.setMax_Blocks(MAX_BLOCKS);
		//clook c;
		//sstf s;
		for(int i = 0; i < iterations; i++)
		{
		
			int num = rand() % MAX_BLOCKS;
			//cout << num << endl;
			requests.push_back(num);
			
			
			if(i == counter)
			{
				if(schedPolicy == "FCFS")
				{
					//cout << schedPolicy << endl;
					f.setRequests(requests);
					f.findCylinders();
					
					//cout << "Sysload: " << counter << endl;
				}
				else if(schedPolicy == "SSTF")
				{
					cout << schedPolicy << endl;
					for(int r = 0; r < requests.size(); r++)
					{
						//cout << requests[r] << endl;
					}
					
					cout << "Sysload: " << counter << endl;
				}
				else if(schedPolicy == "CLOOK")
				{
			
					cout << schedPolicy << endl;
					for(int r = 0; r < requests.size(); r++)
					{
						//cout << requests[r] << endl;
					}
					
					cout << "Sysload: " << counter << endl;
				}		
				counter = counter + sysLoad;
			}
			
		
		}
		numCyl = f.getNumCyli();
		cout << "Size: "<< numCyl.size() << endl;
		
		for(int y = 0; y < numCyl.size(); y++)
		{
			if(requests[y] < 1000)
			{
				cout << numCyl[y] << endl;
			
			}
		}
		
		
	}
	else
	{
		cout << "You didnt enter all the command line arguements" << endl;
	}
	



	return 0;
}
