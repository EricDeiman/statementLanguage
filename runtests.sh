#
#  The statementLanguage programming language
#  Copyright 2017 Eric J. Deiman
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

for f in tests/*.stmnt
do
    java -jar bin/interpreter.jar $f > $f.i
    java -jar bin/compile.jar $f
    java -jar  bin/am.jar ${f/.stmnt/.o} > $f.c
    cmp -s $f.i $f.c
    if [ $? -ne 0 ]; then
        echo Trouble with $f
    fi
done
