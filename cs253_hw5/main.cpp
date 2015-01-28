/********************************************************************************
 * Name: Matt DeSilvey
 * Date: 11/30/07
 * Program: 5
 * See readme for details on input and output
 */
#include <iostream>
#include <iomanip>
#include <time.h>
#include <fstream>
#include <vector>
#include <string>
#include <stdexcept>
using namespace std;

#include "train.h"

//prototype methods
bool runError();
void runParser();
void userInteract();
bool runLine(string line);
void parseLines(string str, int lnCount);
void makeObjects(string str, int count, int which);

//Global varibles
//Use of global functions are very needed, main do to the fact that passing
//as parameters to every function is very tedious.
string firstZone;
//Routes
string origin;
string destination;
string status;
string primary;

//Stations
string station_1;
string station_status;
string station_primary;

vector<string> routeWork;
vector<string> stationWork;


bool runError() {
	const int Size = 500;
	string str_r = "";
	ifstream inFile("routes.txt");
	bool check;
	//create object parse


	if ( !inFile) // overloaded ! operator
	{
		cerr
				<< "File could not be opened, make sure the file is named routes.txt"
				<< endl;
		exit(1); // Exit out if no file
	}//end if

	char routes[Size];

	inFile.getline(routes, Size);
	while (!inFile.eof()) {
		check = runLine(routes);
		inFile.getline(routes, Size);
	}

	//inFile.close();

	return check;
}

bool runLine(string line) {
	bool good;

	//Create object for checking file
	ParseCheck inLine(line);

	good = inLine.errorCheck(line);
	if (!good) {
		inLine.printErrors();
		exit(1);
	} else {
		return good;
	}
}

void runParser() {
	const int Size = 500;
	int lineCounter = 0;
	//Input file
	ifstream nFile("routes.txt");

	if (!nFile) // overloaded ! operator
	{
		cerr
				<< "File could not be opened, make sure the file is named routes.txt"
				<< endl;
		exit(1); // Exit out if no file
	}//end if

	char routes[Size];

	nFile.getline(routes, Size);
	while (!nFile.eof()) {
		parseLines(routes, lineCounter);
		nFile.getline(routes, Size);
		lineCounter++;
	}

	//start parsing1void makeObjects(string str[], int count, int which)
}

void parseLines(string str, int lnCount) {
	const int size = 500;
	int strSize = str.size();
	char str_c[strSize];
	for (int i = 0; i < str.size(); i++) {
		str_c[i] = str[i];
	}

	string route[size];
	string station[size];

	char* prt_line;//line to be tokenized
	char* prt_stat;//Stations
	char* prt_rout;//Routes
	string rout1;
	string stat2;

	int index = 0;
	prt_line = strtok(str_c, " =[{;:|}]");

	if (lnCount == 0) {
		firstZone = str;

	}//do routes
	if (lnCount == 1) {
		int i = 0;
		int j = 50;//represents a route
		//break up the string
		while (prt_line != NULL) {
			//add string to array
			prt_rout = prt_line;
			rout1 = prt_rout;
			if (rout1 != "origin" && rout1 != "destination" && rout1
					!= "closed" && rout1 != "primary") {
				makeObjects(rout1, i, j);
			}
			if (i >= 7) {
				i = -1;//reset counter
			}
			prt_line = strtok(NULL, " =[{;:|}]");
			i++;
			index++;
		}
	}//do stations
	if (lnCount == 2) {
		int i = 0;
		int j = 60;//represents a station
		while (prt_line != NULL) {
			//add string to station array
			prt_stat = prt_line;
			stat2 = prt_stat;
			if (stat2 != "closed" && stat2 != "primary") {
				makeObjects(stat2, i, j);
			}
			if (i >= 5) {
				i = -1;
			}
			prt_line = strtok(NULL, " =[{;:|}]");
			i++;
			index++;
		}
	}
}

