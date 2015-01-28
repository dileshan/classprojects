
/*******************************************************************************************************************
 * Names: Matt DeSilvey. Matt Floyd, Tai Tran
 * Date: 3/14/08
 * Project 2
 *******************************************************************************************************************
 */

#include <iostream>
#include <iomanip>
#include <fstream>
#include <vector>
#include <string>
#include <unistd.h>
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <sys/time.h>
#include <time.h>
#include <fcntl.h>
using namespace std;

#define MAXDATASIZE 10000 //bytes

//create packet structure
struct header{
	short ID;
	short flags;//Query or response, flags
	short QDCOUNT;
	short ANCOUNT;
	short NSCOUNT;
	short ARCOUNT;
}__attribute__((packed));

struct question{
    short QCLASS;
    short QTYPE;       
       
}__attribute__((packed));

struct _server{
	char name[64];
	char ip[64];
};
typedef struct _server server;

int print_h(header* h) {
  cout<<"ID: "<<ntohs(h->ID)<<endl;
  cout << "Flags: " << ntohs(h->flags) << endl;
  cout<<"QDCOUNT: "<<ntohs(h->QDCOUNT)<<endl;
  cout<<"ANCOUNT: "<<ntohs(h->ANCOUNT)<<endl;
  cout<<"NSCOUNT: "<<ntohs(h->NSCOUNT)<<endl;
  cout<<"ARCOUNT: "<<ntohs(h->ARCOUNT)<<endl;
}

char* read_name(char** name, void* data_ptr, void* start_ptr) {
	short offset = (unsigned char) *((char*)(data_ptr)+1);
	
	char* data = ((char*)start_ptr) + offset;
	
	while((unsigned char)*data!=0) { 
		int segmentsize = (unsigned char)*data;
		data++;
		for(int i = 0; i < segmentsize; i++) {
			**name = *data;
			(*name)++;
			data++;
		}
		**name = '.';
		(*name)++;
	}
	return *name;
}


//Return values:
//-1 = Bad QR or RCODE
//0 = Not AA
//1 = AA
int check_flags(short flags) {
	short qr_mask = 0x7FFF;
	short aa_mask = 0xFBFF;
	short mask = 0xFFFF;
			
	//QR Check
	if((flags | qr_mask) == mask) {
		printf("QR_mask: %x\n", qr_mask);
		printf("aa_mask: %x\n", aa_mask);
		printf("flags: %x\n", flags);
		return -1;
	}
	
	//RCODE CHECK
	short format_err = 0xFFFE;
	short server_err = 0xFFFD;
	short name_err = 0xFFFC;
	short query_err = 0xFFFB;
	short refused_err = 0xFFFA;
	
	if((flags | format_err) == mask) {
		cout << "Query Error: Format not valid" << endl;
		return -1;
	}
	if((flags | server_err) == mask) {
		cout << "Server Failure: The name server was unable to process this query due to a problem with the name server" << endl;
		return -1;
	}
	if((flags | name_err) == mask) {
		cout << "Name Error: Domain name does not exist." << endl;
		return -1;
	}
	if((flags | query_err) == mask) {
		cout << "Not Implemented: The name server does not support the requested kind of query" << endl;
		return -1;
	}
	if((flags | refused_err) == mask) {
		cout << "Server Refused Request: The name server refuses to	perform the specified operation" << endl;
		return -1;
	}
	
	//AA Check
	if((flags | aa_mask) != mask) {
		return 0;
	}
	else
		return 1;
		
	return -2;
}

