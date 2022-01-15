package physics2d;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class Physics2D {

    private static final Vec2 GRAVITY = new Vec2(0, -10.0f);
    private static final float PHYSICS_TIME_STEP = 1.0f / 60.0f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;

    private World world = new World(GRAVITY);
    private float physicsTime = 0.0f;

    public void update(float dt) {
        physicsTime += dt;
        if (physicsTime >= 0.0f) {
            physicsTime -= PHYSICS_TIME_STEP;
            world.step(PHYSICS_TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }
}