void makeObjects(string str, int count, int which) {
	string line = str;

	if (which == 50) {
		//first origin
		if (count == 1) {
			origin = line;
		}
		if (count == 3) {
			destination = line;
		}
		if (count == 5) {
			status = line;
		}
		if (count == 7) {
			primary = line;
		}
	}

	if (which == 60) {
		//do stations
		if (count == 1) {
			station_1 = line;
		}
		if (count == 3) {
			station_status = line;
		}
		if (count == 5) {
			station_primary = line;
		}
	}

	//make route objects
	if (which == 50 && count == 7) {
		string tempRoute_org;
		string tempOrg;
		string tempDes;
		string tempStat;
		string tempPri;
		//Route route(origin, destination, status, primary);
		
		//initial vector for pretty print
		//tempRoute_org = route.getRoute();
		//routeArr.push_back(tempRoute_org);

		//Create working vector
		tempOrg = origin;
		tempDes = destination;
		tempStat = status;
		tempPri = primary;

		routeWork.push_back(tempOrg);
		routeWork.push_back(tempDes);
		routeWork.push_back(tempStat);
		routeWork.push_back(tempPri);

		//set vars to empty
		origin = "";
		destination = "";
		status = "";
		primary = "";
	}
	//make station objects
	if (which == 60 && count == 5) {
		//create object station
		string tempStation;
		string tempStation1;
		string tempStation_stat;
		string tempStation_pri;
		//Station station1(station_1, station_status, station_primary);

		//tempStation = station1.getStations();
		//stationArr.push_back(tempStation);

		//Create working vector
		tempStation1 = station_1;
		tempStation_stat = station_status;
		tempStation_pri = station_primary;

		stationWork.push_back(tempStation1);
		stationWork.push_back(tempStation_stat);
		stationWork.push_back(tempStation_pri);

		//set vars to empty
		station_1 = "";
		station_status = "";
		station_primary = "";
	}
}

