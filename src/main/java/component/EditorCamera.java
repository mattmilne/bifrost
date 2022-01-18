package component;

import bifrost.Camera;
import bifrost.KeyListener;
import bifrost.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DECIMAL;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class EditorCamera extends Component {

    private static final float DRAG_SENSITIVITY = 30.0f;
    private static final float SCROLL_SENSITIVITY = 0.1f;

    private final Camera levelEditorCamera;
    private Vector2f clickOrigin;
    // 2 frames
    private float dragDebounce = 0.032f;
    private float lerpTime = 0.0f;
    private boolean reset = false;

    public EditorCamera(Camera camera) {
        this.levelEditorCamera = camera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void editorUpdate(float dt) {
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f mousePos = MouseListener.getWorld();
            if (dragDebounce > 0.0f) {
                this.clickOrigin = mousePos;
                dragDebounce -= dt;
                return;
            }

            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            levelEditorCamera.position.sub(delta.mul(dt).mul(DRAG_SENSITIVITY));
            clickOrigin.lerp(mousePos, dt);
        } else if (dragDebounce <= 0.0f) {
            dragDebounce = 0.1f;
        }

        float scrollY = MouseListener.getScrollY();
        if (scrollY != 0.0f) {
            float addValue = (float) Math.pow(Math.abs(scrollY * SCROLL_SENSITIVITY), 1.0f / levelEditorCamera.getZoom());
            addValue *= -Math.signum(scrollY);
            levelEditorCamera.addZoom(addValue);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_KP_DECIMAL)) {
            reset = true;
        }

        if (reset) {
            levelEditorCamera.position.lerp(new Vector2f(), lerpTime);
            levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() +
                    ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            this.lerpTime += 0.1f * dt;
            if (Math.abs(levelEditorCamera.position.x) <= 5.0f && Math.abs(levelEditorCamera.position.y) <= 5.0f) {
                this.lerpTime = 0.0f;
                levelEditorCamera.position.set(0f, 0f);
                this.levelEditorCamera.setZoom(1.0f);
                reset = false;
            }
        }
    }
}
