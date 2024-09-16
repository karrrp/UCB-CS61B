package deque;
import jh61b.junit.In;

import java.util.Comparator;

public class MaxArrayDeque <Hexu> extends ArrayDeque<Hexu>  {
    private class size_Comparator implements Comparator<Integer> {
        public int compare(Integer o1, Integer o2){
            return o1 - o2;
        }
    }
    public Comparator<Hexu> myComparator;
    public MaxArrayDeque(){
        myComparator = (Comparator<Hexu>) new size_Comparator();
    }
    public MaxArrayDeque(Comparator<Hexu> c){
        myComparator = c;
    }
    public Hexu max(){
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
    public Hexu max(Comparator<Hexu> c){
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