void userInteract() {

	//Create objects station, routes, and zones
	Route *routePtr = new Route(routeWork);
	Station *stationPtr = new Station(stationWork);
	Zone *zonePtr;
	vector<Route> routes_r;
	vector<Zone*> zones;//vector of zones
	vector<string> routeName;
	vector<Route*> stationsVect;

	zonePtr = new Zone(firstZone, stationWork);
	zones.push_back(zonePtr);//send in the file contents

	//move the file routes into the route vector that the user and use and process to find
	// a path in the system
	for(int i = 0; i < routeWork.size(); i++)
	{
		string temp = routeWork[i];
		routeName.push_back(temp);
	}

	//print out starting train system
	cout << "\nGiven routes: " << endl;
	routePtr->printRoutes();

	cout << "\nGiven stations: " << endl;
	stationPtr->printStations();
	
	string quit;
	string input;
	string exitCheck = "exit";

	cout << "-------------------------------menu------------------------------------\n";
	cout << "List of commands and there usages are as follows:\n"
		 << ">>exit: to exit the program at any given time\n"
		 << ">>print <routes> or <stations> or <zones>: prints out routes or stations or zone\n"
		 << ">>add <zone>: to add different zones\n"
		 << ">>find path: find a disired path in the system"<< endl;
	cout << ">";
	getline(cin, input);
	cout << endl;
	//Create an interactive menu for the user *******************
	do {
		string check;
		bool zoneCheck = false;
		if ((input.compare(exitCheck) != 0) && (input.compare(" ") != 0)) {
			string number;
			string sr;//represents a origin station for comparing
			string sr2;//represents a destination station for comparing
			int num;
			
			//Print out the routes
			if (input.compare("print routes") == 0) 
			{
				for(int i = 0; i < stationsVect.size(); i++)
				{
					Route *tempPtr = stationsVect[i];
					tempPtr->printRoutes();
					cout << endl;
				}
			}
			
			//Print out the stations
			else if(input.compare("print stations") == 0)
			{stationPtr->printStations();}
			else if(input.compare("print zones") == 0)
			{
				for(int i = 0; i < zones.size(); i++)
				{
					//temp pointer
					Zone *tempPtr = zones[i];
					tempPtr->printZones();
					cout << endl;
				}
			}//find the path of all routes entered
			else if(input.compare("find path") == 0)
			{
				Path *findPathPtr;
				string start;
				string end;

				cout << "Enter the abbreviated name of station to START at\n"
					<< "Example : CSR \nType \"back\" to go back to the main menu" << endl;
				cout << ">";
				getline(cin, input);
				cout << endl;
				start = input;
				if(input.compare("back") != 0 && input.compare("") != 0)
				{
					cout << "Enter the abbreviated name of station to END at\n"
						<< "Example: BOL \nType \"back\" to go back to the main menu" << endl;
					cout << ">";
					getline(cin, input);
					cout << endl;
					end = input;
				}

				do{
					if(input.compare("back") != 0 && input.compare("") != 0 && input.compare("no") != 0)
					{
						findPathPtr = new Path(start, end, zones, routeName);
						findPathPtr->findPath();//Call the fine path method
					}
					else{
						cout << "Must type a command!" << endl;
					}
					cout << "Would you like to fine another path? type \"yes\" of \"no\"" << endl;
					cout << ">";
					getline(cin, input);
					cout << endl;
					if(input.compare("no") != 0 && input.compare("") != 0)
					{
						cout << "Enter the abbreviated name of station to START at\n"
						<< "Example : CSR \nType \"back\" to go back to the main menu" << endl;
						cout << ">";
						getline(cin, input);
						cout << endl;
						start = input;
						if(input.compare("back") != 0 && input.compare("") != 0)
						{
							cout << "Enter the abbreviated name of station to END at\n"
								<< "Example: BOL \nType \"back\" to go back to the main menu" << endl;
							cout << ">";
							getline(cin, input);
							cout << endl;
							end = input;
						}
					}					
				}while(input.compare("back") != 0 && input.compare("no") != 0);

			}
			else if(input.compare("add zone") == 0)
			{
				cout << "Type the name of new zone or type \"back\", to go back to main menu" << endl;
				cout << ">";
				getline(cin, input);
				cout << endl;
				string zoneName = input;//temp varible for zone name
				vector<string> stationName;//temp vector for new stations
				
 				do{
					if((input.compare("back") != 0) && input.compare("") != 0)
					{
						cout << "Enter stations full name or type \"done\" to \n"
							<< "stop entering stations and start adding routes" << endl;
						cout << ">";
						getline(cin, input);
						cout << endl;
						//Stations name temp varible
						string stationNameTemp = input;
						string addbreTemp;
						string closedOrOpen;

						//Ask user if the station is closed
						if(input.compare("back") != 0 && input.compare("done") != 0 && input.compare("") != 0){
							cout << "Is the Station closed? : Type \"yes\" or \"no\"" << endl;
							cout << ">";
							getline(cin, input);
							cout << endl;
							bool checkOut;
							do{
								if(input.compare("yes") == 0 || input.compare("no") == 0)
								{
									closedOrOpen = input;
									checkOut = false;
								}
								else{
									cout << "Must type either \"yes\" or \"no\"" << endl;
									checkOut = true;
								}
								if(checkOut)
								{
									cout << "Is the Station closed? : Type \"yes\" or \"no\"" << endl;
									cout << ">";
									getline(cin, input);
									cout << endl;
								}
							}while(checkOut);
						}

						if(input.compare("back") != 0 && input.compare("done") != 0 && input.compare("") != 0){
								cout << "Now type the abbreviation of that station\n"
									<< "Example: <FTC> for Fortcollins : abbreviations must be three characters long!" << endl;
								cout << ">";
								getline(cin, input);
								cout << endl;
								//Stations abbreviation key name for easier usage for searching
								addbreTemp = input;
							}

						do{
							//Add name to station temp vector
							if((stationNameTemp.compare("back") != 0) && (stationNameTemp.compare("done") != 0) &&
								(addbreTemp.compare("back") != 0) && (addbreTemp.compare("done") != 0) &&
								(closedOrOpen.compare("back") != 0) && (addbreTemp.compare("done") != 0))
							{
								if((stationNameTemp.compare("") != 0) && (addbreTemp.compare("") != 0)
									&& (closedOrOpen.compare("") != 0)){
									stationName.push_back(stationNameTemp);									
									stationName.push_back(closedOrOpen);
									stationName.push_back(addbreTemp);
								}
								else{
									cout << "Must type name of station" << endl; 
								}
							}
							else
							{cout << "Must type a command!" << endl;}

							if(input.compare("back") != 0 && input.compare("done") != 0 && input.compare("") != 0){
								cout << "Enter stations full name or type \"done\" to" 
									<< "stop entering stations and add routes" << endl;
								cout << ">";
								getline(cin, input);
								cout << endl;
								stationNameTemp = input;
							}
							
								//Ask user if the station is closed
							if(input.compare("back") != 0 && input.compare("done") != 0 && input.compare("") != 0){
								cout << "Is the Station closed? : Type \"yes\" or \"no\"" << endl;
								cout << ">";
								getline(cin, input);
								cout << endl;
								bool checkOut;
								//check the input in the program
								do{
									if(input.compare("yes") == 0 || input.compare("no") == 0)
									{
										closedOrOpen = input;
										checkOut = false;
									}
									else{
										cout << "Must type either \"yes\" or \"no\"" << endl;
										checkOut = true;
									}
									if(checkOut)
									{
										cout << "Is the Station closed? : Type \"yes\" or \"no\"" << endl;
										cout << ">";
										getline(cin, input);
										cout << endl;
									}
								if(input.compare("back") != 0 && input.compare("done") != 0 && input.compare("") != 0){
									cout << "Now type the abbreviation of that station\n"
										<< "Example: <FTC> for FortCollins : abbreviations must be three characters long!" << endl;
									cout << ">";
									getline(cin, input);
									cout << endl;
									addbreTemp = input;
								}
								}while(checkOut);
							}
						}while((stationNameTemp.compare("back") != 0) && (stationNameTemp.compare("done") != 0) &&
								(addbreTemp.compare("back") != 0) && (addbreTemp.compare("done") != 0) &&
								(closedOrOpen.compare("back") != 0) && (addbreTemp.compare("done") != 0));

						if((stationNameTemp.compare("done") == 0) || (addbreTemp.compare("done") == 0) ||
							(addbreTemp.compare("done") == 0))
						{
							string Torig;
							string Tdest;
							string Tclos;
							string Tprim;

							cout << "Enter origin of route\n"
								<< "Example: FTC for FortCollins\n" 
								<< "Cannot exit out until entering the route is completed!"<< endl;
							cout << ">";
							getline(cin, input);
							Torig = input;
							cout << endl;

							cout << "Enter Destination of route\n"
								<< "Example: DEN for Denver\n"
								<< "Cannot exit out until entering the route is completed!"<< endl;
							cout << ">";
							getline(cin, input);
							Tdest = input;
							cout << endl;
							
							cout << "Enter if the route is closed or not\n"
								<< "Example: \"yes\" for closed and \"no\" for open\n"
								<< "Cannot exit out until entering the route is completed!"<< endl;
							cout << ">";
							getline(cin,input);
							
							cout << endl;
							bool checkOut;
							do{
								if(input.compare("yes") == 0 || input.compare("no") == 0)
								{
									Tclos = input;
									checkOut = false;
								}
								else{
									cout << "Must type either \"yes\" or \"no\"" << endl;
									checkOut = true;
								}
								if(checkOut)
								{
									cout << "Enter if the route is closed or not\n"
									<< "Example: \"yes\" for closed and \"no\" for open\n"
									<< "Cannot exit out until entering the route is completed!"<< endl;
									cout << ">";
									getline(cin, input);
									cout << endl;
								}
							}while(checkOut);
							
							cout << "Enter if the route is primary or not\n"
								<< "Example: \"yes\" for primary or \"no\" for not being primary\n"
								<< "Cannot exit out until entering the route is completed!" << endl;
							cout << ">";
							getline(cin, input);
							Tprim = input;
							cout << endl;
							do{
								if(input.compare("yes") == 0 || input.compare("no") == 0)
								{
									Tprim = input;
									checkOut = false;
								}
								else{
									cout << "Must type either \"yes\" or \"no\"" << endl;
									checkOut = true;
								}
								if(checkOut)
								{
									cout << "Enter if the route is primary or not\n"
									<< "Example: \"yes\" for primary or \"no\" for not being primary\n"
									<< "Cannot exit out until entering the route is completed!" << endl;
									cout << ">";
									getline(cin, input);
									cout << endl;
								}
							}while(checkOut);
							
							do{
								if(input.compare("no") != 0)
								{
									//Add all input from the user in the temp vector
									routeName.push_back(Torig);
									routeName.push_back(Tdest);
									routeName.push_back(Tclos);
									routeName.push_back(Tprim);
								}
								else
								{
									if(input.compare("no") != 0)
									{
										cout << "Must enter a command!" << endl;
									}
								}
								cout << "Would you like to keep adding routes?<yes or no>" << endl;
								cout << ">";
								getline(cin, input);
								cout << endl;								
								
								if(input.compare("no") != 0 && input.compare("") != 0)
								{
									cout << "Enter origin of route\n"
										<< "Example: FTC for FortCollins\n" 
										<< "Cannot exit out until entering the route is completed!"<< endl;
									cout << ">";
									getline(cin, input);
									Torig = input;
									cout << endl;
		
									cout << "Enter Destination of route\n"
										<< "Example: DEN for Denver\n"
										<< "Cannot exit out until entering the route is completed!"<< endl;
									cout << ">";
									getline(cin, input);
									Tdest = input;
									cout << endl;
									
									cout << "Enter if the route is closed or not\n"
										<< "Example: \"yes\" for closed and \"no\" for open\n"
										<< "Cannot exit out until entering the route is completed!"<< endl;
									cout << ">";
									getline(cin,input);
									Tclos = input;
									cout << endl;
									bool checkOut;
									do{
										if(input.compare("yes") == 0 || input.compare("no") == 0)
										{
											Tclos = input;
											checkOut = false;
										}
										else{
											cout << "Must type either \"yes\" or \"no\"" << endl;
											checkOut = true;
										}
										if(checkOut)
										{
											cout << "Enter if the route is closed or not\n"
											<< "Example: \"yes\" for closed and \"no\" for open\n"
											<< "Cannot exit out until entering the route is completed!"<< endl;
											cout << ">";
											getline(cin, input);
											cout << endl;
										}
									}while(checkOut);

									
									cout << "Enter if the route is primary or not\n"
										<< "Example: \"yes\" for primary or \"no\" for not being primary\n"
										<< "Cannot exit out until entering the route is completed!" << endl;
									cout << ">";
									getline(cin, input);									
									cout << endl;
									do{
										if(input.compare("yes") == 0 || input.compare("no") == 0)
										{
											Tprim = input;
											checkOut = false;
										}
										else{
											cout << "Must type either \"yes\" or \"no\"" << endl;
											checkOut = true;
										}
										if(checkOut)
										{
											cout << "Enter if the route is primary or not\n"
											<< "Example: \"yes\" for primary or \"no\" for not being primary\n"
											<< "Cannot exit out until entering the route is completed!" << endl;
											cout << ">";
											getline(cin, input);
											cout << endl;
										}
									}while(checkOut);
								}
								else
								{
									cout << "Must type a command" << endl;
								}								
							}while(input.compare("no") != 0);
						}
					}
					else
					{
						if(input.compare("back") != 0 && input.compare("no") != 0){
							cout << "Must type a command!" << endl;
						}
					}
					if(input.compare("back") != 0 && input.compare("no") != 0){
						cout << "Type the name of new zone or type back, to go back to man menu" << endl;
						cout << ">";
						getline(cin, input);
						cout << endl;
					}

				}while((input.compare("back") != 0) && (input.compare("exit") != 0) && (input.compare("no") != 0));
 				
 				//Add objects and use the pointers for class use
 				zonePtr = new Zone(zoneName, stationName);
 				zones.push_back(zonePtr);
				routePtr = new Route(routeName);
				stationsVect.push_back(routePtr);
			}
			else
				cout << "Must type a command!" << endl;

		} else {
			cout << "Incorrect use of commands!" << endl;
		}

			cout << "-------------------------------menu------------------------------------\n";
		cout << "List of commands and there usages are as follows:\n"
			 << ">>exit: to exit the program at any given time\n"
			 << ">>print <routes> or <stations> or <zones>: prints out routes or stations or zone\n"
			 << ">>add <zone>: to add different zones\n"
			 << ">>find path: find a disired path in the system"<< endl;
		cout << ">";
		getline(cin, input);
		cout << endl;
	} while (input.compare(exitCheck) != 0);
	delete routePtr;
	delete stationPtr;
	delete zonePtr;
}


int main() {
	
	bool fileCheck;
	//Error check the file
	fileCheck = runError();
	//parse the data from the file and process it
	if (fileCheck) {
		runParser();
		userInteract();
	} else
		cout << "Error within the file, check syntax of the file!" << endl;
	return 0;
}


