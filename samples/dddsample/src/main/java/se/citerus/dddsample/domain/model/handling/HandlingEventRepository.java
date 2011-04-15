package se.citerus.dddsample.domain.model.handling;

import java.util.List;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

/**
 * Handling event repository.
 */
public interface HandlingEventRepository {

  /**
   * Saves a (new) handling event.
   *
   * @param event handling event to save
   */
  void save(final HandlingEvent event);


  /**
   * @param trackingId cargo tracking id
   * @return All handling events for this cargo, ordered by completion time.
   */
  List<HandlingEvent> findEventsForCargo(final TrackingId trackingId);
}
