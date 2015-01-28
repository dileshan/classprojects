/********************************************************************************
 * Name: Matt DeSilvey
 * Date: 11/30/07
 * Program: 5
 * See readme for details on input and output
 */
#include <iostream>
#include <string>
#include <vector>
using namespace std;

#include "train.h"

Zone::Zone()
{
	
}

Zone::Zone(string &_zoneName, vector<string> &_stations)
{
	setNameOfZone(_zoneName);
	setStationsInZone(_stations);
}

void Zone::setNameOfZone(string &_NameOfZone)
{
	NameOfZone = _NameOfZone;
}

string Zone::getNameOfZone()
{
	return NameOfZone;
}

void Zone::setStationsInZone(vector<string> &_stations)
{
	//set stations
	for(int i = 0; i < _stations.size(); i++)
	{
		string strTemp = _stations[i];
		stations.push_back(strTemp);
	}
}

vector<string> Zone::getStations()
{
	return stations;
}

void Zone::printZones()
{
	cout << "Zone Name: " << getNameOfZone() << endl;
	cout << "Stations: " << endl;
	for(int i = 0; i < stations.size(); i++)
	{
		string checkTemp = stations[i];

		if(checkTemp.compare("") != 0)
		{
			cout << stations[i] << endl;
			cout << "Closed: " << stations[i+1] << endl;
			cout << endl;
			i += 2;
		}
	}
}
