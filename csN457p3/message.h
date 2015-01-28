#ifndef MESSAGE_H
#define MESSAGE_H


class Message
{
      public:
            Message(vector<string>);
            void checkMessage(string);
            void checkChange();
            vector<int> binary(string);
            //setClosed(string);
             
      private:
              vector<string> theMessage;       
};

class ISP
{
	public:
		ISP();
		ISP(vector<int>, int, int);
		int getPrefix(int);
		vector<int> getAS();
		vector<int> getRange();		
		vector<int> getVectorIP();
		void uddateAS(int);
		void updateRange(int);
		void updatePrefixes(int);
		
	private:
		vector<int> prefixes;
		vector<int> as;
		vector<int> range;	
};

#endif
