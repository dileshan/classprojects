/*************
 * Nifty 
 * November 30, 2007
 * Matt Floyd
 * David Becker
 * Matt DeSilvey
 *************/

#include <iostream>
#include <string>
#include "nifty.h"

using namespace std;


int main()
{	
	nifty x;
	nifty a = "abc";
	cout << a << x << endl;
	int i = x;
	x = a;
}
