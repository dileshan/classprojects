

#include <iostream>
#include <iomanip>
#include <fstream>
#include <pthread.h>
#include <vector>
#include <stdlib.h>
#include <string>
using namespace std;

#include "proj2.h"



Client::Client()
{
	dvdList = this.dvdList;

}

void Client::startClient(Object cl)
{
	int count = 0;
	
	do
	{
		int a = rand(dvd.size()-1);
		string dvdTitle = dvd[a];
		
		i = rand();
		if(i < RAND_MAX*prob)
		{
			for(int h = 0; 0 < cl.dvdList.size(); h++)
			{
				if(dvdTitle == cl.dvdList[h])
				{
					int dv_ret = dvd_return(dvdTitle);
					if(dv_ret == 1)
					{
						cl.dvdList.erase(h,1);
					}
				}
			}
			
		}
		else
		{
			int dv_check = dvd_checkout(dvdTitle);
			if(dv_check == 1)
			{
				cl.dvdList.push_back[dvdTitle];
			}
		}
		
	
	count++;
	}while(count < trans)
}






