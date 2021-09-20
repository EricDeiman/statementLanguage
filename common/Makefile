#
#  The statementLanguage programming language
#  Copyright 2016 Eric J. Deiman
#
#  This file is part of the statementLanguage programming language.
#  The statementLanguage programming language is free software: you can redistribute it
#  and/ormodify it under the terms of the GNU General Public License as published by the
#  Free Software Foundation, either version 3 of the License, or (at your option) any
#  later version.
#  
#  The statementLanguage programming language is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
#  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
#  You should have received a copy of the GNU General Public License along with the
#  statementLanguage programming language. If not, see <https://www.gnu.org/licenses/>
#

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
