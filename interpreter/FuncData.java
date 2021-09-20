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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.antlr.v4.runtime.tree.TerminalNode;

import parser.StmntParser.BlockContext;

public class FuncData {

    public FuncData(List<TerminalNode> names, BlockContext body) {
        this.body = body;
        this.name = names.remove(0).getText();
        parameters = new Vector<String>();

        for(TerminalNode id : names) {
            parameters.add(id.getText());
        }

        internalName = String.format("%s\\%d", name, parameters.size());
    }

    public String getName() {
        return name;
    }

    public String getInternalName() {
        return internalName;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public BlockContext getBody() {
        return body;
    }

    private String name;
    private List<String> parameters;
    private BlockContext body;
    private String internalName;
}
