package org.example.pr2;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Task4 {
    private static final Map<Path, List<String>> fileContents = new HashMap<>();
    // Latyshev IKBO-16-21

    public static void main(String[] args) throws IOException, InterruptedException {
        Path dir = Paths.get("src/main/java/org/example/pr2/");
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);

            while (true) {
                WatchKey key = watchService.take();
                List<WatchEvent<?>> events = key.pollEvents();
                for (WatchEvent<?> event : events) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path fileName = (Path) event.context();
                    Path fullPath = dir.resolve(fileName);

                    if (fileName.toString().endsWith("~")) {
                        continue;
                    }

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.out.println("Created: " + fileName);
                        fileContents.put(fullPath, readFileContents(fullPath));
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        System.out.println("Modified: " + fileName);
                        if (Files.exists(fullPath)) {
                            List<String> newLines = readFileContents(fullPath);
                            List<String> oldLines = fileContents.get(fullPath);
                            if (oldLines != null) {
                                // Compare newLines and oldLines to find differences
                                compareFileContents(oldLines, newLines);
                            }
                            fileContents.put(fullPath, newLines);
                        } else {
                            System.out.println("File does not exist: " + fileName);
                        }
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.out.println("Deleted: " + fileName);
                        if (Files.exists(fullPath)) {
                            int checksum = calculateChecksum(fullPath.toString());
                            System.out.println("Size: " + Files.size(fullPath));
                            System.out.println("Checksum: " + Integer.toHexString(checksum));
                        } else {
                            System.out.println("File does not exist: " + fileName);
                        }
                        fileContents.remove(fullPath);
                    }
                }
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        }
    }

    private static List<String> readFileContents(Path filePath) throws IOException {
        return Files.readAllLines(filePath);
    }

    private static int calculateChecksum(String path) throws IOException {
        Path filePath = Path.of(path);
        return Task3.calculateChecksum(filePath);
    }
    private static void compareFileContents(List<String> oldLines, List<String> newLines) {
        Set<String> oldSet = new HashSet<>(oldLines);
        Set<String> newSet = new HashSet<>(newLines);

        Set<String> addedLines = new HashSet<>(newSet);
        addedLines.removeAll(oldSet);
        if (!addedLines.isEmpty()) {
            System.out.println("Added lines:");
            for (String line : addedLines) {
                System.out.println(line);
            }
        }

        Set<String> removedLines = new HashSet<>(oldSet);
        removedLines.removeAll(newSet);
        if (!removedLines.isEmpty()) {
            System.out.println("Removed lines:");
            for (String line : removedLines) {
                System.out.println(line);
            }
        }
    }
}
