temp: proj2.o Dvd.o
	g++ -g proj2.o Dvd.o -o temp
proj2.o: proj2.h proj2.cpp
	g++ -c proj2.cpp
Dvd.o: proj2.h Dvd.cpp
	g++ -c Dvd.cpp
#Client.o: proj2.h Client.cpp
#	g++ -c Client.cpp

clean:
	rm temp
	rm proj2.o
	rm Dvd.o
#	rm Client.o
