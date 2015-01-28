
#include <iostream>
#include <string>
using namespace std;

#include "nifty.h"

int main()
{
	nifty x;
	nifty a = "abc";
	cout << a << x << endl;
	int i = x;
	x = a;
	return 0;
}
