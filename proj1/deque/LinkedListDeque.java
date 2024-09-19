package deque;
import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T> , Iterable<T> {
    //循环节点
    private static class IntNode<T> {
        private IntNode<T> prev;
        private T item;
        private IntNode<T> next;
        private IntNode(T x, IntNode<T> formal,IntNode<T> rest){
            item = x;
            prev = formal;
            next = rest;
        }
    }
    //队列自身
    private int size;
    private IntNode<T> sentinel;

    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator<>();
    }
    private class ArrayIterator<T> implements Iterator<T>{
        int seer = 0;
        @Override
        public boolean hasNext() {
            return seer < size();
        }
/**
 *@return the iterator' next value
 * */
        @Override
        public T next() {
            if (hasNext()) {
                T sww = (T) get(seer);
                seer++;
                return sww;
            }
        return null;
        }
    }

    //创建一个空队列
    public LinkedListDeque() {
        sentinel =new IntNode(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    /**
     * @param x the first value of the list
     */
    @Override
    public void addFirst(T x) {
        IntNode<T> NewNode = new IntNode<>(x, sentinel, sentinel.next);
        sentinel.next.prev = NewNode;
        sentinel.next =NewNode;
        size++;
    }
    /**
     * @param x the last value of the list
     */
    @Override
    public void addLast(T x) {
        IntNode<T> NewNode = new IntNode<T>(x, sentinel.prev, sentinel);
        sentinel.prev.next = NewNode;
        sentinel.prev = NewNode;
        size++;
    }
    /**
     * @return  the size of the list
     */
    @Override
    public int size() {
        return size;
    }
    /** print the Deque
     */
    @Override
    public void printDeque() {
        IntNode<T> p = sentinel;
        while (p.next != sentinel) {
            System.out.print(p.next.item + "");
            p = p.next;
        }
        System.out.println();
    }
    /**
     * @return the first value
     */
    @Override
    public T removeFirst() {
        if (sentinel.next != sentinel) {
            size -= 1;
            IntNode<T> theNote = sentinel.next;
            sentinel.next.next.prev = sentinel;
            sentinel.next = sentinel.next.next;
            return theNote.item;
        } else {
            throw new NullPointerException("There are nothing to remove!");
        }
    }
    /**
     * @return the last value
     */
    @Override
    public T removeLast() {
        if (sentinel.prev != sentinel) {
            size -= 1;
            IntNode<T> theNote = sentinel.prev;
            sentinel.prev.prev.next = sentinel;
            sentinel.prev = sentinel.prev.prev;
            return theNote.item;
        } else {
            throw new NullPointerException("There are nothing to remove!");
        }
    }

    public T getRecursive(int index) {
        if (index == 0) {
            return get(0);
        } else {
            LinkedListDeque<T> a = new LinkedListDeque<>();
            a.sentinel = this.sentinel.next;
            return a.getRecursive(index - 1);
        }
    }
    @Override
    public T get(int index){
        IntNode<T> p = sentinel.next;
        if (index > size() || index < 0){
            return null;
        } else {
            while (index > 0){
                p = p.next;
                index--;
            }
            return p.item;
        }
    }
    //TO BE FINISHED
    // public Iterator<T> iterator()
    public boolean equals(Object o) {
        if (o instanceof Deque<?>) {
            Deque<T> o1 = (Deque<T>) o;
            if (o1.size() == ((Deque<?>) o).size()) {
                for (int i = 0; i < o1.size(); i++) {
                    if (o1.get(i) != ((Deque<?>) o).get(i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    }

