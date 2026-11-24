package dev.protoskey.service;

import dev.protoskey.point.UpdatesEndpoint;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

@ApplicationScoped
public class UpdateNotifier {
    public record UpdateEvent(String topic) {
    }

    @Inject
    private Event<UpdateEvent> events;

    public void notify(String topic) {
        events.fire(new UpdateEvent(topic));
    }

    public void onUpdate(@Observes(during = TransactionPhase.AFTER_SUCCESS) UpdateEvent event) {
        UpdatesEndpoint.broadcast(event.topic());
    }
}
