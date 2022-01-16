package observer;

import bifrost.GameObject;
import observer.event.Event;

public interface Observer {
    void onNotify(GameObject object, Event event);
}
