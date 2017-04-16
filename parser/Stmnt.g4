/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

grammar Stmnt;

prog : statement* EOF
     ;

statement : 'print' expression+ EOS #PrintExp
          | ID '<-' expression EOS #Assign
          ;

expression : arithExp 
           | stringExp
           ;

arithExp : '(' arithExp ')' #ArithGroup
         | <assoc=right> left=arithExp '^' right=arithExp #Power
         | left=arithExp op=('*'|'div'|'rem') right=arithExp #Mult
         | left=arithExp op=('+'|'-') right=arithExp #Add
         | NUMBER #Number
         | ID #Id
         ;

stringExp : STRING
          ;

EOS : ';' ;

NUMBER : DIGIT+ ;
ID : ALPHA(ALPHA|DIGIT|IDSYMBOLS)* ;

STRING : '"' (ESC|.)*? '"' ;

ALPHA : [a-zA-Z] ;
DIGIT : [0-9] ;

fragment
IDSYMBOLS : [!@$%&*=?+:/-] ;

fragment
ESC : '\\"' | '\\\\' ; // The 2 character escape symbols \" and \\

WS : [ \t\r\n]+ -> skip ;

