import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.PriorityQueue;

public class test {
    public static void main(String[] args) {
//        AStarPathfinder pathfinder = new AStarPathfinder();
//        Gui gui = new Gui();
//        Block block1 = pathfinder.getBlocks().get(0).get(0);
        System.out.println(new File(".").getAbsolutePath());
        String[] AlgorithmInfo = new String[5];
        try {
            File file = new File("src/main/java/testFile");
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String currentLine;
            int count = 0;
            do {
                currentLine = bufferedReader.readLine();
                AlgorithmInfo[count] = currentLine;
                count++;
            } while (currentLine != null && count < 5);
        } catch (IOException e) {
            System.err.println("Algorithm info parsing failed");
            e.printStackTrace();
        }

        System.out.println(AlgorithmInfo[1]);
    }
}
