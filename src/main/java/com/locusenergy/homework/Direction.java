package com.locusenergy.homework;

// this really should be an enum with values UP, DOWN, UNKNOWN.
public final class Direction {
    public static final Integer UP = 1;
    public static final Integer DOWN = -1;

    public static String getDirectionText(Integer direction) {
        if (UP.equals(direction)) {
            return "UP";
        }
        if (DOWN.equals(direction)) {
            return "DOWN";
        }
        return "-none-";
    }
}
