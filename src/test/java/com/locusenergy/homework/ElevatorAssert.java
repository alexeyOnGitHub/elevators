package com.locusenergy.homework;

import org.fest.assertions.Assertions;
import org.fest.assertions.GenericAssert;

import java.util.Arrays;
import java.util.List;

/**
 * Sample usage:
 * <pre>
 *    Elevator elevator = ...
 *    assertThat(elevator).hasCurrentFloor(3);
 *    assertThat(elevator).hasName("Elevator 1");
 *    assertThat(elevator).onlyVisited(2, 1, 2, 3);
 * </pre>
 */
public class ElevatorAssert extends GenericAssert<ElevatorAssert, Elevator> {

    public ElevatorAssert(Elevator actual) {
        super(ElevatorAssert.class, actual);
    }

    /**
     * an entry point for ElevatorAssert to follow Fest standard <code>assertThat()</code> statements.<br>
     * With a static import, one can write directly : <code>assertThat(elevator).hasCurrentFloor(5);</code>
     *
     * @param actual the Elevator we want to make assertions on.
     * @return a new </code>{@link ElevatorAssert}</code>
     */
    public static ElevatorAssert assertThat(Elevator actual) {
        return new ElevatorAssert(actual);
    }

    /**
     * Verifies that the actual Elevator's floor is equal to the given one.
     *
     * @param currentFloor the given floor to compare the actual floor to.
     * @return this assertion object.
     * @throws AssertionError - if the actual Elevator's floor is not equal to the given one.
     */
    public ElevatorAssert hasCurrentFloor(int currentFloor) {
        isNotNull();

        String errorMessage = String.format("Expected floor to be <%s> but was <%s> for elevator <%s>", currentFloor, actual.currentFloor(), actual.getName());
        Assertions.assertThat(actual.currentFloor()).overridingErrorMessage(errorMessage).isEqualTo(currentFloor);

        // return the current assertion for method chaining
        return this;
    }

    public ElevatorAssert hasName(String name) {
        isNotNull();
        String errorMessage = String.format("Expected name to be <%s> but was <%s>", name, actual.getName());
        Assertions.assertThat(actual.getName()).overridingErrorMessage(errorMessage).isEqualTo(name);
        return this;
    }

}
