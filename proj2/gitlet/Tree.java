package gitlet;

public class Tree<T> {
    private class Node<T> {
        private T data;
        private Node<T> otherbranch ;
        private Node<T> mainbranch;
        private boolean split ;
        public Node(T data, Node<T> otherbranch, Node<T> mainbranch, boolean split) {
            this.data = data;
            this.otherbranch = otherbranch;
            this.mainbranch = mainbranch;
            this.split = split;
        }
    }
    private Node<T> root;
    public Tree() {
        root = null;
    }

    //add data to main branch
    public void addtomain(T data){
        addhelper(data, root);
    }
    public Node addhelper(T data, Node<T> node){
        if(node == null){
            return new Node(data, null, null, false);
        }
        return addhelper(data, node.mainbranch);
    }

    //whether main branch contains data
    public boolean containsinmain(T data) {
        return containshelper(data,root);
    }
    private boolean containshelper(T data, Node<T> node) {
        if (node == null) return false;
        if (node.data.equals(data)) return true;
        return containshelper(data, node.mainbranch);
    }

    public T getlast() {
        return gethelper(root);
    }

    public T gethelper(Node<T> node) {
        if(node == null) return null;
        if (node.mainbranch == null) {
            return node.data;
        }
        return gethelper(node.mainbranch);

    }


}
