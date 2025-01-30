package de.ostfalia.util;

import java.util.ArrayList;
import java.util.List;

public class EventBus {
    private static final EventBus instance = new EventBus();
    private final List<EventObserver> observers = new ArrayList<>();

    private EventBus() {}

    public static EventBus getInstance() {
        return instance;
    }

    public void registerObserver(EventObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(Event event) {
        for (EventObserver observer : observers) {
            observer.handleEvent(event);
        }
    }
}
