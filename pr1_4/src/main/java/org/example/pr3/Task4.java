package org.example.pr3;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Task4 {
    public static class File {
        enum FileType {
            XML, JSON, XLS
        }

        private FileType fileType;
        private int size; // Размер файла от 10 до 100

        public File(FileType fileType, int size) {
            this.fileType = fileType;
            this.size = size;
        }

        public FileType getFileType() {
            return fileType;
        }

        public int getSize() {
            return size;
        }

        @Override
        public String toString() {
            return "File{" +
                    "fileType=" + fileType +
                    ", size=" + size +
                    '}';
        }

        // Генерация случайного файла
        public static File generateRandomFile() {
            Random random = new Random();
            FileType[] fileTypes = FileType.values();
            FileType fileType = fileTypes[random.nextInt(fileTypes.length)]; // Случайный тип файла
            int size = random.nextInt(91) + 10; // Размер от 10 до 100
            return new File(fileType, size);
        }
    }
    public class FileGenerator {
        private static final Random random = new Random();

        // Метод, создающий поток файлов с задержкой
        public static Observable<File> generateFiles() {
            return Observable.interval(random.nextInt(900) + 100, TimeUnit.MILLISECONDS)
                    .map(tick -> File.generateRandomFile())
                    .doOnNext(file -> System.out.println("Generated: " + file));
        }
    }

    public class FileQueue {
        // Метод, который будет получать файлы из генератора и группировать их в буферы по 5 файлов
        public static Observable<List<File>> getQueue(Observable<File> fileObservable) {
            return fileObservable.buffer(5)
                    .doOnNext(queue -> System.out.println("Queue received: " + queue.size() + " files"));
        }
    }


    public static class FileHandler {
        private File.FileType fileType;

        public FileHandler(File.FileType fileType) {
            this.fileType = fileType;
        }

        // Метод обработки файла
        public Observable<File> processFiles(Observable<File> fileObservable) {
            return fileObservable
                    .filter(file -> file.getFileType() == this.fileType) // Обрабатываем только нужный тип файла
                    .doOnNext(file -> System.out.println("Processing file: " + file))
                    .delay(file -> Observable.timer(file.getSize() * 7, TimeUnit.MILLISECONDS)) // Задержка по времени обработки
                    .doOnNext(file -> System.out.println("File processed: " + file))
                    .subscribeOn(Schedulers.io()); // Обрабатываем файлы в отдельном потоке
        }
    }

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);


        Observable<File> fileStream = FileGenerator.generateFiles();


        Observable<List<File>> fileQueue = FileQueue.getQueue(fileStream);


        FileHandler xmlHandler = new FileHandler(File.FileType.XML);
        FileHandler jsonHandler = new FileHandler(File.FileType.JSON);
        FileHandler xlsHandler = new FileHandler(File.FileType.XLS);


        fileQueue
                .flatMap(Observable::fromIterable)
                .observeOn(Schedulers.io())
                .flatMap(file -> Observable.merge(
                        xmlHandler.processFiles(Observable.just(file)),
                        jsonHandler.processFiles(Observable.just(file)),
                        xlsHandler.processFiles(Observable.just(file))
                ))
                .subscribe(
                        file -> {},
                        Throwable::printStackTrace,
                        latch::countDown
                );


        latch.await(10, TimeUnit.SECONDS);
    }
}
