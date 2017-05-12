import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import parser.*;

import java.util.List;
import java.util.Vector;

import common.Scope;

public class MutableListener extends StmntBaseListener {
    public MutableListener(ParseTreeProperty<Scope> scopes) {
        this.scopes = scopes;
    }

    @Override
    public void enterProg(StmntParser.ProgContext ctx) {
        currentScope = new Scope(null);
        scopes.put(ctx, currentScope);
    }

    @Override
    public void enterFuncDecl(StmntParser.FuncDeclContext ctx) {
        currentScope = new Scope(currentScope);
        scopes.put(ctx, currentScope);
        List<TerminalNode> names = ctx.ID();
        for(int i = 1; i < names.size(); i++) {
            currentScope.put(names.get(i).getText());
        }
    }

    @Override
    public void exitFuncDecl(StmntParser.FuncDeclContext ctx) {
        currentScope = currentScope.getParent();
    }

    @Override
    public void enterBlock(StmntParser.BlockContext ctx) {
        currentScope = new Scope(currentScope);
        scopes.put(ctx, currentScope);
    }

    @Override
    public void exitBlock(StmntParser.BlockContext ctx) {
        currentScope = currentScope.getParent();
    }

    @Override
    public void enterFuncBody(StmntParser.FuncBodyContext ctx) {
        currentScope = new Scope(currentScope);
        scopes.put(ctx, currentScope);
    }

    @Override
    public void exitFuncBody(StmntParser.FuncBodyContext ctx) {
        currentScope = currentScope.getParent();
    }

    @Override
    public void enterAssign(StmntParser.AssignContext ctx) {
        String name = ctx.ID().getText();
        currentScope.put(name);
    }

    private ParseTreeProperty<Scope> scopes;
    private Scope currentScope;
}
