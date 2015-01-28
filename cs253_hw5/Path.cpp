/********************************************************************************
 * Name: Matt DeSilvey
 * Date: 11/30/07
 * Program: 5
 * See readme for details on input and output
 */

#include <string>
#include <iostream>
#include <vector>
using namespace std;

#include "train.h"

Path::Path()
{

}

Path::Path(string &_start, string &_end, vector<Zone*> &_zones, vector<string> &_routes)
{
	//Set vectors
	setStart(_start);
	setEnd(_end);
	setVectorPtrZone(_zones);
	setVectorRoutes(_routes);
}

//Getters and setters
void Path::setStart(string &_start)
{
	start = _start;
}

string Path::getStart()
{
	return start;
}

void Path::setEnd(string &_end)
{
	end = _end;
}

string Path::getEnd()
{
	return end;
}

void Path::setVectorPtrZone(vector<Zone*> &_zones)
{
	for(int i = 0; i < _zones.size(); i++)
	{
		Zone *tempPtr = _zones[i];
		//Add Ptr to private vector
		zones.push_back(tempPtr);
	}
}

vector<Zone*> Path::getVectorPtrZones()
{
	return zones;
}

void Path::setVectorRoutes(vector<string> &_routes)
{
	for(int i = 0; i < _routes.size(); i++)
	{
		string tempStr = _routes[i];
		routes.push_back(tempStr);
	}
}

vector<string> Path::getVectorRoutes()
{
	return routes;
}
//Method find path no arugments or parameters has a function call to another recursive method
void Path::findPath()
{
	//Find path using the start and end nodes and based
	string tempStart = getStart();
	string tempEnd = getEnd();
	vector<string> vistedNodes;
	vector<string> routeTemp;

	vistedNodes.push_back(tempStart);

	if(tempStart.compare(tempEnd) == 0)
	{
		cout << "Path found: \n"
			<< "Base station same as End station!\n"
			<< tempStart << "->" << tempEnd << endl;
	}
	else{
		for(int i = 0; i < routes.size(); i++)
		{
			string tempCheckOrigin = routes[i];
			string tempCheckDestin = routes[i+1];

			if(tempStart.compare(tempCheckOrigin) == 0)
			{
				vistedNodes.push_back(tempCheckDestin);
				routeTemp = findPathRec(tempCheckDestin, vistedNodes);
				break;
			}
			i += 3;
		}
		if(routeTemp.empty())
		{
			cout << "Can't find route to destination!"
				<< tempStart << "->" << tempEnd << endl;
		}
		else{
			cout << "Path found!" << endl;
			cout << "Listed routes to destination: " << endl;
			for(int i = 0; i < routeTemp.size(); i++)
			{
				cout << routeTemp[i] << endl;
			}
		}
	}
}
//recursive method to determine a path specified by the user
vector<string> Path::findPathRec(string &_start, vector<string> &_visted)
{
	vector<string> tempVisted;
	for(int k = 0; k < _visted.size(); k++)
	{
		string temp = _visted[k];
		tempVisted.push_back(temp);
	}
	string end = getEnd();

	if(_start.compare(end) == 0)
	{
		return tempVisted;
	}
	else{
		for(int i = 0; i < routes.size(); i++)
		{
			string tempCheckOrigin = routes[i];
			string tempCheckDestin = routes[i+1];
			//Checking new start to route vector
			if(_start.compare(tempCheckOrigin) == 0)
			{
				bool visted = false;
				for(int j = 0; j < tempVisted.size(); j++)
				{
					string tempyTemp = tempVisted[j];
					if(tempCheckDestin.compare(tempyTemp) == 0)
					{
						visted = true;
					}
				}
				if(!visted)
				{
						tempVisted.push_back(tempCheckDestin);
						return findPathRec(tempCheckDestin, tempVisted);
				}
			}
			i += 3;
		}

	}
	vector<string> empty;
	return empty;
}