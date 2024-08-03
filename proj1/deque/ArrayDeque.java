package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T> {
    public T[] arr;
    public int size;
    public int nextfirst;
    public int nextlast;
    public ArrayDeque(){
        arr = (T[])new Object[8];
        size = 0;
        nextfirst = 0;
        nextlast = 1;
    }
    @Override
    public void addFirst(T item){
        if (size == arr.length) {
            resize(2*size);
        }
        size+=1;
        arr[nextfirst] = item;
        if (nextfirst == 0) {
            nextfirst = arr.length-1;
        } else {
            nextfirst--;
        }
    }
    public T[] arrayofnet(int sizeafter){
        T[] newarr = (T[]) new Object[sizeafter];
        if (nextfirst == 0 && size==arr.length) {
            for (int i = 1; i < arr.length; i++) {
                newarr[i - 1] = arr[i];
            }
            newarr[size - 1] = arr[0];
        } else if (nextlast == 0 && size==arr.length) {
            for (int i = 0; i < arr.length; i++) {
                newarr[i] = arr[i];
            }
        }else if (nextfirst == 0) {
            for (int i = 1; i < size+1; i++) {
                newarr[i - 1] = arr[i];
            }
        }else if (nextfirst != 7) {
            for(int i = nextfirst+1;i < arr.length;i++) {
                newarr[i - nextfirst - 1] = arr[i];
            }
            for(int i = 0;i<nextlast;i++) {
                newarr[i+arr.length-nextfirst-1] = arr[i];
            }
        } else {
            for (int i = 0; i < arr.length; i++) {
                newarr[i] = arr[i];
            }
        }
        return newarr;
    }

    public void resize(int sizeafter){
        T[] newarr = arrayofnet(sizeafter);
        nextfirst = newarr.length - 1;
        nextlast = size;
        arr = newarr;
    }

    @Override
    public void addLast(T item){
        if (size == arr.length) {
            resize(2*size);
        }
        size+=1;
        arr[nextlast] = item;
        if (nextlast == arr.length-1) {
            nextlast = 0;
        } else {
            nextlast++;
        }
    }

    @Override
    public boolean isEmpty(){
        if (size==0) {
            return true;
        }
        return false;
    }

    @Override
    public int size(){
        return size;
    }

    @Override
    public void printDeque() {
        if (nextfirst == 0) {
            for (int i = 1; i < arr.length; i++) {
                System.out.print(arr[i] + " ");
            }
            System.out.println(arr[0]);
        } else if (nextlast == 0) {
            for (int i = 0; i < arr.length; i++) {
                System.out.print(arr[i] + " ");
            }
            System.out.println();
        } else {
            for(int i = nextfirst+1;i < arr.length;i++) {
                System.out.print(arr[i] + " ");
            }
            for(int i = 0;i<nextlast;i++) {
                System.out.print(arr[i] + " ");
            }
            System.out.println();
        }
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        } else {
            T[] newarr = arrayofnet(arr.length);
            T[] newarrnew = (T[]) new Object[arr.length];
            System.arraycopy(newarr,1,newarrnew,0,size-1);
            size = size - 1;
            arr=newarrnew;
            ifcutsize();
            if(size==0){
                nextfirst = 0;
                nextlast = 1;
            } else {
                nextfirst = arr.length-1;
                nextlast = size;
            }
            return newarr[0];
        }
    }

    public void ifcutsize(){
        double useratio = (double)size/arr.length;
        while(size >= 16 && useratio < 0.25) {
            resize(arr.length/2);
        }
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        } else {
            T[] newarr = arrayofnet(arr.length);
            T[] newarrnew = (T[]) new Object[arr.length];
            System.arraycopy(newarr,0,newarrnew,0,size-1);
            size = size - 1;
            arr=newarrnew;
            ifcutsize();
            if(size==0){
                nextfirst = 0;
                nextlast = 1;
            } else {
                nextfirst = arr.length-1;
                nextlast = size;
            }
            return newarr[size];
        }
    }

    @Override
    public T get(int index){
        T[] newarr = arrayofnet(size);
        return newarr[index];
    }
    public Iterator<T> iterator(){
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int cnt;

        public ArrayDequeIterator() {
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
    public boolean equals(Object o){
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)){
            return false;
        }

        Deque<T> obj = (Deque<T>)o;
        if (obj.size() != this.size()){
            return false;
        }
        for(int i = 0; i < obj.size(); i += 1){
            T itemFromObj =  obj.get(i);
            T itemFromThis = this.get(i);
            if (!itemFromObj.equals(itemFromThis)){
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args){
        LinkedListDeque<Integer> list = new LinkedListDeque<Integer>();
        list.addLast(3);
        int i =list.removeFirst();
        System.out.println(i);
        list.addLast(9);
        int k=list.removeFirst();
        System.out.println(k);
    }


}
