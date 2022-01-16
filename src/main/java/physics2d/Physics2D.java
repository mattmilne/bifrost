package physics2d;

import bifrost.GameObject;
import bifrost.Transform;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;
import physics2d.component.Box2DCollider;
import physics2d.component.CircleCollider;
import physics2d.component.Rigidbody2D;

public class Physics2D {

    private static final Vec2 GRAVITY = new Vec2(0, -10.0f);
    private static final float PHYSICS_TIME_STEP = 1.0f / 60.0f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;

    private final World world = new World(GRAVITY);
    private float physicsTime = 0.0f;

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

            PolygonShape shape = new PolygonShape();
            CircleCollider circleCollider;
            Box2DCollider box2DCollider;

            if ((circleCollider = object.getComponent(CircleCollider.class)) != null) {
                shape.setRadius(circleCollider.getRadius());
            } else if ((box2DCollider = object.getComponent(Box2DCollider.class)) != null) {
                Vector2f halfSize = new Vector2f(box2DCollider.getHalfSize()).mul(0.5f);
                Vector2f offset = box2DCollider.getOffset();
                Vector2f origin = new Vector2f(box2DCollider.getOrigin());
                shape.setAsBox(halfSize.x, halfSize.y, new Vec2(origin.x, origin.y), 0.0f);

                Vec2 pos = bodyDef.position;
                float xPos = pos.x + offset.x;
                float yPos = pos.y + offset.y;
                bodyDef.position.set(xPos, yPos);
            }

            Body body = this.world.createBody(bodyDef);
            rigidBody2D.setRawBody(body);
            body.createFixture(shape, rigidBody2D.getMass());
        }
    }

    public void update(float dt) {
        physicsTime += dt;
        if (physicsTime >= 0.0f) {
            physicsTime -= PHYSICS_TIME_STEP;
            world.step(PHYSICS_TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }

    public void destroyGameObject(GameObject gameObject) {
        Body rawBody;
        Rigidbody2D rigidbody2D = gameObject.getComponent(Rigidbody2D.class);
        if (rigidbody2D != null && (rawBody = rigidbody2D.getRawBody()) != null) {
            world.destroyBody(rawBody);
            rigidbody2D.setRawBody(null);
        }
    }
}
