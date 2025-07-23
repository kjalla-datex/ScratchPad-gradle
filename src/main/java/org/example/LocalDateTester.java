package org.example;

import java.time.Clock;
import java.time.LocalDate;

public class LocalDateTester {

    private static final LocalDate FIXED_TODAY = LocalDate.now();
    private static final Clock CLOCK = Clock.systemDefaultZone();

    public static void getNow() {
        System.out.println("Today is: " + FIXED_TODAY);
    }

    public static void getNowWithClock() {
        System.out.println("Today is: " + LocalDate.now(CLOCK));
    }
}
