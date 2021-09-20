/*
  The statementLanguage programming language
  Copyright 2016 Eric J. Deiman

  This file is part of the statementLanguage programming language.
  The statementLanguage programming language is free software: you can redistribute it
  and/ormodify it under the terms of the GNU General Public License as published by the
  Free Software Foundation, either version 3 of the License, or (at your option) any
  later version.
  
  The statementLanguage programming language is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with the
  statementLanguage programming language. If not, see <https://www.gnu.org/licenses/>
*/


grammar Stmnt;

prog : (statement | funcDecl)* EOF
     ;

funcDecl : ID '(' ( ID (',' ID)* )? ')' block
         ;

statement : 'print' expression+ EOS #PrintStmnt
          | ID '<-' expression EOS #Assign
          | 'if' ifBlock ('else' 'if' ifBlock)* ('else' block)? #IfStmnt
          | 'while' '(' test=logicExp ')' body=block  #WhileStmnt
          | expression EOS #ExpressionStmnt
          | 'return' expression EOS #ReturnStmnt
          ;

block : '{' statement*  '}'
      ;

ifBlock : '(' test=logicExp  ')' body=block
        ;

expression : arithExp #ArithE
           | stringExp #StringE
           | logicExp #LogicE
           ;

arithExp : ID '(' (expression (',' expression)* )? ')' #FuncCall
         | '(' arithExp ')' #ArithGroup
         | <assoc=right> left=arithExp '^' right=arithExp #Power
         | left=arithExp op=('*'|'div'|'rem') right=arithExp #Mult
         | left=arithExp op=('+'|'-') right=arithExp #Add
         | NUMBER #Number
         | ID #Id
         ;

stringExp : STRING
          ;

logicExp : '(' logicExp ')' #LogicGroup
         | 'not' logicExp #LogicNot
         | left=logicExp 'and' right=logicExp #LogicAnd
         | left=logicExp 'or' right=logicExp #LogicOr
         | intRelExp #LogicIntRel
         | stringRelExp #LogicStringRel
         | boolLit #LogicLit
         | ID #LogicId
         ;

boolLit : 'true' #LitTrue
        | 'false' #LitFalse
        ;

intRelExp : left=arithExp op=('<'|'<='|'?='|'!='|'>='|'>') right=arithExp
          ;

stringRelExp : left=stringExp op=('<'|'<='|'?='|'!='|'>='|'>') right=stringExp
             ;

EOS : ';' ;

NUMBER : '-'? DIGIT(DIGIT|'_')* ;
ID : ALPHA(ALPHA|DIGIT|IDSYMBOLS)* ;

STRING : '"' (ESC|.)*? '"' ;

ALPHA : [a-zA-Z] ;
DIGIT : [0-9] ;

fragment
IDSYMBOLS : [!@$%&*=?+:/-] ;

fragment
ESC : '\\"' | '\\\\' ; // The 2 character escape symbols \" and \\

COMMENTEOL : '#' .*? '\r'? '\n' -> skip ;
WS : [ \t\r\n]+ -> skip ;

