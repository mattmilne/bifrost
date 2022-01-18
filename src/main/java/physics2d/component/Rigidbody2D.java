package physics2d.component;

import bifrost.Window;
import component.Component;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;
import physics2d.enums.BodyType;

public class Rigidbody2D extends Component {
    private final Vector2f velocity = new Vector2f();
    private float angularDamping = 0.8f;
    private float linearDamping = 0.9f;
    private final float mass = 0.0f;
    private final BodyType bodyType = BodyType.Dynamic;
    private final float friction = 0.1f;
    public float angularVelocity = 0.0f;
    public float gravityScale = 1.0f;
    private boolean isSensor = false;

    private boolean fixedRotation = false;
    private boolean continuousCollision = true;

    private transient Body rawBody = null;

    @Override
    public void update(float dt) {
        if (rawBody != null) {
            this.gameObject.transform.position.set(rawBody.getPosition().x, rawBody.getPosition().y);
            this.gameObject.transform.rotation = (float) Math.toDegrees(rawBody.getAngle());
        }
    }

    public void addVelocity(Vector2f forceToAdd) {
        if (rawBody != null) {
            rawBody.applyForceToCenter(new Vec2(velocity.x, velocity.y));
        }
    }

    public void addImpulse(Vector2f impulse) {
        if (rawBody != null) {
            rawBody.applyLinearImpulse(new Vec2(velocity.x, velocity.y), rawBody.getWorldCenter());
        }
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
        if (rawBody != null) {
            this.rawBody.setLinearVelocity(new Vec2(velocity.x, velocity.y));
        }
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
        if (rawBody != null) {
            this.rawBody.setAngularVelocity(angularVelocity);
        }
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
        if (rawBody != null) {
            this.rawBody.setGravityScale(gravityScale);
        }
    }

    public void setIsSensor() {
        isSensor = true;
        if (rawBody != null) {
            Window.getPhysics().setIsSensor(this);
        }
    }

    public void setNotSensor() {
        isSensor = false;
        if (rawBody != null) {
            Window.getPhysics().setNotSensor(this);
        }
    }

    public float getFriction() {
        return this.friction;
    }

    public boolean isSensor() {
        return this.isSensor;
    }

    public void setRawBody(Body body) {
        this.rawBody = body;
    }

    public Body getRawBody() {
        return this.rawBody;
    }

    public float getAngularDamping() {
        return this.angularDamping;
    }

    public float getLinearDamping() {
        return this.linearDamping;
    }

    public boolean isFixedRotation() {
        return this.fixedRotation;
    }

    public boolean isContinuousCollision() {
        return this.continuousCollision;
    }

    public BodyType getBodyType() {
        return this.bodyType;
    }

    public float getMass() {
        return this.mass;
    }
}
