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


//default constructor
Station::Station()
{

}

Station::Station(vector<string> &_stat)
{
	setStations(_stat);
}
//Main base constructor
Station::Station(string &stat, string &closed, string &pri)
{
	setStation(stat);
	setClosed(closed);
	setPriStation(pri);
}
	

/*****************************************************
 Getters and setters
 *****************************************************/
void Station::setStation(string &_station)
{
	station = _station;
}

string Station::getStation()
{
	return station;
}

void Station::setClosed(string &_closed)
{
	closed = _closed;
}

string Station::getClosed()
{
	return closed;
}

void Station::setPriStation(string &_primary)
{
	primary = _primary;
}

string Station::getPriStation()
{
	return primary;
}
//**************************************//

string Station::getStationNVector()
{
	string tempStations = "";
	for(int i = 0; i < stats.size(); i++)
	{
		tempStations = stats[i];
		if(tempStations.compare("") != 0)
		{
			tempStations += stats[i] + "\n";
			i += 2;
		}
	}
	return tempStations;
}

string Station::getAbvNVector()
{
	string tempAbv = "";
	for(int i = 0; i < stats.size(); i++)
	{
		tempAbv = stats[i];
		if(tempAbv.compare("") != 0)
		{
			tempAbv += stats[i+1] + "\n";
			i += 2;
		}
	}
	return tempAbv;
}

string Station::getClosedNVector()
{
	string tempClose = "";
	for(int i = 0; i < stats.size(); i++)
	{
		tempClose = stats[i];
		if(tempClose.compare("") != 0)
		{
			tempClose += stats[i+2] + "\n";
			i +=2;
		}
	}
	return tempClose;
}

void Station::printStations()
{
	//Print out all information about the station
	cout << "\nGiven stations: " << endl;
	for(int i = 0; i < stats.size(); i++)
	{
		string checkStr = stats[i];
		
		if(checkStr.compare("") != 0)
		{
			cout << "Station: " << stats[i] << endl;
			cout << "Closed: " << stats[i+1] << endl;
			cout << "Primary: " << stats[i+2] << endl;
			cout << endl;
			i += 2;
		}
	}
}

void Station::setStations(vector<string> &_station)
{
	//set vector in private vector.
	for(int i = 0; i < _station.size(); i++)
	{
		string temp = _station[i];
		stats.push_back(temp);
	}
}

string Station::getStations()
{
	//print out the stations
	//return type string print cout in main
	string tempStr = "";
	for(int i = 0; i < stats.size(); i++)
	{
		tempStr = stats[i];
		if(tempStr.compare("") != 0)
		{
			tempStr += stats[i] + "\n";
			i += 2;
		}
	}
	return tempStr;
}