int parse_data(char* address, char* message_body_ptr, int nscount, int arcount) {
	char* parse_ptr = message_body_ptr;
	// 	printf("%x dfgdg ", (unsigned char) *message_body_ptr);

	//SKIP TO FIRST RECORD
		int a = 0;
		while((unsigned char)(*parse_ptr) != 0xc0) {
			parse_ptr++;
			a++;		
		}

		if (arcount > 0) {
			//return ip
			for(int i = 0; i < nscount; i++) {

				//parse_ptr is at first name pointer(start of record)
				parse_ptr += 10; //Advance pointer to rdlength
				unsigned short rdlength = ntohs(*((unsigned short*) parse_ptr));
				parse_ptr += rdlength + 2;//Advance pointer to next record

			}

			//Get the first ip from AR list
			
			for(int i = 0; i < arcount; i++) {
	
				//parse_ptr is at first ar record
				parse_ptr += 2;
				unsigned short rtype =  ntohs(*((unsigned short*) parse_ptr)); //type of record
				parse_ptr += 2;
				unsigned short rclass =  ntohs(*((unsigned short*) parse_ptr));//class of record
				parse_ptr += 6;
				unsigned short rdlength =  ntohs(*((unsigned short*) parse_ptr));// check rd length
				parse_ptr += 2;

				if(rtype == 1 && rclass == 1 && rdlength == 4) {
					//record is internet address
					char* address_ptr = address;
					for(int i2 = 0; i2 < 4; i2++) {
	
						printf("%d ", (unsigned char) *parse_ptr);
						*address_ptr = *parse_ptr;
						printf("%d ", (unsigned char) *address_ptr);
						address_ptr++;
						parse_ptr++;
					}
					break;
				}//else the record does not contain an address
			}
			
		}
		else{
			//return string with name
			for(int i = 0; i < nscount; i++) {
				parse_ptr += 2;
				unsigned short rtype =  ntohs(*((unsigned short*) parse_ptr)); //type of record
				parse_ptr += 2;
				unsigned short rclass =  ntohs(*((unsigned short*) parse_ptr));//class of record
				parse_ptr += 6;
				unsigned short rdlength =  ntohs(*((unsigned short*) parse_ptr));// check rd length
				parse_ptr += 2;
					
				if(rtype == 2 && rclass ==1) {
					//record is a name server
					for(int i2 = 0; i2 < rdlength; i2++) {
						if( *((unsigned short*)parse_ptr) != 0xc0) {
							*address = *parse_ptr;
						}
						else {
							char temparray[64];
							char* tempname = &temparray[0];
						//	read_name(tempname, parse_ptr, message_body_ptr);
							
						}
						
						
					}
					
				}
			}
		}
}

//

