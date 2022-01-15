package component;

import editor.PropertiesWindow;
import bifrost.GameObject;
import bifrost.MouseListener;
import bifrost.Prefab;
import bifrost.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Gizmo extends Component {
    private static final int GIZMO_WIDTH = 16;
    private static final int GIZMO_HEIGHT = 48;
    private static final Vector4f X_AXIS_COLOR = new Vector4f(1, 0.3f, 0.3f, 1);
    private static final Vector4f X_AXIS_COLOR_HOVER = new Vector4f(1, 0, 0, 1);
    private static final Vector4f Y_AXIS_COLOR = new Vector4f(0.3f, 1, 0.3f, 1);
    private static final Vector4f Y_AXIS_COLOR_HOVER = new Vector4f(0, 1, 0, 1);
    private static final Vector2f X_AXIS_OFFSET = new Vector2f(48.0f, -8.0f);
    private static final Vector2f Y_AXIS_OFFSET = new Vector2f(8.0f, 48.0f);

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
        this.xAxisObject = Prefab.generateSpriteObject(arrowSprite, 16, 48);
        this.yAxisObject = Prefab.generateSpriteObject(arrowSprite, 16, 48);
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
        if (!using) return;

        this.activeGameObject = this.propertiesWindow.getActiveGameObject();
        if (this.activeGameObject != null) {
            setActive();
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
            this.xAxisObject.transform.position.add(this.X_AXIS_OFFSET);
            this.yAxisObject.transform.position.add(this.Y_AXIS_OFFSET);
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
        if (mousePos.x <= xAxisObject.transform.position.x &&
                mousePos.x >= xAxisObject.transform.position.x - GIZMO_HEIGHT &&
                mousePos.y >= xAxisObject.transform.position.y &&
                mousePos.y <= xAxisObject.transform.position.y + GIZMO_WIDTH
        ) {
            xAxisSprite.setColor(X_AXIS_COLOR_HOVER);
            return true;
        }

        xAxisSprite.setColor(X_AXIS_COLOR);
        return false;
    }

    private boolean checkYHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        if (mousePos.x <= yAxisObject.transform.position.x &&
                mousePos.x >= yAxisObject.transform.position.x - GIZMO_WIDTH &&
                mousePos.y <= yAxisObject.transform.position.y &&
                mousePos.y >= yAxisObject.transform.position.y - GIZMO_HEIGHT
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
