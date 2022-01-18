package editor;

import component.SpriteRenderer;
import imgui.ImGui;
import bifrost.GameObject;
import org.joml.Vector4f;
import physics2d.component.Box2DCollider;
import physics2d.component.CircleCollider;
import physics2d.component.Rigidbody2D;
import renderer.PickingTexture;

import java.util.ArrayList;
import java.util.List;

public class PropertiesWindow {

    private final List<GameObject> activeGameObjects;
    private final List<Vector4f> activeGameObjectsOrigColor;
    private GameObject activeGameObject = null;
    private final PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;
        this.activeGameObjectsOrigColor = new ArrayList<>();
    }

    public void imgui() {
        if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {
            activeGameObject = activeGameObjects.get(0);
            ImGui.begin("Properties");

            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (ImGui.menuItem("Add Rigidbody")) {
                    if (activeGameObject.getComponent(Rigidbody2D.class) == null) {
                        activeGameObject.addComponent(new Rigidbody2D());
                    }
                }

                if (ImGui.menuItem("Add Box2D Collider") &&
                        activeGameObject.getComponent(CircleCollider.class) == null
                ) {
                    if (activeGameObject.getComponent(Box2DCollider.class) == null) {
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }

                if (ImGui.menuItem("Add Circle Collider") &&
                        activeGameObject.getComponent(Box2DCollider.class) == null
                ) {
                    if (activeGameObject.getComponent(CircleCollider.class) == null) {
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }

                ImGui.endPopup();
            }

            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject() {
        return activeGameObjects.size() == 1
                ? activeGameObjects.get(0)
                : null;
    }

    public List<GameObject> getActiveGameObjects() {
        return activeGameObjects;
    }

    public void clearSelected() {
        if (activeGameObjectsOrigColor.size() > 0) {
            for (int i = 0; i < activeGameObjects.size(); i++) {
                GameObject activeGameObject = activeGameObjects.get(i);
                SpriteRenderer spriteRenderer = activeGameObject.getComponent(SpriteRenderer.class);
                if (spriteRenderer != null) {
                    spriteRenderer.setColor(activeGameObjectsOrigColor.get(i));
                }
            }
        }

        this.activeGameObjects.clear();
        this.activeGameObjectsOrigColor.clear();
    }

    public void setActiveGameObject(GameObject gameObject) {
        if (gameObject != null) {
            clearSelected();
            this.activeGameObjects.add(gameObject);
        }
    }

    public void addActiveGameObject(GameObject gameObject) {
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        if (spriteRenderer != null) {
            this.activeGameObjectsOrigColor.add(new Vector4f(spriteRenderer.getColor()));
            spriteRenderer.setColor(new Vector4f(0.8f, 0.8f, 0.0f, 0.8f));
        } else {
            this.activeGameObjectsOrigColor.add(new Vector4f());
        }

        this.activeGameObjects.add(gameObject);
    }

    public PickingTexture getPickingTexture() {
        return this.pickingTexture;
    }
}
