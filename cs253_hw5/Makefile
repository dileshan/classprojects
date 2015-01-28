train: Zone.o main.o Path.o Route.o Station.o ParseCheck.o
	g++ -g Zone.o main.o Path.o Route.o Station.o ParseCheck.o -o train

main.o: train.h main.cpp
	g++ -c main.cpp

Route.o: train.h Route.cpp
	g++ -c Route.cpp

Station.o: train.h Station.cpp
	g++ -c Station.cpp

ParseCheck.o: train.h ParseCheck.cpp
	g++ -c ParseCheck.cpp

Zone.o: train.h Zone.cpp
	g++ -c Zone.cpp

Path.o: train.h Path.cpp
	g++ -c Path.cpp

clean:
	rm -f train
	rm -f Zone.o
	rm -f main.o
	rm -f Route.o
	rm -f Station.o
	rm -f ParseCheck.o
	rm -f Path.o
