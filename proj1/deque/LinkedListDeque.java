package deque;

import jh61b.junit.In;
import org.junit.Test;

import javax.lang.model.type.NoType;
import java.util.Iterator;

public class LinkedListDeque<BleepBlorp> implements Deque<BleepBlorp> , Iterable<BleepBlorp> {
    //循环节点
    public static class IntNode<BleepBlorp> {
        public IntNode<BleepBlorp> prev;
        public BleepBlorp item;
        public IntNode<BleepBlorp> next;
        public IntNode(BleepBlorp x, IntNode<BleepBlorp> formal,IntNode<BleepBlorp> rest){
            item = x;
            prev = formal;
            next = rest;
        }
    }
    //队列自身
    private int size;
    private IntNode<BleepBlorp> sentinel;

    @Override
    public Iterator<BleepBlorp> iterator() {
        return new ArrayIterator();
    }
    private class ArrayIterator<BleepBlorp> implements Iterator<BleepBlorp>{
        int seer = 0;
        @Override
        public boolean hasNext() {
            return seer < size();
        }

        @Override
        public BleepBlorp next() {
            if(hasNext()) {
                BleepBlorp WPZO = (BleepBlorp) get(seer);
                seer ++;
                return WPZO;
            }
        return null;
        }
    }

    //创建一个空队列
    public LinkedListDeque(){
        sentinel =new IntNode(null,null,null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }
    @Override
    // add
    public void addFirst(BleepBlorp x){
        IntNode<BleepBlorp> NewNode = new IntNode<BleepBlorp>(x,sentinel,sentinel.next);
        sentinel.next.prev = NewNode;
        sentinel.next =NewNode;
        size ++;
    }
    @Override
    public void addLast(BleepBlorp x){
        IntNode<BleepBlorp> NewNode = new IntNode<BleepBlorp>(x,sentinel.prev,sentinel);
        sentinel.prev.next = NewNode;
        sentinel.prev = NewNode;
        size ++;
    }

    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque(){
        IntNode<BleepBlorp> p = sentinel;
        while (p.next != sentinel){
            System.out.print(p.next.item + "");
            p = p.next;
        }
        System.out.println();
    }
    @Override
    public BleepBlorp removeFirst(){
        if(sentinel.next != sentinel) {
            size -= 1;
            IntNode<BleepBlorp> theNote = sentinel.next;
            sentinel.next = sentinel.next.next;
            sentinel.next.next.prev = sentinel;
            return theNote.item;
        }
        else {
            return null;
        }
    }
    @Override
    public BleepBlorp removeLast(){
        if(sentinel.prev != sentinel) {
            size -= 1;
            IntNode<BleepBlorp> theNote = sentinel.prev;
            sentinel.prev.prev.next = sentinel;
            sentinel.prev = sentinel.prev.prev;
            return theNote.item;
        }
        else {
            return null;
        }
    }

    public BleepBlorp getRecursive(int index){
        if(index == 0){
            return get(0);
        }
        else{
            LinkedListDeque<BleepBlorp> a = new LinkedListDeque<>();
            a.sentinel = this.sentinel.next;
            return a.getRecursive(index - 1);
        }
    }
    @Override
    public BleepBlorp get(int index){
        IntNode<BleepBlorp> p = sentinel.next;
        if (index > size() || index < 0){
            return null;
        }
        else{
            while (index > 0){
                p = p.next;
                index --;
            }
            return p.item;
        }
    }
    //TO BE FINISHED
    // public Iterator<T> iterator()
    public boolean equals(Object o){
        if (!(o instanceof LinkedListDeque<?>)){
            return false;
        }
        else{
             return true;
        }
    }
}
