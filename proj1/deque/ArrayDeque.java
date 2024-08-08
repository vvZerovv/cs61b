package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] arr;
    private int size;
    private int nextfirst;
    private int nextlast;
    public ArrayDeque() {
        arr = (T[]) new Object[8];
        size = 0;
        nextfirst = 0;
        nextlast = 1;
    }

    private T[] arrayofnet(int sizeafter, boolean addornot) {
        T[] newarr = (T[]) new Object[sizeafter];
        if (addornot) {
            System.arraycopy(arr, nextlast, newarr, 0, size - nextlast);
            System.arraycopy(arr, 0, newarr, size - nextlast, nextlast);
        } else {
            for (int i = (nextfirst + 1) % arr.length, j = 0; i < arr.length; i = (i + 1) % arr.length) {
                if (arr[i] == null) {
                    break;
                }
                newarr[j] = arr[i];
                j++;
            }
        }
        return newarr;
    }

    private void resize(int sizeafter, boolean addornot) {
        T[] newarr = arrayofnet(sizeafter, addornot);
        nextfirst = newarr.length - 1;
        nextlast = size;
        arr = newarr;
    }

    @Override
    public void addFirst(T item) {
        if (size == arr.length) {
            resize(size * 2, true);
        }
        size += 1;
        arr[nextfirst] = item;
        if (nextfirst == 0) {
            nextfirst = arr.length - 1;
        } else {
            nextfirst--;
        }
    }

    @Override
    public void addLast(T item) {
        if (size == arr.length) {
            resize(size * 2, true);
        }
        size += 1;
        arr[nextlast] = item;
        if (nextlast == arr.length - 1) {
            nextlast = 0;
        } else {
            nextlast++;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        T[] newarr = (T[]) new Object[arr.length];
        if (size == arr.length) {
            newarr = arrayofnet(arr.length, true);
        } else {
            newarr = arrayofnet(arr.length, false);
        }
        for (T i : newarr) {
            System.out.print(i + " ");
        }
    }

    private void ifcutsize() {
        double useratio = (double) size / arr.length;
        while (size >= 8 && useratio < 0.25) {
            useratio = useratio * 4;
            resize(arr.length / 4, false);
        }
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T revenumber = arr[(nextfirst + 1) % arr.length];
        arr[(nextfirst + 1) % arr.length] = null;
        nextfirst = (nextfirst + 1) % arr.length;
        size--;
        ifcutsize();
        return revenumber;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if (nextlast == 0) {
            T revenumber = arr[(nextlast - 1) % arr.length + arr.length];
            arr[(nextlast - 1) % arr.length + arr.length] = null;
            nextlast = (nextlast - 1) % arr.length + arr.length;
            size--;
            ifcutsize();
            return revenumber;
        }
        T revenumber = arr[(nextlast - 1) % arr.length];
        arr[(nextlast - 1) % arr.length] = null;
        nextlast = (nextlast - 1) % arr.length;
        size--;
        ifcutsize();
        return revenumber;
    }

    @Override
    public T get(int index) {
        T[] newarr = (T[]) new Object[arr.length];
        if (size == arr.length) {
            newarr = arrayofnet(arr.length, true);
        } else {
            newarr = arrayofnet(arr.length, false);
        }
        return newarr[index];
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int cnt;

        ArrayDequeIterator() {
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

    //Returns whether or nor the parameter o is equal to the Deque.
    // o is considered equal if it is a Deque and if it contains the same contents
    // (as goverened by the generic T’s equals method) in the same order.
    // note: use "equals" instead of "==", when comparing of content from object
    @Override
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
