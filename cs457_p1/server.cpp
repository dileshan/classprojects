/* Names: Matt Floyd, Matt Desilvey
** Date: 2/1/08
** cs457 p1
*/

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/wait.h>
#include <signal.h>
#include <iostream>
#include <string>

using namespace std;

//#define MYPORT 3490	
#define BACKLOG 10	 // how many pending connections queue will hold
#define MAXDATASIZE 100 // max number of bytes we can get at once 
/* Create structure of the header for the packet */
struct msgRecv{
	char vers;
	unsigned int sequence;
	unsigned int x;
	unsigned int y;
	unsigned int len;
}__attribute__((packed));

/* Create struct for ACK to return to client */
struct ACK{
	char vers;
	unsigned int seq;
}__attribute__((packed));

void sigchld_handler(int s)
{
	while(waitpid(-1, NULL, WNOHANG) > 0);
}
/* Main Class  ------------------------------------ */
int main(int argc, char **argv)
{
	int sockfd, new_fd;  // listen on sock_fd, new connection on new_fd
	struct sockaddr_in my_addr;	// my address information
	struct sockaddr_in their_addr; // connector's address information
	socklen_t sin_size;
	struct sigaction sa;
	char buf[MAXDATASIZE];
	char buf2[MAXDATASIZE];
	struct ACK sendAck;
	socklen_t addr_len;
	int numbytes;
	int yes=1;
	char *temp2;
	string connectionType;
	int portNum = 0;
	int c;
	/* Run while to get input in any order using getopt */
	while ((c = getopt (argc, argv, "tp:")) != -1)
         switch (c)
           {
           case 't':
             connectionType = argv[optind];
             break;
           case 'p':
             temp2 = optarg;
             portNum = atoi(temp2);
             break;
           case '?':
             if (optopt == 'c')
               fprintf (stderr, "Option -%c requires an argument.\n", optopt);
             //else if (isprint (optopt))
               //fprintf (stderr, "Unknown option `-%c'.\n", optopt);
             else
               fprintf (stderr,
                        "Unknown option character `\\x%x'.\n",
                        optopt);
             return 1;
           default:
             abort ();
           }
	//Print out what connection type and what port number being used
	cout << "Connection Type: " << connectionType << endl;
	cout << "Port Number: " << portNum << endl;
	//Create sockets based on tcp or udp and if not then quit
	if(connectionType == "tcp" || connectionType == "TCP")
	{
		if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
			perror("tcp socket");
			exit(1);
		}
		cout << "TCP socket created" << endl;
	}
	else if(connectionType == "udp" || connectionType == "UDP")
	{
		if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) == -1) {
			perror("udp socket");
			exit(1);
		}
		cout << "UDP socket created" << endl;
	}
	else
	{
		cout << "entered in wrong type must be upd or tcp" << endl;
		exit(1);
	}

	if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int)) == -1) {
		perror("setsockopt");
		exit(1);
	}
	
	my_addr.sin_family = AF_INET;		 // host byte order
	my_addr.sin_port = htons(portNum);	 // short, network byte order
	my_addr.sin_addr.s_addr = INADDR_ANY; // automatically fill with my IP
	memset(my_addr.sin_zero, '\0', sizeof my_addr.sin_zero);
	//Bind the socket to that port
	if (bind(sockfd, (struct sockaddr *)&my_addr, sizeof my_addr) == -1) {
		perror("bind");
		exit(1);
	}
	//If connect type TCP start working on listening
	if(connectionType.compare("tcp") == 0 || connectionType.compare("TCP") == 0)
	{
		cout << "TCP" << endl;
		//Listen for connections
		if (listen(sockfd, BACKLOG) == -1) 
		{
			perror("listen");
			exit(1);
		}
		//
		sa.sa_handler = sigchld_handler; // reap all dead processes
		sigemptyset(&sa.sa_mask);
		sa.sa_flags = SA_RESTART;
		if (sigaction(SIGCHLD, &sa, NULL) == -1) 
		{
			perror("sigaction");
			exit(1);
		}
	
		while(1) 
                {  // main accept() loop
                   cout << "Waiting for client...." << endl;
		   sin_size = sizeof their_addr;
		   //accept from client
		   if ((new_fd = accept(sockfd, (struct sockaddr *)&their_addr, &sin_size)) == -1) {
			  perror("accept");
			  continue;
		   }
		   printf("server: got connection from %s\n",inet_ntoa(their_addr.sin_addr));
		        //  if (!fork()) { // this is the child process
		      	//close(sockfd); // child doesn't need the listener
		      	
		    // recieve packet  	
		   if ((numbytes = recv(new_fd, buf, MAXDATASIZE-1, 0)) == -1) {
        				perror("recv");
        				exit(1);
		   	    }
	       		    
		   	    
		   	   //set buf to header struct	
		   	   struct msgRecv * msg = (struct msgRecv *) buf;
		   	   //check version number
		   	   if(msg->vers != 1)
		   	   {
		   		   cout << "Recieved wrong version number!" << endl;
		   		   exit(1);
		   	   }
		   	   
		   	   //create a ack message for the client
		   	   struct ACK * ackBack = (struct ACK *) buf2;
		   	   int tempSeq = ntohl(msg->sequence);
		   	   int num1 = ntohl(msg->x);
		   	   int num2 = ntohl(msg->y);
		   	   
		   	   //cout << "sequence#: " << tempSeq << endl;
		   	   
		   	   ackBack->vers = 0x01;
		   	   ackBack->seq = htonl(tempSeq);
		   	   
		   	   cout << endl;
		   	   char arrStrbuf[numbytes];
		   	   char *ptrCopy = buf + sizeof (struct msgRecv);
		   	   memcpy(arrStrbuf, ptrCopy, numbytes - sizeof (struct msgRecv));
		   	   string strprint = arrStrbuf;
		   	   
		   	   cout << "First number: " << num1 << endl;
		   	   cout << "Message: " << strprint << endl;		
		   	   cout << "Second number: " << num2 << endl;		   	      	   
		   	   cout << "Sum of numbers: " << num1 + num2 << endl << endl;		   	   

		       if(numbytes = send(new_fd, buf2, sizeof (struct ACK), 0) == -1)
		       {
		    	   perror("send");
		    	   exit(1);
		       }
		       cout << "Sent ACK" << endl;
		 // exit(0);
		//}
		//close(new_fd);  // parent doesn't need this
		}
		close(new_fd);
	}
	// check to see if you its a UDP and if it is recvfrom whoever sends....
	if(connectionType.compare("udp") == 0 || connectionType.compare("UDP") == 0)
	{
	while(1){// loop for server
		addr_len = sizeof their_addr;
		cout << "Waiting to recieve" << endl;
   		if ((numbytes = recvfrom(sockfd, buf, MAXDATASIZE-1, 0,(struct sockaddr *)&their_addr, &addr_len)) == -1) {
        		perror("recvfrom");
        		exit(1);
   		 }
   		 printf("got packet from %s\n",inet_ntoa(their_addr.sin_addr));
    		 if(numbytes > 17)
    		 {
		   	   struct msgRecv * msg = (struct msgRecv *) buf;
		   	   if(msg->vers != 1)
		   	   {
		   		   cout << "Recieved wrong version number!" << endl;
		   		   exit(1);
		   	   }
		   	   
		   	   //create a ack message for the client and print out data
		   	   struct ACK * ackBack = (struct ACK *) buf2;
		   	   int tempSeq = ntohl(msg->sequence);
		   	   int num1 = ntohl(msg->x);
		   	   int num2 = ntohl(msg->y);
		   	   
		   	   //cout << "sequence#: " << tempSeq << endl;
		   	   
		   	   ackBack->vers = 0x01;
		   	   ackBack->seq = htonl(tempSeq);
		   	   
		   	   cout << endl;
		   	   char arrStrbuf[numbytes];
		   	   char *ptrCopy = buf + sizeof (struct msgRecv);
		   	   memcpy(arrStrbuf, ptrCopy, numbytes - sizeof (struct msgRecv));
		   	   string strprint = arrStrbuf;
		   	   
		   	   cout << "First number: " << num1 << endl;
		   	   cout << "Message: " << strprint << endl;		
		   	   cout << "Second number: " << num2 << endl;		   	      	   
		   	   cout << "Sum of numbers: " << num1 + num2 << endl << endl;		   	   
			//send to client that you recv from
       		 	if ((numbytes = sendto(sockfd, buf2, MAXDATASIZE-1, 0, (struct sockaddr *)&their_addr, sizeof their_addr)) == -1) 				{
        			perror("sendto");
        			exit(1);
    			 }
    			 cout << "Sent ACK" << endl;
    		 }
    		 else
    		 {
    		 	cout << "Didn't recieve all of packet, try again!" << endl;
    		 	//exit(1);
    		 }
    		 	
       		 
       	    }	
       	    close(sockfd);	 
	}
	cout << "Finish" << endl;
	return 0;
}

