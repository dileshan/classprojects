/*********************************************
* Name: Matt Desilvey
* Date: 11/2/07
* program#4
*/
#include <iostream>
#include <iomanip>
#include <string>
#include <cstring>
#include <vector>
using namespace std;

#include "train.h"

ParseCheck::ParseCheck()
{

}
ParseCheck::ParseCheck(string &str)
{
	setLine(str);
}

//getter and setting for string line
void ParseCheck::setLine(string &str)
{
	line = str;
}

string ParseCheck::getLine()
{
	return line;
}

bool ParseCheck::errorCheck(string &str_c)
{
	new_str = str_c;
	str = new_str.substr(0,1);
	str2 = new_str.substr(new_str.size(),1);

	if(str == "f" || str == "F")
	{
		return true;
	}
	else
	{
		if(str != "[" && str2 != "]")
		{
			return false;
		}
		else
			return true;		
	}
}
	
void ParseCheck::printErrors()
{
	cout << "Errors in file, check syntax or see the readme for syntax." << endl << endl;;
}














