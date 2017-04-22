package common;

import java.util.HashMap;
import java.util.Vector;

/**
 * Track where in the code labels are referenced.  Once label addresses are known,
 * patch those references with the actual address the label refers to.
 */
public class FixUp {

    public FixUp() {
        codeReferences = new HashMap<String, Vector<Integer>>();
    }

    /**
     * 'where' indicates a place in the code that needs the address of 'label' patched
     * in.
     */
    public void addFixup(String label, Integer where) {
        if(codeReferences.containsKey(label)) {
            codeReferences.get(label).add(where);
        }
        else {
            Vector<Integer> v = new Vector<Integer>();
            v.add(where);
            codeReferences.put(label, v);
        }
    }

    public Boolean doFixups(HashMap<String, Integer> labels, CodeBuffer code) {
        for(String key : codeReferences.keySet()) {
            if(!labels.containsKey(key)) {
                throw new Error("in FixUp.doFixups: cannot find label " + key + " in" +
                                " the provided labels. Giving up.");
            }
            Vector<Integer> v = codeReferences.get(key);
            Integer address = labels.get(key);
            for(Integer index : v) {
                code.putInteger(address, index);
            }
        }
        return false;
    }

    public static void main(String[] args) {
        CodeBuffer cb = new CodeBuffer();
        HashMap<String, Integer>labels = new HashMap<String, Integer>();
        FixUp fixUps = new FixUp();
        cb.writeInteger(1).writeInteger(2);
        labels.put("label1", 3);
        fixUps.addFixup("label1", cb.getFinger());
        cb.writeInteger(-1).writeInteger(4).writeInteger(5);
        System.out.println(cb.dumpToString());

        fixUps.doFixups(labels, cb);

        System.out.println(cb.dumpToString());
    }

    private HashMap<String, Vector<Integer>> codeReferences;
}
