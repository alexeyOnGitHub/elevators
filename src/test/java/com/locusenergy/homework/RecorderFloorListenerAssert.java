package com.locusenergy.homework;

import org.fest.assertions.Assertions;
import org.fest.assertions.GenericAssert;

import java.util.Arrays;
import java.util.List;

/**
 * Sample usage:
 * <pre>
 *    Elevator elevator = ...
 *    RecorderFloorListener listener = ...
 *    elevator.setFloorListener(listener)
 *    assertThat(listener).onlyVisited(2, 1, 2, 3);
 * </pre>
 */
public class RecorderFloorListenerAssert extends GenericAssert<RecorderFloorListenerAssert, RecorderFloorListener> {

    public RecorderFloorListenerAssert(RecorderFloorListener actual) {
        super(RecorderFloorListenerAssert.class, actual);
    }

    /**
     * an entry point for RecorderFloorListenerAssert to follow Fest standard <code>assertThat()</code> statements.<br>
     * With a static import, one can write directly : <code>assertThat(listener).onlyVisited(...);</code>
     *
     * @param actual the RecorderFloorListener we want to make assertions on.
     * @return a new </code>{@link RecorderFloorListenerAssert}</code>
     */
    public static RecorderFloorListenerAssert assertThat(RecorderFloorListener actual) {
        return new RecorderFloorListenerAssert(actual);
    }

    public RecorderFloorListenerAssert onlyVisited(Integer... floors) {
        isNotNull();
        List<Integer> actualVisitedFloors = actual.getList();
        String errorMessage = String.format("Must have only visited <%s> but in fact visited <%s>", Arrays.toString(floors), actualVisitedFloors);
        Assertions.assertThat(actualVisitedFloors).overridingErrorMessage(errorMessage).containsExactly(floors);
        return this;
    }
}
