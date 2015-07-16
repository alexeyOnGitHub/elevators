package com.locusenergy.homework.internal;

import com.locusenergy.homework.Elevator;

public class ElevatorFactory {
    /**
     * Creates and initializes an Elevator instance. The new instance is immediately ready to process incoming requests.
     *
     * @param name         name (ID if you wish) of the elevator.
     * @param currentFloor initial floor where the elevator is located.
     * @param minimumFloor lowest floor number serviced by this elevator
     * @param topFloor     top floor number serviced by an elevator can be different from the total number of floors
     *                     in the building where it is installed. E.g. a parking service elevator can only work with floors 1..3
     *                     (real example from our office building in Foster City).
     * @return
     */
    public static Elevator createElevator(String name, int currentFloor, int minimumFloor, int topFloor) {
        ElevatorImpl elevator = new ElevatorImpl(name, currentFloor, minimumFloor, topFloor);
        elevator.startThread();
        return elevator;
    }
}
