package org.example;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        //Check whats printing for each call
        for(int i=0; i<10; i++) {
            System.out.println("i: " + i);
            LocalDateTester.getNow();
        }
        System.out.println("----------------------- ");
        //Check whats printing for each call
        for(int i=0; i<10; i++) {
            System.out.println("i: " + i);
            LocalDateTester.getNowWithClock();
        }
        System.out.println("----------------------- ");
        //Check whats printing for each call
        for(int i=0; i<10; i++) {
            System.out.println("i: " + i);
            LocalDateTester.getNowWithClock();
        }
    }
}