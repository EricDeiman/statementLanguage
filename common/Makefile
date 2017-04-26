JAVAC:=javac
JAVACFLAGS:=
JAR:=jar
JARFLAGS:= cf
JARDIR:=../lib
CP:=cp
PROJECTNAME:=$(shell basename $(shell pwd))
JARNAME:=$(PROJECTNAME).jar
SOURCE:=$(wildcard *.java)

.DEFAULT : $(JARNAME)

$(JARNAME) : $(SOURCE)
	$(JAVAC) $(JAVAFLAGS) *.java
	$(JAR) $(JARFLAGS) $@ ../common/*.class
	$(CP) $(JARNAME) $(JARDIR)

.PHONY : clean scrub

clean :
	rm -f *.class

scrub : clean
	rm -f $(JARNAME)
