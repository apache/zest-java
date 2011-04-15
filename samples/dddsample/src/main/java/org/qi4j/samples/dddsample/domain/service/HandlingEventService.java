package org.qi4j.samples.dddsample.domain.service;

import org.qi4j.samples.dddsample.domain.model.handling.HandlingEvent;

/**
 * Handling event service.
 */
public interface HandlingEventService
{

    /**
     * Registers a handling event in the system, and notifies interested
     * parties that an event has been registered.
     *
     * @param event handling event to register
     */
    void register( HandlingEvent event );
}