package deque;
import java.util.Comparator;
public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;
    MaxArrayDeque(Comparator<T> c) {
        super();
        comparator = c;
    }
    public T max(Comparator<T> c) {
        if(isEmpty()) {
            return null;
        }
        int maxIndex = 0;
        for (int i = 1; i < size(); i++) {
            if (c.compare(get(maxIndex), get(i)) < 0) {
                maxIndex = i;
            }
        }
        return get(maxIndex);
    }
    public T max() {
        return max(comparator);
    }
}
