package physics2d.component;

import bifrost.Window;
import component.Component;
import org.joml.Vector2f;

public class PillboxCollider extends Component {

    private transient CircleCollider topCircle = new CircleCollider();
    private transient CircleCollider bottomCircle = new CircleCollider();
    private transient Box2DCollider box = new Box2DCollider();
    private transient boolean resetFixtureNextFrame = false;

    public float width = 0.1f;
    public float height = 0.2f;
    public Vector2f offset = new Vector2f();

    @Override
    public void start() {
        this.topCircle.gameObject = this.gameObject;
        this.bottomCircle.gameObject = this.gameObject;
        this.box.gameObject = this.gameObject;
        recalculateColliders();
    }

    @Override
    public void editorUpdate(float dt) {
        topCircle.editorUpdate(dt);
        bottomCircle.editorUpdate(dt);
        box.editorUpdate(dt);

        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    @Override
    public void update(float dt) {
        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    public void resetFixture() {
        if (Window.getPhysics().isLocked()) {
            resetFixtureNextFrame = true;
            return;
        }
        resetFixtureNextFrame = false;

        if (gameObject != null) {
            Rigidbody2D rigidbody2D = gameObject.getComponent(Rigidbody2D.class);
            if (rigidbody2D != null) {
                Window.getPhysics().resetPillboxCollider(rigidbody2D, this);
            }
        }
    }

    private void recalculateColliders() {
        float circleRadius = width / 4.0f;
        float boxHeight = height - 2 * circleRadius;
        topCircle.setRadius(circleRadius);
        bottomCircle.setRadius(circleRadius);
        topCircle.setOffset(new Vector2f(offset).add(0, boxHeight / 4.0f));
        bottomCircle.setOffset(new Vector2f(offset).sub(0, boxHeight / 4.0f));
        box.setHalfSize(new Vector2f(width / 2.0f, boxHeight / 2.0f));
        box.setOffset(offset);
    }

    public void setWidth(float width) {
        this.width = width;
        recalculateColliders();
        resetFixture();
    }

    public void setHeight(float height) {
        this.height = height;
        recalculateColliders();
        resetFixture();
    }

    public CircleCollider getTopCircle() {
        return topCircle;
    }

    public CircleCollider getBottomCircle() {
        return bottomCircle;
    }

    public Box2DCollider getBox() {
        return box;
    }
}
