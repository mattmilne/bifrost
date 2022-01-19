package physics2d.component;

import component.Component;
import org.joml.Vector2f;
import renderer.DebugDraw;

public class CircleCollider extends Component {

    private float radius = 1.0f;
    private Vector2f offset = new Vector2f();

    public float getRadius() {
        return this.radius;
    }

    public Vector2f getOffset() {
        return this.offset;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setOffset(Vector2f offset) {
        this.offset.set(offset);
    }

    @Override
    public void editorUpdate(float dt) {
        Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
        DebugDraw.addCircle(center, this.radius);
    }
}
