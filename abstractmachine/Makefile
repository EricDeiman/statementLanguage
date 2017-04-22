JAVAC=javac
JARS:=../jars/
JAVACFLAGS:=-cp $(CLASSPATH)../jars/Stmnt.jar:../jars/common.jar
JAR=jar
JARFLAGS=cfm
CP:= cp

PROJECTNAME:=am
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
