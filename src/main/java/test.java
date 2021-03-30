import java.util.Hashtable;
import java.util.PriorityQueue;

public class test {
    public static void main(String[] args) {
//        AStarPathfinder pathfinder = new AStarPathfinder();
//        Gui gui = new Gui();
//        Block block1 = pathfinder.getBlocks().get(0).get(0);
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        Integer a = 1;
        Integer b = 2;
        Integer c = 3;
        Integer d = 4;
        queue.add(a);
        queue.add(b);
        queue.add(c);
        queue.add(d);



        for (int i = 0; i < 4; i++) {
            System.out.println(queue.poll());
        }
    }
}
