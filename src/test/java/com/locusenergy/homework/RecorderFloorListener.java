package com.locusenergy.homework;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of FloorListener, which records all floor visit events in array.
 */
public class RecorderFloorListener implements FloorListener {
    private final static Logger LOG = Logger.getLogger(RecorderFloorListener.class);

    private final List<Integer> list = new ArrayList<Integer>();

    @Override
    public void floorVisited(int floor) {
        LOG.debug("visited: " + floor);
        synchronized (list) {
            list.add(floor);
        }
    }

    public List<Integer> getList() {
        return list;
    }
}
