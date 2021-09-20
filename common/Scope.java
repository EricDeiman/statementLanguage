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

package common;

import java.util.Vector;

import common.RuntimeError;

public class Scope {
    
    public Scope(Scope parent) {
        this.parent = parent;
        store = new Vector<String>();
    }

    public Scope getParent() {
        return parent;
    }

    public Boolean contains(String name) {
        if(parent == null && !store.contains(name)) {
            return false;   
        } else if(store.contains(name)) {
            return true;
        }
        return parent.contains(name);
    }

    public void put(String name) {
        if(!contains(name)) {
            store.add(name);
        }
    }

    public void putShadow(String name) {
        if(!store.contains(name)) {
            store.add(name);
        }
    }

    public Vector<String> getNames() {
        return store;
    }
    
    public LookupPair get(String name) {
        LookupPair rtn;
        if(store.contains(name)) {
            rtn = new LookupPair();
            rtn.frames = 0;
            rtn.offset = store.indexOf(name);
        } else if(parent != null && parent.contains(name)) {
            rtn = parent.get(name);
            rtn.frames++;
        }
        else {
            throw new RuntimeError("Cannot find " + name + " in scopes");
        }

        return rtn;
    }

    private Scope parent;
    private Vector<String> store;
}
