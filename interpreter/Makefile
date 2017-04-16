JAVAC=javac
JARS:=../jars/Stmnt.jar
JAVACFLAGS:=-cp $(CLASSPATH):$(JARS)
JAR=jar
JARFLAGS=cfm

PROJECTNAME:=$(shell basename `pwd`)
SOURCE:=$(wildcard *.java)
OBJ:=$(SOURCE:.java=.class)

.DEFAULT : $(PROJECTNAME).jar

$(PROJECTNAME).jar : $(OBJ)
	$(JAR) $(JARFLAGS) $@ Manifest.txt *.class

%.class : %.java
	$(JAVAC) $(JAVACFLAGS) *.java

.PHONY : clean scrub

clean :
	rm -f *.class

scrub : clean
	rm -f $(PROJECTNAME).jar
