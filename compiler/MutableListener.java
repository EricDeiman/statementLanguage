import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import parser.*;

import java.util.Vector;

public class MutableListener extends StmntBaseListener {
    public MutableListener() {
        mutables = new Vector<String>();
    }

    @Override
    public void enterAssign(StmntParser.AssignContext ctx) {
        String name = ctx.ID().getText();
        if(!mutables.contains(name)) {
            mutables.add(name);
        }
    }

    public Vector<String> getNames() {
        return mutables;
    }

    private Vector<String> mutables;
}
