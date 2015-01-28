#include <iostream>
#include <iomanip>
#include <fstream>
#include <pthread.h>
#include <vector>
#include <string>

using namespace std;

#include "proj3.h"

fcfs::fcfs()
{

}

fcfs::fcfs(vector<int> &reqs, vector<int> &numCyl, int r)
{
	setRequests(reqs);
	setNumCyli(numCyl);
	
	findCylinders();
	readAhead = r;
	
} 

void fcfs::setRequests(vector<int> &one)
{
	requests.clear();
	for(int i = 0; i < one.size(); i++)
	{
		int temp = one[i];
		requests.push_back(temp);
	}
		
}
void fcfs::setNumCyli(vector<int> &two)
{
	for(int i = 0; i < two.size(); i++)
	{
		int temp = two[i];
		numCyli.push_back(temp);
	}
}

void fcfs::setMax_Blocks(int MAX_BLOCKS)
{
	max_bigNum = MAX_BLOCKS;
}

vector<int> fcfs::getNumCyli()
{
	return numCyli;
}
void fcfs::findCylinders()
{
	while(requests.size() != 0)	
	{
		int count = 0;
		if(cache.size() != 0)
		{
			/*for(int i = 0; i < cache.size(); i++)
			{
				if(requests[0] == cache[i])
				{
					numCyli.push_back(0);
					requests.erase(requests.begin(),requests.begin()+1);
					count++;
				}
			}*/
			if((requests[0] >= cache[0]) && (requests[0] <= cache[cache.size()-1]))
			{
				numCyli.push_back(0);
				requests.erase(requests.begin(),requests.begin()+1);
				count++;
			}
	
		}
		if(count == 0)
		{
			cache.clear();
			int findNum = 0;
			findNum = (requests[0] / 4000) +1;
			cout << findNum << endl;
				
			for(int i = requests[0]; i < readAhead; i++)
			{
				cache.push_back(i);
			}
			requests.erase(requests.begin(),requests.begin()+1);
					
				
			
			numCyli.push_back(findNum);			
		}
		
		
	}	


}

