
/**********************************************************
 * Names: Matt DeSilvey, Matt Floyd
 * Date: 2/1/08
 * Client.cpp
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


int x;//integer 32bit unsigned
int y;//integer 32bit unsigned
string stringMess;//string message
string protocol;//tcp or udp
string Name_of_server;//eg. torreys.cs.colostate.edu
int portNumber;//integer number specifing the port number on that socket

#define MAXDATASIZE 1000 // max number of bytes we can get at once 


struct data_info{
	char vers;
	unsigned int seq;
	unsigned int xInt;
	unsigned int yInt;
	unsigned int len;
} __attribute__((packed));

struct ACK{
	char vers;
	unsigned int seq;
}__attribute__((packed));

//Two arugments are to be checked
int main(int argc, char *argv[])
{
	int xflag = 0;
	int yflag = 0;
	int mflag = 0;
	int tflag = 0;
	int sflag = 0;
	char *message = NULL;
	char *tServer = NULL;
	char *sNameServer = NULL;
	int portFlag = 0;
	int c;
	int index;
	
	opterr = 0;
	
	int xtemp;
	int ytemp;
	string stringMesstemp;
	string protocoltemp;
	string Name_of_servertemp;
	int portNumbertemp;

	char *temp;	
	char *temp1;
	char *temp2;
	char *temp3;
	char *temp4;
	char *temp5;
	char *temp6;
	
	
	while((c = getopt (argc, argv, "xymtsp:")) != -1)
		switch(c)
		{
		case 'x':
			xflag = 1;
			temp = argv[optind];
			xtemp = atoi(temp);
			break;
		case 'y':
			yflag = 1;
			temp2 = argv[optind];
			ytemp = atoi(temp2);
			break;
		case 'm':
			mflag = 1;
			message = optarg;
			temp3 = argv[optind];
			stringMesstemp = temp3;
			break;
		case 't':
			tflag = 1;
			tServer = optarg;
			temp4 = argv[optind];
			protocoltemp = temp4;
			break;
		case 's':
			sflag = 1;
			sNameServer = optarg;
			temp5 = argv[optind];
			Name_of_servertemp = temp5;
			break;
		case 'p':
			portFlag = 1;
			temp6 = optarg;
			portNumbertemp = atoi(temp6);
			break;
		case '?':
			if (optopt == 'm')
				fprintf (stderr, "Option -%m requires an argument.\n", optopt);
			else if (isprint (optopt))
				fprintf (stderr, "Unknown option `-%m'.\n", optopt);
			else
				fprintf (stderr,"Unknown option character `\\x%x'.\n", optopt);
			
			if(optopt == 's')
				fprintf (stderr, "Option -%s requires an argument.\n", optopt);
			else if (isprint (optopt))
				fprintf (stderr, "Unknown option `-%s'.\n", optopt);
			else
				fprintf (stderr,"Unknown option character `\\x%x'.\n", optopt);
			
			if(optopt == 't')
				fprintf (stderr, "Option -%t requires an argument.\n", optopt);
			else if (isprint (optopt))
				fprintf (stderr, "Unknown option `-%t'.\n", optopt);
			else
				fprintf (stderr,"Unknown option character `\\x%x'.\n", optopt);
			
			return 1;
			
		default:
			abort();
		}
	
	/*printf ("xflag = %d\n yflag = %d\n message = %s\n tValue = %s\n sValue = %s\n pFlag = %d\n",
			xflag, yflag, message, tServer, sNameServer, portFlag);*/
	
	/*for (index = optind; index < argc; index++)
		printf ("Option argument %s\n", argv[index]);*/
	
	/*Error checking*/
	cout << "\n";
	if(xflag == 1){
		//cout << "-x = " << xtemp << endl;
		x = xtemp;
	}
	else
	{
		cout << "-x = 0" << endl;
		exit(1);
	}
	
	if(yflag == 1){
		//cout << "-y = " << ytemp << endl;
		y = ytemp;
	}
	else
	{
		cout << "-y = No Argument" << endl;
		exit(1);
	}
	
	if(mflag == 1)
	{
		//cout << "-m = " << stringMesstemp << endl;
		stringMess = stringMesstemp;
	}
	else
	{
		cout << "-m = No Argument!" << endl;
		exit(1);
	}
	
	if(tflag == 1)
	{
		//cout << "-t = " << protocoltemp << endl;
		protocol = protocoltemp;
	}
	else
	{
		cout << "-t = No Argument!" << endl;
		exit(1);
	}
	
	if(sflag == 1)
	{
		//cout << "-s = " << Name_of_servertemp << endl;
		Name_of_server = Name_of_servertemp;
	}
	else
	{
		cout << "-s = No Argument!" << endl;
		exit(1);
	}
	
	if(portFlag == 1)
	{
		//cout << "-p = " << portNumbertemp << endl;
		portNumber = portNumbertemp;
	}
	else
	{
		cout << "-p = No Argument!" << endl;
		exit(1);
	}
	
	/*****************************************************************************************************/
	int sockfd, numbytes, len, bytes_sent;
	char buf[MAXDATASIZE];
	const char *msg;
	struct hostent *he;
	struct hostent *hos; //udp hosts
	struct sockaddr_in their_addr; // connector's address information 
	struct sockaddr_in my_addrMine; // My address information
	struct in_addr my_addr;
	struct sockaddr addr_info;//Socket info
	char buffer[MAXDATASIZE];//sending data buffer
	char recvBuffer[MAXDATASIZE];
	//struct data_info sending_data;
	socklen_t addr_len;

	//**********************************************************************************************************
	//check for tcp or udp
	if(protocol.compare("tcp") == 0)
	{
		//create a socket using SOCK_STREAM
		if((sockfd = socket(PF_INET, SOCK_STREAM, 0)) == -1)
		{
			perror("Error with creating a tcp socket");
			exit(1);
		}
		else
			cout << "Created a socket using TCP/IP" << endl;
	}
	/***********************************************************************************************
	 *	Using UDP
	 *
	 ************************************************************************************************/
	else if(protocol.compare("udp") == 0)
	{
		//create a socket using SOCK_DGRAM
		
		sockfd = socket(AF_INET, SOCK_DGRAM, 0);
		fcntl(sockfd, F_SETFL, O_NONBLOCK);

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
		
		their_addr.sin_family = AF_INET;    // host byte order 
		their_addr.sin_port = htons(portNumber); // short, network byte order
		their_addr.sin_addr = *((struct in_addr *)he->h_addr); // error seg faulting
		memset(their_addr.sin_zero, '\0', sizeof their_addr.sin_zero);
		
		//sending_data.len = strlen(sending_data.mess);//Length of message*/
		struct data_info * sending_data = (struct data_info *) buffer;
		msg = stringMess.c_str();
		len = strlen(msg) + 1;//length of string typed in by the user
		//send stuff into the buffer
		sending_data->vers = 0x01;
		sending_data->seq = htonl(0);
		sending_data->xInt = htonl(x);
		sending_data->yInt = htonl(y);
		sending_data->len = htonl(len);
		char * buf2 = buffer + sizeof (struct data_info);
		memcpy (buf2, msg, len);//copy the string typed into the buffer
		
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
		
		time(&start);
		while(!flag)
		{
			
			if((checkSend = sendto(sockfd, buffer, sizeof (struct data_info)+ len, 0, (struct sockaddr *)&their_addr, 
					sizeof their_addr)) == -1)
			{
				perror("Didn't send correctly\n");
			}
			
			if((checkRecv = recvfrom(sockfd, recvBuffer, MAXDATASIZE-1, 0, (struct sockaddr *)&their_addr, &addr_len)) == -1)
			{
				//perror("Didn't recv correctly");				
			}
			else
			{
				//cout << "Correctly recv" << endl;				
				flag = true;	
			}
			//wait for 3 seconds and quit sending after that
			time(&end);
			dif = difftime (end,start);
			if(dif == 3 && !flag)
			{
				flag = true;
				ackFlag = true;
			}
			
			sending_data->seq = htonl(seqNum++);
		}
		//Check for the ack message from server
		if(!ackFlag)
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
		}
		
		return 0;
		close(sockfd);
	}
	else
		{
			cout << "error with -t argument retype all lowercase!" << endl;
			exit(1);
		}
		
	//****************************************************************************************************************
	
	/*if (argc != 2) {
	    fprintf(stderr,"usage: client hostname\n");
	    exit(1);
	}*/

	//converts the string into a number ip address
	if ((he=gethostbyname(Name_of_server.c_str())) == NULL) {  // get the host info 
	    perror("retype host name");
	    exit(1);
	}

	their_addr.sin_family = AF_INET;    // host byte order 
	their_addr.sin_port = htons(portNumber);  // short, network byte order 
	their_addr.sin_addr = *((struct in_addr *)he->h_addr);
	memset(their_addr.sin_zero, '\0', sizeof their_addr.sin_zero);
	
	if (connect(sockfd, (struct sockaddr *)&their_addr, sizeof their_addr) == -1) {
	    perror("connect");
	    exit(1);
	}
	else
		cout << "Connection successful!" << endl;
		
	
	//Send some data............................
	
	
	//sending_data.len = strlen(sending_data.mess);//Length of message
	
	msg = stringMess.c_str();
	len = strlen(msg) + 1;//length of string typed in by the user
	
	//new struct pointing to buffer
	struct data_info * sending_data = (struct data_info *) buffer;
	
	//send stuff into the buffer
	sending_data->vers = 0x01;
	sending_data->seq = htonl(0);
	sending_data->xInt = htonl(x);
	sending_data->yInt = htonl(y);
	sending_data->len = htonl(len);
	char * buf2 = buffer + sizeof (struct data_info);
	memcpy (buf2, msg, len);//copy the string typed into the buffer
		
	/*cout << len << endl;*/
	
	//cout << sizeof (struct data_info) + len<< endl;//printing out string
	
	/*for(int i = 0; i < (sizeof (struct data_info))+len; i++)
	{
		printf("%x ", buffer[i]);//print out buffer in hex
	}
	cout << endl;*/
	
	if((bytes_sent = send(sockfd, buffer, sizeof (struct data_info)+ len, 0)) == -1)
	{
		perror("Nothing was sent");
	}
	else
		cout << "Packet sent" << endl;	
	
	
	/*Loop for ack message*/
	if ((numbytes = recv(sockfd, recvBuffer, MAXDATASIZE-1, 0)) == -1) {
	    perror("recv");
	    exit(1);
	}

	
	//create a ack message struct pointer
	struct ACK * msgAck = (struct ACK *) recvBuffer;
	char tmp = msgAck->vers;
	
	cout << "ACK message recieved!" << endl;
	printf("Version: %x \n", tmp);
	cout << "Sequence: " << ntohl(msgAck->seq) << endl; 	
 	cout << endl;
	

	close(sockfd);
	
	return 0;
}
