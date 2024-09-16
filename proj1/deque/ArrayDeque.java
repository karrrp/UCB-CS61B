package deque;

public class ArrayDeque<Gloup> implements Deque<Gloup> {
    public Gloup[] items = (Gloup[]) new Object[8];
    int length = 8;
    private int size;
    int theFirst = 0;
    int theLast = length - 1;
    double use_rate = 0.25;

    public ArrayDeque(){
        size = 0;
    }
    @Override
    public int size(){
        return size;
    }
    @Override
    public Gloup get(int i){
        if(i > size()){
            return null;
        }
        if(theFirst + i > length - 1) {
            return items[theLast - size() + i + 1];
        }
        return items[theFirst + i];
    }
    @Override
    public void addFirst(Gloup i){
        if(size == 0){
            theLast = 0;
            theFirst = 0;
            items[theFirst] = i;
            size ++;
            return;
        }
        if(size == length){
            resize(this);
            addFirst(i);
            return;
        }
        else if (theFirst == 0) {
            theFirst = length - 1;
        }
        else {
            theFirst = theFirst - 1;}
        items[theFirst] = i;
        size ++;
        resize(this);
    }
    @Override
    public void addLast(Gloup i){
        if(size == 0){
            theLast = 0;
            theFirst = 0;
            items[theLast] = i;
            size ++;
            return;
        }
        else if(size == length){
            resize(this);
            addLast(i);
            return;
        }
        else if(theLast == length - 1){
            theLast = 0;
        }
        else {
            theLast = theLast + 1;
        }
        items[theLast] = i;
        size ++;
    }

    private void resize(ArrayDeque<Gloup> p){
        if(size == 0){
            return;
        }
        if ((size * 1.0 / length) < use_rate){
            //可优化的,魔数
            length = length / 4 + 1 ;
            Gloup[] new_items = (Gloup[]) new Object[length];
            for (int i = 0; i < size; i++) {
                new_items[i] = get(i);
            }
            items = new_items;
        }
        else if(size == length) {
            length = 2 * length;
            Gloup[] new_items = (Gloup[]) new Object[length];
            for (int i = 0; i < size; i++) {
                new_items[i] = get(i);
            }
            items = new_items;
        }

    }
    @Override
    public void printDeque(){
        for (int i = 0; i < size(); i++) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }
    @Override
    //特殊情况有点多
    public Gloup removeLast(){
        if(size==0){
            return null;
        }
        else if(size == 1){
            size = 0;
            return items[theLast];

        }
        Gloup item = items[theLast];
        theLast --;
        size --;
        return item;
    }
    @Override
    public Gloup removeFirst(){
        if(size==0){
            return null;
        }
        else if(size == 1){
            size = 0;
            return items[theFirst];
        }
        Gloup item = items[theFirst];
        theFirst ++;
        size --;
        return item;
    }



}
