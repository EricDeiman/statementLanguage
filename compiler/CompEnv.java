
import java.util.Vector;
import java.util.HashMap;

public class CompEnv {
    public class LookupPair {
        public Integer frames;
        public Integer slots;
    }
    
    public CompEnv() {
        store = new Vector<HashMap<String, Integer>>();
        store.add(new HashMap<String, Integer>());
    }

    public void put(String key, Integer offset) {
        for(int i = store.size() - 1; 0 <= i; i--) {
            if(store.get(i).containsKey(key)) {
                store.get(i).put(key, offset);
                return;
            }
        }
        store.get(store.size() - 1).put(key, offset);
    }

    // public LookupPair get(String key) {
    //     Integer top = store.size() - 1;
    //     for(int i = top; 0 < i; i--) {
    //         if(store.get(i).containsKey(key)) {
    //             store.get(i).get(key);
    //             return;
    //         }
    //     }
    //     return store.get(0).get(key);
    // }

    public void beginScope() {
        store.add(new HashMap<String, Integer>());
    }

    public void endScope() {
        store.remove(store.size() - 1);
    }

    private Vector<HashMap<String, Integer>> store;
}
