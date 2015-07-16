package com.locusenergy.homework;

public interface Elevator {
    /**
     * Requests the Elevator to move to a certain floor. This method imitates press of a button inside the
     * elevator. Therefore, it should not move the Elevator immediately but just register the request.
     * 
     * Bonus: this method should throw InvalidStateException if the Elevator is NOT busy.
     */
    void requestFloor(int floor);
    
    /**
     * Moves the Elevator to a desired floor.
     */
    void moveElevator(int toFloor);
    
    /**
     * Returns the internal state of the Elevator.
     */
    boolean isBusy();
    
    /**
     * Returns the floor where the Elevator is now.
     */
    int currentFloor();

    Integer getDirection();

    String getName();

    void setFloorListener(FloorListener listener);
}
