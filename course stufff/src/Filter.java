import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class Filter<T> implements Iterable<T> {
    private List<T> FL;
    private Predicate<T> filterPredicate;
    public Filter(List<T> L, Predicate<T> filter) {
        this.FL = L;
        this.filterPredicate = filter;
    }

    @Override
    public Iterator<T> iterator() {
        return new FilterIterator();
    }

    private class FilterIterator implements Iterator<T> {
        int sential;
        public FilterIterator() {
            sential = 0;
        }
        @Override
        public boolean hasNext() {
            T curr= FL.get(sential);
            while (!filterPredicate.test(curr)) {
                sential++;
                curr= FL.get(sential);
            }
            return sential < FL.size();

        }

        @Override
        public T next() {
            if (hasNext()) {
                return FL.get(sential);
            } else {
                return null;
            }
        }
    }
}
