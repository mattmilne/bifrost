package physics2d;

import bifrost.GameObject;
import bifrost.Transform;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;
import physics2d.component.Box2DCollider;
import physics2d.component.CircleCollider;
import physics2d.component.PillboxCollider;
import physics2d.component.Rigidbody2D;

public class Physics2D {

    private static final Vec2 GRAVITY = new Vec2(0, -10.0f);
    private static final float PHYSICS_TIME_STEP = 1.0f / 60.0f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;

    private final World world = new World(GRAVITY);
    private float physicsTime = 0.0f;

    public Physics2D() {
        world.setContactListener(new BifrostContactListener());
    }

    public Vector2f getGravity() {
        return new Vector2f(world.getGravity().x, world.getGravity().y);
    }

    public void add(GameObject object) {
        Rigidbody2D rigidBody2D = object.getComponent(Rigidbody2D.class);
        if (rigidBody2D != null && rigidBody2D.getRawBody() == null) {
            Transform transform = object.transform;

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float) Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x, transform.position.y);
            bodyDef.angularDamping = rigidBody2D.getAngularDamping();
            bodyDef.linearDamping = rigidBody2D.getLinearDamping();
            bodyDef.fixedRotation = rigidBody2D.isFixedRotation();
            bodyDef.bullet = rigidBody2D.isContinuousCollision();
            bodyDef.gravityScale = rigidBody2D.gravityScale;
            bodyDef.angularVelocity = rigidBody2D.angularVelocity;
            bodyDef.userData = rigidBody2D.gameObject;

            switch (rigidBody2D.getBodyType()) {
                case Kinematic:
                    bodyDef.type = BodyType.KINEMATIC;
                    break;
                case Static:
                    bodyDef.type = BodyType.STATIC;
                    break;
                case Dynamic:
                    bodyDef.type = BodyType.DYNAMIC;
                    break;
            }

            Body body = this.world.createBody(bodyDef);
            body.m_mass = rigidBody2D.getMass();
            rigidBody2D.setRawBody(body);

            CircleCollider circleCollider;
            Box2DCollider box2DCollider;
            PillboxCollider pillboxCollider;

            if ((circleCollider = object.getComponent(CircleCollider.class)) != null) {
                addCircleCollider(rigidBody2D, circleCollider);
            }

            if ((box2DCollider = object.getComponent(Box2DCollider.class)) != null) {
                addBox2DCollider(rigidBody2D, box2DCollider);
            }

            if ((pillboxCollider = object.getComponent(PillboxCollider.class)) != null) {
                addPillboxCollider(rigidBody2D, pillboxCollider);
            }
        }
    }

    public void update(float dt) {
        physicsTime += dt;
        if (physicsTime >= 0.0f) {
            physicsTime -= PHYSICS_TIME_STEP;
            world.step(PHYSICS_TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }

    public void setIsSensor(Rigidbody2D rb) {
        Body body = rb.getRawBody();
        if (body == null) return;

        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.m_isSensor = true;
            fixture = fixture.m_next;
        }
    }

    public void setNotSensor(Rigidbody2D rb) {
        Body body = rb.getRawBody();
        if (body == null) return;

        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.m_isSensor = false;
            fixture = fixture.m_next;
        }
    }

    public void resetCircleCollider(Rigidbody2D rb, CircleCollider circleCollider) {
        Body body = rb.getRawBody();
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addCircleCollider(rb, circleCollider);
        body.resetMassData();
    }

    public void destroyGameObject(GameObject gameObject) {
        Body rawBody;
        Rigidbody2D rigidbody2D = gameObject.getComponent(Rigidbody2D.class);
        if (rigidbody2D != null && (rawBody = rigidbody2D.getRawBody()) != null) {
            world.destroyBody(rawBody);
            rigidbody2D.setRawBody(null);
        }
    }

    public void resetBox2DCollider(Rigidbody2D rb, Box2DCollider boxCollider) {
        Body body = rb.getRawBody();
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addBox2DCollider(rb, boxCollider);
        body.resetMassData();
    }

    public void resetPillboxCollider(Rigidbody2D rb, PillboxCollider pillboxCollider) {
        Body body = rb.getRawBody();
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addPillboxCollider(rb, pillboxCollider);
        body.resetMassData();
    }

    public void addBox2DCollider(Rigidbody2D rigidbody2D, Box2DCollider box2DCollider) {
        Body body = rigidbody2D.getRawBody();
        assert body != null : "Raw body must not be null";

        PolygonShape shape = new PolygonShape();
        Vector2f halfSize = new Vector2f(box2DCollider.getHalfSize()).mul(0.5f);
        Vector2f offset = box2DCollider.getOffset();
        Vector2f origin = new Vector2f(box2DCollider.getOrigin());
        shape.setAsBox(halfSize.x, halfSize.y, new Vec2(offset.x, offset.y), 0.0f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rigidbody2D.getFriction();
        fixtureDef.userData = box2DCollider.gameObject;
        fixtureDef.isSensor = rigidbody2D.isSensor();
        body.createFixture(fixtureDef);
    }

    public void addCircleCollider(Rigidbody2D rigidbody2D, CircleCollider circleCollider) {
        Body body = rigidbody2D.getRawBody();
        assert body != null : "Raw body must not be null";

        CircleShape shape = new CircleShape();
        shape.setRadius(circleCollider.getRadius());
        shape.m_p.set(circleCollider.getOffset().x, circleCollider.getOffset().y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rigidbody2D.getFriction();
        fixtureDef.userData = circleCollider.gameObject;
        fixtureDef.isSensor = rigidbody2D.isSensor();
        body.createFixture(fixtureDef);
    }

    public void addPillboxCollider(Rigidbody2D rigidbody2D, PillboxCollider pillboxCollider) {
        Body body = rigidbody2D.getRawBody();
        assert body != null : "Raw body must not be null";

        addBox2DCollider(rigidbody2D, pillboxCollider.getBox());
        addCircleCollider(rigidbody2D, pillboxCollider.getTopCircle());
        addCircleCollider(rigidbody2D, pillboxCollider.getBottomCircle());
    }

    public RaycastInfo raycast(GameObject requestingObject, Vector2f point1, Vector2f point2) {
        RaycastInfo callback = new RaycastInfo(requestingObject);
        world.raycast(callback, new Vec2(point1.x, point1.y), new Vec2(point2.x, point2.y));
        return callback;
    }

    private int fixtureListSize(Body body) {
        int size = 0;
        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            size++;
            fixture = fixture.m_next;
        }
        return size;
    }

    public boolean isLocked() {
        return world.isLocked();
    }
}
