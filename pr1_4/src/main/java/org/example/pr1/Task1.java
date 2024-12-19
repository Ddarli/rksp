package org.example.pr1;

import java.util.Arrays;
import java.util.concurrent.*;
// Latyshev IKBO-16-21
public class Task1 {
    private static void foo1(int[] arr) throws InterruptedException {
        int sum = 0;
        long startTime = System.currentTimeMillis();
        for (int i : arr) {
            sum += i;
            Thread.sleep(1);
        }
        long endTime = System.currentTimeMillis();

        System.out.println("1. Sum " + sum + " Time " + (endTime - startTime));
    }
    private static void foo2(int[] arr) throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();
        int totalsSum = 0;
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Future<Long>[] futures = new Future[4];
        int chunkSize = arr.length / 4;
        for (int i = 0; i < 4; i++) {
            final int start = i * chunkSize;
            final int end = (i == 4 - 1) ? arr.length : start + chunkSize;

            futures[i] = executorService.submit(() -> {
                long sum = 0;
                for (int j = start; j < end; j++) {
                    sum += arr[j];
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return sum;
            });
        }
        for (Future<Long> future : futures) {
            totalsSum += future.get();
        }
        long endTime = System.currentTimeMillis();

        System.out.println("2. Sum " + totalsSum + " Time " + (endTime - startTime));
    }
    static class SumTask extends RecursiveTask<Long>{

        private final int[] array;
        private final int start;
        private final int end;
        private static final int THRESHOLD = 1000;

        public SumTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                long sum = 0;
                try {
                    for (int i = start; i < end; i++) {
                        sum += array[i];
                        Thread.sleep(1);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return sum;
            }else{
                int middle = (start + end) / 2;
                SumTask left = new SumTask(array, start, middle);
                SumTask right = new SumTask(array, middle, end);
                invokeAll(left, right);
                return left.join() + right.join();
            }
        }
    }
    private static void foo3(int[] arr){
        int[] array = new int[10000];
        Arrays.fill(array, 1);
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        long startTime = System.currentTimeMillis();
        long sum = forkJoinPool.invoke(new SumTask(arr, 0, arr.length));
        long endTime = System.currentTimeMillis();

        System.out.println("3. Sum " + sum + " Time " + (endTime - startTime));
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int[] array = new int[1000];
        Arrays.fill(array, 1);
        foo1(array);
        foo2(array);
        foo3(array);
    }
}
