package deque;
import java.util.Iterator;
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items = (T[]) new Object[8];
    private int length;
    private int size;
    private int theFirst;
    private int theLast;

    public ArrayDeque() {
        size = 0;
        length = items.length;
        theFirst = 0;
        theLast = 0;
    }
    @Override
    public int size() {
        return size;

    }
    @Override
    public T get(int i) {
        if (i > size()) {
            return null;
        }
        if (theFirst + i > length - 1) {
            return items[theLast - size() + i + 1];
        }
        return items[theFirst + i];
    }
    @Override
    public void addFirst(T i) {
        if (size == 0) {
            theLast = 0;
            theFirst = 0;
            items[theFirst] = i;
            size++;
            return;
        } else if (size == length) {
            resize();
            addFirst(i);
            return;
        } else if (theFirst == 0) {
            theFirst = length - 1;
        } else {
            theFirst = theFirst - 1;
        }
        items[theFirst] = i;
        size++;
    }
    @Override
    public void addLast(T i) {
        if (size == 0) {
            theLast = 0;
            theFirst = 0;
            items[theLast] = i;
            size++;
            return;
        } else if (size == length) {
            resize();
            addLast(i);
            return;
        } else if (theLast == length - 1) {
            theLast = 0;
        } else {
            theLast = theLast + 1;
        }
        items[theLast] = i;
        size++;
    }

    private void resize() {
        if (size == 0) {
            return;
        }
        double useRate = 0.25;
        if (length > 32 && (size * 1.0 / length) < useRate) {
            //可优化的,魔数
            int newLength = length / 4 + 1;
            T[] newItems = (T[]) new Object[newLength];
            for (int i = 0; i < size; i++) {
                newItems[i] = get(i);
            }
            items = newItems;
            length = newLength;
            theFirst = 0;
            theLast = size() - 1;
        } else if (size == length) {
            int newLength = 2 * length;
            T[] newItems = (T[]) new Object[newLength];
            for (int i = 0; i < size; i++) {
                newItems[i] = get(i);
            }
            items = newItems;
            length = newLength;
            theFirst = 0;
            theLast = size() - 1;
        }

    }
    @Override
    public void printDeque() {
        for (int i = 0; i < size(); i++) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }
    @Override
    //特殊情况有点多
    public T removeLast() {
        if (size == 0) {
            return null;
        } else if (size == 1) {
            size = 0;
            return items[theLast];

        }
        T item = items[theLast];
        if (theLast == 0) {
            theLast = length - 1;
        } else {
            theLast--;
        }
        size--;
        resize();
        return item;
    }
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        } else if (size == 1) {
            size = 0;
            return items[theFirst];
        }
        T item = items[theFirst];
        if (theFirst == length - 1) {
            theFirst = 0;
        } else {
            theFirst++;
        }
        size--;
        resize();
        return item;
    }

    private class LinklistIterator implements Iterator<T> {

        private int seer = 0;
        @Override
        public boolean hasNext() {
            return seer < size();
        }

        @Override
        public T next() {
            if (hasNext()) {
                T szz = (T) get(seer);
                seer++;
                return szz;
            }
            return null;
        }
    }
    @Override
    public Iterator<T> iterator() {
        return new LinklistIterator();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Deque<?>) {
            Deque<T> o1 = (Deque<T>) o;
            if (this.size() == o1.size()) {
                for (int i = 0; i < o1.size(); i++) {
                    if (o1.get(i).equals(this.get(i))) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}


