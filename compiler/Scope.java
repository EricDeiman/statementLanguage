
import java.util.Vector;

import common.RuntimeError;

public class Scope {
    public class LookupPair {
        Integer frames;
        Integer offset;
    }
    
    public Scope(Scope parent) {
        this.parent = parent;
        store = new Vector<String>();
    }

    public Scope getParent() {
        return parent;
    }

    public Boolean contains(String name) {
        if(parent != null && !parent.contains(name)) {
            return store.contains(name);
        }
        return true;
    }

    public void put(String name) {
        if(parent != null && !parent.contains(name)) {
            store.add(name);
        }
    }

    public LookupPair get(String name) {
        LookupPair rtn;
        if(parent != null && parent.contains(name)) {
            rtn = parent.get(name);
            rtn.frames++;
        }
        else if(store.contains(name)) {
            rtn = new LookupPair();
            rtn.frames = 0;
            rtn.offset = store.indexOf(name);
        }
        else {
            throw new RuntimeError("Cannot find " + name + " in scopes");
        }

        return rtn;
    }

    private Scope parent;
    private Vector<String> store;
}
