package org.example.pr3.Task1;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;


import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class SensorSystem {

    private static final int TEMPERATURE_THRESHOLD = 25;
    private static final int CO2_THRESHOLD = 70;

    public static void main(String[] args) {
        SensorSystem system = new SensorSystem();
        system.run();
    }

    private void run() {

        CountDownLatch latch = new CountDownLatch(1);


        CompositeDisposable disposables = new CompositeDisposable();


        Observable<Integer> temperatureSensor = Observable.interval(1, TimeUnit.SECONDS)
                .map(tick -> generateRandomValue(15, 30));


        Observable<Integer> co2Sensor = Observable.interval(1, TimeUnit.SECONDS)
                .map(tick -> generateRandomValue(30, 100));


        disposables.add(Observable.combineLatest(
                        temperatureSensor,
                        co2Sensor,
                        (temperature, co2) -> new SensorData(temperature, co2))
                .subscribeWith(new SensorAlarm()));


        Observable.timer(20, TimeUnit.SECONDS)
                .subscribe(aLong -> {
                    disposables.dispose();
                    latch.countDown();
                });


        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private int generateRandomValue(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }


    private static class SensorData {
        final int temperature;
        final int co2;

        SensorData(int temperature, int co2) {
            this.temperature = temperature;
            this.co2 = co2;
        }
    }


    private static class SensorAlarm extends DisposableObserver<SensorData> {

        @Override
        public void onNext(SensorData data) {
            boolean tempExceeded = data.temperature > TEMPERATURE_THRESHOLD;
            boolean co2Exceeded = data.co2 > CO2_THRESHOLD;

            if (tempExceeded && co2Exceeded) {
                System.out.println("ALARM!!! Both temperature and CO2 levels are too high!");
            } else if (tempExceeded) {
                System.out.println("Warning: Temperature exceeds the threshold! (" + data.temperature + ")");
            } else if (co2Exceeded) {
                System.out.println("Warning: CO2 exceeds the threshold! (" + data.co2 + ")");
            } else {
                System.out.println("Temperature: " + data.temperature + ", CO2: " + data.co2 + " — All normal.");
            }
        }

        @Override
        public void onError(Throwable e) {
            System.err.println("Error occurred: " + e.getMessage());
        }

        @Override
        public void onComplete() {
            System.out.println("Monitoring completed.");
        }
    }
}