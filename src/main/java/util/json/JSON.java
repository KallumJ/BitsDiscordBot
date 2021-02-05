package util.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * A class to assist with the use of JSON files
 */
public class JSON {

    /**
     * A method to return the string of JSON data from a provided File
     *
     * @param file The File to find the String from
     * @return String, the found String
     */
    public static String findJsonStringFromFile(File file) {
        try {
            Scanner scanner = new Scanner(file);

            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNext()) {
                stringBuilder.append(scanner.next());
            }

            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to get json string from file");
        }
    }
}
