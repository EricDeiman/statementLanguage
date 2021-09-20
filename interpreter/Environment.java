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


import java.util.Vector;
import java.util.HashMap;

/**
 * <p>This environment is to support block scoping.  When the environment is built,
 * it has one (global) key-value store.  When a block is entered, a new scope is entered,
 * and this environment will create another key-value store.</p>
 *
 * <p>When a new key-value pair is added, it will always be to the newest scope.</p>
 *
 * <p>When a key is searched for, the search starts from the most recent scope and
 * works backwards through scopes until the key if found or the scopes exhausted.<p>
 *
 * <p>When a block is exited, the most recently created store is removed.</p>
 */
public class Environment {

    public Environment() {
        store = new Vector<HashMap<String, InterpValue>>();
        store.add(new HashMap<String, InterpValue>());
    }

    /**
     * This is a 2-step process.  First check to see if key already exits in some
     * scope.  If it does, use that.  If it does not, then add it to the most recent
     * scope.
     */
    public InterpValue put(String key, InterpValue value) {
        for(int i = store.size() - 1; 0 <= i; i--) {
            if(store.get(i).containsKey(key)) {
                return store.get(i).put(key, value);
            }
        }
        return store.get(store.size() - 1).put(key, value);
    }

    /**
     * Put key into the most recent scope even if it hides the same key in a previous
     * scope.
     */
    public InterpValue putShadow(String key, InterpValue value) {
        return store.get(store.size() - 1).put(key, value);
    }

    public InterpValue get(String key) {
        for(int i = store.size() - 1; 0 < i; i--) {
            if(store.get(i).containsKey(key)) {
                return store.get(i).get(key);
            }
        }
        return store.get(0).get(key);
    }

    public void beginScope() {
        store.add(new HashMap<String, InterpValue>());
    }

    public void endScope() {
        store.remove(store.size() - 1);
    }

    private Vector<HashMap<String, InterpValue>> store;
}
