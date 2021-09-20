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

import java.util.HashMap;
import java.util.Vector;

/**
 * Track where in the code labels are referenced.  Once label addresses are known,
 * patch those references with the actual address the label refers to.
 */
public class BackPatch {

    public BackPatch() {
        codeReferences = new HashMap<String, Vector<Integer>>();
    }

    /**
     * 'where' indicates a place in the code that needs the address of 'label' patched
     * in.
     */
    public void addBackPatch(String label, Integer where) {
        if(codeReferences.containsKey(label)) {
            codeReferences.get(label).add(where);
        }
        else {
            Vector<Integer> v = new Vector<Integer>();
            v.add(where);
            codeReferences.put(label, v);
        }
    }

    public Boolean doBackPatches(HashMap<String, Integer> labels, CodeBuffer code) {
        for(String key : codeReferences.keySet()) {
            if(!labels.containsKey(key)) {
                throw new Error("in BackPatch.doBackPatches: cannot find label " + key +
                                " in the provided labels. Giving up.");
            }
            Vector<Integer> v = codeReferences.get(key);
            Integer address = labels.get(key);
            for(Integer index : v) {
                code.putInteger(address, index);
            }
        }
        return false;
    }

    private HashMap<String, Vector<Integer>> codeReferences;
}
