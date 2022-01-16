package physics2d.component;

import org.joml.Vector2f;
import renderer.DebugDraw;

public class Box2DCollider extends Collider {

    private final Vector2f halfSize = new Vector2f(1);
    private final Vector2f origin = new Vector2f();

    public Vector2f getHalfSize() {
        return this.halfSize;
    }

    public Vector2f getOrigin() {
        return this.origin;
    }

    @Override
    public void editorUpdate(float dt) {
        Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.getOffset());
        DebugDraw.addBox2D(center, this.halfSize, this.gameObject.transform.rotation);
    }
}
