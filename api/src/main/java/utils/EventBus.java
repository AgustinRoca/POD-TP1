package utils;

import java.util.*;
import java.util.concurrent.*;

public class EventBus {
    private static final int NUMBER_OF_THREADS = 10;

    private final ConcurrentMap<String, CopyOnWriteArraySet<EventListener>> listeners;
    private final ExecutorService executorService;

    public EventBus() {
        this.listeners = new ConcurrentHashMap<>();
        this.executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    }

    public void register(String eventName, EventListener eventListener) {
        Collection<EventListener> eventListeners = this.listeners.computeIfAbsent(eventName,
                s -> new CopyOnWriteArraySet<>());
        eventListeners.add(eventListener);
    }

    public void deregister(String eventName, EventListener eventListener) {
        Collection<EventListener> eventListeners = this.listeners.get(eventName);
        if (eventListeners != null) eventListeners.remove(eventListener);
    }

    public void fire(final Event event) {
        CopyOnWriteArraySet<EventListener> eventListeners = this.listeners.get(event.getName());
        if (eventListeners == null) return;

        Iterator<EventListener> iterator = eventListeners.iterator();
        while (iterator.hasNext()) {
            EventListener eventListener = iterator.next();
            this.executorService.execute(() -> eventListener.handle(event));
        }
    }
}
