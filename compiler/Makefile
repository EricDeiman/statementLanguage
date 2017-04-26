JAVAC=javac
JARS:=../bin/
JAVACFLAGS:=-cp $(CLASSPATH)../lib/Stmnt.jar:../lib/common.jar
JAR=jar
JARFLAGS=cfm
CP:= cp

PROJECTNAME:=compile
SOURCE:=$(wildcard *.java)
JARNAME:=$(PROJECTNAME).jar

.DEFAULT : $(JARNAME)

$(JARNAME) : $(SOURCE)
	$(JAVAC) $(JAVACFLAGS) $(SOURCE)
	$(JAR) $(JARFLAGS) $@ Manifest.txt *.class
	$(CP) $(JARNAME) $(JARS)

.PHONY : clean scrub

clean :
	rm -f *.class

scrub : clean
	rm -f $(JARNAME)
