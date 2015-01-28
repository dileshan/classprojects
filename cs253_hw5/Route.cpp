/********************************************************************************
 * Name: Matt DeSilvey
 * Date: 11/30/07
 * Program: 5
 * See readme for details on input and output
 */
#include <iostream>
#include <iomanip>
#include <string>
#include <vector>
using namespace std;

#include "train.h"

Route::Route()
{

}

Route::Route(vector<string> &_route)
{
	setRoutes(_route);
}

Route::Route(string &_org, string &_des, string &_closed, string &_pri)
{
	setOrigin(_org);
	setDestin(_des);
	setRouteClosed(_closed);
	setPriRoute(_pri);
}


void Route::setOrigin(string &_origin)
{
	origin = _origin;
}
string Route::getOrigin()
{
	return origin;
}

void Route::setDestin(string &_destin)
{
	destin = _destin;
}

string Route::getDestin()
{
	return destin;
}

void Route::setRouteClosed(string &_closed)
{
	closed = _closed;
}

string Route::getRouteClosed()
{
	return closed;
}

void Route::setPriRoute(string &_primary)
{
	primary = _primary;
}

string Route::getPriRoute()
{
	return primary;
}

//Open a route if need be
void Route::openRoute(string &sr, string &sr2)
{
	for (int i = 0; i < route.size(); i++) {
		string compare1 = route[i];
		string compare2 = route[i+1];
		
		if(compare2.compare("") != 0)
		{
			if (sr.compare(compare1) == 0 && sr2.compare(compare2) == 0) {

				string closed = route[i+2];
				string no = "no";//set no which means open
				if (closed.compare(no) == 0) 
				{
					cout << "Route already set to open" << endl;					
				} 
				else 
				{
					string insert = "no";
					//change route to open
					route[i+2] = insert;
					//temporally print out the vector
					cout << "Route is now open." << endl;
				}
			}i += 3;
		}
	}
}

//Close a route as long as its not a primary route
void Route::closeRoute(string &sr, string &sr2)
{
	for (int i = 0; i < route.size(); i++) {
		string compare1 = route[i];
		string compare2 = route[i+1];
		string primary = route[i+3];//check if its a primary route

		if((compare2.compare("") != 0) && (primary.compare("") != 0))
		{
			if (sr.compare(compare1) == 0 && sr2.compare(compare2) == 0) 
			{
				//set route to close
				string closed = route[i+2];
				string no = "yes";//set yes which means closed
				
				if(primary.compare("no") == 0)
				{
					if (closed.compare(no) == 0) {
						cout << "Route already set to closed" << endl;
					} else {
						string insert = "yes";
						//change route to closed
						route[i+2] = insert;
						cout << "Route is now closed." << endl;
					}
				}
				else{
					cout << "Can't change a primary route between 0000 and 0600 hours" << endl;
				}
			}i += 3;
		}
	}
}

void Route::printRoutes()
{
	cout << "Routes: \n" << endl;
	for (int i = 0; i < route.size(); i++)
	{
		string checkStr = route[i];

		if (checkStr.compare("") != 0) {
			cout << "Origin: " << route[i] << endl;
			cout << "Destination: " << route[i+1] << endl;
			cout << "Closed: " << route[i+2] << endl;
			cout << "Primary: " << route[i+3] << endl;
			cout << endl;
			i += 3;
		}
	}
}

void Route::setRoutes(vector<string> &_route)
{
	//set vector in private vector.
	for(int i = 0; i < _route.size(); i++)
	{
		string temp = _route[i];
		route.push_back(temp);
	}
}

