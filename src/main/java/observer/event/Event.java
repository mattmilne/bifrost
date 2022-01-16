package observer.event;

public class Event {

    public EventType type;

    public Event() {init(EventType.UserEvent);}

    public Event(EventType type) {
        init(type);
    }

    public void init(EventType type) {
        this.type = type;
    }
}
