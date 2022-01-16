package renderer;

import component.SpriteRenderer;
import bifrost.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {

    private final int MAX_BATCH_SIZE = 1000;
    private final List<RenderBatch> batches;
    private static Shader currentShader;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            add(spr);
        }
    }

    private void add(SpriteRenderer sprite) {
        boolean added = false;
        Texture texture = sprite.getTexture();
        int zIndex = sprite.gameObject.transform.zIndex;
        for (RenderBatch batch : batches) {
            if (batch.hasRoom() && batch.zIndex() == zIndex &&
                    (texture == null || batch.hasTexture(texture) || batch.hasTextureRoom())) {
                batch.addSprite(sprite);
                added = true;
                break;
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, zIndex, this);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    public static void bindShader(Shader shader) {
        currentShader = shader;
    }

    public static Shader getBoundShader() {
        return currentShader;
    }

    public void render() {
        currentShader.use();
        for (int i = 0; i< batches.size(); i++) {
            batches.get(i).render();
        }
    }

    public void destroyGameObject(GameObject gameObject) {
        if (gameObject.getComponent(SpriteRenderer.class) == null) return;
        for (RenderBatch batch : batches) {
            if (batch.destroyIfExists(gameObject)) {
                return;
            }
        }
    }
}
