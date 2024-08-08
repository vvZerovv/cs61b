package deque;
import java.util.Iterator;

//based on linked list

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private class Node {
        private Node prev;
        private T item;
        private Node next;
        private Node(T x, Node pre, Node nex) {
            item = x;
            prev = pre;
            next = nex;
        }
    }


    private Node sentinel;
    private int size;

    //Creates an empty linked list deque.
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public T getRecursive(int index) {
        if (isEmpty() || index < 0) {
            return null;
        }
        Node n = sentinel.next;
        return gethelper(n, index);
    }

    private T gethelper(Node n, int index) {
        if (index == 0) {
            return n.item;
        }
        if (n.next == sentinel) {
            return null;
        }
        return gethelper(n.next, index - 1);
    }

    @Override
    public void addFirst(T item) {
        sentinel.next = new Node(item, sentinel, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        sentinel.prev = new Node(item, sentinel.prev, sentinel);
        sentinel.prev.prev.next = sentinel.prev;
        size += 1;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node temp = sentinel.next;
        while (temp != sentinel) {
            System.out.print(temp.item + " ");
            temp = temp.next;
        }
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        } else {
            T value = sentinel.next.item;
            if (size == 1) {
                sentinel.prev = sentinel;
            }
            sentinel.next = sentinel.next.next;
            sentinel.next.prev = sentinel;
            size = size - 1;
            return value;
        }
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        } else {
            T value = sentinel.prev.item;
            if (size == 1) {
                sentinel.next = sentinel;
            }
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;
            size = size - 1;
            return value;
        }
    }

    @Override
    public T get(int index) {
        int t = size - 1;
        if (t < index) {
            return null;
        } else {
            Node temp = sentinel.next;
            while (index > 0) {
                temp = temp.next;
                index -= 1;
            }
            return temp.item;
        }
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }
    private class LinkedListDequeIterator implements Iterator<T> {
        private int cnt;
        LinkedListDequeIterator() {
            cnt = 0;
        }
        public boolean hasNext() {
            return cnt < size();
        }
        public T next() {
            T returnItem = get(cnt);
            cnt += 1;
            return returnItem;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> obj = (Deque<T>) o;
        if (obj.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < obj.size(); i += 1) {
            T itemFromObj =  obj.get(i);
            T itemFromThis = this.get(i);
            if (!itemFromObj.equals(itemFromThis)) {
                return false;
            }
        }
        return true;
    }
}


