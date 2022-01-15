package editor;

import component.NonPickable;
import imgui.ImGui;
import bifrost.GameObject;
import bifrost.MouseListener;
import bifrost.Scene;
import renderer.PickingTexture;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {

    private static final float INITIAL_DEBOUNCE_TIME = 0.2f;

    private GameObject activeGameObject = null;
    private final PickingTexture pickingTexture;

    private float debounceTime = INITIAL_DEBOUNCE_TIME;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene currentScene) {
        debounceTime -= dt;
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounceTime < 0) {
            int x = (int) MouseListener.getScreenX();
            int y = (int) MouseListener.getScreenY();

            int gameObjectId = pickingTexture.readPixel(x, y);
            GameObject pickedObject = currentScene.getGameObject(gameObjectId);
            if (pickedObject != null && pickedObject.getComponent(NonPickable.class) == null) {
                activeGameObject = pickedObject;
            } else if (pickedObject == null && !MouseListener.isDragging()) {
                activeGameObject = null;
            }

            this.debounceTime = INITIAL_DEBOUNCE_TIME;
        }
    }

    public void imgui() {
        if (activeGameObject != null) {
            ImGui.begin("Properties");
            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject() {
        return this.activeGameObject;
    }
}
