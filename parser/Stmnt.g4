/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

grammar Stmnt;

prog : (statement | funcDecl)* EOF
     ;

funcDecl : ID '(' ( ID (',' ID)* )? ')' funcBody
         ;

statement : 'print' expression+ EOS #PrintStmnt
          | ID '<-' expression EOS #Assign
          | 'if' ifBlock ('else' 'if' ifBlock)* ('else' block)? #IfStmnt
          | 'while' '(' test=logicExp ')' body=block  #WhileStmnt
          | expression EOS #ExpressionStmnt
          ;

block : '{' statement*  '}'
      ;

funcBody : '{' statement* 'return' expression EOS '}'
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

