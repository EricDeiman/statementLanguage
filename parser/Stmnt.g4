/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

grammar Stmnt;

prog : statement* EOF
     ;

statement : 'print' expression EOS #Print
          | ID '<-' expression EOS #Assign
          ;

expression : arithExp #Arith
           ;

arithExp : '(' arithExp ')' #ArithGroup
         | <assoc=right> left=arithExp '^' right=arithExp #Power
         | left=arithExp op=('*'|'/') right=arithExp #Mult
         | left=arithExp op=('+'|'-') right=arithExp #Add
         | NUMBER #Number
         | ID #Id
         ;

EOS : ';' ;

NUMBER : DIGIT+ ;
ID : ALPHA(ALPHA|DIGIT|IDSYMBOLS)* ;

ALPHA : [a-zA-Z] ;
DIGIT : [0-9] ;

fragment
IDSYMBOLS : [!@$%&*=?+:/-] ;

WS : [ \t\r\n]+ -> skip ;

