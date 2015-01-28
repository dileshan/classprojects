

#include <iostream>
#include <iomanip>
#include <fstream>
#include <pthread.h>
#include <vector>
#include <string>
using namespace std;

#include "proj2.h"

Dvd::Dvd()
{
}

Dvd::Dvd(string &_title, int _cop)
{
	title = _title;
	setCop(_cop);
}
	
void Dvd::setCop(int _cop)
{
	copies = _cop;
}

int Dvd::getCop()
{
	return copies;
}
	
string Dvd::getTitle()
{
	return title;
}