import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Utils {

    public static void parseAlgorithmInfo(String[] algorithmInfo) {
        try {
            File file = new File("src/main/java/AlgorithmInfo.txt");
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String currentLine;
            int count = 0;
            do {
                currentLine = bufferedReader.readLine();
                algorithmInfo[count] = currentLine;
                count++;
            } while (currentLine != null && count < algorithmInfo.length);
        } catch (IOException e) {
            System.err.println("Algorithm info parsing failed");
            e.printStackTrace();
        }
    }

    public static double getAspectRatio() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        //System.out.println(dim.getWidth());
        return dim.getWidth()/dim.getHeight();
    }
}
