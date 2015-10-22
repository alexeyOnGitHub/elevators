package com.locusenergy.homework.internal;

import com.locusenergy.homework.Direction;
import com.locusenergy.homework.Elevator;
import com.locusenergy.homework.FloorListener;
import com.locusenergy.homework.InvalidRequest;
import org.apache.log4j.Logger;

import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.locusenergy.homework.Direction.DOWN;
import static com.locusenergy.homework.Direction.UP;

/**
 * Use ElevatorFactory.createElevator() to create instances of this class.
 *
 * @see ElevatorFactory#createElevator(String, int, int, int)
 */
class ElevatorImpl implements Elevator {

    private final static Logger LOG = Logger.getLogger(ElevatorImpl.class);

    private final String name;
    private final TreeSet<Integer> floorsToStopAt = new TreeSet<Integer>();
    private final int minimumFloor;
    private final int topFloor;
    private final Lock lock = new ReentrantLock();
    private final Thread thread;

    // TODO "busy" can be replaced with "direction==null"
    private volatile boolean busy;
    private volatile int currentFloor;
    private volatile Direction direction;
    private volatile FloorListener listener;


    ElevatorImpl(String name, int currentFloor, int minimumFloor, int topFloor) {
        this.name = name;
        this.currentFloor = currentFloor;
        this.minimumFloor = minimumFloor;
        this.topFloor = topFloor;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    lock.lock();
                    try {
                        if (floorsToStopAt.isEmpty()) {
                            // TODO can improve this by using something like
                            //     private static final int MSEC_BEFORE_UNLOCKING_ELEVATOR = 1000;
                            // and checking time expired since last elevator move.
                            releaseElevator();
                        } else {
                            goToNextRequestedFloor();
                        }
                    } finally {
                        lock.unlock();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    private void releaseElevator() {
        lock.lock();
        try {
            busy = false;
            direction = null;
        } finally {
            lock.unlock();
        }

    }

    private void goToNextRequestedFloor() {
        lock.lock();

        try {
            if (needToStopAt(currentFloor)) {
                LOG.debug(name + ": stopped at floor " + currentFloor + ". Removing it from the list");
                floorsToStopAt.remove(currentFloor);
            } else {
                keepMoving();
            }

        } finally {
            lock.unlock();
        }
    }

    private void keepMoving() {
        changeDirectionIfNoStopsOnTheWay();
        LOG.debug(name + ": going " + direction.toString() + " from " + currentFloor + ". " + getFloorsString());
        if (direction.equals(UP)) {
            if (currentFloor < topFloor) {
                currentFloor++;
                notifyListener();
            } else {
                changeDirection();
            }
        } else if (direction.equals(DOWN)) {
            if (currentFloor > 1) {
                currentFloor--;
                notifyListener();
            } else {
                changeDirection();
            }
        } else {
            throw new RuntimeException("don't know what to do");
        }
    }

    private void changeDirectionIfNoStopsOnTheWay() {
        // TODO what if no stops at all? verify this.
        if (UP.equals(direction) && noStopsHigherThanCurrent()) {
            direction = DOWN;
        } else if (DOWN.equals(direction) && noStopsLowerThanCurrent()) {
            direction = UP;
        }
    }

    private boolean noStopsHigherThanCurrent() {
        Integer nextFloorUp = floorsToStopAt.higher(currentFloor);
        return nextFloorUp == null;
    }

    private boolean noStopsLowerThanCurrent() {
        Integer nextFloorDown = floorsToStopAt.lower(currentFloor);
        return nextFloorDown == null;
    }

    private void changeDirection() {
        if (UP == direction) {
            direction = DOWN;
        } else if (DOWN == direction) {
            direction = UP;
        }
    }

    private boolean needToStopAt(int currentFloor) {
        return floorsToStopAt.contains(currentFloor);
    }

    @Override
    public void requestFloor(int floor) {
        checkFloorWithinLimits(floor);
        lock.lock();

        try {
            LOG.debug(name + ": requested floor: " + floor + ". current floor: " + currentFloor + ", current direction: " + direction);
            if (floorsToStopAt.isEmpty()) {
                direction = findDirection(floor);
            }
            floorsToStopAt.add(floor);
            busy = true;
        } finally {
            lock.unlock();
        }
    }

    private Direction findDirection(int floor) {
        if (currentFloor <= floor) {
            return UP;
        }
        return DOWN;
    }

    @Override
    public void moveElevator(int toFloor) {
        throw new RuntimeException("Not implemented yet - what is it supposed to do really?");
    }

    @Override
    public boolean isBusy() {
        return busy;
    }

    @Override
    public int currentFloor() {
        return currentFloor;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setFloorListener(FloorListener listener) {
        this.listener = listener;
    }

    // TODO further improvements:
    // 1. make sure exceptions in listener do not affect our code
    // 2. maybe send notifications in a separate thread because what if listener is very slow?..
    private void notifyListener() {
        // saving instance variable here for thread safety
        // another option would be having a lock object and locking it
        // at the beginning of this and setFloorListener() methods.
        // (and unlocking at the end)
        FloorListener localVar = listener;
        if (localVar != null) {
            localVar.floorVisited(currentFloor);
        }
    }

    void startThread() {
        thread.start();
    }

    @Override
    public String toString() {
        return "{" +
                name +
                ", busy=" + busy +
                ", currentFloor=" + currentFloor +
                ", direction=" + direction.toString()
                + "}";
    }

    private String getFloorsString() {
        String result = "floors to visit { ";
        for (Integer integer : floorsToStopAt) {
            result += integer + " ";
        }
        result += "}";
        return result;
    }

    /**
     * This method incapsulates "floors start with 0/1" logic. Current implementation assumes floors start with 1.
     *
     * @param floor floor to check against our floors limits (top/ground).
     */
    private void checkFloorWithinLimits(int floor) {
        if (floor > topFloor) {
            throw new InvalidRequest("Floor number " + floor + " is higher than the top floor this elevator operates on (" + topFloor + ")");
        }
        if (floor < minimumFloor) {
            throw new InvalidRequest("Floor number " + floor + " is lower  than the lowest floor this elevator operates on (" + minimumFloor + ")");
        }
    }
}
