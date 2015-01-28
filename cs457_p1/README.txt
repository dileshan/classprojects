Matt Floyd
Matt Desilvey
cs457
project 1

TO RUN SERVER:

for TCP:(you can enter tcp or TCP there is error checking)
>g++ -o myserver server.cpp
>myserver -t tcp -p 3490

for UDP:(you can enter udp or UDP there is error checking)
>g++ -o myserver server.cpp
>myserver -t udp -p 3490

both run till you Crtl c them

TO RUN CLIENT:


for TCP: type argument -t tcp
g++ -g client.cpp -o cli -lm
Example: cli -x 43 -y 3 -m "Hello dude" -s hostname -p portnumber -t tcp

for UDP: type argument -t udp
g++ -g client.cpp -o cli -lm
Example: cli -x 43 -y 3 -m "Hello dude" -s hostname -p portnumber -t udp
