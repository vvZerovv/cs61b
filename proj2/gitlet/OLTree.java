package gitlet;

import java.io.Serializable;
import java.util.*;

public class OLTree<T,K> implements Serializable {
    private T key;
    private ArrayList<K> value;
    public OLTree(T key) {
        this.key = key;
        value = new ArrayList<K>();
    }
    public void add(K k) {
        value.add(k);
    }

}
