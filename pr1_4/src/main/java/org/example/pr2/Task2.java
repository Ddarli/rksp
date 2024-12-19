package org.example.pr2;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
// Latyshev IKBO-16-21

public class Task2 {
    public static void copyWithStreams(String source, String destination) throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(destination)) {

            byte[] buffer = new byte[8192];
            int length;

            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }

    public static void copyWithChannel(String source, String destination) throws IOException {
        try (FileChannel sourceChannel = FileChannel.open(Path.of(source), StandardOpenOption.READ);
             FileChannel destChannel = FileChannel.open(Path.of(destination), StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

            sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
        }
    }

    public static void copyWithApacheCommons(String source, String destination) throws IOException {
        File sourceFile = new File(source);
        File destFile = new File(destination);

        FileUtils.copyFile(sourceFile, destFile);
    }

    public static void copyWithFiles(String source, String destination) throws IOException {
        Path sourcePath = Path.of(source);
        Path destPath = Path.of(destination);

        Files.copy(sourcePath, destPath);
    }

    public static void foo1(String sourcePath, String destinationPath) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.nanoTime();
        copyWithStreams(sourcePath, destinationPath);
        long endTime = System.nanoTime();
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = (usedMemoryAfter - usedMemoryBefore) / 1024;
        long duration = (endTime - startTime) / 1_000_000;
        System.out.println("Time: " + duration + " ms");
        System.out.println("Memory: " + memoryUsed + " KB");
    }
    public static void foo2(String sourcePath, String destinationPath) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.nanoTime();
        copyWithChannel(sourcePath, destinationPath);
        long endTime = System.nanoTime();
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = (usedMemoryAfter - usedMemoryBefore) / 1024;
        long duration = (endTime - startTime) / 1_000_000;
        System.out.println("Time: " + duration + " ms");
        System.out.println("Memory: " + memoryUsed + " KB");
    }
    public static void foo3(String sourcePath, String destinationPath) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.nanoTime();
        copyWithApacheCommons(sourcePath, destinationPath);
        long endTime = System.nanoTime();
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = (usedMemoryAfter - usedMemoryBefore) / 1024;
        long duration = (endTime - startTime) / 1_000_000;
        System.out.println("Time: " + duration + " ms");
        System.out.println("Memory: " + memoryUsed + " KB");
    }
    public static void foo4(String sourcePath, String destinationPath) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.nanoTime();
        copyWithFiles(sourcePath, destinationPath);
        long endTime = System.nanoTime();
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = (usedMemoryAfter - usedMemoryBefore) / 1024;
        long duration = (endTime - startTime) / 1_000_000;
        System.out.println("Time: " + duration + " ms");
        System.out.println("Memory: " + memoryUsed + " KB");
    }
    public static void main(String[] args) throws IOException {
        String sourcePath = "src/main/java/org/example/pr2/example.txt";
        String destinationPath = "src/main/java/org/example/pr2/example_copy.txt";
        String destinationPath2 = "src/main/java/org/example/pr2/example_copy1.txt";
        String destinationPath3 = "src/main/java/org/example/pr2/example_copy2.txt";
        String destinationPath4 = "src/main/java/org/example/pr2/example_copy3.txt";
        foo1(sourcePath, destinationPath);
        foo2(sourcePath, destinationPath2);
        foo3(sourcePath, destinationPath3);
        foo4(sourcePath, destinationPath4);
    }
}
