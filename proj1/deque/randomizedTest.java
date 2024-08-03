package deque;
import deque.ArrayDeque;
import deque.LinkedListDeque;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class randomizedTest {
    public static void main(String[] args) {
        LinkedListDeque<Integer> correct= new LinkedListDeque<>();
        ArrayDeque<Integer> broken = new ArrayDeque<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 5);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                broken.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                int randVal = StdRandom.uniform(0, 100);
                correct.addFirst(randVal);
                broken.addFirst(randVal);
                System.out.println("addFirst(" + randVal + ")");
            }else if (operationNumber == 2) {
                if(correct.size()==0){
                    continue;
                }
                int first= correct.removeFirst();
                System.out.println("first: " + first);
                int firstofArray= broken.removeFirst();
                System.out.println("Firstob: " + firstofArray);
            }else if (operationNumber == 3) {
                if(correct.size()==0){
                    continue;
                }
                correct.removeLast();
                broken.removeLast();
                System.out.println("AfterremoveLast(): " + correct.size()+" = "+broken.size());
            }else if (operationNumber == 4) {
                int randVal = StdRandom.uniform(0, 100);
                correct.addFirst(randVal);
                broken.addFirst(randVal);
                System.out.println("addFirst(" + randVal + ")");
            }
        }
    }
}