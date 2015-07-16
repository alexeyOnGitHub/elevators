package com.locusenergy.homework;

import com.locusenergy.homework.internal.ElevatorFactory;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.locusenergy.homework.Direction.DOWN;
import static com.locusenergy.homework.Direction.UP;
import static com.locusenergy.homework.ElevatorAssert.assertThat;
import static com.locusenergy.homework.RecorderFloorListenerAssert.assertThat;

/**
 * This class is not thread safe, its methods cannot be run in parallel, which is acceptable because JUnit will
 * create a new instance of this class for each @Test.
 * To make this class thread-safe (if for some reason that is required),
 * "listeners" and "building" instance variables will need to be moved to methods bodies
 * and then init...() method would return some InitStateObject with building and listeners map. that would
 * give a bad testing API...
 */
public class BuildingTest {
    private final static Logger LOG = Logger.getLogger(BuildingTest.class);

    private static final int NUMBER_OF_FLOORS = 100;

    private static final String ELEVATOR_1 = "Elevator 1";
    private static final String ELEVATOR_2 = "Elevator 2";
    private static final String ELEVATOR_3 = "Elevator 3";

    private final Map<String, RecorderFloorListener> listeners = new HashMap<String, RecorderFloorListener>();

    private Building building;

    @Test(expected = IllegalArgumentException.class)
    public void illegalParameterGivesException() {
        new Building(0, Collections.<Elevator>emptySet());
    }

    @Test(expected = InvalidRequest.class)
    public void callingElevatorFromAboveTopFloorGivesException() {
        initBuildingWithElevatorsAtFloors(1);
        building.callElevator(NUMBER_OF_FLOORS+1, UP);
    }

    @Test(expected = InvalidRequest.class)
    public void callingElevatorFromUnderGroundFloorGivesException() {
        initBuildingWithElevatorsAtFloors(5);
        building.callElevator(0, UP);
    }

    @Test(expected = InvalidRequest.class)
    public void callingElevatorFromUnderGroundFloorDownDirectionGivesException() {
        initBuildingWithElevatorsAtFloors(5);
        building.callElevator(0, DOWN);
    }

    @Test
    public void elevatorFromGroundFloorIsReturnedFromInitialState() {
        initBuildingWithElevatorsAtFloors(1, 1, 1);
        Elevator elevator = building.callElevator(1, UP);
        assertThat(elevator).hasCurrentFloor(1);
    }

    @Test
    public void nearestElevatorIsReturnedWhenNoBusyElevators() {
        initBuildingWithElevatorsAtFloors(1, 50, NUMBER_OF_FLOORS);
        assertThat(building.callElevator(2, UP)).hasName(ELEVATOR_1);
        assertThat(building.callElevator(49, UP)).hasName(ELEVATOR_2);
        assertThat(building.callElevator(NUMBER_OF_FLOORS, UP)).hasName(ELEVATOR_3);
    }

    @Test
    public void nearestElevatorIsReturnedWhenItBecomesAvailableAgain() {
        initBuildingWithElevatorsAtFloors(1, 50, 100);
        Elevator elevator = building.callElevator(100, UP);
        assertThat(elevator).hasName("Elevator 3");
        waitUntilElevatorIsDone(elevator);

        assertThat(building.callElevator(80, UP)).hasName("Elevator 3");
    }

    @Test
    public void callingElevatorFromTopFloorGoingUpCorrectlyOpensElevator() {
        initBuildingWithElevatorsAtFloors(5);
        Elevator elevator = building.callElevator(5, UP);
        waitUntilElevatorIsDone(elevator);
        assertThat(elevator).hasCurrentFloor(5);
    }

    @Test
    public void nearestNonBusyElevatorIsReturned() {
        initBuildingWithElevatorsAtFloors(1, 50, NUMBER_OF_FLOORS);

        Elevator elevator1 = building.callElevator(1, UP);
        elevator1.requestFloor(10); // going 1 --> 10

        assertThat(building.callElevator(1, UP)).hasName(ELEVATOR_2);
    }

    @Test
    public void oneElevatorTwoPassengers() {
        initBuildingWithElevatorsAtFloors(1);

        Elevator elevator = building.callElevator(1, UP);
        elevator.requestFloor(5); // passenger 1 going 1 --> 5
        elevator.requestFloor(10); // passenger 2 going 1 --> 10
        waitUntilElevatorIsDone(elevator);
        assertThat(elevator).hasCurrentFloor(10);
    }

    @Test
    public void elevatorGoesToFloorWhereItWasCalledFrom() {
        initBuildingWithElevatorsAtFloors(5);
        Elevator elevator = building.callElevator(1, UP);
        waitUntilElevatorIsDone(elevator);
        assertThat(elevator).hasCurrentFloor(1);
    }

