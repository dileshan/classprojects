#Uncomment line below if running on a Linux machine
JDKPATH = /soft/java/Linux_x64/jdk1.6.0_02

#Uncomment line below if running on a SunOS machine
#JDKPATH = /soft/java/SunOS/jdk1.6.0_02

LIBPATH = /class/csci5708/minibase/assign/BufMgr/lib/bufmgrAssign.jar
CLASSPATH = $(JDKPATH)/lib/classes.zip:.:..:$(LIBPATH)
BINPATH = $(JDKPATH)/bin
JAVAC = $(JDKPATH)/bin/javac -classpath $(CLASSPATH)
JAVA  = $(JDKPATH)/bin/java  -classpath $(CLASSPATH)

PROGS = together

all: $(PROGS)

bm:BufMgr.java
        $(JAVAC) BufMgr.java

together:*.java
        $(JAVAC) *.java

xx:
        jar tf $(LIBPATH)

clean:
        \rm -f *.class *~ \#* core
