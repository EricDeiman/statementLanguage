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
    Boolean inFunction = false;
    Boolean seenReturn = false;
}
