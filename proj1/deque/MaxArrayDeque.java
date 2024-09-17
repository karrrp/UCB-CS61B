package deque;
import jh61b.junit.In;

import java.util.Comparator;

public class MaxArrayDeque <T> extends ArrayDeque<T>  {
    private class size_Comparator implements Comparator<Integer> {
        public int compare(Integer o1, Integer o2){
            return o1 - o2;
        }
    }
    private Comparator<T> myComparator;
    public MaxArrayDeque(Comparator<T> c){
        myComparator = c;
    }
    public T max(){
        if(size() == 0){
            return null;
        }
        int max = 0;
        for (int i = 1; i < size(); i++) {
            if(myComparator.compare(get(max),get(i)) < 0){
                max = i;
            }
        }
        return get(max);
    }
    public T max(Comparator<T> c){
        if(size() == 0){
            return null;
        }
        int max = 0;
        for (int i = 1; i < size(); i++) {
            if(c.compare(get(max),get(i)) < 0){
                max = i;
            }
        }
        return get(max);
    }
}
