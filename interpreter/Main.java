import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.InputStream;

import parser.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String inputFileName = null;
        if(args.length > 0) {
            inputFileName = args[0];
        }

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
                StmntInterpreter interpreter = new StmntInterpreter();
                interpreter.visit(tree);
            }
            catch(Error err) {
                System.err.println("The program doesn't mean what you think it means: " +
                                   err.getMessage());
            }
        }
        else {
            System.out.println("oops! try again.");
        }
    }
}
