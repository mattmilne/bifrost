package physics2d;

import bifrost.GameObject;
import component.Component;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class BifrostContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        GameObject objectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objectB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component component : objectA.getAllComponents()) {
            component.beginCollision(objectB, contact, aNormal);
        }

        for (Component component : objectB.getAllComponents()) {
            component.beginCollision(objectA, contact, bNormal);
        }
    }

    @Override
    public void endContact(Contact contact) {
        GameObject objectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objectB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component component : objectA.getAllComponents()) {
            component.endCollision(objectB, contact, aNormal);
        }

        for (Component component : objectB.getAllComponents()) {
            component.endCollision(objectA, contact, bNormal);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        GameObject objectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objectB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component component : objectA.getAllComponents()) {
            component.preSolve(objectB, contact, aNormal);
        }

        for (Component component : objectB.getAllComponents()) {
            component.preSolve(objectA, contact, bNormal);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        GameObject objectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objectB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component component : objectA.getAllComponents()) {
            component.postSolve(objectB, contact, aNormal);
        }

        for (Component component : objectB.getAllComponents()) {
            component.postSolve(objectA, contact, bNormal);
        }
    }
}
