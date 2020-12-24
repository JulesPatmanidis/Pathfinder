import java.util.Hashtable;
import java.util.PriorityQueue;

public class test {
    public static void main(String[] args) {
        Gui gui = new Gui();
        AStarPathfinder pathfinder = new AStarPathfinder(gui);
//        Block block1 = new Block(5,5);
//
//        PriorityQueue<Block> openQueue = new PriorityQueue<>();
//        for (int i = 0 ; i < 11 ; i++) {
//            Block temp = new Block(i,2);
//            openQueue.add(temp);
//        }

        Hashtable<String,Integer> hashTable = new Hashtable<>();
        hashTable.put("a",  1);
        hashTable.put("b", 5);
        System.out.println(hashTable.get("a"));
        hashTable.put("a", 9);
        System.out.println(hashTable.get("a"));

    }
}