int main(int argc, char **argv)
{
	//53 port number
	
	//Check arguments
	if(argv[1] == NULL)
	{
		cout << "Error with arugment nothing typed!" << endl;
		exit(1);
	}
	
	char buffer[MAXDATASIZE];//sending data buffer
	//cout << sizeof (struct header) << endl;
	
	/************************************************************************************************/
	
	/************************************************************************************************/
	
	struct header *sending_data = (struct header *) buffer;
		
	sending_data->ID = htons(3633);
	sending_data->flags = htons(0);
	sending_data->QDCOUNT = htons(1);
	sending_data->ANCOUNT = htons(0);
	sending_data->NSCOUNT = htons(0);
	sending_data->ARCOUNT = htons(0);
 
	/**********************************************************************************************/
	char *input = NULL;
	char *name;
	char *delims = ".";
	char sizeTemp = 0;
	string strTemp;
	char demName[500];
	input = argv[1];
	int counter = 0;
	
	/*for(int u = 0; u < sizeof (struct header)-1; u++)
	{
            printf("%x", buffer[u]);        
    } */ 
	//cout << endl;
	name = strtok(input, ".");
	//char *arg = (char*)malloc(strlen(name+1));
	while(name != NULL)
	{
		//count the length
		strTemp = name;
		sizeTemp = strTemp.length();	
		
		//buffer[counter] = htonl(sizeTemp);
		demName[counter] = sizeTemp;
		//printf("%s", domainName[0]);
		counter++;
		for(int i = 0 ; i < sizeTemp; i++)
		{
			demName[counter] = name[i];
			counter++;
		}
		
		name = strtok(NULL, ".");
	}
	demName[counter] = 0;
	counter++;

	char * buf2 = buffer + sizeof (struct header);
	memcpy (buf2, demName, counter);//copy the string typed into the buffer
/*	
  dns_query* query = (dns_query*) malloc(sizeof(dns_query) + strlen(demName));
  make_dns_query(query, demName, 3333);
  free (query);
  */
	char * buf3 = buf2 + counter;
    struct question *question_data = (struct question *) buf3;
    question_data->QCLASS = htons(1);
    question_data->QTYPE = htons(1);
	//************************************************************************
	//cout << sizeof (struct header) << endl;//printing out string
	
    
    
	for(int i = 0; i < (sizeof (struct header)) + counter + (sizeof (struct question)); i++)
	{
		printf("%x ", buffer[i]);//print out buffer in hex
	}
	cout << endl;
	
	//****************************************************************************
	//create a socket using SOCK_DGRAM
	int sockfd, numbytes, len, bytes_sent;
	
	const char *msg;
	struct hostent *he;
	struct hostent *hos; //udp hosts
	struct sockaddr_in their_addr; // connector's address information 
	struct sockaddr_in my_addrMine; // My address information
	struct in_addr my_addr;
	struct sockaddr addr_info;//Socket info
	socklen_t addr_len;
	char recvBuffer[MAXDATASIZE];
	
	//Name of server
	string Name_of_server = input;
	
	sockfd = socket(AF_INET, SOCK_DGRAM, 0);//Create a file descriptor or socket
	fcntl(sockfd, F_SETFL, O_NONBLOCK);//Tell kernel to set to non-blocking

	if(sockfd == -1)
	{
		perror("Error with creating a udp socket");
		exit(1);
	}
	else
	{
		cout << "Created a socket using UDP" << endl;
	}		
	
	if ((he=gethostbyname(Name_of_server.c_str())) == NULL) 
	{  // get the host info 
		perror("retype host name");
	 	exit(1);
	}
	
	
	//while(AA != 1)
	
		
	//Send some data using udp...
					
	int checkSend;
	int tempcheck = 0;
	bool flag = false;
	time_t start,end;
	int checkRecv = 0;
	double dif;
	bool ackFlag = false;
	bool sentFlag = false;
	int seqNum = 0;
	int questionSize = sizeof(struct question);
	int totalSizeBuffer = sizeof (struct header) + counter + questionSize;
	
	server _root;
	server* root = &_root;
	strcpy(root->ip, "74.52.112.162");
	char* target_ip = root->ip;
	
	time(&start);
	while(!flag)
	{
		their_addr.sin_family = AF_INET;    // host byte order 
		their_addr.sin_port = htons(53); // short, network byte order
		//their_addr.sin_addr = *((struct in_addr *)he->h_addr); // error seg faulting
		their_addr.sin_addr.s_addr = inet_addr(target_ip);
		memset(their_addr.sin_zero, '\0', sizeof their_addr.sin_zero);

		
		if((checkSend = sendto(sockfd, buffer, totalSizeBuffer, 0, 
				(struct sockaddr *)&their_addr, sizeof their_addr)) == -1)
		{
			//perror("Didn't send correctly\n");
		}
		sleep(3);
		//recv something
		//
		addr_len = sizeof their_addr;
		if((checkRecv = recvfrom(sockfd, recvBuffer, MAXDATASIZE-1, 0, (struct sockaddr *)&their_addr, &addr_len)) == -1)
		{
			//perror("Didn't recv correctly");				
		}
		else
		{
			cout << "Correctly recv" << endl;				
			flag = true;
			ackFlag = true;
		}

		/*Resend if there is no message returned*/
		//6 103 111 111 103 108 101 3 99 111 109 0
		
		/*//wait for 3 seconds and quit sending after that
		time(&end);
		dif = difftime (end,start);
		if(dif == 3 && !flag)
		{
			flag = true;
			ackFlag = true;
		}
		*/
	}
	
	//print out the data from recvBuffer
	char* data_ptr = recvBuffer;
	for(int i = 0; i < checkRecv; i++) {
		printf("%d ",(unsigned char) *data_ptr);
		(data_ptr)++;
	}
	cout<<endl;
	
	header _r_header;
	header* r_header = &_r_header;
	memcpy(r_header, recvBuffer, sizeof(header));
	print_h(r_header);
  
    /*char arrStrbuf[numbytes];
    char *ptrCopy = buf + sizeof (struct msgRecv);
    memcpy(arrStrbuf, ptrCopy, numbytes - sizeof (struct msgRecv));
    string strprint = arrStrbuf;*/
    int dataSize = 1000;
    char data_str[dataSize];
    char *ptrCopy = recvBuffer;
    memcpy(data_str, ptrCopy, sizeof(recvBuffer));
    string str_data_copy = data_str;
  
    /*--Resolver--*/    
    //New buffer for sending
    char buffer2[MAXDATASIZE];
    char new_recvBuffer[MAXDATASIZE];
    vector<string> ns_visited;
    int AA = check_flags(r_header->flags);
    int _ARCOUNT = ntohs(r_header->ARCOUNT);
    bool if_compare = false;
    bool first_pass = true;
    char* _parse_ptr;
    int new_sockfd;
    
    
    close(sockfd);
    
    while(AA != 1)
     {
    	
    	//Check the vector for duplicate IP'S
        for(int i = 0; i < ns_visited.size(); i++)
        {
        	if(!(ns_visited.empty()))
        	{
        		string compare = ns_visited.at(i);
        		
	        	//compare the new target_ip with the existing ip's
	        	if(compare.compare(target_ip) == 0)
	        	{
	        		if_compare = true;
	        		cout << "Site doesn't exist" << endl;
	        		exit(-1);
	        	}
        	}
        }
        
        //ARcount check
        if(_ARCOUNT == 0)
        {
        	//resend using header packet with new nscount
        	//
        	char *_parse_data2;
        	
        	if(first_pass)
        	{
        		_parse_data2 = recvBuffer;
        		first_pass = false;
        	}
        	else{
        		_parse_data2 = new_recvBuffer;
        	}
        	
        	char demName2[400];
        	
        	char __address[64];
			char *address2 = &__address[0];			
        	parse_data(address2, _parse_data2, r_header->NSCOUNT,_ARCOUNT);
        	
        	struct header *new_sending_data = (struct header *) buffer2;
        	
        	strcpy(demName2, address2);
        	
        	sending_data->ID = htons(1561);
    		sending_data->flags = htons(0);
    		sending_data->QDCOUNT = htons(1);
    		sending_data->ANCOUNT = htons(0);
    		sending_data->NSCOUNT = htons(0);
    		sending_data->ARCOUNT = htons(0);
    		
    		char * new_buf2 = buffer2 + sizeof (struct header);
    		memcpy (new_buf2, demName2, sizeof(demName2));//copy the string typed into the buffer
    		
    		char * buf3_2 = new_buf2 + sizeof(demName2);
		    struct question *question_data2 = (struct question *) buf3_2;
		    question_data2->QCLASS = htons(1);
		    question_data2->QTYPE = htons(1);
		    
			//***************************************************************************************************************
		    
		    their_addr.sin_family = AF_INET;    // host byte order 
    		their_addr.sin_port = htons(53); // short, network byte order
    		//their_addr.sin_addr = *((struct in_addr *)he->h_addr); // error seg faulting
    		their_addr.sin_addr.s_addr = inet_addr(target_ip);
    		memset(their_addr.sin_zero, '\0', sizeof their_addr.sin_zero);
    		
    		//cout << totalSize << endl;
    		if((checkSend = sendto(sockfd, buffer2, totalSizeBuffer, 0, 
    				(struct sockaddr *)&their_addr, sizeof their_addr)) == -1)
    		{
    			//perror("Didn't send correctly\n");
    		}
    		
    		//recv something
    		//
    		addr_len = sizeof their_addr;
    		if((checkRecv = recvfrom(sockfd, new_recvBuffer, MAXDATASIZE-1, 0, (struct sockaddr *)&their_addr, &addr_len)) == -1)
    		{
    			//perror("Didn't recv correctly");				
    		}
    		else
    		{
    			cout << "Correctly recv" << endl;				
    		}
    		
    		cout << "resent with new header" << endl;
    		char* data_ptr = new_recvBuffer;
			for(int i = 0; i < checkRecv; i++) {
				printf("%d ",(unsigned char) *data_ptr);
				(data_ptr)++;
			}
    		
    		header _r_header2;
			header* r_header2 = &_r_header2;
			memcpy(r_header2, new_recvBuffer, sizeof(header));
			
			
			AA = check_flags(r_header2->flags);
			_ARCOUNT = ntohs(r_header2->ARCOUNT);
        }

    	if((!if_compare) && ((AA != -1) || (AA != 1)) && (_ARCOUNT != 0))
        {
    		new_sockfd = socket(AF_INET, SOCK_DGRAM, 0);//Create a file descriptor or socket
    		fcntl(new_sockfd, F_SETFL, O_NONBLOCK);//Tell kernel to set to non-blocking
    		
    		if(sockfd == -1)
			{
				perror("Error with creating a udp socket");
				exit(1);
			}
			else
			{
				cout << "Created a socket using UDP" << endl;
			}		
      		//update vector visted
    		ns_visited.push_back(target_ip);
    		
    		cout << "Target_ip: " << target_ip << endl;   		
    		
    		their_addr.sin_family = AF_INET;    // host byte order 
			their_addr.sin_port = htons(53); // short, network byte order
			//their_addr.sin_addr = *((struct in_addr *)he->h_addr);
			their_addr.sin_addr.s_addr = inet_addr(target_ip);
			memset(their_addr.sin_zero, '\0', sizeof their_addr.sin_zero);
			
			//Send data ------------------------------------------------------------------
			//cout << totalSize << endl;
			if((checkSend = sendto(new_sockfd, buffer, totalSizeBuffer, 0, 
					(struct sockaddr *)&their_addr, sizeof their_addr)) == -1)
			{
				//perror("Didn't send correctly\n");
			}
			else
			{
				cout << "Correctly sent" << endl;
			}
			
			//recv something
			addr_len = sizeof their_addr;
			if((checkRecv = recvfrom(new_sockfd, recvBuffer, MAXDATASIZE-1, 0, (struct sockaddr *)&their_addr, &addr_len)) == -1)
			{
				perror("Didn't recv correctly");			
			}
			else
			{
				cout << "Correctly recv" << endl;
			}
			
			print_h(r_header);
			//print out the data from recvBuffer
			char* data_ptr = recvBuffer;
			for(int i = 0; i < checkRecv; i++) {
				printf("%d ",(unsigned char) *data_ptr);
				(data_ptr)++;
			}
			
			//recheck flags
			AA = check_flags(r_header->flags);
			
			_parse_ptr = recvBuffer;
			//Get new ip to send
			//parse function to get new string of ip and put it in the target_ip
			char _address[64];
			char *address = &_address[0];
			int _NSCOUNT = r_header->NSCOUNT;
			cout<<endl;
			cout<<_NSCOUNT<<" "<<_ARCOUNT<<endl;
			parse_data(address, _parse_ptr, ntohs(_NSCOUNT), _ARCOUNT);
			
			char buffs1[100];				
			char buffs2[100];
			char buffs3[100];
	 		char buffs4[100];
	 		char buffs5[100];
	 		sprintf(buffs2, "%d",(unsigned char)*address);
	 		address++;
	 		sprintf(buffs3, "%d",(unsigned char)*address);
	 		address++;
	 		sprintf(buffs4, "%d",(unsigned char)*address);
	 		address++;
	 		sprintf(buffs5, "%d",(unsigned char)*address);
	
	 		sprintf(buffs1, "%s.%s.%s.%s",buffs2, buffs3, buffs4, buffs5);
	 		cout << endl;
	 		printf("%s\n", buffs1);
			
			cout<<endl;
			target_ip = buffs1;
			cout << "AA : " << AA << endl;
			
			cout << "checksize recv: " << checkRecv << endl;
			
			cout << "vector contents: " << endl;
			cout << "Size: " << ns_visited.size() << endl;
			for(int i = 0; i < ns_visited.size(); i++)
			{
				cout << ns_visited[i] << ",";
			}
			cout << endl;
			
			char *temp_ptr;
			temp_ptr = &buffer[0];
			
			
			
			close(new_sockfd);
        }
    	
		if(AA == -1)
		{
			exit(-1);
		}
     }
     
//********************************************************************************************************************
/*	if(check_flags(r_header->flags) < 0)
		exit(-1);*/
	
	char* parse_ptr = recvBuffer;
	
    while((unsigned char)(*parse_ptr) != 0xc0) {
		parse_ptr++;		
	}
	
	printf("%x ", (unsigned char) *parse_ptr);
	printf("%x ", (unsigned char) *(parse_ptr + 1));
	cout << endl;
	
// 	for(int i = 0; i < r_header->ANCOUNT; i++) {
// 		
// 	}
	
//	for(int i = 0; i < r_header->NSCOUNT; i++) {
		parse_ptr += 10;	
		short rdlength = ntohs(*(short*) (parse_ptr));
		parse_ptr += 2;

		char _cname[64];
		char* cname = &_cname[0];
				
		for(int i = 0; i<rdlength; i++ ){
			cout<<cname<<endl;
			if(((unsigned char)*parse_ptr) == 0xc0) {
				char temp_name[64];
				char* temp_nameptr = &temp_name[0];
				read_name(&temp_nameptr, parse_ptr, recvBuffer);
				strncat(cname, temp_name, strlen(temp_name)-1);
				parse_ptr+=2;
				i =rdlength;
				cout<<temp_name<<endl;
			}
			else {	
				short stLength = (*(char*) (parse_ptr));
				cout << stLength<<endl;
				parse_ptr++;
				for(int i2 = 0; i2 < stLength; i2++)
				{
					*cname = (unsigned char)*parse_ptr;
					cout<<cname<<endl;
					cname++;
					parse_ptr++;
					i++;
				}
				*cname = '.';
				cname++;
				i++;
			}
		}
		cout<<_cname<<endl;
		/*
		while(((unsigned char)*parse_ptr) != 0xc0){	
			short stLength = (*(char*) (parse_ptr));
			parse_ptr++;
				for(int i2 = 0; i2 < stLength; i2++) {
					//strcat(cname, parse_ptr);
					printf("%d",(unsigned char)*parse_ptr);
					cout<<endl;
					cname[i2] = (unsigned char)*parse_ptr;
					cout<<cname<<endl;
					parse_ptr++;	
		}
		if(((unsigned char)*parse_ptr) != 0xc0)
			stLength = (*(char*) (parse_ptr));
}
		else {
				char temp_name[64];
				read_name(temp_name, parse_ptr);
				strcat(cname, temp_name);
			}
		}*/
		//cout<<cname<<endl;
		
//	}
	
	
//   print_header(recv_header);
//   

  
// 	read_dns_body(&dataptr, r_header);

  
//  r_header->Flags = ntohs(r_header->Flags);
 // print_header((dns_header*)r_header);  
  
	/******
	 * Print out what we got from the DNS for now
	 */
// 	if(ackFlag)
// 	{
// 		for(int i = 0; i < sizeof(recvBuffer); i++)
// 		{
// 			printf("%x ", recvBuffer[i]);//print out buffer in hex
// 		}
// 		cout << endl;
// 	}
	
	//Check for the ack message from server
	/*if(!ackFlag)
	{
		struct ACK * msgAck = (struct ACK *) recvBuffer;
		char tmp = msgAck->vers;

		cout << "ACK message recieved!" << endl;
		printf("Version: %x \n", tmp);
		cout << "Sequence: " << ntohl(msgAck->seq) << endl; 	
		cout << endl;	
	}
	else
	{
		cout << "No ACK message!!" << endl;
		close(sockfd);
		exit(1);
	}*/
	
	
	return 0;
}


