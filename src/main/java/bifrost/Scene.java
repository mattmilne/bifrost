package bifrost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import component.Component;
import component.serializer.ComponentSerializer;
import bifrost.serializer.GameObjectSerializer;
import org.joml.Vector2f;
import physics2d.Physics2D;
import renderer.Renderer;
import scene.SceneInitializer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Scene {

    private final Renderer renderer;
    private Camera camera;
    private boolean isRunning;
    private final List<GameObject> gameObjects;
    private final SceneInitializer sceneInitializer;
    private final Physics2D physics2d;

    public Scene(SceneInitializer sceneInitializer) {
        this.sceneInitializer = sceneInitializer;
        this.physics2d = new Physics2D();
        this.renderer = new Renderer();
        this.gameObjects = new ArrayList<>();
        this.isRunning = false;
    }

    public void init() {
        this.camera = new Camera(new Vector2f(0, 0));
        this.sceneInitializer.loadResources(this);
        this.sceneInitializer.init(this);
    }

    public void start() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);
            gameObject.start();
            this.renderer.add(gameObject);
            this.physics2d.add(gameObject);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject gameObject) {
        gameObjects.add(gameObject);
        if (isRunning) {
            gameObject.start();
            this.renderer.add(gameObject);
            this.physics2d.add(gameObject);
        }
    }

    public GameObject getGameObject(int gameObjectId) {
        return this.gameObjects.stream()
                .filter(go -> go.getUid() == gameObjectId)
                .findFirst()
                .orElse(null);
    }

    public void editorUpdate(float dt) {
        this.camera.adjustProjection();

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);
            gameObject.editorUpdate(dt);

            if (gameObject.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(gameObject);
                this.physics2d.destroyGameObject(gameObject);
                i--;
            }
        }
    }

    public void update(float dt) {
        camera.adjustProjection();
        this.physics2d.update(dt);

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);
            gameObject.update(dt);

            if (gameObject.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(gameObject);
                this.physics2d.destroyGameObject(gameObject);
                i--;
            }
        }
    }

    public void render() {
        this.renderer.render();
    }

    public Camera camera() {
        return this.camera;
    }

    public void imgui() {
        this.sceneInitializer.imgui();
    }

    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        Transform transform = new Transform();
        go.addComponent(transform);
        go.transform = transform;
        return go;
    }

    public void save() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentSerializer())
                .registerTypeAdapter(GameObject.class, new GameObjectSerializer())
                .create();

        try {
            FileWriter writer = new FileWriter("level.txt");
            List<GameObject> objectsToSerialize = this.gameObjects.stream()
                    .filter(GameObject::doSerialization)
                    .collect(Collectors.toList());
            writer.write(gson.toJson(objectsToSerialize));
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentSerializer())
                .registerTypeAdapter(GameObject.class, new GameObjectSerializer())
                .create();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inFile.equals("")) {
            int maxGoId = -1;
            int maxCompId = -1;
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i = 0; i < objs.length; i++) {
                GameObject object = objs[i];
                addGameObjectToScene(object);

                for (Component c : object.getAllComponents()) {
                    int compUid = c.getUid();
                    if (compUid > maxCompId) {
                        maxCompId = compUid;
                    }
                }

                int goUid = object.getUid();
                if (goUid > maxGoId) {
                    maxGoId = goUid;
                }
            }

            maxGoId++;
            maxCompId++;
            GameObject.init(maxGoId);
            Component.init(maxCompId);
        }
    }

    public void destroy() {
        for (GameObject gameObject : gameObjects) {
            gameObject.destroy();
        }
    }

    public List<GameObject> getGameObjects() {
        return this.gameObjects;
    }
}