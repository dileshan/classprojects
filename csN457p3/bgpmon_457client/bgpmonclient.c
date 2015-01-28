/* 
 * 	Copyright (c) 2007 Colorado State University
 * 
 *	Permission is hereby granted, free of charge, to any person
 *	obtaining a copy of this software and associated documentation
 *	files (the "Software"), to deal in the Software without
 *	restriction, including without limitation the rights to use,
 *	copy, modify, merge, publish, distribute, sublicense, and/or
 *	sell copies of the Software, and to permit persons to whom
 *	the Software is furnished to do so, subject to the following
 *	conditions:
 *
 *	The above copyright notice and this permission notice shall be
 *	included in all copies or substantial portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *	EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *	OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *	NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *	HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *	WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *	FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *	OTHER DEALINGS IN THE SOFTWARE.
 * 
 * 
 *  File: bgpmonclient.c
 * 	Authors: Dave matthews, He Yan, Nick Parrish
 *  Data: May 31, 2007 
 */

/*
 * A sample client for the bgpmon server
 *  
 * bgpmonclient server-ip server-port 
 * 
 * Will write stream of XML to stdout.
 * 
 * The required parameters are:
 * 
 * server-ip = ip address of bgpmon server
 * server-port = port number for bgpmon server
 * 
 * Important:
 * If you wish to modify this client, remember that you
 * are monitoring a real-time stream and need to process 
 * it in real-time!  Slow clients will be terminated!
 * 
 * The requests are sent to the bgpmon server in a XML format
 */
 
#include <stdio.h>      
#include <sys/socket.h> 
#include <arpa/inet.h>  
#include <stdlib.h>    
#include <string.h>     
#include <unistd.h>     
#include <errno.h>

void fatal(char *errorMessage)
{
    perror(errorMessage);
    exit(1);
}

ssize_t
readn( int fd, void *vptr, size_t n)
{
	/* Read N bytes from a socket.
	 */
	unsigned char 			*ptr;
	ptr = vptr;

	size_t 		nleft;
	ssize_t 	nread;
	nleft = n;
	while (nleft > 0)
	{
		if ((nread = read(fd, ptr, nleft)) < 0)
		{
			if (errno == EINTR)
				nread = 0;
			else
				return(-1);
		}
		else if (nread == 0) // EOF
			break;

		nleft -= nread;
		ptr += nread;
	}
	return (n - nleft); // return >= 0
}


int main(int argc, char *argv[])
{
    char *servIP = NULL; 
    char *servPort = NULL;                   

    switch ( argc )
    {
	    case 3:
 	   		servPort = argv[2];
 	   		servIP = argv[1];
 	   		break;
 	   	default:
 	   		fprintf(stderr, "Usage: %s <bgpmon IP> <bgpmon Port> \n", argv[0]);
				exit(1);
      	break;
    }

    /* Create a reliable, stream socket using TCP */
    int sock;                        
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0)
        fatal("socket() failed");

    /* Construct the server address structure  and establish a connection */
    unsigned short echoServPort = atoi(servPort);     
    struct sockaddr_in echoServAddr; 
    memset(&echoServAddr, 0, sizeof(echoServAddr));     /* Zero out structure */
    echoServAddr.sin_family      = AF_INET;             /* Internet address family */
    echoServAddr.sin_addr.s_addr = inet_addr(servIP);   /* Server IP address */
    echoServAddr.sin_port        = htons(echoServPort); /* Server port */
    if (connect(sock, (struct sockaddr *) &echoServAddr, sizeof(echoServAddr)) < 0)
        fatal("connect() failed");

    /* Construct the request from command line arguments and send to the server */
    int size=2048;
    char *xml = malloc(size+1);
    xml[0] = '\0';
    xml[size] = '\0'; // null terminator after the buffer, just in case ;-)
    int lxml = 0;
    char *xmlver = ""; // "<?xml version=\"1.0\"?>\n";
    char *xmlname = "test client";
    char *type = "message";

    lxml = snprintf( xml, size, "%s<request type=\"%s\" name=\"%s\"/>\n\n", xmlver, type, xmlname );
    
	//fprintf(stderr, "Sending %s\n", xml);
    if ( send(sock, xml, lxml, 0) != lxml )
    	fatal("request sent a different number of bytes than expected");
	//fprintf(stderr, "Receiving\n");

    /* Echo the response back from the server */
    xml[size] = '\0';
    
    for ( ;; )
    {
        int len = readn(sock, xml, size); // size); 
        /* got it all, now echo it out */
        if (len <= 0 )
          break;
        fwrite(xml, 1, len, stdout);
        fflush( stdout );
        // scan for <die> in buffer
        if ( strstr( xml, "<die>" ) != NULL )
        	break;	    
        	    
    }	    
	
	fprintf(stderr, "Done\n");

    close(sock);
    exit(0);
} 


