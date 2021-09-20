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

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.FileInputStream;
import java.io.InputStream;

import parser.*;
import common.RuntimeError;
import common.Scope;

public class Main {
    public static void main(String[] args) throws Exception {
        String inputFileName = null;
        if(args.length > 0) {
            inputFileName = args[0];
        }

        int pos = inputFileName.lastIndexOf('.');
        String target = inputFileName.substring(pos);
        String outputFileName = inputFileName.replace(target, ".o");

        InputStream is = System.in;
        if(inputFileName != null) {
            is = new FileInputStream(inputFileName);
        }

        CharStream input = CharStreams.fromStream(is);
        StmntLexer lexer = new StmntLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        StmntParser parser = new StmntParser(tokens);

        ParseTree tree = parser.prog();

        if(parser.getNumberOfSyntaxErrors() == 0){
            try {
                ParseTreeProperty<Scope> scopes = new ParseTreeProperty<Scope>();
                MutableListener collectMutables = new MutableListener(scopes);
                ParseTreeWalker walker = new ParseTreeWalker();
                walker.walk(collectMutables, tree);

                Compile compiler = new Compile(scopes);
                compiler.visit(tree);
                compiler.writeCodeTo(outputFileName);
            }
            catch(RuntimeError err) {
                System.err.println("The program doesn't mean what you think it means: " +
                                   err.getMessage());
            }
        }
        else {
            System.out.println("oops! try again.");
        }
    }
}
