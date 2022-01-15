package bifrost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import component.Component;
import component.serializer.ComponentSerializer;
import bifrost.serializer.GameObjectSerializer;
import renderer.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected boolean levelLoaded = false;

    public Scene() {}

    public void init() {}

    public void start() {
        for (GameObject go : gameObjects) {
            go.start();
            this.renderer.add(go);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        gameObjects.add(go);
        if (isRunning) {
            go.start();
            renderer.add(go);
        }
    }

    public GameObject getGameObject(int gameObjectId) {
        return this.gameObjects.stream()
                .filter(go -> go.getUid() == gameObjectId)
                .findFirst()
                .orElse(null);
    }

    public abstract void update(float dt);
    public abstract void render();

    public Camera camera() {
        return this.camera;
    }

    public void imgui() {}

    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public void saveExit() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentSerializer())
                .registerTypeAdapter(GameObject.class, new GameObjectSerializer())
                .create();

        try {
            FileWriter writer = new FileWriter("level.txt");
            List<GameObject> objectsToSerialize = this.gameObjects.stream()
                    .filter(GameObject::doSerialize)
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
            this.levelLoaded = true;
        }
    }
}