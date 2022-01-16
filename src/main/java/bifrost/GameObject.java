package bifrost;

import bifrost.serializer.GameObjectSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import component.Component;
import component.SpriteRenderer;
import component.serializer.ComponentSerializer;
import imgui.ImGui;
import util.AssetPool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameObject {

    private static int ID_COUNTER = 0;
    private int uid = -1;

    public String name;
    private Map<String, Component> components;
    private List<Component> componentsList;
    public transient Transform transform;
    private transient boolean doSerialization = true;
    private boolean isDead = false;

    public GameObject(String name) {
        init(name);
    }

    private void init(String name) {
        this.name = name;
        this.components = new ConcurrentHashMap<>();
        this.componentsList = new ArrayList<>();
        this.uid = ID_COUNTER++;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        Component c = components.getOrDefault(componentClass.getSimpleName(), null);

        if (c != null && componentClass.isAssignableFrom(c.getClass())) {
            try {
                return componentClass.cast(c);
            } catch (ClassCastException e) {
                e.printStackTrace();
                assert false : "Error: Casting component";
            }
        }

        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        components.remove(componentClass.getSimpleName());
        for (int i = 0; i < componentsList.size(); i++) {
            Component c = componentsList.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                componentsList.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c) {
        c.generateId();
        components.put(c.getClass().getSimpleName(), c);
        componentsList.add(c);
        c.gameObject = this;
    }

    public void update(float dt) {
        Iterator<Map.Entry<String, Component>> it = components.entrySet().iterator();
        while (it.hasNext()) {
            Component c = it.next().getValue();
            c.update(dt);
        }
    }

    public void editorUpdate(float dt) {
        Iterator<Map.Entry<String, Component>> it = components.entrySet().iterator();
        while (it.hasNext()) {
            Component c = it.next().getValue();
            c.editorUpdate(dt);
        }
    }

    public void start() {
        for (int i = 0; i < componentsList.size(); i++) {
            componentsList.get(i).start();
        }
    }

    public void imgui() {
        Iterator<Map.Entry<String, Component>> it = components.entrySet().iterator();
        while (it.hasNext()) {
            Component c = it.next().getValue();
            if (ImGui.collapsingHeader(c.getClass().getSimpleName())) {
                c.imgui();
            }
        }
    }

    public int getUid() {
        return this.uid;
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    public List<Component> getAllComponents() {
        return new ArrayList<>(this.components.values());
    }

    public void setNoSerialize() {
        this.doSerialization = false;
    }

    public boolean doSerialization() {
        return doSerialization;
    }

    public void destroy() {
        this.isDead = true;
        for (int i = 0; i < componentsList.size(); i++) {
            componentsList.get(i).destroy();
        }
    }

    public GameObject copy() {
        // TODO: come up with cleaner solution
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Component.class, new ComponentSerializer())
                .registerTypeAdapter(GameObject.class, new GameObjectSerializer())
                .create();
        String objAsJson = gson.toJson(this);
        GameObject gameObject = gson.fromJson(objAsJson, GameObject.class);

        gameObject.generateUid();
        for (Component c : gameObject.getAllComponents()) {
            c.generateId();
        }

        SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
        if (sprite != null && sprite.getTexture() != null) {
            sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilepath()));
        }

        return gameObject;
    }

    public void generateUid() {
        this.uid = ID_COUNTER++;
    }

    public boolean isDead() {
        return this.isDead;
    }
}
