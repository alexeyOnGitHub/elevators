package com.locusenergy.homework;

public interface ElevatorController {
    /**
     * This is a blocking call that returns an Elevator instance.
     *
     * @param fromFloor Floor number where the call is made from.
     * @param direction If > 0, going up; if <= 0 going down.
     *
     * @return an instance of an Elevator
     * @throws InvalidRequest when fromFloor < minimum floor, or > maximum floor, or direction is invalid.
     */
    Elevator callElevator(int fromFloor, int direction) throws InvalidRequest;
}
