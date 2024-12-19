package org.example.pr2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
// Latyshev IKBO-16-21
public class Task1 {
    public static void main(String[] args) {
        Path filePath = Paths.get("src\\main\\java\\org\\example\\pr2\\example.txt");

        try {
            List<String> lines = Files.readAllLines(filePath);

            for (String line : lines) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
