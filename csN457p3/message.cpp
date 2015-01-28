#include <sys/socket.h> 
#include <arpa/inet.h>  
#include <stdio.h>
#include <stdlib.h>   
#include <string.h>   
#include <unistd.h>     
#include <errno.h>
#include <iostream>
#include <vector>
#include <string>
using namespace std;

#include "message.h"

Message::Message(vector<string> in_vector)
{
	theMessage = in_vector;
	
}


void Message::checkMessage(string b)
{
	for(int i = 0; i < theMessage.size(); i++)
	{
		string temp = theMessage[i];
		 cout << "Node: " << temp << endl;
	}

	
	int p1, p2, p3, p4;
	int range;
	char *pref = (char *)b.c_str();
	//strtok(
	char *pch;
	
	vector<string> savedData;
    	pch = strtok(pref,"./");
    	while (pch != NULL)
    	{
    		string temp = pch;
          	savedData.push_back(temp);
    		cout << pch << endl;
    		pch = strtok (NULL, "./");
    	}
    	
    	string binaryNum = "";
    	vector<int> ipAdd;
    	for(int c = 0; c < savedData.size()-1; c++)
    	{
    		vector<int> temp = binary(savedData[c]); 
    		for(int i = temp.size()-1; i >=0; i--)
    		{
    			ipAdd.push_back(temp[i]);    		
    		}
    	}
    	
    	for(int e = 0; e < ipAdd.size(); e++)
    	{
    	
    		cout << ipAdd[e];
    	}
    	cout << endl;
    	cout << endl;
    	cout << "KILL" << endl;
}

void Message::checkChange()
{

}

vector<int> Message::binary(string v)
{
	string b = "";
	vector<int> bin;
	int num = 0; 
	int remainder = 0;
	int a = atoi(v.c_str()); 
	int pref = a;
	while( bin.size() < 8)
	{	
		//cout << "Beer " << bin.size() << endl;
		num = a/2;
		remainder = a%2;
		
		bin.push_back(remainder);
		a = num;
	}	
	
	return bin;
}

/*
void binary(int number) {
	int remainder;
        
	if(number <= 1) {
		cout << number;
		return;
	}

	remainder = number%2;
	binary(number >> 1);    
	cout << remainder;
}

int n;
    while (cin >> n) {
        cout << "decimal: " << n << endl;

        // print binary with leading zeros
        cout << "binary : ";
        for (int i=31; i>=0; i--) {
            int bit = ((n >> i) & 1);
            cout << bit;
        }
        cout << endl;
    }//end loop
    return 0;
    */
