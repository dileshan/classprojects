run: nifty.o Main.o
	g++ -g nifty.o Main.o -o run -lm

nifty.o: nifty.h nifty.cpp
	g++ -c nifty.cpp

Main.o: nifty.h Main.cpp
	g++ -c Main.cpp

clean:
	rm run
	rm -f nifty.o
	rm -f main.o
