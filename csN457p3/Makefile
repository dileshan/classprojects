#bah
#bach

bgpmonclient: bgpmonclient.o message.o ISP.o
	g++ -o bgpmonclient bgpmonclient.o ISP.o message.o
	
bgpmonclient.o: bgpmonclient.cpp message.h
	g++ -c bgpmonclient.cpp

message.o: message.cpp message.h
	g++ -c message.cpp

ISP.o: ISP.cpp message.h
	g++ -c ISP.cpp

clean:
	rm bgpmonclient bgpmonclient.o message.o

# that's all
