GCC	= gcc
COMPILE  = $(GCC) -g -Wall -I"/usr/include/libxml2"

all: bgpmonclient

bgpmonclient:
	$(COMPILE) bgpmonclient.cpp -o bgpmonclient -fpack-struct -lz -lm
	
clean:
	rm -f bgpmonclient  
