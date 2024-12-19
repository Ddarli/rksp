package org.example.pr1;

import java.util.Random;
import java.util.concurrent.*;
// Latyshev IKBO-16-21

class File {
    private final String type;
    private final int size;

    public File(String type, int size) {
        this.type = type;
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "File{" + "type='" + type + '\'' + ", size=" + size + '}';
    }
}

class FileGenerator implements Runnable {
    private final BlockingQueue<File> queue;
    private final Random random = new Random();

    public FileGenerator(BlockingQueue<File> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        String[] fileTypes = {"XML", "JSON", "XLS"};
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String type = fileTypes[random.nextInt(fileTypes.length)];
                int size = random.nextInt(91) + 10;

                File file = new File(type, size);
                System.out.println("Generated: " + file);

                Thread.sleep(random.nextInt(901) + 100);

                queue.put(file);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class FileProcessor implements Runnable {
    private final BlockingQueue<File> queue;
    private final String fileType;

    public FileProcessor(BlockingQueue<File> queue, String fileType) {
        this.queue = queue;
        this.fileType = fileType;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                File file = queue.take();

                if (file.getType().equals(fileType)) {
                    System.out.println("Processing " + file + " by " + fileType + " processor");

                    Thread.sleep(file.getSize() * 7);

                    System.out.println(file + " processed by " + fileType + " processor");
                } else {
                    queue.put(file);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
// Latyshev IKBO-16-21

public class Task3 {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<File> queue = new ArrayBlockingQueue<>(5);

        ExecutorService fileGeneratorService = Executors.newSingleThreadExecutor();

        fileGeneratorService.submit(new FileGenerator(queue));

        ExecutorService fileProcessorsService = Executors.newFixedThreadPool(3);

        fileProcessorsService.submit(new FileProcessor(queue, "XML"));
        fileProcessorsService.submit(new FileProcessor(queue, "JSON"));
        fileProcessorsService.submit(new FileProcessor(queue, "XLS"));

        Thread.sleep(10000);

        fileGeneratorService.shutdownNow();
        fileProcessorsService.shutdownNow();

        fileGeneratorService.awaitTermination(1, TimeUnit.SECONDS);
        fileProcessorsService.awaitTermination(1, TimeUnit.SECONDS);

        System.out.println("System shutdown.");
    }
}
