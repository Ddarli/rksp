package org.example.pr1;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
// Latyshev IKBO-16-21
public class Task2 {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Scanner sc = new Scanner(System.in);

        try{
            while (true){
                System.out.println("Enter number: ");
                int n = sc.nextInt();
                Future<Integer> future = executorService.submit(() -> processRequest(n));
                System.out.println("In process...");
                int result = future.get();
                System.out.println("Result: " + result);
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            executorService.shutdown();
        }
    }

    private static Integer processRequest(int n) throws InterruptedException {
        Random r = new Random();
        int delay = r.nextInt(5) + 1;
        Thread.sleep(delay * 1000);
        return n * n;
    }
}
