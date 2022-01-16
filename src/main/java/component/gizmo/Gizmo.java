package component.gizmo;

import bifrost.KeyListener;
import component.Component;
import component.NonPickable;
import component.Sprite;
import component.SpriteRenderer;
import editor.PropertiesWindow;
import bifrost.GameObject;
import bifrost.MouseListener;
import bifrost.Prefab;
import bifrost.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Gizmo extends Component {
    private static final float ASPECT_RATIO = 80.0f;
    private static final float GIZMO_WIDTH = 16.0f / ASPECT_RATIO;
    private static final float GIZMO_HEIGHT = 48.0f / ASPECT_RATIO;
    private static final Vector4f X_AXIS_COLOR = new Vector4f(1, 0.3f, 0.3f, 1);
    private static final Vector4f X_AXIS_COLOR_HOVER = new Vector4f(1, 0, 0, 1);
    private static final Vector4f Y_AXIS_COLOR = new Vector4f(0.3f, 1, 0.3f, 1);
    private static final Vector4f Y_AXIS_COLOR_HOVER = new Vector4f(0, 1, 0, 1);
    private static final Vector2f X_AXIS_OFFSET = new Vector2f(24.0f / ASPECT_RATIO, -6.0f / ASPECT_RATIO);
    private static final Vector2f Y_AXIS_OFFSET = new Vector2f(-7.0f / ASPECT_RATIO, 21.0f / ASPECT_RATIO);

    private final GameObject xAxisObject;
    private final GameObject yAxisObject;
    private final SpriteRenderer xAxisSprite;
    private final SpriteRenderer yAxisSprite;
    private final PropertiesWindow propertiesWindow;

    protected GameObject activeGameObject = null;
    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;
    private boolean using = false;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        this.propertiesWindow = propertiesWindow;
        this.xAxisObject = Prefab.generateSpriteObject(arrowSprite, GIZMO_WIDTH, GIZMO_HEIGHT);
        this.yAxisObject = Prefab.generateSpriteObject(arrowSprite, GIZMO_WIDTH, GIZMO_HEIGHT);
        this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);

        this.xAxisObject.addComponent(new NonPickable());
        this.yAxisObject.addComponent(new NonPickable());

        Window.getScene().addGameObjectToScene(this.xAxisObject);
        Window.getScene().addGameObjectToScene(this.yAxisObject);
    }

    @Override
    public void start() {
        this.xAxisObject.transform.rotation = 90;
        this.yAxisObject.transform.rotation = 180;
        this.xAxisObject.transform.zIndex = Integer.MAX_VALUE;
        this.yAxisObject.transform.zIndex = Integer.MAX_VALUE;
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float dt) {
        if (using) {
            this.setInactive();
        }
    }

    @Override
    public void editorUpdate(float dt) {
        if (!using) return;

        this.activeGameObject = this.propertiesWindow.getActiveGameObject();
        if (this.activeGameObject != null) {
            setActive();

            // TODO: move this into it's own keyEditorBinding component class
            if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                    KeyListener.keyBeginPress(GLFW_KEY_D)) {

                GameObject newGameObject = this.activeGameObject.copy();
                Window.getScene().addGameObjectToScene(newGameObject);
                newGameObject.transform.position.add(0.1f, 0.1f);
                this.propertiesWindow.setActiveGameObject(newGameObject);
                return;
            } else if (KeyListener.keyBeginPress(GLFW_KEY_DELETE)) {
                activeGameObject.destroy();
                this.setInactive();
                this.propertiesWindow.setActiveGameObject(null);
                return;
            }
        } else {
            setInactive();
            return;
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        if ((xAxisHot || xAxisActive) && !yAxisActive && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = true;
            yAxisActive = false;
        } else if ((yAxisHot || yAxisActive) && !xAxisActive && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = false;
            yAxisActive = true;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }

        if (this.activeGameObject != null) {
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.xAxisObject.transform.position.add(X_AXIS_OFFSET);
            this.yAxisObject.transform.position.add(Y_AXIS_OFFSET);
        }
    }

    private void setActive() {
        this.xAxisSprite.setColor(X_AXIS_COLOR);
        this.yAxisSprite.setColor(Y_AXIS_COLOR);
    }

    private void setInactive() {
        this.xAxisSprite.setColor(new Vector4f(0, 0, 0, 0));
        this.yAxisSprite.setColor(new Vector4f(0, 0, 0, 0));
    }

    private boolean checkXHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        if (mousePos.x <= xAxisObject.transform.position.x + (GIZMO_HEIGHT / 2.0f) &&
                mousePos.x >= xAxisObject.transform.position.x - (GIZMO_HEIGHT / 2.0f) &&
                mousePos.y >= xAxisObject.transform.position.y - (GIZMO_WIDTH / 2.0f) &&
                mousePos.y <= xAxisObject.transform.position.y + (GIZMO_WIDTH / 2.0f)
        ) {
            xAxisSprite.setColor(X_AXIS_COLOR_HOVER);
            return true;
        }

        xAxisSprite.setColor(X_AXIS_COLOR);
        return false;
    }

    private boolean checkYHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        if (mousePos.x <= yAxisObject.transform.position.x + (GIZMO_WIDTH / 2.0f) &&
                mousePos.x >= yAxisObject.transform.position.x - (GIZMO_WIDTH / 2.0f) &&
                mousePos.y <= yAxisObject.transform.position.y + (GIZMO_HEIGHT / 2.0f) &&
                mousePos.y >= yAxisObject.transform.position.y - (GIZMO_HEIGHT / 2.0f)
        ) {
            yAxisSprite.setColor(Y_AXIS_COLOR_HOVER);
            return true;
        }

        yAxisSprite.setColor(Y_AXIS_COLOR);
        return false;
    }
    
    public void setUsing() {
        this.using = true;
    }

    public void setNotUsing() {
        this.using = false;
        this.setInactive();
    }
}
