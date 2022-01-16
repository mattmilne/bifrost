package observer;

import bifrost.GameObject;
import observer.event.Event;

import java.util.ArrayList;
import java.util.List;

public class EventSystem {

    private static final List<Observer> observers = new ArrayList<>();

    public static void addObserver(Observer observer) {
        observers.add(observer);
    }

    public static void notify(GameObject object, Event event) {
        for (Observer observer : observers) {
            observer.onNotify(object, event);
        }
    }
}
