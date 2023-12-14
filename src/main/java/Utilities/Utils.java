package Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;


public class Utils {
    public static void parseAlgorithmInfo(String[] algorithmInfo) {
        try {
            File file = new File("src/main/java/resources/AlgorithmInfo.txt");
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String currentLine;
            String currentLine2;
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
