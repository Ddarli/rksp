package org.example.pr3;

import io.reactivex.Observable;

import java.util.Random;

public class Task2 {
    public class SquareStream {
        public static void main(String[] args) {
            Random random = new Random();

            Observable.range(1, 1000)
                    .map(i -> random.nextInt(1001))
                    .map(number -> number * number)
                    .subscribe(square -> System.out.println("Squared number: " + square));
        }
    }

    public class LetterDigitStream {
        public static void main(String[] args) {
            Random random = new Random();
            String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

            Observable<String> letterStream = Observable.range(1, 1000)
                    .map(i -> String.valueOf(alphabet.charAt(random.nextInt(alphabet.length()))));

            Observable<String> digitStream = Observable.range(1, 1000)
                    .map(i -> String.valueOf(random.nextInt(10)));

            Observable.zip(letterStream, digitStream, (letter, digit) -> letter + digit)
                    .subscribe(result -> System.out.println("Combined: " + result));
        }
    }

    public class SkipFirstThree {
        public static void main(String[] args) {
            Random random = new Random();

            Observable.range(1, 10)
                    .map(i -> random.nextInt(100))
                    .skip(3)
                    .subscribe(number -> System.out.println("Number: " + number));
        }
    }
}
