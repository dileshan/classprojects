/**********************************************************
 * Name: Matt DeSilvey
 * Date: 2/21/08
 * Project 1
 * class: cs451
 **********************************************************
 */

#include <limits.h>
#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <fstream>
#include <iostream>
#include <string.h>
#include <string>
using namespace std;

/*Flags for checking file 
 * 0 for ok, -1 for error 
 */
int Flag_F;//file check
int Flag_U;//user perm
int Flag_G;//group perm
int Flag_O;//other perm

int checkexecutable(char *path)
{
	struct stat statbuf;
	
	int USER_id = geteuid();//users id permissions
	int GRUP_id = getegid();//group id permissions	
	
	//check if file is a regular file
	if(stat(path, &statbuf) == -1)
	{
		Flag_F = -1;
		return -1;
	}	
	//if file is ok check user permissions
	if((statbuf.st_uid == USER_id) && ((statbuf.st_mode & S_IXUSR) == S_IXUSR))
	{
		Flag_U = 0;
		return 0;
	}
	else
		Flag_U = -1;
	//check group permissions
	if((statbuf.st_gid == GRUP_id) && ((statbuf.st_mode & S_IXGRP) == S_IXGRP))
	{
		Flag_G = 0;
		return 0;
	}
	else
		Flag_G = -1;
	//check other permissions
	if((statbuf.st_mode & S_IXOTH) == S_IXOTH)
	{
		Flag_O = 2;
		return 2;
	}
	else
		Flag_O = -1;

}

/*******************************************************************
 * Main method
 * Calls checkexecutble method to check permissions and to check if
 * its a file or not.
 * method returns an int value which is checked and printout statements
 * accordingly to thoughs values.
 */
int main(int argc, char **argv)
{	
	//check and add arugment value into a local varible
	const char *execName = NULL;
	int index = 1;
	char *path, *ppath, name[256];
	char *delims = ":";
	
	//Error check the argument
	if(argc != 2)
	{
		cout << "Error with arugment" << endl;
		exit(1);
	}	
	execName = argv[index];	
	
	// Find path
	/*Find the name and the path to that name*/	
	if((path = getenv("PATH")) == NULL)
	{
		cout << "Error: getenv() failed" << endl;
		exit(1);
	}
	
	bool finished = false;
	int checkExe;
	ppath = strtok(path, delims);/*Get a path*/
	while((finished == false) && (ppath != NULL))
	{
		strcpy(name, ppath);
		strcat(name, "/"); /*Append dir divider*/
		strcat(name, execName);/*Append fn*/
		//call checkExe
		if((checkExe = checkexecutable(name)) != -1)
		{
			finished = true;
		}
		else
		{
			ppath = strtok(NULL, delims);/*Try next path*/
		}
	}	
	//call checkexecutable and check flags
	if((checkExe == -1) && Flag_F == -1)/*Initial check for the file*/
	{		
		cout << "File does not exist" << endl;
	}
	else{
		//Print out path
		cout << name << endl;
		if((Flag_U == -1))/*If no user permissions*/
		{
			cout << execName << " :" << "Not owner" << endl;
		}
		if((Flag_G == -1))/*If no group permissions*/
		{
			cout << execName << " :" << "Not within the group" << endl;
		}
		
		if((Flag_O == -1))/*If is not excuble by other*/
		{
			cout << execName << " :" << "No other permissions" << endl;
		}
		else
			cout << "Allowed to run executable" << endl;	
	}	
	return 0;
}




