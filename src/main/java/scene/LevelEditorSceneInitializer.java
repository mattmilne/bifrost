package scene;

import component.EditorCamera;
import component.StateMachine;
import component.gizmo.GizmoSystem;
import component.GridLine;
import component.MouseControl;
import component.Sprite;
import component.SpriteRenderer;
import component.Spritesheet;
import imgui.ImGui;
import imgui.ImVec2;
import bifrost.GameObject;
import bifrost.Prefab;
import bifrost.Scene;
import org.joml.Vector2f;
import renderer.Texture;
import util.AssetPool;
import util.Settings;

public class LevelEditorSceneInitializer extends SceneInitializer {

    private Spritesheet sprites;
    private GameObject levelEditorStuff;

    public LevelEditorSceneInitializer() {}

    @Override
    public void init(Scene scene) {
        sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");
        Spritesheet gizmos = AssetPool.getSpritesheet("assets/images/spritesheets/gizmos.png");

        levelEditorStuff = scene.createGameObject("LevelEditor");
        levelEditorStuff.setNoSerialize();
        levelEditorStuff.addComponent(new MouseControl());
        levelEditorStuff.addComponent(new GridLine());
        levelEditorStuff.addComponent(new EditorCamera(scene.camera()));
        levelEditorStuff.addComponent(new GizmoSystem(gizmos));
        scene.addGameObjectToScene(levelEditorStuff);
    }

    @Override
    public void loadResources(Scene scene) {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                        16, 16, 81, 0));
        AssetPool.addSpritesheet("assets/images/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                        16, 16, 26, 0));
        AssetPool.addSpritesheet("assets/images/spritesheets/items.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/items.png"),
                        16, 16, 43, 0));
        AssetPool.addSpritesheet("assets/images/spritesheets/gizmos.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/gizmos.png"),
                        24, 48, 3, 0));
        AssetPool.getTexture("assets/images/blendImage2.png");

        for (GameObject gameObject : scene.getGameObjects()) {
            SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
            if (spriteRenderer != null) {
                Texture texture = spriteRenderer.getTexture();
                if (texture != null) {
                    spriteRenderer.setTexture(AssetPool.getTexture(texture.getFilepath()));
                }
            }

            StateMachine stateMachine = gameObject.getComponent(StateMachine.class);
            if (stateMachine != null) {
                stateMachine.refreshTextures();
            }
        }
    }

    @Override
    public void imgui() {
        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imgui();
        ImGui.end();

        ImGui.begin("Objects");

        if (ImGui.beginTabBar("WindowTabBar")) {
            if (ImGui.beginTabItem("Blocks")) {
                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < sprites.size(); i++) {
                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 4;
                    float spriteHeight = sprite.getHeight() * 4;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefab.generateSpriteObject(sprite, Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
                        // Attach this to the mouse cursor
                        levelEditorStuff.getComponent(MouseControl.class).pickupObject(object);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }

                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Prefabs")) {
                Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");
                Sprite sprite = playerSprites.getSprite(0);
                float spriteWidth = sprite.getWidth() * 4;
                float spriteHeight = sprite.getHeight() * 4;
                int id = sprite.getTexId();
                Vector2f[] texCoords = sprite.getTexCoords();

                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefab.generateMario();
                    levelEditorStuff.getComponent(MouseControl.class).pickupObject(object);
                }
                ImGui.sameLine();

                Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");
                sprite = items.getSprite(0);
                id = sprite.getTexId();
                texCoords = sprite.getTexCoords();
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefab.generateQuestionBlock();
                    levelEditorStuff.getComponent(MouseControl.class).pickupObject(object);
                }

                ImGui.endTabItem();
            }

            ImGui.endTabBar();
        }

        ImGui.end();
    }
}