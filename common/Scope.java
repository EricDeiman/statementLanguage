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

    public Vector<String> getNames() {
        return store;
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
