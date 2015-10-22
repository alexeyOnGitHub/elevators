package com.locusenergy.homework;

import java.util.Collections;
import java.util.Set;

import static com.locusenergy.homework.Direction.DOWN;
import static com.locusenergy.homework.Direction.UP;

public class Building implements ElevatorController {

    private final Set<Elevator> elevators;
    private final int numberOfFloors;

    /**
     * @param numberOfFloors Number of floors must be greater than 0
     * @param elevators      elevators set. can be empty.
     */
    public Building(int numberOfFloors, Set<Elevator> elevators) {
        if (numberOfFloors <= 0) {
            throw new IllegalArgumentException("Number of floors must be greater than 0. Given value: " + numberOfFloors);
        }
        this.elevators = Collections.unmodifiableSet(elevators);
        this.numberOfFloors = numberOfFloors;
    }

    @Override
    public Elevator callElevator(int fromFloor, Direction direction) throws InvalidRequest {
        checkFloorWithinLimits(fromFloor);
        Elevator elevator = findElevator(fromFloor, direction);
        elevator.requestFloor(fromFloor);
        return elevator;
    }

    private Elevator findElevator(int floor, Direction direction) {
        Elevator candidate = null;
        while (candidate == null) {
            candidate = findPossibleElevator(floor, direction);
            if (candidate == null) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return candidate;
    }

    private Elevator findPossibleElevator(int floor, Direction direction) {
        Elevator candidate = null;
        int minDistanceInFloors = Integer.MAX_VALUE;
        for (Elevator elevator : elevators) {
            if (isNotBusy(elevator) || isApproaching(elevator, direction, floor)) {
                int distance = elevator.currentFloor() - floor;
                int absDistance = Math.abs(distance);
                if (absDistance < minDistanceInFloors) {
                    minDistanceInFloors = absDistance;
                    candidate = elevator;
                }
                if (minDistanceInFloors == 0) {
                    break;
                }
            }
        }
        return candidate;
    }

    private boolean isApproaching(Elevator elevator, Direction direction, int floor) {
        return elevator.isBusy() && elevator.getDirection() == direction && notReachedOurFloorYet(elevator, floor);
    }

    private boolean isNotBusy(Elevator elevator) {
        return !elevator.isBusy();
    }

    private boolean notReachedOurFloorYet(Elevator elevator, int floor) {
        if (elevator.getDirection().equals(UP)) {
            return elevator.currentFloor() < floor;
        }
        if (elevator.getDirection().equals(DOWN)) {
            return elevator.currentFloor() > floor;
        }
        throw new IllegalStateException("unknown direction: " + elevator.getDirection());
    }

    /**
     * This method incapsulates "floors start with 0/1" logic. Current implementation assumes floors start with 1.
     *
     * @param floor floor to check against our floors limits (top/ground).
     */
    private void checkFloorWithinLimits(int floor) {
        if (floor > numberOfFloors) {
            throw new InvalidRequest("Floor number " + floor + " exceeds number of floors in this building");
        }
        if (floor < 1) {
            throw new InvalidRequest("Floor number " + floor + " is less than the ground floor number (1)");
        }
    }

}
