/********************************************************************************
 * Name: Matt DeSilvey
 * Date: 11/30/07
 * Program: 5
 * See readme for details on input and output
 */
#ifndef TRAIN_
#define TRAIN_

#include <string>
#include <iostream>
#include <vector>
using namespace std;

class ParseCheck{
public:
	ParseCheck();
	ParseCheck(string &);
	
	void setLine(string &);
	string getLine();

	bool errorCheck(string &);
	void printErrors();
	
private:
	string line;
	string new_str;
	string str;
	string str2;
};

class Route{
public:
	//public methods and declartions
	Route();
	Route(vector<string> &);
	Route(string &, string &, string &, string &);

	void setOrigin(string &); 
	string getOrigin();

	void setDestin(string &);
	string getDestin();

	void setRouteClosed(string &);
	string getRouteClosed();

	void setPriRoute(string &);
	string getPriRoute();

	void openRoute(string &, string &);
	void closeRoute(string &, string &);
	
	void printRoutes();
	void setRoutes(vector<string> &);
	
private:
	//private methods and declartions
	vector<string> route;
	string origin;
	string destin;
	string closed;
	string primary;

};

class Station{
public:
	Station();
	Station(vector<string> &);
	Station(string &, string &, string &);

	//***********************************************
	void setStation(string &);
	string getStation();

	void setClosed(string &);
	string getClosed();

	void setPriStation(string &);
	string getPriStation();
	//***********************************************

	string getStationNVector();//Name of station
	string getAbvNVector();//Name of Abrvation
	string getClosedNVector();//Type of Closer
	
	//Set the station vector
	void setStations(vector<string> &);

	void printStations();//print stations
	string getStations();
		
private:

	vector<string> stats;
	string station;
	string closed;
	string primary;
};

class Zone{
public:
	Zone();	
	Zone(string &, vector<string> &);
	
	void setNameOfZone(string &);
	string getNameOfZone();

	void setStationsInZone(vector<string> &);
	vector<string> getStations();

	void printZones();
	
private:
	vector<string> stations;
	string NameOfZone;
};

class Path{
public:
	Path();
	Path(string &, string &, vector<Zone*> &, vector<string> &);

	void setStart(string &);
	string getStart();

	void setEnd(string &);
	string getEnd();

	void setVectorRoutes(vector<string> &);
	vector<string> getVectorRoutes();

	void setVectorPtrZone(vector<Zone*> &);
	vector<Zone*> getVectorPtrZones();

	void findPath();
	vector<string> findPathRec(string &, vector<string> &);

private:
	vector<Zone*> zones;
	vector<string> routes;
	string start;
	string end;
};

#endif TRAIN_






