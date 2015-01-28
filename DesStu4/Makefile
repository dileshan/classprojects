#Adam Schaffner & Matt DeSilvey
#Makefile for Parking Ticket pay system

run: ParkingTicket.o PayTix.o
	g++ -g ParkingTicket.o PayTix.o -o run

ParkingTicket.o: Extra.h ParkingTicket.cpp
	g++ -c ParkingTicket.cpp

PayTix.o: Extra.h PayTix.cpp
	g++ -c PayTix.cpp

clean:
	rm ParkingTicket.o
	rm PayTix.o
	rm run