package Utilities;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Utils {
    public static List<String> parseAlgorithmInfo() throws IOException {
        try (InputStream in = Utils.class.getClassLoader()
                .getResourceAsStream("AlgorithmInfo.txt")) {

            if (in == null) {
                throw new FileNotFoundException("AlgorithmInfo.txt not found in classpath");
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))) {

                List<String> lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                return lines;
            }
        }
    }

    public static double getAspectRatio() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        return dim.getWidth() / dim.getHeight();
    }

}
