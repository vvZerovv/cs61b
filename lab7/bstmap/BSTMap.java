package bstmap;

import java.util.Set;
import java.util.Iterator;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    public class Node{
        private K key;
        private V value;
        private Node left, right;

        public Node(K key,V value){
            this.key = key;
            this.value = value;
            left = right = null;
        }
    }
    private Node root;
    private int size;

    public BSTMap(){
        root = null;
        size = 0;
    }

    public BSTMap(K key,V value) {
        root = new Node(key,value);
        size = 1;
    }

    /** Removes all of the mappings from this map. */
    public void clear(){
        root = null;
        size = 0;
    }

    private Node find(K key , Node node){
        if (node == null){
            return null;
        } else if (key.compareTo(node.key) > 0 ){
            return find(key, node.right);
        } else if (key.compareTo(node.key) < 0) {
            return find(key, node.left);
        }
        return node;
    }

    private boolean contains(K key , Node node){
        if (node == null){
            return false;
        }
        if (key.compareTo(node.key) > 0 ){
            return contains(key, node.right);
        }
        if (key.compareTo(node.key) < 0) {
            return contains(key, node.left);
        }
        return true;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key){
        return contains(key,root);
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        Node node = find(key, root);
        if (node == null){
            return null;
        }
        return node.value;
    }


    /* Returns the number of key-value mappings in this map. */
    public int size(){
        return size;
    }

    private Node insert (K key , V value, Node node){
        if (node == null){
            return new Node(key,value);
        } else if (key.compareTo(node.key) > 0 ){
            node.right = insert(key, value, node.right);
        } else if (key.compareTo(node.key) < 0) {
            node.left = insert(key, value, node.left);
        }
        return node;
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value){
        root = insert(key,value,root);
        size +=1;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
