import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.FileInputStream;
import java.io.InputStream;

import parser.*;
import common.RuntimeError;

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
                MutableListener collectMutables = new MutableListener();
                ParseTreeWalker walker = new ParseTreeWalker();
                walker.walk(collectMutables, tree);

                Compile compiler = new Compile(collectMutables.getNames());
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
