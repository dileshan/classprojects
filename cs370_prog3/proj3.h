
#include <string>
#include <vector>
#include <iostream>
using namespace std;


class fcfs
{
public:
	fcfs();
	fcfs(vector<int> &, vector<int> &, int);
	
	void setRequests(vector<int> &);
	void setNumCyli(vector<int> &);
	void findCylinders();
	vector<int> getNumCyli();
	void setMax_Blocks(int);
	
	
private:
	vector<int> requests;
	vector<int> numCyli;
	vector<int> cache;
	int readAhead;
	long int max_bigNum;
};
