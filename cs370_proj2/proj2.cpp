/***************************************************************************
 * Names: Matt DeSilvey
 * 		  Matt Floyd
 * 
 * Date: 11/9/07
 * class: cs370
 * project: 2 
 */


#include <iostream>
#include <iomanip>
#include <fstream>
#include <pthread.h>
#include <vector>
#include <string>
#include <semaphore.h>
#include <stdlib.h>
using namespace std;

#include "proj2.h"

int numClients;//number of clients
float prob;//Probablility
int trans;//transactions
static int TransID = 1000;
sem_t mutex;

vector<string> dvd;
vector<int> numDvd;

/*
int dvd_checkout(string title)
{
	int sucFail = 0;
	TransID++;
	sem_wait(&mutex);
	for(int u = 0; u < dvd.size(); u++)
	{
		Dvd dog = dvd[u];
		if(title == dog.getTitle())
		{
			
			int copies = dog.getCop();
			if(copies > 0)
			{
				sucFail = 1;
				dog.setCop(copies-1);
			}
			//write to file for transaction and for client log
		}
		
	}
		
	sem_post(&mutex);
	return sucFail;
}

int dvd_return(string title)
{
	TransID++;
	int sucFail2 = 0;
	sem_wait(&mutex);
	for(int t = 0; t < dvd.size(); t++)
		{
		Dvd dawg = dvd[t];
			if(title == dawg.getTitle())
			{
				
				int copies = dawg.getCop();
				sucFail2 = 1;
				dawg.setCop(copies+1);
				
				//write to file for transaction and for client log
			}
			
		}
	
	sem_post(&mutex);
	
	return sucFail2;
}*/

int main(int argc, char *argv[])
{
	int Size = 200;
	char arr[Size];
	if((argv[0] == NULL) || (argv[1] == NULL) || (argv[2] == NULL) || (argv[3] == NULL) || (argv[4] == NULL))
	{
		cout << "Must enter the command line arugments, as in the README." << endl;
		exit(1);
	}
	
	ifstream inFile(argv[argc-1]);
	
	cout << inFile << endl;
	
	if(!inFile)
	{
		cout << "Error with opening file, file not found!" << endl;
		exit(1);
	}
	
	int checkInt;
	float checkInt2;
	int checkInt3;
	
	checkInt = atoi(argv[1]);
	checkInt2 = atof(argv[2]);
	checkInt3 = atoi(argv[3]);	
	
	if(checkInt > 0 && (checkInt2 > 0 && checkInt2 < 1) && checkInt3 > 0)
	{
		numClients = checkInt;
		prob = checkInt2;
		trans = checkInt3;	
		cout << numClients << endl;
		cout << prob << endl;
		cout << trans << endl;
	}
	else
	{
		bool exitCheck = false;
		

		if(checkInt < 0)
		{
			cout << "Number of Clients must be a positive integer" << endl;
			exitCheck = true;
		}		
		if(checkInt2 < 0 ||  checkInt > 1)
		{
			cout << "Probablility must be a percentage, a number between 0-1" << endl;
			exitCheck = true;
		}
		if(checkInt3 < 0)
		{
			cout << "Number of transactions must a positive integer" << endl;
			exitCheck = true;
		}
		if(exitCheck)
		{
			exit(1);
		}
	}
	
	
	inFile.getline(arr, Size);//skip first line
	inFile.getline(arr, Size);
	while(!inFile.eof())
	{
		string name;
		string num;
		string strsub;
		int numTemp;
		//********************for check varibles
		string ch = "";
		int count = -1;
		name = strtok(arr, "");
		
		for(int i = name.size()-1; i >= 0; i--)
		{
			string ch;
			ch = name[i];
			count++;
			if(ch == "\"")
			{
				i = -1;//exit for loop
			}
			
		}
		
		if(count == 2)
		{
			num = name.at(name.size()-1);
			numTemp = atoi(num.c_str());
			name.erase(name.size()-2, 2);
		}
		if(count == 3)
		{
			num = name.substr(name.size()-2, 2);
			numTemp = atoi(num.c_str());
			name.erase(name.size()-3, 3);
		}
		
		//create a dvd object//
		dvd.push_back(name);
		numDvd.push_back(numTemp);
		
		inFile.getline(arr, Size);
	}
	for(int i = 0;i < dvd.size(); i++)
	{
		cout << dvd[i] << endl;
	}
	for(int i = 0; i < numDvd.size(); i ++)
	{
		cout << numDvd[i] << endl;
	}

	//Create specified number of threads
	
	/*pthread_t threadID[numClients];
	pthread_attr_t thread_attr;
	
	for(int i = 0; i < numClients; i++)
	{		
		sem_init(&mutex, 0, 1);
		
		Client cli;
		pthread_create(&threadID[i], thread_attr, (void*)cli.startClient, (void*)cli);
		pthread_join(&threadID, NULL);//wait for threads to finish
		
		sem_destroy(&mutex);
		
	}
		*/
	return 0;
}















