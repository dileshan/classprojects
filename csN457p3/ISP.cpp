


#include <sys/socket.h> 
#include <arpa/inet.h>  
#include <stdio.h>
#include <stdlib.h>   
#include <string.h>   
#include <unistd.h>     
#include <errno.h>
#include <iostream>
#include <vector>
#include <string>
using namespace std;


#include "message.h"


ISP::ISP(vector<int> newV, int _as, int _range)
{
	prefixes = newV;
	as = _as;
	range = _range;
}

vector<int> ISP::getAS()
{
	return as;
}

vector<int> ISP::getRange()
{
	return range;
}

vector<int> ISP::getVectorIP()
{
	return prefixes;
}

//return a -1 if number doesn't exist
//returns the prefix 
vector<int> ISP::getPrefix(int compare)
{
	int tmp;
	int countError = 0;
	for(int i = 0; i < prefixes.size(); i++)
	{
		tmp = prefixes.at(i);
		if(tmp == compare)
		{
			return tmp;
		}
		else{
			countError++;
		}
	}
	
	if(countError == prefixes.size())
		return -1;
}

void ISP::updatePrefixes(int updateIP)
{
	prefixes.push_back(updateIP);
}

void ISP::uddateAS(int updateAS)
{
	as.push_back(updateAS);
}

void ISP::updateRange(int updateRange)
{
	range.push_back(updateRange);
}
