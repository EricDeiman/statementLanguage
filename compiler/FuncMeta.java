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

import common.Labeller;

public class FuncMeta {
    public FuncMeta(Labeller labeller, List<TerminalNode> names) {
        name = names.remove(0).getText();
        label = labeller.make(name);
        parameters = new Vector<String>();

        for(TerminalNode param : names) {
            parameters.add(param.getText());
        }

        internalName = String.format("%s\\%d", name, parameters.size());
    }

    public String getName() {
        return name;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getParameters() {
        return parameters;
    }

    private String name;
    private String label;
    private List<String> parameters;
    private String internalName;
}
