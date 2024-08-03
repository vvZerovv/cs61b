package randomizedtest;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class randomizedTest {
    public static void main(String[] args) {
        AListNoResizing<Integer> correct= new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                broken.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = correct.size();
                System.out.println("sizeofcorrect: " + size);
                int sizeob= broken.size();
                System.out.println("sizeofbroken: " + sizeob);
            }else if (operationNumber == 2) {
                if(correct.size()==0){
                    continue;
                }
                int last= correct.getLast();
                System.out.println("last: " + last);
                int lastob= broken.getLast();
                System.out.println("lastob: " + lastob);
            }else if (operationNumber == 3) {
                if(correct.size()==0){
                    continue;
                }
                correct.removeLast();
                broken.removeLast();
                System.out.println("AfterremoveLast(): " + correct.size()+" = "+broken.size());
            }
        }
    }
}