    @Test
    public void elevatorGoesToFloorWhereItWasCalledFromBeforeGoingToRequestedFloor() {
        initBuildingWithElevatorsAtFloors(7);

        Elevator elevator1 = building.callElevator(1, UP);
        waitUntilElevatorIsDone(elevator1);
        elevator1.requestFloor(2); // going 1 --> 2

        waitUntilElevatorIsDone(elevator1);
        assertThat(elevator1).hasCurrentFloor(2);
    }

    @Test
    public void twoElevatorsTwoPassengersGoingUp() {
        initBuildingWithElevatorsAtFloors(5, 5);

        Elevator elevator1 = building.callElevator(1, UP);
        waitUntilElevatorIsDone(elevator1);
        elevator1.requestFloor(5); // going 1 --> 5

        Elevator elevator2 = building.callElevator(1, UP);
        waitUntilElevatorIsDone(elevator2);
        elevator2.requestFloor(6); // going 1 --> 6

        waitUntilElevatorIsDone(elevator1);
        waitUntilElevatorIsDone(elevator2);
        assertThat(elevator1).hasCurrentFloor(5);
        assertThat(elevator2).hasCurrentFloor(6);
    }

    @Test
    public void verifyVisitorAssertWorks() {
        initBuildingWithElevatorsAtFloors(3);

        Elevator elevator = building.callElevator(1, UP);
        waitUntilElevatorIsDone(elevator);
        elevator.requestFloor(3);
        waitUntilElevatorIsDone(elevator);
        assertThat(elevator).hasCurrentFloor(3);
        // TODO the floor listener should also record the initial floor we are at. improve later.
        assertThat(getListenerFor(elevator)).onlyVisited(2, 1, 2, 3);
    }

    @Test
    public void elevatorChangesDirectionAndDoesNotGoAllTheWayUpAfterVisiting5thFloor() {
        initBuildingWithElevatorsAtFloors(3);

        Elevator elevator = building.callElevator(3, UP);
        waitUntilElevatorIsDone(elevator);
        elevator.requestFloor(5);
        elevator.requestFloor(1);

        waitUntilElevatorIsDone(elevator);
        assertThat(elevator).hasCurrentFloor(1);
        // TODO the floor listener should also record the initial floor we are at. improve later.
        assertThat(getListenerFor(elevator)).onlyVisited(4, 5, 4, 3, 2, 1);
    }

    // TODO this test uses Random generator and thus should not be a part of regular testing to avoid ND problems.
    // This is really a DEMO and not a real test.
    @Test
    public void sixElevatorsTest() throws InterruptedException, ExecutionException {
        initBuildingWithElevatorsAtFloors(1, 1, 1, 1, 1, 1);

        ExecutorService executor = Executors.newFixedThreadPool(10);

        int tasksNumber = 20;
        Future<Integer> finalFloors[] = new Future[tasksNumber];
        for (int i = 0; i < tasksNumber; i++) {
            finalFloors[i] = executor.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    Random random = new Random();
                    Integer requestedFromFloor = generateRandomFloorNumber(random);
                    Integer direction = random.nextBoolean() ? UP : DOWN;
                    Elevator elevator = building.callElevator(requestedFromFloor, direction);
                    waitUntilElevatorIsDone(elevator);
                    elevator.requestFloor(generateRandomFloorNumber(random));
                    waitUntilElevatorIsDone(elevator);
                    return elevator.currentFloor();
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        // this is mostly just to see if there were any exceptions during invocation
        for (Future<Integer> floor : finalFloors) {
            LOG.debug("final floor: " + floor.get());
        }
    }

    private int generateRandomFloorNumber(Random random) {
        return random.nextInt(10) + 1;
    }

    private void waitUntilElevatorIsDone(Elevator elevator) {
        while (true) {
            if (elevator.isBusy()) {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                return;
            }
        }
    }

    private HashSet<Elevator> createElevatorsWithFloors(int... floor) {
        HashSet<Elevator> elevators = new HashSet<Elevator>();
        for (int i = 0; i < floor.length; i++) {
            Elevator elevator = ElevatorFactory.createElevator("Elevator " + (i + 1), floor[i], 1, NUMBER_OF_FLOORS);
            RecorderFloorListener listener = new RecorderFloorListener();
            elevator.setFloorListener(listener);
            elevators.add(elevator);
            listeners.put(elevator.getName(), listener);
        }
        return elevators;
    }

    private RecorderFloorListener getListenerFor(Elevator elevator) {
        return listeners.get(elevator.getName());
    }

    /**
     * New elevators receive names like "Elevator 1", "Elevator 2", ..., starting from 1.
     * You can use #ELEVATOR_1, #ELEVATOR_2 and other constants to compare names.
     */
    private void initBuildingWithElevatorsAtFloors(int... elevatorFloors) {
        HashSet<Elevator> elevators = createElevatorsWithFloors(elevatorFloors);
        building = new Building(NUMBER_OF_FLOORS, elevators);
        LOG.debug("Created building with " + NUMBER_OF_FLOORS + " floors and elevators at " + Arrays.toString(elevatorFloors));
    }
}
